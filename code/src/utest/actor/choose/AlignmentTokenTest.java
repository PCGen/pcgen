/*
 * 
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
package actor.choose;

import pcgen.cdom.base.Chooser;
import pcgen.core.PCAlignment;
import plugin.lsttokens.choose.AlignmentToken;

import actor.testsupport.AbstractPersistentCDOMChoiceActorTestCase;

public class AlignmentTokenTest extends
		AbstractPersistentCDOMChoiceActorTestCase<PCAlignment>
{

	static AlignmentToken pca = new AlignmentToken();

	@Override
	public Chooser<PCAlignment> getActor()
	{
		return pca;
	}

	@Override
	public Class<PCAlignment> getCDOMClass()
	{
		return PCAlignment.class;
	}
}
