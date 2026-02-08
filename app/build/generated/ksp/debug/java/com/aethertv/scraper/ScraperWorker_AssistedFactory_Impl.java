package com.aethertv.scraper;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class ScraperWorker_AssistedFactory_Impl implements ScraperWorker_AssistedFactory {
  private final ScraperWorker_Factory delegateFactory;

  ScraperWorker_AssistedFactory_Impl(ScraperWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public ScraperWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<ScraperWorker_AssistedFactory> create(
      ScraperWorker_Factory delegateFactory) {
    return InstanceFactory.create(new ScraperWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<ScraperWorker_AssistedFactory> createFactoryProvider(
      ScraperWorker_Factory delegateFactory) {
    return InstanceFactory.create(new ScraperWorker_AssistedFactory_Impl(delegateFactory));
  }
}
