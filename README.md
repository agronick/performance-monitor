# Android Auto Plugin for Torque

This was a performance monitor, based on [Chillout's Performance Monitor](https://github.com/jilleb/mqb-pm) which was based on Martoreto's aa-stats.
The app has been modified with a focus on making it more customizable with the ability
to obtain any PID from Torque. Torque pro is required to use this app.

The original app focused on VAG (Volkswagen Auto Group) cars. This version focuses on exposing
all data from Torque in a way that is as customizable as possible.

# Main features:
- Display data from Torque including custom PIDs
- Many themes, fonts, incons, and backgrounds to choose from
- Up to 10 dashboards
- Rotary dial support
- Supports custom expressions using [EvalEx](https://ezylang.github.io/EvalEx/references/functions.html)
- Backup, restore, and share your configuration

# Instructions:

Download the latest [release](https://github.com/agronick/aa-torque/releases).

- Install [Torque Pro](https://play.google.com/store/apps/details?id=org.prowl.torque&hl=en_US&gl=US)
- Make sure Android Auto is in developer mode: Open Android Auto (while not connected to the car), go to About. Tap the "About Android Auto" header 10 times until you see a toast message saying you're a developer. From the right top corner select developer options, scroll down and make sure you check "Unknown sources". This will allow programs from non-Playstore apps to be run on Android Auto. See a picture guide how to enable Developer mode here: https://www.howtogeek.com/271132/how-to-enable-developer-settings-on-android-auto/
- Install the AA Torque apk using [Kingstaller](https://github.com/fcaronte/KingInstaller/releases) so your phone thinks it came from the play store
- Open the AA Torque settings, grant all the rights it's requesting. If you don't do this, the app will NOT work.
- Hook your phone to your car's USB, start Android Auto on your unit.
- You can find the AA Torque in the menu on the lower right in Android Auto, the one with the dashboard clock on it.

# Screenshots:
Some screenshots of the app:

![VW theme](https://user-images.githubusercontent.com/8352494/48626461-322c1380-e9b2-11e8-990a-b380c43f93e1.png)

Seat theme:
![Seat theme](https://camo.githubusercontent.com/c3043a363e40cac344c4f2cb4a943671205806d2/68747470733a2f2f692e696d6775722e636f6d2f56436a58474d582e706e67)

Skoda ONEapp theme with high min/max turned on.
![Skoda ONE theme](https://i.imgur.com/OfO3jpb.png)

Stopwatch/laptimer mode in VW GTI theme:
![VW stopwatch](https://i.imgur.com/0jm310L.png)



## Custom Expressions
AA Torque uses [EvalEx](https://ezylang.github.io/EvalEx) for custom expressions. You can find documentation [here](https://ezylang.github.io/EvalEx/references/functions.html).
