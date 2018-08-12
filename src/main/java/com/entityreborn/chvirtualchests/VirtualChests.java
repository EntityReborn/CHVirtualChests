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
package com.entityreborn.chvirtualchests;

import com.laytonsmith.abstraction.MCInventory;
import com.laytonsmith.abstraction.MCInventoryHolder;
import com.laytonsmith.abstraction.MCItemStack;
import com.laytonsmith.abstraction.bukkit.BukkitMCInventory;
import com.laytonsmith.abstraction.enums.MCInventoryType;
import com.laytonsmith.abstraction.enums.bukkit.BukkitMCInventoryType;
import com.laytonsmith.core.ObjectGenerator;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CInt;
import com.laytonsmith.core.constructs.CString;
import com.laytonsmith.core.constructs.Construct;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.exceptions.CRE.CREFormatException;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Jason Unger <entityreborn@gmail.com>
 */
public class VirtualChests {
    private static final Map<String, MCInventory> chests =
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

    public static MCInventory create(String id, MCInventoryType type, String title) {
        VirtualHolder invHolder = new VirtualHolder(id.toLowerCase().trim());
        InventoryType invType = BukkitMCInventoryType.getConvertor().getConcreteEnum(type);
        return new BukkitMCInventory(Bukkit.createInventory((InventoryHolder)invHolder.getHandle(), invType, title));
    }

    public static MCInventory create(String id, int size, String title) {
        if (size <= 0) {
            size = 9;
        }

        int s = size / 9 * 9; // Assert that the size is multiple of 9.

        return Static.getServer().createInventory(
                new VirtualHolder(id.toLowerCase().trim()), s, title);
    }

    public static MCInventory del(String id) {
        String key = id.toLowerCase().trim();
        return chests.remove(key);
    }

    public static MCInventory setContents(MCInventory inv, CArray items, Target t) {
        for (String key : items.stringKeySet()) {
            if(key.equals("id") || key.equals("size") || key.equals("title") || key.equals("type")) {
                continue;
            }
            try {
                int index = Integer.parseInt(key);

                if (index >= 0 && index < inv.getSize()) {

                    MCItemStack is = ObjectGenerator.GetGenerator().item(items.get(index, t), t);

                    if (!is.isEmpty()) {
                        inv.setItem(index, is);
                    }
                } else {
                    ConfigRuntimeException.DoWarning("Out of range value (" + index + ") found in array passed to"
                            + " virtualchest, so ignoring.");
                }
            } catch (NumberFormatException e) {
                ConfigRuntimeException.DoWarning("Expecting integer value for key in array passed to virtualchest, but"
                        + " \"" + key + "\" was found. Ignoring.");
            }
        }

        return inv;
    }

    public static CArray toCArray(MCInventory inv, Target t) {
        CArray items = CArray.GetAssociativeArray(t);

        for (int i = 0; i < inv.getSize(); i++) {
            Construct c = ObjectGenerator.GetGenerator().item(inv.getItem(i), t);
            items.set(i, c, t);
        }

        items.set("id", getID(inv));
        items.set("size", new CInt(inv.getSize(), t), t);
        items.set("type", new CString(inv.getType().name(), t), t);
        items.set("title", inv.getTitle());

        return items;
    }

    public static MCInventory fromCArray(CArray array, Target t) {
        String id = "";
        String title = "Virtual Chest";

        if (array.containsKey("id")) {
            id = array.get("id", t).val();
        } else {
            throw new CREFormatException("Expecting item with key 'id' in array", t);
        }

        if (array.containsKey("title")) {
            title = array.get("title", t).val();
        }

        MCInventoryType type = MCInventoryType.CHEST;
        if (array.containsKey("type")) {
            try {
                type = MCInventoryType.valueOf(array.get("type", t).val().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new CREFormatException("Invalid inventory type \"" + array.get("type", t).val() + "\"", t);
            }
        }

        MCInventory inv;
        if(type.equals(MCInventoryType.CHEST)) {
            int size = 54;
            if (array.containsKey("size")) {
                size = Static.getInt32(array.get("size", t), t);
            }
            inv = VirtualChests.create(id, size, title);
        } else {
            inv = VirtualChests.create(id, type, title);
        }
        VirtualChests.setContents(inv, array, t);
        return inv;
    }

    public static String getID(MCInventory inv) {
        MCInventoryHolder ih = inv.getHolder();

        if (ih.getHandle() instanceof VirtualHolder.Holder) {
            return ((VirtualHolder.Holder) ih.getHandle()).id();
        }

        return null;
    }
}