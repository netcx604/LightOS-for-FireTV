#!/usr/bin/env bash
set -euo pipefail
SERIAL="${1:-192.168.0.100:5555}"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT/ghostlock"
python3 scripts/gl_root.py --serial "$SERIAL" --cmd "$(cat "$ROOT/state/setup_cmd.txt")"
