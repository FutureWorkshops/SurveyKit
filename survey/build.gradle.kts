@file:Suppress("SuspiciousCollectionReassignment")

plugins {
    id("com.android.library")
    kotlin("android")
    id("org.jetbrains.kotlin.android.extensions")
    id("kotlin-kapt")
    id("maven-publish")
}

androidExtensions { isExperimental = true }

android {
    compileSdkVersion(29)
    buildToolsVersion("29.0.3")

    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(29)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    testOptions {
        animationsDisabled = true
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components.getByName("release"))
                groupId = project.property("project.bundleId")?.toString()
                version = project.property("project.buildversion")?.toString()
                artifactId = "surveykit"
                pom {
                    name.set("Mobile Workflow")
                    url.set("http://fws.io")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("futureworkshops")
                            name.set("Future Workshops")
                            email.set("info@futureworkshops.com")
                        }
                    }
                }
            }
        }
        repositories {
            maven {
                val deployPath = project.property("project.deployPath")?.toString()
                url = uri(file("../${deployPath!!}"))
            }
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.72")
    api( "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.0")
    implementation( "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.0")
    implementation("androidx.appcompat:appcompat:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.recyclerview:recyclerview:1.0.0")
    implementation("com.google.android.material:material:1.2.0")
    implementation("com.airbnb.android:lottie:3.0.7")
    api("com.google.android.gms:play-services-maps:17.0.0")
    implementation("com.google.android.gms:play-services-location:17.0.0")
    api("net.openid:appauth:0.7.1")
    api("com.google.code.gson:gson:2.8.6")
    implementation("javax.inject:javax.inject:1")
    implementation("com.github.bumptech.glide:glide:4.11.0")
    kapt("com.github.bumptech.glide:compiler:4.11.0")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.5.2")
    testImplementation("org.junit.platform:junit-platform-runner:1.5.2")
}
