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
public final class SearchChannelsUseCase_Factory implements Factory<SearchChannelsUseCase> {
  private final Provider<ChannelRepository> channelRepositoryProvider;

  public SearchChannelsUseCase_Factory(Provider<ChannelRepository> channelRepositoryProvider) {
    this.channelRepositoryProvider = channelRepositoryProvider;
  }

  @Override
  public SearchChannelsUseCase get() {
    return newInstance(channelRepositoryProvider.get());
  }

  public static SearchChannelsUseCase_Factory create(
      javax.inject.Provider<ChannelRepository> channelRepositoryProvider) {
    return new SearchChannelsUseCase_Factory(Providers.asDaggerProvider(channelRepositoryProvider));
  }

  public static SearchChannelsUseCase_Factory create(
      Provider<ChannelRepository> channelRepositoryProvider) {
    return new SearchChannelsUseCase_Factory(channelRepositoryProvider);
  }

  public static SearchChannelsUseCase newInstance(ChannelRepository channelRepository) {
    return new SearchChannelsUseCase(channelRepository);
  }
}
