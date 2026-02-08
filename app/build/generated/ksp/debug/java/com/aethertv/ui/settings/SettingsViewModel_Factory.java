package com.aethertv.ui.settings;

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

  public SettingsViewModel_Factory(Provider<UpdateRepository> updateRepositoryProvider) {
    this.updateRepositoryProvider = updateRepositoryProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(updateRepositoryProvider.get());
  }

  public static SettingsViewModel_Factory create(
      javax.inject.Provider<UpdateRepository> updateRepositoryProvider) {
    return new SettingsViewModel_Factory(Providers.asDaggerProvider(updateRepositoryProvider));
  }

  public static SettingsViewModel_Factory create(
      Provider<UpdateRepository> updateRepositoryProvider) {
    return new SettingsViewModel_Factory(updateRepositoryProvider);
  }

  public static SettingsViewModel newInstance(UpdateRepository updateRepository) {
    return new SettingsViewModel(updateRepository);
  }
}
