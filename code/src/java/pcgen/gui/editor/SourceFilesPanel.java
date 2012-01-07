/*
 * SourceFilesPanel.java
 * Copyright 2003 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on March 14, 2003, 2:57:20 PM
 *
 * @(#) $Id$
 */
package pcgen.gui.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.gui.utils.JTableEx;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * <code>SourceFilesPanel</code>
 *
 * @author  Bryan McRoberts <merton_monk@yahoo.com>
 * @version $Revision$
 */
final class SourceFilesPanel extends BasePanel<Campaign>
{
	static final long serialVersionUID = 2833296242093657468L;
	private Campaign theCampaign = null;
	private JComboBoxEx fileType;
	private JScrollPane scrollPane;
	private JTableEx fileTable;
	private SourceFileModel fileModel;

	/** Creates new form SourceFilesPanel */
	public SourceFilesPanel()
	{
		initComponents();
		intComponentContents();
	}

	@Override
	public void updateData(Campaign thisPObject)
	{
		List<String> fList = fileModel.getFileList();
		List<String> lList = fileModel.getLocationList();
		Iterator<String> j = lList.iterator();
		theCampaign.removeListFor(ListKey.FILE_ABILITY_CATEGORY);
		theCampaign.removeListFor(ListKey.FILE_ABILITY);
		theCampaign.removeListFor(ListKey.FILE_ARMOR_PROF);
		theCampaign.removeListFor(ListKey.FILE_BIO_SET);
		theCampaign.removeListFor(ListKey.FILE_CLASS);
		theCampaign.removeListFor(ListKey.FILE_COMPANION_MOD);
		theCampaign.removeListFor(ListKey.FILE_COVER);
		theCampaign.removeListFor(ListKey.FILE_DEITY);
		theCampaign.removeListFor(ListKey.FILE_DOMAIN);
		theCampaign.removeListFor(ListKey.FILE_EQUIP);
		theCampaign.removeListFor(ListKey.FILE_EQUIP_MOD);
		theCampaign.removeListFor(ListKey.FILE_FEAT);
		theCampaign.removeListFor(ListKey.FILE_KIT);
		theCampaign.removeListFor(ListKey.FILE_LANGUAGE);
		theCampaign.removeListFor(ListKey.FILE_LOGO);
		theCampaign.removeListFor(ListKey.FILE_LST_EXCLUDE);
		theCampaign.removeListFor(ListKey.FILE_RACE);
		theCampaign.removeListFor(ListKey.FILE_SHIELD_PROF);
		theCampaign.removeListFor(ListKey.FILE_SPELL);
		theCampaign.removeListFor(ListKey.FILE_SPELL);
		theCampaign.removeListFor(ListKey.FILE_TEMPLATE);
		theCampaign.removeListFor(ListKey.FILE_WEAPON_PROF);
		theCampaign.removeListFor(ListKey.COMMENT);

		LoadContext context = Globals.getContext();
		for (Iterator<String> i = fList.iterator(); i.hasNext();)
		{
			String name = i.next();
			String val = j.next();

			if (name.equals("COMMENT"))
			{
				theCampaign.addToListFor(ListKey.COMMENT, val);
			}
			else
			{
				context.unconditionallyProcess(theCampaign, name, val);
			}
		}
	}

	@Override
	public void updateView(Campaign thisPObject)
	{
		theCampaign = thisPObject;

		Collection<String> lines = Globals.getContext().unparse(theCampaign);
		if (lines != null)
		{
			for (String a : lines)
			{

				if (a.startsWith("#"))
				{
					fileModel.addFileAndLocation("COMMENT", a.substring(1));
				}
				else
				{
					String b = a.substring(0, a.indexOf(":"));
					fileModel.addFileAndLocation(b, a.substring(b.length() + 1));
				}
			}
		}

		fileTable.updateUI();
	}

	/*
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		setLayout(new BorderLayout());
		scrollPane = new JScrollPane();
		fileTable = new JTableEx();
		fileModel = new SourceFileModel();
		fileType = new JComboBoxEx();
		fileType.addItem("BIOSET");
		fileType.addItem("CLASS");
		fileType.addItem("COMPANIONMOD");
		fileType.addItem("COMMENT");
		fileType.addItem("DEITY");
		fileType.addItem("DOMAIN");
		fileType.addItem("EQUIPMENT");
		fileType.addItem("EQUIPMOD");
		fileType.addItem("FEAT");
		fileType.addItem("LANGUAGE");
		fileType.addItem("LSTEXCLUDE");
		fileType.addItem("KIT");
		fileType.addItem("PCC");
		fileType.addItem("RACE");
		fileType.addItem("REGION");
		fileType.addItem("SKILL");
		fileType.addItem("SPELL");
		fileType.addItem("TEMPLATE");
		fileType.addItem("WEAPONPROF");
		fileType.setSelectedIndex(0);

		JPanel aPanel = new JPanel();
		aPanel.add(fileType);

		JButton aButton = new JButton("Add");
		aButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent evt)
				{
					String loc = "";
					String d = null;

					if (theCampaign != null)
					{
						d = theCampaign.getSafe(StringKey.DESTINATION);
					}

					if ((d == null) || d.equals(""))
					{
						URI uri = theCampaign.getSourceURI();
						if (uri != null)
						{
							d = uri.getPath();
						}
					}

					if ((d == null) || d.equals(""))
					{
						d = SettingsHandler.getPccFilesLocation().toString();
					}

					if (!fileType.getSelectedItem().toString().equals("COMMENT"))
					{
						final JFileChooser fc = new JFileChooser();
//						 Initialize title with current directory
					    File curDir = fc.getCurrentDirectory();
					    fc.setDialogTitle(""+curDir.getAbsolutePath());

					    // Add listener on chooser to detect changes to current directory
					    fc.addPropertyChangeListener(new PropertyChangeListener() {
					        @Override
							public void propertyChange(PropertyChangeEvent event) {
					            if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(event.getPropertyName())) {
					                File aCurDir = fc.getCurrentDirectory();

					                fc.setDialogTitle(""+aCurDir.getAbsolutePath());
					            }
					        }
					    }) ;

						fc.setCurrentDirectory(new File(d));

						int returnVal = fc.showOpenDialog(SourceFilesPanel.this);

						if (returnVal == JFileChooser.APPROVE_OPTION)
						{
							loc = fc.getSelectedFile().toString();

							if (loc.startsWith(SettingsHandler.getPccFilesLocation().toString()))
							{
								loc = loc.substring(SettingsHandler.getPccFilesLocation().toString().length() + 1);
							}
							else if (loc.startsWith(theCampaign.getSourceURI().toString()))
							{
								loc = loc.substring(theCampaign.getSourceURI().toString().length() + 1);
							}

							if (!(loc.indexOf('\\') < 0)) //checking to see if the @ should be added if not only file name
							{
								loc = "@".concat(loc);
							}
						}
					}

					fileModel.addFileAndLocation(fileType.getSelectedItem().toString(), loc);
					fileTable.updateUI();
				}
			});
		aPanel.add(aButton);
		aButton = new JButton("Remove");
		aButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent evt)
				{
					int row = SourceFilesPanel.this.fileTable.getSelectedRow();
					fileModel.removeItemsAtIndex(row);
					fileTable.updateUI();
				}
			});
		aPanel.add(aButton);
		add(aPanel, BorderLayout.SOUTH);

		fileTable.setModel(fileModel);
		fileTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fileTable.setDoubleBuffered(false);
		scrollPane.setViewportView(fileTable);
		add(scrollPane, BorderLayout.CENTER);
	}

	private void intComponentContents()
	{
	    // TODO This method currently does nothing?
	}

	final class SourceFileModel extends AbstractTableModel
	{
		List<String> fileList = new ArrayList<String>();
		List<String> locationList = new ArrayList<String>();

		@Override
		public boolean isCellEditable(int rowIndex, int colIndex)
		{
			return (colIndex == 1);
		}

		@Override
		public Class<String> getColumnClass(int columnIndex)
		{
			return String.class;
		}

		@Override
		public int getColumnCount()
		{
			return 2;
		}

		public List<String> getFileList()
		{
			return fileList;
		}

		public List<String> getLocationList()
		{
			return locationList;
		}

		@Override
		public int getRowCount()
		{
			return fileList.size();
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			if ((columnIndex == 1) && (rowIndex < locationList.size()))
			{
				String vString = (String) aValue;
				locationList.set(rowIndex, vString);
			}
		}

		@Override
		public String getColumnName(int columnIndex)
		{
			switch (columnIndex)
			{
				case 0:
					return "File Type";

				case 1:
					return "File Name/Location";

				default:
					break;
			}

			return "Out Of Bounds";
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			String rc = null;

			switch (columnIndex)
			{
				case 0:

					if (rowIndex < fileList.size())
					{
						rc = fileList.get(rowIndex);
					}
					else
					{
						rc = "";
					}

					break;

				case 1:

					if (rowIndex < locationList.size())
					{
						rc = locationList.get(rowIndex);
					}
					else
					{
						rc = "";
					}

					break;

				default:
					Logging.errorPrint("Unhandled column " + columnIndex + " at SourceFilesPanel.getValueAt");

					break;
			}

			return rc;
		}

		public void addFileAndLocation(String filetype, String filelocation)
		{
			int row = SourceFilesPanel.this.fileTable.getSelectedRow();

			if (fileList.size() == 0)
			{
				row = 0;
			}
			else if (row < 0)
			{
				row = fileList.size() - 1;
			}

			fileList.add(row, filetype);
			locationList.add(row, filelocation);
		}

		public void removeItemsAtIndex(int loc)
		{
			if ((loc >= 0) && (loc < fileList.size()))
			{
				fileList.remove(loc);
				locationList.remove(loc);
			}
		}
	}
}
