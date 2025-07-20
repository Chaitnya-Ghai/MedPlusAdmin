package com.example.medplusadmin.di

import com.example.medplusadmin.BuildConfig
import com.example.medplusadmin.data.remote.firebaseServices.CatalogService
import com.example.medplusadmin.data.remote.firebaseServices.ProfileService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        val supabaseUrl = BuildConfig.SUPABASE_URL
        val supabaseKey = BuildConfig.SUPABASE_API_KEY
        return createSupabaseClient(supabaseUrl, supabaseKey) {
            install(Storage)
        }
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideCatalogService(
        db: FirebaseFirestore,
        supabaseClient: SupabaseClient
    ): CatalogService = CatalogService(db, supabaseClient)

    @Provides
    @Singleton
    fun provideProfileService(
        db: FirebaseFirestore,
        supabaseClient: SupabaseClient
    ): ProfileService = ProfileService(db, supabaseClient)

}




