pluginManagement {
	repositories {
		gradlePluginPortal()
		maven("https://papermc.io/repo/repository/maven-public/")
	}
}

rootProject.name = "minigames"
include("minigames-plugin")
include("minigames-nms-common")
include("minigames-nms-v12")
include("minigames-nms-v18")
include("minigames-nms-v19")
include("minigames-nms-v19-4")