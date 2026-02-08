package com.aethertv.data.repository;

import com.aethertv.data.local.EpgDao;
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
public final class EpgRepositoryImpl_Factory implements Factory<EpgRepositoryImpl> {
  private final Provider<EpgDao> epgDaoProvider;

  public EpgRepositoryImpl_Factory(Provider<EpgDao> epgDaoProvider) {
    this.epgDaoProvider = epgDaoProvider;
  }

  @Override
  public EpgRepositoryImpl get() {
    return newInstance(epgDaoProvider.get());
  }

  public static EpgRepositoryImpl_Factory create(javax.inject.Provider<EpgDao> epgDaoProvider) {
    return new EpgRepositoryImpl_Factory(Providers.asDaggerProvider(epgDaoProvider));
  }

  public static EpgRepositoryImpl_Factory create(Provider<EpgDao> epgDaoProvider) {
    return new EpgRepositoryImpl_Factory(epgDaoProvider);
  }

  public static EpgRepositoryImpl newInstance(EpgDao epgDao) {
    return new EpgRepositoryImpl(epgDao);
  }
}
