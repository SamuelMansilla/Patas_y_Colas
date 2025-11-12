// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // CAMBIO AQUÍ: Usamos el alias de Hilt que SÍ existe en el TOML
    alias(libs.plugins.hilt) apply false
}