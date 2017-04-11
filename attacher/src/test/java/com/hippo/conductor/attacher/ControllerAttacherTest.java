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

package com.hippo.conductor.attacher;

/*
 * Created by Hippo on 4/10/2017.
 */

import static org.junit.Assert.assertEquals;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.ControllerOpacity;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.internal.NoOpControllerChangeHandler;
import com.bluelinelabs.conductor.util.ActivityProxy;
import com.hippo.conductor.attacher.util.OpacityController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ControllerAttacherTest {

  private ActivityProxy activityProxy;
  private Router router;
  private ViewGroup container;

  @Before
  public void setup() {
    activityProxy = new ActivityProxy().create(null).start().resume();
    router = Conductor.attachRouter(activityProxy.getActivity(), activityProxy.getView(), null);
    container = activityProxy.getView();
  }

  ///////////////////////////////////////////////////////////////////////////
  // Test push and pop
  ///////////////////////////////////////////////////////////////////////////

  @Test
  public void testPushAndPop() {
    testPushAndPopInternal(5, true);
    testPushAndPopInternal(5, false);
  }

  private void testPushAndPopInternal(int size, boolean popLastView) {
    int[] opacityArray = new int[size];
    for (int i = 0, n = (int) Math.pow(3, size); i < n; ++i) {
      for (int j = 0; j < size; ++j) {
        opacityArray[j] = (i / (int) Math.pow(3, j)) % 3;
      }
      testPushAndPopEntry(opacityArray, popLastView);
    }
  }

  private void testPushAndPopEntry(int[] opacityArray, boolean popLastView) {
    router.setPopsLastView(popLastView);

    // Root
    assertEquals(0, container.getChildCount());
    router.setRoot(
        RouterTransaction.with(new OpacityController(opacityArray[0]))
        .pushChangeHandler(new SuspendChangeHandle())
        .popChangeHandler(new SuspendChangeHandle())
    );
    assertEquals(1, container.getChildCount());

    // Push
    for (int i = 1, n = opacityArray.length; i < n; ++i) {
      router.pushController(
          RouterTransaction.with(new OpacityController(opacityArray[i]))
              .pushChangeHandler(new SuspendChangeHandle())
              .popChangeHandler(new SuspendChangeHandle())
      );
      assertEquals(getChildCountOnPushChangeStart(opacityArray, i), container.getChildCount());
      SuspendChangeHandle.onChangeCompleted();
      assertEquals(getChildCountOnPushChangeEnd(opacityArray, i), container.getChildCount());
    }

    // Pop
    for (int i = opacityArray.length - 1; i >= 0; --i) {
      router.popCurrentController();
      if (i == 0 && !popLastView) {
        // Router uses NoOpControllerChangeHandler here
        assertEquals(getChildCountOnPopChangeEnd(opacityArray, popLastView, i), container.getChildCount());
      } else {
        assertEquals(getChildCountOnPopChangeStart(opacityArray, popLastView, i), container.getChildCount());
        SuspendChangeHandle.onChangeCompleted();
        assertEquals(getChildCountOnPopChangeEnd(opacityArray, popLastView, i), container.getChildCount());
      }
    }

    if (!popLastView) {
      // Clear root controller view
      container.removeAllViews();
    }
  }

  private int getChildCountOnPushChangeStart(int[] opacityArray, int index) {
    if (index == 0) {
      return 1;
    } else {
      return getChildCountOnPushChangeEnd(opacityArray, index - 1) + 1;
    }
  }

  private int getChildCountOnPushChangeEnd(int[] opacityArray, int index) {
    return calculateChildCount(opacityArray, index);
  }

  private int getChildCountOnPopChangeStart(int[] opacityArray, boolean popLastView, int index) {
    if (index == 0) {
      return 1;
    } else {
      return getChildCountOnPopChangeEnd(opacityArray, popLastView, index) + 1;
    }
  }

  private int getChildCountOnPopChangeEnd(int[] opacityArray, boolean popLastView, int index) {
    if (index == 0) {
      return popLastView ? 0 : 1;
    } else {
      return getChildCountOnPushChangeEnd(opacityArray, index - 1);
    }
  }

  private static class SuspendChangeHandle extends ControllerChangeHandler {

    private static ControllerChangeCompletedListener listener;

    public SuspendChangeHandle() {}

    @Override
    public void performChange(@NonNull ViewGroup container, @Nullable View from, @Nullable View to,
        boolean isPush, @NonNull ControllerChangeCompletedListener changeListener) {
      listener = changeListener;
    }

    private static void onChangeCompleted() {
      listener.onChangeCompleted();
      listener = null;
    }
  }

  ///////////////////////////////////////////////////////////////////////////
  // Test rebind
  ///////////////////////////////////////////////////////////////////////////

  @Test
  public void testRebind() {
    testRebindInternal(5);
  }

  private void testRebindInternal(int size) {
    int[] opacityArray = new int[size];
    for (int i = 0, n = (int) Math.pow(3, size); i < n; ++i) {
      for (int j = 0; j < size; ++j) {
        opacityArray[j] = (i / (int) Math.pow(3, j)) % 3;
      }
      testRebindEntry(opacityArray);
    }
  }

  private void testRebindEntry(int[] opacityArray) {
    // Root
    assertEquals(0, container.getChildCount());
    router.setRoot(
        RouterTransaction.with(new OpacityController(opacityArray[0]))
            .pushChangeHandler(new NoOpControllerChangeHandler())
            .popChangeHandler(new NoOpControllerChangeHandler())
    );
    assertEquals(1, container.getChildCount());

    // Push
    for (int i = 1, n = opacityArray.length; i < n; ++i) {
      router.pushController(
          RouterTransaction.with(new OpacityController(opacityArray[i]))
              .pushChangeHandler(new NoOpControllerChangeHandler())
              .popChangeHandler(new NoOpControllerChangeHandler())
      );
    }

    // Rotate
    assertEquals(calculateChildCount(opacityArray, opacityArray.length - 1), container.getChildCount());
    activityProxy.rotate();
    router.rebindIfNeeded();
    assertEquals(calculateChildCount(opacityArray, opacityArray.length - 1), container.getChildCount());

    // Pop
    for (int i = opacityArray.length - 1; i >= 0; --i) {
      router.popCurrentController();
    }

    // Clear root controller view
    container.removeAllViews();
  }


  private static int calculateChildCount(int[] opacityArray, int index) {
    int count = 0;
    do {
      ++count;
    } while ((opacityArray[index] == ControllerOpacity.TRANSPARENT ||
        (opacityArray[index] == ControllerOpacity.TRANSLUCENT && count == 1)) && --index >= 0);
    return count;
  }
}
