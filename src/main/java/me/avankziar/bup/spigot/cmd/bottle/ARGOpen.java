package main.java.me.avankziar.bup.spigot.cmd.bottle;

import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import main.java.me.avankziar.bup.spigot.BottleUp;
import main.java.me.avankziar.bup.spigot.assistance.MatchApi;
import main.java.me.avankziar.bup.spigot.assistance.Utility;
import main.java.me.avankziar.bup.spigot.cmdtree.ArgumentConstructor;
import main.java.me.avankziar.bup.spigot.cmdtree.ArgumentModule;
import main.java.me.avankziar.bup.spigot.handler.ConfigHandler;
import main.java.me.avankziar.ifh.general.assistance.ChatApi;

public class ARGOpen extends ArgumentModule
{
	private BottleUp plugin;
	
	public ARGOpen(ArgumentConstructor argumentConstructor)
	{
		super(argumentConstructor);
		this.plugin = BottleUp.getPlugin();
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		Player player = (Player) sender;
		int bottleamount = 0;
		if(args.length >= 2 && MatchApi.isInteger(args[1]))
		{
			bottleamount = Integer.parseInt(args[1]);
		}
		if(bottleamount == 0)
		{
			bottleamount = Integer.MAX_VALUE;
		}
		int ba = 0;
		int pexp = Utility.getTotalExperience(player);
		int texp = 0;
		int expfromb = new ConfigHandler().getExpFromBottle(player);
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
			int expis = amII * expfromb;
			is.setAmount(am);
			texp += expis;
			ba += amII;
			if(breaks)
			{
				break;
			}
		}
		Utility.setTotalExperience(player, pexp+texp);
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdBottle.Filling")
				.replace("%endexp%", String.valueOf(pexp+texp))
				.replace("%addexp%", String.valueOf(texp))
				.replace("%bottleamount%", String.valueOf(ba))
				));
	}
}