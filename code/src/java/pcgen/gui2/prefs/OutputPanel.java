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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.SkillFilter;
import pcgen.core.Globals;
import pcgen.core.PaperInfo;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.tools.Utility;
import pcgen.gui2.util.JComboBoxEx;
import pcgen.gui3.GuiUtility;
import pcgen.system.ConfigurationSettings;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;

import javafx.stage.FileChooser;
import org.apache.commons.lang3.BooleanUtils;

/**
 * The Class {@code OutputPanel} is responsible for
 * displaying character output related preferences and allowing the 
 * preferences to be edited by the user.
 * 
 * 
 */
@SuppressWarnings("serial")
public class OutputPanel extends PCGenPrefsPanel
{
	private static final String IN_OUTPUT = LanguageBundle.getString("in_Prefs_output");

	private static final String IN_ALWAYS_OVERWRITE = LanguageBundle.getString("in_Prefs_alwaysOverwrite");
	private static final String IN_OUTPUT_SHEET_EQ_SET = LanguageBundle.getString("in_Prefs_templateEqSet");
	private static final String IN_PAPER_TYPE = LanguageBundle.getString("in_Prefs_paperType");
	private static final String IN_POST_EXPORT_COMAND_STANDARD =
			LanguageBundle.getString("in_Prefs_postExportCommandStandard");
	private static final String IN_POST_EXPORT_COMMAND_PDF = LanguageBundle.getString("in_Prefs_postExportCommandPDF");
	private static final String IN_REMOVE_TEMP = LanguageBundle.getString("in_Prefs_removeTemp");
	private static final String IN_SAVE_OUTPUT_SHEET_WITH_PC =
			LanguageBundle.getString("in_Prefs_saveOutputSheetWithPC");
	private static final String IN_SHOW_SINGLE_BOX_PER_BUNDLE =
			LanguageBundle.getString("in_Prefs_showSingleBoxPerBundle");
	private static final String IN_WEAPON_PROF_PRINTOUT = LanguageBundle.getString("in_Prefs_weaponProfPrintout");
	private static final String IN_SKILL_FILTER = LanguageBundle.getString("in_Prefs_skillFilterLabel");
	private static final String IN_CHOOSE = LanguageBundle.getString("...");
	private static final String IN_GENERATE_TEMP_FILE_WITH_PDF =
			LanguageBundle.getString("in_Prefs_generateTempFileWithPdf");

	private final JCheckBox printSpellsWithPC = new JCheckBox();
	private final JCheckBox removeTempFiles = new JCheckBox(IN_REMOVE_TEMP);
	private final JCheckBox saveOutputSheetWithPC = new JCheckBox();
	private final JCheckBox generateTempFileWithPdf = new JCheckBox(IN_GENERATE_TEMP_FILE_WITH_PDF);

	private final JCheckBox weaponProfPrintout;
	private final JButton outputSheetEqSetButton;
	private final JButton outputSheetHTMLDefaultButton;
	private final JButton outputSheetPDFDefaultButton;
	private final JButton outputSheetSpellsDefaultButton;

	private final JTextField outputSheetEqSet;
	private final JTextField outputSheetHTMLDefault;
	private final JTextField outputSheetPDFDefault;
	private final JTextField outputSheetSpellsDefault;

	private JComboBoxEx<String> paperType = new JComboBoxEx<>();
	private final JComboBoxEx<SkillFilter> skillFilter = new JComboBoxEx<>();
	private final JComboBox<ExportChoices> exportChoice = new JComboBox<>(ExportChoices.values());

	private final JTextField postExportCommandStandard;
	private final JTextField postExportCommandPDF;
	private final JCheckBox alwaysOverwrite;
	private final JCheckBox showSingleBoxPerBundle;

	private String[] paperNames = null;

	// Listeners
	private final PrefsButtonListener prefsButtonHandler = new PrefsButtonListener();
	private final TextFocusLostListener textFieldListener = new TextFocusLostListener();

	/**
	 * Instantiates a new output panel.
	 */
	public OutputPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, IN_OUTPUT);

		title1.setTitleJustification(TitledBorder.LEADING);
		this.setBorder(title1);
		this.setLayout(gridbag);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(2, 2, 2, 2);

		Utility.buildConstraints(c, 0, 0, 1, 1, 0, 0);
		label = new JLabel(LanguageBundle.getString("in_Prefs_outputSheetHTMLDefault"));
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 1, 0, 1, 1, 1, 0);
		outputSheetHTMLDefault =
				new JTextField(String.valueOf(SettingsHandler.getSelectedCharacterHTMLOutputSheet(null)));

		// sage_sam 9 April 2003
		outputSheetHTMLDefault.addFocusListener(textFieldListener);
		gridbag.setConstraints(outputSheetHTMLDefault, c);
		this.add(outputSheetHTMLDefault);
		Utility.buildConstraints(c, 2, 0, 1, 1, 0, 0);
		outputSheetHTMLDefaultButton = createChooseButton();
		gridbag.setConstraints(outputSheetHTMLDefaultButton, c);
		this.add(outputSheetHTMLDefaultButton);
		outputSheetHTMLDefaultButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 1, 1, 1, 0, 0);
		label = new JLabel(LanguageBundle.getString("in_Prefs_outputSheetPDFDefault"));
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 1, 1, 1, 1, 1, 0);
		outputSheetPDFDefault =
				new JTextField(String.valueOf(SettingsHandler.getSelectedCharacterPDFOutputSheet(null)));

		// sage_sam 9 April 2003
		outputSheetPDFDefault.addFocusListener(textFieldListener);
		gridbag.setConstraints(outputSheetPDFDefault, c);
		this.add(outputSheetPDFDefault);
		Utility.buildConstraints(c, 2, 1, 1, 1, 0, 0);
		outputSheetPDFDefaultButton = createChooseButton();
		gridbag.setConstraints(outputSheetPDFDefaultButton, c);
		this.add(outputSheetPDFDefaultButton);
		outputSheetPDFDefaultButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 2, 1, 1, 0, 0);
		label = new JLabel(IN_OUTPUT_SHEET_EQ_SET);
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 1, 2, 1, 1, 0, 0);
		outputSheetEqSet = new JTextField(String.valueOf(SettingsHandler.getSelectedEqSetTemplate()));

		// sage_sam 9 April 2003
		outputSheetEqSet.addFocusListener(textFieldListener);
		gridbag.setConstraints(outputSheetEqSet, c);
		this.add(outputSheetEqSet);
		Utility.buildConstraints(c, 2, 2, 1, 1, 0, 0);
		outputSheetEqSetButton = createChooseButton();
		gridbag.setConstraints(outputSheetEqSetButton, c);
		this.add(outputSheetEqSetButton);
		outputSheetEqSetButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 3, 3, 1, 0, 0);
		saveOutputSheetWithPC.setText(IN_SAVE_OUTPUT_SHEET_WITH_PC);
		gridbag.setConstraints(saveOutputSheetWithPC, c);
		this.add(saveOutputSheetWithPC);

		Utility.buildConstraints(c, 0, 4, 1, 1, 0, 0);
		label = new JLabel(LanguageBundle.getString("in_Prefs_outputSpellSheetDefault"));
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 1, 4, 1, 1, 0, 0);
		outputSheetSpellsDefault = new JTextField(String.valueOf(SettingsHandler.getSelectedSpellSheet()));
		outputSheetSpellsDefault.addFocusListener(textFieldListener);
		gridbag.setConstraints(outputSheetSpellsDefault, c);
		this.add(outputSheetSpellsDefault);
		Utility.buildConstraints(c, 2, 4, 1, 1, 0, 0);
		outputSheetSpellsDefaultButton = createChooseButton();
		gridbag.setConstraints(outputSheetSpellsDefaultButton, c);
		this.add(outputSheetSpellsDefaultButton);
		outputSheetSpellsDefaultButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 5, 3, 1, 0, 0);
		printSpellsWithPC.setText(LanguageBundle.getString("in_Prefs_printSpellsWithPC"));
		gridbag.setConstraints(printSpellsWithPC, c);
		this.add(printSpellsWithPC);

		Utility.buildConstraints(c, 0, 6, 1, 1, 0, 0);
		label = new JLabel(IN_PAPER_TYPE);
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 1, 6, 2, 1, 0, 0);

		final int paperCount = Globals.getPaperCount();
		paperNames = new String[paperCount];

		for (int i = 0; i < paperCount; ++i)
		{
			paperNames[i] = Globals.getPaperInfo(i, PaperInfo.NAME);
		}

		paperType = new JComboBoxEx<>(paperNames);
		gridbag.setConstraints(paperType, c);
		this.add(paperType);

		Utility.buildConstraints(c, 0, 7, 3, 1, 0, 0);
		gridbag.setConstraints(removeTempFiles, c);
		this.add(removeTempFiles);

		Utility.buildConstraints(c, 0, 8, 3, 1, 0, 0);
		weaponProfPrintout = new JCheckBox(IN_WEAPON_PROF_PRINTOUT, SettingsHandler.getWeaponProfPrintout());
		gridbag.setConstraints(weaponProfPrintout, c);
		this.add(weaponProfPrintout);

		Utility.buildConstraints(c, 0, 9, 1, 1, 0, 0);
		label = new JLabel(IN_POST_EXPORT_COMAND_STANDARD);
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 1, 9, 2, 1, 0, 0);
		postExportCommandStandard = new JTextField(String.valueOf(SettingsHandler.getPostExportCommandStandard()));
		gridbag.setConstraints(postExportCommandStandard, c);
		this.add(postExportCommandStandard);

		Utility.buildConstraints(c, 0, 10, 1, 1, 0, 0);
		label = new JLabel(IN_POST_EXPORT_COMMAND_PDF);
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 1, 10, 2, 1, 0, 0);
		postExportCommandPDF = new JTextField(String.valueOf(SettingsHandler.getPostExportCommandPDF()));
		gridbag.setConstraints(postExportCommandPDF, c);
		this.add(postExportCommandPDF);

		Utility.buildConstraints(c, 0, 11, 1, 1, 0, 0);
		label = new JLabel(IN_SKILL_FILTER);
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 1, 11, GridBagConstraints.REMAINDER, 1, 0, 0);
		skillFilter.setModel(new DefaultComboBoxModel<>(
			new SkillFilter[]{SkillFilter.Ranks, SkillFilter.NonDefault, SkillFilter.Usable, SkillFilter.All}));
		skillFilter.setSelectedItem(SkillFilter.getByValue(
			PCGenSettings.OPTIONS_CONTEXT.initInt(PCGenSettings.OPTION_SKILL_FILTER, SkillFilter.Usable.getValue())));
		gridbag.setConstraints(skillFilter, c);
		this.add(skillFilter);

		Utility.buildConstraints(c, 0, 14, 3, 1, 0, 0);
		alwaysOverwrite = new JCheckBox(IN_ALWAYS_OVERWRITE, SettingsHandler.getAlwaysOverwrite());
		gridbag.setConstraints(alwaysOverwrite, c);
		this.add(alwaysOverwrite);

		Utility.buildConstraints(c, 0, 15, 3, 1, 0, 0);
		showSingleBoxPerBundle = new JCheckBox(IN_SHOW_SINGLE_BOX_PER_BUNDLE, SettingsHandler.getShowSingleBoxPerBundle());
		gridbag.setConstraints(showSingleBoxPerBundle, c);
		this.add(showSingleBoxPerBundle);

		Utility.buildConstraints(c, 0, 16, 1, 1, 0, 0);
		label = new JLabel(LanguageBundle.getString("in_Prefs_exportChoice")); // $NON-NSL-1$
		gridbag.setConstraints(label, c);
		this.add(label);

		Utility.buildConstraints(c, 1, 16, GridBagConstraints.REMAINDER, 1, 0, 0);
		gridbag.setConstraints(exportChoice, c);
		this.add(exportChoice);

		Utility.buildConstraints(c, 0, 17, 3, 1, 0, 0);
		gridbag.setConstraints(generateTempFileWithPdf, c);
		this.add(generateTempFileWithPdf);

		Utility.buildConstraints(c, 0, 20, 3, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		this.add(label);
	}

	private JButton createChooseButton()
	{
		JButton button = new JButton(IN_CHOOSE);
		button.setMargin(new Insets(0, 3, 0, 3));
		return button;
	}

	@Override
	public String getTitle()
	{
		return IN_OUTPUT;
	}

	@Override
	public void setOptionsBasedOnControls()
	{
		UIPropertyContext context = UIPropertyContext.getInstance();

		Globals.selectPaper((String) paperType.getSelectedItem());

		context.setBoolean(UIPropertyContext.CLEANUP_TEMP_FILES, removeTempFiles.isSelected());

		if (SettingsHandler.getWeaponProfPrintout() != weaponProfPrintout.isSelected())
		{
			SettingsHandler.setWeaponProfPrintout(weaponProfPrintout.isSelected());
		}

		if (SettingsHandler.getAlwaysOverwrite() || alwaysOverwrite.isSelected())
		{
			SettingsHandler.setAlwaysOverwrite(alwaysOverwrite.isSelected());
		}

		if (SettingsHandler.getShowSingleBoxPerBundle() || showSingleBoxPerBundle.isSelected())
		{
			SettingsHandler.setShowSingleBoxPerBundle(showSingleBoxPerBundle.isSelected());
		}

		context.setProperty(UIPropertyContext.DEFAULT_HTML_OUTPUT_SHEET, outputSheetHTMLDefault.getText());
		context.setProperty(UIPropertyContext.DEFAULT_PDF_OUTPUT_SHEET, outputSheetPDFDefault.getText());
		SettingsHandler.setSelectedEqSetTemplate(outputSheetEqSet.getText());
		context.setBoolean(UIPropertyContext.SAVE_OUTPUT_SHEET_WITH_PC, saveOutputSheetWithPC.isSelected());
		SettingsHandler.setSelectedSpellSheet(outputSheetSpellsDefault.getText());
		SettingsHandler.setPrintSpellsWithPC(printSpellsWithPC.isSelected());
		SettingsHandler.setPostExportCommandStandard(postExportCommandStandard.getText());
		SettingsHandler.setPostExportCommandPDF(postExportCommandPDF.getText());
		PCGenSettings.OPTIONS_CONTEXT.setInt(PCGenSettings.OPTION_SKILL_FILTER,
			((SkillFilter) skillFilter.getSelectedItem()).getValue());

		ExportChoices choice = (ExportChoices) exportChoice.getSelectedItem();
		context.setProperty(UIPropertyContext.ALWAYS_OPEN_EXPORT_FILE, choice.getValue());
		PCGenSettings.OPTIONS_CONTEXT.setBoolean(PCGenSettings.OPTION_GENERATE_TEMP_FILE_WITH_PDF,
			generateTempFileWithPdf.isSelected());
	}

	@Override
	public void applyOptionValuesToControls()
	{
		UIPropertyContext context = UIPropertyContext.getInstance();

		paperType.setSelectedIndex(Globals.getSelectedPaper());
		weaponProfPrintout.setSelected(SettingsHandler.getWeaponProfPrintout());

		outputSheetHTMLDefault.setText(context.getProperty(UIPropertyContext.DEFAULT_HTML_OUTPUT_SHEET));
		outputSheetPDFDefault.setText(context.getProperty(UIPropertyContext.DEFAULT_PDF_OUTPUT_SHEET));
		saveOutputSheetWithPC.setSelected(context.getBoolean(UIPropertyContext.SAVE_OUTPUT_SHEET_WITH_PC));
		removeTempFiles.setSelected(context.initBoolean(UIPropertyContext.CLEANUP_TEMP_FILES, true));

		printSpellsWithPC.setSelected(SettingsHandler.getPrintSpellsWithPC());
		skillFilter.setSelectedItem(SkillFilter.getByValue(
			PCGenSettings.OPTIONS_CONTEXT.initInt(PCGenSettings.OPTION_SKILL_FILTER, SkillFilter.Usable.getValue())));

		String value = context.getProperty(UIPropertyContext.ALWAYS_OPEN_EXPORT_FILE);
		exportChoice.setSelectedItem(ExportChoices.getChoice(value));

		generateTempFileWithPdf.setSelected(
			PCGenSettings.OPTIONS_CONTEXT.initBoolean(PCGenSettings.OPTION_GENERATE_TEMP_FILE_WITH_PDF, false));
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
			else if (source == outputSheetHTMLDefaultButton)
			{
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle(LanguageBundle.getString("in_Prefs_outputSheetHTMLDefaultTitle"));
				fileChooser.setInitialDirectory(new File(SettingsHandler.getHTMLOutputSheetPath()));
				fileChooser.setInitialFileName(SettingsHandler.getSelectedCharacterHTMLOutputSheet(null));
				File newTemplate = GuiUtility.runOnJavaFXThreadNow(() -> fileChooser.showOpenDialog(null));

				if (newTemplate != null)
				{
					if ((!newTemplate.getName().startsWith("csheet") && !newTemplate.getName().startsWith("psheet")))
					{
						ShowMessageDelegate.showMessageDialog(
							LanguageBundle.getString("in_Prefs_outputSheetDefaultError"), //$NON-NLS-1$
							Constants.APPLICATION_NAME, MessageType.ERROR);
					}
					else
					{
						if (newTemplate.getName().startsWith("csheet"))
						{
							SettingsHandler.setSelectedCharacterHTMLOutputSheet(newTemplate.getAbsolutePath(), null);
						}
						else
						{
							//it must be a psheet
							SettingsHandler.setSelectedPartyHTMLOutputSheet(newTemplate.getAbsolutePath());
						}
					}
				}

				outputSheetHTMLDefault
					.setText(String.valueOf(SettingsHandler.getSelectedCharacterHTMLOutputSheet(null)));
			}
			else if (source == outputSheetPDFDefaultButton)
			{
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle(LanguageBundle.getString("in_Prefs_outputSheetPDFDefaultTitle"));
				fileChooser.setInitialDirectory(new File(SettingsHandler.getPDFOutputSheetPath()));
				fileChooser.setInitialFileName(SettingsHandler.getSelectedCharacterPDFOutputSheet(null));
				File newTemplate = GuiUtility.runOnJavaFXThreadNow(() -> fileChooser.showOpenDialog(null));

				if (newTemplate != null)
				{
					if (!newTemplate.getName().startsWith("csheet") && !newTemplate.getName().startsWith("psheet"))
					{
						ShowMessageDelegate.showMessageDialog(
							LanguageBundle.getString("in_Prefs_outputSheetDefaultError"), //$NON-NLS-1$
							Constants.APPLICATION_NAME, MessageType.ERROR);
					}
					else
					{
						if (newTemplate.getName().startsWith("csheet"))
						{
							SettingsHandler.setSelectedCharacterPDFOutputSheet(newTemplate.getAbsolutePath(), null);
						}
						else
						{
							//it must be a psheet
							SettingsHandler.setSelectedPartyPDFOutputSheet(newTemplate.getAbsolutePath());
						}
					}
				}

				outputSheetPDFDefault.setText(String.valueOf(SettingsHandler.getSelectedCharacterPDFOutputSheet(null)));
			}
			else if (source == outputSheetEqSetButton)
			{
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle(LanguageBundle.getString("in_Prefs_templateEqSetTitle"));
				fileChooser.setInitialDirectory(new File(ConfigurationSettings.getOutputSheetsDir()));
				fileChooser.setInitialFileName(SettingsHandler.getSelectedEqSetTemplate());
				File newTemplate = GuiUtility.runOnJavaFXThreadNow(() -> fileChooser.showOpenDialog(null));

				if (newTemplate != null)
				{
					if (newTemplate.getName().startsWith("eqsheet"))
					{
						//it must be a psheet
						SettingsHandler.setSelectedEqSetTemplate(newTemplate.getAbsolutePath());
					}
					else
					{
						ShowMessageDelegate.showMessageDialog(
								LanguageBundle.getString("in_Prefs_templateEqSetError"), //$NON-NLS-1$
								Constants.APPLICATION_NAME, MessageType.ERROR
						);
					}
				}

				outputSheetEqSet.setText(String.valueOf(SettingsHandler.getSelectedEqSetTemplate()));
			}
			else if (source == outputSheetSpellsDefaultButton)
			{
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle(LanguageBundle.getString("in_Prefs_outputSpellSheetDefault"));
				fileChooser.setInitialDirectory(new File(ConfigurationSettings.getOutputSheetsDir()));
				fileChooser.setInitialFileName(PCGenSettings.getSelectedSpellSheet());
				File newTemplate = GuiUtility.runOnJavaFXThreadNow(() -> fileChooser.showOpenDialog(null));

				if (newTemplate != null)
				{
					if (newTemplate.getName().startsWith("csheet"))
					{
						//it must be a psheet
						PCGenSettings.getInstance().setProperty(
								PCGenSettings.SELECTED_SPELL_SHEET_PATH,
								newTemplate.getAbsolutePath()
						);
					}
					else
					{
						ShowMessageDelegate.showMessageDialog(
								LanguageBundle.getString("in_Prefs_outputSheetDefaultError"), //$NON-NLS-1$
								Constants.APPLICATION_NAME, MessageType.ERROR
						);
					}
				}

				outputSheetSpellsDefault.setText(PCGenSettings.getSelectedSpellSheet());
			}
		}
	}

	// This is the focus listener so that text field values may be manually entered.
	// sage_sam April 2003 for FREQ 707022
	private static final class TextFocusLostListener implements FocusListener
	{
		private String initialValue = null;
		private boolean dialogOpened = false;

		@Override
		public void focusGained(FocusEvent e)
		{
			// reset variables
			dialogOpened = false;

			final Object source = e.getSource();

			if (source instanceof JTextField)
			{
				// get the field value
				initialValue = ((JTextField) source).getText();
			}
		}

		@Override
		public void focusLost(FocusEvent e)
		{
			// Check the source to see if it was a text field
			final Object source = e.getSource();

			if (source instanceof JTextField)
			{
				// get the field value and validate it exists
				final String fieldValue = ((JTextField) source).getText();
				final File fieldFile = new File(fieldValue);

				if ((!fieldFile.exists()) && (!fieldValue.equalsIgnoreCase("null")) && (!fieldValue.trim().isEmpty())
					&& (!dialogOpened))
				{
					// display error dialog and restore previous value
					dialogOpened = true;
					ShowMessageDelegate.showMessageDialog("File does not exist; preferences were not set.",
						"Invalid Path", MessageType.ERROR);
					((JTextField) source).setText(initialValue);
				}
			}
		}
	}

	private enum ExportChoices
	{

		ASK
		{
			@Override
			public String toString()
			{
				return LanguageBundle.getString("in_Prefs_ask"); //$NON-NLS-1$
			}
		},
		ALWAYS_OPEN
		{
			@Override
			public String toString()
			{
				return LanguageBundle.getString("in_Prefs_alwaysOpen"); //$NON-NLS-1$
			}
		},
		NEVER_OPEN
		{
			@Override
			public String toString()
			{
				return LanguageBundle.getString("in_Prefs_neverOpen"); //$NON-NLS-1$
			}
		};

		public String getValue()
		{

			switch (this)
			{
				case ASK:
					return "";
				case ALWAYS_OPEN:
					return "true";
				case NEVER_OPEN:
					return "false";
				default:
					throw new InternalError();
			}
		}

		public static ExportChoices getChoice(String value)
		{
			Boolean choice = BooleanUtils.toBooleanObject(value);
			if (choice == null)
			{
				return ExportChoices.ASK;
			}
			else if (choice)
			{
				return ExportChoices.ALWAYS_OPEN;
			}
			else
			{
				return ExportChoices.NEVER_OPEN;
			}
		}

	}

}
