package net.ess3.testserver;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.BlockChangeDelegate;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Difficulty;
import org.bukkit.Effect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.TreeType;
import org.bukkit.WorldType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class TestWorld implements World {

	@Override
	public String getName()
	{
		return "TestWorld";
	}

	@Override
	public boolean generateTree(Location safeLocation, TreeType tree)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getHighestBlockYAt(int topX, int topZ)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public UUID getUID()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Location getSpawnLocation()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setStorm(boolean b)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setWeatherDuration(int i)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public long getTime()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean setSpawnLocation(int blockX, int blockY, int blockZ)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Block getBlockAt(int i, int i1, int i2)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Block getBlockAt(Location lctn)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getBlockTypeIdAt(int i, int i1, int i2)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getBlockTypeIdAt(Location lctn)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getHighestBlockYAt(Location lctn)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Block getHighestBlockAt(int i, int i1)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Block getHighestBlockAt(Location lctn)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Chunk getChunkAt(int i, int i1)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Chunk getChunkAt(Location lctn)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Chunk getChunkAt(Block block)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isChunkLoaded(Chunk chunk)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Chunk[] getLoadedChunks()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void loadChunk(Chunk chunk)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isChunkLoaded(int i, int i1)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isChunkInUse(int i, int i1)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void loadChunk(int i, int i1)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean loadChunk(int i, int i1, boolean bln)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean unloadChunk(Chunk chunk)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean unloadChunk(int i, int i1)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean unloadChunk(int i, int i1, boolean bln)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean unloadChunk(int i, int i1, boolean bln, boolean bln1)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean unloadChunkRequest(int i, int i1)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean unloadChunkRequest(int i, int i1, boolean bln)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean regenerateChunk(int i, int i1)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean refreshChunk(int i, int i1)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Item dropItem(Location lctn, ItemStack is)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Item dropItemNaturally(Location lctn, ItemStack is)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Arrow spawnArrow(Location lctn, Vector vector, float f, float f1)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean generateTree(Location lctn, TreeType tt, BlockChangeDelegate bcd)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Entity spawnEntity(Location lctn, EntityType et)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public LivingEntity spawnCreature(Location lctn, EntityType et)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public LivingEntity spawnCreature(Location lctn, CreatureType ct)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public LightningStrike strikeLightning(Location lctn)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public LightningStrike strikeLightningEffect(Location lctn)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public List<Entity> getEntities()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public List<LivingEntity> getLivingEntities()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public <T extends Entity> Collection<T> getEntitiesByClass(Class<T>... types)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public <T extends Entity> Collection<T> getEntitiesByClass(Class<T> type)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Collection<Entity> getEntitiesByClasses(Class<?>... types)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public List<Player> getPlayers()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setTime(long l)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public long getFullTime()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setFullTime(long l)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean hasStorm()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getWeatherDuration()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isThundering()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setThundering(boolean bln)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getThunderDuration()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setThunderDuration(int i)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean createExplosion(double d, double d1, double d2, float f)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean createExplosion(double d, double d1, double d2, float f, boolean bln)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean createExplosion(Location lctn, float f)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean createExplosion(Location lctn, float f, boolean bln)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Environment getEnvironment()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public long getSeed()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean getPVP()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setPVP(boolean bln)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ChunkGenerator getGenerator()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void save()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public List<BlockPopulator> getPopulators()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public <T extends Entity> T spawn(Location lctn, Class<T> type) throws IllegalArgumentException
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public FallingBlock spawnFallingBlock(Location lctn, Material mtrl, byte b) throws IllegalArgumentException
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public FallingBlock spawnFallingBlock(Location lctn, int i, byte b) throws IllegalArgumentException
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void playEffect(Location lctn, Effect effect, int i)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void playEffect(Location lctn, Effect effect, int i, int i1)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public <T> void playEffect(Location lctn, Effect effect, T t)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public <T> void playEffect(Location lctn, Effect effect, T t, int i)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ChunkSnapshot getEmptyChunkSnapshot(int i, int i1, boolean bln, boolean bln1)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setSpawnFlags(boolean bln, boolean bln1)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean getAllowAnimals()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean getAllowMonsters()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Biome getBiome(int i, int i1)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setBiome(int i, int i1, Biome biome)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public double getTemperature(int i, int i1)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public double getHumidity(int i, int i1)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getMaxHeight()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getSeaLevel()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean getKeepSpawnInMemory()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setKeepSpawnInMemory(boolean bln)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isAutoSave()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setAutoSave(boolean bln)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setDifficulty(Difficulty dfclt)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Difficulty getDifficulty()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public File getWorldFolder()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public WorldType getWorldType()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean canGenerateStructures()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public long getTicksPerAnimalSpawns()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setTicksPerAnimalSpawns(int i)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public long getTicksPerMonsterSpawns()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setTicksPerMonsterSpawns(int i)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getMonsterSpawnLimit()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setMonsterSpawnLimit(int i)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getAnimalSpawnLimit()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setAnimalSpawnLimit(int i)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getWaterAnimalSpawnLimit()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setWaterAnimalSpawnLimit(int i)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void playSound(Location lctn, Sound sound, float f, float f1)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void sendPluginMessage(Plugin plugin, String string, byte[] bytes)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Set<String> getListeningPluginChannels()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setMetadata(String string, MetadataValue mv)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public List<MetadataValue> getMetadata(String string)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean hasMetadata(String string)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void removeMetadata(String string, Plugin plugin)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
