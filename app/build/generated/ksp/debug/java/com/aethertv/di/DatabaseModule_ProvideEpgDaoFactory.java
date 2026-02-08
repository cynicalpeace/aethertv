package com.aethertv.di;

import com.aethertv.data.local.AetherTvDatabase;
import com.aethertv.data.local.EpgDao;
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
public final class DatabaseModule_ProvideEpgDaoFactory implements Factory<EpgDao> {
  private final Provider<AetherTvDatabase> dbProvider;

  public DatabaseModule_ProvideEpgDaoFactory(Provider<AetherTvDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public EpgDao get() {
    return provideEpgDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideEpgDaoFactory create(
      javax.inject.Provider<AetherTvDatabase> dbProvider) {
    return new DatabaseModule_ProvideEpgDaoFactory(Providers.asDaggerProvider(dbProvider));
  }

  public static DatabaseModule_ProvideEpgDaoFactory create(Provider<AetherTvDatabase> dbProvider) {
    return new DatabaseModule_ProvideEpgDaoFactory(dbProvider);
  }

  public static EpgDao provideEpgDao(AetherTvDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideEpgDao(db));
  }
}
