package com.aethertv.verification;

import com.aethertv.data.repository.ChannelRepository;
import com.aethertv.domain.usecase.VerifyStreamsUseCase;
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
public final class VerificationScheduler_Factory implements Factory<VerificationScheduler> {
  private final Provider<StreamVerifier> streamVerifierProvider;

  private final Provider<VerifyStreamsUseCase> verifyStreamsUseCaseProvider;

  private final Provider<ChannelRepository> channelRepositoryProvider;

  public VerificationScheduler_Factory(Provider<StreamVerifier> streamVerifierProvider,
      Provider<VerifyStreamsUseCase> verifyStreamsUseCaseProvider,
      Provider<ChannelRepository> channelRepositoryProvider) {
    this.streamVerifierProvider = streamVerifierProvider;
    this.verifyStreamsUseCaseProvider = verifyStreamsUseCaseProvider;
    this.channelRepositoryProvider = channelRepositoryProvider;
  }

  @Override
  public VerificationScheduler get() {
    return newInstance(streamVerifierProvider.get(), verifyStreamsUseCaseProvider.get(), channelRepositoryProvider.get());
  }

  public static VerificationScheduler_Factory create(
      javax.inject.Provider<StreamVerifier> streamVerifierProvider,
      javax.inject.Provider<VerifyStreamsUseCase> verifyStreamsUseCaseProvider,
      javax.inject.Provider<ChannelRepository> channelRepositoryProvider) {
    return new VerificationScheduler_Factory(Providers.asDaggerProvider(streamVerifierProvider), Providers.asDaggerProvider(verifyStreamsUseCaseProvider), Providers.asDaggerProvider(channelRepositoryProvider));
  }

  public static VerificationScheduler_Factory create(
      Provider<StreamVerifier> streamVerifierProvider,
      Provider<VerifyStreamsUseCase> verifyStreamsUseCaseProvider,
      Provider<ChannelRepository> channelRepositoryProvider) {
    return new VerificationScheduler_Factory(streamVerifierProvider, verifyStreamsUseCaseProvider, channelRepositoryProvider);
  }

  public static VerificationScheduler newInstance(StreamVerifier streamVerifier,
      VerifyStreamsUseCase verifyStreamsUseCase, ChannelRepository channelRepository) {
    return new VerificationScheduler(streamVerifier, verifyStreamsUseCase, channelRepository);
  }
}
