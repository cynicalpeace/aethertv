package com.aethertv.ui.guide;

import com.aethertv.domain.usecase.GetEpgUseCase;
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

  public GuideViewModel_Factory(Provider<GetEpgUseCase> getEpgUseCaseProvider) {
    this.getEpgUseCaseProvider = getEpgUseCaseProvider;
  }

  @Override
  public GuideViewModel get() {
    return newInstance(getEpgUseCaseProvider.get());
  }

  public static GuideViewModel_Factory create(
      javax.inject.Provider<GetEpgUseCase> getEpgUseCaseProvider) {
    return new GuideViewModel_Factory(Providers.asDaggerProvider(getEpgUseCaseProvider));
  }

  public static GuideViewModel_Factory create(Provider<GetEpgUseCase> getEpgUseCaseProvider) {
    return new GuideViewModel_Factory(getEpgUseCaseProvider);
  }

  public static GuideViewModel newInstance(GetEpgUseCase getEpgUseCase) {
    return new GuideViewModel(getEpgUseCase);
  }
}
