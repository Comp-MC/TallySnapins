package tk.jasonho.tally.snapin.bedwars;

import com.google.gson.JsonObject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.compmc.games.bedwars.BedWarsGame;
import org.compmc.games.bedwars.events.BedDestroyEvent;
import org.compmc.games.bedwars.teams.BWTeamManager;
import org.compmc.games.bedwars.teams.BedWarsTeam;
import org.compmc.games.core.events.GamePreStartEvent;
import org.compmc.games.core.events.GameStartEvent;
import org.compmc.games.core.events.GameTieEvent;
import org.compmc.games.core.events.GameWinEvent;
import org.compmc.games.core.events.PlayerEliminateEvent;
import org.compmc.games.core.gamer.Gamer;
import org.compmc.games.core.teams.Team;
import tk.jasonho.tally.core.bukkit.*;

import java.util.*;

public class BedwarsListener extends TallyListener {

    public BedwarsListener(TallyOperationHandler handler) {
        super(handler);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBedDestroy(BedDestroyEvent event) {
        JsonObject data = BedwarsUtils.newDataFromGame(
            ((BedWarsGame) event.getGame()),
            Objects.requireNonNull(event.getGame().needComponent(BWTeamManager.class)
                .getTeam(event.getActor())));
        super.operationHandler.track("bedwars_bed_broken",
            null,
            event.getActor().getPlayer().getUniqueId(),
            data);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWinEvent(GameWinEvent event) {
        BedWarsTeam winningTeam = ((BedWarsTeam) event.getWinner());

        JsonObject data = BedwarsUtils.newDataFromGame(
            ((BedWarsGame) event.getGame()), winningTeam);
        for (Gamer member : winningTeam.getMembers()) {
            super.operationHandler.track("bedwars_win", null, member.getPlayer().getUniqueId(), data);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTieEvent(GameTieEvent event) {
        for (Team team : event.getGame().needComponent(BWTeamManager.class).getTeams()) {
            JsonObject data = BedwarsUtils.newDataFromGame(
                ((BedWarsGame) event.getGame()), team);
            for (Gamer member : team.getMembers()) {
                super.operationHandler.track("bedwars_tie", null, member.getPlayer().getUniqueId(), data);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onStartEvent(GameStartEvent event) {
        for (Team team : event.getGame().needComponent(BWTeamManager.class).getTeams()) {
            JsonObject data = BedwarsUtils.newDataFromGame((BedWarsGame) event.getGame(), team);
            for (Gamer member : team.getMembers()) {
                super.operationHandler.track("bedwars_start", null, member.getPlayer().getUniqueId(), data);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPreStartEvent(GamePreStartEvent event) {
        for (Team team : event.getGame().needComponent(BWTeamManager.class).getTeams()) {
            JsonObject data = BedwarsUtils.newDataFromGame((BedWarsGame) event.getGame(), team);
            for (Gamer member : team.getMembers()) {
                super.operationHandler.track("bedwars_prestart", null, member.getPlayer()
                    .getUniqueId(), data);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEliminateEvent(PlayerEliminateEvent event) {
        BedWarsTeam team = event.getGame().needComponent(BWTeamManager.class)
            .getTeam(event.getGamer());
        JsonObject data = team == null ? BedwarsUtils.newDataFromGame(
            ((BedWarsGame) event.getGame())) : BedwarsUtils
            .newDataFromGame(((BedWarsGame) event.getGame()), team);
        super.operationHandler.track("bedwars_participate", null, event.getGamer().getPlayer()
            .getUniqueId(), data);
    }
}
