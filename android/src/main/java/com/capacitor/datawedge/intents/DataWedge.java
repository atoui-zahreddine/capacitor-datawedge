package com.capacitor.datawedge.intents;

import android.os.Bundle;
import com.getcapacitor.JSObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class DataWedge {



    //  https://github.com/darryncampbell/darryncampbell-cordova-plugin-intent/blob/master/src/android/IntentShim.java
    static Bundle toBundle(final JSONObject obj) {
        Bundle returnBundle = new Bundle();
        if (obj == null) {
            return null;
        }
        try {
            Iterator<?> keys = obj.keys();
            while(keys.hasNext())
            {
                String key = (String)keys.next();
                Object compare = obj.get(key);
                if (obj.get(key) instanceof String)
                    returnBundle.putString(key, obj.getString(key));
                else if (key.equalsIgnoreCase("keystroke_output_enabled"))
                    returnBundle.putString(key, obj.getString(key));
                else if (obj.get(key) instanceof Boolean)
                    returnBundle.putBoolean(key, obj.getBoolean(key));
                else if (obj.get(key) instanceof Integer)
                    returnBundle.putInt(key, obj.getInt(key));
                else if (obj.get(key) instanceof Long)
                    returnBundle.putLong(key, obj.getLong(key));
                else if (obj.get(key) instanceof Double)
                    returnBundle.putDouble(key, obj.getDouble(key));
                else if (obj.get(key).getClass().isArray() || obj.get(key) instanceof JSONArray)
                {
                    JSONArray jsonArray = obj.getJSONArray(key);
                    int length = jsonArray.length();
                    if (jsonArray.get(0) instanceof String)
                    {
                        String[] stringArray = new String[length];
                        for (int j = 0; j < length; j++)
                            stringArray[j] = jsonArray.getString(j);
                        returnBundle.putStringArray(key, stringArray);
                        //returnBundle.putParcelableArray(key, obj.get);
                    }
                    else if (jsonArray.get(0) instanceof Double)
                    {
                        int[] intArray = new int[length];
                        for (int j = 0; j < length; j++)
                            intArray[j] = jsonArray.getInt(j);
                        returnBundle.putIntArray(key, intArray);
                    }
                    else
                    {
                        Bundle[] bundleArray = new Bundle[length];
                        for (int k = 0; k < length ; k++)
                            bundleArray[k] = toBundle(jsonArray.getJSONObject(k));
                        returnBundle.putParcelableArray(key, bundleArray);
                    }
                }
                else if (obj.get(key) instanceof JSONObject)
                    returnBundle.putBundle(key, toBundle((JSONObject)obj.get(key)));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return returnBundle;
    }
    public static JSObject bundleToJSObject(Bundle bundle) {
        JSObject jsObject = new JSObject();

        for (String key : bundle.keySet()) {
            Object value = bundle.get(key);

            if (value instanceof Boolean) {
                jsObject.put(key, (Boolean) value);
            } else if (value instanceof Integer) {
                jsObject.put(key, (Integer) value);
            } else if (value instanceof Long) {
                jsObject.put(key, (Long) value);
            } else if (value instanceof Double) {
                jsObject.put(key, (Double) value);
            } else if (value instanceof String) {
                jsObject.put(key, (String) value);
            } else if (value instanceof Bundle) {
                jsObject.put(key, bundleToJSObject((Bundle) value));
            } else {
                jsObject.put(key, value != null ? value.toString() : JSONObject.NULL);
            }
        }

        return jsObject;
    }

}

