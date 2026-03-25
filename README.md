# Nightscout xDrip+
> Enhanced personal research version of xDrip

 <img align="right" src="Documentation/images/download-xdrip-plus-qr-code.png">
 Info page and APK download: https://jamorham.github.io/#xdrip-plus

<img align="right" src="https://travis-ci.org/jamorham/xDrip-plus.svg?branch=master"><a align="right" title="Crowdin" target="_blank" href="https://crowdin.com/project/xdrip"><img align="right" src="https://badges.crowdin.net/xdrip/localized.svg"></a>

## Features
* Voice, Keypad or Watch input of Treatments (Insulin/Carbs/Notes)
* Visualization of Insulin and Carb action curves + Undo/Redo
* Improved alerts and predictive low forecasting feature
* Instant data synchronization between phones and tablets
* Support for many different data sources
* Published by the Nightscout Foundation

 <img align="middle" src="https://jamorham.github.io/images/jamorham-natural-language-treatments-two-web.png">

## What does it do?

xDrip+ is an unofficial and independent Android app which works as data hub and processor between many different devices.

It supports wireless connections to G6, G7, Medtrum A6, Libre via NFC and Bluetooth, 630G, 640G, 670G pumps, CareSens Air and Eversense CGM via companion apps. Bluetooth Glucose Meters such as the Contour Next One, AccuChek Guide, Verio Flex & Diamond Mini as well as devices like the Pendiq 2.0 Insulin Pen.

Heart-rate and step counter data is processed from Android Wear, Garmin, Fitbit and Pebble smart-watches and watch-faces for those that show glucose values and graphs.

On some Android Wear watches, it is possible for the G6 to talk directly to the watch so it can display values even when out of range of the phone.

The app contains sophisticated charting, customization and data entry features as well as a predictive simulation model.

Instant two-way synchronization is possible by linking follower handsets, data can also be uploaded and downloaded to a Nightscout web service or uploaded directly to Tidepool, MongoDB or InfluxDB.

Customization allows for different options to configure alarms, vocalize readings, change the display preferences etc. International users can update translations from within the app too.

Your data is yours and can be exported in many different ways. xDrip also intercommunicates with other apps, for example sending and receiving live data with AndroidAPS.


## Ethos
* Developed using Rapid Prototyping methodology
* Immediate results favoured to prove concepts
* Designed to support my personal research goals
* User Choice always a high priority
* No registration or Internet access required
* Community testing and collaboration appreciated!

## Roadmap
* Calibration improvements
* Supporting the large family of devices
* Increasing automation and data backup and sync options
* More Nightscout and APS integration

## Collaboration
We are very happy if people want to collaborate with this project. Please contact us at [Discussions](https://github.com/NightscoutFoundation/xDrip/discussions) if you want to get involved and study the [collaboration guidelines](CONTRIBUTING.md) before submitting any patches or pull requests.

## Thanks
None of this would be possible without all the hard work of the xDrip and Nightscout communities who have developed such excellent software and allowed us to build upon it.


---

## xDrip-Style Watch Face (Galaxy Watch 8 / Wear OS)

The `wear` module includes a dedicated **xDrip Style** watch face designed for Galaxy Watch 8
and other Wear OS watches.

### What it displays

| Element | Description |
|---------|-------------|
| **BG value** | Large, colour-coded (yellow = high, red = low, white = normal) |
| **Trend arrow** | Directional arrow (↗ ↘ → etc.) next to the BG value |
| **Delta** | Change since the last reading (e.g. `+0.5 mmol/L`) |
| **Age** | Minutes since the most recent reading (e.g. `5 min ago`) |
| **Sparkline** | Last ~3 h of readings drawn as a line chart at the bottom |
| **Clock** | Current time at the top of the face |

### How to select it on Galaxy Watch 8

1. Long-press on the current watch face on your Galaxy Watch 8.
2. Swipe left/right to browse available watch faces.
3. Select **"xDrip Style"** from the list.
4. Tap anywhere on the watch face to open the **Glucose Detail** screen.

### Data sync

The watch face relies on data pushed from the phone via the **Wearable Data Layer**
(Google `DataClient`).  The existing `ListenerService` on the watch side receives
BG updates and passes them to the watch face in real time – no extra configuration
is needed.

To ensure reliable updates on Samsung One UI / Wear OS:

* Grant xDrip *Battery Optimisation* exemption on the phone.
* Make sure the companion app (Galaxy Wearable) is installed and the watch is paired.
* The watch face refreshes automatically when a new reading arrives; a one-minute
  timer in the base class also triggers periodic redraws as a fallback.

### Required permissions

The Wear app already declares the permissions it needs (`WAKE_LOCK`, `INTERNET`,
`BODY_SENSORS`, etc.).  No additional permissions are required specifically for the
new watch face.
