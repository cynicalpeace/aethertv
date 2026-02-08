package com.aethertv.epg;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.aethertv.data.preferences.SettingsDataStore;
import com.aethertv.data.repository.EpgRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.ktor.client.HttpClient;
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
public final class EpgRefreshWorker_Factory {
  private final Provider<HttpClient> httpClientProvider;

  private final Provider<XmltvParser> xmltvParserProvider;

  private final Provider<EpgRepository> epgRepositoryProvider;

  private final Provider<SettingsDataStore> settingsDataStoreProvider;

  public EpgRefreshWorker_Factory(Provider<HttpClient> httpClientProvider,
      Provider<XmltvParser> xmltvParserProvider, Provider<EpgRepository> epgRepositoryProvider,
      Provider<SettingsDataStore> settingsDataStoreProvider) {
    this.httpClientProvider = httpClientProvider;
    this.xmltvParserProvider = xmltvParserProvider;
    this.epgRepositoryProvider = epgRepositoryProvider;
    this.settingsDataStoreProvider = settingsDataStoreProvider;
  }

  public EpgRefreshWorker get(Context appContext, WorkerParameters workerParams) {
    return newInstance(appContext, workerParams, httpClientProvider.get(), xmltvParserProvider.get(), epgRepositoryProvider.get(), settingsDataStoreProvider.get());
  }

  public static EpgRefreshWorker_Factory create(
      javax.inject.Provider<HttpClient> httpClientProvider,
      javax.inject.Provider<XmltvParser> xmltvParserProvider,
      javax.inject.Provider<EpgRepository> epgRepositoryProvider,
      javax.inject.Provider<SettingsDataStore> settingsDataStoreProvider) {
    return new EpgRefreshWorker_Factory(Providers.asDaggerProvider(httpClientProvider), Providers.asDaggerProvider(xmltvParserProvider), Providers.asDaggerProvider(epgRepositoryProvider), Providers.asDaggerProvider(settingsDataStoreProvider));
  }

  public static EpgRefreshWorker_Factory create(Provider<HttpClient> httpClientProvider,
      Provider<XmltvParser> xmltvParserProvider, Provider<EpgRepository> epgRepositoryProvider,
      Provider<SettingsDataStore> settingsDataStoreProvider) {
    return new EpgRefreshWorker_Factory(httpClientProvider, xmltvParserProvider, epgRepositoryProvider, settingsDataStoreProvider);
  }

  public static EpgRefreshWorker newInstance(Context appContext, WorkerParameters workerParams,
      HttpClient httpClient, XmltvParser xmltvParser, EpgRepository epgRepository,
      SettingsDataStore settingsDataStore) {
    return new EpgRefreshWorker(appContext, workerParams, httpClient, xmltvParser, epgRepository, settingsDataStore);
  }
}
