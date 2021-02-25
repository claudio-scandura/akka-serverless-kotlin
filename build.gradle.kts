import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    kotlin("jvm") version "1.4.20"

    idea
    application
    id("com.palantir.docker") version "0.25.0"
    id("com.google.protobuf") version "0.8.15"
}


subprojects {

    apply {
        plugin("kotlin")
        plugin("idea")
        plugin("application")
        plugin("com.palantir.docker")
        plugin("com.google.protobuf")
    }


    dependencies {
        implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

        implementation("com.google.guava:guava:29.0-jre")

        testImplementation("org.jetbrains.kotlin:kotlin-test")

        testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
        testImplementation("io.kotest:kotest-runner-junit5:4.4.1")
        testImplementation("io.kotest:kotest-assertions-core:4.4.1")


        implementation("io.cloudstate:cloudstate-kotlin-support:0.5.2")
        implementation("com.google.api.grpc:proto-google-common-protos:2.0.1")
        implementation("ch.qos.logback:logback-classic:1.2.3")
    }

    repositories {
        jcenter()
        mavenCentral()
        mavenLocal()
    }

    protobuf {
        protoc {
            artifact = "com.google.protobuf:protoc:3.11.4"
        }
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    tasks {

        test {
            useJUnitPlatform()
        }

        docker {

            dependsOn(clean.get(), distTar.get())

            setDockerfile(rootProject.file("Dockerfile"))

            name = "cscandura/${project.name}:0.1"

            files("${project.buildDir}/distributions")

            buildArgs(mapOf(
                    Pair("RUNNABLE_NAME", project.name)
            ))
        }
    }
}