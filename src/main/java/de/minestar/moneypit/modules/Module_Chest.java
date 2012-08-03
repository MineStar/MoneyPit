package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.block.BlockPlaceEvent;

import com.bukkit.gemo.utils.BlockUtils;

import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.Core;
import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.data.subprotection.SubProtection;
import de.minestar.moneypit.manager.ModuleManager;
import de.minestar.moneypit.utils.ChestHelper;

public class Module_Chest extends Module {

    private final String NAME = "chest";

    public Module_Chest(YamlConfiguration ymlFile) {
        this.writeDefaultConfig(NAME, ymlFile);
    }

    public Module_Chest(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super();
        this.init(moduleManager, ymlFile, Material.CHEST.getId(), NAME);
        this.setDoNeighbourCheck(true);
    }

    @Override
    public void addProtection(Protection protection, byte subData) {
        // search a second chest and add the subprotection, if found
        Chest secondChest = BlockUtils.isDoubleChest(protection.getVector().getLocation().getBlock());
        if (secondChest != null) {
            SubProtection subProtection = new SubProtection(new BlockVector(secondChest.getLocation()), protection);
            protection.addSubProtection(subProtection);
        }

        // register the protection
        getProtectionManager().addProtection(protection);
    }

    @Override
    public boolean onPlace(BlockPlaceEvent event, BlockVector vector) {
        // search a second chest
        BlockVector doubleChest = ChestHelper.getDoubleChest(vector);
        if (doubleChest == null) {
            return false;
        }

        // check if there is a protection
        Protection protection = Core.protectionManager.getProtection(doubleChest);
        if (protection == null) {
            return false;
        }

        // check permissions
        if (!protection.canEdit(event.getPlayer())) {
            PlayerUtils.sendError(event.getPlayer(), Core.NAME, "You cannot place a chest here.");
            PlayerUtils.sendInfo(event.getPlayer(), "The neighbour is a protected chest.");
            event.setCancelled(true);
            return true;
        }

        // add the SubProtection to the Protection
        SubProtection subProtection = new SubProtection(vector, protection);
        protection.addSubProtection(subProtection);
        // add the SubProtection to the ProtectionManager
        Core.protectionManager.addSubProtection(subProtection);

        // send info
        PlayerUtils.sendInfo(event.getPlayer(), Core.NAME, "Subprotection created.");

        // return true to abort the event
        return true;
    }
}
