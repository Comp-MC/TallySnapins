package tk.jasonho.tally.snapin.skywars;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.compmc.skywars.SkyWarsGame;
import org.compmc.skywars.SkyWarsTeam;

public class SkywarsUtils {

    public static JsonObject newDataFromGame(SkyWarsGame game) {
        JsonObject data = new JsonObject();
        data.addProperty("map", game.getData().getName());
        data.addProperty("player_count", game.getTeams().stream().mapToInt(value -> value.getMembers().size()).sum());

        JsonObject teams = new JsonObject();
        for (SkyWarsTeam team : game.getTeams()) {
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

    public static JsonObject newDataFromGame(SkyWarsGame game, SkyWarsTeam team) {
        JsonObject data = newDataFromGame(game);
        data.addProperty("player_team", team.getName());
        return data;
    }

}
