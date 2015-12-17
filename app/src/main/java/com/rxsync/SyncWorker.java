package com.rxsync;

import android.util.Log;

import java.util.ArrayList;
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
                int size = messages.size();
                String[] array = new String[size];
                for (int index = 0 ; index < size; index ++){
                    array[index] = messages.get(index);
                }
                return Observable.from(array);
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

    public void sync(final String[] messages) {
        Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                return Observable.from(messages);
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
