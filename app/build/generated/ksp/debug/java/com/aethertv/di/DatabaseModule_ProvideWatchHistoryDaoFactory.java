package com.aethertv.di;

import com.aethertv.data.local.AetherTvDatabase;
import com.aethertv.data.local.WatchHistoryDao;
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
public final class DatabaseModule_ProvideWatchHistoryDaoFactory implements Factory<WatchHistoryDao> {
  private final Provider<AetherTvDatabase> dbProvider;

  public DatabaseModule_ProvideWatchHistoryDaoFactory(Provider<AetherTvDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public WatchHistoryDao get() {
    return provideWatchHistoryDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideWatchHistoryDaoFactory create(
      javax.inject.Provider<AetherTvDatabase> dbProvider) {
    return new DatabaseModule_ProvideWatchHistoryDaoFactory(Providers.asDaggerProvider(dbProvider));
  }

  public static DatabaseModule_ProvideWatchHistoryDaoFactory create(
      Provider<AetherTvDatabase> dbProvider) {
    return new DatabaseModule_ProvideWatchHistoryDaoFactory(dbProvider);
  }

  public static WatchHistoryDao provideWatchHistoryDao(AetherTvDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideWatchHistoryDao(db));
  }
}
