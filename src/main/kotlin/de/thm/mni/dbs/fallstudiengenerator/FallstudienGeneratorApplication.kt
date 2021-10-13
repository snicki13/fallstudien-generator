package de.thm.mni.dbs.fallstudiengenerator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import reactor.util.function.Tuple2

@SpringBootApplication
class FallstudienGeneratorApplication

fun main(args: Array<String>) {
    runApplication<FallstudienGeneratorApplication>(*args)
}

operator fun <T1, T2> Tuple2<T1, T2>.component1(): T1 = t1
operator fun <T1, T2> Tuple2<T1, T2>.component2(): T2 = t2
