const { danger, warn, fail, message } = require("danger");
const fs = require("fs");
const path = require("path");

// 1. Check if the PR description is provided and long enough
if (!danger.github.pr.body || danger.github.pr.body.length < 10) {
    warn("Please provide a more detailed description for this PR.");
}

// 2. Warn about large pull requests
const bigPRThreshold = 500; // lines of code
const totalChanges = danger.github.pr.additions + danger.github.pr.deletions;

if (totalChanges > bigPRThreshold) {
    warn(`This PR is quite large (${totalChanges} lines). Consider splitting it into smaller, focused PRs.`);
}

// 3. Check if any Kotlin test files were modified (both unit and instrumented tests)
const testFilesModified = danger.git.modified_files.filter(file =>
file.endsWith(".kt") && (file.includes("src/test") || file.includes("src/androidTest"))
);

if (testFilesModified.length === 0) {
    warn("No test files were modified. Please ensure tests are added or updated for your changes.");
}

// 4. Check if Gradle files were modified
const gradleFiles = danger.git.modified_files.filter(file =>
file.endsWith(".gradle") || file.endsWith(".gradle.kts")
);

if (gradleFiles.length > 0) {
    message("Gradle files have been modified. Please verify that the dependencies are correctly updated.");
}

// 5. Parse JUnit XML files and post failures to the PR
const testResultsPath = path.resolve("app/build/test-results/testDebugUnitTest/");
const testFiles = fs.readdirSync(testResultsPath).filter(file => file.endsWith(".xml"));

let failedTests = [];

testFiles.forEach(file => {
    const filePath = path.join(testResultsPath, file);
    const content = fs.readFileSync(filePath, "utf8");

    const failedTestMatches = content.match(/<testcase .*?>.*?<failure>/gs);
    if (failedTestMatches) {
        failedTests.push(...failedTestMatches);
    }
});

if (failedTests.length > 0) {
    fail(`There are ${failedTests.length} failing tests. Please review them.`);
    failedTests.slice(0, 5).forEach((test, index) => {
        message(`❌ **Test Failure ${index + 1}**: ${test}`);
    });
}
