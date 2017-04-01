package org.adol.tdm.dtools.data.vo;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by adolp on 2017/3/31.
 */

@Table(name = "ble_device")
public class BleDevice {

    @Column(name = "uid", isId = true, autoGen = true)
    public int uid;

    @Column(name = "name")
    public String name;

    @Column(name = "mac")
    public String mac;

    public int getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getMac() {
        return mac;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    @Override
    public String toString() {
        return "BleDevice(uid=" + uid + ", name=" + name + ", mac=" + mac + ")";
    }
}
