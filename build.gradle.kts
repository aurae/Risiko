buildscript {
  repositories {
    mavenCentral()
  }
}

allprojects {
  repositories {
    mavenCentral()

    // Only grab SIMON from its non-https repository;
    // don't reach out to this for other dependencies
    exclusiveContent {
        forRepository {
            maven {
              setUrl("http://maven.root1.de/repository/releases")
            }
        }
        filter {
            includeGroup("de.root1")
        }
    }
  }
}
