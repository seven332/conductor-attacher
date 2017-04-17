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

package com.bluelinelabs.conductor;

/*
 * Created by Hippo on 4/10/2017.
 */

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import java.util.ListIterator;

/**
 * {@code ControllerAttacher} handles view attaching and detaching.
 * {@link ControllerChangeHandler} should not call {@code addView()} and {@code removeView},
 * at least sets {@code RemovesFromViewOnPush} to {@code false}.
 */
public class ControllerAttacher extends Controller.LifecycleListener {

  @Override
  public void onChangeStart(@NonNull Controller controller,
      @NonNull ControllerChangeHandler changeHandler, @NonNull ControllerChangeType changeType) {
    Router router = controller.getRouter();
    List<RouterTransaction> stack = router.getBackstack();
    ViewGroup container = router.getContainer();
    addSeenViews(stack, container);
  }

  private static void addSeenViews(List<RouterTransaction> stack, ViewGroup container) {
    ListIterator<RouterTransaction> iterable = stack.listIterator(stack.size());
    int index = container.getChildCount();
    boolean isTop = true;
    while(iterable.hasPrevious()) {
      Controller controller = iterable.previous().controller();
      // Ensure the view is shown
      View view = controller.getView();
      if (view == null) {
        view = controller.inflate(container);
      }
      if (view.getParent() == null) {
        container.addView(view, index);
      }
      // Update index
      index = container.indexOfChild(view);
      // Check controller opacity
      int opacity = getControllerOpacity(controller);
      if (opacity == ControllerOpacity.OPAQUE ||
          (opacity == ControllerOpacity.TRANSLUCENT && !isTop)) {
        // The controllers below can't be seen
        break;
      }
      isTop = false;
    }
  }

  @Override
  public void onChangeEnd(@NonNull Controller controller,
      @NonNull ControllerChangeHandler changeHandler, @NonNull ControllerChangeType changeType) {
    Router router = controller.getRouter();
    List<RouterTransaction> stack = router.getBackstack();
    removeUnseenViews(stack);

    if (changeType == ControllerChangeType.POP_EXIT &&
        (!stack.isEmpty() || router.getPopsLastView())) {
      // Remove self view
      View view = controller.getView();
      if (view != null && view.getParent() != null) {
        ((ViewGroup) view.getParent()).removeView(view);
      }
    }
  }

  private static void removeUnseenViews(List<RouterTransaction> stack) {
    ListIterator<RouterTransaction> iterable = stack.listIterator(stack.size());

    boolean isTop = true;
    while(iterable.hasPrevious()) {
      Controller controller = iterable.previous().controller();
      int opacity = getControllerOpacity(controller);
      if (opacity == ControllerOpacity.OPAQUE ||
          (opacity == ControllerOpacity.TRANSLUCENT && !isTop)) {
        // The controllers below can't be seen
        break;
      }
      isTop = false;
    }

    // Remove remain views
    while(iterable.hasPrevious()) {
      Controller controller = iterable.previous().controller();
      View view = controller.getView();
      if (view != null && view.getParent() != null) {
        ((ViewGroup) view.getParent()).removeView(view);
      }
    }
  }

  // Gets the opacity value if the Controller implements ControllerOpacity,
  // or ControllerOpacity.OPAQUE as default.
  private static int getControllerOpacity(Controller controller) {
    if (controller instanceof ControllerOpacity) {
      return ((ControllerOpacity) controller).getOpacity();
    } else {
      return ControllerOpacity.OPAQUE;
    }
  }
}
