package net.derecsdoublejump.procedures;

import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.world.WorldEvent;

import net.minecraft.world.IWorld;

import net.derecsdoublejump.DerecsDoubleJumpModVariables;
import net.derecsdoublejump.DerecsDoubleJumpMod;

import java.util.Map;
import java.util.HashMap;

import java.io.IOException;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

public class ReadFromConfigProcedure {
	@Mod.EventBusSubscriber
	private static class GlobalTrigger {
		@SubscribeEvent
		public static void onWorldLoad(WorldEvent.Load event) {
			IWorld world = event.getWorld();
			Map<String, Object> dependencies = new HashMap<>();
			dependencies.put("world", world);
			dependencies.put("event", event);
			executeProcedure(dependencies);
		}
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("world") == null) {
			if (!dependencies.containsKey("world"))
				DerecsDoubleJumpMod.LOGGER.warn("Failed to load dependency world for procedure ReadFromConfig!");
			return;
		}
		IWorld world = (IWorld) dependencies.get("world");
		File config = new File("");
		com.google.gson.JsonObject mainJSON = new com.google.gson.JsonObject();
		config = (File) new File((FMLPaths.GAMEDIR.get().toString() + "/config/doublejump"), File.separator + "config.json");
		if (!config.exists()) {
			try {
				config.getParentFile().mkdirs();
				config.createNewFile();
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
		{
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(config));
				StringBuilder jsonstringbuilder = new StringBuilder();
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					jsonstringbuilder.append(line);
				}
				bufferedReader.close();
				mainJSON = new Gson().fromJson(jsonstringbuilder.toString(), com.google.gson.JsonObject.class);
				if (!(mainJSON.get("jumpCost") != null) || !(mainJSON.get("doSound") != null) || !(mainJSON.get("doParticles") != null)) {
					mainJSON.addProperty("jumpCost", 1);
					mainJSON.addProperty("doParticles", (true));
					mainJSON.addProperty("doSound", (true));
					{
						Gson mainGSONBuilderVariable = new GsonBuilder().setPrettyPrinting().create();
						try {
							FileWriter fileWriter = new FileWriter(config);
							fileWriter.write(mainGSONBuilderVariable.toJson(mainJSON));
							fileWriter.close();
						} catch (IOException exception) {
							exception.printStackTrace();
						}
					}
				}
				DerecsDoubleJumpModVariables.MapVariables.get(world).jumpCost = mainJSON.get("jumpCost").getAsDouble();
				DerecsDoubleJumpModVariables.MapVariables.get(world).syncData(world);
				DerecsDoubleJumpModVariables.MapVariables.get(world).doParticles = mainJSON.get("doParticles").getAsBoolean();
				DerecsDoubleJumpModVariables.MapVariables.get(world).syncData(world);
				DerecsDoubleJumpModVariables.MapVariables.get(world).doSound = mainJSON.get("doSound").getAsBoolean();
				DerecsDoubleJumpModVariables.MapVariables.get(world).syncData(world);
				DerecsDoubleJumpMod.LOGGER.debug(("jumpCost: " + DerecsDoubleJumpModVariables.MapVariables.get(world).jumpCost));
				DerecsDoubleJumpMod.LOGGER.debug(("doParticles: " + DerecsDoubleJumpModVariables.MapVariables.get(world).doParticles));
				DerecsDoubleJumpMod.LOGGER.debug(("doSound: " + DerecsDoubleJumpModVariables.MapVariables.get(world).doSound));

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
