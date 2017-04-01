package org.adol.tdm.dtools;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.adol.tdm.dtools.activity.MgDeviceSinActivity;
import org.adol.tdm.dtools.service.BleCmService;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppActivity {

    public static final String TAG = "MainActivity";

    @ViewInject(R.id.tools_lst)
    private ListView tools_lst;
    private ArrayAdapter<String> stringArrayAdapter;
    private Intent bleCmService;

    private final static Class[] TOOL_CLASSES = {MgDeviceSinActivity.class};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stringArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.tools_array));
        tools_lst.setAdapter(stringArrayAdapter);
        bleCmService = new Intent(MainActivity.this, BleCmService.class);
        startService(bleCmService);
    }

    @Event(value = R.id.tools_lst, type = AdapterView.OnItemClickListener.class)
    private void itemSelected(AdapterView<ArrayAdapter<String>> parent, View view, int position, long id) {
        if (position < TOOL_CLASSES.length) {
            stopService(bleCmService);
            startActivity(new Intent(MainActivity.this, TOOL_CLASSES[position]));
        } else
            Toast.makeText(this, R.string.developing, Toast.LENGTH_SHORT).show();
    }
}
