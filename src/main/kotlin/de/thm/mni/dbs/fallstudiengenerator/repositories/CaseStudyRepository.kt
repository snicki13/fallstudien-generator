package de.thm.mni.dbs.fallstudiengenerator.repositories

import de.thm.mni.dbs.fallstudiengenerator.model.CaseStudy
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface CaseStudyRepository: ReactiveCrudRepository<CaseStudy, Int>
