package paul.fallen.module.modules.render;

import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.utils.render.RenderUtils;

import java.util.ArrayList;

public class Breadcrumbs extends Module {

    private final ArrayList<Vector3d> vecArrayList = new ArrayList<>();

    public Breadcrumbs(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        try {

            Vector3d vector3d = new Vector3d(mc.player.getPosition().getX(), mc.player.getPosition().getY(), mc.player.getPosition().getZ());

            if (!vecArrayList.contains(vector3d)) {
                vecArrayList.add(vector3d);
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (vecArrayList.size() > 0) {
            RenderUtils.renderPath(vecArrayList, event);
        }
    }
}

