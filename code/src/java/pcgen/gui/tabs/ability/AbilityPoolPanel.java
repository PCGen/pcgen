/*
 * AbilityPoolPanel.java
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

import java.awt.FlowLayout;
import java.math.BigDecimal;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.gui.utils.Utility;
import pcgen.util.BigDecimalHelper;
import pcgen.util.PropertyFactory;

/**
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 */
public class AbilityPoolPanel extends JPanel
{
	private PlayerCharacter thePC;
	private AbilityCategory theCategory;
	private JTextField theNumAbilitiesField = new JTextField();

	/**
	 * Construct the panel and add all the components.
	 * 
	 * @param aPC The PC
	 * @param aCategory The <tt>AbilityCategory</tt> this panel represents.
	 */
	public AbilityPoolPanel(final PlayerCharacter aPC,
		final AbilityCategory aCategory)
	{
		super();
		thePC = aPC;
		theCategory = aCategory;

		setLayout(new FlowLayout());
		final JLabel abilitiesRemainingLabel = new JLabel();
		abilitiesRemainingLabel.setText(PropertyFactory.getFormattedString(
			"InfoAbility.Remaining.Label", theCategory)); //$NON-NLS-1$
		add(abilitiesRemainingLabel);

		theNumAbilitiesField.setInputVerifier(new InputVerifier()
		{
			@Override
			public boolean verify(final JComponent input)
			{
				final String text = ((JTextField) input).getText();
				if (text.length() > 0)
				{
					try
					{
						if (theCategory.allowFractionalPool() == false)
						{
							Integer.parseInt(text);
							return true;
						}
						Double.parseDouble(text);
						return true;
					}
					catch (Exception e)
					{
						return false;
					}
				}
				return true;
			}

			@Override
			public boolean shouldYieldFocus(final JComponent input)
			{
				final boolean valueOk = super.shouldYieldFocus(input);
				if (!valueOk)
				{
					getToolkit().beep();
				}
				else
				{
					if (theNumAbilitiesField.getText().length() > 0)
					{
						final BigDecimal expectedValue =
								thePC.getAvailableAbilityPool(theCategory);
						final BigDecimal newValue =
								new BigDecimal(theNumAbilitiesField.getText());
						thePC.adjustAbilities(theCategory, newValue
							.subtract(expectedValue));
					}
					else
					{
						showRemainingAbilityPoints();
					}
				}
				return valueOk;
			}
		});

		showRemainingAbilityPoints();
		theNumAbilitiesField.setColumns(3);
		theNumAbilitiesField.setEditable(aCategory.allowPoolMod());
		Utility.setDescription(theNumAbilitiesField, PropertyFactory
			.getFormattedString("InfoAbility.Pool.Description", //$NON-NLS-1$
				theCategory.getDisplayName()));

		add(theNumAbilitiesField);
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
	 * Displays the current number of remaining points in the ability pool.
	 */
	public void showRemainingAbilityPoints()
	{
		//		theNumAbilitiesField.setText(BigDecimalHelper.trimBigDecimal(new BigDecimal(thePC.getFeats())).toString());
		theNumAbilitiesField.setText(BigDecimalHelper.trimBigDecimal(
			thePC.getAvailableAbilityPool(theCategory)).toString());
	}
}
