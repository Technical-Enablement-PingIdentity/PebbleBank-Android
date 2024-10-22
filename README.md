# Ping PebbleBank Demo App

## Disclaimer of Liability
DISCLAIMER: This code is provided to you expressly as an example("Sample Code"). It is the responsibility of the individual recipient user, in his/her sole discretion, to diligence such Sample Code for accuracy, completeness, security, and final determination for appropriateness of use. 
ANY SAMPLE CODE IS PROVIDED ON AN "AS IS" IS BASIS, WITHOUT WARRANTY OF ANY KIND. FORGEROCK AND ITS LICENSORS EXPRESSLY DISCLAIM ALL WARRANTIES,  WHETHER EXPRESS, IMPLIED, OR STATUTORY, INCLUDING WITHOUT LIMITATION, THE IMPLIED WARRANTIES  OF MERCHANTABILITY, OR FITNESS FOR A PARTICULAR PURPOSE.
PING IDENTITY SHALL NOT HAVE ANY LIABILITY ARISING OUT OF OR RELATING TO ANY USE, IMPLEMENTATION, INTEGRATION, OR CONFIGURATION OF ANY SAMPLE CODE IN ANY PRODUCTION ENVIRONMENT OR FOR ANY COMMERCIAL DEPLOYMENT(S).

## Setup the source code
1. Download the ForgeRock SDK for Android from Github Repo (https://github.com/ForgeRock/forgerock-android-sdk)
2. Clone this repo into the samples folder
3. Rename the directory name to "pebblebank-app"
4. Modify settings.gradle.kts file by adding
```zsh
include(":pebblebank")
project(":pebblebank").projectDir = File("samples/pebblebank-app")
```
