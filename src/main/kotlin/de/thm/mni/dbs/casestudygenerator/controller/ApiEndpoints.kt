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
import de.thm.mni.dbs.casestudygenerator.component3
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
            POST("/generate", ::generateCaseStudies)

            onError<Throwable>(::errorHandler)
        }
    }

    fun getGroupInfo(req: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok().body<StudentGroup>(
            groupRepository.findByToken(req.headers().firstHeader("access-token")!!).zipWhen { studentGroup ->
                this.resultRepository.getAllByGroupId(studentGroup.groupId!!).map {
                    it.caseStudy
                }.publish(caseStudyRepository::findAllById).collectList()
            }.map { tuple ->
                val group = tuple.t1
                val results = tuple.t2
                group.caseStudies.addAll(results)
                group
            }
        )

    fun getCaseStudies(req: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok().body<CaseStudy>(caseStudyRepository.findAll())

    fun generateCaseStudies(
        req: ServerRequest
    ): Mono<ServerResponse> {
        return req.bodyToFlux(CaseStudy::class.java)
            .collectList()
            .flatMap { exclusions ->
                Mono.zip(exclusions.toMono(), caseStudyRepository.findAll().collectList(), req.principal())
            }.map { (exclusions, caseStudies, principal) ->
                principal as StudentGroup
                logger.info("Generate case studies for group {}. Excluded: {}!", principal.groupName, exclusions)
                val caseStudiesWithoutExclusions = applyExclusions(exclusions, caseStudies, principal)
                Pair(selectCaseStudies(caseStudiesWithoutExclusions, principal.numCaseStudies), principal)
            }.delayUntil { (selectedStudies, group) ->
                this.resultRepository.saveAll(selectedStudies.map {
                    StudyResult(it.number, group.groupId!!)
                })
            }.flatMap { selectedStudies ->
                ServerResponse.ok().bodyValue(selectedStudies.first)
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
