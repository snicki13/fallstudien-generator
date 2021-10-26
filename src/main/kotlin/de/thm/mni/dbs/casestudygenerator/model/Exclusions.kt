package de.thm.mni.dbs.casestudygenerator.model

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column

data class Exclusions(
    @Id val caseStudy: Int,
    @Column var timesExcluded: Int
): Persistable<Int> {

    override fun getId(): Int = caseStudy

    override fun isNew(): Boolean = timesExcluded == 0

}
