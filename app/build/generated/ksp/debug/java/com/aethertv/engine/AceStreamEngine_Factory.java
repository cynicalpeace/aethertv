package com.aethertv.engine;

import android.content.Context;
import com.aethertv.data.remote.AceStreamEngineClient;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class AceStreamEngine_Factory implements Factory<AceStreamEngine> {
  private final Provider<Context> contextProvider;

  private final Provider<AceStreamEngineClient> engineClientProvider;

  public AceStreamEngine_Factory(Provider<Context> contextProvider,
      Provider<AceStreamEngineClient> engineClientProvider) {
    this.contextProvider = contextProvider;
    this.engineClientProvider = engineClientProvider;
  }

  @Override
  public AceStreamEngine get() {
    return newInstance(contextProvider.get(), engineClientProvider.get());
  }

  public static AceStreamEngine_Factory create(javax.inject.Provider<Context> contextProvider,
      javax.inject.Provider<AceStreamEngineClient> engineClientProvider) {
    return new AceStreamEngine_Factory(Providers.asDaggerProvider(contextProvider), Providers.asDaggerProvider(engineClientProvider));
  }

  public static AceStreamEngine_Factory create(Provider<Context> contextProvider,
      Provider<AceStreamEngineClient> engineClientProvider) {
    return new AceStreamEngine_Factory(contextProvider, engineClientProvider);
  }

  public static AceStreamEngine newInstance(Context context, AceStreamEngineClient engineClient) {
    return new AceStreamEngine(context, engineClient);
  }
}
