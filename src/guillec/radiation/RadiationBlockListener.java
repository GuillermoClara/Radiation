package guillec.radiation;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;

public class RadiationBlockListener implements Listener {
	private Radiation plugin;
	public RadiationBlockListener(Radiation instance) {this.plugin = instance;}
	
	@EventHandler
	  public void onBlockSpread(BlockSpreadEvent event) {
	    String source = event.getSource().getType().name();
	    if (source == "GRASS") {
	      event.setCancelled(true);
	    }
	  }
	  
	  @EventHandler
	  public void onBlockBreak(BlockBreakEvent event) {
	    Player p = event.getPlayer();
	    org.bukkit.World w = p.getWorld();
	    int px = p.getLocation().getBlockX();
	    int py = p.getLocation().getBlockY();
	    int pz = p.getLocation().getBlockZ();
	    String blockref = String.valueOf(w.getName()) + "-" + px + "-" + py + "-" + pz;
	    this.plugin.blockmap.put(blockref, null);
	  }
	  
	  @EventHandler
	  public void onBlockPlace(BlockPlaceEvent event) {
	    Player p = event.getPlayer();
	    org.bukkit.World w = p.getWorld();
	    int px = p.getLocation().getBlockX();
	    int py = p.getLocation().getBlockY();
	    int pz = p.getLocation().getBlockZ();
	    String blockref = String.valueOf(w.getName()) + "-" + px + "-" + py + "-" + pz;
	    this.plugin.blockmap.put(blockref, null);
	  }
	
}
