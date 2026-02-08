package com.aethertv.data.repository;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.ktor.client.HttpClient;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class UpdateRepository_Factory implements Factory<UpdateRepository> {
  private final Provider<HttpClient> httpClientProvider;

  private final Provider<Context> contextProvider;

  public UpdateRepository_Factory(Provider<HttpClient> httpClientProvider,
      Provider<Context> contextProvider) {
    this.httpClientProvider = httpClientProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public UpdateRepository get() {
    return newInstance(httpClientProvider.get(), contextProvider.get());
  }

  public static UpdateRepository_Factory create(
      javax.inject.Provider<HttpClient> httpClientProvider,
      javax.inject.Provider<Context> contextProvider) {
    return new UpdateRepository_Factory(Providers.asDaggerProvider(httpClientProvider), Providers.asDaggerProvider(contextProvider));
  }

  public static UpdateRepository_Factory create(Provider<HttpClient> httpClientProvider,
      Provider<Context> contextProvider) {
    return new UpdateRepository_Factory(httpClientProvider, contextProvider);
  }

  public static UpdateRepository newInstance(HttpClient httpClient, Context context) {
    return new UpdateRepository(httpClient, context);
  }
}
