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
import com.bluelinelabs.conductor.ControllerAttacher;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.ControllerOpacity;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.hippo.android.dialog.conductor.AnDialogController;
import com.hippo.conductor.attacher.demo.Constants;
import com.hippo.conductor.attacher.demo.changehandler.FadeChangeHandler2;

public abstract class BaseDialogController extends AnDialogController implements ControllerOpacity {

  private static final String KEY_USE_ATTACHER = "HomeController:use_attacher";

  private final boolean useAttacher;

  public BaseDialogController(boolean useAttacher) {
    super();

    this.useAttacher = useAttacher;
    if (useAttacher) {
      addLifecycleListener(new ControllerAttacher());
    }

    // Write to args
    Bundle args = getArgs();
    args.putBoolean(KEY_USE_ATTACHER, useAttacher);
  }

  public BaseDialogController(Bundle args) {
    super(args);

    useAttacher = args.getBoolean(KEY_USE_ATTACHER);
    if (useAttacher) {
      addLifecycleListener(new ControllerAttacher());
    }
  }

  public final boolean getUseAttacher() {
    return useAttacher;
  }

  public final ControllerChangeHandler getChangeHandler(boolean removeFromViewOnPush) {
    return useAttacher
        ? new FadeChangeHandler2(Constants.FADE_DURATION)
        : new FadeChangeHandler(Constants.FADE_DURATION, removeFromViewOnPush);
  }
}
