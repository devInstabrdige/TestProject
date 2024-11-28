import systems.danger.kotlin.*
// Load DangerJS libraries
const { danger, warn, fail, message } = require("danger");
const fs = require("fs");
const path = require("path");

// 1. Warn if the PR description is empty or too short
if (!danger.github.pr.body || danger.github.pr.body.length < 10) {
    warn("Please provide a more detailed description for this PR.");
}

// 2. Warn if the PR is too large
const bigPRThreshold = 500; // lines of code
const totalChanges = danger.github.pr.additions + danger.github.pr.deletions;

if (totalChanges > bigPRThreshold) {
    warn(`This PR is quite large (${totalChanges} lines). Consider splitting it into smaller, focused PRs.`);
}

// 3. Check for modified test files
const hasModifiedTests = danger.git.modified_files.some(f =>
f.includes("src/test") || f.includes("src/androidTest")
);

if (!hasModifiedTests) {
    warn("No test files were modified. Consider adding or updating tests for your changes.");
}

// 4. Check for changes in Gradle files
const gradleFiles = danger.git.modified_files.filter(file =>
file.endsWith(".gradle") || file.endsWith(".gradle.kts")
);

if (gradleFiles.length > 0) {
    message("Gradle files have been modified. Ensure these changes are reviewed carefully.");
}

// 5. Parse JUnit Test Results and Post Failures to PR
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
        message(`x **Test Failure ${index + 1}**: ${test}`);
    });
}


