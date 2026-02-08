package com.aethertv.ui.setup;

import com.aethertv.data.preferences.SettingsDataStore;
import com.aethertv.data.repository.ChannelRepository;
import com.aethertv.engine.AceStreamEngine;
import com.aethertv.engine.StreamEngine;
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
public final class FirstRunViewModel_Factory implements Factory<FirstRunViewModel> {
  private final Provider<StreamEngine> streamEngineProvider;

  private final Provider<AceStreamEngine> aceStreamEngineProvider;

  private final Provider<ChannelRepository> channelRepositoryProvider;

  private final Provider<SettingsDataStore> settingsDataStoreProvider;

  public FirstRunViewModel_Factory(Provider<StreamEngine> streamEngineProvider,
      Provider<AceStreamEngine> aceStreamEngineProvider,
      Provider<ChannelRepository> channelRepositoryProvider,
      Provider<SettingsDataStore> settingsDataStoreProvider) {
    this.streamEngineProvider = streamEngineProvider;
    this.aceStreamEngineProvider = aceStreamEngineProvider;
    this.channelRepositoryProvider = channelRepositoryProvider;
    this.settingsDataStoreProvider = settingsDataStoreProvider;
  }

  @Override
  public FirstRunViewModel get() {
    return newInstance(streamEngineProvider.get(), aceStreamEngineProvider.get(), channelRepositoryProvider.get(), settingsDataStoreProvider.get());
  }

  public static FirstRunViewModel_Factory create(
      javax.inject.Provider<StreamEngine> streamEngineProvider,
      javax.inject.Provider<AceStreamEngine> aceStreamEngineProvider,
      javax.inject.Provider<ChannelRepository> channelRepositoryProvider,
      javax.inject.Provider<SettingsDataStore> settingsDataStoreProvider) {
    return new FirstRunViewModel_Factory(Providers.asDaggerProvider(streamEngineProvider), Providers.asDaggerProvider(aceStreamEngineProvider), Providers.asDaggerProvider(channelRepositoryProvider), Providers.asDaggerProvider(settingsDataStoreProvider));
  }

  public static FirstRunViewModel_Factory create(Provider<StreamEngine> streamEngineProvider,
      Provider<AceStreamEngine> aceStreamEngineProvider,
      Provider<ChannelRepository> channelRepositoryProvider,
      Provider<SettingsDataStore> settingsDataStoreProvider) {
    return new FirstRunViewModel_Factory(streamEngineProvider, aceStreamEngineProvider, channelRepositoryProvider, settingsDataStoreProvider);
  }

  public static FirstRunViewModel newInstance(StreamEngine streamEngine,
      AceStreamEngine aceStreamEngine, ChannelRepository channelRepository,
      SettingsDataStore settingsDataStore) {
    return new FirstRunViewModel(streamEngine, aceStreamEngine, channelRepository, settingsDataStore);
  }
}
