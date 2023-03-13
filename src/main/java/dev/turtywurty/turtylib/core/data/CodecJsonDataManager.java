package dev.turtywurty.turtylib.core.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.turtywurty.turtylib.TurtyLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodecJsonDataManager<T> extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();

    private final Codec<T> codec;
    private final String folder;
    protected Map<ResourceLocation, T> data = new HashMap<>();

    public CodecJsonDataManager(String folder, Codec<T> codec) {
        super(GSON, folder);
        this.folder = folder;
        this.codec = codec;
    }

    public @Nullable T getData(ResourceLocation id) {
        return this.data.get(id);
    }

    public Map<ResourceLocation, T> getDataMap() {
        return this.data;
    }

    public List<T> getDataList() {
        return List.copyOf(this.data.values());
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> inputs, ResourceManager resourceManager, ProfilerFiller profiler) {
        this.data = mapValues(inputs);
    }

    private Map<ResourceLocation, T> mapValues(Map<ResourceLocation, JsonElement> inputs) {
        Map<ResourceLocation, T> map = new HashMap<>();
        inputs.forEach((id, json) -> {
            this.codec.decode(JsonOps.INSTANCE, json).get().ifLeft(result -> map.put(id, result.getFirst())).ifRight(
                    partial -> TurtyLib.LOGGER.error(
                            "Failed to decode data for " + id + " in " + this.folder + " folder!"));
        });

        return map;
    }
}
