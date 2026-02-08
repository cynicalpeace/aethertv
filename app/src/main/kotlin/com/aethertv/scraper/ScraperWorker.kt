package com.aethertv.scraper

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aethertv.domain.usecase.RefreshChannelsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ScraperWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val refreshChannelsUseCase: RefreshChannelsUseCase,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val result = refreshChannelsUseCase()
        return if (result.isSuccess) Result.success() else Result.retry()
    }

    companion object {
        const val WORK_NAME = "scraper_periodic"
    }
}
