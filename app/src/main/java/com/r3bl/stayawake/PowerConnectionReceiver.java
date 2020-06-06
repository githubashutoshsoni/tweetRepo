/*
 * Copyright 2020 R3BL LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.r3bl.stayawake;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import static android.util.Log.d;
import static com.r3bl.stayawake.MyTileService.TAG;

/** Changes to Android broadcast receiver behaviors: http://tinyurl.com/y9rm5wzg */
public class PowerConnectionReceiver extends BroadcastReceiver {

private Context context;

public void onReceive(Context context, Intent intent) {
  this.context = context;
  String action = intent.getAction();
  String msg1 = "onReceive: PowerConnectionReceiver Action=" + action;
  d(TAG, msg1);
  switch (action) {
    case Intent.ACTION_POWER_CONNECTED:
      powerConnected();
      break;
    case Intent.ACTION_POWER_DISCONNECTED:
      powerDisconnected();
      break;
    default:
      //showToast(context, msg1);
      break;
  }
}

// Do nothing when power disconnected.
private void powerDisconnected() {
  MyTileService.stopService(context);
  String msg1 = "onReceive: PowerConnectionReceiver ACTION_POWER_DISCONNECTED ... Stop Service";
  //showDebugToast(context, msg1);
  d(TAG, msg1);
}

// Start service when power connected.
private void powerConnected() {
  MyTileService.startService(context);
  String msg1 = "onReceive: PowerConnectionReceiver ACTION_POWER_CONNECTED ... Start Service";
  //showDebugToast(context, msg1);
  d(TAG, msg1);
}

public static void showToast(Context context, String msg1) {
  Toast.makeText(context, msg1, Toast.LENGTH_LONG).show();
}
}
