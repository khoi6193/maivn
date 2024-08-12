group = "net.minevn"
version = "3.0"

repositories {
	maven("https://repo.codemc.io/repository/nms/")
}

dependencies {
	compileOnly("org.spigotmc:spigot:1.12.2-R0.1-SNAPSHOT")
	compileOnly(project(":minigames-nms-common"))
}