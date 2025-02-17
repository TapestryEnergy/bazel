// Copyright 2019 The Bazel Authors. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.google.devtools.build.lib.rules.android;

import static com.google.devtools.build.lib.rules.android.AndroidStarlarkData.fromNoneable;

import com.google.common.collect.Maps;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.collect.nestedset.Depset;
import com.google.devtools.build.lib.collect.nestedset.NestedSet;
import com.google.devtools.build.lib.concurrent.ThreadSafety.Immutable;
import com.google.devtools.build.lib.packages.BuiltinProvider;
import com.google.devtools.build.lib.packages.NativeInfo;
import com.google.devtools.build.lib.starlarkbuildapi.android.AndroidApplicationResourceInfoApi;
import javax.annotation.Nullable;
import net.starlark.java.eval.Dict;
import net.starlark.java.eval.EvalException;

/** A provider for Android resource APKs (".ap_") and related info. */
@Immutable
public class AndroidApplicationResourceInfo extends NativeInfo
    implements AndroidApplicationResourceInfoApi<Artifact> {

  /** Singleton instance of the provider type for {@link AndroidApplicationResourceInfo}. */
  public static final AndroidApplicationResourceInfoProvider PROVIDER =
      new AndroidApplicationResourceInfoProvider();

  private final Artifact resourceApk;
  private final Artifact resourceJavaSrcJar;
  private final Artifact resourceJavaClassJar;
  private final Artifact manifest;
  private final Artifact resourceProguardConfig;
  private final Artifact mainDexProguardConfig;
  private final Artifact rTxt;
  private final Artifact resourcesZip;
  private final Artifact databindingLayoutInfoZip;
  private final Artifact buildStampJar;
  private final boolean shouldCompileJavaSrcs;
  private final NativeLibs nativeLibs;
  private final NestedSet<Artifact> transitiveNativeLibs;

  AndroidApplicationResourceInfo(
      Artifact resourceApk,
      Artifact resourceJavaSrcJar,
      Artifact resourceJavaClassJar,
      Artifact manifest,
      Artifact resourceProguardConfig,
      Artifact mainDexProguardConfig,
      Artifact rTxt,
      Artifact resourcesZip,
      Artifact databindingLayoutInfoZip,
      Artifact buildStampJar,
      boolean shouldCompileJavaSrcs,
      NativeLibs nativeLibs,
      NestedSet<Artifact> transitiveNativeLibs) {
    this.resourceApk = resourceApk;
    this.resourceJavaSrcJar = resourceJavaSrcJar;
    this.resourceJavaClassJar = resourceJavaClassJar;
    this.manifest = manifest;
    this.resourceProguardConfig = resourceProguardConfig;
    this.mainDexProguardConfig = mainDexProguardConfig;
    this.rTxt = rTxt;
    this.resourcesZip = resourcesZip;
    this.databindingLayoutInfoZip = databindingLayoutInfoZip;
    this.buildStampJar = buildStampJar;
    this.shouldCompileJavaSrcs = shouldCompileJavaSrcs;
    this.nativeLibs = nativeLibs;
    this.transitiveNativeLibs = transitiveNativeLibs;
  }

  @Override
  public AndroidApplicationResourceInfoProvider getProvider() {
    return PROVIDER;
  }

  @Override
  public Artifact getResourceApk() {
    return resourceApk;
  }

  @Override
  public Artifact getResourceJavaSrcJar() {
    return resourceJavaSrcJar;
  }

  @Override
  public Artifact getResourceJavaClassJar() {
    return resourceJavaClassJar;
  }

  @Override
  public Artifact getManifest() {
    return manifest;
  }

  @Override
  public Artifact getResourceProguardConfig() {
    return resourceProguardConfig;
  }

  @Override
  public Artifact getMainDexProguardConfig() {
    return mainDexProguardConfig;
  }

  @Override
  public Artifact getRTxt() {
    return rTxt;
  }

  @Override
  public Artifact getResourcesZip() {
    return resourcesZip;
  }

  @Override
  public Artifact getDatabindingLayoutInfoZip() {
    return databindingLayoutInfoZip;
  }

  @Override
  public Artifact getBuildStampJar() {
    return buildStampJar;
  }

  /**
   * A signal that indicates whether the android_binary rule should compile its Java sources in
   * android_binary.srcs. When false, android_binary.application_resources will provide a JavaInfo
   * that contains the compiled sources of the android_binary target. This step allows
   * android_binary Java compilation to be offloaded to a Starlark rule.
   */
  @Override
  public boolean shouldCompileJavaSrcs() {
    return shouldCompileJavaSrcs;
  }

  @Nullable
  @Override
  public Dict<String, Depset> getNativeLibsStarlark() {
    if (nativeLibs == null) {
      return null;
    }
    return Dict.immutableCopyOf(
        Maps.transformValues(nativeLibs.getMap(), set -> Depset.of(Artifact.TYPE, set)));
  }

  @Nullable
  @Override
  public Artifact getNativeLibsNameStarlark() {
    if (nativeLibs == null) {
      return null;
    }
    return nativeLibs.getName();
  }

  @Nullable
  @Override
  public Depset getTransitiveNativeLibsStarlark() {
    if (transitiveNativeLibs == null) {
      return null;
    }
    return Depset.of(Artifact.TYPE, transitiveNativeLibs);
  }

  @Nullable
  public NativeLibs getNativeLibs() {
    return nativeLibs;
  }

  @Nullable
  public NestedSet<Artifact> getTransitiveNativeLibs() {
    return transitiveNativeLibs;
  }

  /** Provider for {@link AndroidApplicationResourceInfo}. */
  public static class AndroidApplicationResourceInfoProvider
      extends BuiltinProvider<AndroidApplicationResourceInfo>
      implements AndroidApplicationResourceInfoApiProvider<Artifact> {

    private AndroidApplicationResourceInfoProvider() {
      super(AndroidApplicationResourceInfoApi.NAME, AndroidApplicationResourceInfo.class);
    }

    @Override
    public AndroidApplicationResourceInfoApi<Artifact> createInfo(
        Object resourceApk,
        Object resourceJavaSrcJar,
        Object resourceJavaClassJar,
        Artifact manifest,
        Object resourceProguardConfig,
        Object mainDexProguardConfig,
        Object rTxt,
        Object resourcesZip,
        Object databindingLayoutInfoZip,
        Object buildStampJar,
        boolean shouldCompileJavaSrcs,
        Object nativeLibs,
        Object transitiveNativeLibs)
        throws EvalException {

      return new AndroidApplicationResourceInfo(
          fromNoneable(resourceApk, Artifact.class),
          fromNoneable(resourceJavaSrcJar, Artifact.class),
          fromNoneable(resourceJavaClassJar, Artifact.class),
          manifest,
          fromNoneable(resourceProguardConfig, Artifact.class),
          fromNoneable(mainDexProguardConfig, Artifact.class),
          fromNoneable(rTxt, Artifact.class),
          fromNoneable(resourcesZip, Artifact.class),
          fromNoneable(databindingLayoutInfoZip, Artifact.class),
          fromNoneable(buildStampJar, Artifact.class),
          shouldCompileJavaSrcs,
          AndroidStarlarkData.getNativeLibs(nativeLibs),
          AndroidStarlarkData.fromNoneableDepset(transitiveNativeLibs, "transitive_native_libs"));
    }
  }
}
