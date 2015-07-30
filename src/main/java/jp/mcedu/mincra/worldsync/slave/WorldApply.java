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

package jp.mcedu.mincra.worldsync.slave;

import com.google.gson.JsonObject;
import jp.mcedu.mincra.worldsync.WorldSync;
import org.bukkit.Bukkit;
import org.bukkit.Material;

public class WorldApply implements Runnable {
    private WorldSync plugin;

    public WorldApply(WorldSync plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            JsonObject data = plugin.getQueue().poll();
            if (data == null) {
                return;
            }
            int x = data.get("x").getAsInt();
            int y = data.get("y").getAsInt();
            int z = data.get("z").getAsInt();
            Bukkit.getServer().getWorld("world").getBlockAt(x, y + 1, z).setType(Material.AIR);
        }
    }
}