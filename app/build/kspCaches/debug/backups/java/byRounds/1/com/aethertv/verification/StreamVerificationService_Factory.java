package com.aethertv.verification;

import com.aethertv.data.local.ChannelDao;
import com.aethertv.data.remote.AceStreamEngineClient;
import com.aethertv.scraper.StreamChecker;
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
public final class StreamVerificationService_Factory implements Factory<StreamVerificationService> {
  private final Provider<ChannelDao> channelDaoProvider;

  private final Provider<AceStreamEngineClient> aceStreamClientProvider;

  private final Provider<StreamChecker> streamCheckerProvider;

  public StreamVerificationService_Factory(Provider<ChannelDao> channelDaoProvider,
      Provider<AceStreamEngineClient> aceStreamClientProvider,
      Provider<StreamChecker> streamCheckerProvider) {
    this.channelDaoProvider = channelDaoProvider;
    this.aceStreamClientProvider = aceStreamClientProvider;
    this.streamCheckerProvider = streamCheckerProvider;
  }

  @Override
  public StreamVerificationService get() {
    return newInstance(channelDaoProvider.get(), aceStreamClientProvider.get(), streamCheckerProvider.get());
  }

  public static StreamVerificationService_Factory create(
      javax.inject.Provider<ChannelDao> channelDaoProvider,
      javax.inject.Provider<AceStreamEngineClient> aceStreamClientProvider,
      javax.inject.Provider<StreamChecker> streamCheckerProvider) {
    return new StreamVerificationService_Factory(Providers.asDaggerProvider(channelDaoProvider), Providers.asDaggerProvider(aceStreamClientProvider), Providers.asDaggerProvider(streamCheckerProvider));
  }

  public static StreamVerificationService_Factory create(Provider<ChannelDao> channelDaoProvider,
      Provider<AceStreamEngineClient> aceStreamClientProvider,
      Provider<StreamChecker> streamCheckerProvider) {
    return new StreamVerificationService_Factory(channelDaoProvider, aceStreamClientProvider, streamCheckerProvider);
  }

  public static StreamVerificationService newInstance(ChannelDao channelDao,
      AceStreamEngineClient aceStreamClient, StreamChecker streamChecker) {
    return new StreamVerificationService(channelDao, aceStreamClient, streamChecker);
  }
}
