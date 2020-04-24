plugins {
    id("java-library")
}

dependencies {
    // Moved to GitLab: https://gitlab.com/dev.root1.de/simon
    api("de.root1:simon:1.3.0:jar-with-dependencies")

    // Optional
    compileOnly("com.apple:AppleJavaExtensions:1.4")
}
