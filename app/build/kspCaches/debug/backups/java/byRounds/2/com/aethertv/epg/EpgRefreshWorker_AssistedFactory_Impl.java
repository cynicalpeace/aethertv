package com.aethertv.epg;

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
public final class EpgRefreshWorker_AssistedFactory_Impl implements EpgRefreshWorker_AssistedFactory {
  private final EpgRefreshWorker_Factory delegateFactory;

  EpgRefreshWorker_AssistedFactory_Impl(EpgRefreshWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public EpgRefreshWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<EpgRefreshWorker_AssistedFactory> create(
      EpgRefreshWorker_Factory delegateFactory) {
    return InstanceFactory.create(new EpgRefreshWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<EpgRefreshWorker_AssistedFactory> createFactoryProvider(
      EpgRefreshWorker_Factory delegateFactory) {
    return InstanceFactory.create(new EpgRefreshWorker_AssistedFactory_Impl(delegateFactory));
  }
}
