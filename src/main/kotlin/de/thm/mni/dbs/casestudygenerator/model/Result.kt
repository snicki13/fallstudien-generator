package de.thm.mni.dbs.casestudygenerator.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table
data class Result (
    @field:Id val resultId: Int,
    @field:Column val caseStudy: Int,
    @field:Column val groupId: Int
)
