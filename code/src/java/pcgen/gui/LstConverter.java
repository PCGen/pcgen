/*
 * LstConverter.java
 * Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on June 14, 2001, 2:15 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.gui;

import pcgen.core.PObject;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.utils.JComboBoxEditor;
import pcgen.gui.utils.JComboBoxRenderer;
import pcgen.gui.utils.TableSorter;
import pcgen.util.Logging;
import pcgen.io.PCGFile;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Main screen of the application. Some of the custom JPanels created
 * here also help intialise, for example
 * {@link pcgen.gui.MainSource} also loads any
 * default campaigns.
 *
 * @author     Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version    $Revision$
 */
interface LstConverterConstants
{
	/** CONVERT_CLASSTYPE = 2 */
	int CONVERT_CLASSTYPE = 2;
	/** CONVERT_DEITYTYPE = 4 */
	int CONVERT_DEITYTYPE = 4;
	/** CONVERT_DOMAINTYPE = 5 */
	int CONVERT_DOMAINTYPE = 5;
	/** CONVERT_FEATTYPE = 7 */
	int CONVERT_FEATTYPE = 7;
	/** CONVERT_RACETYPE = 1 */
	int CONVERT_RACETYPE = 1;
	/** CONVERT_SKILLTYPE = 6 */
	int CONVERT_SKILLTYPE = 6;
	/** CONVERT_SPELLTYPE = 3 */
	int CONVERT_SPELLTYPE = 3;
	/** CONVERT_TEMPLATETYPE = 8 */
	int CONVERT_TEMPLATETYPE = 8;
}


final class LstConverter extends JFrame
{
	static final long serialVersionUID = 4822388828239441708L;
	private static final String[] typeTypes =
	{
		"UNKNOWN", "RACE", "CLASS", "SPELL", "DEITY", "DOMAIN", "SKILL", "FEAT", "TEMPLATE"
	};
	private static final int LST_TYPE_UNKNOWN = 0;
	private static final int LST_TYPE_RACE = 1;
	private static final int LST_TYPE_CLASS = 2;
	private static final int LST_TYPE_SPELL = 3;
	private static final int LST_TYPE_DEITY = 4;
	private static final int LST_TYPE_DOMAIN = 5;
	private static final int LST_TYPE_SKILL = 6;
	private static final int LST_TYPE_FEAT = 7;
	private static final int LST_TYPE_TEMPLATE = 8;

	private static final String[] okTypes = { "NO", "YES" };
	private List<Integer> doneList = new ArrayList<Integer>(); // list of items already run
	private List<String> lstNameList = new ArrayList<String>(); // list of file names
	private List<String> lstPathList = new ArrayList<String>(); // list of paths
	private List<Integer> lstTypeList = new ArrayList<Integer>(); // list of types
	private List<Integer> okList = new ArrayList<Integer>(); // list of items to be run
	private String basePath = "";
	private TableSorter sorter = new TableSorter();

	/**
	 * Screen initialization. Override close.
	 * <p>
	 * Calls private <code>jbInit()</code> which does real screen
	 * initialization: Sets up all the window properties (icon,
	 * title, size);
	 * @param argBasePath
	 */
	LstConverter(String argBasePath)
	{
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setTitle("Convert Required fields to tagged format");
		basePath = argBasePath;

		try
		{
			loadLSTFilesInDirectory(basePath);
			jbInit();
		}
		catch (Exception e) //This is what jbInit throws...
		{
			Logging.errorPrint("Error while initing form", e);
		}
	}

	/**
	 * Get the base path
	 * @return the base path
	 */
	public String getBasePath()
	{
		return basePath;
	}

	/**
	 * Overridden so we can handle exit on System Close
	 * by calling <code>handleQuit</code>.
	 * @param e
	 */
	protected void processWindowEvent(WindowEvent e)
	{
		super.processWindowEvent(e);

		if (e.getID() == WindowEvent.WINDOW_CLOSING)
		{
			handleQuit();
		}
	}

	// type==0 is BAB
	// type==1 is Check
	private static String getFormulaFor(int type, String formulaString)
	{
		String formula = formulaString;

		if (type == 0)
		{
			if ("G".equals(formulaString))
			{
				formula = "CL";
			}

			if ("M".equals(formulaString))
			{
				formula = "3*CL/4";
			}

			if ("B".equals(formulaString))
			{
				formula = "CL/2";
			}
		}
		else
		{
			if ("G".equals(formulaString))
			{
				formula = "(CL/2)+2";
			}

			if ("M".equals(formulaString))
			{
				formula = "1+((CL/5).INTVAL)+(((CL+3)/5).INTVAL)";
			}

			if ("B".equals(formulaString))
			{
				formula = "CL/3";
			}
		}

		if (formula.equals(formulaString))
		{
			Logging.errorPrint("bad formula String:" + formulaString);
		}

		return formula;
	}

	private void go()
	{
		for (int i = 0; i < okList.size(); i++)
		{
			if ( okList.get(i) == 0 )
			{
				continue;
			}

			int thisType = lstTypeList.get(i);

			if (thisType == LST_TYPE_UNKNOWN)
			{
				Logging.errorPrint(lstNameList.get(i) + " is UNKNOWN - not converting");
			}

			File conversionSource = new File(lstPathList.get(i) + File.separatorChar
					+ lstNameList.get(i));

			try
			{
				//BufferedReader conversionReader = new BufferedReader(new FileReader(conversionSource));
				BufferedReader conversionReader = new BufferedReader(new InputStreamReader(
							new FileInputStream(conversionSource), "UTF-8"));
				int length = (int) conversionSource.length();
				char[] sourceInput = new char[length];
				conversionReader.read(sourceInput, 0, length);
				conversionReader.close();

				//BufferedWriter conversionWriter = new BufferedWriter(new FileWriter(conversionSource));
				BufferedWriter conversionWriter = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(conversionSource), "UTF-8"));
				String sourceInputString = new String(sourceInput);
				StringTokenizer sourceTokenizer = new StringTokenizer(sourceInputString, "\r\n", true);

				while (sourceTokenizer.hasMoreTokens())
				{
					String line = sourceTokenizer.nextToken();

					if ("\r".equals(line) || "\n".equals(line) || (line.trim().length() == 0)
						|| ((line.length() > 0) && (line.charAt(0) == '#')))
					{
						conversionWriter.write(line);

						continue;
					}

					StringTokenizer lineTokenizer = new StringTokenizer(line, "\t", false);
					boolean hasTagless = false;
					lineTokenizer.nextToken();

					if (lineTokenizer.hasMoreTokens())
					{
						String bString = lineTokenizer.nextToken();

						if (bString.indexOf(":") == -1)
						{
							hasTagless = true;
						}
					}

					lineTokenizer = new StringTokenizer(line, "\t", true); // reset tokenizer

					int field = 0;

					if (!hasTagless)
					{
						field = 100;
					}

					while (lineTokenizer.hasMoreTokens())
					{
						String bString = lineTokenizer.nextToken();

						if ("\t".equals(bString))
						{
							conversionWriter.write(bString);

							continue;
						}

						if (field++ == 0)
						{
							conversionWriter.write(bString);

							continue;
						}

						List<PObject> checkList = SettingsHandler.getGame().getUnmodifiableCheckList();
						if (bString.startsWith("PREFORT:"))
						{
							conversionWriter.write("PRECHECK:1,"
								+ checkList.get(0).toString().toUpperCase() + "="
								+ bString.substring(8));

							continue;
						}
						else if (bString.startsWith("PREREFLEX:"))
						{
							conversionWriter.write("PRECHECK:1,"
								+ checkList.get(1).toString().toUpperCase() + "="
								+ bString.substring(10));

							continue;
						}
						else if (bString.startsWith("PREWILL:"))
						{
							conversionWriter.write("PRECHECK:1,"
								+ checkList.get(2).toString().toUpperCase() + "="
								+ bString.substring(8));

							continue;
						}
						else if (bString.startsWith("PREFORTBASE:"))
						{
							conversionWriter.write("PRECHECKBASE:1,"
								+ checkList.get(0).toString().toUpperCase() + "="
								+ bString.substring(12));

							continue;
						}
						else if (bString.startsWith("PREREFLEXBASE:"))
						{
							conversionWriter.write("PRECHECKBASE:1,"
								+ checkList.get(1).toString().toUpperCase() + "="
								+ bString.substring(14));

							continue;
						}
						else if (bString.startsWith("PREWILLBASE:"))
						{
							conversionWriter.write("PRECHECKBASE:1,"
								+ checkList.get(2).toString().toUpperCase() + "="
								+ bString.substring(12));

							continue;
						}
						else if (bString.startsWith("PRESTAT:") && (bString.indexOf(",") == -1))
						{
							conversionWriter.write("PRESTAT:1," + bString.substring(8));

							continue;
						}
						else if (bString.startsWith("PRECLASS:"))
						{
							StringTokenizer p = new StringTokenizer(bString.substring(9), ",", false);
							String q = p.nextToken();

							try
							{
								Integer.parseInt(q);
							}
							catch (Exception exc)
							{
								conversionWriter.write("PRECLASS:1," + bString.substring(9));

								continue;
							}
						}
						else if (bString.startsWith("!PRECLASS:"))
						{
							StringTokenizer p = new StringTokenizer(bString.substring(10), ",", false);
							String q = p.nextToken();

							try
							{
								Integer.parseInt(q);
							}
							catch (Exception exc)
							{
								conversionWriter.write("!PRECLASS:1," + bString.substring(10));

								continue;
							}
						}
						else if (bString.startsWith("PREWEAPONPROF:"))
						{
							StringTokenizer p = new StringTokenizer(bString.substring(14), ",", false);
							String q = p.nextToken();

							try
							{
								Integer.parseInt(q);
							}
							catch (Exception exc)
							{
								conversionWriter.write("PREWEAPONPROF:" + Integer.toString(p.countTokens() + 1) + ","
									+ bString.substring(14));

								continue;
							}
						}

						switch (thisType)
						{
							case LstConverterConstants.CONVERT_RACETYPE: // race

								if ((!hasTagless && bString.startsWith("STATADJ")) || ((field > 1) && (field < 8)))
								{
									int statNum = field - 2;

									if (!hasTagless)
									{
										statNum = Integer.parseInt(bString.substring(7, 8));
										bString = bString.substring(9);
									}

									if (!"0".equals(bString))
									{
										bString = "BONUS:STAT|"
											+ (SettingsHandler.getGame().getUnmodifiableStatList().get(statNum))
											.getAbb() + "|" + bString;
										conversionWriter.write(bString);
									}
								}
								else if (field == 8)
								{
									bString = "FAVCLASS:" + bString;
									conversionWriter.write(bString);
								}
								else if (field == 9)
								{
									if (!"0".equals(bString))
									{
										bString = "XTRASKILLPTSPERLVL:" + bString;
										conversionWriter.write(bString);
									}
								}
								else if (field == 10)
								{
									if (!"0".equals(bString))
									{
										bString = "STARTFEATS:" + bString;
										conversionWriter.write(bString);
									}
								}
								else
								{
									conversionWriter.write(bString);
								}

								break;

							case LstConverterConstants.CONVERT_CLASSTYPE: // class

								if (bString.startsWith("INTMODTOSKILLS"))
								{
									bString = bString.substring(3); // remove INT prefix
								}
								else if (bString.startsWith("GOLD:") || bString.startsWith("AGESET:"))
								{
									bString = ""; // tag was removed for license compliance
								}
								else if (field == 2)
								{
									continue; // alignment string is ignored
								}
								else if (field == 3)
								{
									bString = "HD:" + bString;
								}
								else if (field == 4)
								{
									bString = "STARTSKILLPTS:" + bString;
								}
								else if (field == 5)
								{
									bString = "XTRAFEATS:" + bString;
								}
								else if (field == 6)
								{
									bString = "SPELLSTAT:" + bString;
								}
								else if (field == 7)
								{
									bString = "SPELLTYPE:" + bString;
								}
								else if ((field == 8) || bString.startsWith("BAB:"))
								{
									if (bString.startsWith("BAB:"))
									{
										bString = bString.substring(4);
									}

									bString = getFormulaFor(0, bString);
									bString = "BONUS:COMBAT|BAB|" + bString;

//									BAB: was removed in v3.1.0
								}
								else if ((field == 9) || (!hasTagless && bString.startsWith("FORTITUDECHECK:"))
									|| (!hasTagless && bString.startsWith("CHECK1:")))
								{
//									FORTITUDECHECK has been replaced in v3.1.0
									if (bString.startsWith("FORT"))
									{
										bString = bString.substring(15);
									}

									if (bString.startsWith("CHECK1"))
									{
										bString = bString.substring(7);
									}

									bString = getFormulaFor(1, bString);
									bString = "BONUS:CHECKS|BASE."
										+ checkList.get(0).toString().toUpperCase()
										+ "|" + bString;
								}
								else if ((field == 10) || (!hasTagless && bString.startsWith("REFLEXCHECK:"))
									|| (!hasTagless && bString.startsWith("CHECK2:")))
								{
//									REFLEXCHECK has been replaced in v3.1.0
									if (!hasTagless)
									{
										bString = bString.substring(12);
									}

									if (bString.startsWith("CHECK2"))
									{
										bString = bString.substring(7);
									}

									bString = getFormulaFor(1, bString);
									bString = "BONUS:CHECKS|BASE."
										+ checkList.get(1).toString().toUpperCase()
										+ "|" + bString;
								}
								else if ((field == 11) || (!hasTagless && bString.startsWith("WILLPOWERCHECK:"))
									|| (!hasTagless && bString.startsWith("CHECK3:")))
								{
									//WILLPOWERCHECK has been replaced in v3.1.0
									if (!hasTagless)
									{
										bString = bString.substring(15);
									}

									if (bString.startsWith("CHECK3"))
									{
										bString = bString.substring(7);
									}

									bString = getFormulaFor(1, bString);
									bString = "BONUS:CHECKS|BASE."
										+ checkList.get(2).toString().toUpperCase()
										+ "|" + bString;
								}

								conversionWriter.write(bString);

								break;

							case LstConverterConstants.CONVERT_SPELLTYPE: // spell

								if (bString.startsWith("EFFECTS:"))
								{
									bString = "DESC:" + bString.substring(8);
								}
								else if (bString.startsWith("EFFECTTYPE:"))
								{
									bString = "TARGETAREA:" + bString.substring(11);
								}
								else if (field == 2)
								{
									bString = "SCHOOL:" + bString;
								}
								else if (field == 3)
								{
									bString = "SUBSCHOOL:" + bString;
								}
								else if (field == 4)
								{
									bString = "COMPS:" + bString;
								}
								else if (field == 5)
								{
									bString = "CASTTIME:" + bString;
								}
								else if (field == 6)
								{
									bString = "RANGE:" + bString;
								}
								else if (field == 7)
								{
									bString = "DESC:" + bString;
								}
								else if (field == 8)
								{
									bString = "TARGETAREA:" + bString;
								}
								else if (field == 9)
								{
									bString = "DURATION:" + bString;
								}
								else if (field == 10)
								{
									bString = "SAVEINFO:" + bString;
								}
								else if (field == 11)
								{
									bString = "SPELLRES:" + bString;
								}

								conversionWriter.write(bString);

								break;

							case LstConverterConstants.CONVERT_DEITYTYPE: // deity

								if (field == 2)
								{
									bString = "DOMAINS:" + bString;
								}
								else if (field == 3)
								{
									bString = "FOLLOWERALIGN:" + bString;
								}
								else if (field == 4)
								{
									bString = "DESC:" + bString;
								}
								else if (field == 5)
								{
									bString = "SYMBOL:" + bString;
								}
								else if (field == 6)
								{
									bString = "DEITYWEAP:" + bString;
								}

								conversionWriter.write(bString);

								break;

							case LstConverterConstants.CONVERT_DOMAINTYPE: // domain

								if (field == 2)
								{
									bString = "DESC:" + bString;
								}

								conversionWriter.write(bString);

								break;

							case LstConverterConstants.CONVERT_SKILLTYPE: // skill

								if (field == 2)
								{
									bString = "KEYSTAT:" + bString;
								}
								else if (field == 3)
								{
									bString = "EXCLUSIVE:" + bString;
								}
								else if (field == 4)
								{
									bString = "USEUNTRAINED:" + bString;
								}
								else if (bString.startsWith("SYNERGY:"))
								{
									String skillX = line.substring(0, line.indexOf("\t")); //we need to get the name of the current skill
									String skillY = bString.substring(8, bString.indexOf("="));
									String ranksY = bString.substring(bString.indexOf("=") + 1, bString.lastIndexOf("="));
									String bonusToX = bString.substring(bString.lastIndexOf("=") + 1);

									bString = "BONUS:SKILL|" + skillX + "|" + bonusToX + "|PRESKILL:1," + skillY + "="
										+ ranksY + "|TYPE=Synergy.STACK";
								}

								conversionWriter.write(bString);

								break;

							case LstConverterConstants.CONVERT_FEATTYPE: // feat
							case LstConverterConstants.CONVERT_TEMPLATETYPE: // template
								conversionWriter.write(bString);

								break;

							default:
								Logging.errorPrint("In LstConverter.go the type " + thisType + " is not handled.");

								break;
						}
					}
					 // end while
				}

				okList.set(i, 0);
				doneList.set(i, 1);
				conversionWriter.close();
			}
			catch (Exception e)
			{
				Logging.errorPrint("", e);
			}
		}
	}

	/**
	 * Does the real work in closing the program.
	 * Closes each character tab, giving user a chance to save.
	 * Saves options to file, then cleans up and exits.
	 */
	private void handleQuit()
	{
		this.dispose();
	}

	/**
	 * Real screen initialization is done here. Sets up all
	 * the window properties (icon, title, size).
	 * <p>
	 *
	 * @exception  Exception  Any Exception
	 */
	private void jbInit() throws Exception
	{
		JScrollPane lstScrollPane = new JScrollPane();

		final JTable lstTable = new JTable();
		LstTableModel lstTableModel = new LstTableModel();
		sorter.setModel(lstTableModel);
		sorter.addMouseListenerToHeaderInTable(lstTable);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(lstScrollPane, BorderLayout.CENTER);
		setSize(new Dimension(700, 500));
		lstTable.setModel(sorter);
		lstTable.getColumnModel().getColumn(0).setPreferredWidth(150);
		lstTable.getColumnModel().getColumn(1).setPreferredWidth(160);
		lstTable.getColumnModel().getColumn(2).setCellRenderer(new TypeRenderer(typeTypes));
		lstTable.getColumnModel().getColumn(2).setCellEditor(new TypeEditor(typeTypes));
		lstTable.getColumnModel().getColumn(3).setCellRenderer(new OkRenderer(okTypes));
		lstTable.getColumnModel().getColumn(3).setCellEditor(new OkEditor(okTypes));
		lstTable.getColumnModel().getColumn(4).setCellRenderer(new DoneRenderer(okTypes));
		lstTable.getColumnModel().getColumn(4).setCellEditor(new DoneEditor(okTypes));

		lstTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		lstTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstScrollPane.setViewportView(lstTable);

		JPanel aPanel = new JPanel();
		getContentPane().add(aPanel, BorderLayout.SOUTH);

		final JButton rButton = new JButton("Race");
		final JButton cButton = new JButton("Class");
		final JButton sButton = new JButton("Spell");
		final JButton dButton = new JButton("Deity");
		final JButton oButton = new JButton("Domain");
		final JButton kButton = new JButton("Skill");
		final JButton fButton = new JButton("Feat");
		final JButton tButton = new JButton("Template");
		final JButton aButton = new JButton("All");
		final JButton goButton = new JButton("Run!");
		aPanel.add(goButton);
		aPanel.add(new JLabel("Toggle:"));
		aPanel.add(aButton);
		aPanel.add(rButton);
		aPanel.add(cButton);
		aPanel.add(sButton);
		aPanel.add(dButton);
		aPanel.add(oButton);
		aPanel.add(kButton);
		aPanel.add(fButton);
		aPanel.add(tButton);
		aButton.setToolTipText("Toggle convert-me status of all known file types");
		rButton.setToolTipText("Toggle convert-me status of all race files");
		cButton.setToolTipText("Toggle convert-me status of all class files");
		sButton.setToolTipText("Toggle convert-me status of all spell/power files");
		dButton.setToolTipText("Toggle convert-me status of all deity files");
		oButton.setToolTipText("Toggle convert-me status of all domain files");
		kButton.setToolTipText("Toggle convert-me status of all skill files");
		fButton.setToolTipText("Toggle convert-me status of all feat files");
		tButton.setToolTipText("Toggle convert-me status of all template files");
		goButton.setToolTipText("Convert files from using required fields to use tagged format");

		ActionListener eventListener = new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					if (evt.getSource() == goButton)
					{
						go();
						lstTable.updateUI();
					}

					if (evt.getSource() == rButton)
					{
						toggleType(1);
						lstTable.updateUI();
					}

					if (evt.getSource() == cButton)
					{
						toggleType(2);
						lstTable.updateUI();
					}

					if (evt.getSource() == sButton)
					{
						toggleType(3);
						lstTable.updateUI();
					}

					if (evt.getSource() == dButton)
					{
						toggleType(4);
						lstTable.updateUI();
					}

					if (evt.getSource() == oButton)
					{
						toggleType(5);
						lstTable.updateUI();
					}

					if (evt.getSource() == kButton)
					{
						toggleType(6);
						lstTable.updateUI();
					}

					if (evt.getSource() == fButton)
					{
						toggleType(7);
						lstTable.updateUI();
					}

					if (evt.getSource() == tButton)
					{
						toggleType(8);
						lstTable.updateUI();
					}

					if (evt.getSource() == aButton)
					{
						for (int i = 1; i < 9; i++)
						{
							toggleType(i);
						}

						//lstTable.resizeAndRepaint();
						lstTable.updateUI();
					}
				}
			};

		rButton.addActionListener(eventListener);
		cButton.addActionListener(eventListener);
		sButton.addActionListener(eventListener);
		dButton.addActionListener(eventListener);
		oButton.addActionListener(eventListener);
		kButton.addActionListener(eventListener);
		fButton.addActionListener(eventListener);
		tButton.addActionListener(eventListener);
		aButton.addActionListener(eventListener);
		goButton.addActionListener(eventListener);
	}

	private boolean loadLSTFilesInDirectory(String aDirectory)
	{
		new File(aDirectory).list(new FilenameFilter()
			{
				public boolean accept(File parentDir, String fileName)
				{
					try
					{
						fileName = fileName.toLowerCase();

						if (PCGFile.isPCGenListFile(new File(fileName)))
						{
							lstNameList.add(fileName);
							lstPathList.add(parentDir.getPath());

							int ok = 1;

							if (fileName.endsWith("race.lst") || fileName.endsWith("races.lst"))
							{
								lstTypeList.add(LST_TYPE_RACE);
							}
							else if (fileName.endsWith("class.lst") || fileName.endsWith("classes.lst"))
							{
								lstTypeList.add(LST_TYPE_CLASS);
							}
							else if ((fileName.endsWith("spell.lst") || fileName.endsWith("spells.lst")
								|| fileName.endsWith("power.lst") || fileName.endsWith("powers.lst"))
								&& (fileName.indexOf("classspell") == -1) && (fileName.indexOf("classpowers") == -1))
							{
								lstTypeList.add(LST_TYPE_SPELL);
							}
							else if (fileName.endsWith("deity.lst") || fileName.endsWith("deities.lst"))
							{
								lstTypeList.add(LST_TYPE_DEITY);
							}
							else if (fileName.endsWith("domain.lst") || fileName.endsWith("domains.lst"))
							{
								lstTypeList.add(LST_TYPE_DOMAIN);
							}
							else if ((fileName.endsWith("skill.lst") || fileName.endsWith("skills.lst"))
								&& (fileName.indexOf("classskill") == -1))
							{
								lstTypeList.add(LST_TYPE_SKILL);
							}
							else if (fileName.endsWith("feat.lst") || fileName.endsWith("feats.lst"))
							{
								lstTypeList.add(LST_TYPE_FEAT);
							}
							else if (fileName.endsWith("template.lst") || fileName.endsWith("templates.lst"))
							{
								lstTypeList.add(LST_TYPE_TEMPLATE);
							}
							else
							{
								ok = 0;
								lstTypeList.add(0); // unknown
							}

							if (ok > 0)
							{
								okList.add(1); // default to OK
							}
							else
							{
								okList.add(0);
							}

							doneList.add(0); // default to not-done
						}
						else if (parentDir.isDirectory())
						{
							loadLSTFilesInDirectory(parentDir.getPath() + File.separator + fileName);
						}
					}
					catch (Exception e)
					{
						// LATER: This is not an appropriate way to deal with this exception.
						// Deal with it this way because of the way the loading takes place.  XXX
						Logging.errorPrint("LstConverter", e);
					}

					return false;
				}
			});

		return false;
	}

	private void toggleType(int x)
	{
		for (int i = 0; i < okList.size(); i++)
		{
			int y = lstTypeList.get(i);

			if (x == y)
			{
				y = okList.get(i);

				if (y == 0)
				{
					okList.set(i, 1);
				}
				else
				{
					okList.set(i, 0);
				}
			}
		}
	}

	private static final class DoneEditor extends JComboBoxEditor
	{
		private DoneEditor(String[] choices)
		{
			super(choices);
			addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent ae)
					{
						stopCellEditing();
					}
				});
		}
	}

	private static final class DoneRenderer extends JComboBoxRenderer
	{
		private DoneRenderer(String[] choices)
		{
			super(choices);
		}
	}

	private final class LstTableModel extends AbstractTableModel
	{
		private LstTableModel()
		{
			// Empty Constructor
		}

		public boolean isCellEditable(int row, int column)
		{
			return ((column == 2) || (column == 3));
		}

		public Class<?> getColumnClass(int columnIndex)
		{
			return getValueAt(0, columnIndex).getClass();
		}

		public int getColumnCount()
		{
			return 5;
		}

		public String getColumnName(int columnIndex)
		{
			switch (columnIndex)
			{
				case 0:
					return "File Name";

				case 1:
					return "Path";

				case 2:
					return "Type";

				case 3:
					return "Convert Me";

				case 4:
					return "Converted";

				default:
					Logging.errorPrint("In LstConverter.LstTableModel.getColumnName the column " + columnIndex
						+ " is not handled.");

					break;
			}

			return "Out Of Bounds";
		}

		public int getRowCount()
		{
			return lstNameList.size();
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			if ((aValue == null) || (columnIndex < 1) || (columnIndex > 4))
			{
				return;
			}

			final Integer i = Integer.valueOf(aValue.toString());

			switch (columnIndex)
			{
				case 2:
					lstTypeList.set(rowIndex, i);

					// if type is set to UNKNOWN, we can't convert it
					if (i.intValue() == 0)
					{
						okList.set(rowIndex, i);
					}

					break;

				case 3:

					if (i.intValue() == 1)
					{
						int j = lstTypeList.get(rowIndex);

						if (j == 0)
						{
							ShowMessageDelegate.showMessageDialog("Set type to a known type before marking it to be converted.",
								"Oops!", MessageType.ERROR);

							return;
						}
					}

					okList.set(rowIndex, i);

					break;

				default:
					Logging.errorPrint("In LstConverter.LstTableModel.setValueAt the column " + columnIndex
						+ " is not handled.");

					break;
			}
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if ((lstNameList.size() <= rowIndex) || (columnIndex > 4))
			{
				return "Out of Bounds";
			}

			switch (columnIndex)
			{
				case 0:
					return lstNameList.get(rowIndex);

				case 1:
					return lstPathList.get(rowIndex);

				case 2:

					final int x = lstTypeList.get(rowIndex);

					return Integer.valueOf(x);

				case 3:

					int ok = okList.get(rowIndex);

					return Integer.valueOf(ok);

				case 4:
					ok = doneList.get(rowIndex);

					return Integer.valueOf(ok);

				default:
					Logging.errorPrint("In LstConverter.LstTableModel.getValueAt the column " + columnIndex
						+ " is not handled.");

					break;
			}

			return null;
		}

		public void fireTableDataChanged()
		{
			super.fireTableDataChanged();
		}
	}

	private static final class OkEditor extends JComboBoxEditor
	{
		private OkEditor(String[] choices)
		{
			super(choices);
			addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent ae)
					{
						stopCellEditing();
					}
				});
		}
	}

	private static final class OkRenderer extends JComboBoxRenderer
	{
		private OkRenderer(String[] choices)
		{
			super(choices);
		}
	}

	private static final class TypeEditor extends JComboBoxEditor
	{
		private TypeEditor(String[] choices)
		{
			super(choices);
			addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent ae)
					{
						stopCellEditing();
					}
				});
		}
	}

	private static final class TypeRenderer extends JComboBoxRenderer
	{
		private TypeRenderer(String[] choices)
		{
			super(choices);
		}
	}
}
