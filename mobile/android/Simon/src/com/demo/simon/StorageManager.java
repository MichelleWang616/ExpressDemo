
package com.demo.simon;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;

public class StorageManager {
    private static final long MIN_AVAILABLE_EXTERNAL_MEMORY_SIZE = 1048576 * 50; // 50MB

    private static String sDownloadCompanyPath = null;

    public static void initialize(Context context) {
        File appDataFile = context.getFilesDir();
        sDownloadCompanyPath = appDataFile + "/company";
    }

    public static File getCompanyFolder() {
        return getDir(sDownloadCompanyPath);
    }

    public static File getDownloadedCompanyProfile() {
        return new File(getCompanyFolder(), "profile.json");
    }

    public static File getCompanyLogoFile(Context context, String companyId) {
        return new File(getCompanyFolder(), companyId + ".jpg");
    }

    public static boolean isExternalStorageAvailable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        }
        return false;
    }

    public static boolean isAvailableExternalStorageSizeSmall() {
        return (getAvailableExternalStorageSize() < MIN_AVAILABLE_EXTERNAL_MEMORY_SIZE);
    }

    public static long getAvailableExternalStorageSize() {
        if (!isExternalStorageAvailable()) {
            return 0;
        }

        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    public static String readTextFile(File file) throws IOException {
        InputStream stream = null;
        StringBuilder sb = new StringBuilder();
        try {
            stream = new FileInputStream(file);
            readString(stream, sb);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
        }
        return sb.toString();
    }

    public static void writeStringToFile(File file, String string) throws IOException {
        BufferedWriter fileWriter = null;
        try {
            fileWriter = new BufferedWriter(new FileWriter(file), 8192);
            fileWriter.write(string);
        } finally {
            if (fileWriter != null) {
                fileWriter.close();
            }
        }
    }

    public static Bitmap loadImage(Context context, Uri imageUri) throws IOException {
        Bitmap bmp = null;
        InputStream istream = null;
        try {
            istream = context.getContentResolver().openInputStream(imageUri);
            bmp = BitmapFactory.decodeStream(istream);
            if (bmp == null) {
                throw new IOException("Cannot decode the bitmap from the input stream.");
            }
        } catch (SecurityException se) {
            se.printStackTrace();
            throw new IOException(se);
        } finally {
            if (istream != null) {
                try {
                    istream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bmp;
    }

    private static void readString(InputStream stream, StringBuilder sb) throws IOException {
        BufferedReader buf = new BufferedReader(new InputStreamReader(stream), 8192);
        String readString = null;
        try {
            while ((readString = buf.readLine()) != null) {
                sb.append(readString);
            }
        } finally {
            buf.close();
        }
    }

    private static File getDir(String path)
    {
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }
        return dir;
    }
}
