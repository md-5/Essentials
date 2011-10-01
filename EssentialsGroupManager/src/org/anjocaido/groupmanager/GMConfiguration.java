/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.groupmanager;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

import org.anjocaido.groupmanager.utils.Tasks;
import org.bukkit.util.config.Configuration;

/**
 *
 * @author gabrielcouto
 */
public class GMConfiguration {

    private GroupManager plugin;
    private File configFile;
    private Configuration GMconfig;
    
    public GMConfiguration(GroupManager plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            try {
                Tasks.copy(plugin.getResourceAsStream("config.yml"), configFile);
            } catch (IOException ex) {
                GroupManager.logger.log(Level.SEVERE, null, ex);
            }
        }

        GMconfig = new Configuration(configFile);
        
        try {
        	GMconfig.load();
        } catch (Exception ex) {
            throw new IllegalArgumentException("The following file couldn't pass on Parser.\n" + configFile.getPath(), ex);
        }
        adjustLoggerLevel();
    }
    
    public boolean isOpOverride() {
    	return GMconfig.getBoolean("settings.config.opOverrides", true);
    }

    @SuppressWarnings("unchecked")
	public Map<String, Object> getMirrorsMap() {    	
    	return (Map<String, Object>) GMconfig.getProperty("settings.permission.world.mirror");
    }

    public Integer getSaveInterval() {   	
    	return GMconfig.getInt("settings.data.save.minutes", 10);
    }

    public void adjustLoggerLevel() {
    	
    	try {
            GroupManager.logger.setLevel(Level.parse(GMconfig.getString("settings.logging.level", "INFO")));
            return;
        } catch (Exception e) {
        }
    	
    	GroupManager.logger.setLevel(Level.INFO);
    }
}