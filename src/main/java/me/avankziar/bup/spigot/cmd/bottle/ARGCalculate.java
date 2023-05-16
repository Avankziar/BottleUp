package main.java.me.avankziar.bup.spigot.cmd.bottle;

import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.bup.spigot.BottleUp;
import main.java.me.avankziar.bup.spigot.assistance.Experience;
import main.java.me.avankziar.bup.spigot.assistance.MatchApi;
import main.java.me.avankziar.bup.spigot.cmdtree.ArgumentConstructor;
import main.java.me.avankziar.bup.spigot.cmdtree.ArgumentModule;
import main.java.me.avankziar.bup.spigot.handler.ConfigHandler;
import main.java.me.avankziar.ifh.general.assistance.ChatApi;

public class ARGCalculate extends ArgumentModule
{
	private BottleUp plugin;
	
	public ARGCalculate(ArgumentConstructor argumentConstructor)
	{
		super(argumentConstructor);
		this.plugin = BottleUp.getPlugin();
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		Player player = (Player) sender;
		final int plevel = player.getLevel();
		int level = 0;
		if(MatchApi.isInteger(args[1]))
		{
			level = Integer.parseInt(args[1]);
		}
		if(plevel > level) //Fill
		{
			double expinb = new ConfigHandler().getExpIntoBottle(player);
			double ptexp = Experience.getExp(player);
			double gtexp = Experience.getExp(level);
			int boam = (int) Math.floor((ptexp-gtexp)/expinb);
			player.spigot().sendMessage(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("CmdBottle.CalculateFill")
					.replace("%level%", String.valueOf(level))
					.replace("%bottleamount%", String.valueOf(boam))
					.replace("%playerexp%", String.valueOf((int)ptexp))
					.replace("%gtexp%", String.valueOf((int)gtexp))
					.replace("%expbottle%", String.valueOf((int)expinb))
					));
		} else if(plevel < level) //use
		{
			double expfromb = new ConfigHandler().getExpFromBottle(player);
			double ptexp = Experience.getExp(player);
			double gtexp = Experience.getExp(level);
			int boam = (int) Math.ceil((gtexp-ptexp)/expfromb);
			player.spigot().sendMessage(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("CmdBottle.CalculateUse")
					.replace("%level%", String.valueOf(level))
					.replace("%bottleamount%", String.valueOf(boam))
					.replace("%playerexp%", String.valueOf((int)ptexp))
					.replace("%gtexp%", String.valueOf((int)gtexp))
					.replace("%expbottle%", String.valueOf((int)expfromb))
					));
		} else //plevel == level
		{
			player.spigot().sendMessage(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("CmdBottle.NothingToCalculate")));
		}
	}
}