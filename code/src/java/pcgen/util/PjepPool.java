/*
 * Copyright 2004 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public final class PjepPool
{
	private Stack<PJEP> freeStack = new Stack<>();
	private List<PJEP> usedList = new ArrayList<>();
	private static PjepPool instance = new PjepPool();

	private PjepPool()
	{
		// Do Nothing
	}

	public static PjepPool getInstance()
	{
		return instance;
	}

	public synchronized void initialise()
	{
		freeStack.push(new PJEP());
	}

	public synchronized PJEP aquire()
	{
		return aquire(null, "");
	}

	public synchronized PJEP aquire(final Object parent, String variableSource)
	{
		//pcgen.util.Logging.log(java.util.logging.Level.FINE, "aquireJep()");
		PJEP jep;
		if (!freeStack.isEmpty())
		{
			jep = freeStack.pop();
		}
		else
		{
			jep = new PJEP();
			//pcgen.util.Logging.errorPrint("aquirePJep() - creating new parser");
		}

		usedList.add(jep);
		jep.initSymTab();
		jep.setVariableSource(variableSource);
		jep.setParent(parent);
		return jep;
	}

	public synchronized void release(PJEP interp)
	{
		//pcgen.util.Logging.log(java.util.logging.Level.FINE, "releaseJep( " + interp + " )");
		if (usedList.contains(interp))
		{
			usedList.remove(interp);
		}
		else
		{
			pcgen.util.Logging.errorPrint("Tried to release a PJEP instance that we did not aquire...");
		}
		interp.setParent(null);
		freeStack.push(interp);
	}

	public synchronized void dumpStats()
	{
		pcgen.util.Logging.log(java.util.logging.Level.FINE, "PJEP Pool: ");
		pcgen.util.Logging.log(java.util.logging.Level.FINE, "    Currently Unused: " + freeStack.size());
		pcgen.util.Logging.log(java.util.logging.Level.FINE, "    Currently Used  : " + usedList.size());
	}
}
