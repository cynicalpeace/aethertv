package com.aethertv.epg;

import androidx.hilt.work.WorkerAssistedFactory;
import androidx.work.ListenableWorker;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import javax.annotation.processing.Generated;

@Generated("androidx.hilt.AndroidXHiltProcessor")
@Module
@InstallIn(SingletonComponent.class)
@OriginatingElement(
    topLevelClass = EpgRefreshWorker.class
)
public interface EpgRefreshWorker_HiltModule {
  @Binds
  @IntoMap
  @StringKey("com.aethertv.epg.EpgRefreshWorker")
  WorkerAssistedFactory<? extends ListenableWorker> bind(EpgRefreshWorker_AssistedFactory factory);
}
