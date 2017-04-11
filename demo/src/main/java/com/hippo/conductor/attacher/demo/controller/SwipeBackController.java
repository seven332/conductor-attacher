/*
 * Copyright 2017 Hippo Seven
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hippo.conductor.attacher.demo.controller;

/*
 * Created by Hippo on 4/11/2017.
 */

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.bluelinelabs.conductor.RouterTransaction;
import com.hippo.conductor.attacher.demo.R;
import com.hippo.conductor.attacher.demo.SwipeBackLayout;

public class SwipeBackController extends BaseController {

  public SwipeBackController(boolean useAttacher) {
    super(useAttacher);
  }

  public SwipeBackController(Bundle args) {
    super(args);
  }

  @Override
  public int getOpaque() {
    return TRANSLUCENT;
  }

  @NonNull
  @Override
  protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
    View view = inflater.inflate(R.layout.controller_swipe_back, container, false);
    SwipeBackLayout swipeBackLayout = (SwipeBackLayout) view.findViewById(R.id.swipe_back);
    swipeBackLayout.addSwipeListener(new SwipeBackLayout.SwipeListener() {
      @Override
      public void onSwipe(float percent) {}

      @Override
      public void onStateChange(int edge, int state) {}

      @Override
      public void onSwipeOverThreshold() {}

      @Override
      public void onFinish() {
        getRouter().popController(SwipeBackController.this);
      }
    });
    Button button = (Button) view.findViewById(R.id.button);
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        getRouter().pushController(
            RouterTransaction.with(new SwipeBackController(getUseAttacher()))
                .pushChangeHandler(getChangeHandler(SwipeBackController.getRemoveFromViewOnPush()))
                .popChangeHandler(getChangeHandler(SwipeBackController.getRemoveFromViewOnPush()))
        );
      }
    });
    return view;
  }

  public static boolean getRemoveFromViewOnPush() {
    return false;
  }
}
