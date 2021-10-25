package guillec.radiation;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Radiation extends JavaPlugin {
	private RadiationCommandExecutor myExecutor;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, String> map= new HashMap();
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, String> shelter = new HashMap();
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, String> blockmap = new HashMap();
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, String> playerrads = new HashMap();
	public String[] IrradiatedWorlds = new String[0];
    
	public void doGeiger(Player sender) {
		double myRads = scanRadiation(sender, false); 
		BigDecimal displayRads = BigDecimal.valueOf(myRads).setScale(2, 4);
	    sender.sendMessage(ChatColor.RED + "Radiacion en el ambiente: " + ChatColor.WHITE + displayRads + " mSv");
	    String radcount = "0";
	    radcount = (String)this.playerrads.get(sender.getName());
	    sender.sendMessage(ChatColor.RED + "Nivel de radiacion: " + ChatColor.WHITE + radcount + " mSv");
	}
	
	public void onEnable() {
		getLogger().info("Irradiating...");
	    this.myExecutor = new RadiationCommandExecutor(this);
	    getCommand("radiation").setExecutor(this.myExecutor);
	    getCommand("geiger").setExecutor(this.myExecutor);
	    getConfig().options().copyDefaults(true);
	    saveConfig();
	    
	    PluginManager pm = getServer().getPluginManager();
	    pm.registerEvents(new RadiationPlayerListener(this), this);
	    pm.registerEvents(new RadiationBlockListener(this), this);
	    pm.registerEvents(new RadiationEntityListener(this), this);
	    int tickTimer = getConfig().getInt("UpdateFrequency");
	    this.IrradiatedWorlds = getConfig().getString("IrradiatedWorlds").split(",");
	    
	    int blocksParsed = 0;
	    List<String> radiationBlocks = getConfig().getStringList("RadiationBlocks");
	    
	    for (String s : radiationBlocks) {
	        String[] kv = s.split(":");
	        this.map.put(kv[0], kv[1]);
	        blocksParsed++;
	      }
	    if (blocksParsed < 1) {
	        getLogger().severe("Unable to parse any radiation blocks!");
	      }
	    
	    List<String> shelterBlocks = getConfig().getStringList("ShelterBlocks");
	    for (String s : shelterBlocks) {
	      String[] kv1 = s.split(":");
	      this.shelter.put(kv1[0], kv1[1]);
	    }
	    if (tickTimer < 20) tickTimer = 20;
	    
	    getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable()
	        {
	          public void run()
	          {
	            Radiation.this.doTick();
	          }
	        }, 
	        tickTimer, tickTimer);
	    getLogger().info("...DONE!"); } protected void doTick() {
	    	for (Player player: Bukkit.getOnlinePlayers()) {
	    		if (!player.isDead() && player.getGameMode() != GameMode.CREATIVE) {
	    			double rads = scanRadiation(player, true);
	    			String getrads = (String)this.playerrads.get(player.getName());
	    			double myrads= 0.0D;
	    			if (getrads != null) myrads = Double.parseDouble(getrads);
	    			if (rads > 15.0D) {
	    				if (getConfig().getBoolean("AnnounceRads")) player.sendMessage(ChatColor.RED + "Radiacion" + ChatColor.WHITE + ": +" + rads + " mSv");
	    				myrads += rads;
	    				this.playerrads.put(player.getName(), String.valueOf(myrads));
	    			}
	    			else if (myrads >0.0D) {
	    				myrads -= 0.1D;
	    				this.playerrads.put(player.getName(), String.valueOf(myrads));
	    			}
	    			if (getConfig().getBoolean("RadSickness")) {
	    				if (myrads > 10000.0D) {
	    					new BukkitRunnable() {
	    						@Override
	    						public void run() {
	    							player.addPotionEffect(new PotionEffect(PotionEffectType.HARM, 200, 0), true);
	    						}	
	    					}.runTaskLater(this, 20);
	    					
	    				}
	    				else if (myrads > 7500.0D) {
	    					new BukkitRunnable() {
	    						@Override
	    						public void run() {
	    							player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 0), true);
	    							player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 300, 0), true);
	    							player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 300, 0), true);
	    							player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 300, 0), true);
	    						}	
	    					}.runTaskLater(this, 20);				
	    				}
	    				else if (myrads > 6000.0D) {
	    					new BukkitRunnable() {
	    						@Override
	    						public void run() {
	    							player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 300, 0), true);
	    							player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 300, 0), true);
	    							player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 300, 0), true);
	    						}	
	    					}.runTaskLater(this, 20);				
	    				}
	    				else if (myrads >5000.0D) {
	    					new BukkitRunnable() {
	    						@Override
	    						public void run() {
	    							player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 300, 0), true);
	    							player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 300, 0), true);
	    						}	
	    					}.runTaskLater(this, 20);    					
	    				}
	    				else if (myrads > 1000.0D) {
	    					new BukkitRunnable() {
	    						@Override
	    						public void run() {
	    	    					player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 300, 0), true);
	    						}	
	    					}.runTaskLater(this, 20); 
	    				}
	    				else if (myrads> 500.0D) {
	    					new BukkitRunnable() {
	    						@Override
	    						public void run() {
	    							player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 300, 0), true);
	    						}	
	    					}.runTaskLater(this, 20); 	    					
	    				}
	    			}
	    			
	    			if (getConfig().getInt("DoT") > 0) {
	    				int damage = (int)myrads / getConfig().getInt("DoT");
	    				if (damage > 20) damage = 20;
	    				if (!player.hasPermission("irradiated.hazmat") && damage > 0) {
	    					player.damage(damage);
	    				}
	    			}
	    		}
	    		}
	    }
	    public void onDisable() {
	    	getLogger().info("Deradiated");
	    }
	    public int getNearbyPlayers(Player player, int distance) {
	    	int res= 0;
	    	int d2 = distance * distance;
	    	for (Player p: Bukkit.getOnlinePlayers()) {
	    		if (p.getWorld() == p.getWorld() && p.getLocation().distanceSquared(p.getLocation()) <= d2)
	    			res++;
        }
	    	return res;
	    }
		@SuppressWarnings("deprecation")
		public double scanRadiation(Player player, boolean processFlag) {
	    	World world = player.getWorld();
	    	if (ArrayUtils.contains(this.IrradiatedWorlds, world.getName())) {
	    		Location tickloc = player.getLocation();
	    	      int px = tickloc.getBlockX() - 1;
	    	      int py = tickloc.getBlockY();
	    	      int pz = tickloc.getBlockZ();
	    	      
	    	      int dz = 0;
	    	      int range = 8;
	    	      double rads = 0.0D;
	    	      double naturalRads = 0.0D;
	    	      String blockrads = "";
	    	      String shelterrads = "";
	    	      int convertCount = 0;
	    	      double distance= Math.sqrt(Math.pow(px +3664, 2) + Math.pow(pz +940, 2));
	    	      
	    	      if (getConfig().getBoolean("AcidRain")) {
	    	    	  int topblock = world.getHighestBlockYAt(tickloc);
	    	          Biome biome = world.getBiome(px, pz);
	    	          if (world.hasStorm() && biome != Biome.DESERT && distance<=1250 && py + 1 > topblock) {
	    	        	  naturalRads += 28.0D; rads += 28.0D;
	    	          }
	    	      }
	    	      if (getConfig().getBoolean("IrradiatedWater")) {
	    	    	  Material m = tickloc.getBlock().getType();
	    	    	  if ((m == Material.STATIONARY_WATER || m == Material.WATER || m == Material.SNOW || m == Material.SNOW_BLOCK) && !player.isInsideVehicle()) {
	    	    		  naturalRads += 25.0D; rads += 25.0D;
	    	    	  }
	    	      }
	    	      if (getConfig().getDouble("PlayerShelter") > 0.0D) {
	    	    	  int nearby = getNearbyPlayers(player, 16);
	    	          naturalRads -= nearby * getConfig().getDouble("PlayerShelter");
	    	      }
	    	      
	    	      for (int i = 1; i <= range; i++) {
	    	    	  for (int x = px - 1; x <= px + i; x++) {
	    	    		  int dx = Math.abs(px - x);
	    	    		  for (int y = py - i; y <= py + i; y++) {
	    	    			  int dy = Math.abs(py - y);
	    	    			  for (int z = pz - i; z <= pz + i; z++) {
	    	    				  dz = Math.abs(pz - z);
	    	    				  if (dx == i || dy == i || dz == i) {
	    	    					  String blockref = String.valueOf(world.getName()) + "-" + x + "-" + y + "-" + z;
	    	    					  String block = (String)this.blockmap.get(blockref);
	    	    					  if (block == null) {
	    	    						  Block thisBlock = world.getBlockAt(x, y, z);
	    	    						  block = String.valueOf(thisBlock.getTypeId());
	    	    		                  this.blockmap.put(blockref, block);
	    	    					  }
	    	    					  blockrads = (String)this.map.get(block);
	    	    					  shelterrads = (String)this.shelter.get(block);
	    	    					  
	    	    					  if (Integer.parseInt(block) == 2 && naturalRads >= 25.0D && convertCount < naturalRads / 20.0D && processFlag) {
	    	    		                  convertCount++;
	    	    		                  this.blockmap.put(blockref, null);
	    	    					  }
	    	    					  
	    	    					  if (blockrads != null) {
	    	    						  double blockdist = Math.sqrt(Math.pow((px - x), 2.0D) + Math.pow((py - y), 2.0D) + Math.pow((pz - z), 2.0D));
	    	    		                  double distPerc = blockdist / range;
	    	    		                  rads += Double.parseDouble(blockrads) / distPerc;
	    	    		                  naturalRads += Double.parseDouble(blockrads) * 8.0D;
	    	    		                  if (Double.parseDouble(blockrads) >= 10.0D && processFlag) {
	    	    		                	  tickloc.setX(x);
	    	    		                      tickloc.setY(y);
	    	    		                      tickloc.setZ(z);
	    	    		                      world.playEffect(tickloc, Effect.CLICK1, 0);
	    	    		                  }
	    	    					  }
	    	    					  else if (shelterrads != null) {
	    	    						  double blockdist = Math.sqrt(Math.pow((px - x), 2.0D) + Math.pow((py - y), 2.0D) + Math.pow((pz - z), 2.0D));
	    	    		                  double distPerc = blockdist / range;
	    	    		                  rads -= Double.parseDouble(shelterrads) / distPerc;
	    	    		                  naturalRads -= Double.parseDouble(shelterrads) * 8.0D;
	    	    					  }
	    	    				  }
	    	    			  }
	    	    		  }
	    	    	  }
	    	      }
	    	      if (rads < 0.0D) rads = 0.0D;
	    	      if (naturalRads < 0.0D) naturalRads = 0.0D; 
	    	      ItemStack helm = player.getInventory().getHelmet();
	    	      ItemStack chest = player.getInventory().getChestplate();
	    	      ItemStack pants = player.getInventory().getLeggings();
	    	      ItemStack boots = player.getInventory().getBoots();
	    	      if (helm != null && chest != null && pants != null && boots != null && 
	    	    	        helm.getType() == Material.GOLD_HELMET && chest.getType() == Material.GOLD_CHESTPLATE && pants.getType() == Material.GOLD_LEGGINGS && boots.getType() == Material.GOLD_BOOTS) {
	    	    	  rads *= 0.05D;
	    	      }
	    	      return rads; 	      
	    	}
	    	return 0.0D;
	    	
	    }
	    	
	}