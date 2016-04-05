package io.github.darhkdevelopments.taskplugin;

import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.ChatComponentText;
import net.minecraft.server.v1_9_R1.IChatBaseComponent;
import net.minecraft.server.v1_9_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_9_R1.PacketPlayOutUpdateSign;

/*
 * Listeners for the task plugin
 */
public class TPListeners implements Listener
{
	
	
	/*
	 *  Hook the player move event in order to detect whether or not a sign should be displayed
	 */
	@EventHandler(ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent ev)
	{
		//System.out.println("Moving");
		ArrayList<Block> signs = new ArrayList<Block>();
		Player p = ev.getPlayer();
		Location toLocation = ev.getTo();
		Chunk ch = toLocation.getChunk();
		World w = toLocation.getWorld();
		
		
		int cX = ch.getX() << 4;
		int cZ = ch.getZ() << 4;
		
		/* block radius check */
		for(int x = toLocation.getBlockX() - 20; x < toLocation.getBlockX() + 20; x++ )
		{
			for(int z = toLocation.getBlockZ() - 20; z < toLocation.getBlockZ() + 20; z++)
			{
				for(int y = toLocation.getBlockY() - 20; y < toLocation.getBlockY() + 20; y++)
				{
					Block block = w.getBlockAt(x, y, z); 
					
					if(block != null)
					{
						/* Only collect signs */
						if(block.getType() == Material.SIGN_POST || block.getType() == Material.SIGN || block.getType() == Material.WALL_SIGN)
						{
							signs.add(block);
						}
					}
				}
				
			}
		
		}
		
		/* Iterate through all the collected sign blocks, and selectively display their texts */
		for(Block sign : signs)
		{
			Sign targetSign = (Sign)sign.getState();
			Location signLocation = targetSign.getLocation();
			CraftWorld cWorld = (CraftWorld) signLocation.getWorld();
			BlockPosition bPos = new BlockPosition(signLocation.getX(), signLocation.getY(), signLocation.getZ());
			
			/* If the player will be out of our display radius */
			if(toLocation.distance(targetSign.getLocation()) > 4)
			{
				//System.out.println("Out of Range");
				IChatBaseComponent[] t2 = new IChatBaseComponent[] 
				{
					ChatSerializer.a(this.stringToJSONString(" ")),
                    ChatSerializer.a(this.stringToJSONString(" ")),
                    ChatSerializer.a(this.stringToJSONString(" ")),
                    ChatSerializer.a(this.stringToJSONString(" "))
				};
				
				
				PacketPlayOutUpdateSign updateSignPacket = new PacketPlayOutUpdateSign(cWorld.getHandle(), bPos, t2);
				
				((CraftPlayer)p).getHandle().playerConnection.sendPacket(updateSignPacket);
			}
			
			/* If the player is within our display radius */
			else
			{
				//System.out.println("In Range");
				IChatBaseComponent[] t2 = new IChatBaseComponent[] 
				{
                    ChatSerializer.a(this.stringToJSONString(targetSign.getLine(0))),
                    ChatSerializer.a(this.stringToJSONString(targetSign.getLine(1))),
                    ChatSerializer.a(this.stringToJSONString(targetSign.getLine(2))),
                    ChatSerializer.a(this.stringToJSONString(targetSign.getLine(3)))
				};
				
				
				PacketPlayOutUpdateSign updateSignPacket = new PacketPlayOutUpdateSign(cWorld.getHandle(), bPos, t2);
				
				((CraftPlayer)p).getHandle().playerConnection.sendPacket(updateSignPacket);
				
			}
		}
		
	}
	
	
	/*
	 * Converts a string into it's json representation
	 */
	public String stringToJSONString(String s)
	{
		if(s == null)
		{
			s = " ";
		}
	    return "{\"text\":\"" + s + "\"}";
	}
}
