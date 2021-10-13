package de.thm.mni.dbs.casestudygenerator.security

import de.thm.mni.dbs.casestudygenerator.repositories.TokenRepository
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.toMono

@Component
class AccessTokenSecurity(
    private val tokenRepository: TokenRepository,
    ) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun accessTokenWebFilter(): AuthenticationWebFilter {
        val sessionTokenFilter: AuthenticationWebFilter
        val authManager = this.reactiveAuthenticationManager()
        sessionTokenFilter = AuthenticationWebFilter(authManager)
        sessionTokenFilter.setRequiresAuthenticationMatcher(
            ServerWebExchangeMatchers.pathMatchers("/api/*"),
        )
        sessionTokenFilter.setServerAuthenticationConverter(this.accessTokenAuthenticationConverter())
        return sessionTokenFilter
    }

    private fun reactiveAuthenticationManager() = ReactiveAuthenticationManager { auth ->
        tokenRepository.findById(auth.credentials as String)
            .doOnNext {
                logger.info(it.groupName)
            }
            .filter { it.isAuthenticated }
            .cast(Authentication::class.java)
            .defaultIfEmpty(auth)
    }

    private fun accessTokenAuthenticationConverter() = ServerAuthenticationConverter { exchange ->
        val token = exchange.request.headers.getFirst("access-token") ?: ""
        AccessTokenAuthentication(token).toMono()
    }
}
