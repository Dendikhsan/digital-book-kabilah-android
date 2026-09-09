#!/usr/bin/env bash
set -euo pipefail
MODE="${1:---unsigned}"

cd "$(dirname "$0")/.."

if [ "$MODE" = "--signed" ]; then
  cat > signing.gradle <<'GRADLE'
android {
    signingConfigs {
        release {
            storeFile file(System.getProperty('user.dir') + '/release-upload-key.jks')
            storePassword System.getenv('ANDROID_KEYSTORE_PASSWORD')
            keyAlias System.getenv('ANDROID_KEY_ALIAS')
            keyPassword System.getenv('ANDROID_KEY_PASSWORD')
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.release
        }
    }
}
GRADLE
  cat > /tmp/digitalbook-signing.gradle <<'GRADLE'
android {
    signingConfigs {
        release {
            storeFile file(System.getProperty('user.dir') + '/release-upload-key.jks')
            storePassword System.getenv('ANDROID_KEYSTORE_PASSWORD')
            keyAlias System.getenv('ANDROID_KEY_ALIAS')
            keyPassword System.getenv('ANDROID_KEY_PASSWORD')
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.release
        }
    }
}
GRADLE
  gradle --no-daemon -I /tmp/digitalbook-signing.gradle :app:bundleRelease
else
  gradle --no-daemon :app:bundleRelease
fi
