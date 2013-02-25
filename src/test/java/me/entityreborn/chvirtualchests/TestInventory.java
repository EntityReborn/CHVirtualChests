/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.entityreborn.chvirtualchests;

import com.laytonsmith.abstraction.MCHumanEntity;
import com.laytonsmith.abstraction.MCInventory;
import com.laytonsmith.abstraction.MCInventoryHolder;
import com.laytonsmith.abstraction.MCItemStack;
import com.laytonsmith.abstraction.StaticLayer;
import com.laytonsmith.abstraction.enums.MCInventoryType;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CInt;
import com.laytonsmith.core.constructs.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author import
 */
public abstract class TestInventory implements MCInventory {
    VirtualHolder holder;
    List<MCHumanEntity> viewers;
    Map<Integer, MCItemStack> items;
    
    public TestInventory(String id) {
        holder = new VirtualHolder(id);
        items = new HashMap<Integer, MCItemStack>();
        viewers = new ArrayList<MCHumanEntity>();
    }
    
    public MCInventoryType getType() {
        return MCInventoryType.CHEST;
    }

    public MCItemStack getItem(int index) {
        MCItemStack item = items.get(index);
        
        if (item == null) {
            return StaticLayer.GetItemStack(0, 1);
        }
        
        return item;
    }

    public void setItem(int index, MCItemStack stack) {
        items.put(index, stack);
    }

    public List<MCHumanEntity> getViewers() {
        return viewers;
    }

    public void clear() {
        items.clear();
    }

    public void clear(int index) {
        items.remove(index);
    }

    public MCInventoryHolder getHolder() {
        return holder;
    }

    public Object getHandle() {
        return this;
    }
}
