package paul.fallen.module.modules.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.packetevent.PacketEvent;
import paul.fallen.pathfinding.LocomotionPathfinder;
import paul.fallen.utils.entity.RotationUtils;
import paul.fallen.utils.render.RenderUtils;

import java.util.ArrayList;
import java.util.Collections;


public class InfiniteAura extends Module {

    private final Setting antiTP;

    private LocomotionPathfinder aStarCustomPathFinder;
    private Entity entity;
    private long lastActionTime = 0L; // Variable to store the timestamp of the last action

    public InfiniteAura(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);

        antiTP = new Setting("AntiTP", this, false);
        addSetting(antiTP);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        try {
            if (event.phase == TickEvent.Phase.START) {
                Entity entity = findClosestEntity();

                // Skip if less than 1 second has passed since the last action
                if (System.currentTimeMillis() - lastActionTime < 500) {
                    return;
                }

                // Set the last action time to the current time
                lastActionTime = System.currentTimeMillis();

                if (entity == null)
                    return;

                // Move to entity
                aStarCustomPathFinder = new LocomotionPathfinder(mc.player.getPosition(), entity.getPosition());
                aStarCustomPathFinder.compute();

                this.entity = entity;

                // Move forward
                int pathSize = aStarCustomPathFinder.getPath().size(); // Store the size of the path
                for (int a = 0; a < pathSize; a++) {
                    BlockPos v = aStarCustomPathFinder.getPath().get(a);
                    mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(v.getX(), v.getY(), v.getZ(), true));
                }

                // Look at entity
                float[] rot = RotationUtils.getYawAndPitch(new Vector3d(aStarCustomPathFinder.getPath().get(pathSize - 1).getX(), aStarCustomPathFinder.getPath().get(pathSize - 1).getY(), aStarCustomPathFinder.getPath().get(pathSize - 1).getZ()), entity.getBoundingBox().getCenter());
                mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(rot[0], rot[1], true));

                // Attack entity
                mc.playerController.attackEntity(mc.player, entity);
                mc.player.swingArm(Hand.MAIN_HAND);

                // Reverse path
                ArrayList<BlockPos> vector3ds = new ArrayList<>();

                vector3ds.addAll(aStarCustomPathFinder.getPath());

                Collections.reverse(vector3ds);

                // Move back
                pathSize = vector3ds.size(); // Update the size of the path
                for (int b = 0; b < pathSize; b++) {
                    BlockPos v = vector3ds.get(b);
                    mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(v.getX(), v.getY(), v.getZ(), true));
                }
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        try {
            if (antiTP.getValBoolean()) {
                if (event.getPacket() instanceof SPlayerPositionLookPacket) {
                    mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), mc.player.isOnGround()));
                    event.setCanceled(true);
                }
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        try {
            if (aStarCustomPathFinder.getPath().size() > 0 && aStarCustomPathFinder != null) {
                //aStarCustomPathFinder.renderPath(event);
            }
            if (entity != null) {
                RenderUtils.drawOutlinedBox(entity.getPosition(), 0, 1, 0, event);
            }
        } catch (Exception ignored) {
        }
    }

    private Entity findClosestEntity() {
        Entity closestEntity = null;
        double closestDistance = Double.MAX_VALUE;

        assert mc.world != null;
        for (Entity entity : mc.world.getAllEntities()) {
            if (entity != null && entity != mc.player && entity instanceof LivingEntity) {
                assert mc.player != null;
                double distance = mc.player.getDistanceSq(entity.getPosX(), entity.getPosY(), entity.getPosZ());
                if (distance < closestDistance) { // Fixed variable name
                    closestDistance = distance;
                    closestEntity = entity;
                }
            }
        }
        if (closestEntity != null && mc.player != null) { // Removed assertion for closestEntity not being null
            return closestEntity;
        }
        return null; // Moved return statement out of the if condition
    }
}