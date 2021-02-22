package com.example.camera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class SavedataActivity extends AppCompatActivity {
    ImageView savedImage;
    Button saveButton;
    EditText edittext_date;
    EditText edittext_locate;
    EditText edittext_content;
    String currentPhotoPath;
    Uri imageUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savedata);
        savedImage = findViewById(R.id.savedImage);
        saveButton = findViewById(R.id.savebutton);
        edittext_date = findViewById(R.id.edittext_date);
        edittext_locate = findViewById(R.id.edittext_locate);
        edittext_content = findViewById(R.id.edittext_content);

        edittext_date.setNextFocusDownId(R.id.edittext_locate);
        edittext_locate.setNextFocusDownId(R.id.edittext_content);
        edittext_content.setNextFocusDownId(R.id.savebutton);




        String getCurrentPhotoPath = getIntent().getStringExtra("currentPhotoPath");
        File f = new File(getCurrentPhotoPath);
        savedImage.setImageURI(Uri.fromFile(f));

        verifyStoragePermission(this);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveButton.setVisibility(View.INVISIBLE);
                takeScreenShot(getWindow().getDecorView().getRootView(),"punch_jung");
                galleryAddPic();
                MyDatabaseHelper myDBhelper = new MyDatabaseHelper(SavedataActivity.this);
                myDBhelper.addList(edittext_date.getText().toString()+"_"+ edittext_locate.getText().toString()+"_"+edittext_content.getText().toString().trim());
                finish();

            }
        });



    }


    protected void takeScreenShot(View view, String fileName){
        OutputStream fileOutputStream;
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        int quality = 100;
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){//11버전 이상
            ContentResolver resolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME,timeStamp+".jpg");
            contentValues.put(MediaStore.Images.Media.MIME_TYPE,"image/jpg");
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH,Environment.DIRECTORY_PICTURES+"/Punch Collector");
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            imageUrl = imageUri;
            Log.d("currentPhotoPath",": "+imageUri.toString());
            try {
                fileOutputStream = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }else{

            try{
                File fileDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS+"/Punch collector");

                Log.d("directory123",fileDir.toString());
                if(!fileDir.exists()){
                    fileDir.mkdirs();
                }

                String path = fileDir.getAbsolutePath() + "/" + fileName + "-" + timeStamp + ".jpeg";


                File imageFile = new File(path);

                

                if (imageFile.exists()){//파일이 존재하면
                    imageFile.delete();//삭제
                }

                fileOutputStream = new FileOutputStream(imageFile);

                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                currentPhotoPath = imageFile.getAbsolutePath();
                Log.d("currentPhotoPath",": "+currentPhotoPath);

                Toast.makeText(getApplicationContext(), "사진이 저장되었습니다.", Toast.LENGTH_SHORT).show();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }




    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
            contentUri = imageUrl;
        }else{
            File f = new File(currentPhotoPath);
            contentUri = Uri.fromFile(f);
        }
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private static final int REQUEST_EXTERNAL_STORAGE =1;

    private static String[] PERMISSION_STORGE = {
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermission(Activity activity){

        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,PERMISSION_STORGE,REQUEST_EXTERNAL_STORAGE);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "사진 저장을 취소하였습니다.", Toast.LENGTH_SHORT).show();
    }
}