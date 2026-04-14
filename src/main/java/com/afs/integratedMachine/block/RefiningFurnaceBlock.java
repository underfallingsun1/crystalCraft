package com.afs.integratedMachine.block;

import com.afs.integratedMachine.block.entity.IMBlockEntityTypes;
import com.afs.integratedMachine.block.entity.RefiningFurnaceBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.awt.event.ItemEvent;

public class RefiningFurnaceBlock extends Block implements EntityBlock {
    public RefiningFurnaceBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.EAST).setValue(LIT, false));
    }

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        Direction face = context.getClickedFace();
        Direction resultFacing = Direction.EAST;
        if(player != null){
            double dx = player.getX() - pos.getX(), dz = player.getZ() - pos.getZ();
            switch (face.getAxis()){
                case Direction.Axis.X -> dx *= 1.5;
                case Direction.Axis.Z -> dz *= 1.5;
            }
            if(Math.abs(dx) > Math.abs(dz)) resultFacing = dx > 0? Direction.EAST: Direction.WEST;
            else resultFacing = dz > 0? Direction.SOUTH: Direction.NORTH;
        }
        return defaultBlockState().setValue(FACING, resultFacing);
    }

    public static final MapCodec<RefiningFurnaceBlock> CODEC = simpleCodec(RefiningFurnaceBlock::new);

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RefiningFurnaceBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return (lvl, pos, sta, be) -> {
            if(be instanceof RefiningFurnaceBlockEntity rfBe && !lvl.isClientSide){
                rfBe.serverTick(lvl, pos, sta);
            }
        };
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if(player instanceof ServerPlayer sp){
            RefiningFurnaceBlockEntity be = level.getBlockEntity(pos, IMBlockEntityTypes.REFINING_FURNACE_BE.get()).get();
            sp.openMenu(be.new MenuGetter());
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if(!level.isClientSide && !newState.is(state.getBlock())){
            BlockEntity be = level.getBlockEntity(pos);
            if(be instanceof RefiningFurnaceBlockEntity rbe){
                IItemHandler items = rbe.getItems();
                for(int i = 0;i < items.getSlots();i++){
                    ItemStack stack = items.getStackInSlot(i);
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                }
            }
            level.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
