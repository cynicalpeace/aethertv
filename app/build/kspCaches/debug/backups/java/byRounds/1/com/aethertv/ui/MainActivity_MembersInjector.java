package com.aethertv.ui;

import com.aethertv.data.preferences.SettingsDataStore;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<SettingsDataStore> settingsDataStoreProvider;

  public MainActivity_MembersInjector(Provider<SettingsDataStore> settingsDataStoreProvider) {
    this.settingsDataStoreProvider = settingsDataStoreProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<SettingsDataStore> settingsDataStoreProvider) {
    return new MainActivity_MembersInjector(settingsDataStoreProvider);
  }

  public static MembersInjector<MainActivity> create(
      javax.inject.Provider<SettingsDataStore> settingsDataStoreProvider) {
    return new MainActivity_MembersInjector(Providers.asDaggerProvider(settingsDataStoreProvider));
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectSettingsDataStore(instance, settingsDataStoreProvider.get());
  }

  @InjectedFieldSignature("com.aethertv.ui.MainActivity.settingsDataStore")
  public static void injectSettingsDataStore(MainActivity instance,
      SettingsDataStore settingsDataStore) {
    instance.settingsDataStore = settingsDataStore;
  }
}
