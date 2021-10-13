package de.thm.mni.dbs.fallstudiengenerator.controller

import de.thm.mni.dbs.fallstudiengenerator.model.CaseStudy
import de.thm.mni.dbs.fallstudiengenerator.model.ValidToken
import de.thm.mni.dbs.fallstudiengenerator.repositories.CaseStudyRepository
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import de.thm.mni.dbs.fallstudiengenerator.component1
import de.thm.mni.dbs.fallstudiengenerator.component2
import org.springframework.web.reactive.function.server.body
import reactor.kotlin.core.publisher.toFlux

@Configuration
class ApiEndpoints(private val caseStudyRepository: CaseStudyRepository) {

    private val logger = LoggerFactory.getLogger(ApiEndpoints::class.java)

    @Bean
    fun router() = router {
        "/api".nest {
            GET("/case-studies", ::getCaseStudies)
            POST("/generate", ::generateCaseStudies)

            onError<Exception>(::errorHandler)
        }
    }

    fun getCaseStudies(req: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok().body<CaseStudy>(caseStudyRepository.findAll())

    fun generateCaseStudies(
        req: ServerRequest
    ): Mono<ServerResponse> {
        req.principal()
        val responseBody = req.bodyToFlux(CaseStudy::class.java)
            .collectList()
            .zipWith(caseStudyRepository.findAll().collectList())
            .map { (except, caseStudies) ->
                caseStudies.filter { !except.contains(it) }
            }.zipWith(req.principal())
            .flatMapMany { (caseStudies, token) ->
                token as ValidToken
                caseStudies.shuffled().take(token.countCaseStudies).toFlux()
            }
        return ServerResponse.ok().body<CaseStudy>(responseBody)
    }
    /**
     * Exception logger.
     */
    fun errorHandler(exception: Throwable, serverRequest: ServerRequest? = null): Mono<ServerResponse> {
        logger.error(exception.message)
        return ServerResponse.badRequest().bodyValue(exception.message!!)
    }
}
