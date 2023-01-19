package main.java.me.avankziar.bup.spigot.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import main.java.me.avankziar.bup.spigot.handler.ConfigHandler;

public class PlayerExpBottleListener implements Listener
{	
	@EventHandler
	public void onPlayerThrow(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		if(player.getInventory().getItemInMainHand() == null)
		{
			return;
		}
		if(player.getInventory().getItemInMainHand().getType() != Material.EXPERIENCE_BOTTLE)
		{
			return;
		}
		if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
		{
			return;
		}
		if(!new ConfigHandler().isMechanicVanillaThrowExpBottle())
		{
			event.setCancelled(true);
		}
	}
}