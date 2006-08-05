/*
 * SourceBasePanel
 * Copyright 2003 (C) Bryan McRoberts <merton.monk@codemonkeypublishing.com >
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
 * Created on January 8, 2003, 8:15 PM
 *
 * @(#) $Id$
 */
package pcgen.gui.editor;

import pcgen.core.Campaign;
import pcgen.core.PObject;
import pcgen.core.SettingsHandler;
import pcgen.core.Source;
import pcgen.core.SourceEntry;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.gui.utils.JTableEx;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.*;

/**
 * <code>SourceBasePanel</code>
 *
 * @author  Bryan McRoberts <merton.monk@codemonkeypublishing.com>
 */
class SourceBasePanel extends BasePanel
{
	static final long serialVersionUID = -8057486950329356072L;
	private Campaign theCampaign = null;
	private JCheckBox isD20;
	private JCheckBox isLicensed;
	private JCheckBox isOGL;
	private JCheckBox showInMenu;
	private JCheckBox gm35e;
	private JCheckBox gm3e;
	private JCheckBox gmmodern;
	private JCheckBox gmspycraft;
	private JCheckBox gmxcrawl;
	private JCheckBox gmsidewinder;
	private JComboBoxEx bookType;
	private JScrollPane scrollPane;
	private JTableEx sourceTable;
	private JTextField destination;
	private String game;
	private JTextField genre;
	private JTextField infoText;
	private JTextField pubNameLong;
	private JTextField pubNameShort;
	private JTextField pubNameWeb;
	private JTextField setting;
	private SourceTableModel sourceModel;
	private JSpinner rank;

	/** Creates new form SourceBasePanel */
	public SourceBasePanel()
	{
		initComponents();
		intComponentContents();
	}

	public void updateData(PObject thisPObject)
	{
		theCampaign.setRank(new Integer(rank.getValue().toString()).intValue());
		game = "";
		StringBuffer tempBuff = new StringBuffer(30);
		if (gm35e.isSelected())
		{
			if (tempBuff.length() == 0)
				tempBuff.append("35e");
			else
				tempBuff.append("|35e");
		}
		if (gm3e.isSelected())
		{
			if (tempBuff.length() == 0)
				tempBuff.append("3e");
			else
				tempBuff.append("|3e");
		}
		if (gmmodern.isSelected())
		{
			if (tempBuff.length() == 0)
				tempBuff.append("Modern");
			else
				tempBuff.append("|Modern");
		}
		if (gmspycraft.isSelected())
		{
			if (tempBuff.length() == 0)
				tempBuff.append("Spycraft");
			else
				tempBuff.append("|Spycraft");
		}
		if (gmxcrawl.isSelected())
		{
			if (tempBuff.length() == 0)
				tempBuff.append("XCrawl");
			else
				tempBuff.append("|XCrawl");
		}
		if (gmsidewinder.isSelected())
		{
			if (tempBuff.length() == 0)
				tempBuff.append("Sidewinder");
			else
				tempBuff.append("|Sidewinder");
		}
		game = tempBuff.toString();
		theCampaign.setGameMode(game);
		final Source source = new Source();
		source.setLongName( pubNameLong.getText().trim() );
		source.setShortName( pubNameShort.getText().trim() );
		source.setWebsite( pubNameWeb.getText().trim() );
		theCampaign.setSource( new SourceEntry(source) );
		theCampaign.setIsOGL(isOGL.getSelectedObjects() != null);
		theCampaign.setIsD20(isD20.getSelectedObjects() != null);
		theCampaign.setShowInMenu(showInMenu.getSelectedObjects() != null);
		theCampaign.setIsLicensed(isLicensed.getSelectedObjects() != null);
		theCampaign.setInfoText(infoText.getText().trim());
		theCampaign.setBookType(bookType.getSelectedItem().toString());
		theCampaign.setDestination(destination.getText().trim());
		theCampaign.addLicense(".CLEAR");
		theCampaign.addSection15(".CLEAR");
		theCampaign.setSetting(setting.getText().trim());
		theCampaign.setGenre(genre.getText().trim());

		Properties options = new Properties();

		for (int i = 0; i < sourceModel.getOptionList().size(); i++)
		{
			options.setProperty(sourceModel.getOptionList().get(i).toString(),
			    sourceModel.getOptionValues().get(i).toString());
		}

		theCampaign.setOptions(options);

		for (Iterator i = sourceModel.getLicenseList().iterator(); i.hasNext();)
		{
			theCampaign.addLicense((String) i.next());
		}

		for (Iterator i = sourceModel.getCopyrightList().iterator(); i.hasNext();)
		{
			theCampaign.addSection15((String) i.next());
		}
	}

	public void updateView(PObject thisPObject)
	{
		if (!(thisPObject instanceof Campaign))
		{
			return;
		}

		theCampaign = (Campaign) thisPObject;
		sourceModel.setLists(theCampaign.getOptionsList(), theCampaign.getLicenses(),
		    theCampaign.getSection15s());
		rank.setValue(new Integer(theCampaign.getRank()));
		game = theCampaign.getGameModeString();

		final StringTokenizer aTok = new StringTokenizer(game, ", ");

		while (aTok.hasMoreTokens())
		{
			final String aName = aTok.nextToken();
			if ("35e".equals(aName))
				gm35e.setSelected(true);
			else if ("3e".equals(aName))
				gm3e.setSelected(true);
			else if ("Modern".equals(aName))
				gmmodern.setSelected(true);
			else if ("Spycraft".equals(aName))
				gmspycraft.setSelected(true);
			else if ("XCrawl".equals(aName))
				gmxcrawl.setSelected(true);
			else if ("Sidewinder".equals(aName))
				gmsidewinder.setSelected(true);
		}
		final Source source = theCampaign.getSourceEntry().getSourceBook();
		if ( source != null )
		{
			pubNameLong.setText( source.getLongName() );
			pubNameShort.setText( source.getShortName() );
			pubNameWeb.setText( source.getWebsite() );
		}
		pubNameLong.setCaretPosition(0); //Scroll to beginning of inserted text
		pubNameShort.setCaretPosition(0);
		pubNameWeb.setCaretPosition(0);
		isOGL.setSelected(theCampaign.isOGL());
		isD20.setSelected(theCampaign.isD20());
		isLicensed.setSelected(theCampaign.isLicensed());
		showInMenu.setSelected(theCampaign.canShowInMenu());
		infoText.setText(theCampaign.getInfoText());
		infoText.setCaretPosition(0); //Scroll to beginning of inserted text
		bookType.setSelectedItem(theCampaign.getBookType());
		setting.setText(theCampaign.getSetting());
		setting.setCaretPosition(0); //Scroll to beginning of inserted text
		genre.setText(theCampaign.getGenre());
		genre.setCaretPosition(0); //Scroll to beginning of inserted text

		String a = theCampaign.getDestination();

		if (a.equals("") && (theCampaign.getSourceFile() != null))
		{
			a = theCampaign.getSourceFile();

			if (a.startsWith("file:"))
			{
				a = a.substring(6);
			}

			a = a.replace('\\', File.separator.charAt(0));
			a = a.replace('/', File.separator.charAt(0));

			String b = SettingsHandler.getPccFilesLocation().toString();
			b = b.replace('\\', File.separator.charAt(0));
			b = b.replace('/', File.separator.charAt(0));

			if (a.startsWith(b))
			{
				a = a.substring(b.length() + 1);
			}

			destination.setText(a);
		}
		else
		{
			destination.setText(theCampaign.getDestination());
		}
		destination.setCaretPosition(0); //Scroll to beginning of inserted text
	}

	private void addCopyright()
	{
		sourceModel.addCopyright();
	}

	private void addLicense()
	{
		sourceModel.addLicense();
	}

	private void addOption()
	{
		sourceModel.addOption();
	}

	/*
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		JLabel jLabel12, jLabel13;

		scrollPane = new JScrollPane();
		sourceTable = new JTableEx();
		sourceModel = new SourceTableModel();

		gm35e = new JCheckBox("35e");
		gm3e  = new JCheckBox("3e");
		gmmodern = new JCheckBox("MSRD Modern");
		gmspycraft = new JCheckBox("Spycraft");
		gmxcrawl = new JCheckBox("XCrawl");
		gmsidewinder = new JCheckBox("Sidewinder");

		infoText = new JTextField();
		// SwingConstants.LEFT is equivalent to JTextField.LEFT but more
		// 'correct' in a Java coding context (it is a static reference)
		infoText.setHorizontalAlignment(SwingConstants.LEFT);
		pubNameWeb = new JTextField();
		pubNameWeb.setHorizontalAlignment(SwingConstants.LEFT);
		pubNameShort = new JTextField();
		pubNameShort.setHorizontalAlignment(SwingConstants.LEFT);
		pubNameLong = new JTextField();
		pubNameLong.setHorizontalAlignment(SwingConstants.LEFT);
		setting = new JTextField();
		setting.setHorizontalAlignment(SwingConstants.LEFT);
		destination = new JTextField();
		destination.setHorizontalAlignment(SwingConstants.LEFT);
		genre = new JTextField();
		genre.setHorizontalAlignment(SwingConstants.LEFT);

		isOGL = new JCheckBox("OGL");
		isD20 = new JCheckBox("D20");
		showInMenu = new JCheckBox("In Menu");
		isLicensed = new JCheckBox("Licensed");
		bookType = new JComboBoxEx();
		bookType.setEditable(true);
		bookType.addItem("Campaign Setting");
		bookType.addItem("Core Rulebook ");
		bookType.addItem("Magazine");
		bookType.addItem("Module");
		bookType.addItem("Sourcebook");
		bookType.addItem("Supplement");
		bookType.addItem("Web Enhancement");
		bookType.setSelectedIndex(0);

		setLayout(new BorderLayout());
		JPanel aPanel = new JPanel(new GridBagLayout());
		jLabel12 = new JLabel(
	        "Rank is a number between 1 and 9. Higher Rank loads before lower " +
	        "Rank. Rank 1 highest.");
	    jLabel13 = new JLabel(SettingsHandler.getPccFilesLocation().toString());
	    //	  Create a number spinner that only handles values in the range [1,9]
	    int min = 1;
	    int max = 9;
	    int step = 1;
	    int initValue = 9;
	    SpinnerModel model = new SpinnerNumberModel(initValue, min, max, step);
	    rank = new JSpinner(model);
	    JButton bButton = new JButton("Browse...");
		bButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					String loc = destination.getText();
					String d = "";

					if (theCampaign != null)
					{
						d = theCampaign.getDestination();
					}

					if ((d == null) || d.equals(""))
					{
						d = SettingsHandler.getPccFilesLocation().toString().concat("\\").concat(loc);
					}

					final JFileChooser fc = new JFileChooser();
					// Initialize title with current directory
					    File curDir = fc.getCurrentDirectory();
					    fc.setDialogTitle(""+curDir.getAbsolutePath());

				    // Add listener on chooser to detect changes to current directory
					    fc.addPropertyChangeListener(new PropertyChangeListener() {
					    	public void propertyChange(PropertyChangeEvent event) {
					    		if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(event.getPropertyName())) {
					    			File aCurDir = fc.getCurrentDirectory();

					    			fc.setDialogTitle(""+aCurDir.getAbsolutePath());
					    		}
					    	}
					    }) ;

					fc.setCurrentDirectory(new File(d));

					int returnVal = fc.showOpenDialog(SourceBasePanel.this);

					if (returnVal == JFileChooser.APPROVE_OPTION)
					{
						loc = fc.getSelectedFile().toString();

						if (loc.startsWith(SettingsHandler.getPccFilesLocation().toString()))
						{
							loc = loc.substring(SettingsHandler.getPccFilesLocation().toString().length() + 1);
						}
						else if (loc.startsWith(theCampaign.getSourceFile()))
						{
							loc = loc.substring(theCampaign.getSourceFile().length() + 1);
						}
					}

					destination.setText(loc);
				}
			});


	    aPanel.add(new JLabel("Location of file"), new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
	                                             , GridBagConstraints.CENTER,
	                                             GridBagConstraints.NONE,
	                                             new Insets(0, 0, 0, 0), 0, 0));
	    aPanel.add(rank, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
	                                               , GridBagConstraints.CENTER,
	                                               GridBagConstraints.NONE,
	                                               new Insets(0, 0, 0, 0), 0, 0));
	    aPanel.add(gm35e, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
	                                                , GridBagConstraints.CENTER,
	                                                GridBagConstraints.NONE,
	                                                new Insets(0, 0, 0, 0), 0, 0));
	    aPanel.add(gm3e, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
	                                                , GridBagConstraints.CENTER,
	                                                GridBagConstraints.NONE,
	                                                new Insets(0, 0, 0, 0), 0, 0));
	    aPanel.add(gmmodern, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
	                                                , GridBagConstraints.CENTER,
	                                                GridBagConstraints.NONE,
	                                                new Insets(0, 0, 0, 0), 0, 0));
	    aPanel.add(gmspycraft, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0
	                                                , GridBagConstraints.CENTER,
	                                                GridBagConstraints.NONE,
	                                                new Insets(0, 0, 0, 0), 0, 0));
	    aPanel.add(gmxcrawl, new GridBagConstraints(6, 1, 1, 1, 0.0, 0.0
	                                                , GridBagConstraints.CENTER,
	                                                GridBagConstraints.NONE,
	                                                new Insets(0, 0, 0, 0), 0, 0));
	    aPanel.add(gmsidewinder, new GridBagConstraints(7, 1, 1, 1, 0.0, 0.0
	                                                , GridBagConstraints.CENTER,
	                                                GridBagConstraints.NONE,
	                                                new Insets(0, 0, 0, 0), 0, 0));
	    aPanel.add(bButton, new GridBagConstraints(8, 6, 1, 1, 0.0, 0.0
	                                              , GridBagConstraints.CENTER,
	                                              GridBagConstraints.NONE,
	                                              new Insets(0, 0, 0, 0), 0, 0));
	    aPanel.add(new JLabel("Info Text"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
	                                             , GridBagConstraints.EAST,
	                                             GridBagConstraints.NONE,
	                                             new Insets(5, 5, 5, 2), 0, 0));
	    aPanel.add(new JLabel("Game Mode"), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
	                                             , GridBagConstraints.EAST,
	                                             GridBagConstraints.NONE,
	                                             new Insets(5, 5, 5, 2), 0, 0));
	    aPanel.add(new JLabel("Pub Name Web"), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
	                                             , GridBagConstraints.EAST,
	                                             GridBagConstraints.NONE,
	                                             new Insets(5, 5, 5, 2), 0, 0));
	    aPanel.add(new JLabel("Pub Name Long"), new GridBagConstraints(6, 2, 1, 1, 0.0, 0.0
	                                             , GridBagConstraints.EAST,
	                                             GridBagConstraints.NONE,
	                                             new Insets(5, 2, 5, 2), 0, 0));
	    aPanel.add(new JLabel("Pub Name Short"), new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0
	                                              , GridBagConstraints.EAST,
	                                              GridBagConstraints.NONE,
	                                              new Insets(5, 2, 5, 2), 0, 0));
	    aPanel.add(new JLabel("Setting"), new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
	                                             , GridBagConstraints.EAST,
	                                             GridBagConstraints.NONE,
	                                             new Insets(5, 5, 5, 2), 0, 0));
	    aPanel.add(new JLabel("Genre"), new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0
	                                             , GridBagConstraints.EAST,
	                                             GridBagConstraints.NONE,
	                                             new Insets(5, 2, 5, 2), 0, 0));
	    aPanel.add(new JLabel("Book Type"), new GridBagConstraints(6, 3, 1, 1, 0.0, 0.0
	                                              , GridBagConstraints.EAST,
	                                              GridBagConstraints.NONE,
	                                              new Insets(5, 2, 5, 2), 0, 0));
	    aPanel.add(new JLabel("Rank"), new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
	                                             , GridBagConstraints.EAST,
	                                             GridBagConstraints.NONE,
	                                             new Insets(5, 5, 5, 2), 0, 0));
	    aPanel.add(new JLabel("Current Directory that Location of file is relative to:"),
	    										new GridBagConstraints(0, 5, 5, 1, 0.0, 0.0
	                                             , GridBagConstraints.EAST,
	                                             GridBagConstraints.HORIZONTAL,
	                                             new Insets(5, 5, 5, 2), 0, 0));
	    aPanel.add(jLabel13, new GridBagConstraints(4, 5, 5, 1, 0.0, 0.0
	                                              , GridBagConstraints.WEST,
	                                              GridBagConstraints.HORIZONTAL,
	                                              new Insets(5, 2, 5, 5), 0, 0));
	    aPanel.add(infoText, new GridBagConstraints(1, 0, 8, 1, 1.0, 0.0
	                                                 , GridBagConstraints.WEST,
	                                                 GridBagConstraints.HORIZONTAL,
	                                                 new Insets(5, 2, 5, 5), 0, 0));
	    aPanel.add(pubNameWeb, new GridBagConstraints(1, 2, 2, 1, 0.8, 0.0
	                                                 , GridBagConstraints.WEST,
	                                                 GridBagConstraints.HORIZONTAL,
	                                                 new Insets(5, 2, 5, 2), 0, 0));
	    aPanel.add(pubNameLong, new GridBagConstraints(7, 2, 2, 1, 0.1, 0.0
	                                                 , GridBagConstraints.WEST,
	                                                 GridBagConstraints.HORIZONTAL,
	                                                 new Insets(5, 2, 5, 5), 0, 0));
	    aPanel.add(pubNameShort, new GridBagConstraints(4, 2, 2, 1, 0.1, 0.0
	                                                 , GridBagConstraints.WEST,
	                                                 GridBagConstraints.HORIZONTAL,
	                                                 new Insets(5, 2, 5, 2), 0, 0));
	    aPanel.add(setting, new GridBagConstraints(1, 3, 2, 1, 0.8, 0.0
	                                                 , GridBagConstraints.WEST,
	                                                 GridBagConstraints.HORIZONTAL,
	                                                 new Insets(5, 2, 5, 2), 0, 0));
	    aPanel.add(genre, new GridBagConstraints(4, 3, 2, 1, 0.1, 0.0
	                                                 , GridBagConstraints.WEST,
	                                                 GridBagConstraints.HORIZONTAL,
	                                                 new Insets(5, 2, 5, 2), 0, 0));
	    aPanel.add(bookType, new GridBagConstraints(7, 3, 2, 1, 0.1, 0.0
	                                                , GridBagConstraints.WEST,
	                                                GridBagConstraints.HORIZONTAL,
	                                                new Insets(5, 2, 5, 5), 0, 0));
	    aPanel.add(destination, new GridBagConstraints(1, 6, 7, 1, 0.9, 0.0
	                                                 , GridBagConstraints.WEST,
	                                                 GridBagConstraints.HORIZONTAL,
	                                                 new Insets(5, 2, 5, 2), 0, 0));
	    aPanel.add(jLabel12, new GridBagConstraints(2, 4, 7, 1, 0.0, 0.0
	                                              , GridBagConstraints.WEST,
	                                              GridBagConstraints.HORIZONTAL,
	                                              new Insets(0, 0, 0, 0), 0, 0));

		add(aPanel, BorderLayout.NORTH);

		sourceTable.setModel(sourceModel);
		sourceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sourceTable.setDoubleBuffered(false);
		scrollPane.setViewportView(sourceTable);
		add(scrollPane, BorderLayout.CENTER);

		aPanel = new JPanel();
		final JComboBoxEx legalCombo = new JComboBoxEx();
		legalCombo.addItem("COPYRIGHT");
		legalCombo.addItem("LICENSE");
		legalCombo.addItem("OPTION");
		legalCombo.setSelectedIndex(0);
		aPanel.add(legalCombo);

		JButton aButton = new JButton("Add Tag");
		aButton.setToolTipText("Adds the specified tag line in .pcc file");
		aButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					switch (legalCombo.getSelectedIndex())
					{
						case 0 : //COPYRIGHT
							addCopyright();
							break;
						case 1 : //LICENSE
							addLicense();
							break;
						case 2 : //OPTION
							addOption();
							break;
					}
					sourceTable.updateUI();
				}
			});
		aPanel.add(aButton);
		aButton = new JButton("Remove Tag");
		aButton.setToolTipText("Removes selected tag line from being inserted in .pcc file");
		aButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					removeLine();
					sourceTable.updateUI();
				}
			});
		aPanel.add(aButton);
		aPanel.add(showInMenu);
		aPanel.add(isLicensed);
		aPanel.add(isOGL);
		aPanel.add(isD20);

		add(aPanel, BorderLayout.SOUTH);

	}

	private void intComponentContents()
	{
	    // TODO This method currently does nothing?
	}

	private void removeLine()
	{
		sourceModel.removeLine(sourceTable.getSelectedRow());
	}

	final class SourceTableModel extends AbstractTableModel
	{
		List copyrightList = null;
		List licenseList = null;
		List optionList = null;
		List optionValues = null;

		public boolean isCellEditable(int rowIndex, int colIndex)
		{
			return ((rowIndex < optionList.size()) || (colIndex == 1));
		}

		public Class getColumnClass(int columnIndex)
		{
			return String.class;
		}

		public int getColumnCount()
		{
			return 2;
		}

		public String getColumnName(int columnIndex)
		{
			switch (columnIndex)
			{
				case 0:
					return "Label";

				case 1:
					return "Value";

				default:
					break;
			}

			return "Out Of Bounds";
		}

		public List getCopyrightList()
		{
			return copyrightList;
		}

		public List getLicenseList()
		{
			return licenseList;
		}

		public void setLists(List optList, List licList, List copyList)
		{
			optionList = (optList == null) ? new ArrayList() : optList;
			optionValues = new ArrayList();

			if (optionList != null)
			{
				for (Iterator i = optionList.iterator(); i.hasNext();)
				{
					String aString = (String) i.next();
					String val = SourceBasePanel.this.theCampaign.getOptions().getProperty(aString);
					optionValues.add(val);
				}
			}

			licenseList = (licList == null) ? new ArrayList() : licList;
			copyrightList = (copyList == null) ? new ArrayList() : copyList;
		}

		public List getOptionList()
		{
			return optionList;
		}

		public List getOptionValues()
		{
			return optionValues;
		}

		public int getRowCount()
		{
			if (SourceBasePanel.this.theCampaign == null)
			{
				return 0;
			}

			return optionList.size() + licenseList.size() + copyrightList.size();
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			if (rowIndex < optionList.size())
			{
				String vString = (String) aValue;

				if ((columnIndex == 0) && vString.startsWith("OPTION:"))
				{
					vString = vString.substring(7);
				}

				if (columnIndex == 0)
				{
					optionList.set(rowIndex, vString);
				}
				else if (columnIndex == 1)
				{
					optionValues.set(rowIndex, vString);
				}

				return;
			}

			rowIndex -= optionList.size();

			if (rowIndex < copyrightList.size())
			{
				copyrightList.set(rowIndex, aValue);

				return;
			}

			rowIndex -= copyrightList.size();

			if (rowIndex < licenseList.size())
			{
				licenseList.set(rowIndex, aValue);

				return;
			}
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if (rowIndex < optionList.size())
			{
				String propertyKey = optionList.get(rowIndex).toString();

				if (columnIndex == 0)
				{
					return "OPTION:" + propertyKey;
				}

				return optionValues.get(rowIndex).toString();
			}

			rowIndex -= optionList.size();

			if (rowIndex < copyrightList.size())
			{
				if (columnIndex == 0)
				{
					return "COPYRIGHT:";
				}

				return copyrightList.get(rowIndex).toString();
			}

			rowIndex -= copyrightList.size();

			if (rowIndex < licenseList.size())
			{
				if (columnIndex == 0)
				{
					return "LICENSE:";
				}

				return licenseList.get(rowIndex).toString();
			}

			return "";
		}

		public void addCopyright()
		{
			copyrightList.add("");
		}

		public void addLicense()
		{
			licenseList.add("");
		}

		public void addOption()
		{
			optionList.add("");
			optionValues.add("");
		}

		public void removeLine(int row)
		{
			if (row < 0)
			{
				return;
			}

			if (row < optionList.size())
			{
				optionList.remove(row);
				optionValues.remove(row);

				return;
			}

			row -= optionList.size();

			if (row < copyrightList.size())
			{
				copyrightList.remove(row);

				return;
			}

			row -= copyrightList.size();

			if (row < licenseList.size())
			{
				licenseList.remove(row);
			}
		}
	}
}
