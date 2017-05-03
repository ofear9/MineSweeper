package com.example.ranendelman.minesweeper.GUI;

import android.content.Context;
import android.webkit.WebView;

/**
 * Created by RanEndelman on 06/01/2017.
 */


/**
 * This Class is for showing GIF images in the app using WebView
 */
public class GifWebView extends WebView {
    /**
     * The Class constructor
     */
    public GifWebView(Context context, String path) {
        super(context);
        loadUrl(path);
    }
}
