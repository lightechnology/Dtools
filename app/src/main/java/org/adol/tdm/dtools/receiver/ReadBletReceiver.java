package org.adol.tdm.dtools.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.adol.tdm.dtools.intf.CallBack;

/**
 * Created by adolp on 2017/4/6.
 */

public class ReadBletReceiver extends BroadcastReceiver {

    public static final String BLE_READING = "ble_reading";
    public static final String BLE_RKEY = "ble_rkey";

    private CallBack callBack;

    public ReadBletReceiver(CallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BLE_READING.equals(action)) {
            String msg = intent.getStringExtra(BLE_RKEY);
            this.callBack.callBack(msg);
        }
    }

}
