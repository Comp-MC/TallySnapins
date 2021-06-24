package tk.jasonho.tally.snapin.skywars;

import com.google.gson.JsonObject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.compmc.games.core.events.GamePreStartEvent;
import org.compmc.games.core.events.GameStartEvent;
import org.compmc.games.core.events.GameTieEvent;
import org.compmc.games.core.events.GameWinEvent;
import org.compmc.games.core.events.PlayerEliminateEvent;
import org.compmc.games.core.gamer.Gamer;
import org.compmc.games.core.teams.Team;
import tk.jasonho.tally.core.bukkit.*;

public class SkywarsListener extends TallyListener {

    public SkywarsListener(TallyOperationHandler handler) {
        super(handler);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWinEvent(GameWinEvent event) {
        Team winningTeam = event.getWinner();

        JsonObject data = SkywarsUtils.newDataFromGame(event.getGame(), winningTeam);
        for (Gamer member : winningTeam.getMembers()) {
            super.operationHandler.track("skywars_win", null, member.getPlayer().getUniqueId(), data);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTieEvent(GameTieEvent event) {
        for (Team team : event.getGame().getTeamManager().getTeams()) {
            JsonObject data = SkywarsUtils.newDataFromGame(event.getGame(), team);
            for (Gamer member : team.getMembers()) {
                super.operationHandler.track("skywars_tie", null, member.getPlayer().getUniqueId(), data);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onStartEvent(GameStartEvent event) {
        for (Team team : event.getGame().getTeamManager().getTeams()) {
            JsonObject data = SkywarsUtils.newDataFromGame(event.getGame(), team);
            for (Gamer member : team.getMembers()) {
                super.operationHandler.track("skywars_start", null, member.getPlayer().getUniqueId(), data);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPreStartEvent(GamePreStartEvent event) {
        for (Team team : event.getGame().getTeamManager().getTeams()) {
            JsonObject data = SkywarsUtils.newDataFromGame(event.getGame(), team);
            for (Gamer member : team.getMembers()) {
                super.operationHandler.track("skywars_prestart", null, member.getPlayer().getUniqueId(), data);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEliminateEvent(PlayerEliminateEvent event) {
        Team team = event.getGame().getTeamManager().getTeam(event.getGamer());
        JsonObject data = team == null ? SkywarsUtils.newDataFromGame(event.getGame()) : SkywarsUtils.newDataFromGame(event.getGame(), team);
        super.operationHandler.track("skywars_participate", null, event.getGamer().getPlayer().getUniqueId(), data);
    }
}
