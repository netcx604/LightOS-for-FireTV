#!/usr/bin/env bash
set -euo pipefail
SERIAL="${1:-192.168.0.100:5555}"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
adb -s "$SERIAL" install -r "$ROOT/apk/LightOS-4.2.apk"
