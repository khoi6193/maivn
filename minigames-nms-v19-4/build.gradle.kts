group = "net.minevn"
version = "3.0"

plugins {
	id("io.papermc.paperweight.userdev") version "1.3.8"
}

dependencies {
	paperDevBundle("1.19.4-R0.1-20230402.190140-43")
	compileOnly("io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT")
	compileOnly(project(":minigames-nms-common"))
}

tasks {
	assemble {
		dependsOn(reobfJar)
	}

	java {
		toolchain {
			languageVersion.set(JavaLanguageVersion.of(17))
		}
	}

	compileJava {
		options.release.set(17)
	}
}