package com.aethertv.player;

import android.content.Context;
import androidx.media3.exoplayer.ExoPlayer;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class MediaSessionHandler_Factory implements Factory<MediaSessionHandler> {
  private final Provider<Context> contextProvider;

  private final Provider<ExoPlayer> exoPlayerProvider;

  public MediaSessionHandler_Factory(Provider<Context> contextProvider,
      Provider<ExoPlayer> exoPlayerProvider) {
    this.contextProvider = contextProvider;
    this.exoPlayerProvider = exoPlayerProvider;
  }

  @Override
  public MediaSessionHandler get() {
    return newInstance(contextProvider.get(), exoPlayerProvider.get());
  }

  public static MediaSessionHandler_Factory create(javax.inject.Provider<Context> contextProvider,
      javax.inject.Provider<ExoPlayer> exoPlayerProvider) {
    return new MediaSessionHandler_Factory(Providers.asDaggerProvider(contextProvider), Providers.asDaggerProvider(exoPlayerProvider));
  }

  public static MediaSessionHandler_Factory create(Provider<Context> contextProvider,
      Provider<ExoPlayer> exoPlayerProvider) {
    return new MediaSessionHandler_Factory(contextProvider, exoPlayerProvider);
  }

  public static MediaSessionHandler newInstance(Context context, ExoPlayer exoPlayer) {
    return new MediaSessionHandler(context, exoPlayer);
  }
}
