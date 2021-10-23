package de.thm.mni.dbs.casestudygenerator.repositories

import de.thm.mni.dbs.casestudygenerator.model.StudyResult
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ResultRepository: ReactiveCrudRepository<StudyResult, Int> {

    fun existsByGroupId(groupId: Int): Mono<Boolean>

    fun getAllByGroupId(groupId: Int): Flux<StudyResult>

}
