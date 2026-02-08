package com.aethertv.ui.player;

import androidx.media3.exoplayer.ExoPlayer;
import com.aethertv.data.local.WatchHistoryDao;
import com.aethertv.data.remote.AceStreamEngineClient;
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
public final class PlayerViewModel_Factory implements Factory<PlayerViewModel> {
  private final Provider<ExoPlayer> exoPlayerProvider;

  private final Provider<AceStreamEngineClient> engineClientProvider;

  private final Provider<ChannelRepository> channelRepositoryProvider;

  private final Provider<GetChannelsUseCase> getChannelsUseCaseProvider;

  private final Provider<WatchHistoryDao> watchHistoryDaoProvider;

  public PlayerViewModel_Factory(Provider<ExoPlayer> exoPlayerProvider,
      Provider<AceStreamEngineClient> engineClientProvider,
      Provider<ChannelRepository> channelRepositoryProvider,
      Provider<GetChannelsUseCase> getChannelsUseCaseProvider,
      Provider<WatchHistoryDao> watchHistoryDaoProvider) {
    this.exoPlayerProvider = exoPlayerProvider;
    this.engineClientProvider = engineClientProvider;
    this.channelRepositoryProvider = channelRepositoryProvider;
    this.getChannelsUseCaseProvider = getChannelsUseCaseProvider;
    this.watchHistoryDaoProvider = watchHistoryDaoProvider;
  }

  @Override
  public PlayerViewModel get() {
    return newInstance(exoPlayerProvider.get(), engineClientProvider.get(), channelRepositoryProvider.get(), getChannelsUseCaseProvider.get(), watchHistoryDaoProvider.get());
  }

  public static PlayerViewModel_Factory create(javax.inject.Provider<ExoPlayer> exoPlayerProvider,
      javax.inject.Provider<AceStreamEngineClient> engineClientProvider,
      javax.inject.Provider<ChannelRepository> channelRepositoryProvider,
      javax.inject.Provider<GetChannelsUseCase> getChannelsUseCaseProvider,
      javax.inject.Provider<WatchHistoryDao> watchHistoryDaoProvider) {
    return new PlayerViewModel_Factory(Providers.asDaggerProvider(exoPlayerProvider), Providers.asDaggerProvider(engineClientProvider), Providers.asDaggerProvider(channelRepositoryProvider), Providers.asDaggerProvider(getChannelsUseCaseProvider), Providers.asDaggerProvider(watchHistoryDaoProvider));
  }

  public static PlayerViewModel_Factory create(Provider<ExoPlayer> exoPlayerProvider,
      Provider<AceStreamEngineClient> engineClientProvider,
      Provider<ChannelRepository> channelRepositoryProvider,
      Provider<GetChannelsUseCase> getChannelsUseCaseProvider,
      Provider<WatchHistoryDao> watchHistoryDaoProvider) {
    return new PlayerViewModel_Factory(exoPlayerProvider, engineClientProvider, channelRepositoryProvider, getChannelsUseCaseProvider, watchHistoryDaoProvider);
  }

  public static PlayerViewModel newInstance(ExoPlayer exoPlayer, AceStreamEngineClient engineClient,
      ChannelRepository channelRepository, GetChannelsUseCase getChannelsUseCase,
      WatchHistoryDao watchHistoryDao) {
    return new PlayerViewModel(exoPlayer, engineClient, channelRepository, getChannelsUseCase, watchHistoryDao);
  }
}
