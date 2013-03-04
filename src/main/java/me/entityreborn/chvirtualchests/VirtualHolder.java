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
package me.entityreborn.chvirtualchests;

import com.laytonsmith.abstraction.MCInventory;
import com.laytonsmith.abstraction.MCInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 *
 * @author Jason Unger <entityreborn@gmail.com>
 */
public class VirtualHolder implements MCInventoryHolder {
    // Make bukkit happy.
    public static class Holder implements InventoryHolder {

        String id;
        VirtualHolder parent;

        public Holder(String i, VirtualHolder p) {
            id = i;
            parent = p;
        }

        public String id() {
            return id;
        }

        public Inventory getInventory() {
            if (parent.getInventory().getHandle() instanceof Inventory) {
                return (Inventory) parent.getInventory().getHandle();
            }

            return null;
        }
    }
        String id;
        Holder holder;

        public VirtualHolder(String i) {
            id = i;
        }

        public String getID() {
            return id;
        }

        public MCInventory getInventory() {
            return VirtualChests.get(id);
        }

        public Object getHandle() {
            if (holder == null) {
                holder = new Holder(id, this);
            }

            return holder;
        }
    
}
