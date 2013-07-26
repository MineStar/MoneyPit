package de.minestar.moneypit.data;

import com.bukkit.gemo.patchworking.IProtection;

public class EventResult {
    private final boolean cancelEvent;
    private final boolean abort;
    private final IProtection protection;

    public EventResult(boolean cancelEvent, boolean abort, IProtection protection) {
        this.cancelEvent = cancelEvent;
        this.abort = abort;
        this.protection = protection;
    }

    public IProtection getProtection() {
        return protection;
    }

    /**
     * @return the cancelEvent
     */
    public boolean isCancelEvent() {
        return cancelEvent;
    }

    /**
     * @return the abort
     */
    public boolean isAbort() {
        return abort;
    }
}
