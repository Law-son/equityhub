package com.equityhub.application;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.equityhub.application.activities.DetailActivity;
import com.equityhub.application.activities.SplashActivity;
import com.equityhub.application.activities.WebPage;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final String API_URL = "https://cloudflarecdna.tjkirisun.com/show_search";
    private static final String API_KEY = "K4utUXP1spLWIEXEY8PMHbnsmIHZPu5Ccq3uMwHnDW";
    private static final String PACKAGE_NAME = "com.equityhub.application";

    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        searchView = findViewById(R.id.search_view);
        searchView.setIconified(false);
        searchView.clearFocus();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up click listeners for text headings
        TextView heading1_1 = findViewById(R.id.heading_1_1);
        heading1_1.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("SECTION_ID", "1.1");
            startActivity(intent);
        });

        TextView heading1_2 = findViewById(R.id.heading_1_2);
        heading1_2.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("SECTION_ID", "1.2");
            startActivity(intent);
        });

        TextView heading1_3 = findViewById(R.id.heading_1_3);
        heading1_3.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("SECTION_ID", "1.3");
            startActivity(intent);
        });

        TextView heading2_1 = findViewById(R.id.heading_2_1);
        heading2_1.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("SECTION_ID", "2.1");
            startActivity(intent);
        });

        TextView heading2_2 = findViewById(R.id.heading_2_2);
        heading2_2.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("SECTION_ID", "2.2");
            startActivity(intent);
        });

        TextView heading2_3 = findViewById(R.id.heading_2_3);
        heading2_3.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("SECTION_ID", "2.3");
            startActivity(intent);
        });

        // Initialize SearchView
        searchView = findViewById(R.id.search_view);
        searchView.setIconified(false);
        searchView.clearFocus();

        // Set search listener to trigger our search task
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.trim().isEmpty()) {
                    // Start the search operation
                    new SearchTask(MainActivity.this, query).execute();
                }
                return true;  // indicate that we handled the submission
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // No need to handle text changes for this operation
                return false;
            }
        });

        // Read and print JSON file from res/raw
        String jsonData = readJsonFromRawResource();
        Log.d("JSON_DATA", jsonData); // Log to check if data is read successfully

        // Request camera permission using the new method
        requestCameraPermission();
    }


    private String readJsonFromRawResource() {
        StringBuilder json = new StringBuilder();
        try {
            // Open the raw resource
            InputStream inputStream = getResources().openRawResource(R.raw.data);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line).append("\n");
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission granted
            onCameraPermissionGranted();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {
            // Show rationale and request permission
            Toast.makeText(this, "Camera permission is required to use the camera feature.", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // Directly request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    private void onCameraPermissionGranted() {
        Toast.makeText(this, "Camera permission granted.", Toast.LENGTH_SHORT).show();
        // Add any logic for when the permission is granted (e.g., open camera)
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onCameraPermissionGranted();
            } else {
                Toast.makeText(this, "Camera permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * AsyncTask to perform the search operation.
     */
    private class SearchTask extends AsyncTask<Void, Void, JSONObject> {
        private Context context;
        private String query;

        public SearchTask(Context context, String query) {
            this.context = context;
            this.query = query;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Show “Searching...” in the SearchView query text
            searchView.setQuery("Searching...", false);
            searchView.clearFocus();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                String sp = String.valueOf(System.currentTimeMillis() / 1000);
                String rc = "adlfskhoic";
                String newKey = md5(sp + rc + API_KEY);

                // Encrypt the package name using AES encryption
                String encryptedChars = aesEncrypt(PACKAGE_NAME, newKey);

                // Encode parameters
                String encodedQuery = URLEncoder.encode(query.trim(), StandardCharsets.UTF_8.toString());
                String encodedSp = URLEncoder.encode(sp, StandardCharsets.UTF_8.toString());
                String encodedRc = URLEncoder.encode(rc, StandardCharsets.UTF_8.toString());
                String encodedEc = URLEncoder.encode(encryptedChars, StandardCharsets.UTF_8.toString());

                // Construct the request URL
                String requestUrl = API_URL + "?ks=" + encodedQuery
                        + "&tsp=" + encodedSp
                        + "&rc=" + encodedRc
                        + "&ec=" + encodedEc;

                // Make the API request
                URL url = new URL(requestUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder content = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                connection.disconnect();

                Log.d("API_RESPONSE", content.toString());
                return new JSONObject(content.toString());

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            // Restore the original query text
            searchView.setQuery(query, false);
            if (result != null) {
                try {
                    int status = result.getInt("status");
                    if (status == 201) {
                        String urlRoute = result.getJSONObject("data").getString("url_route");
                        showDialog(context, urlRoute, "FOUND: " + query, "Enter now?", true);
                    } else if (status == 200) {
                        showDialog(context, null, "NO RESULT", query + " NOT FOUND", false);
                    } else {
                        showDialog(context, null, "ERROR", "Request parameters have errors", false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error parsing response", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Error: No response from server", Toast.LENGTH_SHORT).show();
            }
        }

        private String md5(String input) throws Exception {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xFF & b);
                while (hex.length() < 2) {
                    hex = "0" + hex;
                }
                hexString.append(hex);
            }
            return hexString.toString();
        }

        private String aesEncrypt(String data, String key) throws Exception {
            IvParameterSpec iv = new IvParameterSpec(key.substring(0, 16).getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        }

        private void showDialog(Context context, String url, String title, String message, boolean withOkButton) {
            // Update the DEFAULT_URL in shared preferences if a valid URL is provided.
            if (url != null && !url.isEmpty()) {
                SharedPreferences sharedPreferences = context.getSharedPreferences(SplashActivity.PREFS_NAME, Context.MODE_PRIVATE);
                sharedPreferences.edit().putString("DEFAULT_URL", url).apply();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.RoundedDialog);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setCancelable(true);

            if (withOkButton) {
                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                });
                builder.setNeutralButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    // Launch the WebPage activity (assuming this activity exists in your project)
                    Intent intent = new Intent(context, WebPage.class);
                    if (url != null) {
                        intent.putExtra("URL", url);
                    }
                    context.startActivity(intent);
                    if (context instanceof Activity) {
                        ((Activity) context).finish();
                    }
                });
            } else {
                builder.setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                });
            }

            AlertDialog alert = builder.create();
            alert.show();

            // Apply custom styling to the dialog window
            if (alert.getWindow() != null) {
                alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                alert.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dialog_background));

                WindowManager.LayoutParams params = alert.getWindow().getAttributes();
                params.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.8);
                alert.getWindow().setAttributes(params);
            }
        }
    }
}
