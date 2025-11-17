package com.example.detectify

import android.app.Application
import com.google.firebase.FirebaseApp

class DetectifyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
