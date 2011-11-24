package com.earth2me.essentials.storage;

import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;


public class BukkitConstructor extends Constructor
{
	private final transient Pattern NUMPATTERN = Pattern.compile("\\d+");

	public BukkitConstructor(Class clazz)
	{
		super(clazz);
		yamlClassConstructors.put(NodeId.scalar, new ConstructBukkitScalar());
		yamlClassConstructors.put(NodeId.mapping, new ConstructBukkitMapping());
	}


	private class ConstructBukkitScalar extends ConstructScalar
	{
		@Override
		public Object construct(final Node node)
		{
			if (node.getType().equals(Material.class))
			{
				final String val = (String)constructScalar((ScalarNode)node);
				Material mat;
				if (NUMPATTERN.matcher(val).matches())
				{
					final int typeId = Integer.parseInt(val);
					mat = Material.getMaterial(typeId);
				}
				else
				{
					mat = Material.matchMaterial(val);
				}
				return mat;
			}
			if (node.getType().equals(MaterialData.class))
			{
				final String val = (String)constructScalar((ScalarNode)node);
				if (val.isEmpty())
				{
					return null;
				}
				final String[] split = val.split("[:+',;.]", 2);
				if (split.length == 0)
				{
					return null;
				}
				Material mat;
				if (NUMPATTERN.matcher(split[0]).matches())
				{
					final int typeId = Integer.parseInt(split[0]);
					mat = Material.getMaterial(typeId);
				}
				else
				{
					mat = Material.matchMaterial(split[0]);
				}
				byte data = 0;
				if (split.length == 2 && NUMPATTERN.matcher(split[1]).matches())
				{
					data = Byte.parseByte(split[1]);
				}
				return new MaterialData(mat, data);
			}
			if (node.getType().equals(ItemStack.class))
			{
				final String val = (String)constructScalar((ScalarNode)node);
				if (val.isEmpty())
				{
					return null;
				}
				final String[] split1 = val.split("\\W", 2);
				if (split1.length == 0)
				{
					return null;
				}
				final String[] split2 = split1[0].split("[:+',;.]", 2);
				if (split2.length == 0)
				{
					return null;
				}
				Material mat;
				if (NUMPATTERN.matcher(split2[0]).matches())
				{
					final int typeId = Integer.parseInt(split2[0]);
					mat = Material.getMaterial(typeId);
				}
				else
				{
					mat = Material.matchMaterial(split2[0]);
				}
				short data = 0;
				if (split2.length == 2 && NUMPATTERN.matcher(split2[1]).matches())
				{
					data = Short.parseShort(split2[1]);
				}
				int size = mat.getMaxStackSize();
				if (split1.length == 2 && NUMPATTERN.matcher(split1[1]).matches())
				{
					size = Integer.parseInt(split1[1]);
				}
				return new ItemStack(mat, size, data);
			}
			return super.construct(node);
		}
	}


	private class ConstructBukkitMapping extends ConstructMapping
	{
		@Override
		public Object construct(final Node node)
		{
			if (node.getType().equals(Location.class))
			{
				//TODO: NPE checks
				final MappingNode mnode = (MappingNode)node;
				String worldName = "";
				double x = 0, y = 0, z = 0;
				float yaw = 0, pitch = 0;
				if (mnode.getValue().size() < 4)
				{
					return null;
				}
				for (NodeTuple nodeTuple : mnode.getValue())
				{
					final String key = (String)constructScalar((ScalarNode)nodeTuple.getKeyNode());
					final ScalarNode snode = (ScalarNode)nodeTuple.getValueNode();
					if (key.equalsIgnoreCase("world"))
					{
						worldName = (String)constructScalar(snode);
					}
					if (key.equalsIgnoreCase("x"))
					{
						x = Double.parseDouble((String)constructScalar(snode));
					}
					if (key.equalsIgnoreCase("y"))
					{
						y = Double.parseDouble((String)constructScalar(snode));
					}
					if (key.equalsIgnoreCase("z"))
					{
						z = Double.parseDouble((String)constructScalar(snode));
					}
					if (key.equalsIgnoreCase("yaw"))
					{
						yaw = Float.parseFloat((String)constructScalar(snode));
					}
					if (key.equalsIgnoreCase("pitch"))
					{
						pitch = Float.parseFloat((String)constructScalar(snode));
					}
				}
				if (worldName == null || worldName.isEmpty())
				{
					return null;
				}
				final World world = Bukkit.getWorld(worldName);
				if (world == null)
				{
					return null;
				}
				return new Location(world, x, y, z, yaw, pitch);
			}
			return super.construct(node);
		}
	}
}
