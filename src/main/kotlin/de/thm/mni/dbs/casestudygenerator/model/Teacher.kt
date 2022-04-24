package de.thm.mni.dbs.casestudygenerator.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table


@Table
class Teacher (
    @field:Id val teacherId: Int,
    @field:Column val teacherName: String,
    @field:Column val teacherEMail: String
)
