package de.thm.mni.dbs.casestudygenerator.repositories

import de.thm.mni.dbs.casestudygenerator.model.Exclusions
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface ExclusionsRepository: ReactiveCrudRepository<Exclusions, Int> {

    @Query("insert into exclusions values (ex.caseStudy, ex.numExclusions)")
    fun insertExclusion(@Param("ex") ex: Exclusions)

}
