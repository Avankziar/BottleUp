package main.java.me.avankziar.bup.spigot.handler;

import java.util.List;

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
	
	public double getExpIntoBottle(Player player)
	{
		double exp = plugin.getYamlHandler().getConfig().getDouble("ExpBottle.ExpIntoBottle", 20);
		if(plugin.getBonusMalus() != null)
		{
			exp = plugin.getBonusMalus().getResult(player.getUniqueId(), exp, BoniMali.EXP_IN_BOTTLE.getBonusMalus());
		}
		return exp;
	}
	
	public double getExpFromBottle(Player player)
	{
		double exp = plugin.getYamlHandler().getConfig().getDouble("ExpBottle.ExpFromBottle", 20);
		if(plugin.getBonusMalus() != null)
		{
			exp = plugin.getBonusMalus().getResult(player.getUniqueId(), exp, BoniMali.EXP_OUT_BOTTLE.getBonusMalus());
		}
		return exp;
	}
	
	public List<String> getBottleTerm()
	{
		return plugin.getYamlHandler().getConfig().getStringList("ExpBottle.BottleTerm");
	}
	
	public List<String> getLevelTerm()
	{
		return plugin.getYamlHandler().getConfig().getStringList("ExpBottle.LevelTerm");
	}
}