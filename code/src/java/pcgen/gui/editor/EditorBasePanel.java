/*
 * EditorBasePanel.java
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on October 31, 2002, 4:36 PM
 *
 * @(#) $Id$
 */
package pcgen.gui.editor;

import pcgen.core.PObject;
import pcgen.system.LanguageBundle;

import javax.swing.*;
import java.awt.*;

/**
 * <code>EditorBasePanel</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
final class EditorBasePanel extends JPanel
{
	private BasePanel jPanel1;
	private JCheckBox chkProductIdentity;
	private JLabel lblName;
	private JLabel lblSource;
	private JPanel pnlName;
	private JPanel pnlProductIdentity;
	private JPanel pnlSource;
	private JTextField txtName;
	private JTextField txtSource;
	private int editType = EditorConstants.EDIT_NONE;

	/** Creates new form EditorBasePanel
	 * @param argEditType
	 * */
	EditorBasePanel(int argEditType)
	{
		editType = argEditType;
		initComponents();
	}

	/**
	 * Set name text
	 * @param aString
	 */
	public void setNameText(String aString)
	{
		txtName.setText(aString);
	}

	/**
	 * Get name text
	 * @return name text
	 */
	public String getNameText()
	{
		return txtName.getText().trim();
	}

	/**
	 * Set isPI
	 * @param isPI
	 */
	public void setProductIdentity(boolean isPI)
	{
		chkProductIdentity.setSelected(isPI);
	}

	/**
	 * Get PI
	 * @return true if ProductIdentity
	 */
	public boolean getProductIdentity()
	{
		return chkProductIdentity.isSelected();
	}

	/**
	 * Set source text
	 * @param aString
	 */
	public void setSourceText(String aString)
	{
		txtSource.setText(aString);
	}

	/**
	 * Get Sopurce text
	 * @return source text
	 */
	public String getSourceText()
	{
		return txtSource.getText().trim();
	}

	void updateData(PObject thisPObject)
	{
		jPanel1.updateData(thisPObject);
	}

	void updateView(PObject thisPObject)
	{
		jPanel1.updateView(thisPObject);
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		GridBagConstraints gridBagConstraints;

		pnlName = new JPanel();
		lblName = new JLabel();
		txtName = new JTextField();

		String nameID = "";

		switch (editType)
		{
			case EditorConstants.EDIT_CLASS:
				nameID = "ClassName";
				jPanel1 = new ClassBasePanel();

				break;

			case EditorConstants.EDIT_DEITY:
				nameID = "DeityName";
				jPanel1 = new DeityBasePanel();

				break;

			case EditorConstants.EDIT_DOMAIN:
				nameID = "DomainName";
				jPanel1 = new DomainBasePanel();

				break;

			case EditorConstants.EDIT_FEAT:
				nameID = "FeatName";
				jPanel1 = new FeatBasePanel();

				break;

			case EditorConstants.EDIT_LANGUAGE:
				nameID = "LanguageName";
				jPanel1 = new LanguageBasePanel();

				break;

			case EditorConstants.EDIT_RACE:
				nameID = "RaceName";
				jPanel1 = new RaceBasePanel();

				break;

			case EditorConstants.EDIT_SKILL:
				nameID = "SkillName";
				jPanel1 = new SkillBasePanel();

				break;

			case EditorConstants.EDIT_SPELL:
				nameID = "SpellName";
				jPanel1 = new SpellBasePanel();

				break;

			case EditorConstants.EDIT_TEMPLATE:
				nameID = "TemplateName";
				jPanel1 = new TemplateBasePanel();

				break;

			case EditorConstants.EDIT_CAMPAIGN:
				nameID = "CampaignName";
				jPanel1 = new SourceBasePanel();

				break;

			default:
				//jPanel1 = new BasePanel();

				break;
		}

		pnlProductIdentity = new JPanel();
		chkProductIdentity = new JCheckBox();
		pnlSource = new JPanel();
		lblSource = new JLabel();
		txtSource = new JTextField();

		setLayout(new GridBagLayout());

		pnlName.setLayout(new GridBagLayout());

		lblName.setText(LanguageBundle.getString("in_dem" + nameID));
		lblName.setDisplayedMnemonic(LanguageBundle.getMnemonic("in_mn_" + nameID));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		pnlName.add(lblName, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		pnlName.add(txtName, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		add(pnlName, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridheight = 8;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(jPanel1, gridBagConstraints);

		//In working with Source .pcc files the Product Identity and Source Information panels have no meaning.
		//They are worked in the .pcc file, so, only add these panels for every basepanel that is not a "Source" basepanel.
		if (editType != EditorConstants.EDIT_CAMPAIGN)
		{
			pnlProductIdentity.setLayout(new FlowLayout(FlowLayout.RIGHT));

			chkProductIdentity.setText(LanguageBundle.getString("in_demProIden"));
			chkProductIdentity.setMnemonic(LanguageBundle.getMnemonic("in_mn_demProIden"));
			chkProductIdentity.setHorizontalTextPosition(SwingConstants.LEADING);
			pnlProductIdentity.add(chkProductIdentity);

			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridy = 10;
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			add(pnlProductIdentity, gridBagConstraints);

			pnlSource.setLayout(new GridBagLayout());

			lblSource.setLabelFor(txtSource);
			lblSource.setText(LanguageBundle.getString("in_sourceLabel"));
			lblSource.setDisplayedMnemonic(LanguageBundle.getMnemonic("in_mn_sourceLabel"));
			lblSource.setPreferredSize(new Dimension(70, 16));
			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.insets = new Insets(2, 5, 2, 5);
			pnlSource.add(lblSource, gridBagConstraints);

			txtSource.setPreferredSize(new Dimension(280, 20));
			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.insets = new Insets(2, 5, 2, 5);
			pnlSource.add(txtSource, gridBagConstraints);

			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridy = 9;
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			add(pnlSource, gridBagConstraints);
		} //End if for Product Identity and Source Information panels
	}
}
