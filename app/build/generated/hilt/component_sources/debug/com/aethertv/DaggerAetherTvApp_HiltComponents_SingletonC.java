package com.aethertv;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.view.View;
import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import androidx.fragment.app.Fragment;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.hilt.work.WorkerAssistedFactory;
import androidx.hilt.work.WorkerFactoryModule_ProvideFactoryFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;
import com.aethertv.data.local.AetherTvDatabase;
import com.aethertv.data.local.ChannelDao;
import com.aethertv.data.local.EpgDao;
import com.aethertv.data.local.FavoriteDao;
import com.aethertv.data.preferences.SettingsDataStore;
import com.aethertv.data.remote.AceStreamEngineClient;
import com.aethertv.data.repository.ChannelRepositoryImpl;
import com.aethertv.data.repository.EpgRepositoryImpl;
import com.aethertv.data.repository.UpdateRepository;
import com.aethertv.di.AppModule_ProvidePreferencesDataStoreFactory;
import com.aethertv.di.AppModule_ProvideSettingsDataStoreFactory;
import com.aethertv.di.AppModule_ProvideUpdateRepositoryFactory;
import com.aethertv.di.DatabaseModule_ProvideChannelDaoFactory;
import com.aethertv.di.DatabaseModule_ProvideDatabaseFactory;
import com.aethertv.di.DatabaseModule_ProvideEpgDaoFactory;
import com.aethertv.di.DatabaseModule_ProvideFavoriteDaoFactory;
import com.aethertv.di.NetworkModule_ProvideAceStreamEngineClientFactory;
import com.aethertv.di.NetworkModule_ProvideHttpClientFactory;
import com.aethertv.di.NetworkModule_ProvideJsonFactory;
import com.aethertv.di.PlayerModule_ProvideExoPlayerFactory;
import com.aethertv.domain.usecase.GetChannelsUseCase;
import com.aethertv.domain.usecase.GetEpgUseCase;
import com.aethertv.domain.usecase.RefreshChannelsUseCase;
import com.aethertv.domain.usecase.SearchChannelsUseCase;
import com.aethertv.epg.EpgRefreshWorker;
import com.aethertv.epg.EpgRefreshWorker_AssistedFactory;
import com.aethertv.epg.XmltvParser;
import com.aethertv.scraper.ScraperWorker;
import com.aethertv.scraper.ScraperWorker_AssistedFactory;
import com.aethertv.ui.MainActivity;
import com.aethertv.ui.guide.GuideViewModel;
import com.aethertv.ui.guide.GuideViewModel_HiltModules;
import com.aethertv.ui.guide.GuideViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.aethertv.ui.guide.GuideViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.aethertv.ui.home.HomeViewModel;
import com.aethertv.ui.home.HomeViewModel_HiltModules;
import com.aethertv.ui.home.HomeViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.aethertv.ui.home.HomeViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.aethertv.ui.player.PlayerViewModel;
import com.aethertv.ui.player.PlayerViewModel_HiltModules;
import com.aethertv.ui.player.PlayerViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.aethertv.ui.player.PlayerViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.aethertv.ui.search.SearchViewModel;
import com.aethertv.ui.search.SearchViewModel_HiltModules;
import com.aethertv.ui.search.SearchViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.aethertv.ui.search.SearchViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.aethertv.ui.settings.SettingsViewModel;
import com.aethertv.ui.settings.SettingsViewModel_HiltModules;
import com.aethertv.ui.settings.SettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.aethertv.ui.settings.SettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.SingleCheck;
import io.ktor.client.HttpClient;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import kotlinx.serialization.json.Json;

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
public final class DaggerAetherTvApp_HiltComponents_SingletonC {
  private DaggerAetherTvApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public AetherTvApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements AetherTvApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public AetherTvApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements AetherTvApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public AetherTvApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements AetherTvApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public AetherTvApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements AetherTvApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public AetherTvApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements AetherTvApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public AetherTvApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements AetherTvApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public AetherTvApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements AetherTvApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public AetherTvApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends AetherTvApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends AetherTvApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends AetherTvApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends AetherTvApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(ImmutableMap.<String, Boolean>of(GuideViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, GuideViewModel_HiltModules.KeyModule.provide(), HomeViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, HomeViewModel_HiltModules.KeyModule.provide(), PlayerViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, PlayerViewModel_HiltModules.KeyModule.provide(), SearchViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SearchViewModel_HiltModules.KeyModule.provide(), SettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SettingsViewModel_HiltModules.KeyModule.provide()));
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }
  }

  private static final class ViewModelCImpl extends AetherTvApp_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<GuideViewModel> guideViewModelProvider;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<PlayerViewModel> playerViewModelProvider;

    private Provider<SearchViewModel> searchViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    private GetEpgUseCase getEpgUseCase() {
      return new GetEpgUseCase(singletonCImpl.epgRepositoryImplProvider.get());
    }

    private GetChannelsUseCase getChannelsUseCase() {
      return new GetChannelsUseCase(singletonCImpl.channelRepositoryImplProvider.get());
    }

    private SearchChannelsUseCase searchChannelsUseCase() {
      return new SearchChannelsUseCase(singletonCImpl.channelRepositoryImplProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.guideViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.playerViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.searchViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(ImmutableMap.<String, javax.inject.Provider<ViewModel>>of(GuideViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) guideViewModelProvider), HomeViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) homeViewModelProvider), PlayerViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) playerViewModelProvider), SearchViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) searchViewModelProvider), SettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) settingsViewModelProvider)));
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return ImmutableMap.<Class<?>, Object>of();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.aethertv.ui.guide.GuideViewModel 
          return (T) new GuideViewModel(viewModelCImpl.getEpgUseCase());

          case 1: // com.aethertv.ui.home.HomeViewModel 
          return (T) new HomeViewModel(viewModelCImpl.getChannelsUseCase(), singletonCImpl.channelRepositoryImplProvider.get(), singletonCImpl.provideSettingsDataStoreProvider.get(), singletonCImpl.provideAceStreamEngineClientProvider.get());

          case 2: // com.aethertv.ui.player.PlayerViewModel 
          return (T) new PlayerViewModel(singletonCImpl.provideExoPlayerProvider.get(), singletonCImpl.provideAceStreamEngineClientProvider.get(), singletonCImpl.channelRepositoryImplProvider.get());

          case 3: // com.aethertv.ui.search.SearchViewModel 
          return (T) new SearchViewModel(viewModelCImpl.searchChannelsUseCase());

          case 4: // com.aethertv.ui.settings.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.provideUpdateRepositoryProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends AetherTvApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends AetherTvApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends AetherTvApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<Json> provideJsonProvider;

    private Provider<HttpClient> provideHttpClientProvider;

    private Provider<XmltvParser> xmltvParserProvider;

    private Provider<AetherTvDatabase> provideDatabaseProvider;

    private Provider<EpgRepositoryImpl> epgRepositoryImplProvider;

    private Provider<DataStore<Preferences>> providePreferencesDataStoreProvider;

    private Provider<SettingsDataStore> provideSettingsDataStoreProvider;

    private Provider<EpgRefreshWorker_AssistedFactory> epgRefreshWorker_AssistedFactoryProvider;

    private Provider<AceStreamEngineClient> provideAceStreamEngineClientProvider;

    private Provider<ChannelRepositoryImpl> channelRepositoryImplProvider;

    private Provider<ScraperWorker_AssistedFactory> scraperWorker_AssistedFactoryProvider;

    private Provider<ExoPlayer> provideExoPlayerProvider;

    private Provider<UpdateRepository> provideUpdateRepositoryProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private EpgDao epgDao() {
      return DatabaseModule_ProvideEpgDaoFactory.provideEpgDao(provideDatabaseProvider.get());
    }

    private ChannelDao channelDao() {
      return DatabaseModule_ProvideChannelDaoFactory.provideChannelDao(provideDatabaseProvider.get());
    }

    private FavoriteDao favoriteDao() {
      return DatabaseModule_ProvideFavoriteDaoFactory.provideFavoriteDao(provideDatabaseProvider.get());
    }

    private RefreshChannelsUseCase refreshChannelsUseCase() {
      return new RefreshChannelsUseCase(provideAceStreamEngineClientProvider.get(), channelRepositoryImplProvider.get(), provideSettingsDataStoreProvider.get());
    }

    private Map<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>> mapOfStringAndProviderOfWorkerAssistedFactoryOf(
        ) {
      return ImmutableMap.<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>>of("com.aethertv.epg.EpgRefreshWorker", ((Provider) epgRefreshWorker_AssistedFactoryProvider), "com.aethertv.scraper.ScraperWorker", ((Provider) scraperWorker_AssistedFactoryProvider));
    }

    private HiltWorkerFactory hiltWorkerFactory() {
      return WorkerFactoryModule_ProvideFactoryFactory.provideFactory(mapOfStringAndProviderOfWorkerAssistedFactoryOf());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideJsonProvider = DoubleCheck.provider(new SwitchingProvider<Json>(singletonCImpl, 2));
      this.provideHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<HttpClient>(singletonCImpl, 1));
      this.xmltvParserProvider = DoubleCheck.provider(new SwitchingProvider<XmltvParser>(singletonCImpl, 3));
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AetherTvDatabase>(singletonCImpl, 5));
      this.epgRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<EpgRepositoryImpl>(singletonCImpl, 4));
      this.providePreferencesDataStoreProvider = DoubleCheck.provider(new SwitchingProvider<DataStore<Preferences>>(singletonCImpl, 7));
      this.provideSettingsDataStoreProvider = DoubleCheck.provider(new SwitchingProvider<SettingsDataStore>(singletonCImpl, 6));
      this.epgRefreshWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<EpgRefreshWorker_AssistedFactory>(singletonCImpl, 0));
      this.provideAceStreamEngineClientProvider = DoubleCheck.provider(new SwitchingProvider<AceStreamEngineClient>(singletonCImpl, 9));
      this.channelRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<ChannelRepositoryImpl>(singletonCImpl, 10));
      this.scraperWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<ScraperWorker_AssistedFactory>(singletonCImpl, 8));
      this.provideExoPlayerProvider = DoubleCheck.provider(new SwitchingProvider<ExoPlayer>(singletonCImpl, 11));
      this.provideUpdateRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<UpdateRepository>(singletonCImpl, 12));
    }

    @Override
    public void injectAetherTvApp(AetherTvApp aetherTvApp) {
      injectAetherTvApp2(aetherTvApp);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return ImmutableSet.<Boolean>of();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private AetherTvApp injectAetherTvApp2(AetherTvApp instance) {
      AetherTvApp_MembersInjector.injectWorkerFactory(instance, hiltWorkerFactory());
      return instance;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.aethertv.epg.EpgRefreshWorker_AssistedFactory 
          return (T) new EpgRefreshWorker_AssistedFactory() {
            @Override
            public EpgRefreshWorker create(Context appContext, WorkerParameters workerParams) {
              return new EpgRefreshWorker(appContext, workerParams, singletonCImpl.provideHttpClientProvider.get(), singletonCImpl.xmltvParserProvider.get(), singletonCImpl.epgRepositoryImplProvider.get(), singletonCImpl.provideSettingsDataStoreProvider.get());
            }
          };

          case 1: // io.ktor.client.HttpClient 
          return (T) NetworkModule_ProvideHttpClientFactory.provideHttpClient(singletonCImpl.provideJsonProvider.get());

          case 2: // kotlinx.serialization.json.Json 
          return (T) NetworkModule_ProvideJsonFactory.provideJson();

          case 3: // com.aethertv.epg.XmltvParser 
          return (T) new XmltvParser();

          case 4: // com.aethertv.data.repository.EpgRepositoryImpl 
          return (T) new EpgRepositoryImpl(singletonCImpl.epgDao());

          case 5: // com.aethertv.data.local.AetherTvDatabase 
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 6: // com.aethertv.data.preferences.SettingsDataStore 
          return (T) AppModule_ProvideSettingsDataStoreFactory.provideSettingsDataStore(singletonCImpl.providePreferencesDataStoreProvider.get());

          case 7: // androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> 
          return (T) AppModule_ProvidePreferencesDataStoreFactory.providePreferencesDataStore(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 8: // com.aethertv.scraper.ScraperWorker_AssistedFactory 
          return (T) new ScraperWorker_AssistedFactory() {
            @Override
            public ScraperWorker create(Context appContext2, WorkerParameters workerParams2) {
              return new ScraperWorker(appContext2, workerParams2, singletonCImpl.refreshChannelsUseCase());
            }
          };

          case 9: // com.aethertv.data.remote.AceStreamEngineClient 
          return (T) NetworkModule_ProvideAceStreamEngineClientFactory.provideAceStreamEngineClient(singletonCImpl.provideHttpClientProvider.get());

          case 10: // com.aethertv.data.repository.ChannelRepositoryImpl 
          return (T) new ChannelRepositoryImpl(singletonCImpl.channelDao(), singletonCImpl.favoriteDao(), singletonCImpl.provideJsonProvider.get());

          case 11: // androidx.media3.exoplayer.ExoPlayer 
          return (T) PlayerModule_ProvideExoPlayerFactory.provideExoPlayer(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 12: // com.aethertv.data.repository.UpdateRepository 
          return (T) AppModule_ProvideUpdateRepositoryFactory.provideUpdateRepository(singletonCImpl.provideHttpClientProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
