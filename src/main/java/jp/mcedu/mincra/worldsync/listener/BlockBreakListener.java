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

package jp.mcedu.mincra.worldsync.listener;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jp.mcedu.mincra.worldsync.WorldSync;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import redis.clients.jedis.Jedis;

public class BlockBreakListener implements Listener {
    private WorldSync plugin;

    public BlockBreakListener(WorldSync plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Gson gson = new Gson();
        // Store block info
        // TODO: Add metadata if target block has
        JsonObject json = new JsonObject();
        json.addProperty("t", 0);
        json.addProperty("x", block.getX());
        json.addProperty("y", block.getY());
        json.addProperty("z", block.getZ());
        json.addProperty("m", block.getType().ordinal());
        String str = gson.toJson(json);
        try (Jedis jedis = plugin.getMasterPool().getResource()) {
            jedis.rpush(plugin.getLocalConfig().getTableName(), str);
        }
        plugin.getLogger().info("Block break : " + str);
    }
}
