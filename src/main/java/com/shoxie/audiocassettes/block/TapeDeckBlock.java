package com.shoxie.audiocassettes.block;

import javax.annotation.Nullable;

import com.shoxie.audiocassettes.entity.TapeDeckEntity;
import com.shoxie.audiocassettes.init.Init;
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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;


public class TapeDeckBlock extends Block implements EntityBlock {
	public static String name = "tapedeck";
    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);
    public TapeDeckBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new TapeDeckEntity(p_153215_, p_153216_);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == Init.TAPEDECK_ENTITY.get() ? TapeDeckEntity::tick : null;
    }

    @Override
    public @NotNull InteractionResult use(BlockState state, Level Level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
            BlockEntity blockEntity = Level.getBlockEntity(pos);
            if (blockEntity instanceof TapeDeckEntity) {
                if (player instanceof ServerPlayer sp) {
                    NetworkHooks.openScreen(sp, (MenuProvider) blockEntity, pos);
                }
                return InteractionResult.SUCCESS;
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
            if (_entity instanceof TapeDeckEntity entity) {
                ItemStackHandler inv = entity.getInventory();
                level.addFreshEntity(
                        new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                                inv.getStackInSlot(0)
                        )

                );

                level.addFreshEntity(
                        new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                                inv.getStackInSlot(1)
                        )

                );
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
