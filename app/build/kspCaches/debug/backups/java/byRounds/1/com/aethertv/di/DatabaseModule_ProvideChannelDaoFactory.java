package com.aethertv.di;

import com.aethertv.data.local.AetherTvDatabase;
import com.aethertv.data.local.ChannelDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideChannelDaoFactory implements Factory<ChannelDao> {
  private final Provider<AetherTvDatabase> dbProvider;

  public DatabaseModule_ProvideChannelDaoFactory(Provider<AetherTvDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ChannelDao get() {
    return provideChannelDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideChannelDaoFactory create(
      javax.inject.Provider<AetherTvDatabase> dbProvider) {
    return new DatabaseModule_ProvideChannelDaoFactory(Providers.asDaggerProvider(dbProvider));
  }

  public static DatabaseModule_ProvideChannelDaoFactory create(
      Provider<AetherTvDatabase> dbProvider) {
    return new DatabaseModule_ProvideChannelDaoFactory(dbProvider);
  }

  public static ChannelDao provideChannelDao(AetherTvDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideChannelDao(db));
  }
}
