/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.entityreborn.chvirtualchests;

import com.laytonsmith.abstraction.MCInventory;
import com.laytonsmith.abstraction.MCInventoryHolder;
import com.laytonsmith.abstraction.MCItemStack;
import com.laytonsmith.abstraction.StaticLayer;
import com.laytonsmith.core.ObjectGenerator;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CInt;
import com.laytonsmith.core.constructs.Construct;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.functions.Exceptions;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 *
 * @author Jason Unger <entityreborn@gmail.com>
 */
public class VirtualChests {

    // Make bukkit happy.
    public static class Holder implements InventoryHolder {

        String id;
        VirtualChest parent;

        public Holder(String i, VirtualChest p) {
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

    // Make CH happy.
    public static class VirtualChest implements MCInventoryHolder {

        String id;
        Holder holder;

        public VirtualChest(String i) {
            id = i;
        }

        public String getID() {
            return id;
        }

        public MCInventory getInventory() {
            return chests.get(id);
        }

        public Object getHandle() {
            if (holder == null) {
                holder = new Holder(id, this);
            }

            return holder;
        }
    }
    private static Map<String, MCInventory> chests =
            new HashMap<String, MCInventory>();

    public static MCInventory get(String id) {
        return chests.get(id.toLowerCase().trim());
    }

    public static void set(String id, MCInventory inv) {
        chests.put(id.toLowerCase().trim(), inv);
    }

    public static Set<String> getAll() {
        return chests.keySet();
    }

    public static MCInventory create(String id) {
        return create(id, 54, "Virtual Chest");
    }

    public static MCInventory create(String id, int size, String title) {
        if (size <= 0) {
            size = 9;
        }

        int s = size / 9 * 9; // Assert that the size is multiple of 9.

        return StaticLayer.GetConvertor().GetServer().createInventory(
                new VirtualChest(id.toLowerCase().trim()), s, title);
    }

    public static MCInventory del(String id) {
        String key = id.toLowerCase().trim();
        return chests.remove(key);
    }

    public static MCInventory setContents(MCInventory inv, CArray items, Target t) {
        for (String key : items.keySet()) {
            try {
                int index = Integer.parseInt(key);

                MCItemStack is = ObjectGenerator.GetGenerator().item(items.get(index), t);

                if (index >= 0 && index < inv.getSize()) {
                    if (is.getTypeId() != 0) {
                        inv.setItem(index, is);
                    }
                } else {
                    ConfigRuntimeException.DoWarning("Out of range value (" + index + ") found in array passed to set_virtualchest(), so ignoring.");
                }
            } catch (NumberFormatException e) {
                ConfigRuntimeException.DoWarning("Expecting integer value for key in array passed to set_pinv(), but \"" + key + "\" was found. Ignoring.");
            }
        }

        return inv;
    }

    public static CArray toCArray(MCInventory inv) {
        CArray items = CArray.GetAssociativeArray(Target.UNKNOWN);

        for (int i = 0; i < inv.getSize(); i++) {
            Construct c = ObjectGenerator.GetGenerator().item(inv.getItem(i), Target.UNKNOWN);
            items.set(i, c, Target.UNKNOWN);
        }

        items.set("id", getID(inv));
        items.set("size", String.valueOf(inv.getSize()));
        items.set("title", inv.getTitle());

        return items;
    }

    public static MCInventory fromCArray(Target t, CArray array) {
        String id = "";
        String title = "Virtual Chest";
        int size = 54;
        
        if (array.containsKey("id")) {
            id = array.get("id").getValue();
        } else {
            throw new ConfigRuntimeException("Expecting item with key 'id' in arg 2 array", Exceptions.ExceptionType.FormatException, t);
        }

        if (array.containsKey("size") && array.get("size") instanceof CInt) {
            size = (int) ((CInt) array.get("size")).getInt();
        }

        if (array.containsKey("title")) {
            title = array.get("title").getValue();
        }
        
        MCInventory inv = VirtualChests.create(id, size, title);
        VirtualChests.setContents(inv, array, t);
        
        return inv;
    }

    public static String getID(MCInventory inv) {
        MCInventoryHolder ih = inv.getHolder();

        if (ih.getHandle() instanceof Holder) {
            return ((Holder) ih.getHandle()).id();
        }

        return null;
    }
}