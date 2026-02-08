package com.aethertv.di;

import com.aethertv.data.remote.AceStreamEngineClient;
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
public final class NetworkModule_ProvideAceStreamEngineClientFactory implements Factory<AceStreamEngineClient> {
  private final Provider<HttpClient> httpClientProvider;

  public NetworkModule_ProvideAceStreamEngineClientFactory(
      Provider<HttpClient> httpClientProvider) {
    this.httpClientProvider = httpClientProvider;
  }

  @Override
  public AceStreamEngineClient get() {
    return provideAceStreamEngineClient(httpClientProvider.get());
  }

  public static NetworkModule_ProvideAceStreamEngineClientFactory create(
      javax.inject.Provider<HttpClient> httpClientProvider) {
    return new NetworkModule_ProvideAceStreamEngineClientFactory(Providers.asDaggerProvider(httpClientProvider));
  }

  public static NetworkModule_ProvideAceStreamEngineClientFactory create(
      Provider<HttpClient> httpClientProvider) {
    return new NetworkModule_ProvideAceStreamEngineClientFactory(httpClientProvider);
  }

  public static AceStreamEngineClient provideAceStreamEngineClient(HttpClient httpClient) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideAceStreamEngineClient(httpClient));
  }
}
