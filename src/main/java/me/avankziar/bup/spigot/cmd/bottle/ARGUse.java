package main.java.me.avankziar.bup.spigot.cmd.bottle;

import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import main.java.me.avankziar.bup.general.ChatApi;
import main.java.me.avankziar.bup.spigot.BottleUp;
import main.java.me.avankziar.bup.spigot.assistance.Experience;
import main.java.me.avankziar.bup.spigot.assistance.MatchApi;
import main.java.me.avankziar.bup.spigot.cmdtree.ArgumentConstructor;
import main.java.me.avankziar.bup.spigot.cmdtree.ArgumentModule;
import main.java.me.avankziar.bup.spigot.handler.ConfigHandler;

public class ARGUse extends ArgumentModule
{
	private BottleUp plugin;
	
	public ARGUse(ArgumentConstructor argumentConstructor)
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
			return;
		} else if(new ConfigHandler().getLevelTerm().contains(s[1]))
		{
			if(MatchApi.isInteger(s[0]))
			{
				level = Integer.parseInt(s[0]);
			}
			if(level < 0)
			{
				level = 0;
			}
			doLevel(player, level);
			return;
		}
		player.spigot().sendMessage(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("CmdBottle.WrongInput")
				.replace("%level%", String.join(", ", new ConfigHandler().getLevelTerm()))
				.replace("%bottle%", String.join(", ", new ConfigHandler().getBottleTerm()))
				));
	}
	
	private void doBottle(Player player, int bottleamount)
	{
		int ba = 0;
		int pexp = Experience.getExp(player);
		int texp = 0;
		double expfromb = new ConfigHandler().getExpFromBottle(player);
		for(int i = 0; i < player.getInventory().getStorageContents().length; i++)
		{
			ItemStack is = player.getInventory().getStorageContents()[i];
			if(is == null)
			{
				continue;
			}
			if(is.getType() != Material.EXPERIENCE_BOTTLE)
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
			int amII = am;
			boolean breaks = false;
			if(bottleamount < ba + am)
			{
				amII = bottleamount - ba;
				am = am - amII;
				breaks = true;
			} else if(bottleamount == ba + am)
			{
				am = 0;
				breaks = true;
			} else
			{
				am = 0;
			}
			int expis = amII * (int) expfromb;
			is.setAmount(am);
			texp += expis;
			ba += amII;
			if(breaks)
			{
				break;
			}
		}
		Experience.changeExp(player, pexp+texp, false);
		player.spigot().sendMessage(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("CmdBottle.Use")
				.replace("%endexp%", String.valueOf(pexp+texp))
				.replace("%addexp%", String.valueOf(texp))
				.replace("%bottleamount%", String.valueOf(ba))
				));
	}
	
	private void doLevel(Player player, int level)
	{
		int gtexp = Experience.getExp(level);
		int ptexp = Experience.getExp(player);
		double expfromb = new ConfigHandler().getExpFromBottle(player);
		double dif = gtexp-ptexp;
		if(dif < expfromb)
		{
			player.spigot().sendMessage(ChatApi.tctl(
					plugin.getYamlHandler().getLang().getString("CmdBottle.CannotUseMoreBottleBecauseLevelIsAlreadyReached")));
			return;
		}
		double div = dif/expfromb;
		int mustOpenbottle = (int) Math.ceil(div);		
		int haveOpenBottle = 0;
		for(int i = 0; i < player.getInventory().getStorageContents().length; i++)
		{
			ItemStack is = player.getInventory().getStorageContents()[i];
			if(is == null)
			{
				continue;
			}
			if(is.getType() != Material.EXPERIENCE_BOTTLE)
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
			int d = mustOpenbottle - haveOpenBottle;
			if(am > d)
			{
				haveOpenBottle += d;
				am = am - d;
				player.getInventory().setItem(i, new ItemStack(Material.EXPERIENCE_BOTTLE, am));
				break;
			} else if(am == d)
			{
				haveOpenBottle += is.getAmount();
				player.getInventory().setItem(i, null);
				break;
			} else if(am < d)
			{
				haveOpenBottle += is.getAmount();
				player.getInventory().setItem(i, null);
				continue;
			}
		}
		Experience.changeExp(player, (int)(ptexp+haveOpenBottle*expfromb), false);
		player.spigot().sendMessage(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("CmdBottle.UseUntilLevel")
				.replace("%level%", player.getLevel()+"("+level+")")
				.replace("%addexp%", String.valueOf(haveOpenBottle*expfromb))
				.replace("%bottleamount%", String.valueOf(haveOpenBottle))
				));
	}
}