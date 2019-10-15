package com.mcmoddev.mineralogy;



// DON'T FORGET TO UPDATE mcmod.info FILE!!!

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mcmoddev.mineralogy.init.MineralogyRegistry;
import com.mcmoddev.mineralogy.ioc.MinIoC;
import com.mcmoddev.mineralogy.lib.exceptions.TabNotFoundException;
import com.mcmoddev.mineralogy.lib.interfaces.IDynamicTabProvider;
import com.mcmoddev.mineralogy.worldgen.StoneReplacer;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(
		modid = Mineralogy.MODID,
		name = Mineralogy.NAME,
		version = Mineralogy.VERSION,
		//TODO pick a master or docs branch to upload an updates.json folder and keep it up to date, then link the raw file here.
		updateJSON = "",
		acceptedMinecraftVersions = "[1.12,)",
		certificateFingerprint = "@FINGERPRINT@")
public class Mineralogy {

	@Instance
	public static Mineralogy instance;

	/** ID of this Mod */
	public static final String MODID = "mineralogy";

	/** Display name of this Mod */
	static final String NAME = "Mineralogy";

	/**
	 * Version number, in Major.Minor.Patch format. The minor number is
	 * increased whenever a change is made that has the potential to break
	 * compatibility with other mods that depend on this one.
	 */
	static final String VERSION = "3.8.0";

	private static final Logger logger = LogManager.getFormatterLogger(Mineralogy.MODID);

	@Mod.EventHandler
	public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
		logger.warn("Invalid fingerprint detected!");
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new MineralogyEventBusSubscriber());
		MinecraftForge.EVENT_BUS.register(com.mcmoddev.mineralogy.init.Blocks.class);
		MinecraftForge.EVENT_BUS.register(com.mcmoddev.mineralogy.init.Items.class);
		MinecraftForge.EVENT_BUS.register(com.mcmoddev.mineralogy.init.Ores.class);
		MinecraftForge.EVENT_BUS.register(com.mcmoddev.mineralogy.init.Recipes.class);
		
		MineralogyConfig.preInit(event);

		com.mcmoddev.mineralogy.init.Blocks.init();
		com.mcmoddev.mineralogy.init.Items.init();
		com.mcmoddev.mineralogy.init.Ores.Init();
		com.mcmoddev.mineralogy.init.Recipes.Init();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		if (MineralogyConfig.smeltableGravel())
			GameRegistry.addSmelting(Blocks.GRAVEL, new ItemStack(Blocks.STONE), 0.1F);

		GameRegistry.registerWorldGenerator(new StoneReplacer(), 10); // register custom chunk generation

		// register renderers
		if (event.getSide().isClient()) {
			registerItemRenders();
		}
	}

	private void registerItemRenders() {
		for (String name : MineralogyRegistry.MineralogyItemRegistry.keySet()) {
			Item i = MineralogyRegistry.MineralogyItemRegistry.get(name);
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(i, 0,
					new ModelResourceLocation(Mineralogy.MODID + ":" + name, "inventory"));
		}
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		// addons to other mods

		// process black-lists and white-lists
		for (String id : MineralogyConfig.igneousWhitelist()) {
			Block b = Block.getBlockFromName(id);
			if (b == null)
				continue;
			MineralogyRegistry.igneousStones.add(b);
		}
		for (String id : MineralogyConfig.metamorphicWhitelist()) {
			Block b = Block.getBlockFromName(id);
			if (b == null)
				continue;
			MineralogyRegistry.metamorphicStones.add(b);
		}
		for (String id : MineralogyConfig.sedimentaryWhitelist()) {
			Block b = Block.getBlockFromName(id);
			if (b == null)
				continue;
			MineralogyRegistry.sedimentaryStones.add(b);
		}
		for (String id : MineralogyConfig.igneousBlacklist()) {
			Block b = Block.getBlockFromName(id);
			if (b == null)
				continue;
			MineralogyRegistry.igneousStones.remove(b);
		}
		for (String id : MineralogyConfig.metamorphicBlacklist()) {
			Block b = Block.getBlockFromName(id);
			if (b == null)
				continue;
			MineralogyRegistry.metamorphicStones.remove(b);
		}
		for (String id : MineralogyConfig.sedimentaryBlacklist()) {
			Block b = Block.getBlockFromName(id);
			if (b == null)
				continue;
			MineralogyRegistry.sedimentaryStones.remove(b);
		}
		
		MinIoC IoC = MinIoC.getInstance();
		ItemStack sulphurStack = new ItemStack(IoC.resolve(Item.class, Constants.SULFUR, Mineralogy.MODID));
		
		try {
			IDynamicTabProvider tabProvider = IoC.resolve(IDynamicTabProvider.class);
			
			tabProvider.setTabIcons();
			
			if (MineralogyConfig.groupCreativeTabItemsByType())
				tabProvider.setIcon("Item", sulphurStack);
			
		} catch (TabNotFoundException e) {
			e.printStackTrace();
		}
	}
}