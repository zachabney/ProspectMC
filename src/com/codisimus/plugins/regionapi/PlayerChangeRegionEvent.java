package com.codisimus.plugins.regionapi;

import com.codisimus.plugins.regiontools.Region;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerChangeRegionEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private Player player;
    private Region to;
    private Region from;

    public PlayerChangeRegionEvent(Player player, Region to, Region from) {
        this.player = player;
        this.to = to;
        this.from = from;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public Region getTo() {
        return to;
    }

    public Region getFrom() {
        return from;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean arg) {
        cancelled = arg;
    }
}
