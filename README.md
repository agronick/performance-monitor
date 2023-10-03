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

![Screenshot_20231002_130401](https://github.com/agronick/aa-torque/assets/2042303/229315c8-ad3b-42e6-86e6-e7fe7abb16a8)

![Screenshot_20231002_130452](https://github.com/agronick/aa-torque/assets/2042303/698c666b-5e3c-4611-80a5-8e767b04186a)

![20231001-162619](https://github.com/agronick/aa-torque/assets/2042303/1b07b67b-d4bc-41e8-8235-a1418ee37bfe)

![rx2ohmzjsirb1](https://github.com/agronick/aa-torque/assets/2042303/52c83a91-c3b6-430e-967a-4473fc51e51e)




## Custom Expressions
AA Torque uses [EvalEx](https://ezylang.github.io/EvalEx) for custom expressions. You can find documentation [here](https://ezylang.github.io/EvalEx/references/functions.html).
