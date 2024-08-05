 plugins {
    id("java")
     id('com.github.johnrengelman.shadow:8.1.1')
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation('club.minnced:discord-webhooks:0.8.4')
    implementation('com.googlecode.json-simple:json-simple:1.1.1')
    implementation('org.slf4j:slf4j-api:2.1.0')
    implementation('ch.qos.logback:logback-classic:1.5.6')
    implementation("net.dv8tion:JDA:5.0.0-beta.23")
}

tasks.test {
    useJUnitPlatform()
}