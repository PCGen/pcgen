/*
 * ColorsPanel.java
 * Copyright 2010 (C) James Dempsey
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
 * Created on 16/11/2010 08:15:00
 *
 * $Id$
 */
package pcgen.gui2.prefs;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import pcgen.core.Globals;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.tools.Utility;
import pcgen.system.LanguageBundle;

/**
 * The Class {@code ColorsPanel} is responsible for
 * displaying color related preferences and allowing the 
 * preferences to be edited by the user.
 * 
 * 
 * @author James Dempsey &lt;jdempsey@users.sourceforge.net&gt;
 */
@SuppressWarnings("serial")
public class ColorsPanel extends PCGenPrefsPanel
{
	private static String in_color = LanguageBundle.getString("in_Prefs_color");
	private static String in_colorPrereqQualify =
			LanguageBundle.getString("in_Prefs_colorPrereqQualify");
	private static String in_colorPrereqFail =
			LanguageBundle.getString("in_Prefs_colorPrereqFail");
	private static String in_colorAutoFeat =
			LanguageBundle.getString("in_Prefs_colorAutoFeat");
	private static String in_colorVirtFeat =
			LanguageBundle.getString("in_Prefs_colorVirtFeat");

	private static String in_colorSourceRelease =
			LanguageBundle.getString("in_Prefs_colorStatusRelease");
	private static String in_colorSourceAlpha =
			LanguageBundle.getString("in_Prefs_colorStatusAlpha");
	private static String in_colorSourceBeta =
			LanguageBundle.getString("in_Prefs_colorStatusBeta");
	private static String in_colorSourceTest =
			LanguageBundle.getString("in_Prefs_colorStatusTest");

	private JButton featAutoColor;
	private JButton featVirtualColor;
	private JButton prereqFailColor;
	private JButton prereqQualifyColor;

	private JButton sourceStatusRelease;
	private JButton sourceStatusAlpha;
	private JButton sourceStatusBeta;
	private JButton sourceStatusTest;

	private PrefsButtonListener prefsButtonHandler = new PrefsButtonListener();

	/**
	 * Instantiates a new colors panel.
	 */
	public ColorsPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		Border etched = null;
		TitledBorder title1 =
				BorderFactory.createTitledBorder(etched, in_color);

		title1.setTitleJustification(TitledBorder.LEFT);
		this.setBorder(title1);
		gridbag = new GridBagLayout();
		this.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2, 2, 2, 2);

		int col = 0;

		// NB - not alphabetized!
		col =
				addColorsOption(col, c, gridbag, this, prereqQualifyColor =
						new JButton(in_colorPrereqQualify));
		col =
				addColorsOption(col, c, gridbag, this, prereqFailColor =
						new JButton(in_colorPrereqFail));
		col =
				addColorsOption(col, c, gridbag, this, featAutoColor =
						new JButton(in_colorAutoFeat));
		col =
				addColorsOption(col, c, gridbag, this, featVirtualColor =
						new JButton(in_colorVirtFeat));

		col =
				addColorsOption(col, c, gridbag, this, sourceStatusRelease =
						new JButton(in_colorSourceRelease));
		col =
				addColorsOption(col, c, gridbag, this, sourceStatusAlpha =
						new JButton(in_colorSourceAlpha));
		col =
				addColorsOption(col, c, gridbag, this, sourceStatusBeta =
						new JButton(in_colorSourceBeta));
		col =
				addColorsOption(col, c, gridbag, this, sourceStatusTest =
						new JButton(in_colorSourceTest));
		
		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		this.add(label);
	}

	private int addColorsOption(int col, final GridBagConstraints c,
		final GridBagLayout gridbag, final JPanel colorsPanel,
		final JButton button)
	{
		Utility.buildConstraints(c, 0, col++, 1, 1, 0, 0);
		gridbag.setConstraints(button, c);
		colorsPanel.add(button);
		button.addActionListener(prefsButtonHandler);

		return col;
	}

	/* (non-Javadoc)
	 * @see pcgen.gui2.prefs.PCGenPrefsPanel#getTitle()
	 */
	@Override
	public String getTitle()
	{
		return in_color;
	}

	/* (non-Javadoc)
	 * @see pcgen.gui2.prefs.PreferencesPanel#applyPreferences()
	 */
	@Override
	public void setOptionsBasedOnControls()
	{
		UIPropertyContext.setQualifiedColor(prereqQualifyColor.getForeground());
		UIPropertyContext.setNotQualifiedColor(prereqFailColor.getForeground());
		UIPropertyContext.setAutomaticColor(featAutoColor.getForeground());
		UIPropertyContext.setVirtualColor(featVirtualColor.getForeground());

		UIPropertyContext.setSourceStatusReleaseColor(sourceStatusRelease.getForeground());
		UIPropertyContext.setSourceStatusAlphaColor(sourceStatusAlpha.getForeground());
		UIPropertyContext.setSourceStatusBetaColor(sourceStatusBeta.getForeground());
		UIPropertyContext.setSourceStatusTestColor(sourceStatusTest.getForeground());
	}

	/* (non-Javadoc)
	 * @see pcgen.gui2.prefs.PreferencesPanel#initPreferences()
	 */
	@Override
	public void applyOptionValuesToControls()
	{
		prereqQualifyColor.setForeground(UIPropertyContext.getQualifiedColor());
		prereqFailColor.setForeground(UIPropertyContext.getNotQualifiedColor());
		featAutoColor.setForeground(UIPropertyContext.getAutomaticColor());
		featVirtualColor.setForeground(UIPropertyContext.getVirtualColor());

		sourceStatusRelease.setForeground(UIPropertyContext.getSourceStatusReleaseColor());
		sourceStatusAlpha.setForeground(UIPropertyContext.getSourceStatusAlphaColor());
		sourceStatusBeta.setForeground(UIPropertyContext.getSourceStatusBetaColor());
		sourceStatusTest.setForeground(UIPropertyContext.getSourceStatusTestColor());
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
			else if ((source == prereqQualifyColor)
				|| (source == prereqFailColor) || (source == featAutoColor)
				|| (source == featVirtualColor))
			{
				final Color newColor =
						JColorChooser.showDialog(Globals.getRootFrame(),
							LanguageBundle.getString("in_Prefs_colorSelect")
								+ source.getText().toLowerCase(), source
								.getForeground());

				if (newColor != null)
				{
					source.setForeground(newColor);
//
//					if (source == prereqQualifyColor)
//					{
//						SettingsHandler
//							.setPrereqQualifyColor(newColor.getRGB());
//					}
//					else if (source == prereqFailColor)
//					{
//						SettingsHandler.setPrereqFailColor(newColor.getRGB());
//					}
//					else if (source == featAutoColor)
//					{
//						SettingsHandler.setFeatAutoColor(newColor.getRGB());
//					}
//					else if (source == featVirtualColor)
//					{
//						SettingsHandler.setFeatVirtualColor(newColor.getRGB());
//					}
				}
			}
		}
	}
}
