package main.java.me.avankziar.bup.spigot.handler;

import org.bukkit.entity.Player;

import main.java.me.avankziar.bup.spigot.BottleUp;
import main.java.me.avankziar.bup.spigot.permission.BoniMali;

public class ConfigHandler
{
	private BottleUp plugin;
	
	public ConfigHandler()
	{
		plugin = BottleUp.getPlugin();
	}
	
	public boolean isMechanicBonusMalusEnabled()
	{
		return plugin.getYamlHandler().getConfig().getBoolean("EnableMechanic.BonusMalus", false);
	}
	
	public boolean isMechanicVanillaThrowExpBottle()
	{
		return plugin.getYamlHandler().getConfig().getBoolean("EnableMechanic.VanillaThrowExpBottle", false);
	}
	
	public int getExpIntoBottle(Player player)
	{
		Double exp = plugin.getYamlHandler().getConfig().getDouble("ExpBottle.ExpIntoBottle", 20);
		if(plugin.getBonusMalus() != null)
		{
			exp = plugin.getBonusMalus().getResult(player.getUniqueId(), exp, BoniMali.EXP_IN_BOTTLE.getBonusMalus());
		}
		return Integer.parseInt(String.valueOf(exp));
	}
	
	public int getExpFromBottle(Player player)
	{
		Double exp = plugin.getYamlHandler().getConfig().getDouble("ExpBottle.ExpFromBottle", 20);
		if(plugin.getBonusMalus() != null)
		{
			exp = plugin.getBonusMalus().getResult(player.getUniqueId(), exp, BoniMali.EXP_OUT_BOTTLE.getBonusMalus());
		}
		return Integer.parseInt(String.valueOf(exp));
	}
}