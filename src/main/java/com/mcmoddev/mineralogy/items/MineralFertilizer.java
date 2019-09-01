//package com.mcmoddev.mineralogy.items;
//
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.init.Items;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemDye;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.EnumActionResult;
//import net.minecraft.util.EnumFacing;
//import net.minecraft.util.EnumHand;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//
//public class MineralFertilizer extends Item {
//
//	public MineralFertilizer() {
//		super();
//	}
//
//	private final ItemStack phantomBonemeal = new ItemStack(Items.DYE, 1, 15);
//
//	@Override
//	public EnumActionResult onItemUse(EntityPlayer playerEntity, World world, BlockPos target, EnumHand hand,
//			EnumFacing face, float par8, float par9, float par10) {
//		boolean canUse = ItemDye.applyBonemeal(playerEntity.getHeldItemMainhand(), world, target, playerEntity, hand);
//		if (canUse) {
//			phantomBonemeal.setCount(27);
//			for (int dx = -2; dx <= 2; dx++) {
//				for (int dy = -2; dy <= 2; dy++) {
//					for (int dz = -1; dz <= 1; dz++) {
//						if ((dx | dy | dz) == 0)
//							continue;
//						BlockPos t = target.add(dx, dy, dz);
//						ItemDye.applyBonemeal(phantomBonemeal, world, t, playerEntity, hand);
//					}
//				}
//			}
//			return EnumActionResult.SUCCESS;
//		}
//		return EnumActionResult.PASS;
//	}
//}
