package de.thm.mni.dbs.casestudygenerator.controller

import de.thm.mni.dbs.casestudygenerator.GeneratorProperties
import de.thm.mni.dbs.casestudygenerator.model.CaseStudy
import de.thm.mni.dbs.casestudygenerator.model.AccessToken
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
import org.springframework.web.reactive.function.server.body
import reactor.kotlin.core.publisher.toMono

@Configuration
class ApiEndpoints(
    private val caseStudyRepository: CaseStudyRepository,
    generatorProperties: GeneratorProperties
) {

    private val logger = LoggerFactory.getLogger(ApiEndpoints::class.java)
    private val maxExclusions = generatorProperties.getMaxExclusions()

    @Bean
    fun router() = router {
        "/api".nest {
            GET("/case-studies", ::getCaseStudies)
            POST("/generate", ::generateCaseStudies)

            onError<Throwable>(::errorHandler)
        }
    }

    fun getCaseStudies(req: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok().body<CaseStudy>(caseStudyRepository.findAll())

    fun generateCaseStudies(
        req: ServerRequest
    ): Mono<ServerResponse> {
        return req.bodyToFlux(CaseStudy::class.java)
            .collectList()
            .zipWith(caseStudyRepository.findAll().collectList())
            .flatMap { (exclude, caseStudies) ->
                if (exclude.size >= maxExclusions) {
                    Mono.error(Exception("You may only exclude $maxExclusions case studies!"))
                } else {
                    caseStudies.filter { !exclude.contains(it) }.toMono<List<CaseStudy>>()
                }
            }.zipWith(req.principal())
            .flatMap { (caseStudies, token) ->
                token as AccessToken
                val selectedStudies = caseStudies.shuffled().take(token.countCaseStudies)
                if (selectedStudies.size < token.countCaseStudies) {
                    Mono.error(Exception("Not enough case studies!"))
                } else {
                    ServerResponse.ok().bodyValue(selectedStudies)
                }
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
