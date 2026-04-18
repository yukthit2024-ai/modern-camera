package com.vypeensoft.videorecorder;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StorageHelper {
    private static final String TAG = "StorageHelper";
    private static final String FOLDER_NAME = "vdo";

    public static String generateFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return "VID_" + timeStamp + ".mp4";
    }

    public static Uri createVideoUri(Context context, String fileName) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Video.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + File.separator + FOLDER_NAME);
        } else {
            // For legacy storage, we might need to manually handle the path or let MediaStore handle it
            // However, the requirement is /sdcard/Downloads/vdo/
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File vdoDir = new File(downloadsDir, FOLDER_NAME);
            if (!vdoDir.exists()) {
                vdoDir.mkdirs();
            }
            File videoFile = new File(vdoDir, fileName);
            values.put(MediaStore.Video.Media.DATA, videoFile.getAbsolutePath());
        }

        return context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
    }

    /**
     * For API < 29, returns a File object in /sdcard/Downloads/vdo/
     */
    public static File getOutputVideoFile() {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File vdoDir = new File(downloadsDir, FOLDER_NAME);
        if (!vdoDir.exists()) {
            if (!vdoDir.mkdirs()) {
                Log.e(TAG, "Failed to create directory: " + vdoDir.getAbsolutePath());
                return null;
            }
        }
        return new File(vdoDir, generateFileName());
    }
}
