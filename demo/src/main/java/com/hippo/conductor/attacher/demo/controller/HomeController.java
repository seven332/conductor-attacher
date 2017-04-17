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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.bluelinelabs.conductor.RouterTransaction;
import com.hippo.conductor.attacher.demo.R;

public class HomeController extends BaseController {

  public HomeController(boolean useAttacher) {
    super(useAttacher);
  }

  public HomeController(Bundle args) {
    super(args);
  }

  @Override
  public int getOpacity() {
    return OPAQUE;
  }

  @NonNull
  @Override
  protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
    View view = inflater.inflate(R.layout.controller_home, container, false);
    ListView listView = (ListView) view.findViewById(R.id.list);
    listView.setAdapter(new ArrayAdapter<>(inflater.getContext(), android.R.layout.simple_list_item_1,
        new String[] {
            "Dialog",
            "Swipe to Finish",
        }
    ));
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
          case 0:
            getRouter().pushController(
                RouterTransaction.with(new SimpleDialogController(getUseAttacher()))
                    .pushChangeHandler(getChangeHandler(SimpleDialogController.getRemoveFromViewOnPush()))
                    .popChangeHandler(getChangeHandler(SimpleDialogController.getRemoveFromViewOnPush()))
            );
            break;
          case 1:
          default:
            getRouter().pushController(
                RouterTransaction.with(new SwipeBackController(getUseAttacher()))
                    .pushChangeHandler(getChangeHandler(SwipeBackController.getRemoveFromViewOnPush()))
                    .popChangeHandler(getChangeHandler(SwipeBackController.getRemoveFromViewOnPush()))
            );
            break;
        }
      }
    });
    return view;
  }

  public static boolean getRemoveFromViewOnPush() {
    return true;
  }
}
