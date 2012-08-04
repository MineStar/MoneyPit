package de.minestar.moneypit.data;

import de.minestar.moneypit.data.protection.Protection;

public class EventResult {
    private final boolean cancelEvent;
    private final boolean abort;
    private final Protection protection;

    public EventResult(boolean cancelEvent, boolean abort, Protection protection) {
        this.cancelEvent = cancelEvent;
        this.abort = abort;
        this.protection = protection;
    }

    public Protection getProtection() {
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
