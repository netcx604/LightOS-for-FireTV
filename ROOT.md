# Root for LightOS – Fire TV Stick 2 (AFTT / tank)

This guide explains how to get temporary root on a **Fire TV Stick 2nd gen
(AFTT, codename `tank`)** running **Fire OS 5.2.9.5** using **GhostLock**.

> **Important:** GhostLock is a **one-shot root**. It installs **no**
> persistent `su`, no Magisk and no TWRP. Every root command starts the
> exploit again. The **package changes** made with it (debloat, home
> replacement, OTA off) persist because they live in `/data`.

You do not need to root manually for a normal LightOS installation:
`./install.sh` performs the root test and the changes automatically. This
guide is for manual root commands and for understanding the process.

---

## 1. Supported devices / builds

| Check | Expected value |
|---|---|
| Model | `AFTT` |
| Device/codename | `tank` |
| Fire OS | **5.2.9.5** |
| Build | `288.6.8.8_user_688806320` (tested) or `288.6.8.8_user_688805020` (supported by GhostLock) |
| Kernel | `3.10.54+`, ARM32, MT8127 |

**Not supported:** Fire TV Stick 4K/Cube, Fire OS 7/8, other models, other
builds. GhostLock is build-specific (fixed kernel addresses). The build guard
must **not** be bypassed.

---

## 2. Requirements

- Fire TV Stick 2 (AFTT/tank) on Fire OS **5.2.9.5** (see update below)
- PC with **adb** (Android Platform Tools)
- **Python 3.9+**
- Linux, macOS or Windows
- Fire TV and PC on the same network (or USB debugging)
- On the Fire TV: **Settings → My Fire TV → Developer Options →
  ADB Debugging** enabled
- Confirm the ADB dialog on the TV with **“Always allow”**

Connect ADB:

```bash
adb connect 192.168.0.100:5555
adb devices
# "device" must appear, not "unauthorized"/"offline"
```

---

## 3. Check device and build

```bash
adb -s 192.168.0.100:5555 shell getprop ro.product.model
# AFTT
adb -s 192.168.0.100:5555 shell getprop ro.product.device
# tank
adb -s 192.168.0.100:5555 shell getprop ro.build.version.name
# Fire OS 5.2.9.5 (688806320)
adb -s 192.168.0.100:5555 shell getprop ro.build.version.incremental
# 288.6.8.8_user_688806320
adb -s 192.168.0.100:5555 shell cat /proc/version
# Linux version 3.10.54+ ...
```

If the build does not match: **do not root**. Update to 5.2.9.5 first
(Settings → My Fire TV → About → Install update) or use safe mode.

---

## 4. GhostLock dry run (changes nothing)

```bash
cd ~/Dokumente/LightOS/ghostlock
python3 scripts/gl_root.py --serial 192.168.0.100:5555 --dry-run
```

Expected output:

```text
device: 192.168.0.100:5555
build:  288.6.8.8_user_688806320 (tank)
kernel: Linux version 3.10.54+ ...
selinux: Enforcing
build guard: OK
```

`build guard: OK` means the device, build and kernel match the exploit.

---

## 5. Test root

```bash
cd ~/Dokumente/LightOS/ghostlock
python3 scripts/gl_root.py --serial 192.168.0.100:5555 --cmd "id; getenforce"
```

Expected output:

```text
attempt 1/3 ... ROOT (0.5s)
---- command output ----
uid=0(root) gid=0(root) ... context=u:r:kernel:s0
Permissive
```

- `uid=0(root)` = root shell for this command.
- `Permissive` = SELinux is permissive for this session.
- The exploit tries up to three times; on a kernel panic the stick reboots
  and you can try again.
- Logs are stored under `ghostlock/analysis/gl-runs/`.

Alternatively, the quick test:

```bash
cd ~/Dokumente/LightOS
./scripts/root_test.sh 192.168.0.100:5555
```

---

## 6. Running root commands

### Helper `root_cmd.sh`

```bash
cd ~/Dokumente/LightOS
./scripts/root_cmd.sh 192.168.0.100:5555 "id; getenforce"
./scripts/root_cmd.sh 192.168.0.100:5555 "pm list packages -d"
./scripts/root_cmd.sh 192.168.0.100:5555 "pm disable com.amazon.cloud9"
```

The helper starts GhostLock with your command. Chain multiple commands with
`;` so only one root run is needed.

### Examples

```bash
# Disable a package
./scripts/root_cmd.sh 192.168.0.100:5555 "pm disable com.amazon.cloud9"

# Enable a package again
./scripts/root_cmd.sh 192.168.0.100:5555 "pm enable com.amazon.cloud9"

# Remove an app for user 0
./scripts/root_cmd.sh 192.168.0.100:5555 "pm uninstall --user 0 com.amazon.vizzini"

# Force-stop an app
./scripts/root_cmd.sh 192.168.0.100:5555 "am force-stop com.netflix.ninja"

# Write a setting
./scripts/root_cmd.sh 192.168.0.100:5555 "settings put global window_animation_scale 0"

# Verify the root shell
./scripts/root_cmd.sh 192.168.0.100:5555 "id; getenforce"
```

The LightOS scripts use exactly this path:

```bash
./scripts/apply_setup.sh    192.168.0.100:5555
./scripts/apply_debloat.sh  192.168.0.100:5555
./scripts/apply_minimal.sh  192.168.0.100:5555
./scripts/restore_amazon.sh 192.168.0.100:5555
```

---

## 7. Safety and limits

- **The build guard is mandatory.** Wrong build = wrong kernel addresses =
  possible memory corruption. Do not bypass it.
- The exploit does **not** write to eMMC. A failure is usually a kernel panic →
  reboot. Your data/apps remain intact.
- According to GhostLock: about **90–95% success** on a warm system, about
  **80%** right after boot, about **2% panic** per attempt. The tool retries
  automatically up to three times.
- After a successful run SELinux stays permissive until reboot. A reboot
  afterwards is clean.
- Root is **temporary**. There is no persistent `su`, no Magisk, no TWRP and no
  way to write `/system`.
- OTA should be disabled after rooting (done by `apply_setup.sh`) so the build
  does not silently jump to an unsupported version.
- Use only on devices you own. Root may affect warranty and Amazon's terms of
  use.

---

## 8. Troubleshooting

### `device offline` / `unauthorized`

- Wake the Fire TV with the remote.
- Confirm the ADB dialog on the TV (“Always allow”).
- `adb disconnect <IP>:5555`, `adb connect <IP>:5555`, `adb devices`.
- If the IP changed: check router/DHCP.

### `build guard` fails

- The device is not AFTT/tank, or the build is not 5.2.9.5
  (688806320/688805020).
- **Do not** bypass the guard. Update to 5.2.9.5 or use safe mode.

### `attempt 1/3 ... no root`

- Try again; the stick should be warm and as idle as possible.
- Close heavy apps (`am force-stop`), wait briefly, try again.
- On panic/reboot: wait, reconnect ADB, try again.
- Check the logs under `ghostlock/analysis/gl-runs/`.

### `python3: command not found`

Install Python 3.9+:

```bash
# Debian/Ubuntu
sudo apt install python3
```

### `adb: command not found`

Install Android Platform Tools and add them to `PATH`.

### `SecurityException` / `uid=2000` with `pm`

The normal ADB shell cannot change packages on Fire OS 5. Therefore use
`root_cmd.sh` / `apply_*.sh` (GhostLock), not manual `pm disable` in the normal
shell.

### No root, but LightOS should run

Then the device is probably unsupported. Use:

```bash
./install.sh --safe <IP>:5555
```

That installs only the launcher APK, without root and without debloat.

---

## 9. Permanent root (optional, not needed for LightOS)

LightOS does **not** need permanent root. The one-shot session is enough for
the home replacement, debloat and OTA blocking.

If you still want **permanent** root (TWRP/Magisk/`su`), the Fire TV Stick 2
only supports it through the **hardware BootROM route (amonet)**:

- Open the stick, short CLK/GND, enter BootROM mode, flash `amonet-tank`, then
  install TWRP/Magisk.
- Only on units manufactured **before December 2019**. Newer units have a
  locked BootROM; there is no software-only permanent root.
- Requirements: Linux PC, USB, experience, willingness to take risks.
- Risk: brick, loss of warranty, data loss. **No** ADB-only method.

Links (English):

- XDA: `https://xdaforums.com/t/unlock-root-twrp-unbrick-fire-tv-stick-2nd-gen-tank.3907002/`
- AFTVnews: `https://www.aftvnews.com/root/fire-tv-stick-2/`

**Recommendation:** for LightOS, stay with the one-shot root. The hardware
procedure is only for experienced users who really want TWRP/Magisk/custom
ROMs.

---

## 10. Other Fire TV models

This guide applies **only** to AFTT/tank on Fire OS 5.2.9.5.
For Fire TV Stick 4K, Cube, Fire OS 7/8, etc.:

- **do not** use GhostLock,
- **do not** apply the AFTT debloat list,
- use only **safe mode** (`./install.sh --safe`) for the launcher APK.

Other models need other model- and build-specific root methods. Without a
matching exploit, only the launcher APK is available.

---

## 11. Credits

- **GhostLock** – one-shot root for Fire TV Stick 2 (tank):
  `https://github.com/Anezium/ghostlock-firetv-tank`
- Port for build 688806320:
  `https://github.com/mhabou/ghostlock-firetv-tank-688806320`
- **amonet-tank** (hardware root, XDA) by k4y0z:
  `https://xdaforums.com/t/unlock-root-twrp-unbrick-fire-tv-stick-2nd-gen-tank.3907002/`

Good luck – and always run the dry run first.
