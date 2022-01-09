package io.github.darealturtywurty.turtylib.core.data;

import java.util.List;
import java.util.Map;

import net.minecraft.world.level.block.state.BlockState;

public record MultiblockData(Map<Integer, List<String>> layers, Map<String, List<BlockState>> keys,
        String controllerKey) {
    
}
