package com.aethertv.di;

import android.content.Context;
import com.aethertv.data.repository.UpdateRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.ktor.client.HttpClient;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class AppModule_ProvideUpdateRepositoryFactory implements Factory<UpdateRepository> {
  private final Provider<HttpClient> httpClientProvider;

  private final Provider<Context> contextProvider;

  public AppModule_ProvideUpdateRepositoryFactory(Provider<HttpClient> httpClientProvider,
      Provider<Context> contextProvider) {
    this.httpClientProvider = httpClientProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public UpdateRepository get() {
    return provideUpdateRepository(httpClientProvider.get(), contextProvider.get());
  }

  public static AppModule_ProvideUpdateRepositoryFactory create(
      javax.inject.Provider<HttpClient> httpClientProvider,
      javax.inject.Provider<Context> contextProvider) {
    return new AppModule_ProvideUpdateRepositoryFactory(Providers.asDaggerProvider(httpClientProvider), Providers.asDaggerProvider(contextProvider));
  }

  public static AppModule_ProvideUpdateRepositoryFactory create(
      Provider<HttpClient> httpClientProvider, Provider<Context> contextProvider) {
    return new AppModule_ProvideUpdateRepositoryFactory(httpClientProvider, contextProvider);
  }

  public static UpdateRepository provideUpdateRepository(HttpClient httpClient, Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideUpdateRepository(httpClient, context));
  }
}
