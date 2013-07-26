package de.minestar.moneypit.autoclose;

import org.bukkit.block.Block;

import com.bukkit.gemo.patchworking.BlockVector;

public class QueuedAutoClose implements Comparable<QueuedAutoClose> {
    private final BlockVector vector;
    private final long endTime;

    public QueuedAutoClose(BlockVector vector) {
        this.vector = vector;
        this.endTime = System.currentTimeMillis() + (1000 * 2);
    }

    public BlockVector getVector() {
        return vector;
    }

    public Block getBlock() {
        return vector.getLocation().getBlock();
    }

    public boolean hasEnded(long timestamp) {
        return this.endTime <= timestamp;
    }

    @Override
    public int hashCode() {
        return this.vector.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.vector.equals(obj);
    }

    @Override
    public int compareTo(QueuedAutoClose other) {
        return this.hashCode() - other.hashCode();
    }
}
