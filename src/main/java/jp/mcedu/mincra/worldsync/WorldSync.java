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
import jp.mcedu.mincra.worldsync.listener.BlockListener;
import jp.mcedu.mincra.worldsync.slave.FetchThread;
import jp.mcedu.mincra.worldsync.slave.WorldApply;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

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
        getLogger().info("  Master: " + config.getMasterAddress() + ":" + config.getMasterPort());
        getLogger().info("  Slave : " + config.getSlaveAddress() + ":" + config.getSlavePort());

        // Objects initialize
        queue = new ConcurrentLinkedQueue<>();

        initRedis();

        // Event Listener
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);

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
        getLogger().info("Disabled plugin successfully.");
    }

    private void initRedis() {
        masterPool = new JedisPool(new JedisPoolConfig(), config.getMasterAddress(), config.getMasterPort(),
                Protocol.DEFAULT_TIMEOUT, config.getMasterPassword(), Protocol.DEFAULT_DATABASE);
        slavePool = new JedisPool(new JedisPoolConfig(), config.getSlaveAddress(), config.getSlavePort(),
                Protocol.DEFAULT_TIMEOUT, config.getSlavePassword(), Protocol.DEFAULT_DATABASE);
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
