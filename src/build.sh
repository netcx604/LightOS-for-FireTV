#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
SDK="${ANDROID_HOME:-$HOME/Android/Sdk}"
BT="$SDK/build-tools/34.0.0"
AJAR="$SDK/platforms/android-36/android.jar"
OUT="$ROOT/build"
APK="$ROOT/LightOS.apk"

if [ ! -x "$BT/aapt" ] || [ ! -x "$BT/d8" ] || [ ! -x "$BT/apksigner" ]; then
    echo "Android build tools not found under $BT" >&2
    exit 1
fi
if [ ! -f "$AJAR" ]; then
    echo "android.jar not found: $AJAR" >&2
    exit 1
fi

rm -rf "$OUT"
mkdir -p "$OUT/classes" "$OUT/dex" "$OUT/apk"
rm -f "$APK"

echo "[1/5] Packaging (manifest + resources)"
"$BT/aapt" package -f \
    -M "$ROOT/AndroidManifest.xml" \
    -S "$ROOT/res" \
    -I "$AJAR" \
    -F "$OUT/apk/unsigned.apk"

echo "[2/5] Compiling Java"
SOURCES="$(find "$ROOT/src" -name '*.java' -print)"
javac -source 8 -target 8 -encoding UTF-8 \
    -classpath "$AJAR" \
    -d "$OUT/classes" \
    $SOURCES

echo "[3/5] Converting to DEX"
CLASSES="$(find "$OUT/classes" -name '*.class' -print)"
"$BT/d8" --min-api 22 --lib "$AJAR" --output "$OUT/dex" $CLASSES

echo "[4/5] Adding classes.dex and aligning"
(cd "$OUT/dex" && zip -q "$OUT/apk/unsigned.apk" classes.dex)
"$BT/zipalign" -f 4 "$OUT/apk/unsigned.apk" "$OUT/apk/aligned.apk"

echo "[5/5] Signing"
if [ ! -f "$ROOT/keystore.jks" ]; then
    keytool -genkeypair -v \
        -keystore "$ROOT/keystore.jks" \
        -storetype JKS \
        -storepass android \
        -keypass android \
        -alias androiddebugkey \
        -keyalg RSA \
        -keysize 2048 \
        -validity 10000 \
        -dname "CN=Android Debug,O=Android,C=US"
fi
"$BT/apksigner" sign \
    --ks "$ROOT/keystore.jks" \
    --ks-pass pass:android \
    --key-pass pass:android \
    --ks-key-alias androiddebugkey \
    --out "$APK" \
    "$OUT/apk/aligned.apk"
"$BT/apksigner" verify --verbose --print-certs "$APK"

echo
ls -lh "$APK"
echo "Done: $APK"
