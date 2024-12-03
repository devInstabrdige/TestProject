import androidx.preference.contains
import androidx.preference.forEach
import androidx.preference.isEmpty
import androidx.preference.isNotEmpty
import systems.danger.kotlin.sdk.*
import systems.danger.kotlin.sdk.github.*

danger(args) {
    onGitHub {
        // 1. Check for a PR description
        if (pullRequest.body.isNullOrEmpty()) {
            warn("Please provide a description for this PR.")
        }

        // 2. Warn about large PRs
        val bigPRThreshold = 500
        val totalChanges = pullRequest.additions + pullRequest.deletions
        if (totalChanges > bigPRThreshold) {
            warn("This PR is quite large ($totalChanges lines). Consider splitting it into smaller, focused PRs.")
        }

        // 3. Check for modified Kotlin test files
        val testFilesModified = git.modifiedFiles.filter {
            it.endsWith(".kt") && (it.contains("src/test") || it.contains("src/androidTest"))
        }
        if (testFilesModified.isEmpty()) {
            warn("No test files were modified. Please ensure tests are added or updated for your changes.")
        }

        // 4. Check for modified Gradle files
        val gradleFilesModified = git.modifiedFiles.filter {
            it.endsWith(".gradle") || it.endsWith(".gradle.kts")
        }
        if (gradleFilesModified.isNotEmpty()) {
            message("Gradle files have been modified. Please verify that the dependencies are correctly updated.")
        }

        // 5. Check for new files without a license header
        val newFiles = git.createdFiles
        val licenseHeaderRegex = Regex("Copyright \\d{4} The Android Open Source Project") // Adjust as needed
        newFiles.forEach { file ->
            val content = danger.utils.readFile(file)
            if (!content.contains(licenseHeaderRegex)) {
                warn("The file '$file' is missing a license header.")
            }
        }
    }
}