package net.mctechnic.rightclickcommand;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public final class RightClickCommand extends JavaPlugin implements Listener {

	enum RunBy {
		player, server
	}

	enum Hand {
		either, left, right
	}

	private record Command(String command, Hand hand, RunBy runBy) {}


	private HashMap<Location, Command[]> blockCommands;

	@Override
	public void onEnable() {
		// Plugin startup logic
		getServer().getPluginManager().registerEvents(this, this);

		if(loadBlockCommands())
			getLogger().info("Right Click Command blockCommands.conf loaded successfully!");
		else
			getLogger().warning("Right Click Command blockCommands.conf failed to load!");

		PluginCommand pluginCommand = Bukkit.getPluginCommand("sbc");
		CommandReload executor = new CommandReload(this);
		if (pluginCommand != null) {
			pluginCommand.setExecutor(executor);
			pluginCommand.setTabCompleter(executor);
		} else {
			getLogger().warning("pluginCommand is null. This is not good");
		}

		getLogger().info("Right Click Command plugin enabled!");
	}

	public boolean loadBlockCommands() {
		//config dir
		if(!getDataFolder().exists()) {
			boolean madeDir = getDataFolder().mkdir();
			if(madeDir) {
				getLogger().info("Config directory made");
			}
		}

		Path saveFile = Path.of(getDataFolder() + "/blockCommands.conf");

		//if saveFile doesn't exist, generate a new one
		if(!Files.exists(saveFile)) {
			HoconConfigurationLoader loader = HoconConfigurationLoader.builder() //create a loader which can load and save your config-node-tree to a file
					.path(saveFile) //path or file pointing at your config file
					.build();

			ConfigurationNode root = loader.createNode(); // your root configuration node, everything goes in here

			ConfigurationNode blocksNode = root.node("blocks"); // this will be "foo: " in your config .. everything you put in here will be placed inside that "foo: " value

			try {
				ConfigurationNode defaultWorld = blocksNode.appendListNode();

				defaultWorld.node("world").set("world");

				ConfigurationNode pos = defaultWorld.node("pos");
				pos.node("x").set(0);
				pos.node("y").set(64);
				pos.node("z").set(0);

				ConfigurationNode commands = defaultWorld.node("commands");

				ConfigurationNode command = commands.appendListNode();
				command.node("command").set("say Hello World!");
				command.node("hand").set("either");
				command.node("run-by").set("player");
			} catch (SerializationException e) {
				e.printStackTrace();
				return false;
			}

			try {
				loader.save(root); //saves the node tree to the file
			} catch (ConfigurateException e) {
				e.printStackTrace();
				return false;
			}
		}

		//load the saveFile
		blockCommands = new HashMap<>();
		final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
				.path(saveFile) // Set where we will load and save to
				.build();

		ConfigurationNode root;
		try {
			root = loader.load();
			ConfigurationNode blocksNode = root.node("blocks");
			if(blocksNode.virtual()) throw new Exception("blocks property is required!");
			List<? extends ConfigurationNode> children = blocksNode.childrenList();
			for (ConfigurationNode child : children) {
				//world
				ConfigurationNode worldNode = child.node("world");
				if(worldNode.virtual()) throw new Exception("world property is required!");
				String worldName = worldNode.getString();
				if(worldName == null) throw new Exception("world name couldn't be found!");
				World world = Bukkit.getWorld(worldName);
				if(world == null) throw new Exception("world name \"" + worldName + "\" couldn't be found!");

				//pos
				ConfigurationNode pos = child.node("pos");
				if(pos.virtual()) throw new Exception("pos property is required!");
				String xText = pos.node("x").getString();
				if(xText == null) throw new Exception("x coordinate property is required!");
				int x = Integer.parseInt(xText);
				String yText = pos.node("y").getString();
				if(yText == null) throw new Exception("y coordinate property is required!");
				int y = Integer.parseInt(yText);
				String zText = pos.node("z").getString();
				if(zText == null) throw new Exception("z coordinate property is required!");
				int z = Integer.parseInt(zText);

				Location location = new Location(world, x, y, z);

//				getLogger().info("loc:" + location);

				//commands
				ConfigurationNode commandsNode = child.node("commands");
				if(commandsNode.virtual()) throw new Exception("commands property is required!");
				List<? extends ConfigurationNode> commandNodes = commandsNode.childrenList();
				ArrayList<Command> commands = new ArrayList<>();

				for (ConfigurationNode commandNode : commandNodes) {
					String commandText = commandNode.node("command").getString();
					if(commandText == null) throw new Exception("command property is required!");
					String handText = commandNode.node("hand").getString("either");
					Hand hand;
					switch (handText) {
						case "either" -> hand = Hand.either;
						case "left" -> hand = Hand.left;
						case "right" -> hand = Hand.right;
						default -> throw new Exception("hand setting \"" + handText + "\" invalid!");
					}
					String runByText = commandNode.node("run-by").getString("player");
					RunBy runBy;
					switch (runByText) {
						case "player" -> runBy = RunBy.player;
						case "server" -> runBy = RunBy.server;
						default -> throw new Exception("run-by setting \"" + runByText + "\"invalid!");
					}
					commands.add(new Command(commandText, hand, runBy));
//					getLogger().info("cmd: " + commandText + ", " + handText + ", " + runByText);
				}

				if(blockCommands.put(location, commands.toArray(new Command[0])) != null) {
					getLogger().warning("A command block was overwritten!"); //TODO: Add explanation on how to do this properly
				}
			}
		} catch (Exception e) {
			getLogger().warning("An error occurred while loading the block commands: " + e.getMessage());
			if (e.getCause() != null) {
				e.getCause().printStackTrace();
			}
			return false;
		}

		logBlockCommands();
		return true;
	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic
		getLogger().info("Right Click Command plugin disabled!");
	}

	@EventHandler
	public void onInteract(@NotNull PlayerInteractEvent e) {
		if (e.getAction() == Action.PHYSICAL || e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_AIR
				|| e.getHand() == EquipmentSlot.OFF_HAND || e.getClickedBlock() == null) return;
		Location location = e.getClickedBlock().getLocation();
//		getLogger().info(e.getAction() + ", " + location);
		if (blockCommands.containsKey(location)) {
			e.setCancelled(true);
			for (Command command : blockCommands.get(location)) {
				CommandSender commandSender = null;
				switch (command.runBy) {
					case player -> commandSender = e.getPlayer();
					case server -> commandSender = Bukkit.getConsoleSender();
					default -> getLogger().warning("command.runBy was invalid. This shouldn't happen!");
				}
				//TODO: Implement command.hand
				if(commandSender == null) {
					getLogger().warning("commandSender was null. This shouldn't happen!");
					return;
				}
				Bukkit.dispatchCommand(commandSender, command.command);
			}
		}
	}

	public void logBlockCommands() {
		for (HashMap.Entry<Location, Command[]> entry : blockCommands.entrySet()) {
			Location key = entry.getKey();
			Command[] value = entry.getValue();
			StringJoiner cmdText = new StringJoiner(", ");
			for (Command command : value) {
				cmdText.add(command.command);
				cmdText.add(command.hand.toString());
				cmdText.add(command.runBy.toString());
				cmdText.add(" ; ");
			}
			getLogger().info(key + " : " + cmdText);
		}
	}
}

