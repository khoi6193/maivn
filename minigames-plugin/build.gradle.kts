group = "net.minevn"
version = "3.0"

plugins {
	id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
	maven("https://repo.dmulloy2.net/repository/public/")
	maven("https://maven.elmakers.com/repository/")
	maven("https://repo.codemc.org/repository/maven-public/")
	maven("https://jitpack.io")
	maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
	maven("https://nexus.iridiumdevelopment.net/repository/maven-releases/")
	maven("https://repo.codemc.io/repository/maven-public/")
	maven {
		setUrl("http://pack.minevn.net/repo/")
		isAllowInsecureProtocol = true
	}
}

dependencies {
	// spigot
	compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")

	// modules
	implementation(project(":minigames-nms-common"))
	implementation(project(":minigames-nms-v12"))
	implementation(project(":minigames-nms-v18", "reobf"))
	implementation(project(":minigames-nms-v19", "reobf"))
	implementation(project(":minigames-nms-v19-4", "reobf"))

	// minevn
	compileOnly("minevn.depend:MineStrikeAPI:24")
//	compileOnly("net.minevn:minestrike-api:4.2")
	compileOnly("minevn.depend:GuiAPI:6")
	compileOnly("minevn.depend:MatchMakerAPI:20")
	compileOnly("minevn.depend:PlayerPoints:2.1.4")
	compileOnly("minevn.depend:villagedefense:1")
	compileOnly("minevn.depend:murdermystery:1")
	compileOnly("minevn.depend:BuildBattle:1")
	compileOnly("minevn.depend:BedWars:1")
//	compileOnly("net.minevn.woolwars:woolwars-api:DEV")
	compileOnly("minevn.depend:WoolWars:3")

	// plugins
	compileOnly(files("../libs/GadgetsMenu.jar"))
	compileOnly(files("../libs/TheLabLegacy.jar"))
	compileOnly(files("../libs/ccRides-1.7.8.jar"))
	compileOnly("dev.jorel:commandapi-core:8.7.1")
	compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
	compileOnly("com.comphenix.protocol:ProtocolLib:4.8.0")
	compileOnly("me.clip:placeholderapi:2.11.2")
	compileOnly("de.tr7zw:item-nbt-api-plugin:2.11.1")
	implementation("xyz.xenondevs:particle:1.8.3")
//	implementation("com.iridium:IridiumColorAPI:1.0.6")

	// library
	compileOnly("commons-lang:commons-lang:2.6")
	compileOnly("com.mojang:authlib:3.11.50")
}

tasks {
	val jarName = "Minigames"

	register("customCopy") {
		dependsOn(shadowJar)

		val path = project.properties["shadowPath"]
		if (path != null) {
			doLast {
				println(path)
				copy {
					from("build/libs/$jarName.jar")
					into(path)
				}
				println("Copied")
			}
		}
	}

	shadowJar {
		relocate("xyz.xenondevs.particle", "net.minevn.particle")
//		relocate("com.iridium.iridiumcolorapi", "net.minevn.iridiumcolorapi")
		archiveFileName.set("$jarName.jar")
	}

	assemble {
		dependsOn(shadowJar, get("customCopy"))
	}
}
