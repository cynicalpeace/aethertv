package com.aethertv.ui.settings;

import com.aethertv.data.local.ChannelDao;
import com.aethertv.data.local.WatchHistoryDao;
import com.aethertv.data.preferences.SettingsDataStore;
import com.aethertv.data.repository.EpgRepository;
import com.aethertv.data.repository.UpdateRepository;
import com.aethertv.engine.AceStreamEngine;
import com.aethertv.engine.StreamEngine;
import com.aethertv.epg.XmltvParser;
import com.aethertv.verification.StreamVerifier;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<UpdateRepository> updateRepositoryProvider;

  private final Provider<WatchHistoryDao> watchHistoryDaoProvider;

  private final Provider<StreamVerifier> streamVerifierProvider;

  private final Provider<ChannelDao> channelDaoProvider;

  private final Provider<StreamEngine> streamEngineProvider;

  private final Provider<AceStreamEngine> aceStreamEngineProvider;

  private final Provider<SettingsDataStore> settingsDataStoreProvider;

  private final Provider<EpgRepository> epgRepositoryProvider;

  private final Provider<XmltvParser> xmltvParserProvider;

  public SettingsViewModel_Factory(Provider<UpdateRepository> updateRepositoryProvider,
      Provider<WatchHistoryDao> watchHistoryDaoProvider,
      Provider<StreamVerifier> streamVerifierProvider, Provider<ChannelDao> channelDaoProvider,
      Provider<StreamEngine> streamEngineProvider,
      Provider<AceStreamEngine> aceStreamEngineProvider,
      Provider<SettingsDataStore> settingsDataStoreProvider,
      Provider<EpgRepository> epgRepositoryProvider, Provider<XmltvParser> xmltvParserProvider) {
    this.updateRepositoryProvider = updateRepositoryProvider;
    this.watchHistoryDaoProvider = watchHistoryDaoProvider;
    this.streamVerifierProvider = streamVerifierProvider;
    this.channelDaoProvider = channelDaoProvider;
    this.streamEngineProvider = streamEngineProvider;
    this.aceStreamEngineProvider = aceStreamEngineProvider;
    this.settingsDataStoreProvider = settingsDataStoreProvider;
    this.epgRepositoryProvider = epgRepositoryProvider;
    this.xmltvParserProvider = xmltvParserProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(updateRepositoryProvider.get(), watchHistoryDaoProvider.get(), streamVerifierProvider.get(), channelDaoProvider.get(), streamEngineProvider.get(), aceStreamEngineProvider.get(), settingsDataStoreProvider.get(), epgRepositoryProvider.get(), xmltvParserProvider.get());
  }

  public static SettingsViewModel_Factory create(
      javax.inject.Provider<UpdateRepository> updateRepositoryProvider,
      javax.inject.Provider<WatchHistoryDao> watchHistoryDaoProvider,
      javax.inject.Provider<StreamVerifier> streamVerifierProvider,
      javax.inject.Provider<ChannelDao> channelDaoProvider,
      javax.inject.Provider<StreamEngine> streamEngineProvider,
      javax.inject.Provider<AceStreamEngine> aceStreamEngineProvider,
      javax.inject.Provider<SettingsDataStore> settingsDataStoreProvider,
      javax.inject.Provider<EpgRepository> epgRepositoryProvider,
      javax.inject.Provider<XmltvParser> xmltvParserProvider) {
    return new SettingsViewModel_Factory(Providers.asDaggerProvider(updateRepositoryProvider), Providers.asDaggerProvider(watchHistoryDaoProvider), Providers.asDaggerProvider(streamVerifierProvider), Providers.asDaggerProvider(channelDaoProvider), Providers.asDaggerProvider(streamEngineProvider), Providers.asDaggerProvider(aceStreamEngineProvider), Providers.asDaggerProvider(settingsDataStoreProvider), Providers.asDaggerProvider(epgRepositoryProvider), Providers.asDaggerProvider(xmltvParserProvider));
  }

  public static SettingsViewModel_Factory create(
      Provider<UpdateRepository> updateRepositoryProvider,
      Provider<WatchHistoryDao> watchHistoryDaoProvider,
      Provider<StreamVerifier> streamVerifierProvider, Provider<ChannelDao> channelDaoProvider,
      Provider<StreamEngine> streamEngineProvider,
      Provider<AceStreamEngine> aceStreamEngineProvider,
      Provider<SettingsDataStore> settingsDataStoreProvider,
      Provider<EpgRepository> epgRepositoryProvider, Provider<XmltvParser> xmltvParserProvider) {
    return new SettingsViewModel_Factory(updateRepositoryProvider, watchHistoryDaoProvider, streamVerifierProvider, channelDaoProvider, streamEngineProvider, aceStreamEngineProvider, settingsDataStoreProvider, epgRepositoryProvider, xmltvParserProvider);
  }

  public static SettingsViewModel newInstance(UpdateRepository updateRepository,
      WatchHistoryDao watchHistoryDao, StreamVerifier streamVerifier, ChannelDao channelDao,
      StreamEngine streamEngine, AceStreamEngine aceStreamEngine,
      SettingsDataStore settingsDataStore, EpgRepository epgRepository, XmltvParser xmltvParser) {
    return new SettingsViewModel(updateRepository, watchHistoryDao, streamVerifier, channelDao, streamEngine, aceStreamEngine, settingsDataStore, epgRepository, xmltvParser);
  }
}
