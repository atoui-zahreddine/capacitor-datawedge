import type { PluginListenerHandle } from '@capacitor/core';

export type BroadcastListener = (state: JsonObject) => void;
export type BroadcastReceiverFilter = {
  filterActions: string[];
  filterCategories: string[];
};

export type JsonObject = {
  [key: string]:
    | string
    | number
    | boolean
    | JsonObject
    | string[]
    | number[]
    | boolean[]
    | JsonObject[];
};

export type BroadcastIntent = {
  action: string;
  extras: JsonObject;
}

export interface DataWedgePlugin {
  /**
   * Register broadcast receiver
   *
   * @since 0.4.0
   */
  registerBroadcastReceiver(filter: BroadcastReceiverFilter): Promise<void>;

  /**
   * Send broadcast with extras
   *
   * @since 0.4.0
   */
  sendBroadcastWithExtras(intent:BroadcastIntent): Promise<void>;
  /**
   * Listen for successful barcode readings
   *
   * ***Notice:*** Requires intent action to be set to `com.capacitor.datawedge.RESULT_ACTION` in current DataWedge profile (it may change in the future)
   *
   * @since 0.4.0
   */
  addListener(
    eventName: 'broadcast',
    listenerFunc: BroadcastListener,
  ): Promise<PluginListenerHandle>;

  /**
   * Remove all listeners
   *
   * @since 0.4.0
   */
  removeAllListeners(): Promise<void>;
}
