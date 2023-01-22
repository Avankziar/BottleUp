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
import main.java.me.avankziar.bup.spigot.permission.BoniMali;
import main.java.me.avankziar.ifh.general.bonusmalus.BonusMalus;
import main.java.me.avankziar.ifh.general.bonusmalus.BonusMalusType;
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
	
	public static String infoCommandPath = "CmdBase";
	public static String infoCommand = "/";
	
	public Administration administrationConsumer;
	private BonusMalus bonusMalusConsumer;
	
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
		setupBonusMalus();
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
		infoCommand += plugin.getYamlHandler().getCommands().getString("bottleup.Name");
		
		TabCompletion tab = new TabCompletion(plugin);
		
		CommandConstructor bottleup = new CommandConstructor(CommandExecuteType.BOTTLEUP, "bottleup", false);
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
	
	private void setupBonusMalus() 
	{
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
				    RegisteredServiceProvider<main.java.me.avankziar.ifh.general.bonusmalus.BonusMalus> rsp = 
		                             getServer().getServicesManager().getRegistration(
		                            		 main.java.me.avankziar.ifh.general.bonusmalus.BonusMalus.class);
				    if(rsp == null) 
				    {
				    	//Check up to 20 seconds after the start, to connect with the provider
				    	i++;
				        return;
				    }
				    bonusMalusConsumer = rsp.getProvider();
				    log.info(pluginName + " detected InterfaceHub >>> BonusMalus.class is consumed!");
				    cancel();
				} catch(NoClassDefFoundError e)
				{
					cancel();
				}
				if(getBonusMalus() != null)
				{
					if(!new ConfigHandler().isMechanicBonusMalusEnabled())
					{
						return;
					}
					int cmd = 0;
					int bperm = 0;
					int cperm = 0;
					int bm = 0;
					for(BaseConstructor bc : getCommandHelpList())
					{
						if(!bc.isPutUpCmdPermToBonusMalusSystem())
						{
							continue;
						}
						if(getBonusMalus().isRegistered(pluginName.toLowerCase()+":"+bc.getPath()))
						{
							cmd++;
							continue;
						}
						String[] ex = {plugin.getYamlHandler().getCommands().getString(bc.getPath()+".Explanation")};
						getBonusMalus().register(
								pluginName.toLowerCase()+":"+bc.getPath(),
								plugin.getYamlHandler().getCommands().getString(bc.getPath()+".Displayname", "Command "+bc.getName()),
								true,
								BonusMalusType.UP,
								ex);
						cmd++;
					}
					List<BoniMali> list3 = new ArrayList<BoniMali>(EnumSet.allOf(BoniMali.class));
					for(BoniMali ept : list3)
					{
						if(!getBonusMalus().isRegistered(pluginName.toLowerCase()+":"+ept.toString().toLowerCase()))
						{
							BonusMalusType bmt = null;
							switch(ept)
							{
							case EXP_OUT_BOTTLE:
								bmt = BonusMalusType.UP;
								break;
							case EXP_IN_BOTTLE:
								bmt = BonusMalusType.DOWN;
								break;
							}
							List<String> lar = plugin.getYamlHandler().getBMLang().getStringList(ept.toString()+".Explanation");
							getBonusMalus().register(
									ept.getBonusMalus(),
									plugin.getYamlHandler().getBMLang().getString(ept.toString()+".Displayname", ept.toString()),
									false,
									bmt,
									lar.toArray(new String[lar.size()]));
						}
						bm++;
					}
					log.info("===Registered BonusMalus===");
					log.info(">> Commands: "+cmd);
					log.info(">> BypassPerm: "+bperm);
					log.info(">> CountPerm: "+cperm);
					log.info(">> Other BoniMali: "+bm);
				}
			}
        }.runTaskTimer(plugin, 20L, 20*2);
	}
	
	public BonusMalus getBonusMalus()
	{
		return bonusMalusConsumer;
	}
}