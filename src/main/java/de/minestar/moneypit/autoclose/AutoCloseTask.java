package de.minestar.moneypit.autoclose;

import org.bukkit.block.Block;

import de.minestar.moneypit.utils.DoorHelper;

public class AutoCloseTask implements Runnable {

    private final Block block;

    public AutoCloseTask(Block block) {
        this.block = block;
    }

    @Override
    public void run() {
        DoorHelper.closeDoor(block);
    }
}
