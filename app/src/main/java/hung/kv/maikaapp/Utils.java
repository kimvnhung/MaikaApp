package hung.kv.maikaapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.TypedValue;

public class Utils {
    public static float dp2px(Context context, float dp){
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
    }

    public static void SaveLastAccount(Activity activity, String username, String password){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username", username);
        editor.putString("password",password);
        editor.apply();
    }

    public static String[] GetLastAccount(Activity activity) {
        String[] result = new String[2];
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);

        result[0] = sharedPref.getString("username","");
        result[1] = sharedPref.getString("password","");

        return result;
    }

    public static void SavePreference(Activity activity, String key, String value){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String GetPreference(Activity activity, String key,String defaultValue){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);

        return sharedPref.getString(key,defaultValue);
    }

    public static void SavePreference(Activity activity, String key, boolean value){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean GetPreference(Activity activity, String key,boolean defaultValue){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);

        return sharedPref.getBoolean(key,defaultValue);
    }
}
