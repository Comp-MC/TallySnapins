package tk.jasonho.tally.snapin.skywars;

import tk.jasonho.tally.core.bukkit.TallyOperationHandler;
import tk.jasonho.tally.core.bukkit.TallyPlugin;
import tk.jasonho.tally.snapin.core.competitive.CompetitiveOperations;
import tk.jasonho.tally.snapin.core.competitive.Snapin;

public class SkywarsSnapin extends Snapin {

    public TallyPlugin tallyInstance;
    public TallyOperationHandler operationHandler;
    public SkywarsListener skywarsListener;

    @Override
    public void onEnable() {
        this.getLogger().info("Tally Snap-in for Skywars is loading...");
        this.tallyInstance = TallyPlugin.getInstance();
        this.operationHandler = new CompetitiveOperations(tallyInstance);

        this.skywarsListener = new SkywarsListener(this.operationHandler);
        this.tallyInstance.registerTallyListener(this.skywarsListener, this);

        this.getLogger().info("Tally Snap-in for Skywars loaded.");
    }

    @Override
    public void onDisable() {
        this.tallyInstance.unregisterTallyListener(this.skywarsListener);
    }
}
