
package com.demo.simon.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Utility {

    private final static String PREFERENCE_COMPANY_PROFILE_MD5 = "preference.company.profile.md5";

    public final static String INTENT_DATA_REQUEST_ID = "intent.data.request.id";

    public static String getDeviceSerial() {
        return android.os.Build.SERIAL;
    }

    // TODO
    public static long getAllowedMaxDistance() {
        return -1;
    }

    public static String generateMD5OfString(String str) {
        MessageDigest digest;
        try
        {
            digest = MessageDigest.getInstance("MD5");

        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            return null;
        }

        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = digest.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    public static void saveCompanyProfileMD5Checksum(Context context, String md5) {
        saveStringValue(context, PREFERENCE_COMPANY_PROFILE_MD5, md5);
    }

    public static String getCompanyProfileMD5Checksum(Context context) {
        return getStringValue(context, PREFERENCE_COMPANY_PROFILE_MD5, "");
    }

    private static Editor getEditor(Context context)
    {
        return getPreferences(context).edit();
    }

    private static SharedPreferences getPreferences(Context context)
    {
        return context.getSharedPreferences("com.demo.simon", Context.MODE_PRIVATE);
    }

    private static void saveStringValue(Context context, String key, String value) {
        Editor editor = getEditor(context);
        editor.putString(key, value);
        editor.commit();
    }

    private static String getStringValue(Context context, String key, String defValue) {
        return getPreferences(context).getString(key, defValue);
    }
}
