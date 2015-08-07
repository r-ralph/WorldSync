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
import jp.mcedu.mincra.worldsync.Constants;
import jp.mcedu.mincra.worldsync.WorldSync;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import redis.clients.jedis.Jedis;

import java.util.Arrays;

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
        json.addProperty("t", Constants.COMMAND_BLOCK_BREAK);   // type
        json.addProperty("x", block.getX());                    // x
        json.addProperty("y", block.getY());                    // y
        json.addProperty("z", block.getZ());                    // z
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
        int type = block.getType().ordinal();
        Gson gson = new Gson();
        // Store block info
        JsonObject json = new JsonObject();
        json.addProperty("t", Constants.COMMAND_BLOCK_PLACE);   // type
        json.addProperty("x", block.getX());                    // x
        json.addProperty("y", block.getY());                    // y
        json.addProperty("z", block.getZ());                    // z
        json.addProperty("m", type);                            // material
        //noinspection deprecation
        json.addProperty("d", block.getState().getData().getData()); // metadata
        json.addProperty("n", event.getPlayer().getDisplayName());   // player name
        // Optional data
        JsonObject optional = getOptional(block, type);
        if (optional != null) {
            json.add("o", optional);
        }
        String str = gson.toJson(json);
        try (Jedis jedis = plugin.getMasterPool().getResource()) {
            jedis.rpush(plugin.getLocalConfig().getTableName(), str);
        }
        plugin.getLogger().info("Block place : " + str);
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Block block = event.getBlock();
        Gson gson = new Gson();
        // Store block info
        JsonObject json = new JsonObject();
        json.addProperty("t", Constants.COMMAND_SIGN_CHANGE);   // type
        json.addProperty("x", block.getX());                    // x
        json.addProperty("y", block.getY());                    // y
        json.addProperty("z", block.getZ());                    // z

        JsonObject lines = new JsonObject();                    // lines
        lines.addProperty("0", event.getLine(0));
        lines.addProperty("1", event.getLine(1));
        lines.addProperty("2", event.getLine(2));
        lines.addProperty("3", event.getLine(3));
        json.add("o", lines);

        String str = gson.toJson(json);
        try (Jedis jedis = plugin.getMasterPool().getResource()) {
            jedis.rpush(plugin.getLocalConfig().getTableName(), str);
        }
        plugin.getLogger().info("Sign change : " + str);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        Block block = event.getClickedBlock().getRelative(event.getBlockFace());
        if (block.getType() == Material.FIRE) {
            // Fire block was broken by player
            Gson gson = new Gson();
            // Store block info
            JsonObject json = new JsonObject();
            json.addProperty("t", Constants.COMMAND_BLOCK_BREAK);   // type
            json.addProperty("x", block.getX());                    // x
            json.addProperty("y", block.getY());                    // y
            json.addProperty("z", block.getZ());                    // z
            json.addProperty("n", event.getPlayer().getDisplayName());   // player name
            String str = gson.toJson(json);
            try (Jedis jedis = plugin.getMasterPool().getResource()) {
                jedis.rpush(plugin.getLocalConfig().getTableName(), str);
            }
            plugin.getLogger().info("Block break : " + str);
        }
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        // 設置
        Block block = event.getBlockClicked().getRelative(event.getBlockFace());
        int type = event.getBucket() == Material.WATER_BUCKET ? Material.WATER.ordinal() : Material.LAVA.ordinal();
        Gson gson = new Gson();
        // Store block info
        JsonObject json = new JsonObject();
        json.addProperty("t", Constants.COMMAND_BLOCK_PLACE);   // type
        json.addProperty("x", block.getX());                    // x
        json.addProperty("y", block.getY());                    // y
        json.addProperty("z", block.getZ());                    // z
        json.addProperty("m", type);                            // material
        //noinspection deprecation
        json.addProperty("d", 0); // metadata
        json.addProperty("n", event.getPlayer().getDisplayName());   // player name
        String str = gson.toJson(json);
        try (Jedis jedis = plugin.getMasterPool().getResource()) {
            jedis.rpush(plugin.getLocalConfig().getTableName(), str);
        }
        plugin.getLogger().info("Block place : " + str);
    }

    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        // 除去
        Block block = event.getBlockClicked().getRelative(event.getBlockFace());
        Gson gson = new Gson();
        // Store block info
        JsonObject json = new JsonObject();
        json.addProperty("t", Constants.COMMAND_BLOCK_BREAK);   // type
        json.addProperty("x", block.getX());                    // x
        json.addProperty("y", block.getY());                    // y
        json.addProperty("z", block.getZ());                    // z
        json.addProperty("n", event.getPlayer().getDisplayName());   // player name
        String str = gson.toJson(json);
        try (Jedis jedis = plugin.getMasterPool().getResource()) {
            jedis.rpush(plugin.getLocalConfig().getTableName(), str);
        }
        plugin.getLogger().info("Block break : " + str);
    }

    private JsonObject getOptional(Block block, int type) {
        if (Arrays.binarySearch(Constants.OPTIONAL_TYPES, type) < 0) {
            // Block hasn't optional data
            return null;
        }
        JsonObject optional = new JsonObject();
        switch (type) {
            default:
                optional = null;
                break;
        }
        return optional;
    }
}
