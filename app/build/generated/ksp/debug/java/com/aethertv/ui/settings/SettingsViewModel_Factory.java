package com.aethertv.ui.settings;

import com.aethertv.data.local.ChannelDao;
import com.aethertv.data.local.WatchHistoryDao;
import com.aethertv.data.repository.UpdateRepository;
import com.aethertv.verification.StreamVerifier;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<UpdateRepository> updateRepositoryProvider;

  private final Provider<WatchHistoryDao> watchHistoryDaoProvider;

  private final Provider<StreamVerifier> streamVerifierProvider;

  private final Provider<ChannelDao> channelDaoProvider;

  public SettingsViewModel_Factory(Provider<UpdateRepository> updateRepositoryProvider,
      Provider<WatchHistoryDao> watchHistoryDaoProvider,
      Provider<StreamVerifier> streamVerifierProvider, Provider<ChannelDao> channelDaoProvider) {
    this.updateRepositoryProvider = updateRepositoryProvider;
    this.watchHistoryDaoProvider = watchHistoryDaoProvider;
    this.streamVerifierProvider = streamVerifierProvider;
    this.channelDaoProvider = channelDaoProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(updateRepositoryProvider.get(), watchHistoryDaoProvider.get(), streamVerifierProvider.get(), channelDaoProvider.get());
  }

  public static SettingsViewModel_Factory create(
      javax.inject.Provider<UpdateRepository> updateRepositoryProvider,
      javax.inject.Provider<WatchHistoryDao> watchHistoryDaoProvider,
      javax.inject.Provider<StreamVerifier> streamVerifierProvider,
      javax.inject.Provider<ChannelDao> channelDaoProvider) {
    return new SettingsViewModel_Factory(Providers.asDaggerProvider(updateRepositoryProvider), Providers.asDaggerProvider(watchHistoryDaoProvider), Providers.asDaggerProvider(streamVerifierProvider), Providers.asDaggerProvider(channelDaoProvider));
  }

  public static SettingsViewModel_Factory create(
      Provider<UpdateRepository> updateRepositoryProvider,
      Provider<WatchHistoryDao> watchHistoryDaoProvider,
      Provider<StreamVerifier> streamVerifierProvider, Provider<ChannelDao> channelDaoProvider) {
    return new SettingsViewModel_Factory(updateRepositoryProvider, watchHistoryDaoProvider, streamVerifierProvider, channelDaoProvider);
  }

  public static SettingsViewModel newInstance(UpdateRepository updateRepository,
      WatchHistoryDao watchHistoryDao, StreamVerifier streamVerifier, ChannelDao channelDao) {
    return new SettingsViewModel(updateRepository, watchHistoryDao, streamVerifier, channelDao);
  }
}
