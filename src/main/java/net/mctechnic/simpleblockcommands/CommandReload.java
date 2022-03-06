package net.mctechnic.simpleblockcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public record CommandReload(SimpleBlockCommands plugin) implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (args.length != 1) return false;
		if (args[0].equalsIgnoreCase("reload")) {
			if(plugin.loadBlockCommands()) {
				String message = "Right Click Command reloaded successfully!";
				plugin.getLogger().info(message);
				if (sender instanceof Player)
					sender.sendMessage(message);
			} else {
				String message = "Right Click Command failed to reload.";
				plugin.getLogger().warning(message);
				if (sender instanceof Player)
					sender.sendMessage(message + " Check the server console!");
			}

			return true;
		}
		return false;
	}

	@Override
	public @NotNull
	List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		List<String> completions = new ArrayList<>();
		if (args.length == 1) {
			completions.add("reload");
		}
		if (args.length >= 1 && args[args.length - 1].length() > 0) {
			String arg = args[args.length - 1];
			completions.removeIf(suggestion -> !suggestion.startsWith(arg));
		}
		return completions;
	}
}
