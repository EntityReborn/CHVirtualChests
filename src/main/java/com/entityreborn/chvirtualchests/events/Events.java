/*
 * The MIT License
 *
 * Copyright 2013 Jason Unger <entityreborn@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.entityreborn.chvirtualchests.events;

import com.laytonsmith.abstraction.MCInventory;
import com.laytonsmith.abstraction.bukkit.BukkitMCInventory;
import com.laytonsmith.abstraction.bukkit.events.BukkitInventoryEvents;
import com.laytonsmith.abstraction.events.MCInventoryCloseEvent;
import com.laytonsmith.abstraction.events.MCInventoryOpenEvent;
import com.laytonsmith.annotations.api;
import com.laytonsmith.commandhelper.CommandHelperPlugin;
import com.laytonsmith.core.CHVersion;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CString;
import com.laytonsmith.core.constructs.Construct;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.events.AbstractEvent;
import com.laytonsmith.core.events.BindableEvent;
import com.laytonsmith.core.events.Driver;
import com.laytonsmith.core.events.EventUtils;
import com.laytonsmith.core.exceptions.EventException;
import com.laytonsmith.core.exceptions.PrefilterNonMatchException;
import java.util.Map;
import com.entityreborn.chvirtualchests.VirtualChests;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

/**
 *
 * @author Jason Unger <entityreborn@gmail.com>
 */
public class Events implements Listener {

    private static Events listener;

    public static void register() {
        if(listener == null) {
            listener = new Events();
        }
        CommandHelperPlugin.self.registerEvents(listener);
    }

    public static void unregister() {
        InventoryOpenEvent.getHandlerList().unregister(listener);
        InventoryCloseEvent.getHandlerList().unregister(listener);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onOpen(InventoryOpenEvent event) {
        String id = VirtualChests.getID(new BukkitMCInventory(event.getInventory()));
        if (id != null) {
            EventUtils.TriggerListener(Driver.EXTENSION, "virtualchest_opened",
                    new BukkitInventoryEvents.BukkitMCInventoryOpenEvent(event));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClose(InventoryCloseEvent event) {
        String id = VirtualChests.getID(new BukkitMCInventory(event.getInventory()));
        if (id != null) {
            EventUtils.TriggerListener(Driver.EXTENSION, "virtualchest_closed",
                    new BukkitInventoryEvents.BukkitMCInventoryCloseEvent(event));
        }
    }

    @api
    public static class virtualchest_opened extends AbstractEvent {

        public String getName() {
            return "virtualchest_opened";
        }

        public String docs() {
            return "{} "
                    + "Fired when a player opens a virtualchest. "
                    + "{player: The player | " /*"{player: The player who clicked | viewers: everyone looking in this inventory | "*/
                    + "inventory: the inventory items in this inventory} "
                    + "{} "
                    + "{} ";
        }

        public boolean matches(Map<String, Construct> prefilter, BindableEvent event)
                throws PrefilterNonMatchException {
            return true;
        }

        public BindableEvent convert(CArray manualObject, Target t) {
            return null;
        }

        public Map<String, Construct> evaluate(BindableEvent event)
                throws EventException {
            if (event instanceof MCInventoryOpenEvent) {
                MCInventoryOpenEvent e = (MCInventoryOpenEvent) event;
                Map<String, Construct> map = evaluate_helper(event);

                map.put("player", new CString(e.getPlayer().getName(), Target.UNKNOWN));

                MCInventory inv = e.getInventory();

                map.put("chest", VirtualChests.toCArray(inv, Target.UNKNOWN));

                return map;
            } else {
                throw new EventException("Cannot convert e to MCInventoryOpenEvent");
            }
        }

        public Driver driver() {
            return Driver.EXTENSION;
        }

        public boolean modifyEvent(String key, Construct value,
                BindableEvent event) {
            return false;
        }

        public CHVersion since() {
            return CHVersion.V3_3_1;
        }
    }

    @api
    public static class virtualchest_closed extends AbstractEvent {

        public String getName() {
            return "virtualchest_closed";
        }

        public String docs() {
            return "{} "
                    + "Fired when a player closes a virtualchest. "
                    + "{player: The player | " /*"{player: The player who clicked | viewers: everyone looking in this inventory | "*/
                    + "inventory: the inventory items in this inventory} "
                    + "{} "
                    + "{} ";
        }

        public boolean matches(Map<String, Construct> prefilter, BindableEvent event)
                throws PrefilterNonMatchException {
            return true;
        }

        public BindableEvent convert(CArray manualObject, Target t) {
            return null;
        }

        public Map<String, Construct> evaluate(BindableEvent event)
                throws EventException {
            if (event instanceof MCInventoryCloseEvent) {
                MCInventoryCloseEvent e = (MCInventoryCloseEvent) event;
                Map<String, Construct> map = evaluate_helper(event);

                map.put("player", new CString(e.getPlayer().getName(), Target.UNKNOWN));
                
                MCInventory inv = e.getInventory();

                map.put("chest", VirtualChests.toCArray(inv, Target.UNKNOWN));

                return map;
            } else {
                throw new EventException("Cannot convert e to MCInventoryCloseEvent");
            }
        }

        public Driver driver() {
            return Driver.EXTENSION;
        }

        public boolean modifyEvent(String key, Construct value,
                BindableEvent event) {
            return false;
        }

        public CHVersion since() {
            return CHVersion.V3_3_1;
        }
    }
}
