package com.app.pccooker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class HelpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewById(R.id.toolbar).setOnClickListener(v -> finish());

        findViewById(R.id.btnWhatsApp).setOnClickListener(v -> {
            String phone = "917897892656";  // update to support number
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://wa.me/" + phone));
            startActivity(i);
        });

        findViewById(R.id.btnEmail).setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                    Uri.fromParts("mailto", "support@pccooker.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Help Needed");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi, I need assistance with...");
            startActivity(Intent.createChooser(emailIntent, "Send Email..."));
        });
    }
}
