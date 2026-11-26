#!/usr/bin/env bash
set -euo pipefail
SERIAL="${1:-192.168.0.100:5555}"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
if [ ! -f "$ROOT/apk/com.amazon.vizzini.apk" ]; then
    echo "ERROR: apk/com.amazon.vizzini.apk is not included in this repository." >&2
    echo "Provide your own copy of the Amazon Alexa APK for --restore." >&2
    exit 1
fi
adb -s "$SERIAL" push "$ROOT/apk/com.amazon.vizzini.apk" /data/local/tmp/com.amazon.vizzini.apk
cd "$ROOT/ghostlock"
python3 scripts/gl_root.py --serial "$SERIAL" --cmd "pm install -r -d /data/local/tmp/com.amazon.vizzini.apk; $(cat "$ROOT/state/restore_cmd.txt")"
