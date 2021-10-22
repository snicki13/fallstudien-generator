package de.thm.mni.dbs.casestudygenerator.model

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import java.security.Principal
import java.time.LocalDateTime

@Table
data class StudentGroup(
    @field:Id val groupId: Int? = null,
    @field:Column val groupName: String,
    @field:Column val token: String,
    @field:Column val numCaseStudies: Int,
    @field:Column val numExclusions: Int? = null,
    @field:Column val validUntil: LocalDateTime? = null
): Principal, Authentication {

    @Transient
    val caseStudies: MutableList<Int> = mutableListOf()

    @Transient
    private var valid = true

    override fun getName(): String = groupName

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = mutableListOf()

    override fun getCredentials(): Any = token

    override fun getDetails(): Any = groupName

    override fun getPrincipal(): Any = groupId!!

    override fun isAuthenticated(): Boolean = LocalDateTime.now().isBefore(validUntil) && valid

    override fun setAuthenticated(isAuthenticated: Boolean) {
        valid = isAuthenticated
    }
}
