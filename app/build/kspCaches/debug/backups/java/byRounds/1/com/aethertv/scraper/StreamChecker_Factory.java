package com.aethertv.scraper;

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
public final class StreamChecker_Factory implements Factory<StreamChecker> {
  private final Provider<HttpClient> httpClientProvider;

  public StreamChecker_Factory(Provider<HttpClient> httpClientProvider) {
    this.httpClientProvider = httpClientProvider;
  }

  @Override
  public StreamChecker get() {
    return newInstance(httpClientProvider.get());
  }

  public static StreamChecker_Factory create(javax.inject.Provider<HttpClient> httpClientProvider) {
    return new StreamChecker_Factory(Providers.asDaggerProvider(httpClientProvider));
  }

  public static StreamChecker_Factory create(Provider<HttpClient> httpClientProvider) {
    return new StreamChecker_Factory(httpClientProvider);
  }

  public static StreamChecker newInstance(HttpClient httpClient) {
    return new StreamChecker(httpClient);
  }
}
