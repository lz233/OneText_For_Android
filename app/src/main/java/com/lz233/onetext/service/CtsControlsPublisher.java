/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lz233.onetext.service;

import android.os.Build;
import android.service.controls.Control;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

/**
 * Simplified Publisher for use with CTS testing only. Assumes all Controls are added
 * to the Publisher ahead of a subscribe request. Assumes only one request() call.
 */
public class CtsControlsPublisher implements Publisher<Control> {

    private final List<Control> mControls = new ArrayList<>();
    private Subscriber<? super Control> mSubscriber;

    public CtsControlsPublisher(List<Control> controls) {
        if (controls != null) {
            mControls.addAll(controls);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void subscribe(Subscriber<? super Control> subscriber) {
        mSubscriber = subscriber;
        mSubscriber.onSubscribe(new Subscription() {
            public void request(long n) {
                int i = 0;
                while (i < n && i < mControls.size()) {
                    subscriber.onNext(mControls.get(i));
                    i++;
                }

                if (i == mControls.size()) {
                    subscriber.onComplete();
                }
            }

            public void cancel() {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void onNext(Control c) {
        if (mSubscriber == null) {
            mControls.add(c);
        } else {
            mSubscriber.onNext(c);
        }
    }

}