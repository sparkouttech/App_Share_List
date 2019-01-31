package com.shareapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.http.SslError;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class ShareActivity extends AppCompatActivity implements View.OnClickListener {

    AppCompatButton btn_view;
    private boolean webView = false, imageVar = false, textVar = false;

    AppCompatTextView textContent;
    String sharedText;

    AppCompatImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        btn_view = findViewById(R.id.btn_view);
        image = findViewById(R.id.image);
        textContent = findViewById(R.id.textContent);

        final Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {

                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
        }

        btn_view.setOnClickListener(this);


    }


    public void handleSendText(Intent intent) {
        sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {

            if (sharedText.contains("https") || sharedText.contains("http")) {
                webView = true;
                textVar = false;
                imageVar = false;
            } else {
                webView = false;
                textVar = true;
                imageVar = false;
            }

            Log.e("Nive ", "handleSendText: " + sharedText);
            // Update UI to reflect text being shared
        }
    }

    public void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

        Log.e("Nive ", "handleSendImage: URI " + imageUri);
        Log.e("Nive ", "handleSendImage:intent.getData() " + intent.getData());
        if (imageUri != null) {
            webView = false;
            textVar = false;
            imageVar = true;
           /* String filePath = ImageFilePath.getPath(ShareActivity.this, imageUri);
            Log.e("Nive ", "handleSendImage: " + filePath);
*/
            Picasso.with(ShareActivity.this).load(imageUri).error(R.drawable.ic_action_name).into(image);

//            Picasso.with(ShareActivity.this).load(new File(filePath)).error(R.drawable.ic_action_name).into(image);
            // Update UI to reflect image being shared
        }
    }


    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null,
                null, null, null);

        if (cursor == null) { // Source is Dropbox or other similar local file
            // path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor
                    .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }

        Log.e("Nive ", "getRealPathFromURI: " + result);
        return result;
    }


    public void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            webView = false;
            textVar = false;
            imageVar = true;

            // Update UI to reflect multiple images being shared
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn_view:

                Log.e("Nive ", "onClick:webView " + webView);
                Log.e("Nive ", "onClick:textVar " + textVar);


                if (webView) {
                    Intent intent1 = new Intent(ShareActivity.this, WebViewActivity.class);
                    intent1.putExtra("URL", sharedText);
                    startActivity(intent1);
                } else {

                    if (textVar) {

                        textContent.setVisibility(View.VISIBLE);
                        image.setVisibility(View.GONE);


                    } else {

                        textContent.setVisibility(View.GONE);
                        image.setVisibility(View.VISIBLE);


                    }
                }

                break;
        }
    }
}
