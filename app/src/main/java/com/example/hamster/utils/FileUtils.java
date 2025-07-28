// File: app/src/main/java/com/example/hamster/utils/FileUtils.java
package com.example.hamster.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileUtils {

    /**
     * Mengubah Uri menjadi objek File dengan menyalinnya ke cache directory aplikasi.
     * @param context Context aplikasi.
     * @param uri Uri dari file yang akan dikonversi.
     * @return Objek File, atau null jika terjadi error.
     */
    public static File getFile(Context context, Uri uri) {
        if (uri == null) return null;

        String fileName = getFileName(context, uri);
        File file = new File(context.getCacheDir(), fileName);

        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(file)) {

            if (inputStream == null) return null;

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return file;

        } catch (Exception e) {
            Log.e("FileUtils", "Gagal mengubah Uri menjadi File", e);
            return null;
        }
    }

    /**
     * Helper method untuk mendapatkan nama file asli dari sebuah Uri.
     */
    private static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}