package com.silvaniastudios.roads.blocks.decorative;

import javax.annotation.Nullable;

import com.silvaniastudios.roads.FurenikusRoads;
import com.silvaniastudios.roads.blocks.BlockBase;
import com.silvaniastudios.roads.blocks.NonPaintRoadTopBlock;
import com.silvaniastudios.roads.blocks.enums.IConnectable;
import com.silvaniastudios.roads.blocks.paint.PaintBlockBase;
import com.silvaniastudios.roads.items.FRItems;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BarsBarrierBlock extends BlockBase implements IConnectable {
	
	public static final PropertyBool NORTH = PropertyBool.create("north");
	public static final PropertyBool EAST = PropertyBool.create("east");
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool WEST = PropertyBool.create("west");
	public static final PropertyBool POSTS = PropertyBool.create("post");
	
	float height;
	
	public BarsBarrierBlock(String name, float height) {
		super(name, Material.IRON);
		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(NORTH, true)
				.withProperty(EAST, false)
				.withProperty(SOUTH, true)
				.withProperty(WEST, false)
				.withProperty(POSTS, false));
		this.setCreativeTab(FurenikusRoads.tab_road_parts);
		this.setHardness(1.5F);
		this.setHarvestLevel("pickaxe", 1);
		this.height = height;
	}

	public BarsBarrierBlock(String name) {
		this(name, 1.0F);
	}

	@SuppressWarnings("deprecation")
	public boolean canConnectTo(IBlockAccess worldIn, BlockPos pos, boolean checkingLevel) {
		IBlockState state = worldIn.getBlockState(pos);
		Block block = state.getBlock();
		if (checkingLevel) {
			if (block instanceof IConnectable || block.isOpaqueCube(state)) {
				return true;
			}
		} 
		
		return block instanceof IConnectable;
	}

	private boolean canBarrierConnectTo(IBlockAccess world, BlockPos pos, EnumFacing facing) {
		BlockPos offset = pos.offset(facing);
		return canConnectTo(world, offset, true) || canConnectTo(world, offset.offset(EnumFacing.DOWN), false) || canConnectTo(world, offset.offset(EnumFacing.UP), false);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (playerIn.getHeldItem(hand).getItem() == FRItems.wrench) {
			if (getMetaFromState(state) == 1) {
				worldIn.setBlockState(pos, state.withProperty(POSTS, false));
			} else {
				worldIn.setBlockState(pos, state.withProperty(POSTS, true));
			}
			return true;
		}
        return false;
    }
	
	public int getMetaFromState(IBlockState state) {
		if (state.getValue(POSTS).equals(true)) {
			return 1;
		}
		return 0;
	}
	
	public IBlockState getStateFromMeta(int meta) {
		if (meta == 1) {
			return this.getDefaultState().withProperty(POSTS, true);
		}
		return this.getDefaultState();
	}
	
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {NORTH, EAST, WEST, SOUTH, POSTS});
	}

	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return state.withProperty(NORTH, canBarrierConnectTo(worldIn, pos, EnumFacing.NORTH))
			.withProperty(EAST,  canBarrierConnectTo(worldIn, pos, EnumFacing.EAST))
			.withProperty(SOUTH, canBarrierConnectTo(worldIn, pos, EnumFacing.SOUTH))
			.withProperty(WEST,  canBarrierConnectTo(worldIn, pos, EnumFacing.WEST))
			.withProperty(POSTS, getMetaFromState(state) == 1);
	}

    @Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
    	return getBox(state, world, pos);
    }

    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return getBox(state, world, pos);
    }
    
    private AxisAlignedBB getBox(IBlockState state, IBlockAccess world, BlockPos pos) {
    	boolean north = canBarrierConnectTo(world, pos, EnumFacing.NORTH);
    	boolean east  = canBarrierConnectTo(world, pos, EnumFacing.EAST);
    	boolean south = canBarrierConnectTo(world, pos, EnumFacing.SOUTH);
    	boolean west  = canBarrierConnectTo(world, pos, EnumFacing.WEST);
    	
    	double xLow = 0.0;
    	double zLow = 0.0;
    	double xHigh = 1.0;
    	double zHigh = 1.0;
    	
    	if (north) { zLow  = 0.0; } else { zLow  = (1.0/16.0)*5.375; }
    	if (east)  { xHigh = 1.0; } else { xHigh = 1.0-((1.0/16.0)*5.375); }
    	if (south) { zHigh = 1.0; } else { zHigh = 1.0-((1.0/16.0)*5.375); }
    	if (west)  { xLow  = 0.0; } else { xLow  = (1.0/16.0)*5.375; }
    	return new AxisAlignedBB(xLow, -1+getBlockBelowHeight(world, pos)-(1-height), zLow, xHigh, -1+getBlockBelowHeight(world, pos)+1.25-(1-height), zHigh);
    }
    
    @Override
    public Block.EnumOffsetType getOffsetType()
    {
        return Block.EnumOffsetType.XYZ;
    }
    
    @Override
    public Vec3d getOffset(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        double offset = 1.0 - getBlockBelowHeight(worldIn, pos);
        return new Vec3d(0, -offset, 0);
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
    
    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }
}
