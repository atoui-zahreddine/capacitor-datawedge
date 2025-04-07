package com.capacitor.datawedge.intents;

import android.os.Build;
import android.os.Bundle;
import com.getcapacitor.*;
import com.getcapacitor.annotation.CapacitorPlugin;

import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;

import android.util.Log;
import org.json.JSONException;


@CapacitorPlugin(name = "DataWedge")
public class DataWedgePlugin extends Plugin {

    private boolean isReceiverRegistered = false;


    @PluginMethod
    public void sendBroadcastWithExtras(PluginCall call) throws JSONException {
        try {
            String action = call.getString("action");
            Intent intent = new Intent();

            if (action != null) {
                intent.setAction(action);
            }

            JSObject extrasObject = call.getObject("extras");

            if (extrasObject != null) {
                Bundle extrasBundle = DataWedge.toBundle(extrasObject);
                intent.putExtras(extrasBundle);
            }

            getContext().sendBroadcast(intent);
            call.resolve();
        } catch (Exception e) {
            call.reject(e.getMessage());
        }
    }


    @PluginMethod
    public void registerBroadcastReceiver(PluginCall call) throws JSONException {
        try {
            // Unregister any existing receiver to avoid conflicts
            unregisterReceiver(genericReceiver);

            // Create an IntentFilter to specify which broadcasts to listen for
            IntentFilter filter = new IntentFilter();

            if (call.hasOption("filterActions")) {
                JSArray filterActions = call.getArray("filterActions");
                if (filterActions != null) {
                    for (int i = 0; i < filterActions.length(); i++) {
                        filter.addAction(filterActions.getString(i));
                    }
                }
            }

            if (call.hasOption("filterCategories")) {
                JSArray filterCategories = call.getArray("filterCategories");
                if (filterCategories != null) {
                    for (int i = 0; i < filterCategories.length(); i++) {
                        filter.addCategory(filterCategories.getString(i));
                    }
                }
            }

            // Register the broadcast receiver with the specified filter
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                getContext().registerReceiver(genericReceiver, filter, Context.RECEIVER_EXPORTED);
            } else {
                getContext().registerReceiver(genericReceiver, filter);
            }

            // Resolve the plugin call
            call.resolve();
            isReceiverRegistered = true;
        } catch (Exception e) {
            call.reject(e.getMessage());
        }
    }

    private final BroadcastReceiver genericReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            JSObject ret = new JSObject();
            ret.put("action", action);

            // Check if extras is null before attempting to get keys
            JSObject extras = new JSObject();
            if (intent.getExtras() != null) {
                extras = DataWedge.bundleToJSObject(intent.getExtras());
            }

            ret.put("extras", extras);

            notifyListeners("broadcast", ret);
        }

    };


    private void unregisterReceiver(BroadcastReceiver receiver) {
        try
        {
            if (receiver != null && isReceiverRegistered) {
                getContext().unregisterReceiver(receiver);
            }
        }
        catch (IllegalArgumentException e)
        {
            Log.e("Capacitor/DataWedge", e.getMessage());
        }
    }
}
