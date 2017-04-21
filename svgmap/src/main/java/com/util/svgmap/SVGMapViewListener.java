package com.util.svgmap;

import android.graphics.Bitmap;

public interface SVGMapViewListener {

    void onMapLoadComplete();

    void onMapLoadError();

    void onGetCurrentMap(Bitmap bitmap);
}
