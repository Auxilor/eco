package com.willfp.eco.core.web;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Paste {
    /**
     * The contents.
     */
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
            String urly = "https://hastebin.com/documents";
            URL obj = new URL(urly);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");

            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(contents);
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
}
