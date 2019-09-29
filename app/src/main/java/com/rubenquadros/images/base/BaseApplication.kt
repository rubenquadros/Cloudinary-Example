package com.rubenquadros.images.base

import android.app.Application
import com.rubenquadros.images.di.component.AppComponent
import com.rubenquadros.images.di.component.DaggerAppComponent
import com.rubenquadros.images.di.module.DbModule
import com.rubenquadros.images.di.module.RepositoryModule

open class BaseApplication: Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        this.appComponent = initDagger()
    }

    protected open fun initDagger(): AppComponent =
        DaggerAppComponent.builder()
            .dbModule(DbModule(this))
            .repositoryModule(RepositoryModule())
            .build()
}