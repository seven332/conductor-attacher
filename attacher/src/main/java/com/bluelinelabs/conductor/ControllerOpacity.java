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

public interface ControllerOpacity {

  /**
   * The Controller which is under current Controller can always be seen.
   */
  int TRANSPARENT = 0;

  /**
   * The Controller which is under current Controller can only be seen if
   * current Controller is the top Controller.
   */
  int TRANSLUCENT = 1;

  /**
   * The Controller which is under current Controller will never be seen.
   */
  int OPAQUE = 2;

  /**
   * Returns the opacity value of this Controller.
   * Must be one of {@link #TRANSPARENT}, {@link #TRANSLUCENT} and {@link #OPAQUE}.
   */
  int getOpaque();
}
