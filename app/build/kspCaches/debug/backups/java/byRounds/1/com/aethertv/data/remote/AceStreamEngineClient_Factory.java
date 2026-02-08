package com.aethertv.data.remote;

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
public final class AceStreamEngineClient_Factory implements Factory<AceStreamEngineClient> {
  private final Provider<HttpClient> httpClientProvider;

  private final Provider<String> engineAddressProvider;

  public AceStreamEngineClient_Factory(Provider<HttpClient> httpClientProvider,
      Provider<String> engineAddressProvider) {
    this.httpClientProvider = httpClientProvider;
    this.engineAddressProvider = engineAddressProvider;
  }

  @Override
  public AceStreamEngineClient get() {
    return newInstance(httpClientProvider.get(), engineAddressProvider.get());
  }

  public static AceStreamEngineClient_Factory create(
      javax.inject.Provider<HttpClient> httpClientProvider,
      javax.inject.Provider<String> engineAddressProvider) {
    return new AceStreamEngineClient_Factory(Providers.asDaggerProvider(httpClientProvider), Providers.asDaggerProvider(engineAddressProvider));
  }

  public static AceStreamEngineClient_Factory create(Provider<HttpClient> httpClientProvider,
      Provider<String> engineAddressProvider) {
    return new AceStreamEngineClient_Factory(httpClientProvider, engineAddressProvider);
  }

  public static AceStreamEngineClient newInstance(HttpClient httpClient, String engineAddress) {
    return new AceStreamEngineClient(httpClient, engineAddress);
  }
}
