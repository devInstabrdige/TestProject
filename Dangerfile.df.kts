import systems.danger.kotlin.*

danger(args) {
    onGitHub {
        val isTrivial = pullRequest.title.contains("#trivial", ignoreCase = true)

        if (!isTrivial) {
            warn("Please ensure this PR has been reviewed.")
        }

        val jacocoReportPath = "app/build/reports/jacoco/jacocoTestReport.xml" // Adjust the path as necessary

        Jacoco.report(jacocoReportPath) {
            // Optional: Configure thresholds for coverage reporting
            minCoveragePercentage = 80.0 // Set the minimum acceptable coverage percentage
        }
    }
}
