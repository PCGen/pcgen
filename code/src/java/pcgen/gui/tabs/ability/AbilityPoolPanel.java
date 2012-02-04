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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.InputVerifier;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.gui.utils.Utility;
import pcgen.util.BigDecimalHelper;
import pcgen.system.LanguageBundle;

/**
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 */
public class AbilityPoolPanel extends JPanel
{
	private PlayerCharacter thePC;
	private AbilityCategory theCategory;

	private JComboBox theCategoryField = new JComboBox();
	private JTextField theNumAbilitiesField = new JTextField();

	/** A list of listeners registered to receive ability category selection events */
	private List<IAbilityCategorySelectionListener> theCatListeners =
			new ArrayList<IAbilityCategorySelectionListener>();

	private ActionListener categoryListener;
	
	/**
	 * Construct the panel and add all the components.
	 * 
	 * @param aPC The PC
	 * @param aCategoryList The <tt>AbilityCategory</tt> this panel represents.
	 */
	public AbilityPoolPanel(final PlayerCharacter aPC, 
		final Collection<AbilityCategory> aCategoryList)
	{
		super();
		thePC = aPC;

		setLayout(new FlowLayout());
		for (AbilityCategory abilityCategory : aCategoryList)
		{
			theCategoryField.addItem(new AbilityCatDisplay(abilityCategory));
		}
		add(theCategoryField);
		theCategory =
				((AbilityCatDisplay) theCategoryField.getSelectedItem()).abilityCat;
		
		final JLabel abilitiesRemainingLabel = new JLabel();
		abilitiesRemainingLabel.setText(LanguageBundle.getFormattedString(
			"InfoAbility.Remaining.Label", "")); //$NON-NLS-1$
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
		
		categoryListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				categoryFieldActionPerformed();
			}
		};
		theCategoryField.addActionListener(categoryListener);

		showRemainingAbilityPoints();
		theNumAbilitiesField.setColumns(3);
		theNumAbilitiesField.setEditable(theCategory.allowPoolMod());
		Utility.setDescription(theNumAbilitiesField, LanguageBundle
			.getFormattedString("InfoAbility.Pool.Description", //$NON-NLS-1$
				theCategory.getDisplayName()));

		add(theNumAbilitiesField);
	}

	/**
	 * Update the panel based on a change to the  selected ability 
	 * category.
	 */
	private void categoryFieldActionPerformed()
	{
		// Get the selected category
		final AbilityCategory aCategory =
			((AbilityCatDisplay) theCategoryField.getSelectedItem()).abilityCat;
		
		// Tell our parent about the change
		for (final IAbilityCategorySelectionListener listener : getCategoryListeners())
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					listener.abilityCategorySelected(aCategory);
				}
			});
		}
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
	 * Sets the category this panel is displaying information for.
	 * 
	 * @param aCategory the new category
	 */
	public void setCategory(final AbilityCategory aCategory)
	{
		theCategory = aCategory;
		theNumAbilitiesField.setEditable(theCategory.allowPoolMod());
		Utility.setDescription(theNumAbilitiesField, LanguageBundle
			.getFormattedString("InfoAbility.Pool.Description", //$NON-NLS-1$
				theCategory.getDisplayName()));
		
		theCategoryField.removeActionListener(categoryListener);
		for (int i = 0; i < theCategoryField.getItemCount(); i++)
		{
			AbilityCatDisplay acd = (AbilityCatDisplay) theCategoryField.getItemAt(i);
			if (acd.abilityCat == aCategory)
			{
				theCategoryField.setSelectedIndex(i);
				break;
			}
		}
		theCategoryField.addActionListener(categoryListener);
		
	}
	
	/**
	 * Displays the current number of remaining points in the ability pool.
	 */
	public void showRemainingAbilityPoints()
	{
		theNumAbilitiesField.setText(BigDecimalHelper.trimBigDecimal(
			thePC.getAvailableAbilityPool(theCategory)).toString());
	}

	/**
	 * Adds a new ability category selection listener to the panel that 
	 * will be advised of ability category selection events that occur 
	 * within the panel.
	 * 
	 * @param aListener the listener
	 */
	public void addAbilityCategorySelectionListener(
		final IAbilityCategorySelectionListener aListener)
	{
		if (theCatListeners.contains(aListener) == false)
		{
			theCatListeners.add(aListener);
		}
	}

	/**
	 * Gets the ability category listeners.
	 * 
	 * @return An unmodifiable list of the category listeners
	 */
	public List<IAbilityCategorySelectionListener> getCategoryListeners()
	{
		return Collections.unmodifiableList(theCatListeners);
	}	
	/**
	 * <code>AbilityCatDisplay</code> encapsulates an ability category so that 
	 * its plural name can be used for display.
	 *
	 */
	private class AbilityCatDisplay
	{
		public final AbilityCategory abilityCat;
		
		public AbilityCatDisplay(AbilityCategory abilityCat)
		{
			this.abilityCat = abilityCat;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			return abilityCat.getPluralName();
		}
	}
}
