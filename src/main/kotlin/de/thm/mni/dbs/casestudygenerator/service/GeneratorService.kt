package de.thm.mni.dbs.casestudygenerator.service

import de.thm.mni.dbs.casestudygenerator.model.CaseStudy
import de.thm.mni.dbs.casestudygenerator.model.StudentGroup
import de.thm.mni.dbs.casestudygenerator.model.StudyResult
import de.thm.mni.dbs.casestudygenerator.repositories.CaseStudyRepository
import de.thm.mni.dbs.casestudygenerator.repositories.ResultRepository
import de.thm.mni.dbs.casestudygenerator.component1
import de.thm.mni.dbs.casestudygenerator.component2
import de.thm.mni.dbs.casestudygenerator.repositories.TeacherRepository
import de.thm.mni.dbs.casestudygenerator.model.Exclusions
import de.thm.mni.dbs.casestudygenerator.repositories.ExclusionsRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import kotlin.random.Random

@Service
class GeneratorService(
    private val caseStudyRepository: CaseStudyRepository,
    private val resultRepository: ResultRepository,
    private val mailSender: JavaMailSender,
    private val teacherRepository: TeacherRepository,
    private val exclusionsRepository: ExclusionsRepository
    ) {

    @Value("\${generator.mail.from}")
    lateinit var fromMail: String

    private val logger = LoggerFactory.getLogger(GeneratorService::class.java)

    fun generateCaseStudies(studentGroup: StudentGroup, req: ServerRequest): Mono<List<CaseStudy>> {
        return req.bodyToFlux(CaseStudy::class.java)
            .collectList()
            .zipWith(caseStudyRepository.findAll().collectList())
            .delayUntil { (exclusions, _) ->
                exclusions.toFlux().flatMap {
                    exclusionsRepository.findById(it.number)
                        .switchIfEmpty(exclusionsRepository.save(Exclusions(it.number, 0)))
                }.map {
                    it.apply { timesExcluded += 1 }
                }.publish(exclusionsRepository::saveAll)
            }.map { (exclusions, caseStudies) ->
                logger.info("Generate case studies for group {}. Excluded: {}!", studentGroup.groupName, exclusions)
                val caseStudiesWithoutExclusions = applyExclusions(exclusions, caseStudies, studentGroup)
                selectCaseStudies(caseStudiesWithoutExclusions, studentGroup.numCaseStudies)
            }.delayUntil { selectedStudies ->
                this.resultRepository.saveAll(selectedStudies.map {
                    StudyResult(it.number, studentGroup.groupId!!)
                })
            }.delayUntil { caseStudies ->
                val confirmationTo = req.headers().firstHeader("confirmation-mail-to")
                    ?.split(";", "; ", ",", ", ")?.filter(String::isNotBlank)
                this.sendMail(studentGroup, caseStudies, confirmationTo ?: listOf())
            }
    }

    fun getGroupResults(studentGroup: StudentGroup): Mono<MutableList<CaseStudy>> {
        return this.resultRepository.getAllByGroupId(studentGroup.groupId!!)
            .map { studyResult -> studyResult.caseStudy }
            .publish(caseStudyRepository::findAllById)
            .collectList()
            .doOnNext { results ->
                logger.info("Retrieving previously generated case studies for group {}: {}!", studentGroup.groupName, results)
            }
    }

    fun selectCaseStudies(caseStudies: List<CaseStudy>, numCaseStudies: Int): List<CaseStudy> {
        val selectedStudies = caseStudies.shuffled(Random(Random.nextLong())).take(numCaseStudies)
        return if (selectedStudies.size < numCaseStudies) {
            throw Exception("Not enough case studies!")
        } else {
            selectedStudies.sortedBy {
                it.number
            }
        }
    }

    fun applyExclusions(exclusions: List<CaseStudy>, caseStudies: List<CaseStudy>, studentGroup: StudentGroup): List<CaseStudy> {
        return if (exclusions.size > studentGroup.numExclusions!!) {
            throw Exception("You may only exclude ${studentGroup.numExclusions} case studies!")
        } else {
            caseStudies.filterNot(exclusions::contains)
        }
    }

    fun sendMail(group: StudentGroup, caseStudies: List<CaseStudy>, confirmation: List<String>): Mono<Unit> {
        return teacherRepository.findById(group.teacher)
            .map { teacher ->
                val mail = SimpleMailMessage()
                mail.setFrom(this.fromMail)
                mail.setTo(*confirmation.toTypedArray())
                mail.setCc(teacher.teacherEmail)
                mail.setSubject("DBS: Fallstudien ${group.groupName}")
                mail.setText("Ihre zugelosten Fallstudien: \n${caseStudies.joinToString("\n")}")
                mailSender.send(mail)
            }
    }
}
