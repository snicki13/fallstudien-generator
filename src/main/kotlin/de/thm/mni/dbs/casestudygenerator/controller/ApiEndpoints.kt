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
import de.thm.mni.dbs.casestudygenerator.repositories.GroupRepository
import de.thm.mni.dbs.casestudygenerator.repositories.ResultRepository
import de.thm.mni.dbs.casestudygenerator.service.GeneratorService
import org.springframework.web.reactive.function.server.body


@Configuration
class ApiEndpoints(
    private val generatorService: GeneratorService,
    private val groupRepository: GroupRepository,
    private val caseStudyRepository: CaseStudyRepository,
    private val resultRepository: ResultRepository
    ) {

    private val logger = LoggerFactory.getLogger(ApiEndpoints::class.java)

    @Bean
    fun router() = router {
        "/fallstudien/api".nest {
            GET("/case-studies", ::getCaseStudies)
            GET("/group-info", ::getGroupInfo)
            POST("/generate", ::requestCaseStudyGenerator)

            onError<Throwable>(::errorHandler)
        }
    }

    fun getGroupInfo(req: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok().body<StudentGroup>(
            groupRepository.findByToken(req.headers().firstHeader("access-token")!!)
            .zipWhen { studentGroup ->
                generatorService.getGroupResults(studentGroup)
            }.map { tuple ->
                val group = tuple.t1
                val results = tuple.t2
                group.caseStudies.addAll(results)
                group
            }
        )

    fun getCaseStudies(req: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok().body<CaseStudy>(caseStudyRepository.findAll().sort())

    fun requestCaseStudyGenerator(
        req: ServerRequest
    ): Mono<ServerResponse> {
        return req.principal()
            .cast(StudentGroup::class.java)
            .zipWhen { studentGroup ->
                resultRepository.existsByGroupId(studentGroup.groupId!!)
            }.flatMap { (studentGroup, alreadyGenerated) ->
                if (alreadyGenerated) {
                    generatorService.getGroupResults(studentGroup)
                } else {
                    generatorService.generateCaseStudies(studentGroup, req)
                }
            }.flatMap {
                ServerResponse.ok().bodyValue(it)
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
