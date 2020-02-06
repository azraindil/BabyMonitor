package com.example.nianiadladziecka;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

public class CameraActivity extends AppCompatActivity {
    private WebView webView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_camera);
        // Set web view content to be the raspberry pi camera stream
        webView = (WebView)findViewById(R.id.webViewCamera);
        int default_zoom_level=100;
        webView.setInitialScale(default_zoom_level);

        // Get the width and height of the view because its different for different phone or table layouts
        // Pass these values to the URL in the web view to display the HTTP stream
        webView.post(new Runnable()
        {
            @Override
            public void run() {
                int width = webView.getWidth();
                int height = webView.getHeight();
                webView.setBackgroundColor(Color.TRANSPARENT);
                webView.loadUrl(AppConfig.URL_CAMERA + "?width="+width+"&height="+height);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        webView.destroy();
    }
}
