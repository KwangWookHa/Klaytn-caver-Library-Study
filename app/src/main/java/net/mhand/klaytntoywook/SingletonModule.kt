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

    @Provides
    @Singleton
    fun provideCaver() : Caver = Caver(Config.baobabUrl)

    @Provides
    @Singleton
    fun provideKlaytnRepository(caver: Caver) : KlaytnRepository = KlaytnRepository(caver)


}