package com.willfp.eco.core.web;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Paste {
    /**
     * The contents.
     */
    @Getter
    private final String contents;

    /**
     * Create a new paste.
     *
     * @param contents The contents.
     */
    public Paste(@NotNull final String contents) {
        this.contents = contents;
    }

    /**
     * Upload to hastebin and get a token.
     *
     * @return The token.
     */
    public String getHastebinToken() {
        try {
            String url = "https://hastebin.com/documents";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");

            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(URLEncoder.encode(contents, StandardCharsets.UTF_8));
            wr.flush();
            wr.close();

            BufferedReader iny = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String output;
            StringBuilder responseBuilder = new StringBuilder();

            while ((output = iny.readLine()) != null) {
                responseBuilder.append(output);
            }
            iny.close();

            String responseString = responseBuilder.toString();

            responseString = responseString.replace("{\"key\":\"", "");
            responseString = responseString.replace("\"}", "");

            return responseString;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Get paste from hastebin.
     *
     * @param token The token.
     * @return The paste.
     */
    public static Paste getFromHastebin(@NotNull final String token) {
        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL("https://hastebin.com/raw/" + token);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            try (var reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()))) {
                for (String line; (line = reader.readLine()) != null;) {
                    result.append(line);
                }
            }
            return new Paste(URLDecoder.decode(result.toString(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
