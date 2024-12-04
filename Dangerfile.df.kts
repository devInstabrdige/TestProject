@file:DependsOn("net.appsynth.danger:danger-kotlin-jacoco:X.Y.Z")

import net.appsynth.danger.JaCoCoPlugin
import net.appsynth.danger.jacoco
import systems.danger.kotlin.*
import java.io.File
import kotlin.io.walk

register plugin JaCoCoPlugin

danger(args) {
    val changedFiles =  git.modifiedFiles + git.createdFiles

    jacoco {
        val coverageReports = File(".")
            .walk()
            .maxDepth(10)
            .filter {
                it.name == "jacoco.xml" && !it.path.contains("ref-report")
            }
            .toList()

        parse(*coverageReports.toTypedArray())
        reference(File("ref-report/jacoco.xml"))
        report(changedFiles.filter { it.endsWith(".kt") || it.endsWith(".java") })
    }
}