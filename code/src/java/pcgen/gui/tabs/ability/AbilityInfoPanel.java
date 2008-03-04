/*
 * AbilityInfoPanel.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Current Ver: $Revision$
 * Last Editor: $Author: $
 * Last Edited: $Date$
 */
package pcgen.gui.tabs.ability;

import pcgen.core.Ability;
import pcgen.core.PlayerCharacter;
import pcgen.core.utils.CoreUtility;
import static pcgen.gui.HTMLUtils.*;
import pcgen.gui.utils.JLabelPane;
import pcgen.gui.utils.Utility;
import pcgen.util.PropertyFactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 */
public class AbilityInfoPanel extends JPanel
{
	private PlayerCharacter thePC;
	private Ability theAbility = null;

	private JLabelPane theInfoLabel = new JLabelPane();

	/**
	 * Constructs a panel for displaying information about an ability.
	 * 
	 * @param aPC The PC this ability could be associated with. (Used to check
	 * requirements)
	 * @param aTitle The title to display for this component.
	 */
	public AbilityInfoPanel(final PlayerCharacter aPC, final String aTitle)
	{
		super();
		thePC = aPC;

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c = new GridBagConstraints();
		setLayout(gridbag);

		final JScrollPane infoScroll = new JScrollPane();

		Utility.buildConstraints(c, 0, 0, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(infoScroll, c);

		final TitledBorder title1 = BorderFactory.createTitledBorder(aTitle);
		title1.setTitleJustification(TitledBorder.CENTER);
		infoScroll.setBorder(title1);
		theInfoLabel.setBackground(getBackground());
		infoScroll.setViewportView(theInfoLabel);
		Utility.setDescription(infoScroll, PropertyFactory
			.getString("in_infoScrollTip")); //$NON-NLS-1$

		add(infoScroll);
	}

	/**
	 * Sets the PlayerCharacter this panel is displaying information for.
	 * 
	 * @param aPC The PlayerCharacter to set.
	 */
	public void setPC(final PlayerCharacter aPC)
	{
		thePC = aPC;
	}

	/**
	 * Sets the <tt>Ability</tt> information will be displayed about.
	 * 
	 * @param anAbility The <tt>Ability</tt> to display
	 */
	public void setAbility(final Ability anAbility)
	{
		theAbility = anAbility;
		theInfoLabel.setText(getDisplayString());
	}

	private String getDisplayString()
	{
		if (theAbility == null)
		{
			return HTML + END_HTML;
		}

		final StringBuffer sb = new StringBuffer();
		sb.append(HTML).append(FONT_PLUS_1).append(BOLD);
		sb.append(theAbility.piSubString());
		sb.append(END_BOLD).append(END_FONT).append(BR);
		sb.append(PropertyFactory.getFormattedString(
			"in_clInfoType", //$NON-NLS-1$
			CoreUtility.join(theAbility.getTypeList(true), ". "))); //$NON-NLS-1$

		final String costStr = theAbility.getCostString();
		if (!costStr.equals("1")) //$NON-NLS-1$
		{
			sb.append(PropertyFactory.getFormattedString(
				"Ability.Info.Cost", //$NON-NLS-1$
				costStr));
		}

		if (theAbility.isMultiples())
		{
			sb.append(THREE_SPACES).append(
				PropertyFactory.getString("Ability.Info.Multiple")); //$NON-NLS-1$
		}

		if (theAbility.isStacks())
		{
			sb.append(THREE_SPACES).append(
				PropertyFactory.getString("Ability.Info.Stacks")); //$NON-NLS-1$
		}

		final String cString = theAbility.preReqHTMLStrings(thePC, false);

		if (cString.length() > 0)
		{
			sb.append(PropertyFactory.getFormattedString(
				"in_InfoRequirements", //$NON-NLS-1$
				cString));
		}

		sb.append(BR);
		sb.append(PropertyFactory.getFormattedString(
			"in_InfoDescription", //$NON-NLS-1$
			theAbility.piDescSubString(thePC)));

		final String bene = theAbility.getBenefits(thePC);
		if (bene != null && bene.length() > 0)
		{
			sb.append(BR);
			sb.append(PropertyFactory.getFormattedString(
				"Ability.Info.Benefit", //$NON-NLS-1$
				theAbility.getBenefits(thePC)));
		}
		
		sb.append(PropertyFactory.getFormattedString(
			"in_InfoSource", //$NON-NLS-1$
			theAbility.getDefaultSourceString()));

		sb.append(END_HTML);

		return sb.toString();
	}
}
