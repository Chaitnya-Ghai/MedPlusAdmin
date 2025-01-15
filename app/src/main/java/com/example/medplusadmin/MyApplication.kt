package com.example.medplusadmin

import android.app.Application
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage

class MyApplication: Application() {
    lateinit var supabaseClient:SupabaseClient
    override fun onCreate() {
        super.onCreate()
        supabaseClient = createSupabaseClient("https://klumnmagqsrzvahkvnyq.supabase.co",
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImtsdW1ubWFncXNyenZhaGt2bnlxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzQ2MjY5MDgsImV4cCI6MjA1MDIwMjkwOH0.M5C64Cc7jIsISPiRqLyiEhxE4sZssmQYt14OJpKNGBM"){
            install(Storage)
        }
    }
}