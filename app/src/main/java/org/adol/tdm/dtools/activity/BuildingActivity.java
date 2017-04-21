package org.adol.tdm.dtools.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.adol.tdm.dtools.AppActivity;
import org.adol.tdm.dtools.R;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_building)
public class BuildingActivity extends AppActivity {

    @ViewInject(R.id.floor_lst)
    private ListView floor_lst;
    private ArrayAdapter<String> stringArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getIntent().getExtras();
        int n = bundle.getInt("building");
        if (0 == n)
            stringArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.floor_array_a));
        else
            stringArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.floor_array_b));
        floor_lst.setAdapter(stringArrayAdapter);
    }

    @Event(value = R.id.floor_lst, type = AdapterView.OnItemClickListener.class)
    private void itemSelected(AdapterView<ArrayAdapter<String>> parent, View view, int position, long id) {
        Intent intent = new Intent(BuildingActivity.this, MapActivity.class);
        startActivity(intent);
    }
}
