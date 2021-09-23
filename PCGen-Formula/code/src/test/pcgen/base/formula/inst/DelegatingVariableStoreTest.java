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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.ScopeInstanceFactory;
import pcgen.base.formula.base.VariableID;
import pcgen.base.testsupport.GlobalVarScoped;
import pcgen.base.testsupport.NaiveScopeManager;

public class DelegatingVariableStoreTest
{

	private NaiveScopeManager scopeManager;
	private ScopeInstanceFactory instanceFactory;
	private VariableChangeEvent<?> lastEvent;

	@BeforeEach
	void setUp()
	{
		scopeManager = new NaiveScopeManager();
		instanceFactory = new SimpleScopeInstanceFactory(scopeManager);
		lastEvent = null;
	}

	@AfterEach
	void tearDown()
	{
		scopeManager = null;
		instanceFactory = null;
		lastEvent = null;
	}

	@Test
	public void testAddRemove()
	{
		MonitorableVariableStore backingStore = new MonitorableVariableStore();
		DelegatingVariableStore varStore = new DelegatingVariableStore(backingStore);
		
		ScopeInstance globalInst = instanceFactory.get("Global", new GlobalVarScoped("Global"));
		ScopeInstance staticInst = instanceFactory.get("Static", new GlobalVarScoped("Static"));

		VariableID<Number> varID = new VariableID<>(globalInst,
				FormatUtilities.NUMBER_MANAGER, "test");
		VariableID<Number> backID = new VariableID<>(staticInst,
				FormatUtilities.NUMBER_MANAGER, "backtest");

		VariableListener<Number> listener = new EventCapture();
		varStore.addVariableListener(varID, listener);
		varStore.addVariableListener(backID, listener);

		varStore.put(varID, 5);
		assertNull(lastEvent.getOldValue());
		assertEquals(lastEvent.getNewValue(), 5);

		varStore.put(varID, 7);
		assertEquals(lastEvent.getOldValue(), 5);
		assertEquals(lastEvent.getNewValue(), 7);

		//No new event	
		varStore.put(backID, 2);
		assertNull(lastEvent.getOldValue());
		assertEquals(lastEvent.getNewValue(), 2);
		VariableChangeEvent<?> capture = lastEvent;
		
		varStore.removeVariableListener(backID, listener);
		//No longer receive event
		backingStore.put(backID, 3);
		assertSame(capture, lastEvent);
	}

	private final class EventCapture implements VariableListener<Number>
	{
		@Override
		public void variableChanged(VariableChangeEvent<Number> vcEvent)
		{
			lastEvent = vcEvent;
		}
	}
}
