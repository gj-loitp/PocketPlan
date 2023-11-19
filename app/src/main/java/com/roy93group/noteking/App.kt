package com.roy93group.noteking

import android.app.Application

//TODO firebase analytic
//TODO why you see ad

//TODO keystore
//TODO rename app
//TODO github
//TODO license
//TODO ad applovin
//TODO vung bi mat de show applovin config

//done
//gen ic_launcher https://easyappicon.com/
//proguard
//update UI switch
//leak canary
//rate, more app, share app
//policy

class App : Application() {

    companion object {
        lateinit var instance: App private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}
