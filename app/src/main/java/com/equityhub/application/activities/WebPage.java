package com.equityhub.application.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.equityhub.application.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WebPage extends AppCompatActivity {

    private static final String DEFAULT_URL = "https://mobile.anyview1.com";
    private static final int FILE_REQUEST_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;

    private WebView webView;
    private android.webkit.ValueCallback<Uri[]> filePathCallback;
    private Uri cameraPhotoUri;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_page);

        // Mark the WebPage as visited in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SplashActivity.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SplashActivity.KEY_VISITED, true); // Set visited flag to true
        editor.apply();

        initializeWebView();
        loadUrl();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack(); // Navigate back in the WebView's history
        } else {
            super.onBackPressed(); // Finish the activity
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initializeWebView() {
        webView = findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);

        // Set WebView background color to white
        webView.setBackgroundColor(getResources().getColor(android.R.color.white));

        // Set a WebViewClient to handle page loading
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView, android.webkit.ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (WebPage.this.filePathCallback != null) {
                    WebPage.this.filePathCallback.onReceiveValue(null);
                    WebPage.this.filePathCallback = null;
                }

                WebPage.this.filePathCallback = filePathCallback;

                if (arePermissionsGranted()) {
                    showFileChooser();
                } else {
                    requestPermissions();
                }

                return true;
            }
        });
    }

    private void loadUrl() {
        String url = getIntent().getStringExtra("URL");
        if (url == null || url.isEmpty()) {
            SharedPreferences sharedPreferences = getSharedPreferences(SplashActivity.PREFS_NAME, MODE_PRIVATE);
            // "DEFAULT_URL" is the key for the value stored in shared preferences.
            url = sharedPreferences.getString("DEFAULT_URL", "https://mobile.anyview1.com");
        }
        webView.loadUrl(url);
    }

    private void showFileChooser() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                cameraPhotoUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPhotoUri);
            }
        }

        Intent mediaPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent filePickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        filePickerIntent.addCategory(Intent.CATEGORY_OPENABLE);
        filePickerIntent.setType("*/*");

        ArrayList<Intent> intentList = new ArrayList<>();
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            intentList.add(cameraIntent);
        }
        intentList.add(mediaPickerIntent);

        Intent chooserIntent = Intent.createChooser(filePickerIntent, "Choose an option");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Intent[0]));

        startActivityForResult(chooserIntent, FILE_REQUEST_CODE);
    }

    private File createImageFile() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = "IMG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(null);
            return File.createTempFile(fileName, ".jpg", storageDir);
        } catch (IOException e) {
            Toast.makeText(this, "Error creating file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private boolean arePermissionsGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showFileChooser();
            } else {
                Toast.makeText(this, "Permissions denied. Please grant them to continue.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_REQUEST_CODE) {
            Uri[] result = null;

            if (resultCode == RESULT_OK) {
                if (data != null && data.getData() != null) {
                    Uri selectedImageUri = data.getData();
                    String filePath = getPathFromUri(selectedImageUri);
                    if (filePath != null) {
                        result = new Uri[]{Uri.fromFile(new File(filePath))};
                    } else {
                        result = new Uri[]{selectedImageUri}; // Handle the URI directly if the path is null
                    }
                } else if (cameraPhotoUri != null) {
                    result = new Uri[]{cameraPhotoUri};
                }
            }

            // Pass the result to the callback and reset it
            if (filePathCallback != null) {
                filePathCallback.onReceiveValue(result);
                filePathCallback = null; // Ensure callback is reset
            } else {
                Toast.makeText(this, "File selection failed or cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getPathFromUri(Uri uri) {
        String path = null;
        if ("content".equals(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA};
            try (Cursor cursor = getContentResolver().query(uri, projection, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    path = cursor.getString(columnIndex);
                }
            }
        }
        return path;
    }
}
