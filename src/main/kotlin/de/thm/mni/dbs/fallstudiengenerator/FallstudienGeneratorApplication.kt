package de.thm.mni.dbs.fallstudiengenerator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FallstudienGeneratorApplication

fun main(args: Array<String>) {
    runApplication<FallstudienGeneratorApplication>(*args)
}
