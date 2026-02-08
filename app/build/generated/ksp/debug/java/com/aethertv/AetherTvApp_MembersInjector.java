package com.aethertv;

import androidx.hilt.work.HiltWorkerFactory;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

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
public final class AetherTvApp_MembersInjector implements MembersInjector<AetherTvApp> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  public AetherTvApp_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  public static MembersInjector<AetherTvApp> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new AetherTvApp_MembersInjector(workerFactoryProvider);
  }

  public static MembersInjector<AetherTvApp> create(
      javax.inject.Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new AetherTvApp_MembersInjector(Providers.asDaggerProvider(workerFactoryProvider));
  }

  @Override
  public void injectMembers(AetherTvApp instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.aethertv.AetherTvApp.workerFactory")
  public static void injectWorkerFactory(AetherTvApp instance, HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
