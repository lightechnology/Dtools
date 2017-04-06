package org.adol.tdm.dtools.listener;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.util.blecm.intf.FoundBleDevice;

import org.adol.tdm.dtools.data.vo.BleDevice;
import org.adol.tdm.dtools.receiver.BleDeviceScanReceiver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adolp on 2017/4/5.
 */

public class BleScanListener implements FoundBleDevice {

    private Context context;
    private List<BleDevice> bleDeviceList;
    private List<String> addressList;
    private BleDevice bleDevice;

    public BleScanListener(Context context) {
        this.context = context;
        this.bleDeviceList = new ArrayList<BleDevice>();
        this.addressList = new ArrayList<String>();
        this.bleDevice = null;
    }

    public List<BleDevice> getBleDeviceList() {
        return bleDeviceList;
    }

    @Override
    public void recodeBleDevice(BluetoothDevice device) {
        String address = "";
        if (null != bleDevice) {
            address = bleDevice.getDevice().getAddress();
            if (!addressList.contains(address)) {
                add(bleDevice);
            }
        }
        address = device.getAddress();
        if (!addressList.contains(address)) {
            BleDevice bleDevice_temp = new BleDevice();
            bleDevice_temp.setDevice(device);
            add(bleDevice_temp);
        }
    }

    @Override
    public void clear() {
        bleDeviceList.clear();
        addressList.clear();
        Intent intent = new Intent();
        intent.setAction(BleDeviceScanReceiver.SCAN_BLE_DEVICE);
        context.sendBroadcast(intent);
    }

    public void setBleDevice(BleDevice bleDevice) {
        if (null != this.bleDevice) {
            this.bleDevice.setConnected(null);
            update();
        }
        this.bleDevice = bleDevice;
    }

    public BleDevice getBleDevice() {
        return bleDevice;
    }

    public void add(BleDevice bleDevice) {
        bleDeviceList.add(bleDevice);
        addressList.add(bleDevice.getDevice().getAddress());
        Intent intent = new Intent();
        intent.setAction(BleDeviceScanReceiver.SCAN_BLE_DEVICE);
        intent.putExtra(BleDeviceScanReceiver.DEVICE_NAME, bleDevice.getDevice().getName());
        intent.putExtra(BleDeviceScanReceiver.DEVICE_ADDRESS, bleDevice.getDevice().getAddress());
        String connectState = null;
        if (null != bleDevice.isConnected() && bleDevice.isConnected())
            connectState = "true";
        if (null != bleDevice.isConnected() && !bleDevice.isConnected())
            connectState = "false";
        intent.putExtra(BleDeviceScanReceiver.DEVICE_CONNECT_STATE, connectState);
        context.sendBroadcast(intent);
    }

    public void update() {
        add(bleDevice);
    }
}
