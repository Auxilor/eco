package com.willfp.eco.core.web;

import com.willfp.eco.core.Eco;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * A string that works with internet access, specifically
 * to upload and download from hastebin.
 */
public class Paste {
    /**
     * The contents.
     */
    private final String contents;

    /**
     * The host.
     */
    private final String host;

    /**
     * Create a new paste.
     *
     * @param contents The contents.
     */
    public Paste(@NotNull final String contents) {
        this(contents, "https://paste.willfp.com");
    }

    /**
     * Create a new paste.
     *
     * @param contents The contents.
     * @param host     The host.
     */
    public Paste(@NotNull final String contents,
                 @NotNull final String host) {
        this.contents = contents;
        this.host = host;
    }

    /**
     * Upload to hastebin and get a token.
     * <p>
     * Runs asynchronously to avoid hangups.
     *
     * @param callback The consumer to accept the response token.
     */
    public void getHastebinToken(@NotNull final Consumer<String> callback) {
        Eco.get().getEcoPlugin().getScheduler().runAsync(() -> {
            try {
                byte[] postData = URLEncoder.encode(contents, StandardCharsets.UTF_8).getBytes(StandardCharsets.UTF_8);
                int postDataLength = postData.length;

                String requestURL = this.host + "/documents";
                URL url = new URL(requestURL);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setInstanceFollowRedirects(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("User-Agent", "eco-Hastebin");
                conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                conn.setUseCaches(false);

                String response;
                DataOutputStream wr;

                wr = new DataOutputStream(conn.getOutputStream());
                wr.write(postData);
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                response = reader.readLine();

                assert response != null;

                if (response.contains("\"key\"")) {
                    response = response.substring(response.indexOf(":") + 2, response.length() - 2);

                    callback.accept(response);
                }
            } catch (IOException e) {
                callback.accept(e.getMessage());
            }
        });
    }

    /**
     * Get paste from hastebin.
     *
     * @param token The token.
     * @return The paste.
     */
    public static Paste getFromHastebin(@NotNull final String token) {
        return getFromHastebin(token, "https://paste.willfp.com");
    }

    /**
     * Get paste from hastebin.
     *
     * @param token The token.
     * @param host  The host.
     * @return The paste.
     */
    public static Paste getFromHastebin(@NotNull final String token,
                                        @NotNull final String host) {
        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL(host + "/raw/" + token);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            try (var reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()))) {
                for (String line; (line = reader.readLine()) != null; ) {
                    result.append(line);
                }
            }
            return new Paste(URLDecoder.decode(result.toString(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get the contents.
     *
     * @return The contents.
     */
    public String getContents() {
        return this.contents;
    }
}
