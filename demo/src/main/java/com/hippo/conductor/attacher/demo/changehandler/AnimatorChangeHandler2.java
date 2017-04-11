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
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;

public abstract class AnimatorChangeHandler2 extends ControllerChangeHandler {

  private static final String KEY_DURATION = "AnimatorChangeHandler.duration";

  public static final long DEFAULT_ANIMATION_DURATION = -1;

  private long animationDuration;
  private boolean canceled;
  private boolean completed;
  private Animator animator;

  public AnimatorChangeHandler2() {
    this(DEFAULT_ANIMATION_DURATION);
  }

  public AnimatorChangeHandler2(long duration) {
    animationDuration = duration;
  }

  @Override
  public void saveToBundle(@NonNull Bundle bundle) {
    super.saveToBundle(bundle);
    bundle.putLong(KEY_DURATION, animationDuration);
  }

  @Override
  public void restoreFromBundle(@NonNull Bundle bundle) {
    super.restoreFromBundle(bundle);
    animationDuration = bundle.getLong(KEY_DURATION);
  }

  @Override
  public void onAbortPush(@NonNull ControllerChangeHandler newHandler, @Nullable Controller newTop) {
    super.onAbortPush(newHandler, newTop);

    canceled = true;
    if (animator != null) {
      animator.cancel();
    }
  }

  @Override
  public void completeImmediately() {
    super.completeImmediately();

    if (animator != null) {
      animator.end();
    }
  }

  public long getAnimationDuration() {
    return animationDuration;
  }

  @Override
  public boolean removesFromViewOnPush() {
    // Always return false
    return false;
  }

  /**
   * Should be overridden to return the Animator to use while replacing Views.
   *
   * @param container The container these Views are hosted in.
   * @param from The previous View in the container or {@code null} if there was no Controller before this transition
   * @param to The next View that should be put in the container or {@code null} if no Controller is being transitioned to
   * @param isPush True if this is a push transaction, false if it's a pop.
   */
  @NonNull
  protected abstract Animator getAnimator(@NonNull ViewGroup container, @Nullable View from, @Nullable View to, boolean isPush);

  /**
   * Will be called after the animation is complete to reset the View that was removed to its pre-animation state.
   */
  protected abstract void resetFromView(@NonNull View from);

  @Override
  public final void performChange(@NonNull final ViewGroup container, @Nullable final View from, @Nullable final View to, final boolean isPush, @NonNull final ControllerChangeCompletedListener changeListener) {
    if (to != null && !ViewCompat.isLaidOut(to)) {
      to.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
          final ViewTreeObserver observer = to.getViewTreeObserver();
          if (observer.isAlive()) {
            observer.removeOnPreDrawListener(this);
          }
          performAnimation(container, from, to, isPush, changeListener);
          return true;
        }
      });
    } else {
      performAnimation(container, from, to, isPush, changeListener);
    }
  }

  private void complete(@NonNull ControllerChangeCompletedListener changeListener, @Nullable Animator.AnimatorListener animatorListener) {
    if (!completed) {
      completed = true;
      changeListener.onChangeCompleted();
    }

    if (animator != null) {
      if (animatorListener != null) {
        animator.removeListener(animatorListener);
      }
      animator.cancel();
      animator = null;
    }
  }

  private void performAnimation(@NonNull final ViewGroup container, @Nullable final View from, @Nullable final View to, final boolean isPush, @NonNull final ControllerChangeCompletedListener changeListener) {
    if (canceled) {
      complete(changeListener, null);
      return;
    }

    animator = getAnimator(container, from, to, isPush);

    if (animationDuration > 0) {
      animator.setDuration(animationDuration);
    }

    animator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationCancel(Animator animation) {
        complete(changeListener, this);
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        if (!canceled && animator != null) {
          complete(changeListener, this);

          if (isPush && from != null) {
            resetFromView(from);
          }
        }
      }
    });

    animator.start();
  }
}
