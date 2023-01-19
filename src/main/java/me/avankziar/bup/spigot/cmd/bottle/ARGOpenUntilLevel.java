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

public class ARGOpenUntilLevel extends ArgumentModule
{
	private BottleUp plugin;
	
	public ARGOpenUntilLevel(ArgumentConstructor argumentConstructor)
	{
		super(argumentConstructor);
		this.plugin = BottleUp.getPlugin();
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		Player player = (Player) sender;
		int level = 0;
		if(args.length >= 2 && MatchApi.isInteger(args[1]))
		{
			level = Integer.parseInt(args[1]);
		}
		int ptexp = Utility.getTotalExperience(player);
		int pexp = ptexp;
		int gtexp = Utility.getTotalExperience(level);
		int gexp = 0;
		int expfromb = new ConfigHandler().getExpFromBottle(player);
		if(gtexp < pexp+expfromb)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdBottle.CannotOpenMoreBottleBecauseLevelIsAlreadyReached")));
			return;
		}
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
			int bexp = am * expfromb;
			boolean breaks = false;
			if(gexp < pexp+bexp)
			{
				int missingexp = gexp - pexp;
				int rbottle = (missingexp / expfromb)+1;
				gexp += rbottle * expfromb;
				am = am - rbottle;
				breaks = true;
			} else if(gexp == pexp-bexp)
			{
				breaks = true;
				
				am = 0;
			} else
			{
				am = 0;
			}
			ItemStack js = new ItemStack(Material.EXPERIENCE_BOTTLE, am);
			player.getInventory().setItem(i, js);
			gexp += bexp;
			pexp -= bexp;
			if(breaks)
			{
				break;
			}
		}
		Utility.setTotalExperience(player, gexp);
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdBottle.OpenUntilLevel")
				.replace("%level%", String.valueOf(level))
				.replace("%addexp%", String.valueOf(gexp))
				.replace("%bottleamount%", String.valueOf((ptexp-gtexp)/expfromb))
				));
	}
}