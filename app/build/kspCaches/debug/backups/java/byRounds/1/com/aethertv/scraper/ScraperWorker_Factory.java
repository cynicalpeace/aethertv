package com.aethertv.scraper;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.aethertv.domain.usecase.RefreshChannelsUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class ScraperWorker_Factory {
  private final Provider<RefreshChannelsUseCase> refreshChannelsUseCaseProvider;

  public ScraperWorker_Factory(Provider<RefreshChannelsUseCase> refreshChannelsUseCaseProvider) {
    this.refreshChannelsUseCaseProvider = refreshChannelsUseCaseProvider;
  }

  public ScraperWorker get(Context appContext, WorkerParameters workerParams) {
    return newInstance(appContext, workerParams, refreshChannelsUseCaseProvider.get());
  }

  public static ScraperWorker_Factory create(
      javax.inject.Provider<RefreshChannelsUseCase> refreshChannelsUseCaseProvider) {
    return new ScraperWorker_Factory(Providers.asDaggerProvider(refreshChannelsUseCaseProvider));
  }

  public static ScraperWorker_Factory create(
      Provider<RefreshChannelsUseCase> refreshChannelsUseCaseProvider) {
    return new ScraperWorker_Factory(refreshChannelsUseCaseProvider);
  }

  public static ScraperWorker newInstance(Context appContext, WorkerParameters workerParams,
      RefreshChannelsUseCase refreshChannelsUseCase) {
    return new ScraperWorker(appContext, workerParams, refreshChannelsUseCase);
  }
}
