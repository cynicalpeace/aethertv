package com.aethertv.ui.settings;

import com.aethertv.data.local.WatchHistoryDao;
import com.aethertv.data.repository.UpdateRepository;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<UpdateRepository> updateRepositoryProvider;

  private final Provider<WatchHistoryDao> watchHistoryDaoProvider;

  public SettingsViewModel_Factory(Provider<UpdateRepository> updateRepositoryProvider,
      Provider<WatchHistoryDao> watchHistoryDaoProvider) {
    this.updateRepositoryProvider = updateRepositoryProvider;
    this.watchHistoryDaoProvider = watchHistoryDaoProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(updateRepositoryProvider.get(), watchHistoryDaoProvider.get());
  }

  public static SettingsViewModel_Factory create(
      javax.inject.Provider<UpdateRepository> updateRepositoryProvider,
      javax.inject.Provider<WatchHistoryDao> watchHistoryDaoProvider) {
    return new SettingsViewModel_Factory(Providers.asDaggerProvider(updateRepositoryProvider), Providers.asDaggerProvider(watchHistoryDaoProvider));
  }

  public static SettingsViewModel_Factory create(
      Provider<UpdateRepository> updateRepositoryProvider,
      Provider<WatchHistoryDao> watchHistoryDaoProvider) {
    return new SettingsViewModel_Factory(updateRepositoryProvider, watchHistoryDaoProvider);
  }

  public static SettingsViewModel newInstance(UpdateRepository updateRepository,
      WatchHistoryDao watchHistoryDao) {
    return new SettingsViewModel(updateRepository, watchHistoryDao);
  }
}
