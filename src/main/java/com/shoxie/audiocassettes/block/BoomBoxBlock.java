package com.shoxie.audiocassettes.block;

import javax.annotation.Nullable;

import com.shoxie.audiocassettes.entity.BoomBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class BoomBoxBlock extends Block implements EntityBlock {
	public static String name = "boombox";
    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);
    public BoomBoxBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new BoomBoxEntity(p_153215_, p_153216_);
    }

    @Override
    public @NotNull InteractionResult use(BlockState state, Level Level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        BlockEntity blockEntity = Level.getBlockEntity(pos);
            if (blockEntity instanceof BoomBoxEntity) {
                if (player instanceof ServerPlayer sp) {
                    NetworkHooks.openScreen(sp, (MenuProvider) blockEntity, pos);
                    return InteractionResult.SUCCESS;
                }
            }

        return InteractionResult.PASS;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (!level.isClientSide()) {
            BlockEntity _entity = level.getBlockEntity(pos);
            if (_entity instanceof BoomBoxEntity entity) {
                entity.stopMusic();
                var cassette = entity.getCassette();
                level.addFreshEntity(
                        new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                                cassette
                        )
                );
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
