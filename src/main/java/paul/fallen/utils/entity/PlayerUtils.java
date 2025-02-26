package paul.fallen.utils.entity;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.Explosion.Mode;
import net.minecraftforge.common.ToolType;
import paul.fallen.ClientSupport;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PlayerUtils implements ClientSupport {

    public static boolean isMoving() {
        return mc.player.movementInput.moveForward != 0.0f || mc.player.movementInput.moveStrafe != 0.0f;
    }

    public static double getBaseMoveSpeed() {
        return 0.2873D;
    }

    public static double[] calculateMotion(double speed, double increment) {
        double[] motion = new double[2];
        double currentSpeed = 0.0;

        while (currentSpeed < speed) {
            // Increment motion gradually
            motion[0] += increment;
            motion[1] += increment;

            // Calculate current speed (hypotenuse of motion in x and z directions)
            currentSpeed = Math.sqrt(motion[0] * motion[0] + motion[1] * motion[1]);
        }

        return motion;
    }

    public static double calculateMotionY(double speed, double increment) {
        double motion = 0;
        double currentSpeed = 0.0;

        // Use a for loop to increment motion until currentSpeed reaches speed
        for (; currentSpeed < speed; currentSpeed += increment) {
            // Increment motion gradually
            motion += increment;
        }

        return motion;
    }

    public static void setMoveSpeed(double speed) {
        double forward = mc.player.moveForward;
        double strafe = mc.player.moveStrafing;
        float yaw = mc.player.rotationYaw;
        if (forward != 0) {
            if (strafe > 0) {
                yaw += ((forward > 0) ? -45 : 45);
            } else if (strafe < 0) {
                yaw += ((forward > 0) ? 45 : -45);
            }
            strafe = 0;
            if (forward > 0) {
                forward = 1;
            } else {
                forward = -1;
            }
        }
        mc.player.setVelocity(forward * speed * Math.cos(Math.toRadians((yaw + 90.0F))) + strafe * speed * Math.sin(Math.toRadians((yaw + 90.0F))), mc.player.getMotion().getY(), forward * speed * Math.sin(Math.toRadians((yaw + 90.0F))) - strafe * speed * Math.cos(Math.toRadians((yaw + 90.0F))));
    }

    public static double[] getMotions(double speed) {
        double forward = getForward();
        double strafe = getStrafe();
        float yaw = mc.player.rotationYaw;
        if (forward != 0) {
            if (strafe > 0) {
                yaw += ((forward > 0) ? -45 : 45);
            } else if (strafe < 0) {
                yaw += ((forward > 0) ? 45 : -45);
            }
            strafe = 0;
            if (forward > 0) {
                forward = 1;
            } else {
                forward = -1;
            }
        }
        return new double[]{forward * speed * Math.cos(Math.toRadians((yaw + 90.0F))) + strafe * speed * Math.sin(Math.toRadians((yaw + 90.0F))), forward * speed * Math.sin(Math.toRadians((yaw + 90.0F))) - strafe * speed * Math.cos(Math.toRadians((yaw + 90.0F)))};
    }

    public static int getForward() {
        if (mc.gameSettings.keyBindForward.isKeyDown()) {
            return 1;
        } else if (mc.gameSettings.keyBindBack.isKeyDown()) {
            return -1;
        } else {
            return 0;
        }
    }

    public static int getStrafe() {
        if (mc.gameSettings.keyBindRight.isKeyDown()) {
            return 1;
        } else if (mc.gameSettings.keyBindLeft.isKeyDown()) {
            return -1;
        } else {
            return 0;
        }
    }

    public static void setMoveSpeedRidingEntity(double speed) {
        double forward = mc.player.moveForward;
        double strafe = mc.player.moveStrafing;
        float yaw = mc.player.rotationYaw;
        if (forward != 0) {
            if (strafe > 0) {
                yaw += ((forward > 0) ? -45 : 45);
            } else if (strafe < 0) {
                yaw += ((forward > 0) ? 45 : -45);
            }
            strafe = 0;
            if (forward > 0) {
                forward = 1;
            } else {
                forward = -1;
            }
        }
        mc.player.getRidingEntity().setMotion(forward * speed * Math.cos(Math.toRadians((yaw + 90.0F))) + strafe * speed * Math.sin(Math.toRadians((yaw + 90.0F))), mc.player.getMotion().getY(), forward * speed * Math.sin(Math.toRadians((yaw + 90.0F))) - strafe * speed * Math.cos(Math.toRadians((yaw + 90.0F))));
    }

    public static float getDirection() {
        float yaw = mc.player.rotationYaw;
        float forward = mc.player.moveForward;
        float strafe = mc.player.moveStrafing;
        yaw += (forward < 0.0F ? 180 : 0);
        if (strafe < 0.0F) {
            yaw += (forward == 0.0F ? 90 : forward < 0.0F ? -45 : 45);
        }
        if (strafe > 0.0F) {
            yaw -= (forward == 0.0F ? 90 : forward < 0.0F ? -45 : 45);
        }
        return yaw * 0.017453292F;
    }

    public static float getSpeed() {
        return (float) Math.sqrt(mc.player.getMotion().getX() * mc.player.getMotion().getX() + mc.player.getMotion().getZ() * mc.player.getMotion().getZ());
    }

    public static void setSpeed(double speed) {
        mc.player.setVelocity(-MathHelper.sin(getDirection()) * speed, mc.player.getMotion().getY(), MathHelper.cos(getDirection()) * speed);
    }

    public static void setTickSpeed(float speed) {
        try {
            // Get Minecraft instance
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();

            // Get the 'timer' field from the Minecraft class
            Field timerField = mc.getClass().getDeclaredField("timer");

            // Set the accessibility of the field to true to be able to access it
            timerField.setAccessible(true);

            // Get the value of the 'timer' field
            Object timer = timerField.get(mc);

            // Get the 'tickLength' field from the Timer class
            Field tickLengthField = timer.getClass().getDeclaredField("tickLength");

            // Set the accessibility of the field to true to be able to access it
            tickLengthField.setAccessible(true);

            // Set the custom tick speed
            tickLengthField.set(timer, 50 / speed);












            // Get the 'timer' field from the Minecraft class
            Field timerFieldA = mc.getClass().getDeclaredField("field_71428_T");

            // Set the accessibility of the field to true to be able to access it
            timerFieldA.setAccessible(true);

            // Get the value of the 'timer' field
            Object timerA = timerField.get(mc);

            // Get the 'tickLength' field from the Timer class
            Field tickLengthFieldA = timerA.getClass().getDeclaredField("field_194149_e");

            // Set the accessibility of the field to true to be able to access it
            tickLengthFieldA.setAccessible(true);

            // Set the custom tick speed
            tickLengthFieldA.set(timer, 50 / speed);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setCockSpeed(final double moveSpeed, final float pseudoYaw, final double pseudoStrafe, final double pseudoForward) {
        double forward = pseudoForward;
        double strafe = pseudoStrafe;
        float yaw = pseudoYaw;

        if (forward == 0.0 && strafe == 0.0) {
            mc.player.setVelocity(0, mc.player.getMotion().getY(), 0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            Random random = new Random();
            int min = 1;
            int max = 15;
            int delay = random.nextInt(max + 1 - min) + min;

            final double cos = Math.cos(Math.toRadians(yaw + 90.0f + delay));
            final double sin = Math.sin(Math.toRadians(yaw + 90.0f + delay));

            mc.player.setVelocity(forward * moveSpeed * cos + strafe * moveSpeed * sin, mc.player.getMotion().getY(), forward * moveSpeed * sin - strafe * moveSpeed * cos);
        }
    }

    public static float getBlockDensity(Vector3d explosionVector, Entity entity) {
        AxisAlignedBB axisalignedbb = entity.getBoundingBox();
        double d0 = 1.0D / ((axisalignedbb.maxX - axisalignedbb.minX) * 2.0D + 1.0D);
        double d1 = 1.0D / ((axisalignedbb.maxY - axisalignedbb.minY) * 2.0D + 1.0D);
        double d2 = 1.0D / ((axisalignedbb.maxZ - axisalignedbb.minZ) * 2.0D + 1.0D);
        double d3 = (1.0D - Math.floor(1.0D / d0) * d0) / 2.0D;
        double d4 = (1.0D - Math.floor(1.0D / d2) * d2) / 2.0D;

        if (!(d0 < 0.0D) && !(d1 < 0.0D) && !(d2 < 0.0D)) {
            int i = 0;
            int j = 0;

            for (float f = 0.0F; f <= 1.0F; f = (float) ((double) f + d0)) {
                for (float f1 = 0.0F; f1 <= 1.0F; f1 = (float) ((double) f1 + d1)) {
                    for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float) ((double) f2 + d2)) {
                        double d5 = MathHelper.lerp(f, axisalignedbb.minX, axisalignedbb.maxX);
                        double d6 = MathHelper.lerp(f1, axisalignedbb.minY, axisalignedbb.maxY);
                        double d7 = MathHelper.lerp(f2, axisalignedbb.minZ, axisalignedbb.maxZ);
                        Vector3d vector3d = new Vector3d(d5 + d3, d6, d7 + d4);

                        if (entity.world.rayTraceBlocks(new RayTraceContext(vector3d, explosionVector, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity)).getType() == RayTraceResult.Type.MISS) {
                            ++i;
                        }

                        ++j;
                    }
                }
            }

            return (float) i / (float) j;
        } else {
            return 0.0F;
        }
    }

    public static float getDamageMultiplied(final float damage) {
        final int diff = mc.world.getDifficulty().getId();
        return damage * ((diff == 0) ? 0.0f : ((diff == 2) ? 1.0f : ((diff == 1) ? 0.5f : 1.5f)));
    }

    public static float calculateCrystalDamage(double posX, double posY, double posZ, Entity entity) {
        if (entity == mc.player)
            if (mc.player.isCreative())
                return 0.0f;
        final float doubleExplosionSize = 12.0f;
        final double distancedsize = getDistance(posX, posY, posZ) / doubleExplosionSize;
        final Vector3d vec3d = new Vector3d(posX, posY, posZ);
        double blockDensity = 0.0;
        try {
            blockDensity = getBlockDensity(vec3d, entity);
        } catch (Exception ignore) {
        }
        final double v = (1.0 - distancedsize) * blockDensity;
        final float damage = (float) (int) ((v * v + v) / 2.0 * 7.0 * doubleExplosionSize + 1.0);
        double finald = 1.0;
        if (entity instanceof LivingEntity)
            finald = getBlastReduction((LivingEntity) entity, getDamageMultiplied(damage), new Explosion(mc.world, null, posX, posY, posZ, 6.0f, false, Mode.NONE));
        return (float) finald;
    }

    public static float calculateCrystalDamage(EnderCrystalEntity crystal, Entity entity) {
        return calculateCrystalDamage(crystal.getPosX(), crystal.getPosY(), crystal.getPosZ(), entity);
    }

    public static float getBlastReduction(final LivingEntity entity, final float damageI, final Explosion explosion) {
        float damage = damageI;
        if (entity instanceof PlayerEntity) {
            final PlayerEntity ep = (PlayerEntity) entity;
            final DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(), (float) ep.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
            int k = 0;
            try {
                k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            } catch (Exception ignored) {
            }
            final float f = MathHelper.clamp((float) k, 0.0f, 20.0f);
            damage *= 1.0f - f / 25.0f;
            damage = Math.max(damage, 0.0f);
            return damage;
        }
        damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
        return damage;
    }

    public static float getDistance(double posX, double posY, double posZ) {
        float f = (float) (mc.player.getPosX() - posX);
        float f1 = (float) (mc.player.getPosY() - posY);
        float f2 = (float) (mc.player.getPosZ() - posZ);
        return MathHelper.sqrt(f * f + f1 * f1 + f2 * f2);
    }

    public static boolean canPlaceCrystal(final BlockPos blockPos, final boolean thirteen, final boolean specialEntityCheck) {
        final BlockPos boost = blockPos.add(0, 1, 0);
        final BlockPos boost2 = blockPos.add(0, 2, 0);
        final BlockPos finalBoost = blockPos.add(0, 3, 0);
        try {
            if (mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                return false;
            }
            if ((mc.world.getBlockState(boost).getBlock() != Blocks.AIR || (mc.world.getBlockState(boost2).getBlock() != Blocks.AIR && !thirteen))) {
                return false;
            }
            if (!specialEntityCheck) {
                return mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
            }
            for (final Object e : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
                if (!(e instanceof EnderCrystalEntity)) {
                    return false;
                }
            }
            for (final Object e : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2))) {
                if (!(e instanceof EnderCrystalEntity)) {
                    return false;
                }
            }
            for (final Object e : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(finalBoost))) {
                if (e instanceof EnderCrystalEntity) {
                    return false;
                }
            }
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }

    public static List<BlockPos> possiblePlacePositions(final float placeRange, final boolean thirteen, final boolean specialEntityCheck) {
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(getSphere(getPlayerPos(mc.player), placeRange, (int) placeRange, false, true, 0).stream().filter(pos -> canPlaceCrystal(pos, thirteen, specialEntityCheck)).collect(Collectors.toList()));
        return positions;
    }

    public static List<BlockPos> getSphere(final BlockPos pos, final float r, final int h, final boolean hollow, final boolean sphere, final int plus_y) {
        final List<BlockPos> circleBlocks = new ArrayList<>();
        final int cx = pos.getX();
        final int cy = pos.getY();
        final int cz = pos.getZ();
        for (int x = cx - (int) r; x <= cx + r; ++x) {
            for (int z = cz - (int) r; z <= cz + r; ++z) {
                for (int y = sphere ? (cy - (int) r) : cy; y < (sphere ? (cy + r) : ((float) (cy + h))); ++y) {
                    final double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? ((cy - y) * (cy - y)) : 0);
                    if (dist < r * r && (!hollow || dist >= (r - 1.0f) * (r - 1.0f))) {
                        final BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleBlocks.add(l);
                    }
                }
            }
        }
        return circleBlocks;
    }

    public static BlockPos getPlayerPos(final PlayerEntity player) {
        return new BlockPos(Math.floor(player.getPosX()), Math.floor(player.getPosY()), Math.floor(player.getPosZ()));
    }

    public static Vector3d getCameraPos() {
        Minecraft mc = Minecraft.getInstance();
        Entity renderViewEntity = mc.getRenderViewEntity();

        if (renderViewEntity != null) {
            return renderViewEntity.getEyePosition(mc.getRenderPartialTicks());
        }

        return Vector3d.ZERO;
    }

    public static int geSlotHotbar(Item item) {
        return IntStream.range(0, 9)
                .filter(i -> {
                    ItemStack stack = mc.player.inventory.getStackInSlot(i);
                    return stack.getItem() == item;
                })
                .findFirst()
                .orElse(-1);
    }

    public static int geBlockItemSlotHotBar() {
        return IntStream.range(0, 9)
                .filter(i -> {
                    ItemStack stack = mc.player.inventory.getStackInSlot(i);
                    return stack.getItem() instanceof BlockItem;
                })
                .findFirst()
                .orElse(-1);
    }

    public static int getSlotHotbarBestPickaxe() {
        return IntStream.range(0, 9)
                .filter(i -> {
                    ItemStack stack = mc.player.inventory.getStackInSlot(i);
                    return stack.getItem().getToolTypes(stack).contains(ToolType.PICKAXE);
                })
                .boxed()
                .max(Comparator.comparingDouble(i -> {
                    ItemStack stack = mc.player.inventory.getStackInSlot(i);
                    return stack.getDestroySpeed(Blocks.STONE.getDefaultState()); // Efficiency against stone
                }))
                .orElse(-1); // Return -1 if no pickaxe is found
    }

    public static int getSlotHotbarBestToolForBlock(Block block) {
        return IntStream.range(0, 9)
                .filter(i -> {
                    ItemStack stack = mc.player.inventory.getStackInSlot(i);
                    return stack.getItem() instanceof ToolItem && !stack.getItem().getToolTypes(stack).isEmpty();
                })
                .boxed()
                .max(Comparator.comparingDouble(i -> {
                    ItemStack stack = mc.player.inventory.getStackInSlot(i);
                    return stack.getDestroySpeed(block.getDefaultState()); // Efficiency against the given block
                }))
                .orElse(-1); // Return -1 if no suitable tool is found
    }
}
