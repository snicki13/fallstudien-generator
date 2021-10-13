package de.thm.mni.dbs.casestudygenerator.repositories

import de.thm.mni.dbs.casestudygenerator.model.AccessToken
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface TokenRepository: ReactiveCrudRepository<AccessToken, String> {

}
