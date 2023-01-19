package main.java.me.avankziar.bup.spigot.permission;

import main.java.me.avankziar.bup.spigot.BottleUp;

public enum BoniMali
{
	EXP_IN_BOTTLE,
	EXP_OUT_BOTTLE;
	
	public String getBonusMalus()
	{
		return BottleUp.getPlugin().pluginName.toLowerCase()+":"+this.toString().toLowerCase();
	}
}
