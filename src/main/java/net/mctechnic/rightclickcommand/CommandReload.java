package net.mctechnic.rightclickcommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record CommandReload(RightClickCommand plugin) implements CommandExecutor, TabCompleter {

	// This method is called, when somebody uses our command
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (args.length != 1) return false;
		if (args[0].equalsIgnoreCase("reload")) {
			if(plugin.loadBlockCommands())
				sender.sendMessage("Right Click Command reloaded successfully!");
			else
				sender.sendMessage("Right Click Command failed to reload. Check the server console!");

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
