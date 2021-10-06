package com.example.asynctaskloader;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class FetchBook extends AsyncTask<String, Void, String> {

    private WeakReference<TextView> mTitleText;
    private WeakReference<TextView> mAuthorText;

    public FetchBook(TextView mTitleText, TextView mAuthorText) {
        this.mTitleText = new WeakReference<>(mTitleText);
        this.mAuthorText = new WeakReference<>(mAuthorText);
    }

    @Override
    protected String doInBackground(String... strings) {
        Log.v("XXX", "doInBackground: "+strings[0]);
        return NetworkUtils.getBookInfo(strings[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        /**sẽ có lúc doInBackground trả về không phải chuỗi json hoặc bị time out
         * trong trường hợp này việc phân tích cú pháp sẽ bị lỗi
         */
        Log.v("XXX", "onPostExecute: "+s);
        super.onPostExecute(s);
        try {
            //sử dụng các lớp JSONObject và JSONArray để lấy mảng JSON của các mục từ chuỗi kết quả
            JSONObject jsonObject = new JSONObject(s);
            JSONArray itemsArray = jsonObject.getJSONArray("items");
            int i = 0;
            String title = null;
            String authors = null;
            while (i < itemsArray.length() && (authors == null && title == null)) {
                JSONObject book = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");
                try {
                    title = volumeInfo.getString("title");
                    authors = volumeInfo.getString("authors");
                } catch (Exception e) {
                    mTitleText.get().setText(R.string.no_results);
                    mAuthorText.get().setText("");
                    e.printStackTrace();
                }
                i++;
            }
            if (title != null && authors != null) {
                mTitleText.get().setText(title);
                mAuthorText.get().setText(authors);
            } else {
                mTitleText.get().setText(R.string.no_results);
                mAuthorText.get().setText("");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
