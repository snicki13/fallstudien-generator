package de.thm.mni.dbs.casestudygenerator.repositories

import de.thm.mni.dbs.casestudygenerator.model.Teacher
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface TeacherRepository: ReactiveCrudRepository<Teacher, Int>
