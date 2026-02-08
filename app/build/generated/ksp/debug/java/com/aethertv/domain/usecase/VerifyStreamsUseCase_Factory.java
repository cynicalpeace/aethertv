package com.aethertv.domain.usecase;

import com.aethertv.data.local.FavoriteDao;
import com.aethertv.data.local.WatchHistoryDao;
import com.aethertv.data.preferences.SettingsDataStore;
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
public final class VerifyStreamsUseCase_Factory implements Factory<VerifyStreamsUseCase> {
  private final Provider<ChannelRepository> channelRepositoryProvider;

  private final Provider<FavoriteDao> favoriteDaoProvider;

  private final Provider<WatchHistoryDao> watchHistoryDaoProvider;

  private final Provider<SettingsDataStore> settingsDataStoreProvider;

  public VerifyStreamsUseCase_Factory(Provider<ChannelRepository> channelRepositoryProvider,
      Provider<FavoriteDao> favoriteDaoProvider, Provider<WatchHistoryDao> watchHistoryDaoProvider,
      Provider<SettingsDataStore> settingsDataStoreProvider) {
    this.channelRepositoryProvider = channelRepositoryProvider;
    this.favoriteDaoProvider = favoriteDaoProvider;
    this.watchHistoryDaoProvider = watchHistoryDaoProvider;
    this.settingsDataStoreProvider = settingsDataStoreProvider;
  }

  @Override
  public VerifyStreamsUseCase get() {
    return newInstance(channelRepositoryProvider.get(), favoriteDaoProvider.get(), watchHistoryDaoProvider.get(), settingsDataStoreProvider.get());
  }

  public static VerifyStreamsUseCase_Factory create(
      javax.inject.Provider<ChannelRepository> channelRepositoryProvider,
      javax.inject.Provider<FavoriteDao> favoriteDaoProvider,
      javax.inject.Provider<WatchHistoryDao> watchHistoryDaoProvider,
      javax.inject.Provider<SettingsDataStore> settingsDataStoreProvider) {
    return new VerifyStreamsUseCase_Factory(Providers.asDaggerProvider(channelRepositoryProvider), Providers.asDaggerProvider(favoriteDaoProvider), Providers.asDaggerProvider(watchHistoryDaoProvider), Providers.asDaggerProvider(settingsDataStoreProvider));
  }

  public static VerifyStreamsUseCase_Factory create(
      Provider<ChannelRepository> channelRepositoryProvider,
      Provider<FavoriteDao> favoriteDaoProvider, Provider<WatchHistoryDao> watchHistoryDaoProvider,
      Provider<SettingsDataStore> settingsDataStoreProvider) {
    return new VerifyStreamsUseCase_Factory(channelRepositoryProvider, favoriteDaoProvider, watchHistoryDaoProvider, settingsDataStoreProvider);
  }

  public static VerifyStreamsUseCase newInstance(ChannelRepository channelRepository,
      FavoriteDao favoriteDao, WatchHistoryDao watchHistoryDao,
      SettingsDataStore settingsDataStore) {
    return new VerifyStreamsUseCase(channelRepository, favoriteDao, watchHistoryDao, settingsDataStore);
  }
}
