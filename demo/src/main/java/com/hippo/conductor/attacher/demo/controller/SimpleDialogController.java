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

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.bluelinelabs.conductor.RouterTransaction;
import com.hippo.android.dialog.base.DialogView;
import com.hippo.android.dialog.base.DialogViewBuilder;

public class SimpleDialogController extends BaseDialogController {

  public SimpleDialogController(boolean useAttacher) {
    super(useAttacher);
  }

  public SimpleDialogController(Bundle args) {
    super(args);
  }

  @Override
  public int getOpacity() {
    return TRANSPARENT;
  }

  @NonNull
  @Override
  protected DialogView onCreateDialogView(@NonNull LayoutInflater inflater,
      @NonNull ViewGroup container) {
    return new DialogViewBuilder()
        .title("I'm TITLE")
        .message("I'm MESSAGE")
        .stackButtons(true)
        .positiveButton("Push an OpaqueController", new OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            getRouter().pushController(
                RouterTransaction.with(new OpaqueController(getUseAttacher()))
                    .pushChangeHandler(getChangeHandler(OpaqueController.getRemoveFromViewOnPush()))
                    .popChangeHandler(getChangeHandler(OpaqueController.getRemoveFromViewOnPush()))
            );
          }
        }).build(inflater, container);
  }

  public static boolean getRemoveFromViewOnPush() {
    return false;
  }
}
