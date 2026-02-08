package com.aethertv.ui.player;

import androidx.media3.exoplayer.ExoPlayer;
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
public final class PlayerViewModel_Factory implements Factory<PlayerViewModel> {
  private final Provider<ExoPlayer> exoPlayerProvider;

  private final Provider<AceStreamEngineClient> engineClientProvider;

  private final Provider<ChannelRepository> channelRepositoryProvider;

  public PlayerViewModel_Factory(Provider<ExoPlayer> exoPlayerProvider,
      Provider<AceStreamEngineClient> engineClientProvider,
      Provider<ChannelRepository> channelRepositoryProvider) {
    this.exoPlayerProvider = exoPlayerProvider;
    this.engineClientProvider = engineClientProvider;
    this.channelRepositoryProvider = channelRepositoryProvider;
  }

  @Override
  public PlayerViewModel get() {
    return newInstance(exoPlayerProvider.get(), engineClientProvider.get(), channelRepositoryProvider.get());
  }

  public static PlayerViewModel_Factory create(javax.inject.Provider<ExoPlayer> exoPlayerProvider,
      javax.inject.Provider<AceStreamEngineClient> engineClientProvider,
      javax.inject.Provider<ChannelRepository> channelRepositoryProvider) {
    return new PlayerViewModel_Factory(Providers.asDaggerProvider(exoPlayerProvider), Providers.asDaggerProvider(engineClientProvider), Providers.asDaggerProvider(channelRepositoryProvider));
  }

  public static PlayerViewModel_Factory create(Provider<ExoPlayer> exoPlayerProvider,
      Provider<AceStreamEngineClient> engineClientProvider,
      Provider<ChannelRepository> channelRepositoryProvider) {
    return new PlayerViewModel_Factory(exoPlayerProvider, engineClientProvider, channelRepositoryProvider);
  }

  public static PlayerViewModel newInstance(ExoPlayer exoPlayer, AceStreamEngineClient engineClient,
      ChannelRepository channelRepository) {
    return new PlayerViewModel(exoPlayer, engineClient, channelRepository);
  }
}
