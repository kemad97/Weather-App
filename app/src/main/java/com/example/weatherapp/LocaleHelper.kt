package com.example.weatherapp


import android.content.Context
import android.content.res.Configuration
import java.util.*

object LocaleHelper {
    fun updateLocale(context: Context, language: String): Context {
        val currentLocale = context.resources.configuration.locales[0]
        val newLocale = Locale(when (language) {
            "ARABIC" -> "ar"
            else -> "en"
        })
        if (currentLocale == newLocale) {
            return context
        }
        Locale.setDefault(newLocale)

        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(newLocale)

        return context.createConfigurationContext(configuration)
    }
}