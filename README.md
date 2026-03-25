# xDrip-mj — MiniMed 780G Companion App Fork

> A specialised fork of xDrip+ targeting **only** the Medtronic MiniMed 780G via the official
> Medtronic companion app on Android, with a modern Jetpack-Compose / Material 3 UI and Wear OS
> complication support.

## Supported hardware

| Phone | Pump | Data source |
|---|---|---|
| Samsung Galaxy S25 (Snapdragon 8 Elite) | Medtronic MiniMed 780G | MiniMed companion app (notification listener) |

**No other CGM hardware, transmitters, or data bridges are supported in this fork.**

## What is different from upstream xDrip+?

| Area | Change |
|---|---|
| Data sources | Only "MiniMed 780G Companion App" (UiBased) is shown in settings; all other source paths are hidden |
| Home screen | New Jetpack Compose + Material 3 dashboard (glucose, trend, delta, age, 3-h graph) |
| Settings | Compose-based MiniMed settings screen |
| Wear OS | Compose for Wear OS watch app + enhanced complication provider |
| Stability | Battery-optimisation detection and prompt; improved foreground-service logging |

## How it works

The app reads glucose values from the **notification stream** of the official Medtronic MiniMed
Mobile companion app.  It does **not** communicate with the pump directly (no RileyLink / BLE
bridge needed).

### Setup
1. Install and open the Medtronic **MiniMed Mobile** app.  Ensure it is connected to your 780G and
   displaying readings.
2. Install this app.
3. On first launch, go to **Settings → Hardware Data Source** and select
   *MiniMed 780G Companion App*.
4. Grant the **Notification Listener** permission when prompted (required so the app can read the
   BG value displayed in the MiniMed notification).
5. Optionally exclude this app from Samsung's battery optimisation (prompted on the home screen).

## Build

```bash
./gradlew :app:assembleDebug :wear:assembleDebug
```

Requires JDK 17, Android SDK 34, Kotlin 1.8.x.

---

*This project is based on [xDrip+](https://github.com/NightscoutFoundation/xDrip) by the Nightscout
Foundation, licensed under the Apache 2.0 License.  It is an independent, unofficial fork and is
not affiliated with Medtronic.*

