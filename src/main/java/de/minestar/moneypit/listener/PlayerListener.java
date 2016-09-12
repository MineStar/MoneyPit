package de.minestar.moneypit.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import de.minestar.moneypit.MoneyPitCore;

public class PlayerListener implements Listener {

    public PlayerListener() {
        
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        if (MoneyPitCore.playerManager.noAutoLock(player.getName())) {
            MoneyPitCore.playerManager.removeNoAutoLock(player.getName());
        }
    }
}
