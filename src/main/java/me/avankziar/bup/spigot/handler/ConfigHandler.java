package main.java.me.avankziar.bup.spigot.handler;

import java.util.List;

import org.bukkit.entity.Player;

import main.java.me.avankziar.bup.spigot.cmdtree.BaseConstructor;
import main.java.me.avankziar.bup.spigot.conditionbonusmalus.Bypass;
import main.java.me.avankziar.bup.spigot.conditionbonusmalus.ConditionBonusMalus;

public class ConfigHandler
{	
	public ConfigHandler(){}
	
	public enum CountType
	{
		HIGHEST, ADDUP;
	}
	
	public CountType getCountPermType()
	{
		String s = BaseConstructor.getPlugin().getYamlHandler().getConfig().getString("Mechanic.CountPerm", "HIGHEST");
		CountType ct;
		try
		{
			ct = CountType.valueOf(s);
		} catch (Exception e)
		{
			ct = CountType.HIGHEST;
		}
		return ct;
	}
	
	public boolean isMechanicBonusMalusEnabled()
	{
		return BaseConstructor.getPlugin().getYamlHandler().getConfig().getBoolean("EnableMechanic.BonusMalus", false);
	}
	
	public boolean isMechanicConditionEnabled()
	{
		return BaseConstructor.getPlugin().getYamlHandler().getConfig().getBoolean("EnableMechanic.Condition", false);
	}
	
	public boolean isMechanicVanillaThrowExpBottle()
	{
		return BaseConstructor.getPlugin().getYamlHandler().getConfig().getBoolean("EnableMechanic.VanillaThrowExpBottle", false);
	}
	
	public double getExpIntoBottle(Player player)
	{
		double exp = ConditionBonusMalus.getResult(player.getUniqueId(),
				BaseConstructor.getPlugin().getYamlHandler().getConfig().getDouble("ExpBottle.ExpIntoBottle", 20),
				Bypass.Counter.EXP_IN_BOTTLE);
		return exp;
	}
	
	public double getExpFromBottle(Player player)
	{
		double exp = ConditionBonusMalus.getResult(player.getUniqueId(),
				BaseConstructor.getPlugin().getYamlHandler().getConfig().getDouble("ExpBottle.ExpFromBottle", 20),
				Bypass.Counter.EXP_OUT_BOTTLE);
		return exp;
	}
	
	public List<String> getBottleTerm()
	{
		return BaseConstructor.getPlugin().getYamlHandler().getConfig().getStringList("ExpBottle.BottleTerm");
	}
	
	public List<String> getLevelTerm()
	{
		return BaseConstructor.getPlugin().getYamlHandler().getConfig().getStringList("ExpBottle.LevelTerm");
	}
}