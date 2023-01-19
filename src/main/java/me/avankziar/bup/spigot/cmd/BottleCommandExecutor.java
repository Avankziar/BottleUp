package main.java.me.avankziar.bup.spigot.cmd;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.bup.general.ChatApi;
import main.java.me.avankziar.bup.spigot.BottleUp;
import main.java.me.avankziar.bup.spigot.assistance.Utility;
import main.java.me.avankziar.bup.spigot.cmdtree.ArgumentConstructor;
import main.java.me.avankziar.bup.spigot.cmdtree.ArgumentModule;
import main.java.me.avankziar.bup.spigot.cmdtree.CommandConstructor;
import main.java.me.avankziar.bup.spigot.handler.ConfigHandler;
import net.md_5.bungee.api.chat.ClickEvent;

public class BottleCommandExecutor implements CommandExecutor
{
	private BottleUp plugin;
	private static CommandConstructor cc;
	
	public BottleCommandExecutor(BottleUp plugin, CommandConstructor cc)
	{
		this.plugin = plugin;
		BottleCommandExecutor.cc = cc;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) 
	{
		if(cc == null)
		{
			return false;
		}
		if(args.length == 0)
		{
			if (!(sender instanceof Player)) 
			{
				plugin.getLogger().info("Cmd is only for Player!");
				return false;
			}
			Player player = (Player) sender;
			if(!player.hasPermission(cc.getPermission()))
			{
				///Du hast dafür keine Rechte!
				player.spigot().sendMessage(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("NoPermission")));
				return false;
			}
			baseCommands(player); //Base and Info Command
			return true;
		}
		int length = args.length-1;
		ArrayList<ArgumentConstructor> aclist = cc.subcommands;
		for(int i = 0; i <= length; i++)
		{
			for(ArgumentConstructor ac : aclist)
			{
				if(args[i].equalsIgnoreCase(ac.getName()))
				{
					if(length >= ac.minArgsConstructor && length <= ac.maxArgsConstructor)
					{
						if (sender instanceof Player)
						{
							Player player = (Player) sender;
							if(player.hasPermission(ac.getPermission()))
							{
								ArgumentModule am = plugin.getArgumentMap().get(ac.getPath());
								if(am != null)
								{
									try
									{
										am.run(sender, args);
									} catch (IOException e)
									{
										e.printStackTrace();
									}
								} else
								{
									plugin.getLogger().info("ArgumentModule from ArgumentConstructor %ac% not found! ERROR!"
											.replace("%ac%", ac.getName()));
									player.spigot().sendMessage(ChatApi.tctl(
											"ArgumentModule from ArgumentConstructor %ac% not found! ERROR!"
											.replace("%ac%", ac.getName())));
									return false;
								}
								return false;
							} else
							{
								player.spigot().sendMessage(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("NoPermission")));
								return false;
							}
						} else
						{
							ArgumentModule am = plugin.getArgumentMap().get(ac.getPath());
							if(am != null)
							{
								try
								{
									am.run(sender, args);
								} catch (IOException e)
								{
									e.printStackTrace();
								}
							} else
							{
								plugin.getLogger().info("ArgumentModule from ArgumentConstructor %ac% not found! ERROR!"
										.replace("%ac%", ac.getName()));
								sender.spigot().sendMessage(ChatApi.tctl(
										"ArgumentModule from ArgumentConstructor %ac% not found! ERROR!"
										.replace("%ac%", ac.getName())));
								return false;
							}
							return false;
						}
					} else
					{
						aclist = ac.subargument;
						break;
					}
				}
			}
		}
		sender.spigot().sendMessage(ChatApi.clickEvent(plugin.getYamlHandler().getLang().getString("InputIsWrong"),
				ClickEvent.Action.RUN_COMMAND, BottleUp.infoCommand));
		return false;
	}
	
	public void baseCommands(final Player player)
	{
		int texp = Utility.getTotalExperience(player);
		int expinb = new ConfigHandler().getExpIntoBottle(player);
		int expfromb = new ConfigHandler().getExpFromBottle(player);
		double bottle = texp/expinb;
		for(String s : plugin.getYamlHandler().getLang().getStringList("CmdBottle.BaseInfo"))
		{
			player.sendMessage(ChatApi.tl(s
					.replace("%totalexp%", String.valueOf(texp))
					.replace("%expinbottle%", String.valueOf(expinb))
					.replace("%bottle%", String.valueOf(bottle))
					.replace("%expfrombottle%", String.valueOf(expfromb))
					));
		}
	}
}