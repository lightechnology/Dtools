package org.adol.tdm.dtools.adapter;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.adol.tdm.dtools.R;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

/**
 * Created by adolp on 2017/3/31.
 */

public class DeviceListAdapter extends BaseAdapter {

    private ArrayList<String[]> deviceInfos;
    private LayoutInflater layoutInflater;

    public DeviceListAdapter(LayoutInflater layoutInflater) {
        super();
        this.deviceInfos = new ArrayList<String[]>();
        this.layoutInflater = layoutInflater;
    }

    public void add(String[] item) {
        if (!deviceInfos.contains(item))
            deviceInfos.add(item);
    }

    public void clear() {
        deviceInfos.clear();
    }

    @Override
    public int getCount() {
        return deviceInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return deviceInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_simple, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceSin = (TextView) convertView.findViewById(R.id.item_txt);
            viewHolder.deviceMac = (TextView) convertView.findViewById(R.id.subItem_txt);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String[] deviceInfo = deviceInfos.get(position);
        final String deviceSin = deviceInfo[0];
        final String deviceMac = deviceInfo[1];
        if (deviceSin != null && deviceSin.length() > 0)
            viewHolder.deviceSin.setText(deviceSin);
        else
            viewHolder.deviceSin.setText(R.string.unkonw);
        if (deviceMac != null && deviceMac.length() > 0)
            viewHolder.deviceMac.setText(deviceMac);
        else
            viewHolder.deviceMac.setText(R.string.unkonw);

        return convertView;
    }

    static class ViewHolder {
        TextView deviceSin;
        TextView deviceMac;
    }
}
