package com.aethertv.ui.guide;

import com.aethertv.data.repository.ChannelRepository;
import com.aethertv.domain.usecase.GetEpgUseCase;
import com.aethertv.epg.EpgMatcher;
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
public final class GuideViewModel_Factory implements Factory<GuideViewModel> {
  private final Provider<GetEpgUseCase> getEpgUseCaseProvider;

  private final Provider<ChannelRepository> channelRepositoryProvider;

  private final Provider<EpgMatcher> epgMatcherProvider;

  public GuideViewModel_Factory(Provider<GetEpgUseCase> getEpgUseCaseProvider,
      Provider<ChannelRepository> channelRepositoryProvider,
      Provider<EpgMatcher> epgMatcherProvider) {
    this.getEpgUseCaseProvider = getEpgUseCaseProvider;
    this.channelRepositoryProvider = channelRepositoryProvider;
    this.epgMatcherProvider = epgMatcherProvider;
  }

  @Override
  public GuideViewModel get() {
    return newInstance(getEpgUseCaseProvider.get(), channelRepositoryProvider.get(), epgMatcherProvider.get());
  }

  public static GuideViewModel_Factory create(
      javax.inject.Provider<GetEpgUseCase> getEpgUseCaseProvider,
      javax.inject.Provider<ChannelRepository> channelRepositoryProvider,
      javax.inject.Provider<EpgMatcher> epgMatcherProvider) {
    return new GuideViewModel_Factory(Providers.asDaggerProvider(getEpgUseCaseProvider), Providers.asDaggerProvider(channelRepositoryProvider), Providers.asDaggerProvider(epgMatcherProvider));
  }

  public static GuideViewModel_Factory create(Provider<GetEpgUseCase> getEpgUseCaseProvider,
      Provider<ChannelRepository> channelRepositoryProvider,
      Provider<EpgMatcher> epgMatcherProvider) {
    return new GuideViewModel_Factory(getEpgUseCaseProvider, channelRepositoryProvider, epgMatcherProvider);
  }

  public static GuideViewModel newInstance(GetEpgUseCase getEpgUseCase,
      ChannelRepository channelRepository, EpgMatcher epgMatcher) {
    return new GuideViewModel(getEpgUseCase, channelRepository, epgMatcher);
  }
}
