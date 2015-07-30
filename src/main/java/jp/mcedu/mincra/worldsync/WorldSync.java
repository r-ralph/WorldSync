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

import com.google.gson.JsonObject;
import jp.mcedu.mincra.worldsync.listener.BlockBreakListener;
import jp.mcedu.mincra.worldsync.slave.FetchThread;
import jp.mcedu.mincra.worldsync.slave.WorldApply;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.ConcurrentLinkedQueue;

public class WorldSync extends JavaPlugin {
    private Config config;
    private JedisPool masterPool;
    private JedisPool slavePool;
    private FetchThread fetchThread;
    private ConcurrentLinkedQueue<JsonObject> queue;

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

        // Objects initialize
        queue = new ConcurrentLinkedQueue<>();

        initRedis();

        // Event Listener
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);

        // Slave scan thread
        fetchThread = new FetchThread(this);
        fetchThread.start();

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new WorldApply(this), 0L, 10L);

        getLogger().info("Enabled plugin successfully.");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        masterPool.destroy();
        fetchThread.stopThread();
    }

    private void initRedis() {
        masterPool = new JedisPool(new JedisPoolConfig(), config.getMasterAddress(), config.getMasterPort());
        slavePool = new JedisPool(new JedisPoolConfig(), config.getSlaveAddress(), config.getSlavePort());
    }

    public JedisPool getMasterPool() {
        return masterPool;
    }

    public JedisPool getSlavePool() {
        return slavePool;
    }

    public ConcurrentLinkedQueue<JsonObject> getQueue() {
        return queue;
    }

    public Config getLocalConfig() {
        return config;
    }
}
