package ru.yaryabu.yandex_cache_linker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.yaryabu.yandex_cache_linker.logic.Const;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class YandexArtistsUnitTest {

    private static final OkHttpClient client = new OkHttpClient();
    private static Response response;
    private static String jsonString;

    @BeforeClass
    public static void setupClass() {
        Request request = new Request.Builder()
                .url(Const.ARTISTS_URL)
                .build();

        try {
            response = client.newCall(request).execute();
            jsonString = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Ошибка запроса");
        }
    }

    @Test
    public void response_statusCodeCorrect() throws Exception {
        assertEquals(200, response.code());
    }

    @Test
    public void response_isJSONArray() throws Exception {
        JSONArray jArray = new JSONArray(jsonString);
        assertTrue(jArray.getClass() == JSONArray.class);
    }

    @Test
    public void response_checkFieldTypes() throws Exception {
        JSONArray jArray = new JSONArray(jsonString);

        for (int i = 0; i < jArray.length(); i++) {
            JSONObject jObject = jArray.getJSONObject(i);
            assertTrue(Long.class.isInstance(jObject.getLong("id")));
            assertTrue(String.class.isInstance(jObject.getString("name")));
            assertTrue(Integer.class.isInstance(jObject.getInt("tracks")));
            assertTrue(Integer.class.isInstance(jObject.getInt("albums")));
            assertTrue(String.class.isInstance(jObject.getString("description")));
            assertTrue(String.class.isInstance(jObject.getJSONObject("cover").getString("small")));
            assertTrue(String.class.isInstance(jObject.getJSONObject("cover").getString("big")));
            try {

                String lingString = jObject.getString("link");
                assertTrue(String.class.isInstance(lingString));
            } catch (JSONException e) {
                // link - необязательный параметр. Есть не у всех объектов.
            }

            JSONArray genresJsonArray = jObject.getJSONArray("genres");
            assertTrue(genresJsonArray.getClass() == JSONArray.class);
            for (int j = 0; j < genresJsonArray.length(); j++) {
                assertTrue(String.class.isInstance(genresJsonArray.getString(j)));
            }
        }
    }

}