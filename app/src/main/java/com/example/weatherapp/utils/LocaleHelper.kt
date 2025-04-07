package com.example.weatherapp.utils


import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.view.View
import androidx.core.text.layoutDirection
import java.util.*
//
//object LocaleHelper {
//    fun updateLocale(context: Context, language: String): Context {
//        val currentLocale = context.resources.configuration.locales[0]
//        val newLocale = Locale(when (language) {
//            "ARABIC" -> "ar"
//            else -> "en"
//        })
//        if (currentLocale == newLocale) {
//            return context
//        }
//        Locale.setDefault(newLocale)
//
//        val configuration = Configuration(context.resources.configuration)
//        configuration.setLocale(newLocale)
//
//        return context.createConfigurationContext(configuration)
//    }
//}




object LocaleHelper {
    fun updateLocale(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        if (context is Activity) {
            context.runOnUiThread {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    context.window.decorView.layoutDirection =
                        if (locale.language == "ar") View.LAYOUT_DIRECTION_RTL
                        else View.LAYOUT_DIRECTION_LTR
                }
                context.recreate()
            }
        }

        return context.createConfigurationContext(config)
    }
}