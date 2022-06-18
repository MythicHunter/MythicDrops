package com.spiritlight.mythicdrops;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class HTTP {
    static final OkHttpClient client = new OkHttpClient();

    public static String get(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (NullPointerException | IOException npe) {
            npe.printStackTrace();
            return "Error.NPE";
        }
    }
}
