package org.adol.tdm.dtools.receiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.adol.tdm.dtools.service.BleCmService;

/**
 * Created by adolp on 2017/4/5.
 */

public class BleDeviceConnectReceiver extends BroadcastReceiver {

    public final static String TAG = "BDCR";

    public final static String BLE_SCAN = "ble_scan";
    public final static String BLE_ADDRESS = "ble_address";
    public final static String BLE_ACTION = "ble_action";

    private BleCmService bleCmService;

    public BleDeviceConnectReceiver(BleCmService bleCmService) {
        this.bleCmService = bleCmService;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BLE_ACTION.equals(action)) {
            String scan = intent.getStringExtra(BLE_SCAN);
            if ("resume".equals(scan)) {
                bleCmService.resume();
            } else if ("pause".equals(scan)) {
                bleCmService.pause();
            } else {
                String address = intent.getStringExtra(BLE_ADDRESS);
                if (null != address) {
                    bleCmService.connect(address);
                } else {
                    bleCmService.disconnect();
                }
            }
        }
    }

}
