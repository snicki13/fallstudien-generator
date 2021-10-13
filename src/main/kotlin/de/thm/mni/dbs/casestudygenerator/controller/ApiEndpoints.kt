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
import kotlin.random.Random

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
            .flatMap { (exclusions, caseStudies) -> applyExclusions(exclusions, caseStudies)}
            .zipWith(req.principal())
            .map { (caseStudies, token) ->
                token as AccessToken
                selectCaseStudies(caseStudies, token.numCaseStudies)
            }.flatMap { selectedStudies ->
                ServerResponse.ok().bodyValue(selectedStudies)
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

    private fun applyExclusions(exclusions: List<CaseStudy>, caseStudies: List<CaseStudy>): Mono<List<CaseStudy>> {
        return if (exclusions.size > maxExclusions) {
            Mono.error(Exception("You may only exclude $maxExclusions case studies!"))
        } else {
            caseStudies.filter { !exclusions.contains(it) }.toMono()
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
