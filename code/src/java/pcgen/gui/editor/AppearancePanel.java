/*
 * AppearancePanel.java
 * Copyright 2003 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 *
 * Created on January 2, 2003, 12:00 PM
 *
 * @(#) $Id$
 */
package pcgen.gui.editor;

import pcgen.cdom.base.Constants;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.Race;
import pcgen.util.PropertyFactory;

import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

/**
 * <code>AppearancePanel</code>
 *
 * Manages the setting of non age related bio-settings.
 * These are: HAIR, EYES and SKINTONE
 *
 * @author  James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
final class AppearancePanel extends JPanel implements PObjectUpdater
{
	private static String defaultRegionName = Constants.NONE;
	private TypePanel pnlEyeColor;
	private TypePanel pnlHairColor;
	private TypePanel pnlSkinTone;

	/**
	 * Creates a new AppearancePanel
	 */
	AppearancePanel()
	{
		super();
		initComponents();
		initComponentContents();
	}

	/**
	 * Sets eye color list
	 * @param aList
	 * @param sort
	 */
	public void setEyeColorAvailableList(final List<String> aList, final boolean sort)
	{
		pnlEyeColor.setAvailableList(aList, sort);
	}

	/**
	 * Sets hair color list
	 * @param aList
	 * @param sort
	 */
	public void setHairColorAvailableList(final List aList, final boolean sort)
	{
		pnlHairColor.setAvailableList(aList, sort);
	}

	/**
	 * Sets skin tone list
	 * @param aList
	 * @param sort
	 */
	public void setSkinToneAvailableList(final List aList, final boolean sort)
	{
		pnlSkinTone.setAvailableList(aList, sort);
	}

	/* updateData takes the GUI components and updates the
	 * PObject obj with those values
	 */
	public void updateData(PObject obj)
	{
		Object[] sel;
		Race race;
		String region;
		String raceName;
		String aString;

		if (!(obj instanceof Race))
		{
			return;
		}

		race = (Race) obj;

		String[] unp = Globals.getContext().unparseSubtoken(race, "REGION");

		if (unp == null)
		{
			region = defaultRegionName;
		}
		else
		{
			region = unp[0];
		}

		raceName = race.getKeyName();

		sel = pnlHairColor.getSelectedList();
		aString = EditUtil.delimitArray(sel, '|');
		Globals.getBioSet().removeFromUserMap(region, raceName, "HAIR");
		Globals.getBioSet().addToUserMap(region, raceName, "HAIR:" + aString, 0);

		sel = pnlEyeColor.getSelectedList();
		aString = EditUtil.delimitArray(sel, '|');
		Globals.getBioSet().removeFromUserMap(region, raceName, "EYES");
		Globals.getBioSet().addToUserMap(region, raceName, "EYES:" + aString, 0);

		sel = pnlSkinTone.getSelectedList();
		aString = EditUtil.delimitArray(sel, '|');
		Globals.getBioSet().removeFromUserMap(region, raceName, "SKINTONE");
		Globals.getBioSet().addToUserMap(region, raceName, "SKINTONE:" + aString, 0);
	}

	/* updateView takes the values from PObject obj
	 * and updates the GUI components
	 */
	public void updateView(PObject obj)
	{
		Race race;
		String region;
		String raceName;

		if (!(obj instanceof Race))
		{
			return;
		}

		race = (Race) obj;

		String[] unp = Globals.getContext().unparseSubtoken(race, "REGION");

		if (unp == null)
		{
			region = defaultRegionName;
		}
		else
		{
			region = unp[0];
		}

		raceName = race.getKeyName();

		List aList = Globals.getBioSet().getTagForRace(region, raceName, "HAIR");
		pnlHairColor.setSelectedList(aList, true);
		aList = Globals.getBioSet().getTagForRace(region, raceName, "EYES");
		pnlEyeColor.setSelectedList(aList, true);
		aList = Globals.getBioSet().getTagForRace(region, raceName, "SKINTONE");
		pnlSkinTone.setSelectedList(aList, true);
	}

	private void initComponentContents()
	{
		// TODO This method currently does nothing?
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		GridBagConstraints gridBagConstraints;

		pnlHairColor = new TypePanel(PropertyFactory.getString("in_appNewHairColor"),
				PropertyFactory.getString("in_appHairColor"));
		pnlEyeColor = new TypePanel(PropertyFactory.getString("in_appNewEyeColor"),
				PropertyFactory.getString("in_appEyeColor"));
		pnlSkinTone = new TypePanel(PropertyFactory.getString("in_appNewSkintoneColor"),
				PropertyFactory.getString("in_appSkintoneColor"));

		this.setLayout(new GridBagLayout());

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 0.3;
		this.add(pnlHairColor, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 0.3;
		this.add(pnlEyeColor, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 0.3;
		this.add(pnlSkinTone, gridBagConstraints);
	}
}
