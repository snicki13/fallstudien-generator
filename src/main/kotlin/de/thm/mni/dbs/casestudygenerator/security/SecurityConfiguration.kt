package de.thm.mni.dbs.casestudygenerator.security

import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import reactor.core.publisher.Mono
import kotlin.text.Charsets.UTF_8

/**
 * Main security configuration of the digital classroom.
 */
@EnableWebFluxSecurity
class SecurityConfiguration(
    private val accessTokenSecurity: AccessTokenSecurity,
) {
    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http.exceptionHandling()
            .authenticationEntryPoint { exchange, denied ->
                val response = exchange.response
                response.statusCode = HttpStatus.UNAUTHORIZED
                val buffer = response.bufferFactory().wrap(denied.message?.toByteArray(UTF_8) ?: ByteArray(0))
                response.writeWith(Mono.just(buffer))
                exchange.mutate().response(response)
                Mono.empty()
            }
        return http
            .httpBasic().disable()
            .csrf().disable()
            .cors().disable()
            .logout().disable()
            .addFilterAt(accessTokenSecurity.accessTokenWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
            .authorizeExchange()
            .pathMatchers("/fallstudien/api/*")
            .authenticated()
            .and()
            .authorizeExchange()
            .pathMatchers("/fallstudien", "/fallstudien/generate", "/fallstudien/generate/*")
            .permitAll()
            .and()
            .build()
    }
}
