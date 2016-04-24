package ru.yaryabu.yandex_cache_linker.logic;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Общие Realm'ы приложения. Класс необходим, чтобы избежать путаницы с инициализацией и закрытием всех Realm на разных тредах
 */
public class SharedRealm {
    public static Realm uiThreadRealm;

    public static void configureRealmOnLaunch(Context uiThreadContext) {
        RealmConfiguration config = new RealmConfiguration.Builder(uiThreadContext)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        uiThreadRealm = Realm.getDefaultInstance();
    }
}
