package paul.fallen.module.modules.world;

import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.packetevent.PacketEvent;

public class AutoFish extends Module {

    public AutoFish(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);
    }

    private boolean a = false;

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {
            if (event.phase == TickEvent.Phase.START) {
                if (mc.player.fishingBobber == null) {
                    if (!a) {
                        mc.playerController.processRightClick(mc.player, mc.world, Hand.MAIN_HAND);
                        a = true;
                    }
                } else {
                    a = false;
                }
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        try {
            if (mc.player == null || mc.player.fishingBobber == null)
                return;

            // Check if the packet is of the correct type
            if (!(event.getPacket() instanceof SPlaySoundEffectPacket))
                return;

            // Cast the packet to the correct type
            SPlaySoundEffectPacket sound = (SPlaySoundEffectPacket) event.getPacket();
            if (!SoundEvents.ENTITY_FISHING_BOBBER_SPLASH.equals(sound.getSound()))
                return;

            // Get the player's fishing bobber entity
            FishingBobberEntity bobber = mc.player.fishingBobber;
            if (Math.abs(sound.getX() - bobber.getPosX()) > 1.5
                    || Math.abs(sound.getZ() - bobber.getPosZ()) > 1.5)
                return;

            // Catch fish
            mc.playerController.processRightClick(mc.player, mc.world, Hand.MAIN_HAND);
        } catch (Exception ignored) {
        }
    }
}