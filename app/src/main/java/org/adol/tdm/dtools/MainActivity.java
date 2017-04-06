package org.adol.tdm.dtools;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.util.blecm.BleDrive;

import org.adol.tdm.dtools.activity.MgBleConnectActivity;
import org.adol.tdm.dtools.activity.MgDeviceLocationActivity;
import org.adol.tdm.dtools.activity.MgDeviceSinActivity;
import org.adol.tdm.dtools.data.vo.Device;
import org.adol.tdm.dtools.service.BleCmService;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cn.pedant.SweetAlert.SweetAlertDialog;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppActivity {

    public static final String TAG = "MainActivity";

    @ViewInject(R.id.tools_lst)
    private ListView tools_lst;
    private ArrayAdapter<String> stringArrayAdapter;
    private Intent bleCmService;

    private final static Class[] TOOL_CLASSES = {MgBleConnectActivity.class, MgDeviceSinActivity.class, MgDeviceLocationActivity.class};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stringArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.tools_array));
        tools_lst.setAdapter(stringArrayAdapter);
        String str = null;
        if (!BleDrive.checkIsSupportBle(this, false))
            str = getString(R.string.ble_not_supported);
        BluetoothAdapter bluetoothAdapter = BleDrive.initBleAdapter(this, false);
        if (null == BleDrive.initBleAdapter(this, false))
            str = getString(R.string.error_bluetooth_not_supported);
        if (null != str)
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(str)
                    .setConfirmText(getString(R.string.ok))
                    .show();
        BleDrive.enableBleDrive(bluetoothAdapter, this);
        bleCmService = new Intent(MainActivity.this, BleCmService.class);
        startService(bleCmService);
    }

    @Event(value = R.id.tools_lst, type = AdapterView.OnItemClickListener.class)
    private void itemSelected(AdapterView<ArrayAdapter<String>> parent, View view, int position, long id) {
        if (position < TOOL_CLASSES.length) {
            startActivity(new Intent(MainActivity.this, TOOL_CLASSES[position]));
        } else
            Toast.makeText(this, R.string.developing, Toast.LENGTH_SHORT).show();
    }

}
