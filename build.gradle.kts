plugins {
    application
    kotlin("jvm") version "2.2.0"
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("ru.kiseru.tablereportcreator.MainKt")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.2")
    testImplementation("org.assertj:assertj-core:3.27.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<Test>() {
    useJUnitPlatform()
}
