package io.github.darealturtywurty.turtylib.core.data;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public class SimpleJsonDataManager<T> extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    private final Class<T> dataClass;
    protected Map<ResourceLocation, T> data = new HashMap<>();

    public SimpleJsonDataManager(String folder, Class<T> dataClass) {
        super(GSON, folder);
        this.dataClass = dataClass;
    }

    public Map<ResourceLocation, T> mapValues(Map<ResourceLocation, JsonElement> inputs,
            BiFunction<ResourceLocation, JsonElement, T> mapper) {
        final Map<ResourceLocation, T> newMap = new HashMap<>();

        inputs.forEach((key, input) -> newMap.put(key, mapper.apply(key, input)));

        return newMap;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager manager, ProfilerFiller profiler) {
        this.data = mapValues(jsons, this::getJsonAsData);
    }

    protected T getJsonAsData(ResourceLocation loc, JsonElement json) {
        return GSON.fromJson(json, this.dataClass);
    }
}
