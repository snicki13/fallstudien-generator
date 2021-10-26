package de.thm.mni.dbs.insertgenerator

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import org.apache.commons.lang3.RandomStringUtils

fun main() {
    csvReader {
        delimiter = ';'
    }.open("DBS-test.csv") {
        parseCsvSequence(readAllWithHeaderAsSequence())
    }
}

fun parseCsvSequence(csv: Sequence<Map<String, String>>) {
    val groupMap: MutableMap<String, Int> = mutableMapOf()

    csv.filter {
        !it["Gruppe"].isNullOrBlank()
    }.forEach {
        groupMap.compute(it["Gruppe"]!!) { _, v -> v?.plus(1) ?: 1 }
    }

    groupMap.map { (k, v) ->
        "insert into student_group(group_name, token, num_case_studies) " +
                "values ('$k','${RandomStringUtils.randomAlphanumeric(20)}', $v);"
    }.forEach { println(it) }

}
