/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.entityreborn.chvirtualevents.functions;

import com.laytonsmith.abstraction.MCHumanEntity;
import com.laytonsmith.abstraction.MCInventory;
import com.laytonsmith.abstraction.MCPlayer;
import com.laytonsmith.annotations.api;
import com.laytonsmith.annotations.shutdown;
import com.laytonsmith.core.CHVersion;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.CArray;
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
import me.entityreborn.chvirtualevents.VirtualChests;

/**
 *
 * @author import
 */
public class Player {
    
    @shutdown
    public static void onShutdown() {
        for (String id : VirtualChests.getAll()) {
            for (MCHumanEntity p : VirtualChests.get(id).getViewers()) {
                if (p != null) {
                    try {
                        p.closeInventory();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    @api(environments = {CommandHelperEnvironment.class})
    public static class popen_virtualchest extends AbstractFunction {

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
            MCPlayer p;
            String id;

            if (args.length == 2) {
                p = Static.GetPlayer(args[0], t);
                id = args[1].getValue();

                if (id.isEmpty() || args[1] instanceof CNull) {
                    throw new ConfigRuntimeException("invalid id. Use either a string or integer.", Exceptions.ExceptionType.FormatException, t);
                }
            } else {
                p = environment.getEnv(CommandHelperEnvironment.class).GetPlayer();
                id = args[0].getValue();

                if (id.isEmpty() || args[0] instanceof CNull) {
                    throw new ConfigRuntimeException("invalid id. Use either a string or integer.", Exceptions.ExceptionType.FormatException, t);
                }
            }

            Static.AssertPlayerNonNull(p, t);

            if (VirtualChests.get(id) == null) {
                VirtualChests.set(id, VirtualChests.create(id));
            }

            p.openInventory(VirtualChests.get(id));

            return new CVoid(t);
        }

        public String getName() {
            return "popen_virtualchest";
        }

        public Integer[] numArgs() {
            return new Integer[]{1, 2};
        }

        public String docs() {
            return "void {[player,] id} Shows the specified inventory to either the"
                    + " specified player, or the player calling the function.";
        }

        public CHVersion since() {
            return CHVersion.V3_3_1;
        }
    }

    @api(environments = {CommandHelperEnvironment.class})
    public static class pget_virtualchest extends AbstractFunction {

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
            MCPlayer p;

            if (args.length == 2) {
                p = Static.GetPlayer(args[0], t);
            } else {
                p = environment.getEnv(CommandHelperEnvironment.class).GetPlayer();
            }

            Static.AssertPlayerNonNull(p, t);
            
            String id = VirtualChests.getID(p.getOpenInventory().getTopInventory());
            
            if (id != null) {
                return new CString(id, t);
            }

            return new CNull(t);

        }

        public String getName() {
            return "pget_virtualchest";
        }

        public Integer[] numArgs() {
            return new Integer[]{0, 1};
        }

        public String docs() {
            return "string {[player]} Returns the id of the virtual chest a player is"
                    + " looking at, or null.";
        }

        public CHVersion since() {
            return CHVersion.V3_3_1;
        }
    }
    
    @api(environments = {CommandHelperEnvironment.class})
    public static class pviewing_virtualchest extends AbstractFunction {

        public Exceptions.ExceptionType[] thrown() {
            return new Exceptions.ExceptionType[]{Exceptions.ExceptionType.FormatException, 
                Exceptions.ExceptionType.NullPointerException};
        }

        public boolean isRestricted() {
            return true;
        }

        public Boolean runAsync() {
            return false;
        }

        public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
            CArray arr = new CArray(t);
            String id = args[0].getValue();

            if (id.isEmpty() || args[0] instanceof CNull) {
                throw new ConfigRuntimeException("invalid id. Use either a string or integer.", Exceptions.ExceptionType.FormatException, t);
            }
            
            MCInventory inv = VirtualChests.get(id);
            
            if (inv == null) {
                throw new ConfigRuntimeException("unknown chest id. Please consult all_virtualchests().", Exceptions.ExceptionType.NullPointerException, t);
            }
                
            for (MCHumanEntity p : inv.getViewers()) {
                arr.push(new CString(p.getName(), t));
            }

            return arr;
        }

        public String getName() {
            return "pviewing_virtualchest";
        }

        public Integer[] numArgs() {
            return new Integer[]{1};
        }

        public String docs() {
            return "array {id} Returns the playernames of all players viewing a certain chest.";
        }

        public CHVersion since() {
            return CHVersion.V3_3_1;
        }
    }
}
