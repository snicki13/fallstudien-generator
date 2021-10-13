package de.thm.mni.dbs.fallstudiengenerator.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table
data class CaseStudy(
    @field:Id val number: Int,
    val title: String
)
