package org.adol.tdm.dtools.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.util.blecm.intf.FoundBleDevice;

import org.adol.tdm.dtools.R;
import org.adol.tdm.dtools.data.vo.BleDevice;
import org.adol.tdm.dtools.receiver.BleDeviceScanReceiver;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by adolp on 2017/4/5.
 */

public class BleDeviceListAdapter extends BaseAdapter {

    public static final String TAG = "BleDeviceListAdapter";

    private ArrayList<Map<String, String>> bleDeviceArrayList;
    private LayoutInflater layoutInflater;

    public BleDeviceListAdapter(LayoutInflater layoutInflater) {
        super();
        this.bleDeviceArrayList = new ArrayList<Map<String, String>>();
        this.layoutInflater = layoutInflater;
    }

    public void add(Map<String, String> device) {
        int n = has(device);
        if (-1 == n) {
            bleDeviceArrayList.add(device);
        } else {
            bleDeviceArrayList.remove(n);
            bleDeviceArrayList.add(n, device);
        }
    }

    private int has(Map<String, String> device) {
        for (int i = 0; i < bleDeviceArrayList.size(); i++) {
            Map<String, String> d = bleDeviceArrayList.get(i);
            if (d.get(BleDeviceScanReceiver.DEVICE_ADDRESS).equals(device.get(BleDeviceScanReceiver.DEVICE_ADDRESS)))
                return i;
        }
        return -1;
    }

    public void clear() {
        bleDeviceArrayList.clear();
    }

    public Map<String, String> getDevice(int position) {
        return bleDeviceArrayList.get(position);
    }

    @Override
    public int getCount() {
        return bleDeviceArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return bleDeviceArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.device_ble_item, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceName = (TextView) convertView.findViewById(R.id.item_txt);
            viewHolder.deviceAddress = (TextView) convertView.findViewById(R.id.subItem_txt);
            viewHolder.connectState = (ImageView) convertView.findViewById(R.id.connectState_img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Map<String, String> device = bleDeviceArrayList.get(position);
        final String name = device.get(BleDeviceScanReceiver.DEVICE_NAME);
        final String address = device.get(BleDeviceScanReceiver.DEVICE_ADDRESS);
        final String connectState = device.get(BleDeviceScanReceiver.DEVICE_CONNECT_STATE);
        if (name != null && name.length() > 0)
            viewHolder.deviceName.setText(name);
        else
            viewHolder.deviceName.setText(R.string.unknown_device);
        viewHolder.deviceAddress.setText(address);
        if (null != connectState && "true".equals(connectState))
            viewHolder.connectState.setImageResource(R.drawable.connected);
        if (null != connectState && "false".equals(connectState))
            viewHolder.connectState.setImageResource(R.drawable.disconnect);
        if (null == connectState)
            viewHolder.connectState.setImageBitmap(null);

        return convertView;
    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        ImageView connectState;
    }
}
