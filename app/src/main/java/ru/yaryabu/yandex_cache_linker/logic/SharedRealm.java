package ru.yaryabu.yandex_cache_linker.logic;

import io.realm.Realm;

/**
 * Общие Realm'ы приложения. Класс необходим, чтобы избежать путаницы с инициализацией и закрытием всех Realm на разных тредах
 */
public class SharedRealm {
    public static Realm uiThreadRealm;
}
