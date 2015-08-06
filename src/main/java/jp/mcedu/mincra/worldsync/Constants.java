/*
 * Copyright 2015 TENTO, Mincra, Ralph
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

package jp.mcedu.mincra.worldsync;

import java.util.Arrays;

public class Constants {
    public static final int COMMAND_BLOCK_BREAK = 0;
    public static final int COMMAND_BLOCK_PLACE = 1;
    public static final int COMMAND_SIGN_CHANGE = 2;

    public static final int[] OPTIONAL_TYPES = {
    };

    static {
        Arrays.sort(OPTIONAL_TYPES);
    }
}
