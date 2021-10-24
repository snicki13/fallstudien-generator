package de.thm.mni.dbs.casestudygenerator.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.router
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Configuration
@CrossOrigin
class StaticController {

    @Value("classpath:static/index.html")
    lateinit var html: Resource

    @Bean
    fun staticRouter() = router {
        GET("/fallstudien", ::getIndex)
        GET("/fallstudien/generate", ::getIndex)
    }

    fun getIndex(req: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok().contentType(MediaType.TEXT_HTML).bodyValue(html)

}
