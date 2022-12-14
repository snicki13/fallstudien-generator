package de.thm.mni.dbs.casestudygenerator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.runApplication
import reactor.util.function.Tuple2
import reactor.util.function.Tuple3

@SpringBootApplication
@ConfigurationPropertiesScan("de.thm.mni.dbs")
class CaseStudyGeneratorApplication

fun main(args: Array<String>) {
    runApplication<CaseStudyGeneratorApplication>(*args)
}

operator fun <T1, T2> Tuple2<T1, T2>.component1(): T1 = t1
operator fun <T1, T2> Tuple2<T1, T2>.component2(): T2 = t2

