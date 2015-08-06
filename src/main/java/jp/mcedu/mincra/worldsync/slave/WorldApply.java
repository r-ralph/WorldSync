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
import jp.mcedu.mincra.worldsync.Constants;
import jp.mcedu.mincra.worldsync.WorldSync;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import java.util.Arrays;

import static jp.mcedu.mincra.worldsync.Constants.*;

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
            switch (data.get("t").getAsInt()) {
                case COMMAND_BLOCK_BREAK:
                    onBreak(data);
                    break;
                case COMMAND_BLOCK_PLACE:
                    onPlace(data);
                    break;
                case COMMAND_SIGN_CHANGE:
                    onSignChange(data);
                    break;
                default:
                    break;
            }
        }
    }

    public void onBreak(JsonObject data) {
        int x = data.get("x").getAsInt();
        int y = data.get("y").getAsInt();
        int z = data.get("z").getAsInt();
        plugin.getLogger().info("Breaking block at (" + String.format("%d, %d, %d", x, y, z) + ")");
        World world = Bukkit.getServer().getWorld("world");
        Block block = world.getBlockAt(x, y, z);
        block.setType(Material.AIR);
    }

    public void onPlace(JsonObject data) {
        int x = data.get("x").getAsInt();
        int y = data.get("y").getAsInt();
        int z = data.get("z").getAsInt();
        int material = data.get("m").getAsInt();
        byte metadata = data.get("d").getAsByte();
        plugin.getLogger().info("Placing block at (" + String.format("%d, %d, %d", x, y, z) + ") " + String.format("t: %d, d: %s", material, metadata));
        //noinspection deprecation
        World world = Bukkit.getServer().getWorld("world");
        Block block = world.getBlockAt(x, y, z);
        //noinspection deprecation
        block.setTypeIdAndData(material, metadata, true);
        parseOptional(block, material, data);
    }

    public void onSignChange(JsonObject data) {
        int x = data.get("x").getAsInt();
        int y = data.get("y").getAsInt();
        int z = data.get("z").getAsInt();
        plugin.getLogger().info("Changing sign at (" + String.format("%d, %d, %d", x, y, z) + ")");
        World world = Bukkit.getServer().getWorld("world");
        Block block = world.getBlockAt(x, y, z);
        BlockState state = block.getState();
        if (state instanceof Sign) {
            Sign sign = ((Sign) state);
            JsonObject lines = data.getAsJsonObject("o");
            sign.setLine(0, lines.get("0").getAsString());
            sign.setLine(1, lines.get("1").getAsString());
            sign.setLine(2, lines.get("2").getAsString());
            sign.setLine(3, lines.get("3").getAsString());
            sign.update(true);
        } else {
            plugin.getLogger().severe("Expected sign at (" + String.format("%d, %d, %d", x, y, z) + ") but appears " + block.getClass().getName());
        }
    }

    private void parseOptional(Block block, int type, JsonObject data) {
        if (Arrays.binarySearch(Constants.OPTIONAL_TYPES, type) < 0) {
            // Block hasn't optional data
            return;
        }
        JsonObject optional = data.getAsJsonObject("o");
        switch (type) {
            default:
                break;
        }
    }
}
