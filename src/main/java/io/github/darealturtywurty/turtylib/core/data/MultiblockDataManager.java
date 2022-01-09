package io.github.darealturtywurty.turtylib.core.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;

public class MultiblockDataManager extends SimpleJsonDataManager<MultiblockData> {
    public MultiblockDataManager() {
        super("multiblocks", MultiblockData.class);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected MultiblockData getJsonAsData(ResourceLocation location, JsonElement json) {
        final var layers = new HashMap<Integer, List<String>>();
        final var keys = new HashMap<String, List<BlockState>>();
        final JsonObject main = json.getAsJsonObject();

        final JsonObject keysObj = main.getAsJsonObject("keys");
        keysObj.entrySet().forEach(entry -> {
            final String key = entry.getKey();
            final JsonObject value = entry.getValue().getAsJsonObject();
            final var blockList = new ArrayList<BlockState>();
            final JsonArray blocks = value.getAsJsonArray("blocks");
            blocks.forEach(option -> {
                final JsonObject blockOption = option.getAsJsonObject();
                final String name = blockOption.get("name").getAsString();
                final Optional<Block> optionBlock = ForgeRegistries.BLOCKS.getEntries().stream()
                        .filter(etry -> etry.getKey().getRegistryName().toString().equalsIgnoreCase(name))
                        .map(Map.Entry::getValue).findFirst();
                final var atomicBlock = new AtomicReference<Block>();
                optionBlock.ifPresentOrElse(atomicBlock::set, () -> {
                    throw new JsonParseException(
                            "JSON at: '" + location + "' references block: '" + name + "' which does not exist!");
                });

                final Block block = atomicBlock.get();
                final BlockState state = block.defaultBlockState();
                final StateDefinition stateDef = block.getStateDefinition();
                final JsonObject stateObj = blockOption.getAsJsonObject("state");
                stateObj.entrySet().forEach(ery -> {
                    final String propertyStr = ery.getKey();
                    final JsonElement propertyVal = ery.getValue();
                    if (!propertyVal.isJsonPrimitive())
                        throw new JsonParseException(
                                "JSON at: '" + location + "' references a value for property name: '" + propertyStr
                                        + "' for block name: '" + name + "' for defined key '" + key
                                        + "', however is not a valid integer, boolean or string!");

                    final JsonPrimitive primitiveVal = propertyVal.getAsJsonPrimitive();
                    final Property property = stateDef.getProperty(propertyStr);
                    if (primitiveVal.isBoolean() && property instanceof final BooleanProperty boolProperty) {
                        state.setValue(boolProperty, primitiveVal.getAsBoolean());
                    } else if (primitiveVal.isNumber() && property instanceof final IntegerProperty intProperty) {
                        state.setValue(intProperty, primitiveVal.getAsInt());
                    } else if (primitiveVal.isString() && property instanceof final EnumProperty enumProperty) {
                        final Optional<? extends Enum> val = enumProperty.getValue(primitiveVal.getAsString());
                        if (!val.isPresent())
                            throw new JsonParseException(
                                    "JSON at: '" + location + "' references value: '" + primitiveVal.getAsString()
                                            + " for property name: '" + propertyStr + "' for block name: '" + name
                                            + "' for defined key: '" + key + "', but it does not exist!");
                        state.setValue(enumProperty, val.get());
                    }
                });

                blockList.add(state);
            });

            keys.put(key, blockList);
        });

        final JsonObject layersObj = main.getAsJsonObject("layers");
        layersObj.entrySet().forEach(entry -> {
            final String key = entry.getKey();
            final JsonArray layerArray = entry.getValue().getAsJsonArray();
            final var layerList = new ArrayList<String>();
            layerArray.forEach(layer -> {
                final String layerStr = layer.getAsString();
                for (final char c : layerStr.toCharArray()) {
                    if (!keys.containsKey(String.valueOf(c)) && !Character.isWhitespace(c))
                        throw new JsonParseException(
                                "JSON at: '" + location + "' is missing a defintion for key: '" + c + "'");
                }

                layerList.add(layer.getAsString());
            });
            layers.put(Integer.parseInt(key), layerList);
        });

        final String controllerKey = main.get("controller").getAsString();

        return new MultiblockData(layers, keys, controllerKey);
    }
}
