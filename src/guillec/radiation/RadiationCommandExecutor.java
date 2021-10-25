package guillec.radiation;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RadiationCommandExecutor implements CommandExecutor {
	private Radiation plugin;
	public RadiationCommandExecutor(Radiation plugin) {this.plugin = plugin;}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = null;
	    if (sender instanceof Player) {
	    	player = (Player)sender;
	    }
	    if (cmd.getName().equalsIgnoreCase("radiation")) {
	    	String version = this.plugin.getDescription().getVersion();
	        sender.sendMessage("[" + ChatColor.RED + "Radiation" + ChatColor.WHITE + "] Version " + version + " active.");
	        return true;
	    }
	    if (cmd.getName().equalsIgnoreCase("rads")) {
	    	sender.sendMessage("[" + ChatColor.RED + "Irradiado" + ChatColor.WHITE + "]");
	        return true;
	    }
	    if (cmd.getName().equalsIgnoreCase("geiger") && player.hasPermission("irradiated.geiger")) {
	    	World w = player.getWorld();
	        if (ArrayUtils.contains(this.plugin.IrradiatedWorlds, w.getName())) {
	        	this.plugin.doGeiger(player); 	    		
	    	}
	        else {
	        	sender.sendMessage("Irradiated has not been enabled for world " + w.getName());
	        }
	        return true;
	    }
	    return false;   
	}
}
