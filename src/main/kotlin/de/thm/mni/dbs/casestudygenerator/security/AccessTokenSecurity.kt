package de.thm.mni.dbs.casestudygenerator.security

import de.thm.mni.dbs.casestudygenerator.repositories.GroupRepository
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.toMono

@Component
class AccessTokenSecurity(private val groupRepository: GroupRepository) {

    private val logger = LoggerFactory.getLogger(AccessTokenSecurity::class.java)

    fun accessTokenWebFilter(): AuthenticationWebFilter {
        val sessionTokenFilter: AuthenticationWebFilter
        val authManager = this.reactiveAuthenticationManager()
        sessionTokenFilter = AuthenticationWebFilter(authManager)
        sessionTokenFilter.setRequiresAuthenticationMatcher(
            ServerWebExchangeMatchers.pathMatchers("/fallstudien/api/**"),
        )
        sessionTokenFilter.setServerAuthenticationConverter(this.accessTokenAuthenticationConverter())
        return sessionTokenFilter
    }

    private fun reactiveAuthenticationManager() = ReactiveAuthenticationManager { auth ->
        groupRepository.findByToken(auth.credentials as String)
            .filter {
                logger.debug("Authenticated group ${it.groupName}: ${it.isAuthenticated}")
                it.isAuthenticated
            }.cast(Authentication::class.java)
            .defaultIfEmpty(auth)
    }

    private fun accessTokenAuthenticationConverter() = ServerAuthenticationConverter { exchange ->
        val token = exchange.request.headers.getFirst("access-token") ?: ""
        logger.debug("exchange: ${exchange.request.uri} Access token: $token")
        AccessTokenAuthentication(token).toMono()
    }
}
