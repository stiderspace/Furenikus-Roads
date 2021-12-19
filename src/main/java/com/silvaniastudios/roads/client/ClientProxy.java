package com.silvaniastudios.roads.client;

import com.silvaniastudios.roads.CommonProxy;
import com.silvaniastudios.roads.FurenikusRoads;
import com.silvaniastudios.roads.RoadsConfig;
import com.silvaniastudios.roads.blocks.FRBlocks;
import com.silvaniastudios.roads.blocks.tileentities.crusher.CrusherEntity;
import com.silvaniastudios.roads.blocks.tileentities.distiller.TarDistillerEntity;
import com.silvaniastudios.roads.blocks.tileentities.paintfiller.PaintFillerEntity;
import com.silvaniastudios.roads.blocks.tileentities.paintfiller.hopper.PaintFillerHopperEntity;
import com.silvaniastudios.roads.blocks.tileentities.paintoven.PaintOvenEntity;
import com.silvaniastudios.roads.blocks.tileentities.roadfactory.RoadFactoryEntity;
import com.silvaniastudios.roads.blocks.tileentities.tarmaccutter.TarmacCutterEntity;
import com.silvaniastudios.roads.client.gui.GuiPaintGun;
import com.silvaniastudios.roads.client.model.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {

	@Override
	public void registerItemRenderer(Item item, int meta, String id) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(FurenikusRoads.MODID + ":" + id, "inventory"));
	}

	@Override
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(ModelBakeHandler.instance);
	}

	@Override
	public void init() {
		super.init();
		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new ModBlockColours(), FRBlocks.road_block_grass);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new ModItemColours(), FRBlocks.road_block_grass);
		//Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new ModBlockColours(), FRItem)

		if (!RoadsConfig.general.performanceMode) {
			ClientRegistry.bindTileEntitySpecialRenderer(PaintFillerEntity.class, new PaintFillerRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(TarDistillerEntity.class, new TarDistillerRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(RoadFactoryEntity.class, new RoadFactoryRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(CrusherEntity.class, new CrusherRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(TarmacCutterEntity.class, new TarmacCutterRenderer());

			ClientRegistry.bindTileEntitySpecialRenderer(PaintFillerHopperEntity.class, new PaintFillerHopperRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(PaintOvenEntity.class, new PaintOvenRenderer());
		}
	}


	@Override
	public void openGui(int guiId) {
		if (guiId == 0) { Minecraft.getMinecraft().displayGuiScreen(new GuiPaintGun()); }
	}
}
