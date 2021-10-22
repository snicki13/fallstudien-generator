package de.thm.mni.dbs.casestudygenerator.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table
data class StudyResult (
    @field:Column val caseStudy: Int,
    @field:Column val groupId: Int,
    @field:Id val resultId: Int? = null,
    )
