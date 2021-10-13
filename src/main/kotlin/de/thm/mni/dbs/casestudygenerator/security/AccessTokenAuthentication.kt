package de.thm.mni.dbs.casestudygenerator.security

import org.springframework.security.authentication.AbstractAuthenticationToken

class AccessTokenAuthentication(private val accessToken: String): AbstractAuthenticationToken(listOf()) {

    override fun getCredentials(): Any = accessToken

    override fun getPrincipal(): Any = accessToken

}
