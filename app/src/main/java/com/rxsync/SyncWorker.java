package com.rxsync;

import android.util.Log;
import java.lang.reflect.Array;
import java.util.List;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func0;

public class SyncWorker {
    private static final String TAG = SyncWorker.class.getSimpleName();

    public void sync(final List<String> messages) {
        Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                String[] arrays = new String[messages.size()];
                int index = 0;
                for (String message : messages) {
                    Array.set(arrays, index++, message);
                }
                return Observable.from(arrays);
            }
        }).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "onCompleted()");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError()", e);
            }

            @Override
            public void onNext(String string) {
                Log.d(TAG, "onNext(" + string + ")");
                RxMappingProxy.getInstance().exec(string);
            }
        });
    }

    public void sync(final String message) {
        Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                return Observable.just(message);
            }
        }).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "onCompleted()");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError()", e);
            }

            @Override
            public void onNext(String string) {
                Log.d(TAG, "onNext(" + string + ")");
                RxMappingProxy.getInstance().exec(string);
            }
        });
    }


}
