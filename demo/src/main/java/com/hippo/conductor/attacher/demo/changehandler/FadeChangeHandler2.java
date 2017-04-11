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

package com.hippo.conductor.attacher.demo.changehandler;

/*
 * Created by Hippo on 4/11/2017.
 */

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;

public class FadeChangeHandler2 extends AnimatorChangeHandler2 {

  public FadeChangeHandler2() { }

  public FadeChangeHandler2(long duration) {
    super(duration);
  }

  @Override @NonNull
  protected Animator getAnimator(@NonNull ViewGroup container, @Nullable View from, @Nullable View to, boolean isPush) {
    AnimatorSet animator = new AnimatorSet();
    if (to != null) {
      float start = isPush ? 0 : to.getAlpha();
      animator.play(ObjectAnimator.ofFloat(to, View.ALPHA, start, 1));
    }

    if (from != null && !isPush) {
      animator.play(ObjectAnimator.ofFloat(from, View.ALPHA, 0));
    }

    return animator;
  }

  @Override
  protected void resetFromView(@NonNull View from) {
    from.setAlpha(1);
  }

  @Override @NonNull
  public ControllerChangeHandler copy() {
    return new FadeChangeHandler(getAnimationDuration(), removesFromViewOnPush());
  }
}
