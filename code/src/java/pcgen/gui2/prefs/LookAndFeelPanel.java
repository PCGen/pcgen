/*
 * Copyright 2010(C) James Dempsey
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

import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import pcgen.cdom.base.Constants;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui2.plaf.LookAndFeelManager;
import pcgen.gui2.tools.Utility;
import pcgen.system.ConfigurationSettings;
import pcgen.system.LanguageBundle;

import org.apache.commons.lang3.StringUtils;

/**
 * The Class {@code LookAndFeelPanel} is responsible for
 * displaying look and feel related preferences and allowing the 
 * preferences to be edited by the user.
 */
@SuppressWarnings("serial")
public class LookAndFeelPanel extends PCGenPrefsPanel
{
	private static final String in_lookAndFeel =
		LanguageBundle.getString("in_Prefs_lookAndFeel");

	private static final String in_skinnedLAF =
		LanguageBundle.getString("in_Prefs_skinnedLAF");
	private static final String in_choose = "...";

	private final JRadioButton[] laf;
	private final JRadioButton skinnedLookFeel = new JRadioButton();
	private final JButton themepack;
	private final JTextField themepackLabel;
	private final PrefsButtonListener prefsButtonHandler = new PrefsButtonListener();
	private String oldLAF;
	private String oldThemePack;
	/**
	 * Instantiates a new look and feel panel.
	 */
	public LookAndFeelPanel(Dialog parent)
	{

		JLabel label;
		ButtonGroup exclusiveGroup;
		Border etched = null;
		TitledBorder title1 =
				BorderFactory.createTitledBorder(etched, in_lookAndFeel);

		title1.setTitleJustification(TitledBorder.LEFT);
		this.setBorder(title1);
		GridBagLayout gridbag = new GridBagLayout();
		this.setLayout(gridbag);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2, 2, 2, 2);

		exclusiveGroup = new ButtonGroup();
		Action[] actions = LookAndFeelManager.getActions();
		laf = new JRadioButton[actions.length-1];

		for (int i = 0; i < laf.length; ++i)
		{
			laf[i] = new JRadioButton(actions[i]);

			int whichChar = (laf[i].getText().charAt(0) == 'C') ? 1 : 0;
			laf[i].setMnemonic(laf[i].getText().charAt(whichChar));

			Utility.buildConstraints(c, 0, i, 3, 1, 0, 0);
			gridbag.setConstraints(laf[i], c);
			this.add(laf[i]);
			exclusiveGroup.add(laf[i]);
		}

		skinnedLookFeel.addActionListener(actions[actions.length-1]);
		skinnedLookFeel.setText(in_skinnedLAF + ": ");
		skinnedLookFeel.setToolTipText(LanguageBundle
			.getString("in_Prefs_skinnedLAFTooltip"));
		skinnedLookFeel.setMnemonic(LanguageBundle
			.getMnemonic("in_mn_Prefs_skinnedLAF"));
		Utility.buildConstraints(c, 0, laf.length, 3, 1, 0, 0);
		gridbag.setConstraints(skinnedLookFeel, c);
		this.add(skinnedLookFeel);
		exclusiveGroup.add(skinnedLookFeel);

		Utility.buildConstraints(c, 3, laf.length, 1, 1, 1, 0);
		themepackLabel = new JTextField(LookAndFeelManager.getCurrentThemePack());
		themepackLabel.setEditable(false);
		gridbag.setConstraints(themepackLabel, c);
		this.add(themepackLabel);
		Utility.buildConstraints(c, 4, laf.length, 1, 1, 0, 0);
		themepack = new JButton(in_choose);
		themepack.setToolTipText(LanguageBundle
			.getString("in_Prefs_chooseSkinTooltip"));
		gridbag.setConstraints(themepack, c);
		this.add(themepack);
		themepack.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, laf.length+1, 5, 1, 0, 0);
		label = new JLabel("");
		gridbag.setConstraints(label, c);
		this.add(label);

		Utility.buildConstraints(c, 0, laf.length+2, 5, 1, 0, 0);
		label = new JLabel(LanguageBundle.getString("in_Prefs_restartInfo"));
		gridbag.setConstraints(label, c);
		this.add(label);

		Utility.buildConstraints(c, 0, 20, 5, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		this.add(label);
	}

	private void selectThemePack()
	{
		JFileChooser fc =
				new JFileChooser(ConfigurationSettings.getThemePackDir());
		fc.setDialogTitle(LanguageBundle
			.getString("in_Prefs_chooseSkinDialogTitle"));

		String theme = LookAndFeelManager.getCurrentThemePack();

		if (StringUtils.isNotEmpty(theme))
		{
			fc.setCurrentDirectory(new File(LookAndFeelManager.getCurrentThemePack()));
			fc.setSelectedFile(new File(LookAndFeelManager.getCurrentThemePack()));
		}

		fc.addChoosableFileFilter(new ThemePackFilter());

		if (fc.showOpenDialog(getParent().getParent()) == JFileChooser.APPROVE_OPTION) //ugly, but it works
		{
			File newTheme = fc.getSelectedFile();

			if (newTheme.isDirectory()
				|| (!newTheme.getName().endsWith("themepack.zip")))
			{
				ShowMessageDelegate.showMessageDialog(
					LanguageBundle.getString("in_Prefs_notAThemeErrorItem"),
					Constants.APPLICATION_NAME, MessageType.ERROR);
			}
			else
			{
				LookAndFeelManager.setSelectedThemePack(newTheme.getAbsolutePath());
			}
		}
	}

	static final class ThemePackFilter extends FileFilter
	{
		// The description of this filter
		@Override
		public String getDescription()
		{
			return "Themepacks (*themepack.zip)";
		}

		// Accept all directories and themepack.zip files.
		@Override
		public boolean accept(File f)
		{
			if (f.isDirectory())
			{
				return true;
			}

			if (f.getName().endsWith("themepack.zip"))
			{
				return true;
			}

			return false;
		}
	}

	/**
	 * @see pcgen.gui2.prefs.PCGenPrefsPanel#getTitle()
	 */
	@Override
	public String getTitle()
	{
		return in_lookAndFeel;
	}
	
	/**
	 * @see pcgen.gui2.prefs.PreferencesPanel#applyPreferences()
	 */
	@Override
	public void setOptionsBasedOnControls()
	{
		//NB: options are already set using the actions defined in the PCGenUIManager
	}

	@Override
	public void resetOptionValues()
	{
		LookAndFeelManager.setSelectedThemePack(oldThemePack);
		LookAndFeelManager.setLookAndFeel(oldLAF);
	}

	@Override
	public boolean needsRestart()
	{
		boolean needsRestart = false;
		needsRestart |= (oldLAF != LookAndFeelManager.getCurrentLAF());
		needsRestart |= (oldThemePack != LookAndFeelManager.getCurrentThemePack());
		
		return needsRestart;
	}
	
	/**
	 * @see pcgen.gui2.prefs.PreferencesPanel#initPreferences()
	 */
	@Override
	public void applyOptionValuesToControls()
	{
		oldLAF = LookAndFeelManager.getCurrentLAF();
		oldThemePack = LookAndFeelManager.getCurrentThemePack();
		for (int i = 0; i < laf.length; i++)
		{
			laf[i].setSelected(oldLAF.equals(laf[i].getText()));
		}
		skinnedLookFeel.setSelected(oldLAF.equals("Skinned"));
	}

	private final class PrefsButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent actionEvent)
		{
			JButton source = (JButton) actionEvent.getSource();

			if (source == null)
			{
				// Do nothing
			}
			else if (source == themepack)
			{
				selectThemePack();
				themepackLabel.setText(LookAndFeelManager.getCurrentThemePack());
			}
		}
	}

}
