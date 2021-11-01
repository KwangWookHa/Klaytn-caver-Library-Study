package net.mhand.klaytntoywook

import com.klaytn.caver.Caver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {

    private val baobabUrl by lazy { "https://api.baobab.klaytn.net:8651" }

    @Provides
    @Singleton
    fun provideCaver() : Caver = Caver(baobabUrl)

    @Provides
    @Singleton
    fun provideKlaytnRepository(caver: Caver) : KlaytnRepository = KlaytnRepository(caver)


}