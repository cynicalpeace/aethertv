package com.aethertv.domain.usecase;

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
public final class RefreshChannelsUseCase_Factory implements Factory<RefreshChannelsUseCase> {
  private final Provider<AceStreamEngineClient> engineClientProvider;

  private final Provider<ChannelRepository> channelRepositoryProvider;

  private final Provider<SettingsDataStore> settingsDataStoreProvider;

  public RefreshChannelsUseCase_Factory(Provider<AceStreamEngineClient> engineClientProvider,
      Provider<ChannelRepository> channelRepositoryProvider,
      Provider<SettingsDataStore> settingsDataStoreProvider) {
    this.engineClientProvider = engineClientProvider;
    this.channelRepositoryProvider = channelRepositoryProvider;
    this.settingsDataStoreProvider = settingsDataStoreProvider;
  }

  @Override
  public RefreshChannelsUseCase get() {
    return newInstance(engineClientProvider.get(), channelRepositoryProvider.get(), settingsDataStoreProvider.get());
  }

  public static RefreshChannelsUseCase_Factory create(
      javax.inject.Provider<AceStreamEngineClient> engineClientProvider,
      javax.inject.Provider<ChannelRepository> channelRepositoryProvider,
      javax.inject.Provider<SettingsDataStore> settingsDataStoreProvider) {
    return new RefreshChannelsUseCase_Factory(Providers.asDaggerProvider(engineClientProvider), Providers.asDaggerProvider(channelRepositoryProvider), Providers.asDaggerProvider(settingsDataStoreProvider));
  }

  public static RefreshChannelsUseCase_Factory create(
      Provider<AceStreamEngineClient> engineClientProvider,
      Provider<ChannelRepository> channelRepositoryProvider,
      Provider<SettingsDataStore> settingsDataStoreProvider) {
    return new RefreshChannelsUseCase_Factory(engineClientProvider, channelRepositoryProvider, settingsDataStoreProvider);
  }

  public static RefreshChannelsUseCase newInstance(AceStreamEngineClient engineClient,
      ChannelRepository channelRepository, SettingsDataStore settingsDataStore) {
    return new RefreshChannelsUseCase(engineClient, channelRepository, settingsDataStore);
  }
}
