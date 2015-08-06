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
import org.bukkit.event.block.BlockPlaceEvent;
import redis.clients.jedis.Jedis;

public class BlockListener implements Listener {
    private WorldSync plugin;

    public BlockListener(WorldSync plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Gson gson = new Gson();
        // Store block info
        JsonObject json = new JsonObject();
        json.addProperty("t", 0);                           // type
        json.addProperty("x", block.getX());                // x
        json.addProperty("y", block.getY());                // y
        json.addProperty("z", block.getZ());                // z
        json.addProperty("n", event.getPlayer().getDisplayName());   // player name
        String str = gson.toJson(json);
        try (Jedis jedis = plugin.getMasterPool().getResource()) {
            jedis.rpush(plugin.getLocalConfig().getTableName(), str);
        }
        plugin.getLogger().info("Block break : " + str);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Gson gson = new Gson();
        // Store block info
        JsonObject json = new JsonObject();
        json.addProperty("t", 1);                           // type
        json.addProperty("x", block.getX());                // x
        json.addProperty("y", block.getY());                // y
        json.addProperty("z", block.getZ());                // z
        json.addProperty("m", block.getType().ordinal());   // material
        //noinspection deprecation
        json.addProperty("d", block.getState().getData().getData()); // metadata
        json.addProperty("n", event.getPlayer().getDisplayName());   // player name
        // TODO: Store TileEntity or more optional data(e.g. Sign lines)
        String str = gson.toJson(json);
        try (Jedis jedis = plugin.getMasterPool().getResource()) {
            jedis.rpush(plugin.getLocalConfig().getTableName(), str);
        }
        plugin.getLogger().info("Block place : " + str);
    }
}
