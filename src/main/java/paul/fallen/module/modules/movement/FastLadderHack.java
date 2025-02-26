/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.movement;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.utils.entity.EntityUtils;

public final class FastLadderHack extends Module {
    public FastLadderHack(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.PlayerTickEvent event) {
        try {
            PlayerEntity player = event.player;

            if (!player.isOnLadder() || !player.collidedHorizontally)
                return;

            if (player.moveForward == 0
                    && player.moveStrafing == 0)
                return;

            if (player.getMotion().y < 0.25)
                EntityUtils.setMotionY(0.25);
        } catch (Exception ignored) {
        }
    }
}
