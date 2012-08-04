package de.minestar.moneypit.data;

public class EventResult {
    private final boolean cancelEvent;
    private final boolean abort;

    public EventResult(boolean cancelEvent, boolean abort) {
        this.cancelEvent = cancelEvent;
        this.abort = abort;
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
