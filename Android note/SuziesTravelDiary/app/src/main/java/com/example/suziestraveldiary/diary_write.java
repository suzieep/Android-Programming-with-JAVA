package com.example.suziestraveldiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class diary_write extends AppCompatActivity {
    String msg = " ------- : ";
    private static final int REQUEST_CODE = 0;
    private ImageView imageView;
    private String uri;
    Button create2Button;

    private void setImage(Uri uri) {
        try {
            InputStream in = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private final int GALLERY_CODE=1112;


    private void selectGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_CODE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_write);
        System.out.println("Android App : On Create");
        imageView = findViewById(R.id.image_);

       imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        create2Button = (Button) findViewById(R.id.button3);
        create2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPost();
                Intent intent2 = new Intent(getApplicationContext(),MainActivity.class);
                startActivityForResult(intent2,1001);
            }
        });
    }

    public void addPost() {
        ContentValues addValues = new ContentValues();
        System.out.println("addpost 들어");
        addValues.put(MyContentProvider._TITLE, ((EditText) findViewById(R.id.title_text_)).getText().toString());
        addValues.put(MyContentProvider._DETAILS, ((EditText) findViewById(R.id.detail_text_)).getText().toString());
        addValues.put(MyContentProvider._IMAGE, uri);
        addValues.put(MyContentProvider._LATITUDE, ((EditText) findViewById(R.id.latitude_text_)).getText().toString());
        addValues.put(MyContentProvider._LONGITUDE, ((EditText) findViewById(R.id.longitude_text_)).getText().toString());
        getContentResolver().insert(MyContentProvider.CONTENT_URI, addValues);
        System.out.println("토스트 전");

        Toast.makeText(getBaseContext(), "Record Added", Toast.LENGTH_LONG).show();
        System.out.println("나가기 직전 s");
    }
/*
    public void getPosts(View view) {
        String[] columns = new String[]{"_title", "_details", "_image", "_latitude", "_longitude"};
        Cursor c = getContentResolver().query(MyContentProvider.CONTENT_URI, columns, null, null, null, null);
        if (c != null) {
            EditText editMultipleText = findViewById(R.id.longitudeText);
            editMultipleText.setText("");
            while (c.moveToNext()) {
                String title = c.getString(0);
                String detail = c.getString(1);
                String image = c.getString(2);
                String latitude = c.getString(3);
                String longitude = c.getString(4);
                editMultipleText.append("id: " + title + "\n number: " + detail + "\n name: " + image + "\n la/long: " + latitude + longitude+"\n");
            }
            editMultipleText.append("\n Total : " + c.getCount());
            c.close();
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLERY_CODE:
                    sendPicture(data.getData());
                    uri = getRealPathFromURI(data.getData());//갤러리에서 가져오기
                    break;

                default:
                    break;
            }
        }
    }

    private void sendPicture(Uri imgUri) {
        String imagePath = getRealPathFromURI(imgUri); // path 경로
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        //경로를 통해 비트맵으로 전환
        imageView.setImageBitmap(rotate(bitmap, exifDegree));
        // 이미지 뷰에 비트맵 넣기
    }

    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap src, float degree) { // Matrix 객체 생성
        Matrix matrix = new Matrix(); // 회전 각도 셋팅 matrix.postRotate(degree); // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    private String getRealPathFromURI(Uri contentUri) {
        int column_index = 0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }
        return cursor.getString(column_index);
    }


}
