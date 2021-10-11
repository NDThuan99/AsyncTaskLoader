package com.example.asynctaskloader;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {
    private static final String LOG_TAG = "XXX";
    //trả về 1 đối tương JSOn
    private static final String BOOK_BASE_URL =  "https://www.googleapis.com/books/v1/volumes?";

    private static final String MAX_RESULTS = "maxResults";
    private static final String PRINT_TYPE = "printType";
    private static final String QUERY_PARAM = "q";

    public static String getBookInfo(String query){
        //biến để kết nối với internet
        HttpURLConnection urlConnection = null;
        //biến để đọc dữ liệu trả về
        BufferedReader reader = null;
        //biến để lưu dữ liệu trả về
        String bookJSONString = "";
        try{
            Uri builtURI = Uri.parse(BOOK_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, query)
                    .appendQueryParameter(MAX_RESULTS, "10")
                    .appendQueryParameter(PRINT_TYPE, "books")
                    .build();
            Log.d("XXX", "builtURI: "+builtURI.toString());
            URL requestURL = new URL(builtURI.toString());
            //mở kết nối URL và thực hiện yêu cầu
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
            if (builder.length() == 0) {
                return null;
            }
            bookJSONString = builder.toString();
        }catch (Exception e){

        }
        finally {
            //đóng cả kết nối và BufferedReader
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Log.d(LOG_TAG, bookJSONString);
        return bookJSONString;
    }
}
