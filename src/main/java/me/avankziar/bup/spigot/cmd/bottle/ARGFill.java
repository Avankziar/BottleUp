package main.java.me.avankziar.bup.spigot.cmd.bottle;

import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import main.java.me.avankziar.bup.spigot.BottleUp;
import main.java.me.avankziar.bup.spigot.assistance.Experience;
import main.java.me.avankziar.bup.spigot.assistance.MatchApi;
import main.java.me.avankziar.bup.spigot.cmdtree.ArgumentConstructor;
import main.java.me.avankziar.bup.spigot.cmdtree.ArgumentModule;
import main.java.me.avankziar.bup.spigot.handler.ConfigHandler;
import main.java.me.avankziar.ifh.general.assistance.ChatApi;

public class ARGFill extends ArgumentModule
{
	private BottleUp plugin;
	
	public ARGFill(ArgumentConstructor argumentConstructor)
	{
		super(argumentConstructor);
		this.plugin = BottleUp.getPlugin();
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		Player player = (Player) sender;
		int bottleamount = 0;
		int level = 0;
		if(args.length == 1)
		{
			bottleamount = Integer.MAX_VALUE;
			doBottle(player, bottleamount);
			return;
		}
		String term = args[1];
		String[] s = args[1].split(":");
		if(s.length != 2 || new ConfigHandler().getBottleTerm().contains(s[1]))
		{
			if(MatchApi.isInteger(term))
			{
				bottleamount = Integer.parseInt(term);
			}
			if(bottleamount == 0)
			{
				bottleamount = Integer.MAX_VALUE;
			}
			doBottle(player, bottleamount);
		} else if(new ConfigHandler().getLevelTerm().contains(s[1]))
		{
			if(MatchApi.isInteger(s[1]))
			{
				level = Integer.parseInt(s[1]);
			}
			if(level < 0)
			{
				level = 0;
			}
			doLevel(player, level);
		}
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdBottle.WrongInput")
				.replace("%level%", String.join(", ", new ConfigHandler().getLevelTerm()))
				.replace("%bottle%", String.join(", ", new ConfigHandler().getBottleTerm()))
				));
	}
	
	private void doBottle(Player player, int bottleamount)
	{
		int texp = Experience.getExp(player);
		int expinb = (int) new ConfigHandler().getExpIntoBottle(player);
		if(bottleamount == 0)
		{
			bottleamount = texp/expinb;
		}
		if(texp < expinb*bottleamount)
		{
			bottleamount = texp/expinb;
		}
		int endexp = texp;
		int boam = bottleamount;
		int rexp = 0;
		int overglassbottle = 0;
		for(int i = 0; i < player.getInventory().getStorageContents().length; i++)
		{
			ItemStack is = player.getInventory().getStorageContents()[i];
			if(is == null)
			{
				continue;
			}
			if(is.getType() != Material.GLASS_BOTTLE)
			{
				continue;
			}
			if(is.hasItemMeta())
			{
				if(is.getItemMeta().hasDisplayName()
						|| is.getItemMeta().hasLore())
				{
					continue;
				}
			}
			int am = is.getAmount();
			boolean breaks = false;
			if(boam < am)
			{
				overglassbottle = am-boam;
				am = boam;
				breaks = true;
			} else if(boam == am)
			{
				am = boam;
				breaks = true;
			}
			rexp += am * expinb;
			ItemStack js = new ItemStack(Material.EXPERIENCE_BOTTLE, am);
			player.getInventory().setItem(i, js);
			boam = boam - am;
			endexp = endexp - am*expinb;
			if(breaks)
			{
				break;
			}
		}
		if(overglassbottle > 0)
		{
			HashMap<Integer, ItemStack> map = player.getInventory().addItem(new ItemStack(Material.GLASS_BOTTLE, overglassbottle));
			if(!map.isEmpty())
			{
				for(ItemStack is : map.values())
				{
					player.getWorld().dropItem(player.getLocation(), is);
				}
			}
		}
		Experience.changeExp(player, endexp, false);
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdBottle.Fill")
				.replace("%endexp%", String.valueOf(endexp))
				.replace("%removeexp%", String.valueOf(rexp))
				.replace("%bottleamount%", String.valueOf(boam))
				));
	}
	
	private void doLevel(Player player, int level)
	{
		int ptexp = Experience.getExp(player);
		int gtexp = Experience.getExp(level);
		double expinb = new ConfigHandler().getExpIntoBottle(player);
		if(gtexp > ptexp-expinb)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdBottle.CannotFillMoreBottleBecauseLevelIsAlreadyReached")));
			return;
		}
		double dif = ptexp-gtexp;
		double div = dif/expinb;
		int fillbottle = (int) Math.floor(div);		
		int boam = 0;
		int overglassbottle = 0;
		for(int i = 0; i < player.getInventory().getStorageContents().length; i++)
		{
			ItemStack is = player.getInventory().getStorageContents()[i];
			if(is == null)
			{
				continue;
			}
			if(is.getType() != Material.GLASS_BOTTLE)
			{
				continue;
			}
			if(is.hasItemMeta())
			{
				if(is.getItemMeta().hasDisplayName()
						|| is.getItemMeta().hasLore())
				{
					continue;
				}
			}
			int am = is.getAmount();
			boolean breaks = false;
			if(fillbottle > boam + am)
			{
				boam += is.getAmount();
				am = 0;
			} else if(fillbottle == boam + am)
			{
				boam += is.getAmount();
				am = 0;
				breaks = true;
			} else //fillbottle < boam + am
			{
				am = is.getAmount() - (fillbottle - boam);
				overglassbottle = is.getAmount() - am;
				boam += fillbottle - boam;
				breaks = true;
			}
			ItemStack js = new ItemStack(Material.EXPERIENCE_BOTTLE, am);
			player.getInventory().setItem(i, js);
			if(breaks)
			{
				break;
			}
		}
		if(overglassbottle > 0)
		{
			HashMap<Integer, ItemStack> map = player.getInventory().addItem(new ItemStack(Material.GLASS_BOTTLE, overglassbottle));
			if(!map.isEmpty())
			{
				for(ItemStack is : map.values())
				{
					player.getWorld().dropItem(player.getLocation(), is);
				}
			}
		}
		Experience.changeExp(player, (int)(ptexp-boam*expinb), false);
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdBottle.FillUntilLevel")
				.replace("%level%", player.getLevel()+"("+level+")")
				.replace("%removeexp%", String.valueOf(boam*expinb))
				.replace("%bottleamount%", String.valueOf(boam))
				));
	}
}