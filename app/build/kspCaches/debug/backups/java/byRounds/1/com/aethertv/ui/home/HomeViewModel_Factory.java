package com.aethertv.ui.home;

import com.aethertv.data.local.WatchHistoryDao;
import com.aethertv.data.preferences.SettingsDataStore;
import com.aethertv.data.remote.AceStreamEngineClient;
import com.aethertv.data.repository.ChannelRepository;
import com.aethertv.domain.usecase.GetChannelsUseCase;
import com.aethertv.domain.usecase.GetEpgUseCase;
import com.aethertv.epg.EpgMatcher;
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

  private final Provider<AceStreamEngineClient> aceStreamClientProvider;

  private final Provider<WatchHistoryDao> watchHistoryDaoProvider;

  private final Provider<GetEpgUseCase> getEpgUseCaseProvider;

  private final Provider<EpgMatcher> epgMatcherProvider;

  public HomeViewModel_Factory(Provider<GetChannelsUseCase> getChannelsUseCaseProvider,
      Provider<ChannelRepository> channelRepositoryProvider,
      Provider<SettingsDataStore> settingsDataStoreProvider,
      Provider<AceStreamEngineClient> aceStreamClientProvider,
      Provider<WatchHistoryDao> watchHistoryDaoProvider,
      Provider<GetEpgUseCase> getEpgUseCaseProvider, Provider<EpgMatcher> epgMatcherProvider) {
    this.getChannelsUseCaseProvider = getChannelsUseCaseProvider;
    this.channelRepositoryProvider = channelRepositoryProvider;
    this.settingsDataStoreProvider = settingsDataStoreProvider;
    this.aceStreamClientProvider = aceStreamClientProvider;
    this.watchHistoryDaoProvider = watchHistoryDaoProvider;
    this.getEpgUseCaseProvider = getEpgUseCaseProvider;
    this.epgMatcherProvider = epgMatcherProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(getChannelsUseCaseProvider.get(), channelRepositoryProvider.get(), settingsDataStoreProvider.get(), aceStreamClientProvider.get(), watchHistoryDaoProvider.get(), getEpgUseCaseProvider.get(), epgMatcherProvider.get());
  }

  public static HomeViewModel_Factory create(
      javax.inject.Provider<GetChannelsUseCase> getChannelsUseCaseProvider,
      javax.inject.Provider<ChannelRepository> channelRepositoryProvider,
      javax.inject.Provider<SettingsDataStore> settingsDataStoreProvider,
      javax.inject.Provider<AceStreamEngineClient> aceStreamClientProvider,
      javax.inject.Provider<WatchHistoryDao> watchHistoryDaoProvider,
      javax.inject.Provider<GetEpgUseCase> getEpgUseCaseProvider,
      javax.inject.Provider<EpgMatcher> epgMatcherProvider) {
    return new HomeViewModel_Factory(Providers.asDaggerProvider(getChannelsUseCaseProvider), Providers.asDaggerProvider(channelRepositoryProvider), Providers.asDaggerProvider(settingsDataStoreProvider), Providers.asDaggerProvider(aceStreamClientProvider), Providers.asDaggerProvider(watchHistoryDaoProvider), Providers.asDaggerProvider(getEpgUseCaseProvider), Providers.asDaggerProvider(epgMatcherProvider));
  }

  public static HomeViewModel_Factory create(
      Provider<GetChannelsUseCase> getChannelsUseCaseProvider,
      Provider<ChannelRepository> channelRepositoryProvider,
      Provider<SettingsDataStore> settingsDataStoreProvider,
      Provider<AceStreamEngineClient> aceStreamClientProvider,
      Provider<WatchHistoryDao> watchHistoryDaoProvider,
      Provider<GetEpgUseCase> getEpgUseCaseProvider, Provider<EpgMatcher> epgMatcherProvider) {
    return new HomeViewModel_Factory(getChannelsUseCaseProvider, channelRepositoryProvider, settingsDataStoreProvider, aceStreamClientProvider, watchHistoryDaoProvider, getEpgUseCaseProvider, epgMatcherProvider);
  }

  public static HomeViewModel newInstance(GetChannelsUseCase getChannelsUseCase,
      ChannelRepository channelRepository, SettingsDataStore settingsDataStore,
      AceStreamEngineClient aceStreamClient, WatchHistoryDao watchHistoryDao,
      GetEpgUseCase getEpgUseCase, EpgMatcher epgMatcher) {
    return new HomeViewModel(getChannelsUseCase, channelRepository, settingsDataStore, aceStreamClient, watchHistoryDao, getEpgUseCase, epgMatcher);
  }
}
