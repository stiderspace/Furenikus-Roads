package com.silvaniastudios.roads.blocks;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.silvaniastudios.roads.FurenikusRoads;
import com.silvaniastudios.roads.blocks.decorative.CurbBlock;
import com.silvaniastudios.roads.blocks.paint.PaintBlockBase;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRoadSnow extends BlockSnow {
	
	protected String name;
	
	public BlockRoadSnow(String name) {
		super();
		this.name = name;
		setUnlocalizedName(FurenikusRoads.MODID + "." + name);
		setRegistryName(name);
	}
	
	public void registerItemModel(Item itemBlock) {
		FurenikusRoads.proxy.registerItemRenderer(itemBlock, 0, name);
	}
	
	public Item createItemBlock() {
		return new ItemBlock(this).setRegistryName(getRegistryName());
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel() {
		StateMapperBase b = new DefaultStateMapper();
		BlockStateContainer bsc = this.getBlockState();
		ImmutableList<IBlockState> values = bsc.getValidStates();
		
		for(IBlockState state : values) {
			ModelResourceLocation mrl = new ModelResourceLocation(state.getBlock().getRegistryName(), b.getPropertyString(state.getProperties()));
			
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(state.getBlock()), this.getMetaFromState(state), mrl);
		}
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
    public Vec3d getOffset(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        if (worldIn.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock() instanceof RoadBlock) {
        	IBlockState roadState = worldIn.getBlockState(pos.offset(EnumFacing.DOWN));
        	RoadBlock block = (RoadBlock) roadState.getBlock();
        	int meta = block.getMetaFromState(roadState);
        	return new Vec3d(0, -1 + 0.0625 + (meta*0.0625), 0);
        }
        return super.getOffset(state, worldIn, pos);
    }
	
	@Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    	return new AxisAlignedBB(0.0D, -1+getBlockBelowHeight(source, pos), 0.0D, 1.0D, -1+getBlockBelowHeight(source, pos)+0.0625, 1.0D);
    }

    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }
    
    @SuppressWarnings("deprecation")
	public double getBlockBelowHeight(IBlockAccess worldIn, BlockPos pos) {
    	IBlockState underState = worldIn.getBlockState(pos.offset(EnumFacing.DOWN));
        Block underBlock = underState.getBlock();
        double extraOffset = 0.0;
        
        if (underBlock instanceof PaintBlockBase || underBlock instanceof NonPaintRoadTopBlock || underBlock instanceof CurbBlock) {
        	extraOffset = 0.062;
        }
        
        return underBlock.getBoundingBox(underState, worldIn, pos.offset(EnumFacing.DOWN)).maxY - extraOffset;
    }

}
