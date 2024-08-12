import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "net.minevn"
version = "3.0"

plugins {
	`java-library`
	kotlin("jvm") version "1.7.20"
}

allprojects {
	apply(plugin = "java")
	apply(plugin = "org.jetbrains.kotlin.jvm")

	repositories {
		mavenCentral()
		mavenLocal()
		maven("https://papermc.io/repo/repository/maven-public/")
	}

	tasks.withType<JavaCompile> {
		options.encoding = Charsets.UTF_8.name()
	}

	dependencies {
		compileOnly(kotlin("stdlib-jdk8"))
	}

	val compileKotlin: KotlinCompile by tasks
	compileKotlin.kotlinOptions {
		jvmTarget = "17"
	}
	val compileTestKotlin: KotlinCompile by tasks
	compileTestKotlin.kotlinOptions {
		jvmTarget = "17"
	}
}
