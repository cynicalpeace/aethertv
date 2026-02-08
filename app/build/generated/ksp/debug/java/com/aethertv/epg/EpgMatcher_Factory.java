package com.aethertv.epg;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class EpgMatcher_Factory implements Factory<EpgMatcher> {
  @Override
  public EpgMatcher get() {
    return newInstance();
  }

  public static EpgMatcher_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static EpgMatcher newInstance() {
    return new EpgMatcher();
  }

  private static final class InstanceHolder {
    static final EpgMatcher_Factory INSTANCE = new EpgMatcher_Factory();
  }
}
