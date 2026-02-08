package com.aethertv.di;

import com.aethertv.data.local.AetherTvDatabase;
import com.aethertv.data.local.FavoriteDao;
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
public final class DatabaseModule_ProvideFavoriteDaoFactory implements Factory<FavoriteDao> {
  private final Provider<AetherTvDatabase> dbProvider;

  public DatabaseModule_ProvideFavoriteDaoFactory(Provider<AetherTvDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public FavoriteDao get() {
    return provideFavoriteDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideFavoriteDaoFactory create(
      javax.inject.Provider<AetherTvDatabase> dbProvider) {
    return new DatabaseModule_ProvideFavoriteDaoFactory(Providers.asDaggerProvider(dbProvider));
  }

  public static DatabaseModule_ProvideFavoriteDaoFactory create(
      Provider<AetherTvDatabase> dbProvider) {
    return new DatabaseModule_ProvideFavoriteDaoFactory(dbProvider);
  }

  public static FavoriteDao provideFavoriteDao(AetherTvDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideFavoriteDao(db));
  }
}
