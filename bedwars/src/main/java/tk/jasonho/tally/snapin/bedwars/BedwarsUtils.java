package tk.jasonho.tally.snapin.bedwars;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.compmc.games.bedwars.BedWarsGame;
import org.compmc.games.bedwars.teams.BWTeamManager;
import org.compmc.games.core.gamer.GamerRegistry;
import org.compmc.games.core.info.MapInfoComponent;
import org.compmc.games.core.teams.Team;

public class BedwarsUtils {

    public static JsonObject newDataFromGame(BedWarsGame game) {
        JsonObject data = new JsonObject();
        data.addProperty("map", game.needComponent(MapInfoComponent.class).getName());
        data.addProperty("player_count", GamerRegistry.INSTANCE.getGamers().size());

        JsonObject teams = new JsonObject();
        for (Team team : game.needComponent(BWTeamManager.class).getTeams()) {
            JsonObject teamData = new JsonObject();

            JsonArray players = new JsonArray();
            team.getMembers().forEach(p -> {
                players.add(new JsonPrimitive(p.toString()));
            });

            teamData.addProperty("name", team.getName());
            teamData.addProperty("color", team.getColor().toString());
            teamData.addProperty("spawn", team.getSpawn().toString());
            teamData.add("members", players);

            teams.add(team.getName(), teamData);
        }
        data.add("teams", teams);

        return data;
    }

    public static JsonObject newDataFromGame(BedWarsGame game, Team team) {
        JsonObject data = newDataFromGame(game);
        data.addProperty("player_team", team.getName());
        return data;
    }

}
