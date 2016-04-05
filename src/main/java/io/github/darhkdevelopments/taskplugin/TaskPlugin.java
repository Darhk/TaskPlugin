package io.github.darhkdevelopments.taskplugin;

import org.bukkit.plugin.java.JavaPlugin;


public class TaskPlugin extends JavaPlugin
{
	@Override
	public void onEnable()
	{
		getLogger().info("Task Plugin Has Been Enabled");
		this.getServer().getPluginManager().registerEvents(new TPListeners(), this);
		
	}
	
	@Override
	public void onDisable()
	{
		getLogger().info("Task Plugin Has Been Disabled");
	}
	
	
}	
