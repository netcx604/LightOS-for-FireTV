# LightOS for Fire TV
(Disclaimer: Ai was Used in Creation of this Project!)
LightOS is a lean, ad-free, Apple-TV-style home screen for Amazon Fire TV
devices – with an app dock, a dynamic hero area, custom Apps/Settings views,
app reordering and a minimal service profile.

This repository contains the APK, installer, root tool, debloat/restore
commands, source code and documentation. Amazon firmware and Amazon system
APKs are **not** included for size and licensing reasons; see **Not included**
below.

Status: **2026-11-26**
App: **de.customtv.start / LightOS 4.2** (versionCode 7, English UI)

## Not included

- Amazon Fire OS 5.2.9.2 firmware archive (kept privately)
- Amazon system APK `com.amazon.vizzini.apk`
- FTVLaunchX (third-party; use the upstream project and respect its license)

---

## 1. Compatibility – read this first

| Device / system | LightOS mode |
|---|---|
| **Fire TV Stick 2nd gen (AFTT / `tank`)**, Fire OS **5.2.9.5** build **688806320** or **688805020**, kernel 3.10.54+ | **Full** (root, home replacement, debloat, minimal profile) |
| Other Fire TV models or other Fire OS versions | **Safe mode**: launcher APK only, no root, no debloat, Amazon Home remains |
| Fire OS 5.2.9.2 or older on AFTT | Update to **5.2.9.5** first; then use full mode |
| Fire OS 7/8, Fire TV Stick 4K/Cube, etc. | Safe mode only; Prime/Settings deep links may differ |

**Important:** GhostLock and the debloat list are **build-specific**. The
installer refuses full mode on unsupported devices. There is no “root for all
Fire TVs”.

---

## 2. Requirements

- Windows, Linux or macOS with **adb** (Android Platform Tools)
- For full mode: **Python 3.9+** (GhostLock)
- Fire TV and PC on the same network (or USB debugging)
- On the Fire TV: **Settings → My Fire TV → Developer Options →
  ADB Debugging** enabled
- Confirm the ADB dialog on the TV with **“Always allow”**
- For full mode: AFTT/tank on Fire OS **5.2.9.5**
  (build `288.6.8.8_user_688806320` or `...688805020`)

Connect and verify ADB:

```bash
adb connect 192.168.0.100:5555
adb devices
# It must say "device", not "unauthorized" or "offline".
```

---

## 2.1 Root (GhostLock)

LightOS uses **temporary** root through GhostLock: every root command starts
the exploit again. There is **no** persistent `su`, no Magisk and no TWRP.
Package changes (debloat, home replacement, OTA off) still survive reboots.

The complete guide, including dry run, root test, safety, troubleshooting and
the optional hardware-root route, is in **[ROOT.md](ROOT.md)**.

Quick version:

```bash
cd LightOS/ghostlock
python3 scripts/gl_root.py --serial 192.168.0.100:5555 --dry-run
python3 scripts/gl_root.py --serial 192.168.0.100:5555 --cmd "id; getenforce"
```

Or use the helper:

```bash
cd LightOS
./scripts/root_test.sh 192.168.0.100:5555
./scripts/root_cmd.sh  192.168.0.100:5555 "id; getenforce"
```

Expected: `build guard: OK`, `uid=0(root)`, `Permissive`.

**Only for AFTT/tank on Fire OS 5.2.9.5 (688806320/688805020).** Do not use it
on other devices. LightOS does not need persistent root.

---

## 3. Quick start

```bash
cd LightOS
chmod +x install.sh scripts/*.sh
./install.sh 192.168.0.100:5555
```

The installer detects the device:

- **AFTT/tank + 5.2.9.5:** asks no questions; runs the root test,
  installation, setup, debloat and minimal profile; opens the Home chooser;
  then reboots.
- **All other devices:** installs **only the launcher APK** and exits. No root,
  no debloat, Amazon Home remains.

After the reboot on the TV:

1. Press **Home**.
2. Choose **LightOS**.
3. Confirm **“Always”**.

If no chooser appears:

```bash
adb -s 192.168.0.100:5555 shell am start -a android.intent.action.MAIN -c android.intent.category.HOME
```

### Installer options

```bash
./install.sh --check 192.168.0.100:5555   # check only, change nothing
./install.sh --safe  192.168.0.100:5555   # APK only, no root/debloat
./install.sh --full  192.168.0.100:5555   # force full mode (AFTT 5.2.9.5 only)
./install.sh --restore 192.168.0.100:5555 # restore Amazon UI/services
./install.sh --yes --no-reboot 192.168.0.100:5555
```

`--full` is **rejected** on unsupported devices. `--safe` works on every Fire
TV. The `auto` mode (no option) decides from model and build.

---

## 4. Manual installation (without install.sh)

Only for AFTT/tank on Fire OS 5.2.9.5:

```bash
cd LightOS

./scripts/root_test.sh 192.168.0.100:5555
./scripts/install_launcher.sh 192.168.0.100:5555
./scripts/apply_setup.sh 192.168.0.100:5555
# On the TV: Home -> LightOS -> Always
./scripts/apply_debloat.sh 192.168.0.100:5555
./scripts/apply_minimal.sh 192.168.0.100:5555
adb -s 192.168.0.100:5555 reboot
```

The scripts use GhostLock for temporary root. Every call starts the exploit
again; that is normal. Package changes persist.

---

## 5. What full mode does

The complete list of removed functions, packages, trade-offs and RAM effects
is in **[REMOVED-FUNCTIONS.md](REMOVED-FUNCTIONS.md)**.

### Home screen / launcher

- Large Apple-TV-style home screen with top tabs:
  **Home · Account · Apps · Movies · Shows · Settings**
- Dynamic hero: shows the currently focused app with icon, name, subtitle,
  colour gradient and a large watermark
- Floating dock with round icons:
  Prime Video, YouTube, Appstore, Settings (gear), Netflix and other
  installed apps
- Round focus ring, inset and never clipped
- **Move apps:** MENU or long OK → left/right → OK; the order is saved
- **Apps & Appstore:** custom app management, Amazon Appstore button,
  app info/uninstall
- **Settings:** custom view with Fire TV shortcuts and **Customize Home**
  (24-hour clock, date)

### System changes

- Parental controls/PIN disabled
- Amazon Home and HomeStarter disabled
- Amazon launcher package kept for the Prime catalogue and Settings
- OTA updates disabled
- Animations set to 0
- Boot receiver starts LightOS after 6 s and again after 20 s

### Debloat / minimal profile

**65 packages/components disabled**, including:

- Alexa (`com.amazon.vizzini`) uninstalled for user 0
- FTVLaunchX uninstalled (no longer needed)
- Ads, telemetry, shopping, support, Live TV, IMDb, MiniTV, GameHub,
  Music/Photos, OTA, parental controls, Amazon Home, HomeStarter
- Silk browser (`cloud9`), Ambient/screensaver (`glorialist`),
  web launcher, onboarding (`oobe`)
- Casting/setup: WhisperLink, WhisperJoin, WhisperPlay, DialService,
  TV routing, communication discovery
- Phone-remote REST (`firerestapiframework`)
- Alexa H2 service (`alta.h2clientservice`)
- Netflix boot receiver (`com.netflix.ninja/.BootCompleteReceiver`);
  Netflix itself stays usable

**Kept:** Prime Video (player + catalogue deep link), Amazon Appstore,
YouTube, Netflix, Settings, launcher package, remote/Bluetooth, Wi-Fi,
sound/CEC, keyboard, input picker, time, account sync, messaging.

**Trade-offs:** Alexa voice button, phone remote/casting, Silk browser and
onboarding are off. The physical remote stays active. Amazon OTA is blocked.
After the first debloat, the next boot may optimize once and take longer;
after that it is faster.

---

## 6. Safe mode on other Fire TVs

Safe mode installs only `apk/LightOS-4.2.apk`:

```bash
./install.sh --safe <IP>:5555
```

- The app appears in the Fire TV app list and can be started there.
- The Amazon home screen remains.
- No root, no debloat, no OTA/PIN changes.
- The app includes a boot receiver and can be offered as a Home candidate.
  On some Fire OS versions the Prime/Settings deep links differ; the app
  remains usable as a launcher.
- FTVLaunchX is **not included** in this repository. If needed, use the
  upstream project and respect its license. On some devices it can set up a
  home replacement through the accessibility service. Fire OS may block the
  service; full mode on AFTT does not use it.

Safe mode is **not** a complete LightOS conversion. A home replacement,
debloat and OTA blocking require the supported AFTT build.

---

## 7. Restore (bring back the Amazon UI)

Intended for the supported AFTT full mode:

```bash
./install.sh --restore 192.168.0.100:5555
```

or:

```bash
./scripts/restore_amazon.sh 192.168.0.100:5555
```

The script:

1. requires your own copy of `apk/com.amazon.vizzini.apk` and pushes it to
   `/data/local/tmp`,
2. installs Alexa for user 0,
3. enables all 65 disabled packages,
4. enables Amazon Home, HomeStarter, parental controls and OTA,
5. enables the Netflix boot receiver,
6. sets animations back to 0.5,
7. opens the Home chooser.

Then choose **Amazon Home** on the TV. If LightOS is no longer needed:

```bash
adb -s 192.168.0.100:5555 uninstall de.customtv.start
```

The Alexa APK is not included (Amazon proprietary). Provide your own copy in
`apk/` or restore Alexa through the Amazon Appstore.

---

## 7.1 Original backup: Fire OS 5.2.9.2 (681822620)

The official Amazon 5.2.9.2 full-firmware archive for AFTT/tank (build
`288.6.8.1_user_681822620`) plus the pre-update state of the reference device
is **kept privately and is not part of this repository**. The local archive
has:

| | |
|---|---|
| Firmware | `update-kindle-full_tank-288.6.8.1_user_681822620.bin` |
| Size | 526,001,324 bytes |
| Firmware SHA-256 | `6b144ae90a039f6c2d6e6deb7ecdfb7cb08b55dba2d16806a710c719faa5a04b` |
| ZIP size | 526,007,917 bytes |
| ZIP SHA-256 | `73b5dfa946f3b00a12d9947db42475285389390505dbe65f53ca79c227031ab5` |

The private ZIP contains its own English `README.md`, the firmware, the
pre-update state and `SHA256SUMS`.

**Very important:** this is **not an ADB downgrade**. Fire OS blocks older
builds; the `.bin` is an official Amazon full OTA, not a TWRP ZIP. A forced
downgrade can **brick the stick**. The archive is intended for archival and
recovery research.

**What works safely:** restore the Amazon UI and disabled services on the
current **5.2.9.5**:

```bash
./install.sh --restore <IP>:5555
```

The Fire OS version stays 5.2.9.5.

**If you really want the old Fire OS version:** that is a hardware procedure
(amonet) and only possible on AFTT units manufactured before December 2019
(open the stick, CLK/GND, TWRP/Magisk). The `.bin` is not a TWRP ZIP; the
amonet guide uses a matching recovery/ROM. High brick risk, no warranty.
Details and links are in the private ZIP's `README.md`.

---

## 8. Updating to Fire OS 5.2.9.5 (AFTT)

Before full mode:

**Settings → My Fire TV → About → Install update**

Then verify:

```bash
adb shell getprop ro.build.version.incremental
# 288.6.8.8_user_688806320 (or ...688805020)
adb shell getprop ro.build.version.name
# Fire OS 5.2.9.5
```

After the update, confirm the ADB dialog again. Install LightOS **only after
that**, because full mode disables OTA.

---

## 9. Troubleshooting

### `adb: device offline` / `unauthorized`

- Wake the Fire TV with the remote.
- Confirm the ADB dialog on the TV (“Always allow”).
- `adb disconnect <IP>:5555`, `adb connect <IP>:5555`, `adb devices`.
- If the IP changed: check router/DHCP or scan the network.

### `SecurityException` / `uid=2000`

On Fire OS 5 the normal ADB shell cannot change packages/components. That is
why setup/debloat run through GhostLock (`scripts/*.sh`), not through manual
`pm disable` commands.

### GhostLock: build guard / wrong build

Full mode is exclusively for AFTT/tank on Fire OS 5.2.9.5
(688806320/688805020). **Do not** bypass the build guard. Use `--safe` on
other devices.

### PIN prompt still appears

`apply_setup.sh` disables parental controls. If the PIN remains: verify the
build, run setup again, reboot the Fire TV. If needed, disable
`com.amazon.tv.parentalcontrols` explicitly (included in setup).

### No Home chooser

```bash
adb shell am start -a android.intent.action.MAIN -c android.intent.category.HOME
```

Choose **LightOS** and confirm **“Always”**.

### Moving apps does not start

- Focus a dock icon and press **MENU**, or hold **OK** for about one second.
- Use **left/right** to move the focused icon, then press **OK** to finish.
- The order is saved immediately and restored after a reboot.
- In LightOS 4.2, MENU also works when a top tab is focused: it starts edit
  mode on the last focused dock app (or the first app if none was focused yet).

### Black screen / three dots after boot

This is usually the one-time app optimisation after debloat. Wait 1–2 minutes,
then press Home. The boot receiver starts the home screen again after 20 s. Do
not disconnect power.

### Prime Video

`com.amazon.avod` is only the player on Fire OS 5. The catalogue runs through
the Amazon launcher deep link. Do not uninstall it. The Android TV Prime app
(`com.amazon.amazonvideo.livingroom`) requires Google Play Services and cannot
be used on Fire OS 5.

### Amazon Appstore

The custom Apps screen manages installed apps. The **Amazon Appstore**
button/card opens the real Amazon Appstore. The Amazon catalogue itself cannot
be recreated without the Amazon API.

### Netflix / new apps

New apps appear automatically in the dock and the Apps grid. Octagon/special
shapes are cropped to a circle; the dominant icon colour fills only outer
transparent corners, while inner transparency is preserved.

### Language

The LightOS interface is English. The Amazon settings screens use the Fire OS
system language; if they should be English too, change the Fire TV language in
the Fire TV settings. The app clock/date is always formatted in English.

---

## 10. Performance

- 65 packages/components disabled
- Animations 0, accessibility cleared
- Heavy cached apps can be released with `am force-stop`
- **RAM after reboot:** about 337 MB available
- After releasing cached apps: about **344 MB available / 231 MB strictly free**
- Prime launcher/player stay cached for a fast Prime start
- `pm trim-caches` is **not** in the scripts; the first boot after debloat may
  optimise once, after that it is faster

---

## 11. Folder structure

```text
LightOS/
├── install.sh                 # Universal installer (auto/safe/full/restore/check)
├── README.md                  # This guide
├── ROOT.md                    # Detailed root guide (GhostLock)
├── REMOVED-FUNCTIONS.md       # Removed functions & performance notes
├── CHECKSUMS.txt              # Checksums
├── apk/
│   ├── LightOS-4.2.apk        # LightOS launcher (English UI)
│   └── README.md              # notes on APKs not included
├── scripts/
│   ├── install_launcher.sh
│   ├── root_test.sh
│   ├── root_cmd.sh            # run individual root commands
│   ├── apply_setup.sh
│   ├── apply_debloat.sh
│   ├── apply_minimal.sh
│   └── restore_amazon.sh
├── ghostlock/                 # GhostLock root tool (AFTT 5.2.9.5)
├── state/                     # setup/debloat/minimal/restore commands
├── src/                       # source, manifest and build script
├── screenshots/               # reference screenshots
└── original-backup/
    └── README.md              # local firmware archive is not included
```

---

## 12. Checksums

The APK checksum is in `CHECKSUMS.txt`:

```bash
sha256sum apk/LightOS-4.2.apk
# a6a52684f3d6a315d79d1e480306b063251a0c0ff32a94e9fbc95b3c102b6809
```

The installed APK on the reference device has exactly this checksum (verified
after installation). The full local package (not in this repository) has an
outer checksum in `LightOS.zip.sha256` next to the ZIP.

---

## 13. Safety and limits

- GhostLock is **build-specific** and **temporary** (one-shot).
- A complete system image is not possible without an unlocked bootloader.
- The included APK is signed with a local debug key. The public repository
  does not include a keystore; `src/build.sh` generates a local one. Do not use
  a debug key for production.
- The Amazon Alexa APK is not included in this repository; use your own copy
  only for personal restore.
- OTA is disabled in full mode; update to 5.2.9.5 first.
- Use only on devices you own; root/modifications may affect warranty and
  Amazon's terms of use.
- The bundled GhostLock tool is third-party software; its own documentation is
  partly in English and partly in French and is kept unmodified for integrity.

---

## 14. Credits

- **GhostLock** – one-shot root for Fire TV Stick 2 (tank):
  https://github.com/Anezium/ghostlock-firetv-tank
- **Debloat list / launcher approach**:
  https://github.com/HokutoNoCode/FireTV-Stick-Basic-Debloat
- **LightOS** – custom launcher app, source in `src/`

Enjoy LightOS.
