package org.adol.tdm.dtools.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.util.qrcode.MipcaActivityCapture;

import org.adol.tdm.dtools.AppActivity;
import org.adol.tdm.dtools.DtApplication;
import org.adol.tdm.dtools.R;
import org.adol.tdm.dtools.adapter.DeviceListAdapter;
import org.adol.tdm.dtools.data.vo.Device;
import org.adol.tdm.dtools.intf.CallBack;
import org.adol.tdm.dtools.receiver.BleDeviceScanReceiver;
import org.adol.tdm.dtools.receiver.ReadBletReceiver;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

@ContentView(R.layout.activity_mg_device_sin)
public class MgDeviceSinActivity extends AppActivity implements CallBack {

    public final static String TAG = "MgDeviceSinActivity";
    private final static int SCANNIN_GREQUEST_CODE = 1;

    @ViewInject(R.id.toolbar)
    Toolbar toolbar;
    @ViewInject(R.id.add_device_btn)
    FloatingActionButton fab;
    @ViewInject(R.id.device_lst)
    private ListView device_lst;
    private DeviceListAdapter deviceListAdapter;
    private ReadBletReceiver readBleReceiver;
    private String mac = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);
        fab.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
        deviceListAdapter = new DeviceListAdapter(this.getLayoutInflater());
        device_lst.setAdapter(deviceListAdapter);
        try {
            List<Device> list = x.getDb(DtApplication.daoConfig).selector(Device.class).findAll();
            if (null != list && !list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    String[] item = new String[]{list.get(i).getName(), list.get(i).getMac()};
                    deviceListAdapter.add(item);
                }
                deviceListAdapter.notifyDataSetChanged();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        IntentFilter readBle = new IntentFilter(ReadBletReceiver.BLE_READING);
        readBleReceiver = new ReadBletReceiver(this);
        registerReceiver(readBleReceiver, readBle);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(readBleReceiver);
        super.onDestroy();
    }

    @Event(R.id.add_device_btn)
    private void fabOnClick(View view) {
        Intent intent = new Intent();
        intent.setClass(MgDeviceSinActivity.this, MipcaActivityCapture.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String qrcode = bundle.getString("result");
                    deviceListAdapter.add(new String[]{qrcode, mac});
                    deviceListAdapter.notifyDataSetChanged();
                    Device device = new Device();
                    device.setName(qrcode);
                    device.setMac(mac);
                    try {
                        x.getDb(DtApplication.daoConfig).save(device);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @Override
    public void callBack(String mac) {
        this.mac = mac;
        Intent intent = new Intent();
        intent.setClass(MgDeviceSinActivity.this, MipcaActivityCapture.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
    }
}
