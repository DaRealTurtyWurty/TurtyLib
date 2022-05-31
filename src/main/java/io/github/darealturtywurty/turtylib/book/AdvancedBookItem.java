package io.github.darealturtywurty.turtylib.book;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;

public class AdvancedBookItem extends Item {
    private final SafeRunnable serverSafeGui;
    
    public AdvancedBookItem(Properties properties, SafeRunnable serverSafeGui) {
        super(properties);
        this.serverSafeGui = serverSafeGui;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide && !player.isCrouching()) {
            DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> this.serverSafeGui);
        }
        
        return super.use(level, player, hand);
    }
}
