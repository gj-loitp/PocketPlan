package com.roy93group.noteking

import android.app.Application

//done
//gen ic_launcher https://easyappicon.com/

class App: Application() {

    companion object{
        lateinit var instance: App private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}