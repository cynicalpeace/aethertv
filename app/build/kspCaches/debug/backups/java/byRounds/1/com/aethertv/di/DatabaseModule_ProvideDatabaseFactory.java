package com.aethertv.di;

import android.content.Context;
import com.aethertv.data.local.AetherTvDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class DatabaseModule_ProvideDatabaseFactory implements Factory<AetherTvDatabase> {
  private final Provider<Context> contextProvider;

  public DatabaseModule_ProvideDatabaseFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AetherTvDatabase get() {
    return provideDatabase(contextProvider.get());
  }

  public static DatabaseModule_ProvideDatabaseFactory create(
      javax.inject.Provider<Context> contextProvider) {
    return new DatabaseModule_ProvideDatabaseFactory(Providers.asDaggerProvider(contextProvider));
  }

  public static DatabaseModule_ProvideDatabaseFactory create(Provider<Context> contextProvider) {
    return new DatabaseModule_ProvideDatabaseFactory(contextProvider);
  }

  public static AetherTvDatabase provideDatabase(Context context) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideDatabase(context));
  }
}
