package de.thm.mni.dbs.casestudygenerator.service

import de.thm.mni.dbs.casestudygenerator.model.CaseStudy
import de.thm.mni.dbs.casestudygenerator.model.StudentGroup
import de.thm.mni.dbs.casestudygenerator.model.StudyResult
import de.thm.mni.dbs.casestudygenerator.repositories.CaseStudyRepository
import de.thm.mni.dbs.casestudygenerator.repositories.ResultRepository
import de.thm.mni.dbs.casestudygenerator.component1
import de.thm.mni.dbs.casestudygenerator.component2
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono
import kotlin.random.Random

@Service
class GeneratorService(
    private val caseStudyRepository: CaseStudyRepository,
    private val resultRepository: ResultRepository
) {

    private val logger = LoggerFactory.getLogger(GeneratorService::class.java)

    fun generateCaseStudies(studentGroup: StudentGroup, req: ServerRequest): Mono<List<CaseStudy>> {
        return req.bodyToFlux(CaseStudy::class.java)
            .collectList()
            .zipWith(caseStudyRepository.findAll().collectList())
            .map { (exclusions, caseStudies) ->
                logger.info("Generate case studies for group {}. Excluded: {}!", studentGroup.groupName, exclusions)
                val caseStudiesWithoutExclusions = applyExclusions(exclusions, caseStudies, studentGroup)
                selectCaseStudies(caseStudiesWithoutExclusions, studentGroup.numCaseStudies)
            }.delayUntil { selectedStudies ->
                this.resultRepository.saveAll(selectedStudies.map {
                    StudyResult(it.number, studentGroup.groupId!!)
                })
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
            selectedStudies
        }
    }

    fun applyExclusions(exclusions: List<CaseStudy>, caseStudies: List<CaseStudy>, studentGroup: StudentGroup): List<CaseStudy> {
        return if (exclusions.size > studentGroup.numExclusions!!) {
            throw Exception("You may only exclude ${studentGroup.numExclusions} case studies!")
        } else {
            caseStudies.filterNot(exclusions::contains)
        }
    }
}
