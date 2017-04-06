package org.adol.tdm.dtools.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.BaseAdapter;

import org.adol.tdm.dtools.adapter.BleDeviceListAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by adolp on 2017/4/6.
 */

public class BleDeviceScanReceiver extends BroadcastReceiver {

    public static final String SCAN_BLE_DEVICE = "scanBleDevice";
    public static final String DEVICE_NAME = "name";
    public static final String DEVICE_ADDRESS = "address";
    public static final String DEVICE_CONNECT_STATE = "connectState";

    private BleDeviceListAdapter bleDeviceListAdapter;

    public BleDeviceScanReceiver(BleDeviceListAdapter bleDeviceListAdapter) {
        this.bleDeviceListAdapter = bleDeviceListAdapter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (SCAN_BLE_DEVICE.equals(action)) {
            String address = intent.getStringExtra(DEVICE_ADDRESS);
            if (null != address) {
                String name = intent.getStringExtra(DEVICE_NAME);
                String connectState = intent.getStringExtra(DEVICE_CONNECT_STATE);
                Map<String, String> device = new HashMap<String, String>();
                device.put(DEVICE_NAME, name);
                device.put(DEVICE_ADDRESS, address);
                device.put(DEVICE_CONNECT_STATE, connectState);
                bleDeviceListAdapter.add(device);
            } else {
                bleDeviceListAdapter.clear();
            }
            bleDeviceListAdapter.notifyDataSetChanged();
        }
    }
}
