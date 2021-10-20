package de.thm.mni.dbs.casestudygenerator.repositories

import de.thm.mni.dbs.casestudygenerator.model.StudentGroup
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface GroupRepository: ReactiveCrudRepository<StudentGroup, String> {

    fun findByToken(token: String): Mono<StudentGroup>
}
