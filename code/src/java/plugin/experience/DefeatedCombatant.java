/*
 *  Copyright (C) 2002 Devon Jones
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *  DefeatedCombatant.java
 */
package plugin.experience;

import java.util.List;

import gmgen.plugin.Combatant;
import gmgen.plugin.SystemHP;
import gmgen.plugin.SystemInitiative;
import pcgen.system.LanguageBundle;

import org.jdom2.Element;

public class DefeatedCombatant extends Combatant
{
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
