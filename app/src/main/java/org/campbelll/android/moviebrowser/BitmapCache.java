package org.campbelll.android.moviebrowser;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Utility class; concrete implementation of ImageLoader.ImageCache for use in Volley's ImageLoader.
 *
 * This code is adapted from org.stevej.android.propertyfinder.utils.BitmapCache
 *
 */
public class BitmapCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {

    public BitmapCache(int size) {
        super(size);
    }

    public BitmapCache() {
        super(getDefaultLruCacheSize());
    }

    /** Returns cache size being an 1/8th of total heap size (if cached items are 1024 Bytes in size). */
    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        return cacheSize;
    }

    /** Wrapper for LruCache.get(url) */
    public Bitmap getBitmap(String url) {
        Log.d("BitmapCache", "getBitmap : " + url);

        Bitmap b = get(url);
        if (b != null) {
            Log.d("BitmapCache", "getBitmap : " + b.getWidth() + " x " + b.getHeight());
        }
        return get(url);
    }

    /** Wrapper for LruCache.put(url) */
    public void putBitmap(String url, Bitmap bitmap) {
        Log.d("BitmapCache", "putBitmap : " + url);

        Log.d("BitmapCache", "putBitmap : " + bitmap.getWidth() + " x " + bitmap.getHeight());
        put(url, bitmap);
    }
}
