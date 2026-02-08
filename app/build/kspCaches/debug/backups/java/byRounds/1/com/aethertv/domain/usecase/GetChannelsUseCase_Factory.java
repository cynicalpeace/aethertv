package com.aethertv.domain.usecase;

import com.aethertv.data.repository.ChannelRepository;
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
public final class GetChannelsUseCase_Factory implements Factory<GetChannelsUseCase> {
  private final Provider<ChannelRepository> channelRepositoryProvider;

  public GetChannelsUseCase_Factory(Provider<ChannelRepository> channelRepositoryProvider) {
    this.channelRepositoryProvider = channelRepositoryProvider;
  }

  @Override
  public GetChannelsUseCase get() {
    return newInstance(channelRepositoryProvider.get());
  }

  public static GetChannelsUseCase_Factory create(
      javax.inject.Provider<ChannelRepository> channelRepositoryProvider) {
    return new GetChannelsUseCase_Factory(Providers.asDaggerProvider(channelRepositoryProvider));
  }

  public static GetChannelsUseCase_Factory create(
      Provider<ChannelRepository> channelRepositoryProvider) {
    return new GetChannelsUseCase_Factory(channelRepositoryProvider);
  }

  public static GetChannelsUseCase newInstance(ChannelRepository channelRepository) {
    return new GetChannelsUseCase(channelRepository);
  }
}
