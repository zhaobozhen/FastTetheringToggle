package com.absinthe.aptest;

import android.app.Application;
import android.content.Context;

import me.weishu.reflection.Reflection;

public class App extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Reflection.unseal(base);
    }
}
