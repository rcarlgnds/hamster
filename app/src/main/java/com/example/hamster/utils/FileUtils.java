package com.example.hamster.utils;

import android.content.Context;
import android.net.Uri;
import android.provider.OpenableColumns;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import android.database.Cursor;

public class FileUtils {


    public static File getFile(Context context, Uri uri) {
        if (uri == null) return null;

        String filePath = null;
        if ("content".equals(uri.getScheme())) {
            try {
                // Try to get file from content resolver (e.g., gallery images)
                String fileName = null;
                try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (nameIndex != -1) {
                            fileName = cursor.getString(nameIndex);
                        }
                    }
                }

                if (fileName == null) {
                    fileName = "temp_file_" + System.currentTimeMillis();
                }

                File tempFile = new File(context.getCacheDir(), fileName);
                try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
                     FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                    if (inputStream != null) {
                        byte[] buffer = new byte[1024];
                        int read;
                        while ((read = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, read);
                        }
                        filePath = tempFile.getAbsolutePath();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Fallback to simpler path if content resolver fails
                filePath = uri.getPath();
            }
        } else if ("file".equals(uri.getScheme())) {
            filePath = uri.getPath();
        }

        if (filePath != null) {
            return new File(filePath);
        }
        return null;
    }
}