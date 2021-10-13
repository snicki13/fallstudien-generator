package de.thm.mni.dbs.fallstudiengenerator.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.security.Principal
import java.time.LocalDateTime

@Table
data class ValidToken(
    @field:Id val token: String,
    val groupName: String,
    val countCaseStudies: Int,
    val until: LocalDateTime
): Principal {
    override fun getName(): String = groupName
}
