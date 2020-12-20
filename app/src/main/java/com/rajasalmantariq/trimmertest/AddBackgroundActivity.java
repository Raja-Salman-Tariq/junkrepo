package com.rajasalmantariq.trimmertest;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.PathUtils;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;

public class AddBackgroundActivity extends AppCompatActivity {

    Button btn;
    String auFilePath, viFilePath, dest;
    String [] cmd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_background);
        btn=findViewById(R.id.addaudio);

        openVideo();
        selectAudioFile();
        getDest();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    addAudio();
                } catch (FFmpegNotSupportedException | FFmpegCommandAlreadyRunningException e) {
                    e.printStackTrace();
                }
            }
        });





    }

    private void getDest() {
        File dirUpr= new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES),"/Obstructy");

//            File dir=new File(dest.getPath(),"/Obstructy");
        if (! dirUpr.exists()){
            if (! dirUpr.mkdirs()){
                Log.d("myvid", "failed to create directory");
                return;
            }
            else{
                Log.d("myvid", "dir made at abs: "+dirUpr.getAbsolutePath()
                        +", w path: "+dirUpr.getPath());
//                        +", w can path: "+dir.getCanonicalPath());
            }
        }
        else{
            Log.d("myvid", "dir existed at abs: "+dirUpr.getAbsolutePath()
                    +", w path: "+dirUpr.getPath());
//                    +", w can path: "+dir.getCanonicalPath());
        }

        File dir=new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES)+"/Obstructy","/Cropped");

        if (! dir.exists()){
            if (! dir.mkdirs()){
                Log.d("myvid", "failed to create directory");
                return;
            }
            else{
                Log.d("myvid", "dir made at abs: "+dir.getAbsolutePath()
                        +", w path: "+dir.getPath());
//                        +", w can path: "+dir.getCanonicalPath());
            }
        }
        else{
            Log.d("myvid", "dir existed at abs: "+dir.getAbsolutePath()
                    +", w path: "+dir.getPath());
//                    +", w can path: "+dir.getCanonicalPath());
        }

        dest=dir.getAbsolutePath();

    }

    void openVideo(){
        Intent i=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("video/*");
        startActivityForResult(i, 100);
    }

    private void selectAudioFile() {

        Intent i=new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        i.setType("audio/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(i, 3360);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_OK && requestCode==3360){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                auFilePath= getPath(getApplicationContext() , data.getData() );
                Log.d("add", "onActivityResult: "+auFilePath);
            }
            else {
                Log.d("add", "onActivityResult: Error, version issues...");
                finish();
            }
        }

        if (requestCode==100 && resultCode==RESULT_OK){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                viFilePath= getPath(getApplicationContext() , data.getData() );
                Log.d("add", "onActivityResult: "+viFilePath);
            }
            else {
                Log.d("add", "onActivityResult: Error, version issues...");
                finish();
            }
        }
    }

    private void addAudio() throws FFmpegNotSupportedException, FFmpegCommandAlreadyRunningException {
        FFmpeg obj=FFmpeg.getInstance(AddBackgroundActivity.this);
        obj.loadBinary(new LoadBinaryResponseHandler(){

            @Override
            public void onFailure() {
                super.onFailure();
            }

            @Override
            public void onSuccess() {
                super.onSuccess();
            }
        });

        cmd = new String[] {"-i", viFilePath, "-i", auFilePath,
                            "-c", "copy", "-map", "0:0", "-map", "1:0",
                            "-shortest", dest+"/xyz.mp4"};
        obj.execute(cmd, new ExecuteBinaryResponseHandler(){
            @Override
            public void onSuccess(String message) {
                super.onSuccess(message);
            }

            @Override
            public void onProgress(String message) {
                super.onProgress(message);
            }

            @Override
            public void onFailure(String message) {
                super.onFailure(message);
//                pctg.setValue(100);
            }

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });

        Toast.makeText(AddBackgroundActivity.this, "Done", Toast.LENGTH_SHORT).show();
    }


    // Implementation of the getPath() method and all its requirements is taken from the StackOverflow Paul Burke's answer: https://stackoverflow.com/a/20559175/5426539
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


}