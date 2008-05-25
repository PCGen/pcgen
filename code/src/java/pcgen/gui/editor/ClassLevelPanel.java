/*
 * ClassLevelPanel
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

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.*;
import pcgen.core.bonus.BonusObj;
import pcgen.core.levelability.LevelAbility;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.gui.utils.JTableEx;
import pcgen.gui.utils.TableSorter;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.PCClassLoader;
import pcgen.util.InputFactory;
import pcgen.util.InputInterface;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import pcgen.core.prereq.Prerequisite;

/**
 * <code>ClassLevelPanel</code>
 *
 * @author  Bryan McRoberts <merton.monk@codemonkeypublishing.com>
 */
public class ClassLevelPanel extends JPanel implements PObjectUpdater
{
	static final long serialVersionUID = 1485178774957708877L;
	private static List<LevelTag> levelTagList = new ArrayList<LevelTag>();
	private JButton addBtn = new JButton();
	private JButton delBtn = new JButton();
	private JComboBoxEx tagList = new JComboBoxEx();
	private JScrollPane levelPane;
	private JTableEx levelTable = new JTableEx();
	private JTextField level = new JTextField();
	private LevelModel levelModel = new LevelModel();
	private PCClass obj = null;
	private TableSorter sortedLevelModel = new TableSorter();

	/** Creates new form ClassLevelPanel */
	public ClassLevelPanel()
	{
		initComponents();
	}

	public void updateData(PObject po)
	{
		// Verify the object passed in was the right type (a PCClass) and not null
		if (!(po instanceof PCClass))
		{
			Logging.errorPrint("Invalid PObject passed to ClassLevelPanel.updateData()!");

			return;
		}

		// Cast the object for convenience
		PCClass object = (PCClass) po;

		try
		{
			// Get/Create the dummy campaign for custom data.
			Campaign customCampaign = new Campaign();
			customCampaign.setName("Custom");
			customCampaign.addDescription(new Description("Custom data"));

			// Make sure the object source file is set
			URI sourceFile = object.getSourceURI();

			if (sourceFile == null)
			{
				// Make sure that the source file is in URL format for use in
				// the campaign source entry
				String path = CustomData.customClassFilePath(true);
				if (!path.startsWith("/"))
				{
					path = "/" + path;
				}
				sourceFile = new URI("file", null, path, null);
				object.setSourceURI(sourceFile);
				object.setSourceCampaign(customCampaign);
			}

			// Create the custom source entry
			CampaignSourceEntry tempSource = new CampaignSourceEntry(customCampaign, sourceFile);

			// Clear the level ability list
			object.addAddList(-1, ".CLEAR");

			// Loop through level tags.  Parse them to apply them to the PCClass.
			for (Iterator<LevelTag> i = levelTagList.iterator(); i.hasNext();)
			{
				LevelTag lt = i.next();

				if (lt.needsSaving())
				{
					String b = lt.getLevel() + "\t" + lt.getTag() + ":" + lt.getValue();
					PCClassLoader classLoader = new PCClassLoader();
					classLoader.parseLine(Globals.getContext(), object, b, tempSource);
				}
			}
		}
		catch (URISyntaxException e)
		{
			Logging.errorPrint(e.getLocalizedMessage());
		}
		catch (PersistenceLayerException exc)
		{
			Logging.errorPrint(exc.getMessage());
		}
	}

	public void updateView(PObject po)
	{
		if (!(po instanceof PCClass))
		{
			return;
		}

		levelTagList.clear();
		obj = (PCClass) po;

		List aList = obj.getLevelAbilityList();

		if (aList != null)
		{
			for (Iterator i = aList.iterator(); i.hasNext();)
			{
				LevelAbility la = (LevelAbility) i.next();
				LevelTag lt = new LevelTag(la.level(), LevelTag.TAG_ADD, la.getTagData());
				levelTagList.add(lt);
			}
		}

		aList = obj.getAddDomains();

		if (aList != null)
		{
			for (Iterator i = aList.iterator(); i.hasNext();)
			{
				String v = (String) i.next();
				final int sepPos = v.indexOf('|');
				String l = v.substring(0, sepPos);
				String t = v.substring(sepPos + 1);
				LevelTag lt = new LevelTag(l, LevelTag.TAG_ADDDOMAINS, t);
				levelTagList.add(lt);
			}
		}

		final Iterator bonusIter = obj.getBonusList().iterator();

		while (bonusIter.hasNext())
		{
			// updated 29 Jul 2003 -- sage_sam
			BonusObj bonus = (BonusObj) bonusIter.next();
			String bonusValue = bonus.toString();
			final String levelString = String.valueOf(bonus.getPCLevel());

			if (bonusValue.startsWith(levelString))
			{
				bonusValue = bonusValue.substring(levelString.length() + 1);
			}

			LevelTag lt = new LevelTag(bonus.getPCLevel(), LevelTag.TAG_BONUS, bonusValue);
			levelTagList.add(lt);
		}

		for (int x = 0; x < obj.getVariableCount(); ++x)
		{
			String c = obj.getVariableDefinition(x);
			int y = c.indexOf('|');
			LevelTag lt = new LevelTag(c.substring(0, y), LevelTag.TAG_DEFINE, c.substring(y + 1));
			levelTagList.add(lt);
		}

		if (obj.hasCastList())
		{
			for (Entry<Integer, List<String>> me : obj.getCastProgression().entrySet())
			{
				LevelTag lt = new LevelTag(me.getKey(), LevelTag.TAG_CAST, StringUtil.join(me.getValue(), ","));
				levelTagList.add(lt);
			}
		}
		
		if (obj.hasKnownList()) {
			for (Entry<Integer, List<String>> me : obj.getKnownMap().entrySet())
			{
				LevelTag lt = new LevelTag(me.getKey(), LevelTag.TAG_KNOWN, StringUtil.join(me.getValue(), ","));
				levelTagList.add(lt);
			}
		}

		// Output the list of spells associated with the class.
		for (int i=0;i<=obj.getSpellSupport().getMaxSpellListLevel();i++)
		{
			final List spellList = obj.getSpellSupport().getSpellListForLevel(i);

			if (spellList != null)
			{
				for (Iterator li = spellList.iterator(); li.hasNext();)
				{
					String src = ((PCSpell) li.next()).getPCCText();
					LevelTag lt = new LevelTag(String.valueOf(i), LevelTag.TAG_SPELLS, src);
					levelTagList.add(lt);
				}
			}

		}

		List drList = obj.getDRList();
		for (Iterator i = drList.iterator(); i.hasNext();)
		{
			DamageReduction dr = (DamageReduction) i.next();
			List preList = dr.getPreReqList();
			for (Iterator j = preList.iterator(); j.hasNext();)
			{
				Prerequisite prereq = (Prerequisite) j.next();
				if (DamageReduction.isPrereqForClassLevel(prereq, obj
					.getKeyName()))
				{
					String src = dr.getReduction() + "/" + dr.getBypass();
					LevelTag lt = new LevelTag(prereq.getOperand(),
						LevelTag.TAG_DR, src);
					levelTagList.add(lt);
				}
			}
		}
		
		aList = obj.getListFor(ListKey.SPECIAL_ABILITY);

		if ((aList != null) && (aList.size() != 0))
		{
			for (Iterator se = aList.iterator(); se.hasNext();)
			{
				final SpecialAbility sa = (SpecialAbility) se.next();
				String src = sa.getSASource();
				String lev = src.substring(src.lastIndexOf('|') + 1);

				LevelTag lt = new LevelTag(lev, LevelTag.TAG_SAB, sa.toString());
				levelTagList.add(lt);
			}
		}

		aList = new ArrayList<SpecialAbility>();
		obj.addSABToList(aList, null);

		if (!aList.isEmpty())
		{
			for (Iterator<SpecialAbility> e = aList.iterator(); e.hasNext();)
			{
				SpecialAbility sa = e.next();
				String src = sa.getSASource();
				String lev = src.substring(src.lastIndexOf('|') + 1);

				LevelTag lt = new LevelTag(lev, LevelTag.TAG_SAB, sa.toString());
				levelTagList.add(lt);
			}
		}


		for (LevelProperty<String> lp : obj.getSRlist())
		{
			levelTagList.add(new LevelTag(lp.getLevel(), LevelTag.TAG_SR, lp.getObject()));
		}

		/*
		   aCol = obj.vFeatList();
		   if (aCol != null)
		   {
			   for (Iterator se = aCol.iterator(); se.hasNext();)
			   {
				   String c = (String) se.next();
				   int y = c.indexOf(':');
				   LevelTag lt = new LevelTag(c.substring(0, y), LevelTag.TAG_VFEAT, c.substring(y + 1));
				   levelTagList.add(lt);
			   }
		   }
		 */

		for (Iterator it = obj.getSafeListFor(ListKey.KITS).iterator(); it.hasNext();)
		{
			String s = (String) it.next();
			int y = s.indexOf('|');
			String l = "1";

			if (y > 0)
			{
				l = s.substring(0, y);
				s = s.substring(y + 1);
			}

			LevelTag lt = new LevelTag(l, LevelTag.TAG_KIT, s);
			levelTagList.add(lt);
		}

		String s = obj.getRegionString();

		if ((s != null) && !s.equals(""))
		{
			int y = s.indexOf('|');
			String l = "1";

			if (y > 0)
			{
				l = s.substring(0, y);
				s = s.substring(y + 1);
			}

			LevelTag lt = new LevelTag(l, LevelTag.TAG_REGION, s);
			levelTagList.add(lt);
		}

		for ( final LevelProperty<String> c : obj.getTemplates() )
		{
			LevelTag lt = new LevelTag(c.getLevel(), LevelTag.TAG_TEMPLATE, c.getObject());
			levelTagList.add(lt);
		}

		aList = obj.getListFor(ListKey.UMULT);

		if ((aList != null) && (aList.size() != 0))
		{
			for (Iterator se = aList.iterator(); se.hasNext();)
			{
				String c = (String) se.next();
				int y = c.indexOf('|');
				LevelTag lt = new LevelTag(c.substring(0, y), LevelTag.TAG_UMULT, c.substring(y + 1));
				levelTagList.add(lt);
			}
		}

		aList = obj.getListFor(ListKey.UDAM);

		if (aList != null)
		{
			for (int x = 0; x < aList.size(); ++x)
			{
				String c = (String) aList.get(x);

				if (!c.equals(""))
				{
					LevelTag lt = new LevelTag(x + 1, LevelTag.TAG_UDAM, c);
					levelTagList.add(lt);
				}
			}
		}

		levelModel.updateModel();
	}

	/*
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		JLabel tempLabel;

		for (int i = 0; i < LevelTag.validTags.length; ++i)
		{
			tagList.addItem(LevelTag.validTags[i]);
		}

/*        tagList.addItem("ADD");
   tagList.addItem("ADDOMAINS");
   tagList.addItem("BONUS");
   tagList.addItem("DEFINE");
   tagList.addItem("CAST");
   tagList.addItem("KNOWN");
   tagList.addItem("SPELL");
   tagList.addItem("DR");
   tagList.addItem("SA");
   tagList.addItem("SR");
   tagList.addItem("FEAT");
   tagList.addItem("FEATAUTO");
   tagList.addItem("VFEAT");
   tagList.addItem("KIT");
   tagList.addItem("REGION");
   tagList.addItem("TEMPLATE");
   tagList.addItem("UMULT");
   tagList.addItem("UDAM");
 */
		/*
		 * CONSIDER TODO Need to add SpecialtyKnown to the above list...
		 * and add the functionality to this class - thpr 10/31/06
		 */
		tagList.setSelectedIndex(0);
		level.setText("1     ");

		setLayout(new BorderLayout());

		levelPane = new JScrollPane(levelTable);

		sortedLevelModel.setModel(levelModel);
		levelTable.setModel(sortedLevelModel);

		levelTable.setColAlign(0, SwingConstants.CENTER);

		levelPane.setViewportView(levelTable);
		levelTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sortedLevelModel.addMouseListenerToHeaderInTable(levelTable);
		levelTable.getColumnModel().getColumn(0).setPreferredWidth(5);
		levelTable.getColumnModel().getColumn(1).setPreferredWidth(10);

		add(levelPane, BorderLayout.CENTER);

		JPanel sth = new JPanel();
		sth.setLayout(new FlowLayout());

		tempLabel = new JLabel("Level:");
		sth.add(tempLabel);

		sth.add(level);

		tempLabel = new JLabel("Tag:");
		sth.add(tempLabel);

		sth.add(tagList);

		addBtn.setText(PropertyFactory.getString("in_add"));
		sth.add(addBtn);
		addBtn.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					String tag = tagList.getSelectedItem().toString();

					if (tag.equals("CAST") || tag.equals("KNOWN"))
					{
						String[] cols =
						{
							"Level 0", "Level 1", "Level 2", "Level 3", "Level 4", "Level 5", "Level 6", "Level 7",
							"Level 8", "Level 9"
						};
						String[] values = { "", "", "", "", "", "", "", "", "", "" };
						MatrixFrame mf = new MatrixFrame(cols, 10, values, tag);
						String v = "";

						for (int col = 0; col < 10; ++col)
						{
							if (col > 0)
							{
								v += ",";
							}

							v += mf.fields[col];
						}

						int x = 0;

						while (v.endsWith(",") && (x++ < 9))
						{
							v = v.substring(0, v.length() - 1);
						}

						if (!v.equals(""))
						{
							LevelTag lt = new LevelTag(level.getText().trim(), tag, v.trim(), true);
							levelTagList.add(lt);
							levelModel.updateModel();
						}
					}
					else if (tag.equals("FEAT") || tag.equals("VFEAT") || tag.equals("FEATAUTO"))
					{
						ListFrame lf = new ListFrame(
								"Choices for " + tag,
								Globals.getUnmodifiableAbilityList("FEAT"));
						String v = lf.getSelectedList();

						if (!v.equals(""))
						{
							LevelTag lt = new LevelTag(level.getText().trim(), tag, v.trim(), true);
							levelTagList.add(lt);
							levelModel.updateModel();
						}
					}
					else
					{
						InputInterface ii = InputFactory.getInputInstance();
						Object selectedValue = ii.showInputDialog(null, "Enter the value for " + tag,
								Constants.s_APPNAME, MessageType.INFORMATION, null, "");

						if (selectedValue == null)
						{
							return;
						}

						LevelTag lt = new LevelTag(level.getText().trim(), tag, selectedValue.toString().trim(), true);
						levelTagList.add(lt);
						levelModel.updateModel();
					}
				}
			});

		delBtn.setText(PropertyFactory.getString("in_remove"));
		sth.add(delBtn);
		delBtn.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					removeLevelTag();
				}
			});

		add(sth, BorderLayout.SOUTH);
	}

	private void removeLevelTag()
	{
		int x = levelTable.getSelectedRow();

		if ((x >= 0) && (x < levelTagList.size()))
		{
			x = sortedLevelModel.getRowTranslated(x);

			LevelTag lt = levelTagList.get(x);

			if (!lt.needsSaving())
			{ // this is a pre-existing one that needs to be removed

				if (obj == null)
				{
					return;
				}

				boolean bRemoved = false;

				switch (lt.getTagVal())
				{
					case LevelTag.TAG_ADD:
						obj.removeLevelAbility(lt.getLevel(), lt.getValue());
						bRemoved = true;

						break;

					case LevelTag.TAG_ADDDOMAINS:
					case LevelTag.TAG_BONUS:
					case LevelTag.TAG_CAST:
					case LevelTag.TAG_DEFINE:
					case LevelTag.TAG_DR:
					case LevelTag.TAG_FEAT:
					case LevelTag.TAG_KIT:
					case LevelTag.TAG_KNOWN:
					case LevelTag.TAG_REGION:
					case LevelTag.TAG_SAB:
					case LevelTag.TAG_SPELLS:
					case LevelTag.TAG_SR:
					case LevelTag.TAG_TEMPLATE:
					case LevelTag.TAG_UDAM:
					case LevelTag.TAG_UMULT:
					case LevelTag.TAG_VFEAT:default:
						break;
				}

				// TODO: based upon the lt.getTag() value (e.g. "ADD") remove it from the
				// appropriate list in the class
				// for now the easy solution is to not do anything except give a warning
				if (!bRemoved)
				{
					Logging.errorPrint("This tag " + lt.getTag() + ":" + lt.getValue()
						+ " needs to be hand-deleted from customClasses.lst");
					ShowMessageDelegate.showMessageDialog("This tag " + lt.getTag() + ":" + lt.getValue()
					+ " needs to be hand-deleted from customClasses.lst",
						Constants.s_APPNAME, MessageType.ERROR);

					return;
				}
			}

			// if this isn't a pre-existing level tag, then go ahead and remove it (new ones can be removed without worry)
			levelTagList.remove(x);
			levelModel.updateModel();
		}
	}

	static final class ListFrame extends JDialog
	{
		AvailableSelectedPanel asPanel = new AvailableSelectedPanel();

		/**
		 * Constructor
		 * @param title
		 * @param aList
		 */
		public ListFrame(String title, List aList)
		{
			super(Globals.getRootFrame(), title, true);
			initComponents();
			asPanel.setAvailableList(aList, true);
			setSize(new Dimension(400, 400));
			setVisible(true);
			pack();
		}

		/**
		 * Get the selected list
		 * @return the selected list
		 */
		public String getSelectedList()
		{
			Object[] sels = asPanel.getSelectedList();

			if (sels.length == 0)
			{
				return "";
			}

			String ret = sels[0].toString();

			for (int i = 1; i < sels.length; ++i)
			{
				ret += ("|" + sels[i].toString());
			}

			return ret;
		}

		private void initComponents()
		{
			getContentPane().setLayout(new BorderLayout());
			getContentPane().add(asPanel, BorderLayout.CENTER);

			JButton btn = new JButton(PropertyFactory.getString("in_ok"));
			getContentPane().add(btn, BorderLayout.SOUTH);
			btn.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent evt)
					{
						setVisible(false);
					}
				});
		}
	}

	/**
	 * <code>MatrixFrame</code> is a dialog for the gathering of
	 * a number of fields at once form a user. It is used for
	 * things like spells known or castable information. It
	 * allows the caller to define a number of labelled text
	 * fields which the user may enter a value into. None of the
	 * fields are mandatory however.
	 */
	static final class MatrixFrame extends JDialog
	{
		/** The values entered by the user into the fields. */
		public String[] fields;

		/** The names of each field. */
		private String[] colNames;
		/** The text fields being displayed on the dialog. */
		private JTextField[] textField;
		/** The initial values of the fields. */
		private String[] values;
		/** The number of fields to be displayed. */
		private int columns;

		/**
		 * Construct a new MatrixFrame instance, display the dialog and
		 * block processing until the user closes the dialog.
		 *
		 * @param colNs The names of each field.
		 * @param colNum The number of fields to be displayed.
		 * @param vals The initial values of the fields.
		 * @param title The title of the dialog.
		 */
		public MatrixFrame(String[] colNs, int colNum, String[] vals, String title)
		{
			super(Globals.getRootFrame(), title, true);
			colNames = colNs;
			columns = colNum;
			values = vals;
			initComponents();
			setSize(new Dimension(60 * columns, 140));
			setVisible(true);
			pack();
		}

		/**
		 * Update and return the supplied GridBagConstraints object.
		 *
		 * @param gridBagConstraints The GridBagConstraints object to be udpated.
		 * @param gridx The new horizontal coordinate.
		 * @param gridy The new vertical coordinate.
		 * @param useInsets Should a predefined inset be added to the contraints.
		 * @return The updated GridBagConstraints object.
		 */
		private GridBagConstraints buildConstraints(GridBagConstraints gridBagConstraints, int gridx, int gridy,
			boolean useInsets)
		{
			gridBagConstraints.gridx = gridx;
			gridBagConstraints.gridy = gridy;

			if (useInsets)
			{
				gridBagConstraints.insets = new Insets(2, 5, 2, 5);
			}

			return gridBagConstraints;
		}

		/**
		 * Initialise the display of the dialog.
		 */
		private void initComponents()
		{
			getContentPane().setLayout(new GridBagLayout());

			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			textField = new JTextField[columns];

			for (int col = 0; col < columns; ++col)
			{
				JLabel tempLabel = new JLabel(colNames[col]);
				gridBagConstraints = buildConstraints(gridBagConstraints, col, 0, true);
				getContentPane().add(tempLabel, gridBagConstraints);

				textField[col] = new JTextField(values[col]);
				gridBagConstraints = buildConstraints(gridBagConstraints, col, 1, true);
				getContentPane().add(textField[col], gridBagConstraints);
			}

			JButton btn = new JButton(PropertyFactory.getString("in_ok"));
			gridBagConstraints = buildConstraints(gridBagConstraints, columns - 2, 2, true);
			getContentPane().add(btn, gridBagConstraints);
			btn.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent evt)
					{
						setVisible(false);
						fields = new String[columns];

						for (int col = 0; col < columns; ++col)
						{
							fields[col] = textField[col].getText();
						}
					}
				});

			btn = new JButton(PropertyFactory.getString("in_cancel"));
			gridBagConstraints = buildConstraints(gridBagConstraints, columns - 1, 2, true);
			getContentPane().add(btn, gridBagConstraints);
			btn.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent evt)
					{
						setVisible(false);
						fields = new String[columns];

						for (int col = 0; col < columns; ++col)
						{
							fields[col] = "";
						}
					}
				});
		}
	}

	/**
	 *
	 * A TableModel to handle the list of tags by level.
	 *
	 **/
	private static final class LevelModel extends AbstractTableModel
	{
		static final long serialVersionUID = 1485178774957708877L;
		private final String[] colNames = { "Level", "Tag", "Value" };

		private LevelModel()
		{
			// Empty Constructor
		}

		/**
		 * @param columnIndex the index of the column to retrieve
		 * @return the type of the specified column
		 */
		public Class getColumnClass(final int columnIndex)
		{
			if (columnIndex == 0)
			{
				return Integer.class;
			}

			return String.class;
		}

		/**
		 * @return the number of columns
		 */
		public int getColumnCount()
		{
			return colNames.length;
		}

		/**
		 * @param columnIndex the index of the column name to retrieve
		 * @return the name.. of the specified column
		 */
		public String getColumnName(final int columnIndex)
		{
			return ((columnIndex >= 0) && (columnIndex < colNames.length)) ? colNames[columnIndex] : "Out Of Bounds";
		}

		/**
		 * @return the number of rows in the model
		 */
		public int getRowCount()
		{
			return ClassLevelPanel.levelTagList.size();
		}

		/**
		 * @param rowIndex the row of the cell to retrieve
		 * @param columnIndex the column of the cell to retrieve
		 * @return the value of the cell
		 */
		public Object getValueAt(final int rowIndex, final int columnIndex)
		{
			if ((rowIndex >= 0) && (rowIndex < ClassLevelPanel.levelTagList.size()))
			{
				LevelTag lt = ClassLevelPanel.levelTagList.get(rowIndex);

				switch (columnIndex)
				{
					case 0:
						return Integer.valueOf(lt.getLevel());

					case 1:
						return lt.getTag();

					case 2:
						return lt.getValue();

					default:
						Logging.errorPrint("In ClassLevelPanel.LevelModel.getValueAt the column " + columnIndex
							+ " is not supported.");

						break;
				}
			}

			return null;
		}

		private void updateModel()
		{
			fireTableDataChanged();
		}
	}

	private static final class LevelTag
	{
		static final String[] validTags =
		{
			"ADD", "ADDDOMAINS", "BONUS", "CAST", "DEFINE", "DR", "FEAT", "AUTO:FEAT", "KIT", "KNOWN", "REGION", 
			"SAB", "SPELLS", "SR", "TEMPLATE", "UDAM", "UMULT", "VFEAT"
		};
		private static final int TAG_ADD = 0;
		private static final int TAG_ADDDOMAINS = 1;
		private static final int TAG_BONUS = 2;
		private static final int TAG_CAST = 3;
		private static final int TAG_DEFINE = 4;
		private static final int TAG_DR = 5;
		private static final int TAG_FEAT = 6;
		private static final int TAG_KIT = 8;
		private static final int TAG_KNOWN = 9;
		private static final int TAG_REGION = 10;
		private static final int TAG_SAB = 11;
		private static final int TAG_SPELLS = 12;
		private static final int TAG_SR = 13;
		private static final int TAG_TEMPLATE = 14;
		private static final int TAG_UDAM = 15;
		private static final int TAG_UMULT = 16;
		private static final int TAG_VFEAT = 17;
		private String value;
		private boolean needsSaving;
		private int level;
		private int tagVal;

		/**
		 * Constructor
		 * @param l
		 * @param t
		 * @param v
		 * @param b
		 */
		public LevelTag(String l, String t, String v, boolean b)
		{
			int i = 0;

			if ((l != null) && (l.length() > 0))
			{
				i = Integer.parseInt(l);
			}

			setData(i, parseTag(t), v, b);
		}

		LevelTag(final String l, final int ttag, final String val)
		{
			int i = 0;

			if ((l != null) && (l.length() > 0))
			{
				i = Integer.parseInt(l);
			}

			setData(i, ttag, val, true);
		}

		LevelTag(final int l, final int ttag, final String val)
		{
			setData(l, ttag, val, true);
		}

		LevelTag(final int llevel, final String ttag, final String val, boolean saveIt)
		{
			setData(llevel, parseTag(ttag), val, saveIt);
		}

		/**
		 * Get the level
		 * @return level
		 */
		public int getLevel()
		{
			return level;
		}

		/**
		 * Get the tag
		 * @return tag
		 */
		public String getTag()
		{
			if (tagVal >= 0)
			{
				return validTags[tagVal];
			}

			return "Unknown";
		}

		/**
		 * Get the tag value
		 * @return the tag value
		 */
		public int getTagVal()
		{
			return tagVal;
		}

		/**
		 * Get the value
		 * @return the value
		 */
		public String getValue()
		{
			return value;
		}

		/**
		 * Needs saving
		 * @return TRUE if it needs saving
		 */
		public boolean needsSaving()
		{
			return needsSaving;
		}

		void setData(final int llevel, int ttagval, final String val, boolean saveIt)
		{
			if (ttagval >= validTags.length)
			{
				ttagval = -1;
			}

			level = llevel;
			tagVal = ttagval;
			value = val;
			needsSaving = saveIt;
		}

		private int parseTag(final String ttag)
		{
			for (int i = 0; i < validTags.length; ++i)
			{
				if (validTags[i].equalsIgnoreCase(ttag))
				{
					return i;
				}
			}

			return -1;
		}
	}
}
