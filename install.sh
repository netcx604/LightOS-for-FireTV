#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
ADB="${ADB:-adb}"
SERIAL=""
MODE="auto"
ASSUME_YES=0
NO_REBOOT=0

usage() {
    cat <<'USAGE'
LightOS Installer for Fire TV

Usage:
  ./install.sh [OPTIONS] [SERIAL]

Options:
  --serial SERIAL   ADB serial (e.g. 192.168.0.100:5555)
  --check           Check device/build only, change nothing
  --safe            Install only the launcher APK (no root, no debloat)
  --full            Force full LightOS mode (AFTT 5.2.9.5 only)
  --restore         Restore the Amazon UI and all services
  --yes, -y         Continue without confirmation
  --no-reboot       Do not reboot after installation
  -h, --help        Show this help

Without options:
  - AFTT/tank on Fire OS 5.2.9.5 (688806320/688805020): full mode
  - all other devices: safe mode (launcher APK only)

Examples:
  ./install.sh 192.168.0.100:5555
  ./install.sh --check 192.168.0.100:5555
  ./install.sh --safe 192.168.0.100:5555
  ./install.sh --restore 192.168.0.100:5555
USAGE
}

while [ $# -gt 0 ]; do
    case "$1" in
        --serial)
            SERIAL="${2:-}"
            [ -n "$SERIAL" ] || { echo "Missing value for --serial" >&2; exit 1; }
            shift 2
            ;;
        --check) MODE="check"; shift ;;
        --safe) MODE="safe"; shift ;;
        --full) MODE="full"; shift ;;
        --restore) MODE="restore"; shift ;;
        --yes|-y) ASSUME_YES=1; shift ;;
        --no-reboot) NO_REBOOT=1; shift ;;
        -h|--help) usage; exit 0 ;;
        -*)
            echo "Unknown option: $1" >&2
            usage
            exit 1
            ;;
        *)
            if [ -z "$SERIAL" ]; then
                SERIAL="$1"
                shift
            else
                echo "Unknown argument: $1" >&2
                usage
                exit 1
            fi
            ;;
    esac
done

if ! command -v "$ADB" >/dev/null 2>&1; then
    echo "ERROR: adb not found. Install the Android Platform Tools." >&2
    exit 1
fi

if [ -z "$SERIAL" ]; then
    SERIAL="$($ADB devices | awk 'NR > 1 && $2 == "device" { print $1; exit }')"
fi

if [ -z "$SERIAL" ]; then
    echo "ERROR: No authorized ADB device found." >&2
    "$ADB" devices
    exit 1
fi

if [[ "$SERIAL" == *:* ]]; then
    if ! "$ADB" -s "$SERIAL" get-state >/dev/null 2>&1; then
        echo "Connecting to $SERIAL ..."
        "$ADB" connect "$SERIAL" >/dev/null 2>&1 || true
        sleep 2
    fi
fi

STATE="$("$ADB" -s "$SERIAL" get-state 2>/dev/null || true)"
if [ "$STATE" != "device" ]; then
    echo "ERROR: Device $SERIAL is not ready (status: ${STATE:-unknown})." >&2
    echo "Allow USB/network debugging on the Fire TV and confirm the dialog." >&2
    "$ADB" devices
    exit 1
fi

getprop_value() {
    "$ADB" -s "$SERIAL" shell getprop "$1" 2>/dev/null | tr -d '\r'
}

MODEL="$(getprop_value ro.product.model)"
DEVICE="$(getprop_value ro.product.device)"
FIREOS="$(getprop_value ro.build.version.name)"
BUILD="$(getprop_value ro.build.version.incremental)"
RELEASE="$(getprop_value ro.build.version.release)"
KERNEL="$("$ADB" -s "$SERIAL" shell cat /proc/version 2>/dev/null | tr -d '\r')"

SUPPORTED=0
if [ "$DEVICE" = "tank" ] && { [ "$BUILD" = "288.6.8.8_user_688806320" ] || [ "$BUILD" = "288.6.8.8_user_688805020" ]; }; then
    SUPPORTED=1
fi

echo "=============================================="
echo " LightOS Installer"
echo "=============================================="
echo "Serial: $SERIAL"
echo "Model:       $MODEL ($DEVICE)"
echo "Fire OS:      $FIREOS"
echo "Build:        $BUILD"
echo "Android:      $RELEASE"
echo "Kernel:       $KERNEL"
if [ "$MODE" = "check" ]; then
    echo "Mode:         CHECK (no changes)"
elif [ "$MODE" = "safe" ]; then
    echo "Mode:         SAFE (launcher APK only)"
elif [ "$MODE" = "restore" ]; then
    echo "Mode:         RESTORE (Amazon UI)"
elif [ "$SUPPORTED" -eq 1 ]; then
    echo "Mode:         FULL (LightOS + root + debloat)"
else
    echo "Mode:         SAFE (launcher APK only)"
fi
echo "=============================================="

if [ "$MODE" = "check" ]; then
    if [ "$SUPPORTED" -eq 1 ]; then
        echo "Result: this device supports full LightOS mode."
    else
        echo "Result: safe mode only (launcher APK)."
        echo "Full mode is intended exclusively for AFTT/tank on Fire OS 5.2.9.5"
        echo "(build 688806320 or 688805020)."
    fi
    exit 0
fi

if [ "$MODE" = "restore" ]; then
    if [ "$SUPPORTED" -ne 1 ]; then
        echo "WARNING: restore is intended for AFTT/5.2.9.5. Continue at your own risk." >&2
    fi
    "$ROOT/scripts/restore_amazon.sh" "$SERIAL"
    echo
    echo "Now choose Amazon Home as the default on the Fire TV."
    if [ "$ASSUME_YES" -eq 0 ]; then
        read -r -p "Press Enter when Amazon Home is selected ... "
    fi
    if [ "$NO_REBOOT" -eq 0 ]; then
        "$ADB" -s "$SERIAL" reboot
    fi
    exit 0
fi

if [ "$MODE" = "safe" ] || { [ "$MODE" = "auto" ] && [ "$SUPPORTED" -ne 1 ]; }; then
    echo
    echo "Installing launcher APK (safe mode, no root, no debloat) ..."
    "$ROOT/scripts/install_launcher.sh" "$SERIAL"
    echo
    echo "Done. Open the app list on the Fire TV and start "LightOS"."
    echo "The Amazon home screen remains unchanged."
    echo
    echo "Note: full home replacement, debloat and OTA blocking are only available on"
    echo "AFTT/tank with Fire OS 5.2.9.5 (688806320/688805020)."
    exit 0
fi

if [ "$MODE" = "full" ] && [ "$SUPPORTED" -ne 1 ]; then
    echo "ERROR: --full is not supported on this device/build." >&2
    echo "GhostLock and the debloat list are build-specific." >&2
    echo "Use --safe for the launcher APK." >&2
    exit 1
fi

if [ "$SUPPORTED" -ne 1 ]; then
    echo "ERROR: full mode is not possible on this device." >&2
    exit 1
fi

if ! command -v python3 >/dev/null 2>&1; then
    echo "ERROR: python3 is required for GhostLock." >&2
    exit 1
fi

if [ ! -f "$ROOT/apk/LightOS-4.2.apk" ]; then
    echo "ERROR: APK missing: $ROOT/apk/LightOS-4.2.apk" >&2
    exit 1
fi
if [ ! -x "$ROOT/scripts/root_test.sh" ]; then
    echo "ERROR: root scripts are missing or not executable." >&2
    exit 1
fi

echo
echo "[1/5] Testing root ..."
"$ROOT/scripts/root_test.sh" "$SERIAL"

echo
echo "[2/5] Installing launcher ..."
"$ROOT/scripts/install_launcher.sh" "$SERIAL"

echo
echo "[3/5] Disabling PIN, Amazon Home, HomeStarter and OTA ..."
"$ROOT/scripts/apply_setup.sh" "$SERIAL"

echo
echo "[4/5] Applying Amazon debloat ..."
"$ROOT/scripts/apply_debloat.sh" "$SERIAL"

echo
echo "[5/5] Applying minimal profile ..."
"$ROOT/scripts/apply_minimal.sh" "$SERIAL"

echo
echo "Opening the Home chooser ..."
"$ADB" -s "$SERIAL" shell am start -a android.intent.action.MAIN -c android.intent.category.HOME >/dev/null 2>&1 || true

echo
echo "=============================================="
echo " On the Fire TV now:"
echo "   Press Home -> "LightOS" -> "Always""
echo "=============================================="
if [ "$ASSUME_YES" -eq 0 ]; then
    read -r -p "Press Enter when "LightOS" is selected as the home screen ... "
fi

if [ "$NO_REBOOT" -eq 0 ]; then
    echo "Rebooting Fire TV ..."
    "$ADB" -s "$SERIAL" reboot || true
    echo "After the reboot, "LightOS" starts automatically."
else
    echo "Reboot skipped (--no-reboot)."
fi

echo
echo "LightOS installation complete."
echo "If you have problems: read README.md in the LightOS folder."
