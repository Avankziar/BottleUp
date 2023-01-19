package main.java.me.avankziar.bup.spigot.assistance;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import org.bukkit.entity.Player;

import main.java.me.avankziar.bup.spigot.BottleUp;
import main.java.me.avankziar.ifh.spigot.position.ServerLocation;

public class Utility
{	
	public Utility(BottleUp plugin)
	{}
	
	public static double getNumberFormat(double d)//FIN
	{
		BigDecimal bd = new BigDecimal(d).setScale(1, RoundingMode.HALF_UP);
		double newd = bd.doubleValue();
		return newd;
	}
	
	public static double getNumberFormat(double d, int scale)
	{
		BigDecimal bd = new BigDecimal(d).setScale(scale, RoundingMode.HALF_UP);
		double newd = bd.doubleValue();
		return newd;
	}
	
	/*public static String convertUUIDToName(String uuid)
	{
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.PLAYERDATA, "player_uuid = ?", uuid))
		{
			return ((PlayerData) plugin.getMysqlHandler().getData(MysqlHandler.Type.PLAYERDATA, "player_uuid = ?", uuid)).getName();
		}
		return null;
	}
	
	public static UUID convertNameToUUID(String playername)
	{
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.PLAYERDATA, "`player_name` = ?", playername))
		{
			return ((PlayerData) plugin.getMysqlHandler().getData(MysqlHandler.Type.PLAYERDATA, "`player_name` = ?", playername)).getUUID();
		}
		return null;
	}*/
	
	public static String serialised(LocalDateTime dt)
	{
		String MM = "";
		int month = 0;
		if(dt.getMonthValue()<10)
		{
			MM+=month;
		}
		MM += dt.getMonthValue();
		String dd = "";
		int day = 0;
		if(dt.getDayOfMonth()<10)
		{
			dd+=day;
		}
		dd +=dt.getDayOfMonth();
		String hh = "";
		int hour = 0;
		if(dt.getHour()<10)
		{
			hh+=hour;
		}
		hh += dt.getHour();
		String mm = "";
		int min = 0;
		if(dt.getMinute()<10)
		{
			mm+=min;
		}
		mm += dt.getMinute();
		return dd+"."+MM+"."+dt.getYear()+" "+hh+":"+mm;
	}
	
	public static double round(double value, int places) 
	{
	    if (places < 0) throw new IllegalArgumentException();
	    try
	    {
	    	BigDecimal bd = BigDecimal.valueOf(value);
		    bd = bd.setScale(places, RoundingMode.HALF_UP);
		    return bd.doubleValue();
	    } catch (NumberFormatException e)
	    {
	    	return 0;
	    }
	}
	
	public static ServerLocation getLocation(String s)
	{
		String[] split = s.split(";");
		ServerLocation sl = new ServerLocation(split[0], split[1],
				Double.parseDouble(split[2]),
				Double.parseDouble(split[3]),
				Double.parseDouble(split[4]),
				Float.parseFloat(split[5]), 
				Float.parseFloat(split[6]));
		return sl;
	}
	
	public static int getTotalExperience(Player player) 
	{
	    int experience = 0;
	    int level = player.getLevel();
	    if(level >= 0 && level <= 15) {
	        experience = (int) Math.ceil(Math.pow(level, 2) + (6 * level));
	        int requiredExperience = 2 * level + 7;
	        double currentExp = Double.parseDouble(Float.toString(player.getExp()));
	        experience += Math.ceil(currentExp * requiredExperience);
	        return experience;
	    } else if(level > 15 && level <= 30) {
	        experience = (int) Math.ceil((2.5 * Math.pow(level, 2) - (40.5 * level) + 360));
	        int requiredExperience = 5 * level - 38;
	        double currentExp = Double.parseDouble(Float.toString(player.getExp()));
	        experience += Math.ceil(currentExp * requiredExperience);
	        return experience;
	    } else {
	        experience = (int) Math.ceil(((4.5 * Math.pow(level, 2) - (162.5 * level) + 2220)));
	        int requiredExperience = 9 * level - 158;
	        double currentExp = Double.parseDouble(Float.toString(player.getExp()));
	        experience += Math.ceil(currentExp * requiredExperience);
	        return experience;
	    }
	}
	
	public static int getTotalExperience(int level) 
	{
	    int experience = 0;
	    if(level >= 0 && level <= 15) 
	    {
	        experience = (int) Math.ceil(Math.pow(level, 2) + (6 * level));
	        int requiredExperience = 2 * level + 7;
	        double currentExp = Double.parseDouble(Float.toString(0F));
	        experience += Math.ceil(currentExp * requiredExperience);
	    } else if(level > 15 && level <= 30) 
	    {
	        experience = (int) Math.ceil((2.5 * Math.pow(level, 2) - (40.5 * level) + 360));
	        int requiredExperience = 5 * level - 38;
	        double currentExp = Double.parseDouble(Float.toString(0F));
	        experience += Math.ceil(currentExp * requiredExperience);
	    } else 
	    {
	        experience = (int) Math.ceil(((4.5 * Math.pow(level, 2) - (162.5 * level) + 2220)));
	        int requiredExperience = 9 * level - 158;
	        double currentExp = Double.parseDouble(Float.toString(0F));
	        experience += Math.ceil(currentExp * requiredExperience);
	    }
	    return experience;
	}

	public static void setTotalExperience(Player player, int xp) 
	{
	    //Levels 0 through 15
	    if(xp >= 0 && xp < 351) {
	        //Calculate Everything
	        int a = 1; int b = 6; int c = -xp;
	        int level = (int) (-b + Math.sqrt(Math.pow(b, 2) - (4 * a * c))) / (2 * a);
	        int xpForLevel = (int) (Math.pow(level, 2) + (6 * level));
	        int remainder = xp - xpForLevel;
	        int experienceNeeded = (2 * level) + 7;
	        float experience = (float) remainder / (float) experienceNeeded;
	        experience = round(experience, 2);

	        //Set Everything
	        player.setLevel(level);
	        player.setExp(experience);
	        //Levels 16 through 30
	    } else if(xp >= 352 && xp < 1507) {
	        //Calculate Everything
	        double a = 2.5; double b = -40.5; int c = -xp + 360;
	        double dLevel = (-b + Math.sqrt(Math.pow(b, 2) - (4 * a * c))) / (2 * a);
	        int level = (int) Math.floor(dLevel);
	        int xpForLevel = (int) (2.5 * Math.pow(level, 2) - (40.5 * level) + 360);
	        int remainder = xp - xpForLevel;
	        int experienceNeeded = (5 * level) - 38;
	        float experience = (float) remainder / (float) experienceNeeded;
	        experience = round(experience, 2);

	        //Set Everything
	        player.setLevel(level);
	        player.setExp(experience);
	        //Level 31 and greater
	    } else {
	        //Calculate Everything
	        double a = 4.5; double b = -162.5; int c = -xp + 2220;
	        double dLevel = (-b + Math.sqrt(Math.pow(b, 2) - (4 * a * c))) / (2 * a);
	        int level = (int) Math.floor(dLevel);
	        int xpForLevel = (int) (4.5 * Math.pow(level, 2) - (162.5 * level) + 2220);
	        int remainder = xp - xpForLevel;
	        int experienceNeeded = (9 * level) - 158;
	        float experience = (float) remainder / (float) experienceNeeded;
	        experience = round(experience, 2);

	        //Set Everything
	        player.setLevel(level);
	        player.setExp(experience);
	    }
	}

	@SuppressWarnings("deprecation")
	private static float round(float d, int decimalPlace)
	{
	    BigDecimal bd = new BigDecimal(Float.toString(d));
	    bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_DOWN);
	    return bd.floatValue();
	}
}