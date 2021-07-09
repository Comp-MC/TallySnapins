package tk.jasonho.tally.snapin.bedwars;

import tk.jasonho.tally.core.bukkit.TallyOperationHandler;
import tk.jasonho.tally.core.bukkit.TallyPlugin;
import tk.jasonho.tally.snapin.core.competitive.CompetitiveOperations;
import tk.jasonho.tally.snapin.core.competitive.Snapin;

public class BedwarsSnapin extends Snapin {

    public TallyPlugin tallyInstance;
    public TallyOperationHandler operationHandler;
    public BedwarsListener bedwarsListener;

    @Override
    public void onEnable() {
        this.getLogger().info("Tally Snap-in for Bedwars is loading...");
        this.tallyInstance = TallyPlugin.getInstance();
        this.operationHandler = new CompetitiveOperations(tallyInstance);

        this.bedwarsListener = new BedwarsListener(this.operationHandler);
        this.tallyInstance.registerTallyListener(this.bedwarsListener, this);

        this.getLogger().info("Tally Snap-in for Bedwars loaded.");
    }

    @Override
    public void onDisable() {
        this.tallyInstance.unregisterTallyListener(this.bedwarsListener);
    }
}
