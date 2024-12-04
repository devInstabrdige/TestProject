import org.gradle.kotlin.dsl.gradlePlugin

plugins {
    `maven-publish`
    id("java-gradle-plugin")
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
 }

group = "com.example"
version = "1.0.0"

//gradlePlugin {
//    plugins {
//        create("testGradlePlugin") {
//            id = "com.example.testgradleplugin"
//            implementationClass = "com.example.plugin.TestGradlePlugin"
//        }
//    }
//}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") { // Or use a different name for your publication
                from(components["java"]) // Or "release" if you have a release variant
                groupId = "com.example" // Replace with your group ID
                artifactId = "testgradleplugin" // Replace with your artifact ID
                version = "1.0.0" // Replace with your version
            }
        }
        repositories {
            mavenLocal()
            gradlePluginPortal()
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}


dependencies {
    implementation(gradleApi())
}