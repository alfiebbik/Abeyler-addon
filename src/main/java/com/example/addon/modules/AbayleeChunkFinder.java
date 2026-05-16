package com.example.addon.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import com.example.addon.AddonTemplate;

public class AbayleeChunkFinder extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> susChunks = sgGeneral.add(new BoolSetting.Builder()
        .name("sus-chunk-detect")
        .description("Alerts you to chunks with cobbled deepslate or rotated deepslate underground.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> chatAlert = sgGeneral.add(new BoolSetting.Builder()
        .name("chat-alert")
        .description("Sends a chat message when a sus chunk is found.")
        .defaultValue(true)
        .build()
    );

    public AbayleeChunkFinder() {
        super(AddonTemplate.CATEGORY, "abaylee-chunk-finder-v1", "Detects loaded chunks and sus chunks with deepslate.");
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
                if (chunk == null || chunk.isEmpty()) continue;

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
                    BlockPos pos = new BlockPos(cx * 16 + x, y, cz * 16 + z);
                    BlockState state = chunk.getBlockState(pos);
                    String id = state.getBlock().toString();

                    boolean isCobbledDeepslate = id.contains("cobbled_deepslate");
                    boolean isRotatedDeepslate = id.contains("deepslate") &&
                        state.contains(net.minecraft.state.property.Properties.AXIS);

                    if (isCobbledDeepslate || isRotatedDeepslate) {
                        if (chatAlert.get()) {
                            mc.player.sendMessage(
                                Text.literal("§c[AbayleeChunkFinder] §eSus chunk: §fX=" + cx + " Z=" + cz + " §7(y=" + y + ")"),
                                false
                            );
                        }
                        return;
                    }
                }
            }
        }
    }
}
