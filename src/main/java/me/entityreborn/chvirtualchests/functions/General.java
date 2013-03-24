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
package me.entityreborn.chvirtualchests.functions;

import com.laytonsmith.abstraction.MCHumanEntity;
import com.laytonsmith.abstraction.MCInventory;
import com.laytonsmith.abstraction.MCItemMeta;
import com.laytonsmith.abstraction.MCItemStack;
import com.laytonsmith.abstraction.StaticLayer;
import com.laytonsmith.annotations.api;
import com.laytonsmith.core.CHVersion;
import com.laytonsmith.core.ObjectGenerator;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CInt;
import com.laytonsmith.core.constructs.CNull;
import com.laytonsmith.core.constructs.CString;
import com.laytonsmith.core.constructs.CVoid;
import com.laytonsmith.core.constructs.Construct;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.environments.CommandHelperEnvironment;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.functions.AbstractFunction;
import com.laytonsmith.core.functions.Exceptions;
import java.util.Map;
import me.entityreborn.chvirtualchests.VirtualChests;

/**
 *
 * @author Jason Unger <entityreborn@gmail.com>
 */
public class General {

    @api(environments = {CommandHelperEnvironment.class})
    public static class get_virtualchest extends AbstractFunction {

        public Exceptions.ExceptionType[] thrown() {
            return new Exceptions.ExceptionType[]{Exceptions.ExceptionType.FormatException};
        }

        public boolean isRestricted() {
            return true;
        }

        public Boolean runAsync() {
            return false;
        }

        public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
            String id = args[0].getValue();

            if (id.isEmpty() || args[0] instanceof CNull) {
                throw new ConfigRuntimeException("invalid id. Use either a string or integer.", Exceptions.ExceptionType.FormatException, t);
            }

            if (VirtualChests.get(id) == null) {
                return new CNull(t);
            }

            MCInventory inv = VirtualChests.get(id);

            return VirtualChests.toCArray(inv);
        }

        public String getName() {
            return "get_virtualchest";
        }

        public Integer[] numArgs() {
            return new Integer[]{1};
        }

        public String docs() {
            return "array {id} Gets a cached virtual chest associated with a given id. The id is case "
                    + "insensitive. If the chest doesn't exist, an empty chest "
                    + "is created and returned. Returns an indexed array whose indexes "
                    + "correspond with slot location. ";
        }

        public CHVersion since() {
            return CHVersion.V3_3_1;
        }
    }

    @api(environments = {CommandHelperEnvironment.class})
    public static class close_virtualchest extends AbstractFunction {

        public Exceptions.ExceptionType[] thrown() {
            return null;
        }

        public boolean isRestricted() {
            return true;
        }

        public Boolean runAsync() {
            return false;
        }

        public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
            String id = args[0].getValue();

            if (id.isEmpty() || args[0] instanceof CNull) {
                throw new ConfigRuntimeException("invalid id. Use either a string or integer.", Exceptions.ExceptionType.FormatException, t);
            }

            MCInventory inv = VirtualChests.get(id);

            for (MCHumanEntity ent : inv.getViewers()) {
                ent.closeInventory();
            }

            return new CVoid(t);
        }

        public String getName() {
            return "close_virtualchest";
        }

        public Integer[] numArgs() {
            return new Integer[]{1, 2};
        }

        public String docs() {
            return "void {[player,] id} Closes the specified virtualchest on either the"
                    + " specified player, or all players viewing that virtualchest.";
        }

        public CHVersion since() {
            return CHVersion.V3_3_1;
        }
    }

    @api(environments = {CommandHelperEnvironment.class})
    public static class create_virtualchest extends AbstractFunction {

        public Exceptions.ExceptionType[] thrown() {
            return new Exceptions.ExceptionType[]{Exceptions.ExceptionType.FormatException};
        }

        public boolean isRestricted() {
            return true;
        }

        public Boolean runAsync() {
            return false;
        }

        public Construct exec(Target t, Environment environment, Construct... args)
                throws ConfigRuntimeException {
            CArray items = CArray.GetAssociativeArray(t);
            MCInventory inv;

            if (args[0] instanceof CArray) {
                items = (CArray) args[0];
                inv = VirtualChests.fromCArray(t, items);
            } else {
                throw new ConfigRuntimeException("bad arguments. Expecting item "
                        + "array including 'id', and optionally 'size' and 'title'.",
                        Exceptions.ExceptionType.FormatException, t);
            }

            VirtualChests.setContents(inv, items, t);
            VirtualChests.set(VirtualChests.getID(inv), inv);

            return new CVoid(t);
        }

        public String getName() {
            return "create_virtualchest";
        }

        public Integer[] numArgs() {
            return new Integer[]{1, 2};
        }

        public String docs() {
            return "void {id[, options]} Creates a cached virtual chest associated with a given id. The id is case "
                    + "insensitive. options is expected to be an array which could "
                    + "contain the optional following keys: size (int), title (string), items "
                    + "(indexed list of items). Defaults to 54, \"Virtual Chest\" and empty, respectively.";
        }

        public CHVersion since() {
            return CHVersion.V3_3_1;
        }
    }

    @api(environments = {CommandHelperEnvironment.class})
    public static class set_virtualchest extends AbstractFunction {

        public Exceptions.ExceptionType[] thrown() {
            return new Exceptions.ExceptionType[]{Exceptions.ExceptionType.FormatException};
        }

        public boolean isRestricted() {
            return true;
        }

        public Boolean runAsync() {
            return false;
        }

        public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
            int size = 54;
            String title = "Virtual Chest";
            String id;
            MCInventory inv;
            CArray items;
            if (args.length == 2) {
                if (args[0].val().isEmpty() || args[0] instanceof CNull) {
                    throw new ConfigRuntimeException("invalid id. Use either a string or integer.", Exceptions.ExceptionType.FormatException, t);
                }

                id = args[0].val();

                if (args[1] instanceof CArray) {
                    items = (CArray) args[1];
                } else if (args[0] instanceof CNull) {
                    VirtualChests.del(id);

                    return new CVoid(t);
                } else {
                    throw new ConfigRuntimeException("Expecting an array or null as argument 2", Exceptions.ExceptionType.CastException, t);
                }
            } else {
                if (args[0] instanceof CArray) {
                    items = (CArray) args[0];
                    inv = VirtualChests.fromCArray(t, items);
                    id = VirtualChests.getID(inv);
                } else {
                    throw new ConfigRuntimeException("Expecting an array or null as argument 1", Exceptions.ExceptionType.CastException, t);
                }
            }

            VirtualChests.set(id, VirtualChests.create(id, size, title));

            inv = VirtualChests.get(id);
            VirtualChests.setContents(inv, items, t);

            return new CVoid(t);
        }

        public String getName() {
            return "set_virtualchest";
        }

        public Integer[] numArgs() {
            return new Integer[]{1, 2};
        }

        public String docs() {
            return "void {id, array} Sets a cached virtual chest associated with a given id. The "
                    + "array must be an indexed associative array, whose indexes correspond with "
                    + "slot locations and values are synonymous with those of set_pinv(). "
                    + "The array can be incomplete, indexes not mentioned will be "
                    + "empty, and indexes beyond the permitted range will generate a warning. "
                    + "Specifying null as the second argument deletes the virtualchest.";
        }

        public CHVersion since() {
            return CHVersion.V3_3_1;
        }
    }

    @api(environments = {CommandHelperEnvironment.class})
    public static class addto_virtualchest extends AbstractFunction {

        public Exceptions.ExceptionType[] thrown() {
            return new Exceptions.ExceptionType[]{Exceptions.ExceptionType.FormatException};
        }

        public boolean isRestricted() {
            return true;
        }

        public Boolean runAsync() {
            return false;
        }

        public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
            String id;
            MCInventory inv;
            MCItemStack is;
            Construct m = null;

            if (args[0].val().isEmpty() || args[0] instanceof CNull) {
                throw new ConfigRuntimeException("invalid id. Use either a string or integer.", Exceptions.ExceptionType.FormatException, t);
            }

            id = args[0].val();

            if (VirtualChests.get(id) == null) {
                return new CNull(t);
            }

            is = Static.ParseItemNotation(this.getName(), args[1].val(), Static.getInt32(args[2], t), t);

            if(args.length == 4) {
                m = args[3];
            }

            MCItemMeta meta;
            if (m != null) {
                    meta = ObjectGenerator.GetGenerator().itemMeta(m, is.getTypeId(), t);
            } else {
                    meta = ObjectGenerator.GetGenerator().itemMeta(new CNull(), is.getTypeId(), t);
            }

            is.setItemMeta(meta);

            inv = VirtualChests.get(id);

            Map<Integer, MCItemStack> h = inv.addItem(is);

            if (h.isEmpty()) {
                    return new CInt(0, t);
            } else {
                    return new CInt(h.get(0).getAmount(), t);
            }
        }

        public String getName() {
            return "addto_virtualchest";
        }

        public Integer[] numArgs() {
            return new Integer[]{3, 4};
        }

        public String docs() {
            return "int {chestID, itemID, qty, [meta]} Adds to virtual chest the specified item * qty. The meta argument uses the"
                    + " same format as [http://wiki.sk89q.com/wiki/CommandHelper/Staged/API/set_itemmeta set_itemmeta]."
                    + " Unlike update_virtualchest(), this does not specify a slot. The qty is distributed"
                    + " in the virtual chest, first filling up slots that have the same item"
                    + " type, up to the max stack size, then fills up empty slots, until either"
                    + " the entire virtual chest is filled, or the entire amount has been given."
                    + " The number of items that couldn't be added is returned, which will be less than"
                    + " or equal to the quantity provided. Supports 'infinite' stacks by providing a negative number."
                    + " If the virtual chest is full, 0 is returned in this case instead of the amount given.";
        }

        public CHVersion since() {
            return CHVersion.V3_3_1;
        }
    }

    @api(environments = {CommandHelperEnvironment.class})
    public static class takefrom_virtualchest extends AbstractFunction {

        public Exceptions.ExceptionType[] thrown() {
            return new Exceptions.ExceptionType[]{Exceptions.ExceptionType.FormatException};
        }

        public boolean isRestricted() {
            return true;
        }

        public Boolean runAsync() {
            return false;
        }

        public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
            String id;
            MCInventory inv;
            MCItemStack is;

            if (args[0].val().isEmpty() || args[0] instanceof CNull) {
                throw new ConfigRuntimeException("invalid id. Use either a string or integer.", Exceptions.ExceptionType.FormatException, t);
            }

            id = args[0].val();

            if (VirtualChests.get(id) == null) {
                return new CNull(t);
            }

            is = Static.ParseItemNotation(this.getName(), args[1].val(), Static.getInt32(args[2], t), t);

            inv = VirtualChests.get(id);

            int total = is.getAmount();
            int remaining = is.getAmount();

            for (int i = inv.getSize() - 1; i >= 0; i--) {
                MCItemStack iis = inv.getItem(i);
                if (remaining <= 0) {
                    break;
                }

                if (match(is, iis)) {
                    int toTake = java.lang.Math.min(remaining, iis.getAmount());
                    remaining -= toTake;
                    int replace = iis.getAmount() - toTake;
                    if (replace == 0) {
                        inv.setItem(i, StaticLayer.GetItemStack(0, 0));
                    } else {
                        inv.setItem(i, StaticLayer.GetItemStack(is.getTypeId(), is.getData().getData(), replace));
                    }
                }
            }

            return new CInt(total - remaining, t);
        }

        public String getName() {
            return "takefrom_virtualchest";
        }

        public Integer[] numArgs() {
            return new Integer[]{3};
        }

        public String docs() {
            return "int {chestID, itemID, qty} Works in reverse of pgive_item(), but"
                    + " returns the number of items actually taken, which will be"
                    + " from 0 to qty.";
        }

        private boolean match(MCItemStack is, MCItemStack iis){
            return (is.getTypeId() == iis.getTypeId() && is.getData().getData() == iis.getData().getData());
        }

        public CHVersion since() {
            return CHVersion.V3_3_1;
        }
    }

    @api(environments = {CommandHelperEnvironment.class})
    public static class update_virtualchest extends AbstractFunction {

        public Exceptions.ExceptionType[] thrown() {
            return new Exceptions.ExceptionType[]{Exceptions.ExceptionType.FormatException};
        }

        public boolean isRestricted() {
            return true;
        }

        public Boolean runAsync() {
            return false;
        }

        public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
            String id;
            MCInventory inv;
            CArray items;
            if (args.length == 2) {
                if (args[0].val().isEmpty() || args[0] instanceof CNull) {
                    throw new ConfigRuntimeException("invalid id. Use either a string or integer.", Exceptions.ExceptionType.FormatException, t);
                }

                id = args[0].val();

                if (args[1] instanceof CArray) {
                    items = (CArray) args[1];
                } else {
                    throw new ConfigRuntimeException("Expecting an array or null as argument 2", Exceptions.ExceptionType.CastException, t);
                }
            } else {
                if (args[0] instanceof CArray) {
                    items = (CArray) args[0];

                    if (!items.containsKey("id")) {
                        throw new ConfigRuntimeException("No id specified in array. Use either a string or integer.", Exceptions.ExceptionType.FormatException, t);
                    }

                    id = items.get("id").val();
                } else {
                    throw new ConfigRuntimeException("Expecting an array or null as argument 2", Exceptions.ExceptionType.CastException, t);
                }
            }

            if (VirtualChests.get(id) == null) {
                return new CNull(t);
            }

            inv = VirtualChests.get(id);
            VirtualChests.setContents(inv, items, t);

            return new CVoid(t);
        }

        public String getName() {
            return "update_virtualchest";
        }

        public Integer[] numArgs() {
            return new Integer[]{1, 2};
        }

        public String docs() {
            return "void {id, array} Updates a cached virtual chest associated with a given id. The "
                    + "array must be an indexed associative array, whose indexes correspond with "
                    + "slot locations and values are synonymous with those of set_pinv(). "
                    + "The array can be incomplete, indexes not mentioned will be "
                    + "empty, and indexes beyond the permitted range will generate a warning. "
                    + "This will update a chest, retaining what is already there, and replacing "
                    + "slots that are defined.";
        }

        public CHVersion since() {
            return CHVersion.V3_3_1;
        }
    }

    @api(environments = {CommandHelperEnvironment.class})
    public static class del_virtualchest extends AbstractFunction {

        public Exceptions.ExceptionType[] thrown() {
            return new Exceptions.ExceptionType[]{Exceptions.ExceptionType.FormatException};
        }

        public boolean isRestricted() {
            return true;
        }

        public Boolean runAsync() {
            return false;
        }

        public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
            String id = args[0].getValue();

            if (id.isEmpty() || args[0] instanceof CNull) {
                throw new ConfigRuntimeException("invalid id. Use either a string or integer.", Exceptions.ExceptionType.FormatException, t);
            }

            if (VirtualChests.get(id) != null) {
                MCInventory inv = VirtualChests.del(id);

                for (MCHumanEntity ent : inv.getViewers()) {
                    ent.closeInventory();
                }
            }

            return new CVoid(t);
        }

        public String getName() {
            return "del_virtualchest";
        }

        public Integer[] numArgs() {
            return new Integer[]{1};
        }

        public String docs() {
            return "void {id} Deletes a cached virtualchest.";
        }

        public CHVersion since() {
            return CHVersion.V3_3_1;
        }
    }

    @api(environments = {CommandHelperEnvironment.class})
    public static class all_virtualchests extends AbstractFunction {

        public Exceptions.ExceptionType[] thrown() {
            return null;
        }

        public boolean isRestricted() {
            return true;
        }

        public Boolean runAsync() {
            return false;
        }

        public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
            CArray arr = new CArray(t);

            for (String key : VirtualChests.getAll()) {
                arr.push(new CString(key, t));
            }

            return arr;
        }

        public String getName() {
            return "all_virtualchests";
        }

        public Integer[] numArgs() {
            return new Integer[]{0};
        }

        public String docs() {
            return "array {} Returns the ids of all cached virtual chests.";
        }

        public CHVersion since() {
            return CHVersion.V3_3_1;
        }
    }

    @api(environments = {CommandHelperEnvironment.class})
    public static class clear_virtualchest extends AbstractFunction {

        public Exceptions.ExceptionType[] thrown() {
            return null;
        }

        public boolean isRestricted() {
            return true;
        }

        public Boolean runAsync() {
            return false;
        }

        public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
            String id = args[0].getValue();

            if (id.isEmpty() || args[0] instanceof CNull) {
                throw new ConfigRuntimeException("invalid id. Use either a string or integer.", Exceptions.ExceptionType.FormatException, t);
            }

            MCInventory inv = VirtualChests.get(id);

            if (args.length == 2) {
                if (args[1] instanceof CInt) {
                    CInt ci = (CInt) (args[1]);
                    int i = (int) ci.getInt();

                    if (i >= 0 && i < inv.getSize()) {
                        inv.clear(i);
                    } else {
                        throw new ConfigRuntimeException(
                                "invalid slot. Use an integer 0 or above and less than "
                                + inv.getSize(), Exceptions.ExceptionType.FormatException, t);
                    }
                } else {
                    throw new ConfigRuntimeException(
                            "invalid slot. Use an integer 0 or above and less than "
                            + inv.getSize(), Exceptions.ExceptionType.FormatException, t);
                }
            } else {
                inv.clear();
            }

            return new CVoid(t);
        }

        public String getName() {
            return "clear_virtualchest";
        }

        public Integer[] numArgs() {
            return new Integer[]{1, 2};
        }

        public String docs() {
            return "void {id [,slot]} Clears the virtualchest specified. If slot is"
                    + " given, clears that slot, otherwise clears the whole inventory.";
        }

        public CHVersion since() {
            return CHVersion.V3_3_1;
        }
    }
}
