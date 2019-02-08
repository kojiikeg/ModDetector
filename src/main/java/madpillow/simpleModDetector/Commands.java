package madpillow.simpleModDetector;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class Commands implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("modlist")) {
			Player target;
			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED + "usage: /modlist <MCID>");
				return true;
			} else if ((target = Bukkit.getPlayerExact(args[0])) == null) {
				sender.sendMessage(ChatColor.RED + "can't search " + args[0]);
				return true;
			}

			sender.sendMessage(ChatColor.RED + target.getName() + " using MODs:");
			if (target.hasMetadata("modlist")) {
				if (!target.getMetadata("modlist").isEmpty()
						&& target.getMetadata("modlist").size() != 0
						&& target.getMetadata("modlist").get(0) != null) {
					Map<String, String> map = (HashMap) target.getMetadata("modlist").get(0).value();
					for (String key : map.keySet()) {
						sender.sendMessage(ChatColor.GREEN + " - " + key + ChatColor.DARK_GREEN + " ("
								+ map.get(key) + " )");
					}
				}
			}

			return true;
		}
		return false;
	}
}
