package com.example.myapplication;

import android.app.Application;

import androidx.annotation.NonNull;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmApp extends Application {

    @Override
    public void onCreate() {

        super.onCreate();
        Realm.init(this);

        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .name("todo.db")
                .deleteRealmIfMigrationNeeded()
                .schemaVersion(0)
                .allowWritesOnUiThread(true)
                .allowQueriesOnUiThread(true)
                .build();

        Realm.setDefaultConfiguration(configuration);
    }
}
