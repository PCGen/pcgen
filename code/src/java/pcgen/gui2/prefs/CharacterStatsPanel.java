/*
 * Copyright 2008 (C) James Dempsey
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
 */
package pcgen.gui2.prefs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import pcgen.cdom.base.Constants;
import pcgen.cdom.content.RollMethod;
import pcgen.core.GameMode;
import pcgen.core.PointBuyMethod;
import pcgen.core.SettingsHandler;
import pcgen.gui2.tools.Utility;
import pcgen.gui2.util.JComboBoxEx;
import pcgen.system.LanguageBundle;

/**
 * The Class {@code CharacterStatsPanel} is responsible for managing
 * the character stats preferences.
 * 
 * 
 */
@SuppressWarnings("serial")
public class CharacterStatsPanel extends PCGenPrefsPanel
{

	private static final String IN_ABILITIES = LanguageBundle.getString("in_Prefs_abilities");
	private String[] pMode;
	private String[] pModeMethodName;
	private JDialog parent;

	private JRadioButton abilitiesAllSameButton;
	private JRadioButton abilitiesPurchasedButton;
	private JRadioButton abilitiesRolledButton;
	private JRadioButton abilitiesUserRolledButton;
	private JComboBoxEx<String> abilityPurchaseModeCombo;
	private JComboBoxEx<String> abilityRolledModeCombo = null;
	private JComboBoxEx<String> abilityScoreCombo;
	private PurchaseModeFrame pmsFrame = null;

	private ActionListener rolledModeListener;
	private ActionListener purchaseModeListener;
	private ActionListener scoreListener;

	/**
	 * Instantiates a new character stats panel.
	 * 
	 * @param parent the parent dialog
	 */
	public CharacterStatsPanel(JDialog parent)
	{
		this.parent = parent;

		initComponents();

		addAbilitiesPanelListeners();
	}

	/**
	 * Build and initialise the user interface.
	 */
	private void initComponents()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		ButtonGroup exclusiveGroup;
		Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, IN_ABILITIES);

		title1.setTitleJustification(TitledBorder.LEFT);
		this.setBorder(title1);
		this.setLayout(gridbag);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(2, 2, 2, 2);

		final GameMode gameMode = SettingsHandler.getGame();

		int row = 0;

		exclusiveGroup = new ButtonGroup();
		Utility.buildConstraints(c, 0, row++, 3, 1, 0, 0);
		label = new JLabel(LanguageBundle.getFormattedString(
			"in_Prefs_abilitiesGenLabel", gameMode.getDisplayName())); //$NON-NLS-1$
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 0, row, 1, 1, 0, 0);
		label = new JLabel("  ");
		gridbag.setConstraints(label, c);
		this.add(label);

		Utility.buildConstraints(c, 1, row++, 2, 1, 0, 0);
		abilitiesUserRolledButton = new JRadioButton(LanguageBundle.getString("in_Prefs_abilitiesUserRolled"));
		gridbag.setConstraints(abilitiesUserRolledButton, c);
		this.add(abilitiesUserRolledButton);
		exclusiveGroup.add(abilitiesUserRolledButton);

		Utility.buildConstraints(c, 1, row++, 2, 1, 0, 0);
		abilitiesAllSameButton = new JRadioButton(LanguageBundle.getString("in_Prefs_abilitiesAllSame") + ": ");
		gridbag.setConstraints(abilitiesAllSameButton, c);
		this.add(abilitiesAllSameButton);
		exclusiveGroup.add(abilitiesAllSameButton);
		Utility.buildConstraints(c, 1, row, 1, 1, 0, 0);
		label = new JLabel("  ");
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 2, row++, 2, 1, 0, 0);

		abilityScoreCombo = new JComboBoxEx<>();
		for (int i = gameMode.getStatMin(); i <= gameMode.getStatMax(); ++i)
		{
			abilityScoreCombo.addItem(String.valueOf(i));
		}

		gridbag.setConstraints(abilityScoreCombo, c);
		this.add(abilityScoreCombo);

		List<RollMethod> rollMethods =
				gameMode.getModeContext().getReferenceContext().getSortkeySortedCDOMObjects(RollMethod.class);
		if (!rollMethods.isEmpty())
		{
			Utility.buildConstraints(c, 1, row++, 2, 1, 0, 0);
			abilitiesRolledButton = new JRadioButton("Rolled:");
			gridbag.setConstraints(abilitiesRolledButton, c);
			this.add(abilitiesRolledButton);
			exclusiveGroup.add(abilitiesRolledButton);
			Utility.buildConstraints(c, 2, row++, 2, 1, 0, 0);

			abilityRolledModeCombo = new JComboBoxEx<>();

			for (RollMethod rm : rollMethods)
			{
				abilityRolledModeCombo.addItem(rm.getDisplayName());
			}

			gridbag.setConstraints(abilityRolledModeCombo, c);
			this.add(abilityRolledModeCombo);
		}

		Collection<PointBuyMethod> methods = SettingsHandler.getGame().getModeContext().getReferenceContext()
			.getConstructedCDOMObjects(PointBuyMethod.class);
		final int purchaseMethodCount = methods.size();
		Utility.buildConstraints(c, 1, row++, 2, 1, 0, 0);
		abilitiesPurchasedButton = new JRadioButton(LanguageBundle.getString("in_Prefs_abilitiesPurchased") + ": ");
		gridbag.setConstraints(abilitiesPurchasedButton, c);
		this.add(abilitiesPurchasedButton);
		exclusiveGroup.add(abilitiesPurchasedButton);
		Utility.buildConstraints(c, 2, row++, 2, 1, 0, 0);

		pMode = new String[purchaseMethodCount];
		pModeMethodName = new String[purchaseMethodCount];

		int i = 0;
		for (PointBuyMethod pbm : methods)
		{
			pMode[i] = pbm.getDescription();
			pModeMethodName[i] = pbm.getDisplayName();
			i++;
		}

		abilityPurchaseModeCombo = new JComboBoxEx<>(pMode);

		gridbag.setConstraints(abilityPurchaseModeCombo, c);
		this.add(abilityPurchaseModeCombo);

		//
		// Hide controls if there are no entries to select
		//
		if (purchaseMethodCount == 0)
		{
			abilityPurchaseModeCombo.setVisible(false);
			abilitiesPurchasedButton.setVisible(false);
		}

		Utility.buildConstraints(c, 1, row++, 1, 1, 0, 0);
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 1, row++, 3, 1, 0, 0);
		JButton purchaseModeButton = new JButton(LanguageBundle.getString("in_Prefs_purchaseModeConfig"));
		gridbag.setConstraints(purchaseModeButton, c);
		this.add(purchaseModeButton);
		purchaseModeButton.addActionListener(new PurchaseModeButtonListener());

		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		this.add(label);
	}

	@Override
	public void applyOptionValuesToControls()
	{
		stopListeners();

		final GameMode gameMode = SettingsHandler.getGame();
		boolean bValid = true;
		final int rollMethod = gameMode.getRollMethod();

		switch (rollMethod)
		{
			case Constants.CHARACTER_STAT_METHOD_USER:
				abilitiesUserRolledButton.setSelected(true);

				break;

			case Constants.CHARACTER_STAT_METHOD_ALL_THE_SAME:
				abilitiesAllSameButton.setSelected(true);

				break;

			case Constants.CHARACTER_STAT_METHOD_PURCHASE:
				if (!abilitiesPurchasedButton.isVisible() || (pMode.length == 0))
				{
					bValid = false;
				}
				else
				{
					abilitiesPurchasedButton.setSelected(true);
				}

				break;

			case Constants.CHARACTER_STAT_METHOD_ROLLED:
				if (abilitiesRolledButton == null)
				{
					bValid = false;
				}
				else
				{
					abilitiesRolledButton.setSelected(true);
					abilityRolledModeCombo.setSelectedItem(gameMode.getRollMethodExpressionName());
				}

				break;

			default:
				bValid = false;

				break;
		}

		if (!bValid)
		{
			abilitiesUserRolledButton.setSelected(true);
			gameMode.setRollMethod(Constants.CHARACTER_STAT_METHOD_USER);
		}

		int allStatsValue = Math.min(gameMode.getStatMax(), gameMode.getAllStatsValue());
		allStatsValue = Math.max(gameMode.getStatMin(), allStatsValue);
		gameMode.setAllStatsValue(allStatsValue);
		abilityScoreCombo.setSelectedIndex(allStatsValue - gameMode.getStatMin());

		if ((pMode != null) && (pModeMethodName != null))
		{
			final String methodName = gameMode.getPurchaseModeMethodName();

			for (int i = 0; i < pMode.length; ++i)
			{
				if (pModeMethodName[i].equals(methodName))
				{
					abilityPurchaseModeCombo.setSelectedIndex(i);
				}
			}
		}

		startListeners();
	}

	/**
	 * Create and display purchase mode stats popup frame.
	 */
	private void showPurchaseModeConfiguration()
	{
		if (pmsFrame == null)
		{
			pmsFrame = new PurchaseModeFrame(parent);
			final GameMode gameMode = SettingsHandler.getGame();

			pmsFrame.setStatMin(gameMode.getStatMin());
			pmsFrame.setStatMax(gameMode.getStatMax());

			// add a listener to know when the window has closed
			pmsFrame.addWindowListener(new WindowAdapter()
			{
				@Override
				public void windowClosed(WindowEvent e)
				{
					Collection<PointBuyMethod> methods = SettingsHandler.getGame().getModeContext()
						.getReferenceContext().getConstructedCDOMObjects(PointBuyMethod.class);
					final int purchaseMethodCount = methods.size();
					pMode = new String[purchaseMethodCount];
					pModeMethodName = new String[purchaseMethodCount];

					final String methodName = SettingsHandler.getGame().getPurchaseModeMethodName();
					abilityPurchaseModeCombo.removeAllItems();

					int i = 0;
					for (PointBuyMethod pbm : methods)
					{
						pMode[i] = pbm.getDescription();
						pModeMethodName[i] = pbm.getDisplayName();
						abilityPurchaseModeCombo.addItem(pMode[i]);

						if (pModeMethodName[i].equals(methodName))
						{
							abilityPurchaseModeCombo.setSelectedIndex(i);
						}
						i++;
					}

					// free resources
					pmsFrame = null;

					//
					// If user has added at least one method, then make the controls visible. Otherwise
					// it is not a valid choice and cannot be selected, so hide it.
					//
					abilityPurchaseModeCombo.setVisible(purchaseMethodCount != 0);
					abilitiesPurchasedButton.setVisible(purchaseMethodCount != 0);

					//
					// If no longer visible, but was selected, then use 'user rolled' instead
					//
					if (!abilitiesPurchasedButton.isVisible() && abilitiesPurchasedButton.isSelected())
					{
						abilitiesUserRolledButton.setSelected(true);
					}

				}
			});
		}

		pmsFrame.pack();
		pmsFrame.setLocationRelativeTo(null);
		pmsFrame.setVisible(true);
	}

	/**
	 * Create and add the listeners for the panel.
	 */
	private void addAbilitiesPanelListeners()
	{
		scoreListener = evt -> abilitiesAllSameButton.setSelected(true);

		purchaseModeListener = evt -> abilitiesPurchasedButton.setSelected(true);

		rolledModeListener = evt -> abilitiesRolledButton.setSelected(true);

		startListeners();
	}

	/**
	 * Start the listeners that track changing data. These have to
	 * be stopped when updating data programatically to avoid
	 * spurious setting of dirty flags etc.
	 */
	private void startListeners()
	{
		abilityScoreCombo.addActionListener(scoreListener);
		abilityPurchaseModeCombo.addActionListener(purchaseModeListener);
		if (abilityRolledModeCombo != null)
		{
			abilityRolledModeCombo.addActionListener(rolledModeListener);
		}
	}

	/**
	 * Stop the listeners that track changing data. These have to
	 * be stopped when updating data programatically to avoid
	 * spurious setting of dirty flags etc.
	 */
	private void stopListeners()
	{
		abilityScoreCombo.removeActionListener(scoreListener);
		abilityPurchaseModeCombo.removeActionListener(purchaseModeListener);
		if (abilityRolledModeCombo != null)
		{
			abilityRolledModeCombo.removeActionListener(rolledModeListener);
		}
	}

	@Override
	public String getTitle()
	{
		return IN_ABILITIES;
	}

	@Override
	public void setOptionsBasedOnControls()
	{
		final GameMode gameMode = SettingsHandler.getGame();
		gameMode.setAllStatsValue(abilityScoreCombo.getSelectedIndex() + gameMode.getStatMin());

		if (abilitiesUserRolledButton.isSelected())
		{
			gameMode.setRollMethod(Constants.CHARACTER_STAT_METHOD_USER);
		}
		else if (abilitiesAllSameButton.isSelected())
		{
			gameMode.setRollMethod(Constants.CHARACTER_STAT_METHOD_ALL_THE_SAME);
		}
		else if (abilitiesPurchasedButton.isSelected())
		{
			if (abilityPurchaseModeCombo.isVisible() && (abilityPurchaseModeCombo.getSelectedIndex() >= 0))
			{
				gameMode.setPurchaseMethodName(pModeMethodName[abilityPurchaseModeCombo.getSelectedIndex()]);
			}
			else
			{
				gameMode.setRollMethod(Constants.CHARACTER_STAT_METHOD_USER);
			}
		}
		else if ((abilitiesRolledButton != null) && (abilitiesRolledButton.isSelected()))
		{
			if (abilityRolledModeCombo.getSelectedIndex() >= 0)
			{
				gameMode.setRollMethodExpressionByName(abilityRolledModeCombo.getSelectedItem().toString());
			}
			else
			{
				gameMode.setRollMethod(Constants.CHARACTER_STAT_METHOD_USER);
			}
		}
	}

	/**
	 * Handler for the Purchase Mode Config button.
	 */
	private final class PurchaseModeButtonListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent actionEvent)
		{
			showPurchaseModeConfiguration();
		}
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(JDialog parent)
	{
		this.parent = parent;
	}

}
