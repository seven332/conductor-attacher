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

package com.hippo.conductor.attacher.demo;

/*
 * Created by Hippo on 4/11/2017.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.hippo.conductor.attacher.demo.changehandler.FadeChangeHandler2;
import com.hippo.conductor.attacher.demo.controller.HomeController;

public class ControllerActivity extends AppCompatActivity {

  public static final String KEY_USE_ATTACHER = "ControllerActivity:use_attacher";

  private boolean useAttacher;
  private int childCount;
  private Router router;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    useAttacher = getIntent().getBooleanExtra(KEY_USE_ATTACHER, false);

    setContentView(R.layout.activity_controller);
    ViewGroup container = (ViewGroup) findViewById(R.id.controller_container);
    container.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
      @Override
      public void onChildViewAdded(View parent, View child) {
        ++childCount;
        setTitle("Child Count: " + childCount);
      }

      @Override
      public void onChildViewRemoved(View parent, View child) {
        --childCount;
        setTitle("Child Count: " + childCount);
      }
    });

    router = Conductor.attachRouter(this, container, savedInstanceState);
    if (!router.hasRootController()) {
      router.setRoot(
          RouterTransaction.with(new HomeController(useAttacher))
              .pushChangeHandler(getChangeHandler(HomeController.getRemoveFromViewOnPush()))
              .popChangeHandler(getChangeHandler(HomeController.getRemoveFromViewOnPush()))
      );
    }
  }

  @Override
  public void onBackPressed() {
    if (!router.handleBack()) {
      super.onBackPressed();
    }
  }

  public ControllerChangeHandler getChangeHandler(boolean removeFromViewOnPush) {
    return useAttacher
        ? new FadeChangeHandler2(Constants.FADE_DURATION)
        : new FadeChangeHandler(Constants.FADE_DURATION, removeFromViewOnPush);
  }
}
