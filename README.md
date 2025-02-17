# Capacitor DataWedge - community maintained plugin

![capacitor-version](https://img.shields.io/badge/Capacitor-v6-lightgreen)
![version](https://img.shields.io/npm/v/capacitor-datawedge)
![downloads](https://img.shields.io/npm/dm/capacitor-datawedge)
![contributors](https://img.shields.io/github/contributors/jkbz64/capacitor-datawedge)
![license](https://img.shields.io/npm/l/capacitor-datawedge)

This plugin allows you to simply gain access to receiving barcode data and use some api methods from the DataWedge API designed for Capacitor with Zebra devices.

## Install

```bash
npm install capacitor-datawedge
npx cap sync
```

## Usage

To use the `DataWedge` plugin with the DataWedge API, follow these steps:

1. **Register the broadcast receiver**:
   ```typescript
   import { DataWedge } from 'capacitor-datawedge';

   const filterActions = ["com.symbol.datawedge.api.RESULT_ACTION", "com.zebra.bumbal.ACTION"];
   const filterCategories = ["android.intent.category.DEFAULT"];
   const filter = { filterActions, filterCategories };

   await DataWedge.registerBroadcastReceiver(filter);
   ```

2. **Send a command to get the version info**:
   ```typescript
   const intent = {
     action: "com.symbol.datawedge.api.ACTION",
     extras: { "com.symbol.datawedge.api.GET_VERSION_INFO": "" }
   };

   await DataWedge.sendBroadcastWithExtras(intent);
   ```

3. **Listen for broadcast events**:
   ```typescript
   DataWedge.addListener('broadcast', (state) => {
     console.log('Received broadcast:', state);
   });
   ```

4. **Remove all listeners** (when needed):
   ```typescript
   await DataWedge.removeAllListeners();
   ```

### Example

```typescript
import { Capacitor } from "@capacitor/core";
import { DataWedge } from 'capacitor-datawedge';

if (Capacitor.getPlatform() === "android") {
  const filterActions = ["com.symbol.datawedge.api.RESULT_ACTION", "com.your.app.ACTION"];
  const filterCategories = ["android.intent.category.DEFAULT"];
  const filter = { filterActions, filterCategories };

  DataWedge.registerBroadcastReceiver(filter)
    .then(() => {
      console.log("Broadcast receiver registered");

      const intent = {
        action: "com.symbol.datawedge.api.ACTION",
        extras: { "com.symbol.datawedge.api.GET_VERSION_INFO": "" }
      };

      return DataWedge.sendBroadcastWithExtras(intent);
    })
    .then(() => {
      console.log("Version info command sent");
    })
    .catch((e) => {
      console.error("Error during setup", e);
    });

  DataWedge.addListener('broadcast', (intent) => {
    const extras = intent["extras"] as JsonObject

    if (extras) {
      const extrasKeys = Object.keys(extras)

      for (const extrasKey of extrasKeys) {
        switch (extrasKey) {
          case "com.symbol.datawedge.api.RESULT_GET_VERSION_INFO":
            // create a datawedge profile
            break

          case "com.symbol.datawedge.data_string":
            // A barcode has been scanned
            break
        }
      }
    }
  });

  const createDataWedgeProfile = async () => {
    await this.sendCommand("com.symbol.datawedge.api.CREATE_PROFILE", "profile_name")

    const firstProfileConfig = {
      PROFILE_NAME: "profile_name",
      PROFILE_ENABLED: "true",
      CONFIG_MODE: "UPDATE",
      PLUGIN_CONFIG: {
        PLUGIN_NAME: "BARCODE",
        RESET_CONFIG: "true",
        PARAM_LIST: {}
      },
      APP_LIST: [
        {
          PACKAGE_NAME: "com.your.app",
          ACTIVITY_LIST: ["*"]
        }
      ]
    }
    await this.sendCommand("com.symbol.datawedge.api.SET_CONFIG", firstProfileConfig)

    const secondProfileConfig = {
      PROFILE_NAME: "Bumbal v2",
      PROFILE_ENABLED: "true",
      CONFIG_MODE: "UPDATE",
      PLUGIN_CONFIG: {
        PLUGIN_NAME: "INTENT",
        RESET_CONFIG: "true",
        PARAM_LIST: {
          intent_output_enabled: "true",
          intent_action: "com.your.app.ACTION",
          intent_category: "android.intent.category.DEFAULT",
          intent_delivery: "2"
        }
      }
    }
    await this.sendCommand("com.symbol.datawedge.api.SET_CONFIG", secondProfileConfig)
  }

  const sendCommand = async (extraName: string, extraValue: string | JsonObject): Promise<void> => {
    let broadcastExtras = {}
    broadcastExtras[extraName] = extraValue
    await DataWedge.sendBroadcastWithExtras({
      action: "com.symbol.datawedge.api.ACTION",
      extras: broadcastExtras
    })
  }
}
```

This example demonstrates the basic steps to register the broadcast receiver, send a command to get version info, and listen for broadcast events using the `DataWedge` plugin.


## API

<docgen-index>

* [`registerBroadcastReceiver(...)`](#registerbroadcastreceiver)
* [`sendBroadcastWithExtras(...)`](#sendbroadcastwithextras)
* [`addListener('broadcast', ...)`](#addlistenerbroadcast)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)

</docgen-index>

Every broadcasted intent assumes `com.symbol.datawedge.api` package as default.

Package name can be changed by modyfing `DATAWEDGE_PACKAGE` variable [here](android/src/main/java/com/jkbz/capacitor/datawedge/DataWedge.java)

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### registerBroadcastReceiver(...)

```typescript
registerBroadcastReceiver(filter: BroadcastReceiverFilter) => any
```

Register broadcast receiver

| Param        | Type                                                                        |
| ------------ | --------------------------------------------------------------------------- |
| **`filter`** | <code><a href="#broadcastreceiverfilter">BroadcastReceiverFilter</a></code> |

**Returns:** <code>any</code>

**Since:** 0.4.0

--------------------


### sendBroadcastWithExtras(...)

```typescript
sendBroadcastWithExtras(intent: BroadcastIntent) => any
```

Send broadcast with extras

| Param        | Type                                                        |
| ------------ | ----------------------------------------------------------- |
| **`intent`** | <code><a href="#broadcastintent">BroadcastIntent</a></code> |

**Returns:** <code>any</code>

**Since:** 0.4.0

--------------------


### addListener('broadcast', ...)

```typescript
addListener(eventName: 'broadcast', listenerFunc: BroadcastListener) => any
```

Listen for successful barcode readings

***Notice:*** Requires intent action to be set to `com.capacitor.datawedge.RESULT_ACTION` in current DataWedge profile (it may change in the future)

| Param              | Type                                                            |
| ------------------ | --------------------------------------------------------------- |
| **`eventName`**    | <code>'broadcast'</code>                                        |
| **`listenerFunc`** | <code><a href="#broadcastlistener">BroadcastListener</a></code> |

**Returns:** <code>any</code>

**Since:** 0.4.0

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => any
```

Remove all listeners

**Returns:** <code>any</code>

**Since:** 0.4.0

--------------------


### Interfaces


#### PluginListenerHandle

| Prop         | Type                      |
| ------------ | ------------------------- |
| **`remove`** | <code>() =&gt; any</code> |


### Type Aliases


#### BroadcastReceiverFilter

<code>{ filterActions: string[]; filterCategories: string[]; }</code>


#### BroadcastIntent

<code>{ action: string; extras: <a href="#jsonobject">JsonObject</a>; }</code>


#### JsonObject

<code>{ [key: string]: | string | number | boolean | <a href="#jsonobject">JsonObject</a> | string[] | number[] | boolean[] | JsonObject[]; }</code>


#### BroadcastListener

<code>(state: <a href="#jsonobject">JsonObject</a>): void</code>

</docgen-api>

## License

**BSD-3-Clause**

```
Copyright (c) 2021-2023, jkbz64 and contributors
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

* Neither the name of the copyright holder nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
```
