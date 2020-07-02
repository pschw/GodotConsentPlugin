package magicnut.godotplugin.consentgdpr;

import android.app.Activity;
import android.provider.Settings;
import android.util.Log;

import com.google.ads.consent.ConsentInformation;

import java.io.UnsupportedEncodingException;

import static magicnut.godotplugin.consentgdpr.ConsentPlugin.PLUGIN_NAME;

public class TestDevice {
    private Activity activity;

    public TestDevice(Activity activity) {
        this.activity = activity;
    }

    public void setupTestDeviceIfDebugEnabled() {
        if (BuildConfig.DEBUG) {
            Log.d(PLUGIN_NAME, "Running in debug mode");
            String deviceId = getDeviceId();
            Log.d(PLUGIN_NAME, "Generated device id: " + deviceId);
            ConsentInformation.getInstance(activity).addTestDevice(deviceId);
            Log.d(PLUGIN_NAME, "Added device as test device");
        } else {
            Log.d(PLUGIN_NAME, "Running in release mode");
        }
    }

    private String getDeviceId() {
        String android_id = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        return getMd5(android_id).toUpperCase();
    }

    private String getMd5(String md5) {
        // TOOD use intellij recommendations ;)
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes("UTF-8"));
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            Log.d(PLUGIN_NAME, e.getLocalizedMessage());
        } catch (UnsupportedEncodingException ex) {
            Log.d(PLUGIN_NAME, ex.getLocalizedMessage());
        }
        Log.d(PLUGIN_NAME, "Could not produce md5");
        return null;
    }
}
