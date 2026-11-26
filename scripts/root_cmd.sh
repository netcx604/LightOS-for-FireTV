#!/usr/bin/env bash
set -euo pipefail

if [ $# -lt 2 ]; then
    echo "Usage: $0 <ADB-SERIAL> \"<command> [; command ...]\"" >&2
    echo "Example: $0 192.168.0.100:5555 \"id; getenforce\"" >&2
    exit 1
fi

SERIAL="$1"
shift
COMMAND="$*"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"

if ! command -v python3 >/dev/null 2>&1; then
    echo "ERROR: python3 is required for GhostLock." >&2
    exit 1
fi

cd "$ROOT/ghostlock"
python3 scripts/gl_root.py --serial "$SERIAL" --cmd "$COMMAND"
