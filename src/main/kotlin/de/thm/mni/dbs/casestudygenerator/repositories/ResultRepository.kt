package de.thm.mni.dbs.casestudygenerator.repositories

import de.thm.mni.dbs.casestudygenerator.model.Result
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface ResultRepository: ReactiveCrudRepository<Result, Int>
