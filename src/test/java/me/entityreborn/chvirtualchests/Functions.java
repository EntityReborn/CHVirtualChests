package me.entityreborn.chvirtualchests;

import com.laytonsmith.PureUtilities.ClassDiscovery;
import com.laytonsmith.PureUtilities.ReflectionUtils;
import com.laytonsmith.abstraction.MCInventory;
import com.laytonsmith.abstraction.MCPlayer;
import com.laytonsmith.abstraction.MCServer;
import com.laytonsmith.abstraction.MCItemStack;
import com.laytonsmith.core.ObjectGenerator;
import com.laytonsmith.core.exceptions.ConfigCompileException;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 *
 * @author Layton
 */
@RunWith(PowerMockRunner.class)
public class Functions {

    static MCPlayer fakePlayer;
    static VirtualChests vc;
    static MCInventory inv;

    public Functions() {
    }
    
    @BeforeClass
    public static void setup() throws Exception {
        // Add this extension to searchable locations.
        File dir = new File("target", "classes");
        String path = dir.toURI().getPath();
        ClassDiscovery.InstallDiscoveryLocation("file://" + path);
        
        // Add test classes to searchable locations.
        dir = new File("target", "test-classes");
        path = dir.toURI().getPath();
        ClassDiscovery.InstallDiscoveryLocation("file://" + path);
        
        fakePlayer = StaticTest.GetOnlinePlayer();
        
        MCServer srv = StaticTest.GetFakeServer();
        when(srv.createInventory(any(VirtualHolder.class), anyInt(), anyString())).thenAnswer(new Answer<MCInventory>() {

            public MCInventory answer(InvocationOnMock invocation) throws Throwable {
                String id = ((VirtualHolder)invocation.getArguments()[0]).getID();
                final Integer sz = (Integer)invocation.getArguments()[1];
                final String ttl = (String)invocation.getArguments()[2];
                inv = new TestInventory(id) {
                    public int getSize() {
                        return sz;
                    }
                    public String getTitle() {
                        return ttl;
                    }
                };
                return inv;
            }
            
        });
        
        fakePlayer = StaticTest.GetOnlinePlayer("wraithguard01", srv);
        when(fakePlayer.getServer()).thenReturn(srv);
        String name = fakePlayer.getName();
        when(srv.getPlayer(name)).thenReturn(fakePlayer);
    }
    
    @Before
    public void before() {
        Set<String> all = new HashSet<String>(VirtualChests.getAll());
        for (String id : all) {
            VirtualChests.del(id);
        }
    }
    
    @Test
    public void testPOpen() throws ConfigCompileException {
        StaticTest.SRun("create_virtualchest(array('id':'test')) \\ popen_virtualchest('test')", fakePlayer);
        verify(fakePlayer).openInventory(VirtualChests.get("test"));
    }
    
    @Test
    public void testPOpenOther() throws ConfigCompileException, Exception {
        StaticTest.SRun("create_virtualchest(array('id':'test')) \\ popen_virtualchest('wraithguard01', 'test')", fakePlayer);
        // fakePlayer is already 'wraithguard01' here.
        verify(fakePlayer).openInventory(VirtualChests.get("test"));
    }

    @Test
    public void testCreate() throws ConfigCompileException {
        StaticTest.SRun("create_virtualchest(array('id':'test'))", fakePlayer);

        inv = VirtualChests.get("test");
        
        assertEquals(inv.getSize(), 54);
        assertEquals(inv.getTitle(), "Virtual Chest");
    }
    
    @Test
    public void testAdvCreate() throws ConfigCompileException {
        assertFalse(VirtualChests.getAll().contains("test"));
        StaticTest.SRun("create_virtualchest(array('id':'test', 'size':45, 'title':'Virtual Chest!!!'))", fakePlayer);
        assertTrue(VirtualChests.getAll().contains("test"));
        
        inv = VirtualChests.get("test");
        
        assertEquals(inv.getSize(), 45);
        assertEquals(inv.getTitle(), "Virtual Chest!!!");
    }
    
    @Test
    public void testCreateWItems() throws ConfigCompileException {
        StaticTest.SRun("create_virtualchest(array('id':'test', '1':array('type':1, 'qty':5)))", fakePlayer);
        
        inv = VirtualChests.get("test");
        
        MCItemStack is = inv.getItem(1);
        
        assertEquals(is.getTypeId(), 1);
        assertEquals(is.getAmount(), 5);
    }
    
    @Test
    public void testGet() throws ConfigCompileException {
        StaticTest.SRun("set_virtualchest(array('id':'test', '1':array('type':1, 'qty':5)))", fakePlayer);
        String s = StaticTest.SRun("get_virtualchest('test')", fakePlayer);
        System.out.println(s);
    }
    
    @Test
    public void testSet1() throws ConfigCompileException {
        StaticTest.SRun("set_virtualchest(array('id':'test', '1':array('type':1, 'qty':5)))", fakePlayer);
        
        inv = VirtualChests.get("test");
        
        MCItemStack is = inv.getItem(1);
        
        assertEquals(is.getTypeId(), 1);
        assertEquals(is.getAmount(), 5);
        
        StaticTest.SRun("set_virtualchest(array('id':'test', '2':array('type':1, 'qty':5)))", fakePlayer);
        
        inv = VirtualChests.get("test");
        
        is = inv.getItem(1);
        
        assertEquals(is.getTypeId(), 0);
        
        is = inv.getItem(2);
        
        assertEquals(is.getTypeId(), 1);
        assertEquals(is.getAmount(), 5);
    }
    
    @Test
    public void testSet2() throws ConfigCompileException {
        StaticTest.SRun("set_virtualchest('test', array('1':array('type':1, 'qty':5)))", fakePlayer);
        
        inv = VirtualChests.get("test");
        
        MCItemStack is = inv.getItem(1);
        
        assertEquals(is.getTypeId(), 1);
        assertEquals(is.getAmount(), 5);
        
        StaticTest.SRun("set_virtualchest('test', array('2':array('type':1, 'qty':5)))", fakePlayer);
        
        inv = VirtualChests.get("test");
        
        is = inv.getItem(1);
        
        assertEquals(is.getTypeId(), 0);
        
        is = inv.getItem(2);
        
        assertEquals(is.getTypeId(), 1);
        assertEquals(is.getAmount(), 5);
    }
    
    @Test
    public void testUpdate1() throws ConfigCompileException {
        MCItemStack is;
        StaticTest.SRun("set_virtualchest(array('id':'test', '1':array('type':1, 'qty':5)))", fakePlayer);
        StaticTest.SRun("update_virtualchest(array('id':'test', '2':array('type':3, 'qty':5)))", fakePlayer);
        
        inv = VirtualChests.get("test");
        
        is = inv.getItem(1);
        
        assertEquals(is.getTypeId(), 1);
        assertEquals(is.getAmount(), 5);
        
        is = inv.getItem(2);
        
        assertEquals(is.getTypeId(), 3);
        assertEquals(is.getAmount(), 5);
    }
    
    @Test
    public void testUpdate2() throws ConfigCompileException {
        MCItemStack is;
        StaticTest.SRun("set_virtualchest(array('id':'test', '1':array('type':1, 'qty':5)))", fakePlayer);
        StaticTest.SRun("update_virtualchest('test', array('2':array('type':3, 'qty':5)))", fakePlayer);
        
        inv = VirtualChests.get("test");
        
        is = inv.getItem(1);
        
        assertEquals(is.getTypeId(), 1);
        assertEquals(is.getAmount(), 5);
        
        is = inv.getItem(2);
        
        assertEquals(is.getTypeId(), 3);
        assertEquals(is.getAmount(), 5);
    }
}
