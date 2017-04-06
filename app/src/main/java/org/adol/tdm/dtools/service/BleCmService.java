package org.adol.tdm.dtools.service;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;
import android.widget.Toast;

import com.util.blecm.BleDrive;
import com.util.blecm.BleScanBroadcastReceiver;
import com.util.blecm.intf.BleAcListener;

import org.adol.tdm.dtools.R;
import org.adol.tdm.dtools.data.vo.BleDevice;
import org.adol.tdm.dtools.listener.BleScanListener;
import org.adol.tdm.dtools.receiver.BleDeviceConnectReceiver;
import org.adol.tdm.dtools.receiver.BleDeviceScanReceiver;
import org.adol.tdm.dtools.receiver.ReadBletReceiver;
import org.adol.tdm.dtools.util.ComParams;
import org.afinal.simplecache.ACache;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BleCmService extends Service implements BleAcListener, Runnable {

    public static final String TAG = "BleCmService";

    private BleDrive bleDrive;
    private BleScanListener bleScanListener;
    private BleDevice bleDevice;

    private Lock lock;
    private Condition condition;

    private BleDeviceConnectReceiver bleDeviceConnectReceiver;

    private Thread thread;
    private boolean run;

    public BleCmService() {
        this.bleScanListener = new BleScanListener(this);
        this.bleDrive = new BleDrive(this, bleScanListener);

        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();

        this.bleDeviceConnectReceiver = new BleDeviceConnectReceiver(this);

        this.thread = new Thread(this);
        this.run = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 检查BLE是否连接过，连接过的话，如果状态是断开的，尝试连接
        if (!run) {
            run = true;
            thread.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        bleDrive.init(ComParams.SERVICEID, ComParams.READID, ComParams.WRITEID, this);
        IntentFilter bleDeviceAction = new IntentFilter(BleDeviceConnectReceiver.BLE_ACTION);
        registerReceiver(bleDeviceConnectReceiver, bleDeviceAction);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        bleDrive.destroy();
        unregisterReceiver(bleDeviceConnectReceiver);
        this.run = false;
        super.onDestroy();
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        Log.e(TAG, "status: " + status + " newState: " + newState + " BluetoothProfile.STATE_CONNECTED: " + BluetoothProfile.STATE_CONNECTED + " BluetoothProfile.STATE_DISCONNECTED: " + BluetoothProfile.STATE_DISCONNECTED + " BluetoothProfile.STATE_DISCONNECTING: " + BluetoothProfile.STATE_DISCONNECTING);
        try {
            lock.lock();
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                bleDevice.setConnected(true);
            } else {
                if (null != bleDevice)
                    if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        bleDevice.setConnected(false);
                    } else {
                        bleDevice.setConnected(null);
                    }
                condition.signal();
            }
            bleScanListener.update();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        byte[] datat = characteristic.getValue();
        //if ((byte) 0x8F == datat[0]) {
            Log.e(TAG, "xxxxxxxxxxxxxx");
        try {
            final String str = new String(characteristic.getValue(), "GBK");
            Intent intent = new Intent();
            intent.setAction(ReadBletReceiver.BLE_READING);
            intent.putExtra(ReadBletReceiver.BLE_RKEY, str);
            sendBroadcast(intent);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //}
    }

    public void connect(String address) {
        if (null != address) {
            try {
                lock.lock();
                bleDevice = found(address);
                bleScanListener.setBleDevice(bleDevice);
                bleDrive.disconnect();
                bleDrive.connect(bleDevice.getDevice());
            } finally {
                lock.unlock();
            }
        }
    }

    public void disconnect() {
        try {
            lock.lock();
            if (null != bleScanListener.getBleDevice())
                bleScanListener.getBleDevice().setConnected(false);
            bleDevice = null;
            bleDrive.disconnect();
        } finally {
            lock.unlock();
        }
    }

    public void resume() {
        try {
            lock.lock();
            Log.e(TAG, "resume");
            bleDrive.resume();
        } finally {
            lock.unlock();
        }
    }

    public void pause() {
        try {
            lock.lock();
            Log.e(TAG, "pause");
            bleDrive.pause();
        } finally {
            lock.unlock();
        }
    }

    public BleDevice found(String address) {
        if (null != address && 0 < address.length()) {
            for (int i = 0; i < bleScanListener.getBleDeviceList().size(); i++) {
                BleDevice bleDevice = bleScanListener.getBleDeviceList().get(i);
                if (address.equals(bleDevice.getDevice().getAddress())) {
                    return bleDevice;
                }
            }
        }
        return null;
    }

    @Override
    public void run() {
        while (run) {
            try {
                lock.lock();
                while (null == bleDevice || null != bleDevice.isConnected() && bleDevice.isConnected()) {
                    condition.await();
                }
                if (null != bleDevice) {
                    bleDrive.disconnect();
                    bleDrive.connect(bleDevice.getDevice());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
            try {
                Thread.sleep(BleDrive.SCAN_PERIOD);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
