package com.equityhub.application.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.equityhub.application.R;
import com.equityhub.application.models.Section;
import com.equityhub.application.models.SectionsData;
import com.google.gson.Gson;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private TextView headingText;
    private TextView paragraph1Text;
    private TextView paragraph2Text;
    private ImageView sectionImage, brainIcon, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Initialize Views
        TextView titleText = findViewById(R.id.titleText);
        headingText = findViewById(R.id.headingText);
        paragraph1Text = findViewById(R.id.paragraph1Text);
        paragraph2Text = findViewById(R.id.paragraph2Text);
        sectionImage = findViewById(R.id.sectionImage);
        brainIcon = findViewById(R.id.brainIcon);
        backButton = findViewById(R.id.backButton); // now an ImageView

        // Set up the back button to finish the activity
        backButton.setOnClickListener(v -> finish());

        // Get ID from Intent
        Intent intent = getIntent();
        String sectionId = intent.getStringExtra("SECTION_ID");

        // Load JSON and find the corresponding section
        Section section = getSectionById(sectionId);
        if (section != null) {
            // Set data to views
            titleText.setText(section.getTitle());
            headingText.setText(section.getHeading());
            if (section.getParagraphs() != null && section.getParagraphs().size() >= 2) {
                paragraph1Text.setText(section.getParagraphs().get(0));
                paragraph2Text.setText(section.getParagraphs().get(1));
            }

            // Set image dynamically (Assuming images are in the drawable folder)
            int imageResId = getResources().getIdentifier(section.getImage(), "drawable", getPackageName());
            if (imageResId != 0) {
                sectionImage.setImageResource(imageResId);
            }
        }

        // Enable ActionBar up button if needed
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle ActionBar back button click
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Section getSectionById(String sectionId) {
        String json = loadJSONFromRaw();
        if (json == null) return null;

        Gson gson = new Gson();
        SectionsData data = gson.fromJson(json, SectionsData.class);
        List<Section> sections = data.getSections();

        for (Section section : sections) {
            if (section.getId().equals(sectionId)) {
                return section;
            }
        }
        return null;
    }

    private String loadJSONFromRaw() {
        try {
            InputStream is = getResources().openRawResource(R.raw.data);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Log.e("DetailActivity", "Error loading JSON", e);
            return null;
        }
    }
}
