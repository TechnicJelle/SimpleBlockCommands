package net.mctechnic.rightclickcommand;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public final class RightClickCommand extends JavaPlugin implements Listener {

	private HashMap<Location, String[]> blockCommands;

	@Override
	public void onEnable() {
		// Plugin startup logic
		getServer().getPluginManager().registerEvents(this, this);

		loadBlockCommands();

		PluginCommand pluginCommand = Bukkit.getPluginCommand("rcc");
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

		//csv
		File csvFile = new File(getDataFolder() + "/blockCommands.csv");

		//generate a new csv file if not present
		if(!csvFile.exists()) {
			getLogger().info("CSV doesn't exist! Generating a new one...");

			List<String[]> csvData = new ArrayList<>();
			csvData.add(new String[]{"world", "x", "y", "z", "command"});
			csvData.add(new String[]{"world", "0", "64", "0", "say \"Hello world!\""});

			try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
				writer.writeAll(csvData);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		//load it
		getLogger().info("Loading CSV...");
		blockCommands = new HashMap<>();
		int line = 1;
		try {
			FileReader filereader = new FileReader(csvFile);
			CSVReader csvReader = new CSVReaderBuilder(filereader).build();
			String[] nextRecord;

			//read csv line by line
			while ((nextRecord = csvReader.readNext()) != null) {
				line++;
				if (nextRecord.length == 1 && nextRecord[0].isEmpty()) //Skip empty lines
					continue;
				if(nextRecord[2].equalsIgnoreCase("y"))
					continue;

				World world = null;
				Integer x = null;
				Integer y = null;
				Integer z = null;
				ArrayList<String> commands = new ArrayList<>();
				for (int i = 0; i < nextRecord.length; i++) {
					String piece = nextRecord[i];

					switch (i) {
						case 0 -> world = Bukkit.getWorld(piece);
						case 1 -> x = Integer.parseInt(piece);
						case 2 -> y = Integer.parseInt(piece);
						case 3 -> z = Integer.parseInt(piece);
						default -> commands.add(piece);
					}
				}
				if(world == null) throw new Exception("World name couldn't be found!");
				if(x == null || y == null || z == null) throw new Exception("Coordinates are invalid on line " + line);
				if(blockCommands.put(new Location(world, x, y, z), commands.toArray(new String[0])) != null) {
					getLogger().warning("A command block was overwritten on line " + line);
					getLogger().warning("\tTo use multiple commands per block, put them all behind each other, seperated by commas (example available on GitHub)");
				}

			}
			getLogger().info("Loaded CSV!");
//			logHashMap(blockCommands);
			return true;
		} catch (Exception e) {
			getLogger().warning("Couldn't load the CSV file! There might be a problem on line " + line);
			e.printStackTrace();
			return false;
		}
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
			for (String command : blockCommands.get(location)) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
			}
		}
	}
	
	@SuppressWarnings("unused")
	public void logHashMap(HashMap<Location, String[]> hashMap) {
		for (HashMap.Entry<Location, String[]> entry : hashMap.entrySet()) {
			Location key = entry.getKey();
			String[] value = entry.getValue();
			getLogger().info(key + " " + Arrays.toString(value));
		}
	}
}

