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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jp.mcedu.mincra.worldsync.WorldSync;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class FetchThread extends Thread {

    private WorldSync plugin;

    private AtomicBoolean stop = new AtomicBoolean(false);

    public FetchThread(WorldSync plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        int id = 0;
        Gson gson = new Gson();
        while (!stop.get()) {
            try (Jedis jedis = plugin.getSlavePool().getResource()) {
                sleep(plugin.getLocalConfig().getFetchInterval());
                List<String> block_l = jedis.lrange(plugin.getLocalConfig().getTableName(), id, -1);
                for (String s : block_l) {
                    JsonObject data = gson.fromJson(s, JsonObject.class);
                    plugin.getQueue().offer(data);
                }
                id += block_l.size();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            sleep(100);
        } catch (InterruptedException ignored) {
        }
        notifyAll();
    }

    public synchronized void stopThread() {
        plugin.getLogger().info("Stopping fetch thread...");
        stop.set(true);
        try {
            this.wait(1000);
            plugin.getLogger().info("Stopped fetch thread successfully!");
        } catch (InterruptedException ignored) {
        }
    }
}
