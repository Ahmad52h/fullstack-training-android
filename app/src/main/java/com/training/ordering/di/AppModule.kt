package com.training.ordering.di

import androidx.room.Room
import com.training.ordering.data.AppDatabase
import com.training.ordering.data.network.ApiService
import com.training.ordering.data.repository.AppRepository
import com.training.ordering.ui.viewmodel.MainViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }
    }

    single { ApiService(get()) }

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "ordering_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<AppDatabase>().categoryDao() }
    single { get<AppDatabase>().productDao() }
    single { get<AppDatabase>().orderDao() }

    single { AppRepository(get(), get(), get()) }

    viewModel { MainViewModel(get()) }
}