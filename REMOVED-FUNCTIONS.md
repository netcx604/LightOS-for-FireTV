# LightOS – removed functions & performance notes

This note documents **what LightOS removed or disabled** to save RAM and make
the Fire TV Stick 2 faster – and what is deliberately kept.

Status: **2026-11-26**
Device: AFTT / `tank`, Fire OS 5.2.9.5 (688806320)
LightOS: **LightOS 4.2** (English UI)

---

## 1. Summary

- **2 apps uninstalled:** Alexa (`com.amazon.vizzini`) and FTVLaunchX
- **65 packages disabled**
- **5 components disabled** (Amazon Home, 3 receivers, Netflix boot)
- **Amazon Home, PIN/parental controls and OTA off**
- **Ads, telemetry, shopping, Alexa, casting, browser, onboarding and
  ambient/screensaver off**
- **Kept:** Prime Video, YouTube, Netflix, Amazon Appstore, Settings,
  remote/Bluetooth, Wi-Fi, sound/CEC, keyboard, account, time, messaging

After a reboot, free RAM is about **337 MB available**. After releasing
cache-only apps (Netflix, Appstore, YouTube, Settings), about **344 MB
available / 231 MB strictly free** was measured. Values vary with usage; the
Prime launcher and Prime player stay cached for a fast Prime start.

---

## 2. Removed / disabled by function

### 2.1 Amazon home screen & UI

| Package / component | Function | Status |
|---|---|---|
| `com.amazon.tv.launcher/.ui.HomeActivity_vNext` | Amazon home screen | component disabled |
| `com.amazon.firehomestarter` | restarts Amazon Home | package disabled |
| `com.amazon.tv.notificationcenter` | Fire TV notifications | disabled |
| `com.amazon.tv.website_launcher` | web launcher | disabled |
| `com.amazon.tv.oobe` | onboarding/first setup | disabled |
| `com.amazon.tv.parentalcontrols` | PIN/parental controls | disabled |

The `com.amazon.tv.launcher` package itself stays **active** because it holds
the Prime catalogue deep link and the Amazon settings. Only the home component
is off.

### 2.2 Ads, telemetry, analytics

| Package | Function |
|---|---|
| `com.amazon.client.metrics` | Amazon metrics |
| `com.amazon.device.metrics` | device metrics |
| `com.amazon.device.crashmanager` | crash upload |
| `com.amazon.device.logmanager` | log upload |
| `com.amazon.dp.logger` | debug/log service |
| `com.amazon.perfcollection` | performance data collection |
| `com.amazon.hybridadidservice` | advertising IDs |
| `com.amazon.ftvads.deeplinking` | ad deep links |
| `com.amazon.adep` | remote config/analytics |
| `com.amazon.tv.fw.metrics` | Fire TV metrics |
| `com.fireos.usagestats.proxy` | usage statistics |
| `com.amazon.wifilocker` | Wi-Fi password cloud lock |
| `com.amazon.tv.developer.dataservice` | developer data service |
| `com.amazon.device.sale.service` | sales/deal service |

### 2.3 Alexa & voice services

| Package | Function |
|---|---|
| `com.amazon.vizzini` | Alexa app (**uninstalled**) |
| `com.amazon.alta.h2clientservice` | Alexa H2 service |
| `com.amazon.alexa.externalmediaplayer.fireos` | Alexa media player |
| `com.amazon.alexamediaplayer.runtime.ftv` | Alexa media runtime |
| `com.amazon.alexashopping` | Alexa shopping |
| `com.amazon.wha.mediabrowserservice` | Alexa media browser |
| `com.amazon.tv.alexaalerts` | Alexa alerts |
| `com.amazon.tv.alexanotifications` | Alexa notifications |

**Trade-off:** the Alexa voice button on the remote no longer works.

### 2.4 Shopping, promotional content, games, demos

| Package | Function |
|---|---|
| `com.amazon.shoptv.client` | Shop TV |
| `com.amazon.shoptv.firetv.client` | Shop TV Fire TV |
| `com.amazon.tv.livetv` | Live TV |
| `com.amazon.imdb.tv.android.app` | IMDb TV |
| `com.imdb.livingroom.firetv` | IMDb Fire TV |
| `com.amazon.minitv.android.app` | MiniTV |
| `com.amazon.gamehub` | GameHub |
| `com.amazon.kso.blackbird` | Amazon game/Blackbird |
| `com.amazon.firebat` | Firebat/gaming service |
| `com.amazon.ceviche` | Amazon Ceviche/another service |
| `com.amazon.tahoe` | Tahoe/shopping service |
| `com.amazon.bueller.music` | Amazon Music (Bueller) |
| `com.amazon.bueller.photos` | Amazon Photos (Bueller) |
| `com.amazon.kor.demo` | retail demo |
| `com.amazon.storm.lightning.tutorial` | tutorial |
| `com.amazon.tmm.tutorial` | tutorial |

### 2.5 OTA & updates

| Package | Function |
|---|---|
| `com.amazon.device.software.ota` | OTA updater |
| `com.amazon.device.software.ota.override` | OTA override |
| `com.amazon.tv.forcedotaupdater.v2` | forced OTA updater |
| `com.amazon.tv.easyupgrade` | upgrade component |

**Trade-off:** no more automatic Fire OS updates. Update to 5.2.9.5 before
LightOS; afterwards the build stays stable.

### 2.6 Browser, screensaver, ambient, misc.

| Package | Function |
|---|---|
| `com.amazon.cloud9` | Silk browser |
| `com.amazon.ftv.screensaver` | Amazon screensaver |
| `com.amazon.ftv.glorialist` | ambient/Gloria service |
| `com.amazon.assetsync.service` | app asset sync |
| `com.amazon.ods.kindleconnect` | Kindle Connect/support notifications |
| `com.amazon.tv.csapp` | customer service app |
| `com.amazon.tv.support` | support |
| `com.amazon.tv.releasenotes` | release notes |
| `com.amazon.tv.ffsprovisioneeclient` | provisioning client |

**Trade-off:** no Silk browser, no Amazon screensaver/ambient.

### 2.7 Casting, phone remote, discovery

| Package | Function |
|---|---|
| `com.amazon.whisperlink.core.android` | WhisperLink/casting |
| `com.amazon.whisperjoin.middleware.np` | WhisperJoin/setup |
| `com.amazon.whisperjoin.wss.wifiprovisioner` | Wi-Fi provisioning |
| `com.amazon.whisperplay.contracts` | WhisperPlay/casting |
| `com.amazon.whisperplay.service.install` | WhisperPlay installer |
| `com.amazon.dialservice` | DIAL/casting |
| `com.amazon.tv.routing` | TV routing |
| `com.amazon.communication.discovery` | discovery |
| `com.amazon.firerestapiframework` | phone remote REST |

**Trade-off:** the **physical remote stays active** (Bluetooth, `tcomm`,
`imp`, `cardinal`), but the Fire TV app/phone remote/casting and second screen
are off.

### 2.8 Other components

| Component | Function | Status |
|---|---|---|
| `com.amazon.connectivitydiag/.backend.CaptivePortalReceiver` | captive portal diagnostics | disabled |
| `com.amazon.diode/.events.ExternalEventReceiver` | event receiver | disabled |
| `com.amazon.diode/.StartEventReceiver` | start event receiver | disabled |
| `com.netflix.ninja/.BootCompleteReceiver` | Netflix starts at boot | disabled |

**Netflix remains usable**; it just no longer starts automatically at boot.

### 2.9 Also uninstalled

| Package | Function |
|---|---|
| `de.codefaktor.ftvlaunchx` | FTVLaunchX (old accessibility home helper) |
| `com.amazon.vizzini` | Alexa (see above) |

---

## 3. Deliberately kept

| Area | Packages / function |
|---|---|
| Prime Video | `com.amazon.avod` (player) + Amazon launcher (catalogue deep link) |
| Amazon Appstore | `com.amazon.venezia`, `com.amazon.aca`, `mas.client.locker`, auth/sync |
| YouTube | `com.amazon.firetv.youtube` |
| Netflix | `com.netflix.ninja` (app; only the boot receiver is off) |
| Settings | `com.amazon.tv.settings.v2`, `settings.core` |
| Amazon launcher | package kept for the Prime catalogue and Settings |
| Remote | `tcomm`, `imp`, `cardinal`, Bluetooth, `devicecontrol` |
| Wi-Fi | network/connectivity services (not disabled) |
| Sound/CEC | `com.amazon.tv.devicecontrol`, `devicecontrolsettings` |
| Keyboard/input | `com.amazon.tv.ime`, `com.amazon.ftv.xpicker` |
| Account/time/push | `identity.auth`, `sync.*`, `kindleautomatictimezone`, `device.messaging` |
| WebView/Settings | `webview.chromium`, `logan`, `prism` |

These packages are required for Prime, YouTube, Netflix, Appstore, Settings,
remote, Wi-Fi and sound, and were not touched.

---

## 4. What this means for RAM and speed

- No Alexa, shopping, advertising, telemetry, casting or onboarding services in
  the background.
- Amazon Home and HomeStarter no longer start.
- OTA and update services no longer run.
- Netflix no longer starts automatically at boot.
- 65 fewer packages + 5 fewer components registering services/receivers.
- After reboot: about **337 MB RAM available**.
- After releasing cached apps: about **344 MB available / 231 MB strictly free**.
- The Prime launcher and Prime player stay in cache intentionally so Prime
  starts fast; Android releases them when needed.
- The **first boot after debloat** may optimize once and take longer. After
  that it is faster. `pm trim-caches` is not in the scripts.

---

## 5. Trade-offs at a glance

| Removed | Consequence |
|---|---|
| Alexa | remote voice button does nothing |
| Amazon Home | Amazon recommendations/ads gone; LightOS is the home screen |
| PIN/parental controls | no PIN prompt, no kids profile |
| OTA | no automatic Fire OS updates |
| Silk/Cloud9 | no built-in browser |
| Screensaver/Glorialist | no Amazon ambient/screensaver |
| Casting/phone remote | Fire TV app/phone remote/second screen off |
| Onboarding/OOBE | no Amazon first setup |
| Netflix boot | Netflix does not start automatically at boot |
| FTVLaunchX | no longer needed; LightOS is the home replacement |

The **physical remote**, **Wi-Fi**, **sound/CEC**, **keyboard**,
**Prime Video**, **YouTube**, **Netflix**, **Appstore** and **Settings** stay
functional.

---

## 6. Bringing something back

Everything:

```bash
cd LightOS
./install.sh --restore <IP>:5555
```

Enable a single package:

```bash
./scripts/root_cmd.sh <IP>:5555 "pm enable com.amazon.ftv.screensaver"
```

A single component:

```bash
./scripts/root_cmd.sh <IP>:5555 "pm enable com.netflix.ninja/.BootCompleteReceiver"
```

Reinstall Alexa (only if needed):

```bash
./install.sh --restore <IP>:5555
# or install your own copy of apk/com.amazon.vizzini.apk manually
```

The restore enables all 65 packages, the home/receiver components and the
Netflix boot receiver. Then choose Amazon Home on the TV.

---

## 7. Full list: 65 disabled packages

```text
com.amazon.adep
com.amazon.alexa.externalmediaplayer.fireos
com.amazon.alexamediaplayer.runtime.ftv
com.amazon.alexashopping
com.amazon.alta.h2clientservice
com.amazon.aria
com.amazon.assetsync.service
com.amazon.bueller.music
com.amazon.bueller.photos
com.amazon.ceviche
com.amazon.client.metrics
com.amazon.cloud9
com.amazon.communication.discovery
com.amazon.device.crashmanager
com.amazon.device.logmanager
com.amazon.device.metrics
com.amazon.device.sale.service
com.amazon.device.software.ota
com.amazon.device.software.ota.override
com.amazon.dialservice
com.amazon.dp.logger
com.amazon.firebat
com.amazon.firehomestarter
com.amazon.firerestapiframework
com.amazon.ftvads.deeplinking
com.amazon.ftv.glorialist
com.amazon.ftv.screensaver
com.amazon.gamehub
com.amazon.hybridadidservice
com.amazon.imdb.tv.android.app
com.amazon.kor.demo
com.amazon.kso.blackbird
com.amazon.minitv.android.app
com.amazon.ods.kindleconnect
com.amazon.perfcollection
com.amazon.shoptv.client
com.amazon.shoptv.firetv.client
com.amazon.storm.lightning.tutorial
com.amazon.tahoe
com.amazon.tmm.tutorial
com.amazon.tv.alexaalerts
com.amazon.tv.alexanotifications
com.amazon.tv.csapp
com.amazon.tv.developer.dataservice
com.amazon.tv.easyupgrade
com.amazon.tv.ffsprovisioneeclient
com.amazon.tv.forcedotaupdater.v2
com.amazon.tv.fw.metrics
com.amazon.tv.livetv
com.amazon.tv.notificationcenter
com.amazon.tv.oobe
com.amazon.tv.parentalcontrols
com.amazon.tv.releasenotes
com.amazon.tv.routing
com.amazon.tv.support
com.amazon.tv.website_launcher
com.amazon.wha.mediabrowserservice
com.amazon.whisperjoin.middleware.np
com.amazon.whisperjoin.wss.wifiprovisioner
com.amazon.whisperlink.core.android
com.amazon.whisperplay.contracts
com.amazon.whisperplay.service.install
com.amazon.wifilocker
com.fireos.usagestats.proxy
com.imdb.livingroom.firetv
```

Additional disabled components:

```text
com.amazon.tv.launcher/.ui.HomeActivity_vNext
com.amazon.connectivitydiag/.backend.CaptivePortalReceiver
com.amazon.diode/.events.ExternalEventReceiver
com.amazon.diode/.StartEventReceiver
com.netflix.ninja/.BootCompleteReceiver
```

Uninstalled:

```text
com.amazon.vizzini
de.codefaktor.ftvlaunchx
```

---

## 8. Notes

- `com.amazon.aria` is the Amazon ARIA/installer service. The Amazon Appstore
  and Netflix installation worked after the debloat. If a future Appstore
  installation hangs, ARIA can be enabled specifically:
  `./scripts/root_cmd.sh <IP>:5555 "pm enable com.amazon.aria"`
- `com.amazon.vizzini` is uninstalled for user 0. The Amazon APK is not
  included in this repository; provide your own copy for a restore.
- The 65 packages reflect the reference device. Other builds/devices have
  other packages; LightOS full mode is intended only for AFTT/5.2.9.5.
- The complete root guide is in `ROOT.md`; installation is in `README.md`.

---

## 9. Conclusion

LightOS deliberately removes the Amazon home screen, ads/telemetry, Alexa,
shopping, casting/phone remote, browser, onboarding, ambient and OTA. What
remains is what is needed for **Prime Video, YouTube, Netflix, Appstore,
Settings, remote, Wi-Fi, sound and account** – with about **337–344 MB of
available RAM** and a lean Apple-TV-style home screen.
