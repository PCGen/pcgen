/*
 * Copyright (c) 2012 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.facet;

import pcgen.cdom.facet.base.AbstractConditionalSpellFacet;
import pcgen.cdom.facet.base.AbstractConditionalSpellStorageFacet;

/**
 * The Class <code>ConditionalMasterSpellAvailableFacet</code> tracks
 * conditional available spells from the master lists (CLASSES or DOMAINS in the
 * Spell LST file)
 * 
 * @author Tom Parker <thpr@users.sourceforge.net>
 */
public class ConditionallyGrantedMasterAvailableSpellFacet extends
		AbstractConditionalSpellStorageFacet
{
	private ConditionalMasterAvailableSpellFacet conditionalMasterAvailableSpellFacet;

	private AvailableSpellFacet availableSpellFacet;

	@Override
	protected AbstractConditionalSpellFacet getConditionalFacet()
	{
		return conditionalMasterAvailableSpellFacet;
	}

	public void setConditionalMasterAvailableSpellFacet(
		ConditionalMasterAvailableSpellFacet conditionalMasterAvailableSpellFacet)
	{
		this.conditionalMasterAvailableSpellFacet =
				conditionalMasterAvailableSpellFacet;
	}

	public void setAvailableSpellFacet(AvailableSpellFacet availableSpellFacet)
	{
		this.availableSpellFacet = availableSpellFacet;
	}

	public void init()
	{
		addSpellChangeListener(availableSpellFacet);
	}
}
