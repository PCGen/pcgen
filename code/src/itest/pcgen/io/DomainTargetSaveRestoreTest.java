/*
 * Copyright (c) 2013 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.io;

import pcgen.cdom.helper.ClassSource;
import pcgen.core.Domain;
import pcgen.core.PCClass;
import pcgen.io.testsupport.AbstractGlobalTargetedSaveRestoreTest;
import plugin.lsttokens.pcclass.HdToken;

public class DomainTargetSaveRestoreTest extends
		AbstractGlobalTargetedSaveRestoreTest<Domain>
{

	@Override
	public Class<Domain> getObjectClass()
	{
		return Domain.class;
	}

	@Override
	protected void applyObject(Domain obj)
	{
		PCClass cl =
				context.ref.silentlyGetConstructedCDOMObject(PCClass.class,
					"MyClass");
		pc.addClass(cl);
		pc.incrementClassLevel(1, cl);
		pc.setHP(pc.getActiveClassLevel(cl, 0), 4);
		pc.addDomain(obj, new ClassSource(cl, 1));
	}

	@Override
	protected Object prepare(Domain obj)
	{
		PCClass cl = create(PCClass.class, "MyClass");
		new HdToken().parseToken(context, cl, "6");
		return obj;
	}

	@Override
	protected void remove(Object o)
	{
		reloadedPC.removeDomain((Domain) o);
	}

}
