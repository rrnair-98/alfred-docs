import com.strumenta.antlrkotlin.gradle.AntlrKotlinTask
import org.gradle.internal.declarativedsl.parsing.main
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// TODO: add multiplatform and kotlin native support
plugins {
    kotlin("jvm") version "2.1.10"
    id("com.strumenta.antlr-kotlin") version "1.0.0"
}

group = "com.rohan"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.slf4j:slf4j-simple:2.0.17")
    implementation("com.strumenta:antlr-kotlin-runtime:1.0.0")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
    sourceSets {
        getByName("main").kotlin.srcDir("target/antlr")
    }
}

val generatePhpKotlinGrammarSource = tasks.register<AntlrKotlinTask>("generatePhpKotlinGrammarSource") {
    // dependsOn("cleanGenerateKotlinGrammarSource")

    // ANTLR .g4 files are under {example-project}/antlr
    // Only include *.g4 files. This allows tools (e.g., IDE plugins)
    // to generate temporary files inside the base path
    source = fileTree(layout.projectDirectory.dir("src/main/antlr/com/rohan/grammars/php")) {
        include("**/*.g4")
    }

    // We want the generated source files to have this package name
    val pkgName = "com.rohan.grammars.php"
    packageName = pkgName

    // We want visitors alongside listeners.
    // The Kotlin target language is implicit, as is the file encoding (UTF-8)
    arguments = listOf("-visitor")

    // Generated files are outputted inside build/generatedAntlr/{package-name}
    val outDir = "target/antlr/${pkgName.replace(".", "/")}"
    outputDirectory = layout.projectDirectory.dir(outDir).asFile
}

tasks.withType<KotlinCompile> {
    dependsOn(generatePhpKotlinGrammarSource)
}
