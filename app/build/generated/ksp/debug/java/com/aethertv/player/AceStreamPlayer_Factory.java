package com.aethertv.player;

import androidx.media3.exoplayer.ExoPlayer;
import com.aethertv.data.remote.AceStreamEngineClient;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AceStreamPlayer_Factory implements Factory<AceStreamPlayer> {
  private final Provider<ExoPlayer> exoPlayerProvider;

  private final Provider<AceStreamEngineClient> engineClientProvider;

  public AceStreamPlayer_Factory(Provider<ExoPlayer> exoPlayerProvider,
      Provider<AceStreamEngineClient> engineClientProvider) {
    this.exoPlayerProvider = exoPlayerProvider;
    this.engineClientProvider = engineClientProvider;
  }

  @Override
  public AceStreamPlayer get() {
    return newInstance(exoPlayerProvider.get(), engineClientProvider.get());
  }

  public static AceStreamPlayer_Factory create(javax.inject.Provider<ExoPlayer> exoPlayerProvider,
      javax.inject.Provider<AceStreamEngineClient> engineClientProvider) {
    return new AceStreamPlayer_Factory(Providers.asDaggerProvider(exoPlayerProvider), Providers.asDaggerProvider(engineClientProvider));
  }

  public static AceStreamPlayer_Factory create(Provider<ExoPlayer> exoPlayerProvider,
      Provider<AceStreamEngineClient> engineClientProvider) {
    return new AceStreamPlayer_Factory(exoPlayerProvider, engineClientProvider);
  }

  public static AceStreamPlayer newInstance(ExoPlayer exoPlayer,
      AceStreamEngineClient engineClient) {
    return new AceStreamPlayer(exoPlayer, engineClient);
  }
}
