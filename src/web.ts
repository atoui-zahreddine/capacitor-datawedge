import { WebPlugin } from '@capacitor/core';

import type { DataWedgePlugin } from './definitions';

export class DataWedgeWeb extends WebPlugin implements DataWedgePlugin {

  registerBroadcastReceiver(): Promise<void> {
    console.warn('registerBroadcastReceiver is not available on the web');
    return Promise.resolve();
  }

  sendBroadcastWithExtras(): Promise<void> {
    console.warn('sendBroadcastWithExtras is not available on the web');
    return Promise.resolve();
  }

}
