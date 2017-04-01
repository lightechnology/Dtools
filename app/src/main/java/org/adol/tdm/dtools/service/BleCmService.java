package org.adol.tdm.dtools.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.widget.Toast;

import com.util.blecm.BleDrive;

import org.adol.tdm.dtools.R;

public class BleCmService extends Service {

    private BleDrive bleDrive;

    public BleCmService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, R.string.developing, Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);
    }
}
