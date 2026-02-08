package com.aethertv.ui.search;

import com.aethertv.domain.usecase.SearchChannelsUseCase;
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
public final class SearchViewModel_Factory implements Factory<SearchViewModel> {
  private final Provider<SearchChannelsUseCase> searchChannelsUseCaseProvider;

  public SearchViewModel_Factory(Provider<SearchChannelsUseCase> searchChannelsUseCaseProvider) {
    this.searchChannelsUseCaseProvider = searchChannelsUseCaseProvider;
  }

  @Override
  public SearchViewModel get() {
    return newInstance(searchChannelsUseCaseProvider.get());
  }

  public static SearchViewModel_Factory create(
      javax.inject.Provider<SearchChannelsUseCase> searchChannelsUseCaseProvider) {
    return new SearchViewModel_Factory(Providers.asDaggerProvider(searchChannelsUseCaseProvider));
  }

  public static SearchViewModel_Factory create(
      Provider<SearchChannelsUseCase> searchChannelsUseCaseProvider) {
    return new SearchViewModel_Factory(searchChannelsUseCaseProvider);
  }

  public static SearchViewModel newInstance(SearchChannelsUseCase searchChannelsUseCase) {
    return new SearchViewModel(searchChannelsUseCase);
  }
}
