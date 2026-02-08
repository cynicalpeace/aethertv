package com.aethertv.di;

import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import com.aethertv.data.preferences.SettingsDataStore;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideSettingsDataStoreFactory implements Factory<SettingsDataStore> {
  private final Provider<DataStore<Preferences>> dataStoreProvider;

  public AppModule_ProvideSettingsDataStoreFactory(
      Provider<DataStore<Preferences>> dataStoreProvider) {
    this.dataStoreProvider = dataStoreProvider;
  }

  @Override
  public SettingsDataStore get() {
    return provideSettingsDataStore(dataStoreProvider.get());
  }

  public static AppModule_ProvideSettingsDataStoreFactory create(
      javax.inject.Provider<DataStore<Preferences>> dataStoreProvider) {
    return new AppModule_ProvideSettingsDataStoreFactory(Providers.asDaggerProvider(dataStoreProvider));
  }

  public static AppModule_ProvideSettingsDataStoreFactory create(
      Provider<DataStore<Preferences>> dataStoreProvider) {
    return new AppModule_ProvideSettingsDataStoreFactory(dataStoreProvider);
  }

  public static SettingsDataStore provideSettingsDataStore(DataStore<Preferences> dataStore) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideSettingsDataStore(dataStore));
  }
}
