package tk.jasonho.tally.snapin.bedwars;

import com.google.common.util.concurrent.AtomicDouble;
import com.google.gson.JsonObject;
import java.util.concurrent.atomic.AtomicInteger;
import net.avicus.grave.event.PlayerDeathEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.compmc.games.bedwars.BedWarsGame;
import org.compmc.games.bedwars.BedWarsPlugin;
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
import tc.oc.tracker.Damage;
import tc.oc.tracker.DamageInfo;
import tc.oc.tracker.Lifetime;
import tc.oc.tracker.damage.AnvilDamageInfo;
import tc.oc.tracker.damage.BlockDamageInfo;
import tc.oc.tracker.damage.ExplosiveDamageInfo;
import tc.oc.tracker.damage.FallDamageInfo;
import tc.oc.tracker.damage.GravityDamageInfo;
import tc.oc.tracker.damage.LavaDamageInfo;
import tc.oc.tracker.damage.MeleeDamageInfo;
import tc.oc.tracker.damage.OwnedMobDamageInfo;
import tc.oc.tracker.damage.ProjectileDamageInfo;
import tc.oc.tracker.damage.VoidDamageInfo;
import tc.oc.tracker.trackers.base.gravity.Fall;
import tk.jasonho.tally.core.bukkit.*;

import java.util.*;
import tk.jasonho.tally.snapin.core.competitive.CompetitiveOperations;
import tk.jasonho.tally.snapin.core.competitive.StatType;

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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeathEvent(PlayerDeathEvent event) {
        Player killed = event.getPlayer();
        BedWarsGame match = BedWarsPlugin.INSTANCE.getGame();
        Lifetime lifetime = event.getLifetime();
        Damage lastDamage = lifetime.getLastDamage();

        StringJoiner cause = new StringJoiner(".");
        UUID killer;
        if(lastDamage == null) {
            killer = DamageTrackModule.UNKNOWN;
            cause.add("unknown");
        } else {
            DamageInfo info = lastDamage.getInfo();

            LivingEntity resolvedDamager = info.getResolvedDamager();
            if(resolvedDamager != null) {
                if(!(resolvedDamager instanceof Player)) {
                    if(info instanceof OwnedMobDamageInfo) {
                        OwnedMobDamageInfo ownedInfo = (OwnedMobDamageInfo) info;
                        cause.add("ownedentity");

                        Player mobOwner = ownedInfo.getMobOwner();
                        if(mobOwner != null) {
                            killer = mobOwner.getUniqueId();
                        } else {
                            killer = DamageTrackModule.UNKNOWN;
                        }
                    } else {
                        killer = DamageTrackModule.ENVIRONMENT;
                        cause.add("entity");
                    }
                    cause.add(resolvedDamager.getType().name().toLowerCase().replaceAll("_", "-"));
                } else {
                    if(resolvedDamager instanceof Player) {
                        killer = resolvedDamager.getUniqueId();
                    } else {
                        killer = DamageTrackModule.UNKNOWN;
                    }

                    if(info instanceof AnvilDamageInfo) {
                        cause.add("anvil");
                    } else if(info instanceof ExplosiveDamageInfo) {
                        cause.add("explosive");
                    } else if(info instanceof GravityDamageInfo) {
                        cause.add("gravity");

                        GravityDamageInfo gravityInfo = (GravityDamageInfo) info;
                        Fall.Cause fallCause = gravityInfo.getCause();
                        Fall.From fallFrom = gravityInfo.getFrom();

                        if(fallCause == Fall.Cause.HIT) {
                            cause.add("hit");
                        } else if(fallCause == Fall.Cause.SHOOT) {
                            Damage projectileDamage = lifetime.getLastDamage(ProjectileDamageInfo.class);
                            cause.add("shot-dist-" + projectileDamage != null ?
                                ((ProjectileDamageInfo) projectileDamage.getInfo()).getDistance() + "" :
                                "unknown");
                        } else if(fallCause == Fall.Cause.SPLEEF) {
                            cause.add("spleefed");
                        }

                        if(fallFrom == Fall.From.FLOOR) {
                            cause.add("off-floor");
                        } else if(fallFrom == Fall.From.LADDER) {
                            cause.add("off-ladder");
                        } else if(fallFrom == Fall.From.WATER) {
                            cause.add("off-water");
                        }

                        if(event.getLocation().getY() < 0) cause.add("into-void");
                    } else if(info instanceof MeleeDamageInfo) {
                        cause.add("melee");

                        MeleeDamageInfo meleeInfo = (MeleeDamageInfo) info;
                        Material weapon = meleeInfo.getWeapon();

                        if(weapon == Material.AIR) {
                            cause.add("fist");
                        } else {
                            ItemStack weaponStack = meleeInfo.getWeaponStack();
                            cause.add(weapon.name().toLowerCase().replaceAll("_", "-"));
                            if(weaponStack != null && weaponStack.getItemMeta().hasDisplayName()) {
                                cause.add(weaponStack.getItemMeta().getDisplayName());
                            }
                        }
                    } else if(info instanceof ProjectileDamageInfo) {
                        cause.add("projectile");
                        ProjectileDamageInfo projectileInfo = (ProjectileDamageInfo) info;

                        cause.add(projectileInfo.getProjectile().getType().name().replaceAll("_", "-"));
                        cause.add("shot-dist-" +
                            (resolvedDamager != null ? lastDamage.getLocation().distance(resolvedDamager.getLocation()) : "unknown"));
                    } else if(info instanceof VoidDamageInfo) {
                        cause.add("void");
                    } else {
                        cause.add("unknown");
                    }
                }
            } else {
                killer = DamageTrackModule.ENVIRONMENT;
                if(info instanceof AnvilDamageInfo) {
                    cause.add("anvil");
                } else if(info instanceof BlockDamageInfo) {
                    cause.add("block");
                } else if(info instanceof ExplosiveDamageInfo) {
                    cause.add("explosive");
                } else if(info instanceof FallDamageInfo) {
                    cause.add("fall-dist-" + ((FallDamageInfo) info).getFallDistance());
                } else if(info instanceof LavaDamageInfo) {
                    cause.add("lava");
                } else if(info instanceof VoidDamageInfo) {
                    cause.add("void");
                } else if(info instanceof ProjectileDamageInfo) {
                    cause.add("projectile");
                } else {
                    cause.add("unknown");
                }
            }
        }

        // Assists
        CompetitiveOperations operationHandler = ((CompetitiveOperations) super.operationHandler);
        DamageTrackModule damageTrackModule = operationHandler.getTally().getDamageTrackModule();
        List<DamageTrackModule.DamageExchange> damageExchanges = damageTrackModule.getDamageExchanges();

        Map<UUID, Pair<AtomicDouble, AtomicInteger>> assisters = new HashMap<>();
        for (DamageTrackModule.DamageExchange exc : damageExchanges) {
            if(exc.getDirection() == DamageTrackModule.DamageDirection.GIVE && exc.getYou() == killed.getUniqueId()) {
                if(exc.getMe() == killer // is the killer
                    || exc.getMe() == killed.getUniqueId() // or is suicide
                    || exc.isCreditRewarded()) { // or is already tracked/rewarded
                    continue;
                }

                if(assisters.containsKey(exc.getMe())) { // if previously damaged
                    assisters.get(exc.getMe()).getLeft().addAndGet(exc.getAmount()); // add to damage total
                    assisters.get(exc.getMe()).getRight().incrementAndGet(); // add to hits total
                } else { // not previously damaged
                    assisters.put(exc.getMe(), Pair.of(new AtomicDouble(exc.getAmount()), new AtomicInteger(1))); // insert to damagers
                }

                // set as rewarded :)
                exc.setCreditRewarded(true);
            }
        }

        // track!
        operationHandler.trackPVPTransaction(killer, killed.getUniqueId(), cause.toString());

        JsonObject jsonObject = BedwarsUtils.newDataFromGame(match);
        jsonObject.addProperty("caused_by_type", cause.toString());
        jsonObject.addProperty("assisted", killer.toString());

        for (Map.Entry<UUID, Pair<AtomicDouble, AtomicInteger>> assist : assisters.entrySet()) {
            Player assister = Bukkit.getPlayer(assist.getKey());
            if(assister != null) {
                operationHandler.track(StatType.ASSIST, killed.getUniqueId(), assister.getUniqueId(), jsonObject);
            }
        }
    }

}
