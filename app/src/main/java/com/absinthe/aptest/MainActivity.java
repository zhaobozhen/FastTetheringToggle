package com.absinthe.aptest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 判断是否有WRITE_SETTINGS权限if(!Settings.System.canWrite(this))
                    if (!Settings.System.canWrite(MainActivity.this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                                Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startTethering(true);
                        } else {
                            startTetheringPreO(true);
                        }
                    }
                } else {
                    startTetheringPreO(true);
                }
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 判断是否有WRITE_SETTINGS权限if(!Settings.System.canWrite(this))
                    if (!Settings.System.canWrite(MainActivity.this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                                Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startTethering(false);
                        } else {
                            startTetheringPreO(false);
                        }
                    }
                } else {
                    startTetheringPreO(false);
                }
            }
        });

    }

    private void startTethering(boolean flag) {
        Object mConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE);

        if (mConnectivityManager != null) {
            try {
                Field[] arrayOfField = mConnectivityManager.getClass().getDeclaredFields();

                for (Field field : arrayOfField) {
                    if (field.getName().equals("mService")) {
                        field.setAccessible(true);
                        mConnectivityManager = field.get(mConnectivityManager);
                        break;
                    }
                }

                ResultReceiver dummyResultReceiver = new ResultReceiver(null);

                if (flag) {
                    try {
                        Method startTetheringMethod;
                        if (mConnectivityManager != null) {
                            startTetheringMethod = mConnectivityManager.getClass().getDeclaredMethod("startTethering",
                                    Integer.TYPE,
                                    ResultReceiver.class,
                                    Boolean.TYPE);

                            startTetheringMethod.invoke(mConnectivityManager,
                                    0,
                                    dummyResultReceiver,
                                    false);
                        }

                    } catch (NoSuchMethodException e) {
                        @SuppressLint("SoonBlockedPrivateApi")
                        Method startTetheringMethod = mConnectivityManager.getClass().getDeclaredMethod("startTethering",
                                Integer.TYPE,
                                ResultReceiver.class,
                                Boolean.TYPE,
                                String.class);

                        startTetheringMethod.invoke(mConnectivityManager,
                                0,
                                dummyResultReceiver,
                                false,
                                getPackageName());
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Method startTetheringMethod;
                        if (mConnectivityManager != null) {
                            startTetheringMethod = mConnectivityManager.getClass().getDeclaredMethod("stopTethering",
                                    Integer.TYPE);

                            startTetheringMethod.invoke(mConnectivityManager, 0);
                        }

                    } catch (NoSuchMethodException e) {
                        @SuppressLint("SoonBlockedPrivateApi")
                        Method startTetheringMethod = mConnectivityManager.getClass().getDeclaredMethod("stopTethering",
                                Integer.TYPE,
                                String.class);

                        startTetheringMethod.invoke(mConnectivityManager,
                                0,
                                getPackageName());
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startTetheringPreO(boolean flag) {
        WifiManager localWifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (localWifiManager != null) {
            Method[] arrayOfMethod = localWifiManager.getClass().getDeclaredMethods();
            for (Method method : arrayOfMethod) {
                if (method.getName().equals("setWifiApEnabled")) {
                    try {
                        method.invoke(localWifiManager, null, flag);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

}
