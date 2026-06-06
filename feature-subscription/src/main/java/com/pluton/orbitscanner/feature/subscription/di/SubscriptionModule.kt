package com.pluton.orbitscanner.feature.subscription.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SubscriptionModule {
    // Scaffold hook ready for injecting Play billing repositories during subsequent sprints.
}
