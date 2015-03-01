package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.bukkit.gemo.patchworking.BlockVector;
import com.bukkit.gemo.patchworking.IProtection;

import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.data.EventResult;
import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.manager.ModuleManager;
import de.minestar.moneypit.utils.ChestHelper;

public class Module_TrappedChest extends Module {

    private final String NAME = "trappedchest";

    public Module_TrappedChest(YamlConfiguration ymlFile) {
        this.writeDefaultConfig(NAME, ymlFile);
    }

    public Module_TrappedChest(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super();
        this.init(moduleManager, ymlFile, Material.TRAPPED_CHEST.getId(), NAME);
        this.setDoNeighbourCheck(true);
    }

    @Override
    public boolean addProtection(IProtection protection, byte subData, boolean saveToDatabase) {
        // search a second chest and add the subprotection, if found
        Chest secondChest = ChestHelper.isDoubleTrappedChest(protection.getVector().getLocation().getBlock());

        if (secondChest != null) {
            IProtection subProtection = new Protection(new BlockVector(secondChest.getLocation()), protection);
            protection.addSubProtection(subProtection);
            MoneyPitCore.databaseManager.createSubProtection(subProtection, saveToDatabase);
        }

        // register the protection
        return getProtectionManager().addProtection(protection);
    }

    @Override
    public EventResult onPlace(Player player, BlockVector vector) {
        // search a second chest
        BlockVector doubleChest = ChestHelper.getDoubleTrappedChest(vector);
        if (doubleChest == null) {
            return new EventResult(false, false, null);
        }

        // check if there is a protection
        IProtection protection = MoneyPitCore.protectionManager.getProtection(doubleChest);
        if (protection == null) {
            return new EventResult(false, false, null);
        }

        // check permissions
        if (!protection.canEdit(player)) {
            PlayerUtils.sendError(player, MoneyPitCore.NAME, "You cannot place a chest here.");
            PlayerUtils.sendInfo(player, "The neighbour is a protected chest.");
            return new EventResult(true, true, protection);
        }

        // add the SubProtection to the Protection
        IProtection subProtection = new Protection(vector, protection);
        protection.addSubProtection(subProtection);

        // add the SubProtection to the ProtectionManager
        MoneyPitCore.protectionManager.addSubProtection(subProtection);
        MoneyPitCore.databaseManager.createSubProtection(subProtection, true);

        // send info
        PlayerUtils.sendInfo(player, MoneyPitCore.NAME, "Subprotection created.");
        return new EventResult(false, true, null);
    }
}
