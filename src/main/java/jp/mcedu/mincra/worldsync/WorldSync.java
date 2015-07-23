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

import jp.mcedu.mincra.worldsync.listener.BlockBreakListener;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldSync extends JavaPlugin {
    private Config config;

    @Override
    public void onEnable() {
        // Config
        getConfig().options().copyDefaults(true);
        saveConfig();
        reloadConfig();

        config = Config.load(getConfig());
        getLogger().info("Redis configuration:");
        getLogger().info("  Master: " + config.getMasterAddress());
        getLogger().info("  Slave : " + config.getSlaveAddress());

        // Event Listener
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);

        getLogger().info("Enabled plugin successfully.");
    }
}
