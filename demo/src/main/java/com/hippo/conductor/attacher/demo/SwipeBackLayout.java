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

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

// https://github.com/seven332/SwipeBack/blob/master/library/src/main/java/com/hippo/swipeback/SwipeBackLayout.java
public class SwipeBackLayout extends ViewGroup {

  public static final String LOG_TAG = SwipeBackLayout.class.getSimpleName();

  public static final int EDGE_NONE = 0;
  public static final int EDGE_LEFT = ViewDragHelper.EDGE_LEFT;
  public static final int EDGE_RIGHT = ViewDragHelper.EDGE_RIGHT;

  private static final float DEFAULT_SCROLL_THRESHOLD = 0.3f;
  private static final int OVER_SCROLL_DISTANCE = 10;
  private static final int DEFAULT_SCRIM_COLOR = 0x99000000;
  private static final int FULL_ALPHA = 255;

  private View mContentView;
  private ViewDragHelper mDragHelper;

  private int mEdgeFlag = EDGE_LEFT | EDGE_RIGHT;
  private float mScrollThreshold = DEFAULT_SCROLL_THRESHOLD;
  private int mScrimColor = DEFAULT_SCRIM_COLOR;

  private boolean mSwipeEnabled = true;
  private boolean mSwipeVertically = true;
  private float mStartX;
  private float mStartY;

  private int mTrackingEdge;
  private float mScrollPercent;
  private float mScrimOpacity;
  private int mContentLeft;
  private int mContentTop;

  @Nullable
  private List<SwipeListener> mSwipeListeners;

  public SwipeBackLayout(Context context) {
    super(context);
    init();
  }

  public SwipeBackLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public SwipeBackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    mDragHelper = ViewDragHelper.create(this, new ViewDragCallback());
  }

  @Override
  public void onViewAdded(View child) {
    super.onViewAdded(child);
    mContentView = child;
  }

  /**
   * Swipe out contentView and finish the activity.
   *
   * {@code edge} must be one of {@link SwipeBackLayout#EDGE_LEFT}
   * and {@link SwipeBackLayout#EDGE_RIGHT}.
   */
  public void swipeToFinishActivity(int edge) {
    final int childWidth = mContentView.getWidth();

    final int left;
    if (edge == EDGE_LEFT) {
      left = childWidth + OVER_SCROLL_DISTANCE;
      mTrackingEdge = EDGE_LEFT;
    } else if (edge == EDGE_RIGHT) {
      left = -childWidth - OVER_SCROLL_DISTANCE;
      mTrackingEdge = EDGE_RIGHT;
    } else {
      Log.e(LOG_TAG, "Invalid edge for swipeToFinishActivity: " + edge);
      return;
    }

    mDragHelper.smoothSlideViewTo(mContentView, left, 0);
    invalidate();
  }

  /**
   * Register the SwipeListener.
   */
  public void addSwipeListener(SwipeListener listener) {
    if (listener != null) {
      if (mSwipeListeners == null) {
        mSwipeListeners = new ArrayList<>();
      }
      mSwipeListeners.add(listener);
    }
  }

  /**
   * Unregister the SwipeListener.
   */
  public void removeSwipeListener(SwipeListener listener) {
    if (listener != null && mSwipeListeners != null) {
      mSwipeListeners.remove(listener);
    }
  }

  /**
   * Clear all SwipeListeners.
   */
  public void clearSwipeListeners() {
    if (mSwipeListeners != null) {
      mSwipeListeners.clear();
    }
  }

  /**
   * Set a color to use for the scrim that obscures primary content while layout swiped.
   */
  public void setScrimColor(int color) {
    mScrimColor = color;
    invalidate();
  }

  /**
   * Set scroll threshold. Must be in [0, 1.0f].
   */
  public void setScrollThresHold(float threshold) {
    if (threshold >= 1.0f || threshold <= 0) {
      throw new IllegalArgumentException("Threshold value should be between 0 and 1.0");
    }
    mScrollThreshold = threshold;
  }

  /**
   * Set the edges to track swipe action.
   * Must be one of {@code EDGE_NONE}, {@code EDGE_LEFT},
   * {@code EDGE_RIGHT} or {@code EDGE_LEFT|EDGE_RIGHT}.
   */
  public void setSwipeEdge(int edgeFlags) {
    mEdgeFlag = edgeFlags;
    mDragHelper.setEdgeTrackingEnabled(mEdgeFlag);
  }

  /**
   * Return the edges to track swipe action.
   */
  public int getSwipeEdge() {
    return mEdgeFlag;
  }

  /**
   * Set enabled state for swipe detection.
   */
  public void setSwipeEnabled(boolean swipeEnabled) {
    mSwipeEnabled = swipeEnabled;
  }

  /**
   * Returns the enabled status for swipe detection.
   */
  public boolean isSwipeEnabled() {
    return mSwipeEnabled;
  }

  @Override
  protected LayoutParams generateDefaultLayoutParams() {
    return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
  }

  @Override
  protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
    return p instanceof MarginLayoutParams ? p : new MarginLayoutParams(p);
  }

  @Override
  protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
    return p instanceof MarginLayoutParams && super.checkLayoutParams(p);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final MarginLayoutParams lp = (MarginLayoutParams) mContentView.getLayoutParams();
    final int widthPadding = getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin;
    final int heightPadding = getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin;
    mContentView.measure(getChildMeasureSpec(widthMeasureSpec, widthPadding, lp.width),
        getChildMeasureSpec(heightMeasureSpec, heightPadding, lp.height));
    setMeasuredDimension(resolveSize(mContentView.getMeasuredWidth() + widthPadding, widthMeasureSpec),
        resolveSize(mContentView.getMeasuredHeight() + heightPadding, heightMeasureSpec));
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    final MarginLayoutParams lp = (MarginLayoutParams) mContentView.getLayoutParams();
    final int left = getPaddingLeft() + lp.leftMargin + mContentLeft;
    final int top = getPaddingTop() + lp.topMargin + mContentTop;
    mContentView.layout(left, top,
        left + mContentView.getMeasuredWidth(),
        top + mContentView.getMeasuredHeight());
  }

  private void drawScrim(Canvas canvas, View child) {
    final int baseAlpha = (mScrimColor & 0xff000000) >>> 24;
    final int alpha = (int) (baseAlpha * mScrimOpacity);
    final int color = alpha << 24 | (mScrimColor & 0xffffff);

    if (mTrackingEdge == EDGE_LEFT) {
      canvas.clipRect(0, 0, child.getLeft(), getHeight());
    } else if (mTrackingEdge == EDGE_RIGHT) {
      canvas.clipRect(child.getRight(), 0, getRight(), getHeight());
    }
    canvas.drawColor(color);
  }

  @Override
  protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
    final boolean drawContent = child == mContentView;

    final boolean ret = super.drawChild(canvas, child, drawingTime);
    if (mScrimOpacity > 0 && drawContent
        && mDragHelper.getViewDragState() != ViewDragHelper.STATE_IDLE) {
      drawScrim(canvas, child);
    }
    return ret;
  }

  @Override
  public void computeScroll() {
    mScrimOpacity = 1 - mScrollPercent;
    if (mDragHelper.continueSettling(true)) {
      ViewCompat.postInvalidateOnAnimation(this);
    }
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent event) {
    if (!mSwipeEnabled) {
      return false;
    }

    final float x = event.getX();
    final float y = event.getY();

    if (event.getAction() == MotionEvent.ACTION_DOWN) {
      mSwipeVertically = false;
      mStartX = x;
      mStartY = y;
    } else if (!mSwipeVertically) {
      final float dx = Math.abs(mStartX - x);
      final float dy = Math.abs(mStartY - y);
      mSwipeVertically = dy > mDragHelper.getTouchSlop() && dy > 1.5 * dx;
    }

    if (mSwipeVertically) {
      return false;
    } else {
      try {
        return mDragHelper.shouldInterceptTouchEvent(event);
      } catch (Exception e) {
        return false;
      }
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (!mSwipeEnabled) {
      return false;
    }

    try {
      mDragHelper.processTouchEvent(event);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  private class ViewDragCallback extends ViewDragHelper.Callback {

    private boolean mIsScrollOverValid;

    @Override
    public boolean tryCaptureView(View view, int i) {
      final boolean ret = mDragHelper.isEdgeTouched(mEdgeFlag, i);
      if (ret) {
        if (mDragHelper.isEdgeTouched(EDGE_LEFT, i)) {
          mTrackingEdge = EDGE_LEFT;
        } else if (mDragHelper.isEdgeTouched(EDGE_RIGHT, i)) {
          mTrackingEdge = EDGE_RIGHT;
        }

        mIsScrollOverValid = true;
      }
      return ret;
    }

    @Override
    public int getViewHorizontalDragRange(View child) {
      return mEdgeFlag & (EDGE_LEFT | EDGE_RIGHT);
    }

    @Override
    public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
      if (mTrackingEdge == EDGE_LEFT) {
        mScrollPercent = Math.abs((float) left
            / (mContentView.getWidth()));
      } else if (mTrackingEdge == EDGE_RIGHT) {
        mScrollPercent = Math.abs((float) left
            / (mContentView.getWidth()));
      }
      mContentLeft = left;
      mContentTop = top;
      invalidate();
      if (mScrollPercent < mScrollThreshold && !mIsScrollOverValid) {
        mIsScrollOverValid = true;
      }

      // Callback
      if (mSwipeListeners != null && !mSwipeListeners.isEmpty()) {
        // onSwipe
        for (SwipeListener listener : mSwipeListeners) {
          listener.onSwipe(mScrollPercent);
        }
        // onSwipeOverThreshold
        if (mDragHelper.getViewDragState() == ViewDragHelper.STATE_DRAGGING
            && mScrollPercent >= mScrollThreshold && mIsScrollOverValid) {
          mIsScrollOverValid = false;
          for (SwipeListener listener : mSwipeListeners) {
            listener.onSwipeOverThreshold();
          }
        }
      }

      if (mScrollPercent >= 1) {
        // Callback
        if (mSwipeListeners != null && !mSwipeListeners.isEmpty()) {
          for (SwipeListener listener : mSwipeListeners) {
            listener.onFinish();
          }
        }
      }
    }

    @Override
    public void onViewReleased(View releasedChild, float xvel, float yvel) {
      final int childWidth = releasedChild.getWidth();

      int left = 0;
      if (mTrackingEdge == EDGE_LEFT) {
        left = xvel > 0 || xvel == 0 && mScrollPercent > mScrollThreshold ? childWidth
            + OVER_SCROLL_DISTANCE : 0;
      } else if (mTrackingEdge == EDGE_RIGHT) {
        left = xvel < 0 || xvel == 0 && mScrollPercent > mScrollThreshold ? -(childWidth
            + OVER_SCROLL_DISTANCE) : 0;
      }

      mDragHelper.settleCapturedViewAt(left, 0);
      invalidate();
    }

    @Override
    public int clampViewPositionHorizontal(View child, int left, int dx) {
      int targetEdge = mTrackingEdge;
      if (targetEdge == EDGE_NONE) {
        // It is test for intercept touch event
        // Try to get target edge
        if ((mEdgeFlag & EDGE_LEFT) != 0 && mDragHelper.isEdgeTouched(EDGE_LEFT)) {
          targetEdge = EDGE_LEFT;
        } else if ((mEdgeFlag & EDGE_RIGHT) != 0 && mDragHelper.isEdgeTouched(EDGE_RIGHT)) {
          targetEdge = EDGE_RIGHT;
        }
      }

      int ret = 0;
      if (targetEdge == EDGE_LEFT) {
        ret = Math.min(child.getWidth(), Math.max(left, 0));
      } else if (targetEdge == EDGE_RIGHT) {
        ret = Math.min(0, Math.max(left, -child.getWidth()));
      }
      return ret;
    }

    @Override
    public void onViewDragStateChanged(int state) {
      // Callback
      if (mSwipeListeners != null && !mSwipeListeners.isEmpty()) {
        for (SwipeListener listener : mSwipeListeners) {
          listener.onStateChange(mTrackingEdge, state);
        }
      }

      if (state == ViewDragHelper.STATE_IDLE) {
        // Reset mTrackingEdge
        mTrackingEdge = EDGE_NONE;
      }
    }
  }

  public interface SwipeListener {

    /**
     * Called when scroll percent changed.
     */
    void onSwipe(float percent);

    /**
     * Called when state change.
     */
    void onStateChange(int edge, int state);

    /**
     * Called when swipe percent over the threshold for the first time.
     */
    void onSwipeOverThreshold();

    /**
     * Called when done.
     */
    void onFinish();
  }
}
