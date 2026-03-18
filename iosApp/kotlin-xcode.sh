#!/bin/bash
set -euo pipefail

export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"

REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$REPO_ROOT"

case "${CONFIGURATION:-Debug}" in
  Debug*)
    BUILD_TYPE="Debug"
    BUILD_DIR_NAME="debugFramework"
    ;;
  Release*)
    BUILD_TYPE="Release"
    BUILD_DIR_NAME="releaseFramework"
    ;;
  *)
    echo "error: Unsupported Xcode CONFIGURATION='${CONFIGURATION:-}'" >&2
    exit 1
    ;;
esac

case "${SDK_NAME:-}" in
  iphoneos*)
    TARGET_DIR="iosArm64"
    TARGET_SUFFIX="IosArm64"
    ;;
  iphonesimulator*)
    if [[ "${ARCHS:-}" == *"x86_64"* ]]; then
      TARGET_DIR="iosX64"
      TARGET_SUFFIX="IosX64"
    else
      TARGET_DIR="iosSimulatorArm64"
      TARGET_SUFFIX="IosSimulatorArm64"
    fi
    ;;
  *)
    echo "error: Unsupported Xcode SDK_NAME='${SDK_NAME:-}'" >&2
    exit 1
    ;;
esac

GRADLE_TASK=":shared:link${BUILD_TYPE}Framework${TARGET_SUFFIX}"
FRAMEWORK_SRC="$REPO_ROOT/shared/build/bin/$TARGET_DIR/$BUILD_DIR_NAME/shared.framework"
FRAMEWORK_DST="${TARGET_BUILD_DIR:?}/${FRAMEWORKS_FOLDER_PATH:?}"

echo "Building Kotlin framework with $GRADLE_TASK"
./gradlew "$GRADLE_TASK"

mkdir -p "$FRAMEWORK_DST"
rsync -a --delete "$FRAMEWORK_SRC" "$FRAMEWORK_DST/"
