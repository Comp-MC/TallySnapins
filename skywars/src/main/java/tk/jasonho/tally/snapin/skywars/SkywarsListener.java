package tk.jasonho.tally.snapin.skywars;

import com.google.gson.JsonObject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.compmc.games.skywars.SkyWarsTeam;
import org.compmc.games.core.events.*;
import tk.jasonho.tally.core.bukkit.*;

import java.util.*;

public class SkywarsListener extends TallyListener {

    public SkywarsListener(TallyOperationHandler handler) {
        super(handler);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWinEvent(GameWinEvent event) {
        SkyWarsTeam winningTeam = event.getWinner();

        JsonObject data = SkywarsUtils.newDataFromGame(event.getGame(), winningTeam);
        for (UUID member : winningTeam.getMembers()) {
            super.operationHandler.track("skywars_win", null, member, data);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTieEvent(GameTieEvent event) {
        for (SkyWarsTeam team : event.getGame().getTeams()) {
            JsonObject data = SkywarsUtils.newDataFromGame(event.getGame(), team);
            for (UUID member : team.getMembers()) {
                super.operationHandler.track("skywars_tie", null, member, data);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onStartEvent(GameStartEvent event) {
        for (SkyWarsTeam team : event.getGame().getTeams()) {
            JsonObject data = SkywarsUtils.newDataFromGame(event.getGame(), team);
            for (UUID member : team.getMembers()) {
                super.operationHandler.track("skywars_start", null, member, data);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPreStartEvent(GamePreStartEvent event) {
        for (SkyWarsTeam team : event.getGame().getTeams()) {
            JsonObject data = SkywarsUtils.newDataFromGame(event.getGame(), team);
            for (UUID member : team.getMembers()) {
                super.operationHandler.track("skywars_prestart", null, member, data);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEliminateEvent(PlayerEliminateEvent event) {
        SkyWarsTeam team = event.getGame().getTeam(event.getPlayer());
        JsonObject data = team == null ? SkywarsUtils.newDataFromGame(event.getGame()) : SkywarsUtils.newDataFromGame(event.getGame(), team);
        super.operationHandler.track("skywars_participate", null, event.getPlayer().getUniqueId(), data);
    }
}
