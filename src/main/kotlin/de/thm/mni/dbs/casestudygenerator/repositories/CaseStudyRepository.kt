package de.thm.mni.dbs.casestudygenerator.repositories

import de.thm.mni.dbs.casestudygenerator.model.CaseStudy
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface CaseStudyRepository: ReactiveCrudRepository<CaseStudy, Int>
