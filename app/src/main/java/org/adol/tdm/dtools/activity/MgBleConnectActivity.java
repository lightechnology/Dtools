package org.adol.tdm.dtools.activity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.util.blecm.BleDrive;
import com.util.blecm.intf.BleAcListener;

import org.adol.tdm.dtools.AppActivity;
import org.adol.tdm.dtools.R;
import org.adol.tdm.dtools.adapter.BleDeviceListAdapter;
import org.adol.tdm.dtools.data.vo.BleDevice;
import org.adol.tdm.dtools.receiver.BleDeviceConnectReceiver;
import org.adol.tdm.dtools.receiver.BleDeviceScanReceiver;
import org.adol.tdm.dtools.util.ComFunction;
import org.adol.tdm.dtools.util.ComParams;
import org.afinal.simplecache.ACache;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.Map;

@ContentView(R.layout.activity_mg_ble_connect)
public class MgBleConnectActivity extends AppActivity {

    public static final String TAG = "MgBleConnectActivity";

    private BleDeviceListAdapter bleDeviceListAdapter;
    private BleDeviceScanReceiver bleDeviceScanReceiver;

    @ViewInject(R.id.ble_device_lst)
    private ListView bleDevice_lst;
    private BleDevice bleDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bleDeviceListAdapter = new BleDeviceListAdapter(MgBleConnectActivity.this.getLayoutInflater());
        bleDevice_lst.setAdapter(bleDeviceListAdapter);

        IntentFilter bleDeviceAction = new IntentFilter(BleDeviceScanReceiver.SCAN_BLE_DEVICE);
        bleDeviceScanReceiver = new BleDeviceScanReceiver(bleDeviceListAdapter);
        registerReceiver(bleDeviceScanReceiver, bleDeviceAction);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent();
        intent.setAction(BleDeviceConnectReceiver.BLE_ACTION);
        intent.putExtra(BleDeviceConnectReceiver.BLE_SCAN, "resume");
        sendBroadcast(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent intent = new Intent();
        intent.setAction(BleDeviceConnectReceiver.BLE_ACTION);
        intent.putExtra(BleDeviceConnectReceiver.BLE_SCAN, "pause");
        sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(bleDeviceScanReceiver);
        super.onDestroy();
    }

    @Event(value = R.id.ble_device_lst, type = AdapterView.OnItemClickListener.class)
    private void itemOnClick(AdapterView<?> parent, View view, int position, long id) {
        Map<String, String> device = bleDeviceListAdapter.getDevice(position);
        String address = device.get(BleDeviceScanReceiver.DEVICE_ADDRESS);
        String connectState = device.get(BleDeviceScanReceiver.DEVICE_CONNECT_STATE);
        Intent intent = new Intent();
        intent.setAction(BleDeviceConnectReceiver.BLE_ACTION);
        if (null == connectState || "false".equals(connectState) || "null".equals(connectState)) {
            intent.putExtra(BleDeviceConnectReceiver.BLE_ADDRESS, address);
        }
        sendBroadcast(intent);
    }
}
