package main.java.me.avankziar.bup.spigot;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.bup.spigot.assistance.Utility;
import main.java.me.avankziar.bup.spigot.cmd.BottleCommandExecutor;
import main.java.me.avankziar.bup.spigot.cmd.BottleUpCommandExecutor;
import main.java.me.avankziar.bup.spigot.cmd.TabCompletion;
import main.java.me.avankziar.bup.spigot.cmd.bottle.ARGCalculate;
import main.java.me.avankziar.bup.spigot.cmd.bottle.ARGFill;
import main.java.me.avankziar.bup.spigot.cmd.bottle.ARGUse;
import main.java.me.avankziar.bup.spigot.cmdtree.ArgumentConstructor;
import main.java.me.avankziar.bup.spigot.cmdtree.ArgumentModule;
import main.java.me.avankziar.bup.spigot.cmdtree.BaseConstructor;
import main.java.me.avankziar.bup.spigot.cmdtree.CommandConstructor;
import main.java.me.avankziar.bup.spigot.cmdtree.CommandExecuteType;
import main.java.me.avankziar.bup.spigot.database.YamlHandler;
import main.java.me.avankziar.bup.spigot.database.YamlManager;
import main.java.me.avankziar.bup.spigot.handler.ConfigHandler;
import main.java.me.avankziar.bup.spigot.listener.PlayerExpBottleListener;
import main.java.me.avankziar.bup.spigot.metrics.Metrics;
import main.java.me.avankziar.bup.spigot.modifiervalueentry.Bypass;
import main.java.me.avankziar.ifh.general.modifier.ModificationType;
import main.java.me.avankziar.ifh.general.modifier.Modifier;
import main.java.me.avankziar.ifh.general.valueentry.ValueEntry;
import main.java.me.avankziar.ifh.spigot.administration.Administration;

public class BottleUp extends JavaPlugin
{
	public static Logger log;
	private static BottleUp plugin;
	public String pluginName = "BottleUp";
	private YamlHandler yamlHandler;
	private YamlManager yamlManager;
	private Utility utility;
	
	private ArrayList<BaseConstructor> helpList = new ArrayList<>();
	private ArrayList<CommandConstructor> commandTree = new ArrayList<>();
	private LinkedHashMap<String, ArgumentModule> argumentMap = new LinkedHashMap<>();
	private ArrayList<String> players = new ArrayList<>();
	
	public static String infoCommandPath = "CmdBUP";
	public static String infoCommand = "/";
	
	private Administration administrationConsumer;
	private ValueEntry valueEntryConsumer;
	private Modifier modifierConsumer;
	
	public void onEnable()
	{
		plugin = this;
		log = getLogger();
		
		//https://patorjk.com/software/taag/#p=display&f=ANSI%20Shadow&t=BUP
		log.info(" ██████╗ ██╗   ██╗██████╗  | API-Version: "+plugin.getDescription().getAPIVersion());
		log.info(" ██╔══██╗██║   ██║██╔══██╗ | Author: "+plugin.getDescription().getAuthors().toString());
		log.info(" ██████╔╝██║   ██║██████╔╝ | Plugin Website: "+plugin.getDescription().getWebsite());
		log.info(" ██╔══██╗██║   ██║██╔═══╝  | Depend Plugins: "+plugin.getDescription().getDepend().toString());
		log.info(" ██████╔╝╚██████╔╝██║      | SoftDepend Plugins: "+plugin.getDescription().getSoftDepend().toString());
		log.info(" ╚═════╝  ╚═════╝ ╚═╝      | LoadBefore: "+plugin.getDescription().getLoadBefore().toString());
		
		setupIFHAdministration();
		
		yamlHandler = new YamlHandler(this);
		
		utility = new Utility(plugin);
		
		setupCommandTree();
		setupListeners();
		setupIFHConsumer();
		setupBstats();
	}
	
	public void onDisable()
	{
		Bukkit.getScheduler().cancelTasks(this);
		HandlerList.unregisterAll(this);
		log.info(pluginName + " is disabled!");
	}

	public static BottleUp getPlugin()
	{
		return plugin;
	}
	
	public YamlHandler getYamlHandler() 
	{
		return yamlHandler;
	}
	
	public YamlManager getYamlManager()
	{
		return yamlManager;
	}

	public void setYamlManager(YamlManager yamlManager)
	{
		this.yamlManager = yamlManager;
	}
	
	public Utility getUtility()
	{
		return utility;
	}
	
	public String getServername()
	{
		return getPlugin().getAdministration() != null ? getPlugin().getAdministration().getSpigotServerName() 
				: getPlugin().getYamlHandler().getConfig().getString("ServerName");
	}
	
	private void setupCommandTree()
	{		
		infoCommand += plugin.getYamlHandler().getCommands().getString("bup.Name");
		
		TabCompletion tab = new TabCompletion(plugin);
		
		CommandConstructor bottleup = new CommandConstructor(CommandExecuteType.BOTTLEUP, "bup", false);
		registerCommand(bottleup.getPath(), bottleup.getName());
		getCommand(bottleup.getName()).setExecutor(new BottleUpCommandExecutor(plugin, bottleup));
		getCommand(bottleup.getName()).setTabCompleter(tab);
		
		ArgumentConstructor cal = new ArgumentConstructor(CommandExecuteType.BOTTLE_USE, "bottle_calculate", 0, 1, 1, false, null);
		new ARGCalculate(cal);
		ArgumentConstructor fill = new ArgumentConstructor(CommandExecuteType.BOTTLE_FIll, "bottle_fill", 0, 0, 1, false, null);
		new ARGFill(fill);
		ArgumentConstructor use = new ArgumentConstructor(CommandExecuteType.BOTTLE_USE, "bottle_use", 0, 0, 1, false, null);
		new ARGUse(use);
		
		CommandConstructor bottle = new CommandConstructor(CommandExecuteType.BOTTLE, "bottle", false,
				cal, fill, use);
		registerCommand(bottle.getPath(), bottle.getName());
		getCommand(bottle.getName()).setExecutor(new BottleCommandExecutor(plugin, bottle));
		getCommand(bottle.getName()).setTabCompleter(tab);
	}
	
	public ArrayList<BaseConstructor> getCommandHelpList()
	{
		return helpList;
	}
	
	public void addingCommandHelps(BaseConstructor... objects)
	{
		for(BaseConstructor bc : objects)
		{
			helpList.add(bc);
		}
	}
	
	public ArrayList<CommandConstructor> getCommandTree()
	{
		return commandTree;
	}
	
	public CommandConstructor getCommandFromPath(String commandpath)
	{
		CommandConstructor cc = null;
		for(CommandConstructor coco : getCommandTree())
		{
			if(coco.getPath().equalsIgnoreCase(commandpath))
			{
				cc = coco;
				break;
			}
		}
		return cc;
	}
	
	public CommandConstructor getCommandFromCommandString(String command)
	{
		CommandConstructor cc = null;
		for(CommandConstructor coco : getCommandTree())
		{
			if(coco.getName().equalsIgnoreCase(command))
			{
				cc = coco;
				break;
			}
		}
		return cc;
	}
	
	public void registerCommand(String... aliases) 
	{
		PluginCommand command = getCommand(aliases[0], plugin);
	 
		command.setAliases(Arrays.asList(aliases));
		getCommandMap().register(plugin.getDescription().getName(), command);
	}
	 
	private static PluginCommand getCommand(String name, BottleUp plugin) 
	{
		PluginCommand command = null;
	 
		try 
		{
			Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
			c.setAccessible(true);
	 
			command = c.newInstance(name, plugin);
		} catch (SecurityException e) 
		{
			e.printStackTrace();
		} catch (IllegalArgumentException e) 
		{
			e.printStackTrace();
		} catch (IllegalAccessException e) 
		{
			e.printStackTrace();
		} catch (InstantiationException e) 
		{
			e.printStackTrace();
		} catch (InvocationTargetException e) 
		
		{
			e.printStackTrace();
		} catch (NoSuchMethodException e) 
		{
			e.printStackTrace();
		}
	 
		return command;
	}
	 
	private static CommandMap getCommandMap() 
	{
		CommandMap commandMap = null;
	 
		try {
			if (Bukkit.getPluginManager() instanceof SimplePluginManager) 
			{
				Field f = SimplePluginManager.class.getDeclaredField("commandMap");
				f.setAccessible(true);
	 
				commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
			}
		} catch (NoSuchFieldException e) 
		{
			e.printStackTrace();
		} catch (SecurityException e) 
		{
			e.printStackTrace();
		} catch (IllegalArgumentException e) 
		{
			e.printStackTrace();
		} catch (IllegalAccessException e) 
		{
			e.printStackTrace();
		}
	 
		return commandMap;
	}
	
	public LinkedHashMap<String, ArgumentModule> getArgumentMap()
	{
		return argumentMap;
	}
	
	public ArrayList<String> getMysqlPlayers()
	{
		return players;
	}

	public void setMysqlPlayers(ArrayList<String> players)
	{
		this.players = players;
	}
	
	public void setupListeners()
	{
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerExpBottleListener(), plugin);
	}
	
	public boolean existHook(String externPluginName)
	{
		if(plugin.getServer().getPluginManager().getPlugin(externPluginName) == null)
		{
			return false;
		}
		log.info(pluginName+" hook with "+externPluginName);
		return true;
	}
	
	private void setupIFHAdministration()
	{ 
		if(!plugin.getServer().getPluginManager().isPluginEnabled("InterfaceHub")) 
	    {
	    	return;
	    }
		RegisteredServiceProvider<main.java.me.avankziar.ifh.spigot.administration.Administration> rsp = 
                getServer().getServicesManager().getRegistration(Administration.class);
		if (rsp == null) 
		{
		   return;
		}
		administrationConsumer = rsp.getProvider();
		log.info(pluginName + " detected InterfaceHub >>> Administration.class is consumed!");
	}
	
	public Administration getAdministration()
	{
		return administrationConsumer;
	}
	
	public void setupIFHConsumer()
	{
		setupIFHCondition();
		setupIFHBonusMalus();
	}
	
	public void setupIFHCondition()
	{
		if(!new ConfigHandler().isMechanicValueEntryEnabled())
		{
			return;
		}
		if(!plugin.getServer().getPluginManager().isPluginEnabled("InterfaceHub")) 
	    {
	    	return;
	    }
        new BukkitRunnable()
        {
        	int i = 0;
			@Override
			public void run()
			{
				try
				{
					if(i == 20)
				    {
						cancel();
				    	return;
				    }
				    RegisteredServiceProvider<main.java.me.avankziar.ifh.general.valueentry.ValueEntry> rsp = 
		                             getServer().getServicesManager().getRegistration(
		                            		 main.java.me.avankziar.ifh.general.valueentry.ValueEntry.class);
				    if(rsp == null) 
				    {
				    	i++;
				        return;
				    }
				    valueEntryConsumer = rsp.getProvider();
				    log.info(pluginName + " detected InterfaceHub >>> ValueEntry.class is consumed!");
				    cancel();
				} catch(NoClassDefFoundError e)
				{
					cancel();
				}
				if(getValueEntry() != null)
				{
					for(BaseConstructor bc : getCommandHelpList())
					{
						if(!bc.isPutUpCmdPermToConditionSystem())
						{
							continue;
						}
						if(getValueEntry().isRegistered(bc.getConditionPath()))
						{
							continue;
						}
						String[] ex = {plugin.getYamlHandler().getCommands().getString(bc.getPath()+".Explanation")};
						getValueEntry().register(
								bc.getConditionPath(),
								plugin.getYamlHandler().getCommands().getString(bc.getPath()+".Displayname", "Command "+bc.getName()),
								ex);
					}
				}
			}
        }.runTaskTimer(plugin, 0L, 20*2);
	}
	
	public ValueEntry getValueEntry()
	{
		return valueEntryConsumer;
	}
	
	private void setupIFHBonusMalus() 
	{
		if(!new ConfigHandler().isMechanicModifierEnabled())
		{
			return;
		}
        if(Bukkit.getPluginManager().getPlugin("InterfaceHub") == null) 
        {
            return;
        }
        new BukkitRunnable()
        {
        	int i = 0;
			@Override
			public void run()
			{
				try
				{
					if(i == 20)
				    {
						cancel();
						return;
				    }
				    RegisteredServiceProvider<main.java.me.avankziar.ifh.general.modifier.Modifier> rsp = 
		                             getServer().getServicesManager().getRegistration(
		                            		 main.java.me.avankziar.ifh.general.modifier.Modifier.class);
				    if(rsp == null) 
				    {
				    	//Check up to 20 seconds after the start, to connect with the provider
				    	i++;
				        return;
				    }
				    modifierConsumer = rsp.getProvider();
				    log.info(pluginName + " detected InterfaceHub >>> Modifier.class is consumed!");
				    cancel();
				} catch(NoClassDefFoundError e)
				{
					cancel();
				}
				if(getModifier() != null)
				{				
					List<Bypass.Counter> list = new ArrayList<Bypass.Counter>(EnumSet.allOf(Bypass.Counter.class));
					for(Bypass.Counter ept : list)
					{
						if(!getModifier().isRegistered(ept.getModification()))
						{
							ModificationType modt = null;
							switch(ept)
							{
							case EXP_OUT_BOTTLE:
								modt = ModificationType.UP;
								break;
							case EXP_IN_BOTTLE:
								modt = ModificationType.DOWN;
								break;
							}
							List<String> lar = plugin.getYamlHandler().getMVELang().getStringList(ept.toString()+".Explanation");
							getModifier().register(
									ept.getModification(),
									plugin.getYamlHandler().getMVELang().getString(ept.toString()+".Displayname", ept.toString()),
									modt,
									lar.toArray(new String[lar.size()]));
						}
					}
				}
			}
        }.runTaskTimer(plugin, 20L, 20*2);
	}
	
	public Modifier getModifier()
	{
		return modifierConsumer;
	}
	
	public void setupBstats()
	{
		int pluginId = 17589;
        new Metrics(this, pluginId);
	}
}