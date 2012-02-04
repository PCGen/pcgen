/*
 * CreateSourceDialog.java
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
 *
 * Created on 18/11/2008 08:26:36
 *
 * $Id$
 */
package pcgen.gui.sources;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import pcgen.core.SettingsHandler;
import pcgen.gui.utils.Utility;
import pcgen.persistence.PersistenceManager;
import pcgen.util.Logging;
import pcgen.system.LanguageBundle;


/**
 * The Class <code>CreateSourceDialog</code> is responsible for 
 * providing the user interface that the user users to create a 
 * new source collection that will be displayed in the 
 * SourceSelectionDialog.
 * 
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
@SuppressWarnings("serial")
class CreateSourceDialog extends JDialog implements ActionListener
{
	JTextField sourceTitle = new JTextField();
	
	/** The Constant ACTION_CANCEL. */
	private static final String ACTION_CANCEL = "cancel";
	
	/** The Constant ACTION_OK. */
	private static final String ACTION_OK = "OK";
	
	/**
	 * Creates new form CreateSourceDialog.
	 * 
	 * @param parent the parent dialog or window.
	 * @param modal Should the dialog block the program
	 */
	public CreateSourceDialog(Frame parent, boolean modal)
	{
		super(parent, modal);
		setTitle(LanguageBundle.getString("in_cs_title"));
		initComponents();
		setLocationRelativeTo(parent); // centre on parent
	}

	/**
	 * Create the dialog's user interface.
	 */
	private void initComponents()
	{
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new java.awt.GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(4, 4, 4, 4);

		
		JLabel introLabel = new JLabel(LanguageBundle.getString("in_cs_intro"));
		Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1, 100, 100,
			GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
		getContentPane().add(introLabel, gbc);
		
		MainSource mainSource = new MainSource(true);
		Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1, 100, 100,
			GridBagConstraints.BOTH, GridBagConstraints.WEST);
		getContentPane().add(mainSource, gbc);

		JLabel fieldLabel = new JLabel(LanguageBundle.getString("in_cs_sourceTitle"));
		Utility.buildRelativeConstraints(gbc, 1, 1, 100, 100,
			GridBagConstraints.NONE, GridBagConstraints.WEST);
		getContentPane().add(fieldLabel, gbc);
		sourceTitle = new JTextField(30);
		Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1, 100, 100,
			GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
		getContentPane().add(sourceTitle, gbc);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		JButton okButton = new JButton(LanguageBundle.getString("in_ok"));
		okButton.setActionCommand(ACTION_OK);
		getRootPane().setDefaultButton(okButton);
		buttonPanel.add(okButton);

		JButton cancelButton =
				new JButton(LanguageBundle.getString("in_cancel"));
		cancelButton.setActionCommand(ACTION_CANCEL);
		buttonPanel.add(cancelButton);

		Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1, 0.0, 0.0,
			GridBagConstraints.NONE, GridBagConstraints.EAST);
		getContentPane().add(buttonPanel, gbc);

		//Listen for actions on the buttons
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);

		pack();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (ACTION_OK.equals(e.getActionCommand()))
		{
			List<URI> campList = PersistenceManager.getInstance().getChosenCampaignSourcefiles();

			String name = sourceTitle.getText();
			//JOptionPane.showInputDialog(this, "Enter PCC filename");

			if (name == null || name.trim().length()==0)
			{
				JOptionPane.showMessageDialog(this, LanguageBundle.getString("in_cs_noTitle"));
				return;
			}

			String filename = SourceSelectionUtils.sanitiseFilename(name, ".pcc");
			writePCC(campList, filename, name);

		}
		setVisible(false);
		this.dispose();
	}

	/**
	 * Write pcc.
	 * 
	 * @param campList the list of campaigns
	 * @param filename the name of the pcc file
	 * @param name the name of the source collection
	 */
	private void writePCC(List<URI> campList, String filename, String name)
	{
		try
		{
			FileOutputStream fout =
					new FileOutputStream(new File(SettingsHandler
						.getPccFilesLocation(), filename));
			PrintStream pr = new PrintStream(fout);
			pr.println("CAMPAIGN:" + name);

			pr.println("GAMEMODE:" + SettingsHandler.getGame().getName());
			pr.println("TYPE:Custom");
			pr.println("RANK:1");
			pr.println("SHOWINMENU:YES");
			pr.println("SOURCELONG:Custom - " + name);
			pr.println("SOURCESHORT:Custom");
			pr.println("SOURCEWEB:http://pcgen.sf.net");
			pr.println("");

			for (URI uri : campList)
			{
				String absPath = uri.getPath();
				String relPath = SourceSelectionUtils.convertPathToDataPath(absPath);
				pr.println("PCC:"+relPath);
				
			}
			pr.close();
			fout.close();
		}
		catch (IOException e1)
		{
			Logging.errorPrint("Error writing new simple source", e1);
		}
	}

	
}