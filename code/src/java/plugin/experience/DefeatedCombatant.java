/*
 *  Initiative - A role playing utility to track turns
 *  Copyright (C) 2002 Devon D Jones
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *  The author of this program grants you the ability to use this code
 *  in conjunction with code that is covered under the Open Gaming License
 *
 *  XMLCombatant.java
 *
 *  Created on January 24, 2002, 11:15 AM
 *
 *  This file is Open Game Content, covered by the OGL.
 */
package plugin.experience;

import gmgen.plugin.Combatant;
import gmgen.plugin.SystemHP;
import gmgen.plugin.SystemInitiative;
import org.jdom.Element;

import pcgen.system.LanguageBundle;

import java.util.List;

/**
 *@author     devon
 *@since    March 20, 2003
 *@version $Revision$
 */
public class DefeatedCombatant extends Combatant
{
	/*
	 *  History:
	 *  March 20, 2003: Cleanup for Version 1.0
	 */
	protected String name;
	protected float cr;
	protected int xp;

	/**
	 *  Creates new Combatant
	 * @param name
	 * @param cr
	 */
	public DefeatedCombatant(String name, float cr)
	{
		setName(name);
		setCR(cr);
		this.init = new SystemInitiative();
		this.hitPoints = new SystemHP(1);
		kill();
	}

    @Override
	public void setCR(float cr)
	{
		this.cr = cr;
	}

    @Override
	public float getCR()
	{
		return cr;
	}

    @Override
	public void setName(String name)
	{
		this.name = name;
	}

    @Override
	public String getName()
	{
		return name;
	}

    @Override
	public String getPlayer()
	{
		return LanguageBundle.getString("in_gm"); //$NON-NLS-1$
	}

    @Override
	public Element getSaveElement()
	{
		return new Element("DefeatedCombatant");
	}

    @Override
	public void setXP(int xp)
	{
		this.xp = xp;
	}

    @Override
	public int getXP()
	{
		return xp;
	}

    @Override
	public void editRow(List<String> columnOrder, int colNumber, Object data)
	{
		// TODO:  Method doesn't do anything?
	}

    @Override
	public String toHtmlString()
	{
		return LanguageBundle.getFormattedString("in_plugin_xp_defeated", getName(), getPlayer()); //$NON-NLS-1$
	}
}
