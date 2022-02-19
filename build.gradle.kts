import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.compose") version "1.0.1"
}

val v = "1.0.0"
group = "com.blunderer"
version = v

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "15"
}

compose.desktop {
    application {
        mainClass = "AppKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Exe, TargetFormat.Deb)
            packageName = "Svg2Compose"
            packageVersion = v
            windows {
                iconFile.set(File("icon.ico"))
                menuGroup = "start-menu-group"
            }
            linux {
                iconFile.set(File("icon.png"))
            }
            macOS {
                iconFile.set(File("icon.icns"))
            }
        }
    }
}
