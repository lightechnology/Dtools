package org.adol.tdm.dtools.data.vo;

import android.bluetooth.BluetoothDevice;

/**
 * Created by adolp on 2017/4/5.
 */

public class BleDevice {

    private Boolean connected;
    private BluetoothDevice device;

    public BleDevice() {
        this.connected = null;
        this.device = null;
    }

    public Boolean isConnected() {
        return connected;
    }

    public void setConnected(Boolean connected) {
        this.connected = connected;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }
}
