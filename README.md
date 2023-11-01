# MQB Performance Monitor for VAG Cars

[![ci][1]][2]

This was a performance monitor, based on Martoreto's aa-stats.
It was made specifically for use with the Volkswagen/Skoda/Seat/Audi MIB2 infotainment units, but it might also work on others, as long as they have Android Auto support.

NOTE: I am no longer maintaining the app, and will not provide any support anymore. I've spend a lot of time on it, and had a lot of fun, but after failing to be listed on Google Play, I got demotivated because this meant it was harder for users to use the app. That meant I had to spend a lot of time to support them, because it's not everyone's hobby to jump through tons of hoops to get an app working like it's supposed to work. And then came the sudden influx of AAAD users that overwhelmed the issues-section and my personal email with (mostly) Italian messages. Sometimes the messages were a bit dumb (Like: "How to install on iPhone?" or "Why don't I get data on my BMW?") and sometimes they were very rude and making demands for something they got free in the first place... This took away a lot of the fun and made me decide to spend time on things that are more fun and/or more important. Thanks for all the fun and enthousiasm from everyone! Let's meet again in my future projects.

# Main features:
- Display data from the in-car exlap channel on your infotainment screen
- Display OBD2 data from Torque
- Many OEM-styled themes and backgrounds

# Screenshots:
Some screenshots of the app:

![VW theme](https://user-images.githubusercontent.com/8352494/48626461-322c1380-e9b2-11e8-990a-b380c43f93e1.png)

Seat theme:
![Seat theme](https://camo.githubusercontent.com/c3043a363e40cac344c4f2cb4a943671205806d2/68747470733a2f2f692e696d6775722e636f6d2f56436a58474d582e706e67)

Skoda ONEapp theme with high min/max turned on.
![Skoda ONE theme](https://i.imgur.com/OfO3jpb.png)

Stopwatch/laptimer mode in VW GTI theme:
![VW stopwatch](https://i.imgur.com/0jm310L.png)


# Installation instructions:

Download the latest release here: https://github.com/jilleb/mqb-pm/releases

- Make sure Android Auto is in developer mode: Open Android Auto (while not connected to the car), go to About. Tap the "About Android Auto" header 10 times untill you see a toast message saying you're a developer. From the right top corner select developer options, scroll down and make sure you check "Unknown sources". This will allow programs from non-Playstore apps to be run on Android Auto. See a picture guide how to enable Developer mode here: https://www.howtogeek.com/271132/how-to-enable-developer-settings-on-android-auto/
- Install aauto-vex-vag.apk (open source service by Martoreto: https://github.com/martoreto/aauto-vex-vag/) , the service that supplies exlap-communication between Android and the head-unit. Note: this app will not show up in your launcher.
- Install the Performance Monitor apk
- Open the Performance Monitor settings, grant all the rights it's requesting. If you don't do this, the app will NOT work.
- Hook your phone to your car's USB, start Android Auto on your unit.
- You can find the Performance Monitor in the menu on the lower right in Android Auto, the one with the dashboard clock on it.
- If you want to use Torque data elements, make sure you have installed Torque: https://play.google.com/store/apps/details?id=org.prowl.torque. Also make sure you've enabled full plugin access in Torque settings, otherwise Performance Momitor will not be able to see any data.


# Known issues
- Google is actively blocking custom apps for Android Auto. So the app is not working with Android Auto v3.0 (unless you apply the workaround). Its also not working on Google Play Services v12.5.21. There are workarounds, but I will not describe or support them. 

- Black screen of death: On some cars it doesn't work for unknown reasons. This will not kill your car or headunit. Send me a logcat. 
On Huawei and Honor devices this is a common problem, caused by a bug in their ROMs. A workaround to get OEM apps working again is to clear the Android Auto apk cache when this happens. (thanks to nerone-github for the information.)

- No data
If you don't see any data in the app, it can have various reasons:
Some cars don't provide any data to the headunit. Workaround: Use Torque with an OBD2 dongle to get data. 
Some MIB2 firmware versions don't provide data to the exlap channel.
Not all data elements are available on all cars. If one element doesn't provide data, try an other one.
Most cars do not report oil and coolant temperatures below 50.
Most cars do not report any turbo pressure. Consider yourself lucky when your car does.

- Appkillers/memory managers kill the service
Put the Vag extensions for Android Auto app in the whitelist of your memory manager.

- Phone locking causes data to stop
Some phones kill the datafeed as soon as the screen is locked. This doesn't happen with all phones.

# What to do when it's not working?
- Try choosing a different type of data in the settings app. Not all cars have all data available. Reconnect to the car after you've done this.
- Check if you have applied all rights. Did you open the settings app after installing? Did you install both APK's?
- Unplug/replug your phone and try again

# Currently not working firmware versions:
**MIB2 High:**
- VW 0245
- VW 0343
- VW 0430 
- VW 0613
- VW 0617
- VW 0751
- VW 0753
- VW 0755
- VW 1156
- VW 1427


**MIB2 Std**
- Seat 0351
- Seat 0359

**RCD330:**
- VW 5406

# Currently confirmed working firmware versions:
**MIB2 Std**
- Seat 0462
- Skoda 0468

**MIB2 High:**
- VW 0814
- VW 1187
- Seat 0472
- Seat 1146
- Seat 1219
- Seat 1308
- Seat 1338
- Seat 1409
- Seat 1447
- Skoda 0468
- Skoda 1433

**MIB2.5 High**
- VW 1161
- VW 1367
- Skoda 1440

**MIB3**
MIB3 has no exlap channel, so it won't show any data.

[1]: https://github.com/jilleb/mqb-pm/workflows/ci/badge.svg
[2]: https://github.com/jilleb/mqb-pm/actions
