package com.aethertv.data.repository;

import com.aethertv.data.local.ChannelDao;
import com.aethertv.data.local.FavoriteDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import kotlinx.serialization.json.Json;

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
public final class ChannelRepositoryImpl_Factory implements Factory<ChannelRepositoryImpl> {
  private final Provider<ChannelDao> channelDaoProvider;

  private final Provider<FavoriteDao> favoriteDaoProvider;

  private final Provider<Json> jsonProvider;

  public ChannelRepositoryImpl_Factory(Provider<ChannelDao> channelDaoProvider,
      Provider<FavoriteDao> favoriteDaoProvider, Provider<Json> jsonProvider) {
    this.channelDaoProvider = channelDaoProvider;
    this.favoriteDaoProvider = favoriteDaoProvider;
    this.jsonProvider = jsonProvider;
  }

  @Override
  public ChannelRepositoryImpl get() {
    return newInstance(channelDaoProvider.get(), favoriteDaoProvider.get(), jsonProvider.get());
  }

  public static ChannelRepositoryImpl_Factory create(
      javax.inject.Provider<ChannelDao> channelDaoProvider,
      javax.inject.Provider<FavoriteDao> favoriteDaoProvider,
      javax.inject.Provider<Json> jsonProvider) {
    return new ChannelRepositoryImpl_Factory(Providers.asDaggerProvider(channelDaoProvider), Providers.asDaggerProvider(favoriteDaoProvider), Providers.asDaggerProvider(jsonProvider));
  }

  public static ChannelRepositoryImpl_Factory create(Provider<ChannelDao> channelDaoProvider,
      Provider<FavoriteDao> favoriteDaoProvider, Provider<Json> jsonProvider) {
    return new ChannelRepositoryImpl_Factory(channelDaoProvider, favoriteDaoProvider, jsonProvider);
  }

  public static ChannelRepositoryImpl newInstance(ChannelDao channelDao, FavoriteDao favoriteDao,
      Json json) {
    return new ChannelRepositoryImpl(channelDao, favoriteDao, json);
  }
}
