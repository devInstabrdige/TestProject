import danger.*
import java.io.File

// Paths to the Jacoco and Test result files
val jacocoReportPath = "app/build/reports/jacoco/testDebugUnitTest/coverage.xml"
val testReportPath = "app/build/test-results/testDebugUnitTest/TESTS-TestSuite.xml"

// Check if Jacoco and Test Results files exist
if (File(jacocoReportPath).exists() && File(testReportPath).exists()) {
    // Parse Jacoco report to get coverage information
    val jacocoData = File(jacocoReportPath).readText()
    val regex = Regex("<counter type=\"LINE\" missed=\"(\\d+)\" covered=\"(\\d+)\"")
    val matchResult = regex.find(jacocoData)

    val missedLines = matchResult?.groups?.get(1)?.value?.toIntOrNull() ?: 0
    val coveredLines = matchResult?.groups?.get(2)?.value?.toIntOrNull() ?: 0
    val totalLines = missedLines + coveredLines
    val coveragePercentage = (coveredLines.toDouble() / totalLines) * 100

    // Post Jacoco Coverage Results to PR
    message("### Jacoco Coverage\nCoverage: %.2f%%".format(coveragePercentage))

    // Check if any test cases failed
    val testResults = File(testReportPath).readText()
    val failedTests = Regex("<failure").find(testResults)

    if (failedTests != null) {
        warn("Some tests failed! Please review the test results.")
    } else {
        message("All tests passed successfully!")
    }
} else {
    warn("Jacoco or test results not found. Please check your test configuration.")
}
