package com.lyterk.rxjavasandbox;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class GetRequest {

    private static final String TAG = "com.lyterk.GetRequest";
    private ProcessListener mProcessListener;

    public GetRequest(String sUrl, ProcessListener processListener) {
        this.mProcessListener = processListener;
        try {
            url = new URL(sUrl);
        } catch (IOException e) {
            Log.e(TAG, "Bad URL");
        }
    }

    private HttpURLConnection mHttpURLConnection;
    private URL url;
    private String result;

    public String getResult() {
        return result;
    }

    Observable<String> fetchPage = Observable.create(new Observable.OnSubscribe<String>() {
        @Override
        public void call(Subscriber<? super String> subscriber) {
            try {
                mHttpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(mHttpURLConnection.getInputStream());
                result = inputStreamToString(in);
                subscriber.onNext(result);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
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

    public void pageFetching() {
        fetchPage
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
