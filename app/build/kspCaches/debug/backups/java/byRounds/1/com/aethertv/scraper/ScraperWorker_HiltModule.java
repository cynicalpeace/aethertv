package com.aethertv.scraper;

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
    topLevelClass = ScraperWorker.class
)
public interface ScraperWorker_HiltModule {
  @Binds
  @IntoMap
  @StringKey("com.aethertv.scraper.ScraperWorker")
  WorkerAssistedFactory<? extends ListenableWorker> bind(ScraperWorker_AssistedFactory factory);
}
