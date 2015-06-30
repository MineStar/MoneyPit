package de.minestar.moneypit.queues.entity;

import de.minestar.moneypit.data.protection.EntityProtection;

public interface EntityQueue {

    public boolean execute();

    public EntityProtection getProtection();
}
