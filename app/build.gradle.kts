import com.android.build.gradle.internal.tasks.factory.dependsOn

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("jacoco")
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true

        }
        release {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    testCoverage {
        jacocoVersion = "0.8.12"
    }


    applicationVariants.all(closureOf<com.android.build.gradle.api.ApplicationVariant> {
        val testTaskName = "test${this@closureOf.name.capitalize()}UnitTest"

        val excludes = listOf(
            // Android
            "**/R.class",
            "**/R\$*.class",
            "**/BuildConfig.*",
            "**/Manifest*.*"
        )

        val reportTask = tasks.register("jacoco${testTaskName.capitalize()}Report", JacocoReport::class) {
            dependsOn(testTaskName)

//            reports {
//                xml.isEnabled = true
//                html.isEnabled = true
//            }

            classDirectories.setFrom(
                files(
                    fileTree(this@closureOf.javaCompileProvider.get().destinationDir) {
                        exclude(excludes)
                    },
                    fileTree("$layout. buildDirectory/tmp/kotlin-classes/${this@closureOf.name}") {
                        exclude(excludes)
                    }
                )
            )

            // Code underneath /src/{variant}/kotlin will also be picked up here
            sourceDirectories.setFrom(this@closureOf.sourceSets.flatMap { it.javaDirectories })
            executionData.setFrom(file("$layout. buildDirectory/jacoco/$testTaskName.exec"))
        }

        jacocoTestReport.dependsOn(reportTask)
    })

}

jacoco {
    toolVersion = "0.8.12"
    reportsDirectory = layout.buildDirectory.dir("customJacocoReportDir")
}

tasks.withType<Test> {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}


val jacocoTestReport = tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.named("testDebugUnitTest"))  // Ensure tests run before generating the report

    reports {
        xml.required.set(true)  // For CI integration
        html.required.set(true) // For local viewing
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*"
    )


    val debugTree = fileTree("${layout.buildDirectory}/intermediates/javac/debug") {
        exclude(fileFilter)
    }
    val mainSrc = "${project.projectDir}/src/main/java"

    classDirectories.setFrom(files(debugTree))
    sourceDirectories.setFrom(files(mainSrc))
    executionData.setFrom(fileTree(layout.buildDirectory) {
        include("**/jacoco/testDebugUnitTest.exec")
    })
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("systems.danger:danger-kotlin-sdk:1.2")
}