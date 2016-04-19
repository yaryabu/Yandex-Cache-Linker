package ru.yaryabu.yandex_cache_linker.logic;


import java.util.ArrayList;

import ru.yaryabu.yandex_cache_linker.model.Artist;

/**
 * Интерфейс для получения callback'а при запросе артистов из интернета.
 *
 * <p>Сделан, чтобы отделить ошибки, которые не обрататываются интерфейсе callback'а OkHttp {@link okhttp3.Callback} .
 * К примеру, в callback от OkHttp запрос со статус кодом 404 может не являться ошибкой и обрабоку этой логики приходится делать в Activity.
 *
 * <p>Все методы вызываются на UI thread.
 */
public interface ArtistQueryCallback {
    void onFailure(Error error);
    void onResponse(ArrayList<Artist> artists);
}
