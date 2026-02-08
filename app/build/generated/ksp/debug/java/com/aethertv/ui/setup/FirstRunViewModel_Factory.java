package com.aethertv.ui.setup;

import com.aethertv.data.preferences.SettingsDataStore;
import com.aethertv.data.remote.AceStreamEngineClient;
import com.aethertv.data.repository.ChannelRepository;
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
  private final Provider<AceStreamEngineClient> aceStreamClientProvider;

  private final Provider<ChannelRepository> channelRepositoryProvider;

  private final Provider<SettingsDataStore> settingsDataStoreProvider;

  public FirstRunViewModel_Factory(Provider<AceStreamEngineClient> aceStreamClientProvider,
      Provider<ChannelRepository> channelRepositoryProvider,
      Provider<SettingsDataStore> settingsDataStoreProvider) {
    this.aceStreamClientProvider = aceStreamClientProvider;
    this.channelRepositoryProvider = channelRepositoryProvider;
    this.settingsDataStoreProvider = settingsDataStoreProvider;
  }

  @Override
  public FirstRunViewModel get() {
    return newInstance(aceStreamClientProvider.get(), channelRepositoryProvider.get(), settingsDataStoreProvider.get());
  }

  public static FirstRunViewModel_Factory create(
      javax.inject.Provider<AceStreamEngineClient> aceStreamClientProvider,
      javax.inject.Provider<ChannelRepository> channelRepositoryProvider,
      javax.inject.Provider<SettingsDataStore> settingsDataStoreProvider) {
    return new FirstRunViewModel_Factory(Providers.asDaggerProvider(aceStreamClientProvider), Providers.asDaggerProvider(channelRepositoryProvider), Providers.asDaggerProvider(settingsDataStoreProvider));
  }

  public static FirstRunViewModel_Factory create(
      Provider<AceStreamEngineClient> aceStreamClientProvider,
      Provider<ChannelRepository> channelRepositoryProvider,
      Provider<SettingsDataStore> settingsDataStoreProvider) {
    return new FirstRunViewModel_Factory(aceStreamClientProvider, channelRepositoryProvider, settingsDataStoreProvider);
  }

  public static FirstRunViewModel newInstance(AceStreamEngineClient aceStreamClient,
      ChannelRepository channelRepository, SettingsDataStore settingsDataStore) {
    return new FirstRunViewModel(aceStreamClient, channelRepository, settingsDataStore);
  }
}
