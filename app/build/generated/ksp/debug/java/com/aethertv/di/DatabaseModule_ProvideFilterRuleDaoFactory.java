package com.aethertv.di;

import com.aethertv.data.local.AetherTvDatabase;
import com.aethertv.data.local.FilterRuleDao;
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
public final class DatabaseModule_ProvideFilterRuleDaoFactory implements Factory<FilterRuleDao> {
  private final Provider<AetherTvDatabase> dbProvider;

  public DatabaseModule_ProvideFilterRuleDaoFactory(Provider<AetherTvDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public FilterRuleDao get() {
    return provideFilterRuleDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideFilterRuleDaoFactory create(
      javax.inject.Provider<AetherTvDatabase> dbProvider) {
    return new DatabaseModule_ProvideFilterRuleDaoFactory(Providers.asDaggerProvider(dbProvider));
  }

  public static DatabaseModule_ProvideFilterRuleDaoFactory create(
      Provider<AetherTvDatabase> dbProvider) {
    return new DatabaseModule_ProvideFilterRuleDaoFactory(dbProvider);
  }

  public static FilterRuleDao provideFilterRuleDao(AetherTvDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideFilterRuleDao(db));
  }
}
