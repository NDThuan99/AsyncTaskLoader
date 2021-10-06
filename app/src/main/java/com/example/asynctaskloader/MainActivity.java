package com.example.asynctaskloader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private EditText mEdtBookInput;
    private TextView mTxtTitleText, mTxtAuthorText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEdtBookInput = (EditText)findViewById(R.id.edt_inputContent);
        mTxtTitleText = (TextView)findViewById(R.id.txt_title);
        mTxtAuthorText = (TextView)findViewById(R.id.txt_author);

        //nếu loader đã tồn tại thì kết nối lại với activity
        if(getSupportLoaderManager().getLoader(0)!=null){
            getSupportLoaderManager().initLoader(0,null,this);
        }
    }

    public void searchBooks(View view) {
        String str_query = mEdtBookInput.getText().toString();
        //new FetchBook(mTxtTitleText, mTxtAuthorText).execute(str_query);
        Bundle queryBundle = new Bundle();
        queryBundle.putString("queryString", str_query);
        /**
         * các đối số trong restartLoader(id, bundle, callback)
         * id: sẽ hữu ích nếu bạn triển khai nhiều hơn một trình tải trong hoạt động của mình.
         * Bundle: cho số bất kỳ dữ liệu nào cần tải.
         * callback: Nếu bạn muốn tải xuống cung cấp kết quả cho MainActivity, hãy chỉ định thứ ba đối số này là this.
         * */
        getSupportLoaderManager().restartLoader(0, queryBundle, this);

        mTxtAuthorText.setText("");
        mTxtTitleText.setText(R.string.loading);
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputManager != null ) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        //kiểm tra kết nối mạng
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }
        if (networkInfo != null && networkInfo.isConnected()
                && str_query.length() != 0) {
            new FetchBook(mTxtTitleText, mTxtAuthorText).execute(str_query);
            mTxtAuthorText.setText("");
            mTxtTitleText.setText(R.string.loading);
        }else {
            if (str_query.length() == 0) {
                mTxtAuthorText.setText("");
                mTxtTitleText.setText(R.string.no_search_term);
            } else {
                mTxtAuthorText.setText("");
                mTxtTitleText.setText(R.string.no_network);
            }
        }
    }

    @NonNull
    @Override
    //được gọi khi khởi tạo.
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        String queryString = "";

        if (args != null) {
            queryString = args.getString("queryString");
        }

        return new BookLoader(this, queryString);
    }
    //được gọi khi tác vụ của bộ nạp kết thúc. Đây là nơi bạn thêm mã để cập nhật giao diện người dùng của mình với kết quả.
    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        try {
            //sử dụng các lớp JSONObject và JSONArray để lấy mảng JSON của các mục từ chuỗi kết quả
            JSONObject jsonObject = new JSONObject(data);
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
                    mTxtTitleText.setText(R.string.no_results);
                    mTxtAuthorText.setText("");
                    e.printStackTrace();
                }
                i++;
            }
            if (title != null && authors != null) {
                mTxtTitleText.setText(title);
                mTxtAuthorText.setText(authors);
            } else {
                mTxtTitleText.setText(R.string.no_results);
                mTxtAuthorText.setText("");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //dọn dẹp mọi tài nguyên còn lại.
    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}