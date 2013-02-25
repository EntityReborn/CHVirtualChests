/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.entityreborn.chvirtualchests;

import com.laytonsmith.abstraction.MCInventory;
import com.laytonsmith.abstraction.MCInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 *
 * @author import
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
