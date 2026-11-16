// Root build script. Semua versi plugin ada di gradle/libs.versions.toml.
// `org.jetbrains.kotlin.android` sengaja TIDAK ada: AGP 9 memakai built-in Kotlin.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
}
