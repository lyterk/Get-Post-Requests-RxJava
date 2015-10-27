package com.lyterk.rxjavasandbox;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class PostRequest {

    private static final String TAG = "com.lyterk.PostRequest";

    private String postString = "";

    private ProcessListener mProcessListener;

    private HttpURLConnection conn;
    private URL url;
    private String result;
    private OutputStream os;

    public PostRequest(String sUrl, ProcessListener processListener) {
        this.mProcessListener = processListener;
        try {
            url = new URL(sUrl);
        } catch (IOException e) {
            Log.e(TAG, "Bad URL");
        }
    }

    public void setmPostString(String postString) {
        this.postString = postString;
    }

    public String getResult() {
        return result;
    }

    Observable<String> postData = Observable.create(new Observable.OnSubscribe<String>() {
        @Override
        public void call(Subscriber<? super String> subscriber) {
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setFixedLengthStreamingMode(postString.getBytes().length);
                conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
                conn.connect();
                os = new BufferedOutputStream(conn.getOutputStream());
                os.write(postString.getBytes());
                os.flush();
                InputStream in = new BufferedInputStream(conn.getInputStream());
                result = inputStreamToString(in);
                subscriber.onNext(result);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            } finally {
                conn.disconnect();
            }
        }
    });

    private String inputStreamToString(InputStream input) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(input));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }
        return total.toString();
    }

    public void post() {
        postData
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        mProcessListener.processingDone(s);
                    }
                });       
    }
}
