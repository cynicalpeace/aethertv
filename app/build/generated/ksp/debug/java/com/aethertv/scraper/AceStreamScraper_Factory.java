package com.aethertv.scraper;

import com.aethertv.data.remote.AceStreamEngineClient;
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
public final class AceStreamScraper_Factory implements Factory<AceStreamScraper> {
  private final Provider<AceStreamEngineClient> engineClientProvider;

  private final Provider<ChannelFilter> channelFilterProvider;

  public AceStreamScraper_Factory(Provider<AceStreamEngineClient> engineClientProvider,
      Provider<ChannelFilter> channelFilterProvider) {
    this.engineClientProvider = engineClientProvider;
    this.channelFilterProvider = channelFilterProvider;
  }

  @Override
  public AceStreamScraper get() {
    return newInstance(engineClientProvider.get(), channelFilterProvider.get());
  }

  public static AceStreamScraper_Factory create(
      javax.inject.Provider<AceStreamEngineClient> engineClientProvider,
      javax.inject.Provider<ChannelFilter> channelFilterProvider) {
    return new AceStreamScraper_Factory(Providers.asDaggerProvider(engineClientProvider), Providers.asDaggerProvider(channelFilterProvider));
  }

  public static AceStreamScraper_Factory create(
      Provider<AceStreamEngineClient> engineClientProvider,
      Provider<ChannelFilter> channelFilterProvider) {
    return new AceStreamScraper_Factory(engineClientProvider, channelFilterProvider);
  }

  public static AceStreamScraper newInstance(AceStreamEngineClient engineClient,
      ChannelFilter channelFilter) {
    return new AceStreamScraper(engineClient, channelFilter);
  }
}
