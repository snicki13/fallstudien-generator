package de.thm.mni.dbs.fallstudiengenerator.security

import de.thm.mni.dbs.fallstudiengenerator.model.ValidToken
import de.thm.mni.dbs.fallstudiengenerator.repositories.TokenRepository
import reactor.core.publisher.Mono

class TokenValidator(
    private val tokenRepository: TokenRepository
) {

    fun getGroupByToken(token: String): Mono<ValidToken> {
        return this.tokenRepository.findById(token)
    }
}
