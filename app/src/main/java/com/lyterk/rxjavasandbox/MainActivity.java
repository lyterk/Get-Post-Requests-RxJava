package com.lyterk.rxjavasandbox;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity
        extends AppCompatActivity {

    private static final String TAG = "com.lyterk.MainActivity";

    private String getUrl = "http://httpbin.org/";
    private String postUrl = "http://httpbin.org/post";
    private TextView resultsTV;
    private EditText postET;
    private Toolbar toolbar;

    public ProcessListener mProcessListener;

    private GetRequest mGetRequest;
    private PostRequest mPostRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();

        mGetRequest = new GetRequest(getUrl, mProcessListener);
        mPostRequest = new PostRequest(postUrl, mProcessListener);

        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
            case R.id.get_button:
                mGetRequest.pageFetching();
                resultsTV.setText(mGetRequest.getResult());
                break;
        }
    }

    public void initialize() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        resultsTV = (TextView) findViewById(R.id.results_tv);
        postET = (EditText) findViewById(R.id.post_et);

        mProcessListener = new ProcessListener() {
            @Override
            public void processingDone(String s) {
                resultsTV.setText(s);
            }
        };

        postET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.v(TAG, "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.v(TAG, "onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                mPostRequest.setmPostString(str);
                mPostRequest.post();
            }
        });
    }
}
