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

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    private int fetchInterval;

    private String masterAddress;

    private int masterPort;

    private String masterPassword;

    private String slaveAddress;

    private int slavePort;

    private String slavePassword;

    private String tableName;

    private Config() {

    }

    public static Config load(FileConfiguration config) {
        Config c = new Config();
        ConfigurationSection generic = config.getConfigurationSection("generic");
        ConfigurationSection redis = config.getConfigurationSection("redis");
        ConfigurationSection master = redis.getConfigurationSection("master");
        ConfigurationSection slave = redis.getConfigurationSection("slave");
        c.fetchInterval = generic.getInt("fetch_interval");
        c.masterAddress = master.getString("address");
        c.masterPort = master.getInt("port");
        c.masterPassword = master.getString("password", null);
        c.slaveAddress = slave.getString("address");
        c.slavePort = slave.getInt("port");
        c.slavePassword = slave.getString("password", null);
        c.tableName = redis.getString("table");
        return c;
    }

    public int getFetchInterval() {
        return fetchInterval;
    }

    public String getMasterAddress() {
        return masterAddress;
    }

    public int getMasterPort() {
        return masterPort;
    }

    public String getMasterPassword() {
        return masterPassword;
    }

    public String getSlaveAddress() {
        return slaveAddress;
    }

    public int getSlavePort() {
        return slavePort;
    }

    public String getSlavePassword() {
        return slavePassword;
    }

    public String getTableName() {
        return tableName;
    }
}
