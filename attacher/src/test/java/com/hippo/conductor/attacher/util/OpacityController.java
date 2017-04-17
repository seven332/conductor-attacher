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

package com.hippo.conductor.attacher.util;

/*
 * Created by Hippo on 4/11/2017.
 */

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerAttacher;
import com.bluelinelabs.conductor.ControllerOpacity;

public class OpacityController extends Controller implements ControllerOpacity {

  private static final String KEY_OPACITY = "OpacityController:opacity";

  private int opacity;

  public OpacityController(int opacity) {
    super();

    Bundle args = getArgs();
    args.putInt(KEY_OPACITY, opacity);

    this.opacity = opacity;

    addLifecycleListener(new ControllerAttacher());
  }

  public OpacityController(Bundle args) {
    super(args);

    opacity = args.getInt(KEY_OPACITY);

    addLifecycleListener(new ControllerAttacher());
  }

  @NonNull
  @Override
  protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
    return new View(inflater.getContext());
  }

  @Override
  public int getOpacity() {
    return opacity;
  }
}
