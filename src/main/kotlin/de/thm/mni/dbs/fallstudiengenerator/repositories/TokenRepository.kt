package de.thm.mni.dbs.fallstudiengenerator.repositories

import de.thm.mni.dbs.fallstudiengenerator.model.ValidToken
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface TokenRepository: ReactiveCrudRepository<ValidToken, String> {

}
