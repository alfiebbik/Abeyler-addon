package com.yourname.addon.modules;

import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;

public class AbayleeChunkFinder extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> susChunks = sgGeneral.add(new BoolSetting.Builder()
        .name("sus-chunk-detect")
        .description("Highlights chunks with cobbled deepslate or rotated deepslate.")
        .defaultValue(true)
        .build()
    );

    public AbayleeChunkFinder() {
        super(Categories.World, "abaylee-chunk-finder-v1", "Detects loaded chunks and sus chunks with deepslate.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.world == null || mc.player == null) return;

        int playerChunkX = mc.player.getChunkPos().x;
        int playerChunkZ = mc.player.getChunkPos().z;
        int radius = 8;

        for (int x = playerChunkX - radius; x <= playerChunkX + radius; x++) {
            for (int z = playerChunkZ - radius; z <= playerChunkZ + radius; z++) {
                WorldChunk chunk = mc.world.getChunk(x, z);
                if (chunk == null) continue;

                if (susChunks.get()) {
                    checkSusChunk(chunk, x, z);
                }
            }
        }
    }

    private void checkSusChunk(WorldChunk chunk, int cx, int cz) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = mc.world.getBottomY(); y < 0; y++) {
                    net.minecraft.block.BlockState state = chunk.getBlockState(
                        new net.minecraft.util.math.BlockPos(cx * 16 + x, y, cz * 16 + z)
                    );
                    String blockName = state.getBlock().toString();
                    if (blockName.contains("cobbled_deepslate") || blockName.contains("deepslate") && state.contains(net.minecraft.state.property.Properties.AXIS)) {
                        mc.player.sendMessage(
                            net.minecraft.text.Text.literal("§c[AbayleeChunkFinder] §eSus chunk at: §f" + cx + ", " + cz + " §7(y=" + y + ")"),
                            false
                        );
                        return;
                    }
                }
            }
        }
    }
}
