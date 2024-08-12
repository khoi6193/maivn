group = "net.minevn"
version = "3.0"

plugins {
    `maven-publish`
}

repositories {
    maven("https://repo.codemc.org/repository/maven-public/")
    maven {
        setUrl("http://pack.minevn.net/repo/")
        isAllowInsecureProtocol = true
    }
}

dependencies {
    // spigot
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")

    // minevn
//    compileOnly("minevn.depend:MineStrikeAPI:14")
    compileOnly("net.minevn:minestrike-api:4.2")
    compileOnly("minevn.depend:MatchMakerAPI:18")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "net.minevn"
            artifactId = project.name
            version = project.version as String?

            from(components["java"])
        }
    }
}

tasks {
    var jarName = ""
    val destName = "MinigamesAPI"

    jar {
        jarName = archiveFileName.get()
    }

    register("customCopy") {
        dependsOn(jar)

        doLast {
            val path = project.properties["shadowPath"]
            if (path != null) {
                println("Copying $jarName to $path")
                val to = File("$path/$jarName")
                val rename = File("$path/$destName.jar")
                File(project.projectDir, "build/libs/$jarName").copyTo(to, true)
                if (rename.exists()) rename.delete()
                to.renameTo(rename)
                println("Copied")
            }
        }
    }

    assemble {
        dependsOn(get("customCopy"))
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