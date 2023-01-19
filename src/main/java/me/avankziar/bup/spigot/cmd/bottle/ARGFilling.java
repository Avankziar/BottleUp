package main.java.me.avankziar.bup.spigot.cmd.bottle;

import java.io.IOException;
import java.util.HashMap;

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

public class ARGFilling extends ArgumentModule
{
	private BottleUp plugin;
	
	public ARGFilling(ArgumentConstructor argumentConstructor)
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
		int texp = Utility.getTotalExperience(player);
		int expinb = new ConfigHandler().getExpIntoBottle(player);
		if(bottleamount == 0)
		{
			bottleamount = texp/expinb;
		}
		if(texp < expinb*bottleamount)
		{
			bottleamount = texp/expinb;
		}
		int rexp = expinb*bottleamount;
		int endtexp = texp-rexp;
		int ba = bottleamount;
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
			if(ba < am)
			{
				overglassbottle = am-ba;
				am = ba;
				
			}
			ItemStack js = new ItemStack(Material.EXPERIENCE_BOTTLE, am);
			player.getInventory().setItem(i, js);
			ba = ba - am;
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
		Utility.setTotalExperience(player, endtexp);
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdBottle.Filling")
				.replace("%endexp%", String.valueOf(endtexp))
				.replace("%removeexp%", String.valueOf(rexp))
				.replace("%bottleamount%", String.valueOf(bottleamount))
				));
	}
}