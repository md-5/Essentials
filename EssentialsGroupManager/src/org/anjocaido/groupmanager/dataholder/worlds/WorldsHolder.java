/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.groupmanager.dataholder.worlds;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.WorldDataHolder;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.anjocaido.groupmanager.utils.Tasks;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author gabrielcouto
 */
public class WorldsHolder {

    /**
     * Map with instances of loaded worlds.
     */
    private Map<String, OverloadedWorldHolder> worldsData = new HashMap<String, OverloadedWorldHolder>();
    /**
     * Map of mirrors: <nonExistingWorldName, existingAndLoadedWorldName>
     * The key is the mirror.
     * The object is the mirrored.
     *
     * Mirror shows the same data of mirrored.
     */
    private Map<String, String> mirrors = new HashMap<String, String>();
    private OverloadedWorldHolder defaultWorld;
    private String serverDefaultWorldName;
    private GroupManager plugin;
    private File worldsFolder;

    /**
     *
     * @param plugin
     */
    public WorldsHolder(GroupManager plugin) {
        this.plugin = plugin;
        // Setup folders and check files exist for the primary world
        verifyFirstRun();
        initialLoad();
        if (defaultWorld == null) {
            throw new IllegalStateException("There is no default group! OMG!");
        }
    }

    private void initialLoad() {
    	// load the initial world
        initialWorldLoading();
        // Configure and load any mirrors and additional worlds as defined in config.yml
        mirrorSetUp();
        // search the worlds folder for any manually created worlds (not listed in config.yml)
        loadAllSearchedWorlds();
    }

    private void initialWorldLoading() {
        //Load the default world
        loadWorld(serverDefaultWorldName);
        defaultWorld = worldsData.get(serverDefaultWorldName);
    }
    
    private void loadAllSearchedWorlds() {
    	
    	/*
    	 *  Read all known worlds from Bukkit
    	 *  Create the data files if they don't already exist,
    	 *  and they are not mirrored.
    	 */
    	for (World world: plugin.getServer().getWorlds())
    		if ((!worldsData.containsKey(world.getName().toLowerCase()))
    				&& (!mirrors.containsKey(world.getName().toLowerCase())))
    			setupWorldFolder(world.getName());
    	/*
    	 * Loop over all folders within the worlds folder
    	 * and attempt to load the world data
    	 */
        for (File folder : worldsFolder.listFiles()) {
        	if (folder.isDirectory()) {
        		GroupManager.logger.info("World Found: " + folder.getName());
        	
        		/*
        		 * don't load any worlds which are already loaded
        		 * or mirrored worlds that don't need data.
        		 */
	        	if (!worldsData.containsKey(folder.getName().toLowerCase())
	        			&& !mirrors.containsKey(folder.getName().toLowerCase())) {
	        		loadWorld(folder.getName());
	            }
	            
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public void mirrorSetUp() {
        mirrors.clear();
        Map<String, Object> mirrorsMap = plugin.getGMConfig().getMirrorsMap();
        if (mirrorsMap != null) {
            for (String source : mirrorsMap.keySet()) {
            	// Make sure all non mirrored worlds have a set of data files.
            	setupWorldFolder(source);
            	// Load the world data
            	if (!worldsData.containsKey(source.toLowerCase()))
            		loadWorld(source);
            	
                if (mirrorsMap.get(source) instanceof ArrayList) {
                    ArrayList mirrorList = (ArrayList) mirrorsMap.get(source);
                    for (Object o : mirrorList) {
                        try {
                            mirrors.remove(o.toString().toLowerCase());
                        } catch (Exception e) {
                        }
                        mirrors.put(o.toString().toLowerCase(), getWorldData(source).getName());
                    }
                } else if (mirrorsMap.get(source) instanceof Object) {
                    String aMirror = mirrorsMap.get(source).toString();
                    mirrors.put(aMirror.toLowerCase(), getWorldData(source).getName());
                }
            }
        }
    }

    /**
     *
     */
    public void reloadAll() {
        ArrayList<WorldDataHolder> alreadyDone = new ArrayList<WorldDataHolder>();
        for (WorldDataHolder w : worldsData.values()) {
            if (alreadyDone.contains(w)) {
                continue;
            }
            w.reload();
            alreadyDone.add(w);
        }
        // Load global groups
        GroupManager.getGlobalGroups().load();
    }

    /**
     *
     * @param worldName
     */
    public void reloadWorld(String worldName) {
        getWorldData(worldName).reload();
    }
    
    /**
     * Wrapper to retain backwards compatibility
     * (call this function to auto overwrite files)
     */
    public void saveChanges() {
    	saveChanges(true);
    }

    /**
     *
     */
    public void saveChanges(boolean overwrite) {
        ArrayList<WorldDataHolder> alreadyDone = new ArrayList<WorldDataHolder>();
        Tasks.removeOldFiles(plugin, plugin.getBackupFolder());
        
        for (OverloadedWorldHolder w : worldsData.values()) {
            if (alreadyDone.contains(w)) {
                continue;
            }
            if (w == null) {
                GroupManager.logger.severe("WHAT HAPPENED?");
                continue;
            }
            if (w.haveGroupsChanged()) {
            	if (overwrite || (!overwrite && (w.getTimeStampGroups() >= w.getGroupsFile().lastModified()))) {
	                // Backup Groups file
            		backupFile(w,true);
            		
	                WorldDataHolder.writeGroups(w, w.getGroupsFile());
	                //w.removeGroupsChangedFlag();
            	} else {
            		// Newer file found.
            		GroupManager.logger.log(Level.WARNING, "Newer Groups file found for " + w.getName() + ", but we have local changes!");
            		throw new IllegalStateException("Unable to save unless you issue a '/mansave force'");
            	}
            } else {
            	//Check for newer file as no local changes.
            	if (w.getTimeStampGroups() < w.getGroupsFile().lastModified()) {
            		System.out.print("Newer Groups file found (Loading changes)!");
            		// Backup Groups file
	            	backupFile(w,true);
            		w.reloadGroups();
            	}
            }
            if (w.haveUsersChanged()) {
            	if (overwrite || (!overwrite && (w.getTimeStampUsers() >= w.getUsersFile().lastModified()))) {
	            	// Backup Users file
	            	backupFile(w,false);
	            	
	                WorldDataHolder.writeUsers(w, w.getUsersFile());
	                //w.removeUsersChangedFlag();
            	} else {
            		// Newer file found.
            		GroupManager.logger.log(Level.WARNING, "Newer Users file found for " + w.getName() + ", but we have local changes!");
            		throw new IllegalStateException("Unable to save unless you issue a '/mansave force'");
            	}
            } else {
            	//Check for newer file as no local changes.
            	if (w.getTimeStampUsers() < w.getUsersFile().lastModified()) {
            		System.out.print("Newer Users file found (Loading changes)!");
            		// Backup Users file
	            	backupFile(w,false);
            		w.reloadUsers();
            	}
            }
            alreadyDone.add(w);
        }
        // Write Global Groups
        if (GroupManager.getGlobalGroups().haveGroupsChanged()) {
        	GroupManager.getGlobalGroups().writeGroups(overwrite);
        } else {
        	if (GroupManager.getGlobalGroups().getTimeStampGroups() < GroupManager.getGlobalGroups().getGlobalGroupsFile().lastModified()) {
        		System.out.print("Newer GlobalGroups file found (Loading changes)!");
        		GroupManager.getGlobalGroups().load();
        	}
        }
    }
    
    /**
     * Backup the Groups/Users file
     * @param w
     * @param groups
     */
    private void backupFile(OverloadedWorldHolder w, Boolean groups) {
    	
    	File backupFile = new File(plugin.getBackupFolder(), "bkp_" + w.getName() + (groups ? "_g_" : "_u_") + Tasks.getDateString() + ".yml");
        try {
            Tasks.copy((groups ? w.getGroupsFile() : w.getUsersFile()), backupFile);
        } catch (IOException ex) {
            GroupManager.logger.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns the dataHolder for the given world.
     * If the world is not on the worlds list, returns the default world
     * holder.
     *
     * (WHEN A WORLD IS CONFIGURED TO MIRROR, IT WILL BE ON THE LIST, BUT
     * POINTING TO ANOTHER WORLD HOLDER)
     *
     * Mirrors prevails original data.
     *
     * @param worldName
     * @return OverloadedWorldHolder
     */
    public OverloadedWorldHolder getWorldData(String worldName) {
        OverloadedWorldHolder data = worldsData.get(worldName.toLowerCase());
        if (mirrors.containsKey(worldName.toLowerCase())) {
            String realOne = mirrors.get(worldName.toLowerCase());
            data = worldsData.get(realOne.toLowerCase());
        }
        if (data == null) {
            GroupManager.logger.finest("Requested world " + worldName + " not found or badly mirrored. Returning default world...");
            data = getDefaultWorld();
        }
        return data;
    }

    /**
     * Do a matching of playerName, if it s found only one player, do
     * getWorldData(player)
     * @param playerName
     * @return null if matching returned no player, or more than one.
     */
    public OverloadedWorldHolder getWorldDataByPlayerName(String playerName) {
        List<Player> matchPlayer = plugin.getServer().matchPlayer(playerName);
        if (matchPlayer.size() == 1) {
            return getWorldData(matchPlayer.get(0));
        }
        return null;
    }

    /**
     * Retrieves the field player.getWorld().getName() and do
     * getWorld(worldName)
     * @param player
     * @return OverloadedWorldHolder
     */
    public OverloadedWorldHolder getWorldData(Player player) {
        return getWorldData(player.getWorld().getName());
    }

    /**
     * It does getWorld(worldName).getPermissionsHandler()
     * @param worldName
     * @return AnjoPermissionsHandler
     */
    public AnjoPermissionsHandler getWorldPermissions(String worldName) {
        return getWorldData(worldName).getPermissionsHandler();
    }

    /**
     * Returns the PermissionsHandler for this player data
     * @param player
     * @return AnjoPermissionsHandler
     */
    public AnjoPermissionsHandler getWorldPermissions(Player player) {
        return getWorldData(player).getPermissionsHandler();
    }

    /**
     * Id does getWorldDataByPlayerName(playerName).
     * If it doesnt return null, it will return result.getPermissionsHandler()
     * @param playerName
     * @return null if the player matching gone wrong.
     */
    public AnjoPermissionsHandler getWorldPermissionsByPlayerName(String playerName) {
        WorldDataHolder dh = getWorldDataByPlayerName(playerName);
        if (dh != null) {
            return dh.getPermissionsHandler();
        }
        return null;
    }

    private void verifyFirstRun() {
        
        Properties server = new Properties();
        try {
            server.load(new FileInputStream(new File("server.properties")));
            serverDefaultWorldName = server.getProperty("level-name").toLowerCase();
            setupWorldFolder(serverDefaultWorldName);
        } catch (IOException ex) {
            GroupManager.logger.log(Level.SEVERE, null, ex);
        }
        
    }
        
    public void setupWorldFolder(String worldName) {
        worldsFolder = new File(plugin.getDataFolder(), "worlds");
        if (!worldsFolder.exists()) {
            worldsFolder.mkdirs();
        }
        
        File defaultWorldFolder = new File(worldsFolder, worldName);
        if (!defaultWorldFolder.exists()) {
            defaultWorldFolder.mkdirs();
        }
        if (defaultWorldFolder.exists()) {
            File groupsFile = new File(defaultWorldFolder, "groups.yml");
            File usersFile = new File(defaultWorldFolder, "users.yml");
            File oldDataFile = new File(plugin.getDataFolder(), "data.yml");
            if (!groupsFile.exists() || groupsFile.length() == 0) {
                if (oldDataFile.exists()) {
                    try {
                        Tasks.copy(oldDataFile, groupsFile);
                    } catch (IOException ex) {
                        GroupManager.logger.log(Level.SEVERE, null, ex);
                    }
                } else {
                    InputStream template = plugin.getResourceAsStream("groups.yml");
                    try {
                        Tasks.copy(template, groupsFile);
                    } catch (IOException ex) {
                        GroupManager.logger.log(Level.SEVERE, null, ex);
                    }
                }
            }
            if (!usersFile.exists() || usersFile.length() == 0) {
                if (oldDataFile.exists()) {
                    try {
                        Tasks.copy(oldDataFile, usersFile);
                    } catch (IOException ex) {
                        GroupManager.logger.log(Level.SEVERE, null, ex);
                    }
                } else {
                    InputStream template = plugin.getResourceAsStream("users.yml");
                    try {
                        Tasks.copy(template, usersFile);
                    } catch (IOException ex) {
                        GroupManager.logger.log(Level.SEVERE, null, ex);
                    }
                }
            }
            try {
                if (oldDataFile.exists()) {
                    oldDataFile.renameTo(new File(plugin.getDataFolder(), "NOT_USED_ANYMORE_data.yml"));
                }
            } catch (Exception ex) {
            }
        }
    }

    /**
     * Copies the specified world data to another world
     * @param fromWorld
     * @param toWorld
     * @return true if successfully copied.
     */
    public boolean cloneWorld(String fromWorld, String toWorld) {
        File fromWorldFolder = new File(worldsFolder, fromWorld);
        File toWorldFolder = new File(worldsFolder, toWorld);
        if (toWorldFolder.exists() || !fromWorldFolder.exists()) {
            return false;
        }
        File fromWorldGroups = new File(fromWorldFolder, "groups.yml");
        File fromWorldUsers = new File(fromWorldFolder, "users.yml");
        if (!fromWorldGroups.exists() || !fromWorldUsers.exists()) {
            return false;
        }
        File toWorldGroups = new File(toWorldFolder, "groups.yml");
        File toWorldUsers = new File(toWorldFolder, "users.yml");
        toWorldFolder.mkdirs();
        try {
            Tasks.copy(fromWorldGroups, toWorldGroups);
            Tasks.copy(fromWorldUsers, toWorldUsers);
        } catch (IOException ex) {
            Logger.getLogger(WorldsHolder.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    /**
     * Load a world from file.
     * If it already been loaded, summon reload method from dataHolder.
     * @param worldName
     */
    public void loadWorld(String worldName) {
        if (worldsData.containsKey(worldName.toLowerCase())) {
            worldsData.get(worldName.toLowerCase()).reload();
            return;
        }
        GroupManager.logger.finest("Trying to load world " + worldName + "...");
        File thisWorldFolder = new File(worldsFolder, worldName);
        if (thisWorldFolder.exists() && thisWorldFolder.isDirectory()) {
            File groupsFile = new File(thisWorldFolder, "groups.yml");
            File usersFile = new File(thisWorldFolder, "users.yml");
            if (!groupsFile.exists()) {
                throw new IllegalArgumentException("Groups file for world '" + worldName + "' doesnt exist: " + groupsFile.getPath());
            }
            if (!usersFile.exists()) {
                throw new IllegalArgumentException("Users file for world '" + worldName + "' doesnt exist: " + usersFile.getPath());
            }
            try {
                OverloadedWorldHolder thisWorldData = new OverloadedWorldHolder(WorldDataHolder.load(worldName, groupsFile, usersFile));
                
                // Set the file TimeStamps as it will be default from the initial load.
                thisWorldData.setTimeStamps();
                
                if (thisWorldData != null) {
                    GroupManager.logger.finest("Successful load of world " + worldName + "...");
                    worldsData.put(worldName.toLowerCase(), thisWorldData);
                    return;
                }
            } catch (FileNotFoundException ex) {
                GroupManager.logger.log(Level.SEVERE, null, ex);
                return;
            } catch (IOException ex) {
                GroupManager.logger.log(Level.SEVERE, null, ex);
                return;
            }
            //GroupManager.logger.severe("Failed to load world " + worldName + "...");
        }
    }

    /**
     * Tells if the such world has been mapped.
     *
     * It will return true if world is a mirror.
     *
     * @param worldName
     * @return true if world is loaded or mirrored. false if not listed
     */
    public boolean isInList(String worldName) {
        if (worldsData.containsKey(worldName.toLowerCase()) || mirrors.containsKey(worldName.toLowerCase())) {
            return true;
        }
        return false;
    }

    /**
     * Verify if world has it's own file permissions.
     *
     * @param worldName
     * @return true if it has its own holder. false if not.
     */
    public boolean hasOwnData(String worldName) {
        if (worldsData.containsKey(worldName.toLowerCase())) {
            return true;
        }
        return false;
    }

    /**
     * @return the defaultWorld
     */
    public OverloadedWorldHolder getDefaultWorld() {
        return defaultWorld;
    }

    /**
     * Returns all physically loaded worlds.
     * @return ArrayList<OverloadedWorldHolder> of all loaded worlds
     */
    public ArrayList<OverloadedWorldHolder> allWorldsDataList() {
        ArrayList<OverloadedWorldHolder> list = new ArrayList<OverloadedWorldHolder>();
        for (OverloadedWorldHolder data : worldsData.values()) {
            if (!list.contains(data)) {
                list.add(data);
            }
        }
        return list;
    }
}
