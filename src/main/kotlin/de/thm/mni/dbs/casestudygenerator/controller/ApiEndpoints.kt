package de.thm.mni.dbs.casestudygenerator.controller

import de.thm.mni.dbs.casestudygenerator.model.CaseStudy
import de.thm.mni.dbs.casestudygenerator.repositories.CaseStudyRepository
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import de.thm.mni.dbs.casestudygenerator.component1
import de.thm.mni.dbs.casestudygenerator.component2
import de.thm.mni.dbs.casestudygenerator.model.StudentGroup
import de.thm.mni.dbs.casestudygenerator.model.StudyResult
import de.thm.mni.dbs.casestudygenerator.repositories.GroupRepository
import de.thm.mni.dbs.casestudygenerator.repositories.ResultRepository
import org.springframework.web.reactive.function.server.body
import reactor.kotlin.core.publisher.toMono
import kotlin.random.Random

@Configuration
class ApiEndpoints(
    private val caseStudyRepository: CaseStudyRepository,
    private val groupRepository: GroupRepository,
    private val resultRepository: ResultRepository
) {

    private val logger = LoggerFactory.getLogger(ApiEndpoints::class.java)

    @Bean
    fun router() = router {
        "/api".nest {
            GET("/case-studies", ::getCaseStudies)
            GET("/group-info", ::getGroupInfo)
            POST("/generate", ::requestCaseStudyGenerator)

            onError<Throwable>(::errorHandler)
        }
    }

    fun getGroupInfo(req: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok().body<StudentGroup>(
            groupRepository.findByToken(req.headers().firstHeader("access-token")!!).zipWhen { studentGroup ->
                getGroupResults(studentGroup)
            }.map { tuple ->
                val group = tuple.t1
                val results = tuple.t2
                group.caseStudies.addAll(results)
                group
            }
        )

    fun getCaseStudies(req: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok().body<CaseStudy>(caseStudyRepository.findAll())

    fun requestCaseStudyGenerator(
        req: ServerRequest
    ): Mono<ServerResponse> {
        return req.principal()
            .cast(StudentGroup::class.java)
            .zipWhen { studentGroup ->
                resultRepository.existsByGroupId(studentGroup.groupId!!)
            }.flatMap { (studentGroup, alreadyGenerated) ->
                if (alreadyGenerated) {
                    this.getGroupResults(studentGroup)
                } else {
                    this.generateCaseStudies(studentGroup, req)
                }
            }.flatMap {
                ServerResponse.ok().bodyValue(it)
            }
        }

    private fun generateCaseStudies(studentGroup: StudentGroup, req: ServerRequest): Mono<List<CaseStudy>> {
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

    private fun getGroupResults(studentGroup: StudentGroup): Mono<MutableList<CaseStudy>> {
        return this.resultRepository.getAllByGroupId(studentGroup.groupId!!)
            .map { studyResult -> studyResult.caseStudy }
            .publish(caseStudyRepository::findAllById)
            .collectList()
            .doOnNext { results ->
                logger.info("Retrieving previously generated case studies for group {}: {}!", studentGroup.groupName, results)
            }
    }

    private fun selectCaseStudies(caseStudies: List<CaseStudy>, numCaseStudies: Int): List<CaseStudy> {
        val selectedStudies = caseStudies.shuffled(Random(Random.nextLong())).take(numCaseStudies)
        return if (selectedStudies.size < numCaseStudies) {
            throw Exception("Not enough case studies!")
        } else {
            selectedStudies
        }
    }

    private fun applyExclusions(exclusions: List<CaseStudy>, caseStudies: List<CaseStudy>, studentGroup: StudentGroup): List<CaseStudy> {
        return if (exclusions.size > studentGroup.numExclusions!!) {
            throw Exception("You may only exclude ${studentGroup.numExclusions} case studies!")
        } else {
            caseStudies.filterNot(exclusions::contains)
        }
    }
    /**
     * Exception logger.
     */
    fun errorHandler(exception: Throwable, req: ServerRequest? = null): Mono<ServerResponse> {
        logger.error(exception.message)
        return ServerResponse.badRequest().bodyValue(exception.message!!)
    }
}
