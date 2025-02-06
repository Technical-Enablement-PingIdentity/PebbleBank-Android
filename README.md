# Ping PebbleBank Demo App

## Disclaimer of Liability

DISCLAIMER: This code is provided to you expressly as an example("Sample Code"). It is the responsibility of the individual recipient user, in his/her sole discretion, to diligence such Sample Code for accuracy, completeness, security, and final determination for appropriateness of use.
ANY SAMPLE CODE IS PROVIDED ON AN "AS IS" IS BASIS, WITHOUT WARRANTY OF ANY KIND. FORGEROCK AND ITS LICENSORS EXPRESSLY DISCLAIM ALL WARRANTIES, WHETHER EXPRESS, IMPLIED, OR STATUTORY, INCLUDING WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTABILITY, OR FITNESS FOR A PARTICULAR PURPOSE.
PING IDENTITY SHALL NOT HAVE ANY LIABILITY ARISING OUT OF OR RELATING TO ANY USE, IMPLEMENTATION, INTEGRATION, OR CONFIGURATION OF ANY SAMPLE CODE IN ANY PRODUCTION ENVIRONMENT OR FOR ANY COMMERCIAL DEPLOYMENT(S).

## Prerequisites

1. You will need to download and install Android Studio, follow the instructions [here](https://developer.android.com/codelabs/basic-android-kotlin-compose-install-android-studio#0).
2. If you have an android phone you can set it up for wireless debugging, follow instructions [here](https://developer.android.com/studio/debug/dev-options).
   1. Alternatively, if you don't have an android phone, you can run the app in an emulator.

## Setup the source code

1. Download the ForgeRock SDK for Android from the [Github Repo](https://github.com/ForgeRock/forgerock-android-sdk).
2. Clone this repo into the /samples folder, if the folder does not exist you will need to create it.
3. Rename the directory to "pebblebank-app".
4. Modify settings.gradle.kts file by adding:

```zsh
include(":pebblebank")
project(":pebblebank").projectDir = File("samples/pebblebank-app")
```

## Configure ForgeRock Tenant

1. Create a new ForgeRock Cloud Tenant from Encore - if you don't have one already.
2. Configure your server to be used with Android SDK by following the instructions [here](https://backstage.forgerock.com/docs/sdks/latest/sdks/serverconfiguration/cloud/index.html). You will skip any javascript only configurations (e.g. CORS).
3. Open the forgerock-android-sdk folder in Android Studio.
   1. If you run into any issues related to Java, ensure you are using Java version 17. You can check by navigating to the Android Studio settings -> Build, Execution, Deployment -> Build Tools -> Gradle -> Gradle JDK.
4. Navigate to `pebblebank-app/res/values/strings.xml` and update the values under the `<!-- Forgerock SDK Configuration -->` comment as they apply to your tenant, OIDC application configuration and Journeys.
   1. forgerock_url, forgerock_cookie_name and forgerock_oauth_client_id will likely be different from what you configured
   2. Update other keys in this file as needed
5. Run the app (by clicking Run -> pebblebank).
6. Once the app comes up, you should be able to authenticate through the Embedded Login form at the top of the home view or through the centralized login button below that (Login with XXXID button)
