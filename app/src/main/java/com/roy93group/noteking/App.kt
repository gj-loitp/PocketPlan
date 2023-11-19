package com.roy93group.noteking

import android.app.Application

//TODO firebase analytic
//TODO why you see ad
//TODO ad applovin
//TODO vung bi mat de show applovin config

//TODO keystore
//TODO license

//done
//gen ic_launcher https://easyappicon.com/
//proguard
//update UI switch
//leak canary
//rate, more app, share app
//policy
//rename app
//github

class App : Application() {

    companion object {
        lateinit var instance: App private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}
