/*
 * NPCGeneratorDlg.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Current Ver: $Revision$
 * Last Editor: $Author: $
 * Last Edited: $Date$
 */
package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import pcgen.cdom.base.Constants;
import pcgen.cdom.content.RollMethod;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.core.GameMode;
import pcgen.core.Names;
import pcgen.core.SettingsHandler;
import pcgen.core.npcgen.AlignGeneratorOption;
import pcgen.core.npcgen.ClassGeneratorOption;
import pcgen.core.npcgen.GenderGeneratorOption;
import pcgen.core.npcgen.GeneratorOption;
import pcgen.core.npcgen.LevelGeneratorOption;
import pcgen.core.npcgen.NPCGenerator;
import pcgen.core.npcgen.RaceGeneratorOption;
import pcgen.util.PropertyFactory;

/**
 * This class implements a dialog to present the configurable options for 
 * generating a random NPC.
 * 
 * <p>It includes dropdowns for Alignment, Race, Gender, Classes and Levels, 
 * and rolling method.
 * 
 * <ul>
 * <li>TODO - Implement Edit buttons</li>
 * <li>TODO - Only display Alignment panel if Alignment is used by the Game Mode</li>
 * <li>TODO - Add interface to the random name generator (which one?) </li>
 * </ul>
 * 
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 *
 */
@SuppressWarnings("serial")
public class NPCGeneratorDlg extends JDialog
{

    private JButton okButton = new JButton();
    private JButton cancelButton = new JButton();
    private JComboBox alignCombo = new JComboBox();
    private JComboBox raceCombo = new JComboBox();
    private JButton editRace = new JButton();
    private JButton editAlign = new JButton();
    private JButton editGender = new JButton();
    private JComboBox genderCombo = new JComboBox();
    private JButton editStats = new JButton();
    private JComboBox statsCombo = new JComboBox();
    public static final int OK_BUTTON = 1;
    public static final int CANCEL_BUTTON = 0;
    private int retValue = CANCEL_BUTTON;
    private AlignGeneratorOption theAlignment = null;
    private RaceGeneratorOption theRace = null;
    private GenderGeneratorOption theGender = null;
    private List<ClassGeneratorOption> theClassList = new ArrayList<ClassGeneratorOption>();
    private List<LevelGeneratorOption> theLevelList = new ArrayList<LevelGeneratorOption>();
    private RollMethod theRollMethod = null;
    private static final int MAX_CLASSES = 3;
    private JComboBox[] classCombos = new JComboBox[MAX_CLASSES];
    private JComboBox[] lvlCombos = new JComboBox[MAX_CLASSES];
    private JComboBox nameCombo = new JComboBox();

    public NPCGeneratorDlg(final Frame owner, final String title, final boolean modal)
    {
	super(owner, title, modal);
	try
	{
	    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	    initComponents();
	    pack();
	    setLocationRelativeTo(owner);

	    SwingUtilities.invokeLater(new Runnable()
			       {

				   public void run()
				   {
				       populateControls();
				   }

			       });
	} catch (Exception exception)
	{
	    exception.printStackTrace();
	}
    }

    public NPCGeneratorDlg()
    {
	this(new Frame(), "NPC Generator", true);
    }

    public int getValue()
    {
	return retValue;
    }

    public AlignGeneratorOption getAlignment()
    {
	return theAlignment;
    }

    public RaceGeneratorOption getRace()
    {
	return theRace;
    }

    public GenderGeneratorOption getGender()
    {
	return theGender;
    }

    public List<ClassGeneratorOption> getClassList()
    {
	return theClassList;
    }

    public List<LevelGeneratorOption> getLevels()
    {
	return theLevelList;
    }

    public RollMethod getRollMethod()
    {
	return theRollMethod;
    }

    public NameElement getNameChoice()
    {
	return (NameElement) nameCombo.getSelectedItem();
    }

    private void okActionPerformed()
    {
	if (okButton.isEnabled())
	{
	    retValue = OK_BUTTON;
	    setVisible(false);

	    theAlignment = (AlignGeneratorOption) alignCombo.getSelectedItem();

	    theRace = (RaceGeneratorOption) raceCombo.getSelectedItem();

	    theGender = (GenderGeneratorOption) genderCombo.getSelectedItem();
	    for (int i = 0; i < MAX_CLASSES; i++)
	    {
		final Object selClass = classCombos[i].getSelectedItem();
		if (selClass instanceof String && selClass.equals(Constants.NONESELECTED))
		{
		    continue;
		}
		theClassList.add((ClassGeneratorOption) classCombos[i].getSelectedItem());
		theLevelList.add((LevelGeneratorOption) lvlCombos[i].getSelectedItem());
	    }

	    theRollMethod = (RollMethod) statsCombo.getSelectedItem();

	    dispose();
	}
    }

    private void cancelActionPerformed()
    {
	retValue = CANCEL_BUTTON;
	setVisible(false);
	dispose();
    }

    private void initComponents()
	    throws Exception
    {
	setModal(true);
	setResizable(true);
//		setAlwaysOnTop(true);

	addWindowListener(new WindowAdapter()
		  {

		      @Override
		      public void windowClosing(WindowEvent evt)
		      {
			  cancelActionPerformed();
		      }

		  });

	getContentPane().setLayout(new FlowLayout());

	// Create the main panel
	JPanel mainPanel = new JPanel();
	BorderLayout borderLayout1 = new BorderLayout();
	mainPanel.setLayout(borderLayout1);

	// Create the work panel
	JPanel workPanel = new JPanel();
	BoxLayout boxLayout1 = new BoxLayout(workPanel, BoxLayout.Y_AXIS);
	workPanel.setLayout(boxLayout1);

	// Create the Alignment Panel
	JPanel alignPanel = new JPanel();

	FlowLayout flowLayout3 = new FlowLayout();
	flowLayout3.setAlignment(FlowLayout.RIGHT);
	alignPanel.setLayout(flowLayout3);
//		alignPanel.setBounds(new Rectangle(0, 0, 400, 267));
	alignPanel.setMaximumSize(new Dimension(32767, 33));
//		alignPanel.setMinimumSize(new Dimension(32767, 25));
	alignPanel.setPreferredSize(new Dimension(390, 33));
	JLabel alignLbl = new JLabel(PropertyFactory.getString("in_alignString") + ": "); //$NON-NLS-1$ //$NON-NLS-2$
	alignCombo.setMaximumSize(new Dimension(150, 19));
	alignCombo.setPreferredSize(new Dimension(150, 19));
	editAlign.setText("Edit");
	Component alignStrut = Box.createHorizontalStrut(60);
	alignPanel.add(alignLbl);
	alignPanel.add(alignCombo);
//        alignPanel.add(editAlign); I have only prevented it from getting added
//									so the edit box is still floating in the code
//									in case someone finds a use for it later
	alignPanel.add(alignStrut);

	workPanel.add(alignPanel);

	// Create Race Panel
	JPanel racePanel = new JPanel();
	FlowLayout flowLayout2 = new FlowLayout();
	flowLayout2.setAlignment(FlowLayout.RIGHT);
	racePanel.setLayout(flowLayout2);
	racePanel.setMaximumSize(new Dimension(32767, 33));
	JLabel raceLbl = new JLabel(PropertyFactory.getString("in_raceString") + ": "); //$NON-NLS-1$ //$NON-NLS-2$
	raceCombo.setMaximumSize(new Dimension(150, 19));
	raceCombo.setMinimumSize(new Dimension(150, 19));
	raceCombo.setPreferredSize(new Dimension(150, 19));
	Component raceStrut = Box.createHorizontalStrut(60);
	editRace.setText("Edit");
	racePanel.add(raceLbl);
	racePanel.add(raceCombo);
//        racePanel.add(editRace);I have only prevented it from getting added
//									so the edit box is still floating in the code
//									in case someone finds a use for it later
	racePanel.add(raceStrut);

	workPanel.add(racePanel);

	// Create the Gender panel
	JPanel genderPanel = new JPanel();
	FlowLayout flowLayout4 = new FlowLayout();
	flowLayout4.setAlignment(FlowLayout.RIGHT);
	genderPanel.setLayout(flowLayout4);
	genderPanel.setMaximumSize(new Dimension(32767, 33));
	editGender.setText("Edit");
	JLabel genderLbl = new JLabel(PropertyFactory.getString("in_gender") + ": "); //$NON-NLS-1$ //$NON-NLS-2$
	genderCombo.setMinimumSize(new Dimension(150, 19));
	genderCombo.setPreferredSize(new Dimension(150, 19));
	Component genderStrut = Box.createHorizontalStrut(60);
	genderPanel.add(genderLbl);
	genderPanel.add(genderCombo);
//        genderPanel.add(editGender);I have only prevented it from getting added
//									so the edit box is still floating in the code
//									in case someone finds a use for it later
	genderPanel.add(genderStrut);

	workPanel.add(genderPanel);

	// Create the class/level panels
	for (int i = 0; i < MAX_CLASSES; i++)
	{
	    createClassPanel(workPanel, i);
	}

	// Create the Roll Stats panel
	JPanel statsPanel = new JPanel();
	FlowLayout flowLayout5 = new FlowLayout();
	flowLayout5.setAlignment(FlowLayout.RIGHT);
	statsPanel.setLayout(flowLayout5);
	statsPanel.setMaximumSize(new Dimension(32767, 33));
	editStats.setText("Edit");
	JLabel statsLbl = new JLabel(PropertyFactory.getString("in_Prefs_abilities") + ": "); //$NON-NLS-1$ //$NON-NLS-2$
	statsCombo.setMinimumSize(new Dimension(150, 19));
	statsCombo.setPreferredSize(new Dimension(150, 19));
	Component statsStrut = Box.createHorizontalStrut(60);
	statsPanel.add(statsLbl);
	statsPanel.add(statsCombo);
//        statsPanel.add(editStats);I have only prevented it from getting added
//									so the edit box is still floating in the code
//									in case someone finds a use for it later
	statsPanel.add(statsStrut);

	workPanel.add(statsPanel);

	final JPanel namePanel = new JPanel();
	final JLabel nameLabel = new JLabel("Name Set:");

	nameCombo.setMinimumSize(new Dimension(210, 19));
	nameCombo.setPreferredSize(new Dimension(210, 19));
	namePanel.add(nameLabel);
	namePanel.add(nameCombo);

	workPanel.add(namePanel);

	mainPanel.add(workPanel, java.awt.BorderLayout.CENTER);

	// Create the Button panel
	JPanel buttonPanel = new JPanel();

	okButton.setPreferredSize(new Dimension(80, 23));
	okButton.setText("OK");
	okButton.addActionListener(new ActionListener()
			   {

			       public void actionPerformed(ActionEvent evt)
			       {
				   okActionPerformed();
			       }

			   });
	cancelButton.setPreferredSize(new Dimension(80, 23));
	cancelButton.setText("Cancel");
	cancelButton.addActionListener(new ActionListener()
			       {

				   public void actionPerformed(ActionEvent evt)
				   {
				       cancelActionPerformed();
				   }

			       });
	buttonPanel.add(okButton);
	buttonPanel.add(cancelButton);

	mainPanel.add(buttonPanel, java.awt.BorderLayout.SOUTH);

	getContentPane().setLayout(new BorderLayout());
	getContentPane().add(mainPanel, BorderLayout.CENTER);
    }

    private void createClassPanel(final JPanel workPanel, final int number)
    {
	classCombos[number] = new JComboBox();
	lvlCombos[number] = new JComboBox();

	JPanel classPanel = new JPanel();
	FlowLayout flowLayout5 = new FlowLayout();
	flowLayout5.setAlignment(FlowLayout.CENTER);
	classPanel.setLayout(flowLayout5);
	JLabel classLbl = new JLabel(PropertyFactory.getString("in_classString") //$NON-NLS-1$
				     + "# " + number + ": "); //$NON-NLS-1$ //$NON-NLS-2$
	classCombos[number].setMinimumSize(new Dimension(150, 19));
	classCombos[number].setPreferredSize(new Dimension(150, 19));
	JLabel lvlLbl1 = new JLabel(PropertyFactory.getString("in_level") //$NON-NLS-1$
				    + "# " + number + ": "); //$NON-NLS-1$ //$NON-NLS-2$
	lvlCombos[number].setMinimumSize(new Dimension(80, 19));
	lvlCombos[number].setPreferredSize(new Dimension(80, 19));
	classPanel.add(classLbl);
	classPanel.add(classCombos[number]);
	classPanel.add(lvlLbl1);
	classPanel.add(lvlCombos[number]);

	workPanel.add(classPanel);
    }

    private void populateControls()
    {
	final NPCGenerator npcgen = NPCGenerator.getInst();
	final List<AlignGeneratorOption> customAlignOptions = npcgen.getAlignmentOptions();
	for (final GeneratorOption opt : customAlignOptions)
	{
	    alignCombo.addItem(opt);
	}

	final List<RaceGeneratorOption> customRaceOptions = npcgen.getCustomRaceOptions();
	for (final GeneratorOption opt : customRaceOptions)
	{
	    raceCombo.addItem(opt);
	}

	final List<GenderGeneratorOption> customGenderOptions = npcgen.getCustomGenderOptions();
	for (final GeneratorOption opt : customGenderOptions)
	{
	    genderCombo.addItem(opt);
	}

	for (int j = 0; j < MAX_CLASSES; j++)
	{
	    final List<ClassGeneratorOption> customClassOptions = npcgen.getCustomClassOptions();
	    for (final GeneratorOption opt : customClassOptions)
	    {
		classCombos[j].addItem(opt);
	    }
	    if (j > 0)
	    {
		classCombos[j].addItem(Constants.NONESELECTED);
		classCombos[j].setSelectedItem(Constants.NONESELECTED);
	    }
	    final List<LevelGeneratorOption> customLevelOptions = npcgen.getCustomLevelOptions();
	    for (final GeneratorOption opt : customLevelOptions)
	    {
		lvlCombos[j].addItem(opt);
	    }
	}

	GameMode gameMode = SettingsHandler.getGame();
	ReferenceManufacturer<RollMethod> mfg = gameMode.getModeContext().ref
				.getManufacturer(RollMethod.class);
	for (RollMethod rm : mfg.getOrderSortedObjects())
	{
	    statsCombo.addItem(rm);
	}

	List<NameElement> allNamesFiles = Names.findAllNamesFiles();
	Collections.sort(allNamesFiles);

	for (int i = 0; i < allNamesFiles.size(); i++)
	{
	    nameCombo.addItem(allNamesFiles.get(i));
	}
    }

}
