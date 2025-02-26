/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package paul.fallen.module.modules.player;

import net.minecraft.network.play.client.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import paul.fallen.module.Module;
import paul.fallen.packetevent.PacketEvent;

public final class Disabler extends Module {

    public Disabler(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof CKeepAlivePacket) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CConfirmTransactionPacket) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CAnimateHandPacket) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CEntityActionPacket) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPlayerDiggingPacket) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPlayerAbilitiesPacket) {
            event.setCanceled(true);
        }
    }
}