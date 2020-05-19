import org.gradle.jvm.tasks.Jar

val springBootVersion = "2.2.6.RELEASE"
val springVersion = "5.2.5.RELEASE"

plugins {
    `java-library`
    id("java")
}

group = "org.pensatocode.simplicity.processor"
version = "0.1.0-ALPHA"
description = "Annotation processor for the Simplicity Java framework."
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    // Apache Velocity
    implementation("org.apache.velocity:velocity:1.7")
}


// https://guides.gradle.org/building-kotlin-jvm-libraries/
// https://kotlinlang.org/docs/reference/using-gradle.html#targeting-the-jvm
// https://guides.gradle.org/migrating-build-logic-from-groovy-to-kotlin/
tasks {
    jar {
        manifest {
            attributes(
                    mapOf("Implementation-Title" to project.name,
                            "Implementation-Version" to project.version)
            )
        }
    }

    val sourcesJar by creating(Jar::class) {
        dependsOn(JavaPlugin.CLASSES_TASK_NAME)
        val sourceSets: SourceSetContainer by project
        from(sourceSets["main"].allSource)
    }

    artifacts {
        add("archives", sourcesJar)
    }

}

tasks.register<Jar>("annotationJar") {
    archiveName = "spring-simplicity-annotation.jar"

    from(sourceSets["main"].output.classesDirs)
    include("**/annotation/**/*")

    manifest {
        attributes(
                mapOf("Implementation-Title" to "spring-simplicity-annotation",
                        "Implementation-Version" to project.version)
        )
    }
}


