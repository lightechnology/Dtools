package org.adol.tdm.dtools.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.adol.tdm.dtools.AppActivity;
import org.adol.tdm.dtools.MainActivity;
import org.adol.tdm.dtools.R;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_mg_device_location)
public class MgDeviceLocationActivity extends AppActivity {

    @ViewInject(R.id.building_lst)
    private ListView building_lst;
    private ArrayAdapter<String> stringArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stringArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.building_array));
        building_lst.setAdapter(stringArrayAdapter);
    }

    @Event(value = R.id.building_lst, type = AdapterView.OnItemClickListener.class)
    private void itemSelected(AdapterView<ArrayAdapter<String>> parent, View view, int position, long id) {
        startActivity(new Intent(MgDeviceLocationActivity.this, MapActivity.class));
    }
}
