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

import static pcgen.gui.HTMLUtils.BOLD;
import static pcgen.gui.HTMLUtils.BR;
import static pcgen.gui.HTMLUtils.END_BOLD;
import static pcgen.gui.HTMLUtils.END_FONT;
import static pcgen.gui.HTMLUtils.END_HTML;
import static pcgen.gui.HTMLUtils.FONT_PLUS_1;
import static pcgen.gui.HTMLUtils.HTML;
import static pcgen.gui.HTMLUtils.THREE_SPACES;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.math.BigDecimal;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.enumeration.AspectName;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.BenefitFormatting;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.DescriptionFormatting;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.gui.utils.JLabelPane;
import pcgen.gui.utils.Utility;
import pcgen.system.LanguageBundle;

/**
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 */
@SuppressWarnings("serial")
public class AbilityInfoPanel extends JPanel
{
	private PlayerCharacter thePC;
	private Ability theAbility = null;

	private JLabelPane theInfoLabel = new JLabelPane();
	private final TitledBorder border;

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

		border = BorderFactory.createTitledBorder(aTitle);
		border.setTitleJustification(TitledBorder.CENTER);
		infoScroll.setBorder(border);
		theInfoLabel.setBackground(getBackground());
		infoScroll.setViewportView(theInfoLabel);
		Utility.setDescription(infoScroll, LanguageBundle
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

	public void setCategory(final AbilityCategory cat)
	{
		border.setTitle(LanguageBundle.getFormattedString("InfoAbility.Title",
				cat.getDisplayName()));
		repaint();
	}

	private String getDisplayString()
	{
		if (theAbility == null)
		{
			return HTML + END_HTML;
		}

		final StringBuffer sb = new StringBuffer();
		sb.append(HTML).append(FONT_PLUS_1).append(BOLD);
		sb.append(OutputNameFormatting.piString(theAbility, false));
		sb.append(END_BOLD).append(END_FONT).append(BR);
		sb.append(LanguageBundle.getFormattedString(
			"Ability.Info.Type", //$NON-NLS-1$
			StringUtil.join(theAbility.getTrueTypeList(true), ". "))); //$NON-NLS-1$

		BigDecimal costStr = theAbility.getSafe(ObjectKey.SELECTION_COST);
		if (!costStr.equals(BigDecimal.ONE)) //$NON-NLS-1$
		{
			sb.append(LanguageBundle.getFormattedString(
				"Ability.Info.Cost", //$NON-NLS-1$
				costStr));
		}

		if (theAbility.getSafe(ObjectKey.MULTIPLE_ALLOWED))
		{
			sb.append(THREE_SPACES).append(
				LanguageBundle.getString("Ability.Info.Multiple")); //$NON-NLS-1$
		}

		if (theAbility.getSafe(ObjectKey.STACKS))
		{
			sb.append(THREE_SPACES).append(
				LanguageBundle.getString("Ability.Info.Stacks")); //$NON-NLS-1$
		}

		final String cString = PrerequisiteUtilities.preReqHTMLStringsForList(thePC, null,
		theAbility.getPrerequisiteList(), false);

		if (cString.length() > 0)
		{
			sb.append(LanguageBundle.getFormattedString(
				"in_InfoRequirements", //$NON-NLS-1$
				cString));
		}

		sb.append(BR);
		sb.append(LanguageBundle.getFormattedString(
			"in_InfoDescription", //$NON-NLS-1$
			DescriptionFormatting.piDescSubString(thePC, theAbility)));

		if (theAbility.getSafeSizeOfMapFor(MapKey.ASPECT) > 0)
		{
			Set<AspectName> aspectKeys = theAbility.getKeysFor(MapKey.ASPECT);
			StringBuffer buff = new StringBuffer();
			for (AspectName key : aspectKeys)
			{
				if (buff.length() > 0)
				{
					buff.append(", ");
				}
				buff.append(theAbility.printAspect(thePC, key));
			}
			sb.append(BR);
			sb.append(LanguageBundle.getFormattedString(
				"Ability.Info.Aspects", //$NON-NLS-1$
				buff.toString()));
		}
		
		final String bene = BenefitFormatting.getBenefits(thePC, theAbility);
		if (bene != null && bene.length() > 0)
		{
			sb.append(BR);
			sb.append(LanguageBundle.getFormattedString(
				"Ability.Info.Benefit", //$NON-NLS-1$
				BenefitFormatting.getBenefits(thePC, theAbility)));
		}
		
		sb.append(LanguageBundle.getFormattedString(
			"in_InfoSource", //$NON-NLS-1$
			SourceFormat.getFormattedString(theAbility,
			Globals.getSourceDisplay(), true)));

		sb.append(END_HTML);

		return sb.toString();
	}
}
