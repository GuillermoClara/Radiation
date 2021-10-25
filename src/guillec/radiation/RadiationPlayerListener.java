package guillec.radiation;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class RadiationPlayerListener implements Listener {
	private Radiation plugin;
	public RadiationPlayerListener(Radiation instance) { this.plugin = instance; }
	
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item != null && item.getTypeId() == this.plugin.getConfig().getInt("GeigerID") && player.hasPermission("irradiated.geiger")) {
          this.plugin.doGeiger(player);
        }
    }
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        this.plugin.playerrads.put(player.getName(), "0");
      }
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onEat(FoodLevelChangeEvent event) {
      Player player = (Player)event.getEntity();
      ItemStack food = event.getEntity().getItemInHand();
      if (food.getTypeId() == this.plugin.getConfig().getInt("RadX")) {
        String getrads = (String)this.plugin.playerrads.get(player.getName());
        double myrads = 0.0D;
        if (getrads != null) myrads = Double.parseDouble(getrads); 
        myrads -= myrads*0.75;
        this.plugin.playerrads.put(player.getName(), String.valueOf(myrads));
      } 
    }
}
