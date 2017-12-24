package com.ashutosh.ndkpixels;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RESULT_LOAD_IMG = 111;
    private static final String TAG = MainActivity.class.getSimpleName();

    // Load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private RecyclerView rvContent;
    private ContentAdapter contentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        infoText = (TextView) findViewById(R.id.sample_text);

        Button galleryBtn = (Button) findViewById(R.id.btn_pick_img);
        galleryBtn.setOnClickListener(this);

        contentAdapter = new ContentAdapter();

        rvContent = (RecyclerView) findViewById(R.id.rv_content);
        rvContent.setLayoutManager(new LinearLayoutManager(this));
        rvContent.setAdapter(contentAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pick_img:
                pickImage();
                break;
        }
    }

    private void pickImage() {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImageUrl = data.getData();

                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImageUrl,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
//                ImageView imgView = (ImageView) findViewById(R.id.imgView);
                // Set the Image in ImageView after decoding the String
//                imgView.setImageBitmap(BitmapFactory
//                        .decodeFile(imgDecodableString));
//                imgView.setVisibility(View.VISIBLE);

                if(contentAdapter != null){
                    contentAdapter.resetData();
                    contentAdapter.addImage(BitmapFactory.decodeFile(imgDecodableString));
                }

                final String imgUrl = Utils.getPath(this, selectedImageUrl);
                Log.d(TAG, "Img url : " + imgUrl);

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        processImage(imgUrl);
                    }
                });

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }

    public void showGraphs(int freqA[], int freqR[], int freqG[], int freqB[]){
        Log.d(TAG, "A=" + freqA.length + ", R=" + freqB.length + ", G=" + freqG.length + ", B=" + freqB.length);

        if(contentAdapter != null){
            contentAdapter.addItem(new ItemContent(freqA, Color.GRAY, "Alpha Frequency Graph"));
            contentAdapter.addItem(new ItemContent(freqR, Color.RED, "Red Frequency Graph"));
            contentAdapter.addItem(new ItemContent(freqG, Color.GREEN, "Green Frequency Graph"));
            contentAdapter.addItem(new ItemContent(freqB, Color.BLUE, "Blue Frequency Graph"));

            contentAdapter.notifyDataSetChanged();
        }

    }

    public void showGraphs(int freqA[]){
        Log.d(TAG, "A=" + freqA.length);

//        for(int i = 0; i < freqA.length; i++){
//            Log.d(TAG, "i = " + i + ", A = " + freqA[i]);
//        }

        if(contentAdapter != null){
            contentAdapter.addItem(new ItemContent(freqA, Color.GRAY, "Frequency Alpha Graph"));
            contentAdapter.notifyDataSetChanged();
        }
    }

    /* Native method to process image */
    public native void processImage(String imgUrl);

//        private void pickImgFromGallery(){
//        Intent intent = new Intent();
//        // Show only images, no videos or anything else
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        // Always show the chooser (if there are multiple options available)
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
//    }
}
