plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.lanterna)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

application {
    mainClass = "monsterarena.App"
    applicationName = "monsterarena"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
