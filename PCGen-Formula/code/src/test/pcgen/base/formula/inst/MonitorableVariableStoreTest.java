/*
 * Copyright 2020 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.formula.inst;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VariableID;
import pcgen.base.testsupport.GlobalVarScoped;
import pcgen.base.testsupport.NaiveScopeManager;

public class MonitorableVariableStoreTest
{

	private NaiveScopeManager scopeManager;
	private ScopeInstanceFactory instanceFactory;
	private VariableChangeEvent<?> lastEvent;
	private List<EventCapture> order;

	@BeforeEach
	void setUp()
	{
		scopeManager = new NaiveScopeManager();
		instanceFactory = new SimpleScopeInstanceFactory(scopeManager);
		order = new ArrayList<>();
		lastEvent = null;
	}

	@AfterEach
	void tearDown()
	{
		scopeManager = null;
		instanceFactory = null;
		lastEvent = null;
		order = null;
	}

	@Test
	public void testAddRemove()
	{
		MonitorableVariableStore varStore = new MonitorableVariableStore();
		ScopeInstance globalInst = instanceFactory.get("Global", new GlobalVarScoped("Global"));
		VariableID<Number> varID = new VariableID<>(globalInst,
			FormatUtilities.NUMBER_MANAGER, "test");
		VariableListener<Number> listener = new EventCapture();
		varStore.addVariableListener(varID, listener);
		varStore.put(varID, 5);
		assertNull(lastEvent.getOldValue());
		assertEquals(lastEvent.getNewValue(), 5);
		//Useless remove
		varStore.removeVariableListener(varID, new EventCapture());
		varStore.put(varID, 7);
		assertEquals(lastEvent.getOldValue(), 5);
		assertEquals(lastEvent.getNewValue(), 7);
		VariableChangeEvent<?> capture = lastEvent;
		//No new event	
		varStore.put(varID, 7);
		assertSame(capture, lastEvent);
		varStore.removeVariableListener(varID, listener);
		//No longer receive event
		varStore.put(varID, 3);
		assertSame(capture, lastEvent);
	}

	@Test
	public void testPriority()
	{
		MonitorableVariableStore varStore = new MonitorableVariableStore();
		ScopeInstance globalInst = instanceFactory.get("Global", new GlobalVarScoped("Global"));
		VariableID<Number> varID = new VariableID<>(globalInst,
			FormatUtilities.NUMBER_MANAGER, "test");
		VariableID<Number> otherID = new VariableID<>(globalInst,
			FormatUtilities.NUMBER_MANAGER, "other");
		VariableListener<Number> listener1 = new EventCapture();
		VariableListener<Number> listener2 = new EventCapture();
		varStore.addVariableListener(100, varID, listener1);
		varStore.addVariableListener(200, varID, listener2);
		varStore.put(varID, 5);
		assertNull(lastEvent.getOldValue());
		assertEquals(5, lastEvent.getNewValue());
		//Lower priority goes first
		assertEquals(listener1, order.get(0));
		assertEquals(listener2, order.get(1));
		varStore.put(varID, 7);
		assertEquals(5, lastEvent.getOldValue());
		assertEquals(7, lastEvent.getNewValue());
		//Useless remove
		varStore.removeVariableListener(50, varID, listener1);
		varStore.put(varID, 9);
		assertEquals(7, lastEvent.getOldValue());
		assertEquals(9, lastEvent.getNewValue());
		VariableChangeEvent<?> capture = lastEvent;
		//No new event	
		varStore.put(varID, 9);
		assertSame(capture, lastEvent);
		//Won't work (wrong priority)
		varStore.removeVariableListener(100, varID, listener1);
		order.clear();
		//No longer receive event at listener1, but still at listener2
		varStore.put(varID, 3);
		capture = lastEvent;
		assertEquals(9, lastEvent.getOldValue());
		assertEquals(3, lastEvent.getNewValue());
		assertFalse(order.contains(listener1));
		assertTrue(order.contains(listener2));
		order.clear();
		//Don't receive for otherID
		varStore.put(otherID, 5);
		assertSame(capture, lastEvent);
		assertTrue(order.isEmpty());
	}

	private final class EventCapture implements VariableListener<Number>
	{
		@Override
		public void variableChanged(VariableChangeEvent<Number> vcEvent)
		{
			lastEvent = vcEvent;
			order.add(this);
		}
	}
}
