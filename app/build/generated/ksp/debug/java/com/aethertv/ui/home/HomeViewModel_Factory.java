package com.aethertv.ui.home;

import com.aethertv.data.preferences.SettingsDataStore;
import com.aethertv.data.repository.ChannelRepository;
import com.aethertv.domain.usecase.GetChannelsUseCase;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<GetChannelsUseCase> getChannelsUseCaseProvider;

  private final Provider<ChannelRepository> channelRepositoryProvider;

  private final Provider<SettingsDataStore> settingsDataStoreProvider;

  public HomeViewModel_Factory(Provider<GetChannelsUseCase> getChannelsUseCaseProvider,
      Provider<ChannelRepository> channelRepositoryProvider,
      Provider<SettingsDataStore> settingsDataStoreProvider) {
    this.getChannelsUseCaseProvider = getChannelsUseCaseProvider;
    this.channelRepositoryProvider = channelRepositoryProvider;
    this.settingsDataStoreProvider = settingsDataStoreProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(getChannelsUseCaseProvider.get(), channelRepositoryProvider.get(), settingsDataStoreProvider.get());
  }

  public static HomeViewModel_Factory create(
      javax.inject.Provider<GetChannelsUseCase> getChannelsUseCaseProvider,
      javax.inject.Provider<ChannelRepository> channelRepositoryProvider,
      javax.inject.Provider<SettingsDataStore> settingsDataStoreProvider) {
    return new HomeViewModel_Factory(Providers.asDaggerProvider(getChannelsUseCaseProvider), Providers.asDaggerProvider(channelRepositoryProvider), Providers.asDaggerProvider(settingsDataStoreProvider));
  }

  public static HomeViewModel_Factory create(
      Provider<GetChannelsUseCase> getChannelsUseCaseProvider,
      Provider<ChannelRepository> channelRepositoryProvider,
      Provider<SettingsDataStore> settingsDataStoreProvider) {
    return new HomeViewModel_Factory(getChannelsUseCaseProvider, channelRepositoryProvider, settingsDataStoreProvider);
  }

  public static HomeViewModel newInstance(GetChannelsUseCase getChannelsUseCase,
      ChannelRepository channelRepository, SettingsDataStore settingsDataStore) {
    return new HomeViewModel(getChannelsUseCase, channelRepository, settingsDataStore);
  }
}
