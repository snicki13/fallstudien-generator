package de.thm.mni.dbs.casestudygenerator.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table
data class CaseStudy(
    @field:Id val number: Int,
) {
    @Column
    var title: String = ""

    override fun toString(): String = "$number: $title"
}
