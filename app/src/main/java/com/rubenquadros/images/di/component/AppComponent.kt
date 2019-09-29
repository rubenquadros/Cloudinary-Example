package com.rubenquadros.images.di.component

import com.rubenquadros.images.base.BaseActivity
import com.rubenquadros.images.di.module.DbModule
import com.rubenquadros.images.di.module.RepositoryModule
import com.rubenquadros.images.di.module.ViewModelModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DbModule::class, RepositoryModule::class,
                        ViewModelModule::class])
interface AppComponent {
    fun inject(baseActivity: BaseActivity)
}