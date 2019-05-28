/*
 * Copyright 2003 (C) Devon Jones
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
package plugin.overland.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import pcgen.system.LanguageBundle;
import plugin.overland.model.RoomBoard;
import plugin.overland.model.RoomBoardFactory;
import plugin.overland.model.TravelMethod;
import plugin.overland.model.TravelMethodFactory;
import plugin.overland.model.TravelMethodListener;
import plugin.overland.model.TravelSpeedEvent;

public class OverPanel extends javax.swing.JPanel
{
	// ### Constants ###

	/** Value property used in {@link JFormattedTextField#addPropertyChangeListener(String, PropertyChangeListener)} */
	private static final String VALUE_PROPERTY = "value"; //$NON-NLS-1$

	protected static enum TravelMethodTextField
	{
		IMPERIAL_DISTANCE, METRIC_DISTANCE, TIME
	};

	// ### Fields ###

	private TravelMethodTextField lastEdited = null;

	private javax.swing.JButton butToDist;
	private javax.swing.JButton butToMap;
	private javax.swing.JButton butToReal;
	private javax.swing.JButton butToTime;
	private JButton butToTime2;
	private javax.swing.JComboBox cmbAnimal;
	private javax.swing.JComboBox cmbFood;
	private javax.swing.JComboBox cmbInn;
	private JComboBox<TravelMethod> cmbFile;
	private javax.swing.JLabel jLabel11;
	private javax.swing.JLabel jLabel15;
	private javax.swing.JLabel jLabel16;
	private javax.swing.JLabel jLabel17;
	private javax.swing.JLabel jLabel18;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel20;
	private javax.swing.JLabel jLabel21;
	private javax.swing.JLabel jLabel22;
	private javax.swing.JLabel jLabel23;
	private javax.swing.JLabel jLabel24;
	private javax.swing.JLabel jLabel25;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel imperialSpeedLabel;
	private javax.swing.JPanel panelScaleConv;
	private javax.swing.JPanel panelTravelTime;
	private javax.swing.JPanel panelRoomBoard;
	private javax.swing.JSeparator jSeparator1;
	private JLabel lblSpeed;
	private JFormattedTextField textMap;
	private JFormattedTextField textReal;
	private JFormattedTextField textScale;
	private JFormattedTextField txtAnim;
	private JFormattedTextField txtDayAnimal;
	private JFormattedTextField txtDayFood;
	private JFormattedTextField txtDayInn;
	private JFormattedTextField txtDayTotal;
	private JFormattedTextField txtDays;
	private JFormattedTextField txtDist;
	private JFormattedTextField txtPeop;
	private JLabel imperialSpeed;
	private JLabel metricSpeed;
	private JFormattedTextField txtDistMetric;
	private JFormattedTextField txtTime;
	private JLabel txtTotal;
	private JLabel txtWeekAnimal;
	private JLabel txtWeekFood;
	private JLabel txtWeekInn;
	private JLabel txtWeekTotal;
	private JTextArea ruleComment;
	private JLabel metricSpeedLabel;

	private JComboBox terrain;
	private JComboBox route;
	private JLabel percent;
	private JComboBox method;
	private JComboBox pace;
	private JComboBox choice;

	private DefaultComboBoxModel aModel;
	private TravelMethod selectedTM;
	private NumberFormat gp = NumberFormat.getNumberInstance();
	private NumberFormat nf = NumberFormat.getNumberInstance();

	private RoomBoard rb;

	/** holds the travel methods */
	@SuppressWarnings({"UseOfObsoleteCollectionType", "PMD.ReplaceVectorWithList"})
	private Vector<TravelMethod> tms;

	// ### Constructors ###

	/** Creates new form NameGenPanel
	 * @param DataDir
	 */
	public OverPanel(File DataDir)
	{
		initComponents();
		loadData(DataDir);
		initData();
	}

	private void butToDistActionPerformed()
	{
		if (selectedTM == null)
		{
			return;
		}
		lastEdited = TravelMethodTextField.TIME;
		Object o = txtTime.getValue();
		if (o != null && o instanceof Number)
		{
			double time = ((Number) o).doubleValue();

			txtDist.setValue(selectedTM.convertToMiles(time));
			txtDistMetric.setValue(selectedTM.convertToKm(time));
		}
	}

	private void butImperialToTimeActionPerformed()
	{
		if (selectedTM == null)
		{
			return;
		}
		lastEdited = TravelMethodTextField.IMPERIAL_DISTANCE;
		Object o = txtDist.getValue();
		if (o != null && o instanceof Number)
		{
			double miles = ((Number) o).doubleValue();

			txtTime.setValue(selectedTM.convertToTimeFromImperial(miles));
		}
	}

	private void butMetricToTimeActionPerformed()
	{
		if (selectedTM == null)
		{
			return;
		}
		lastEdited = TravelMethodTextField.METRIC_DISTANCE;
		Object o = txtDistMetric.getValue();
		if (o != null && o instanceof Number)
		{
			double km = ((Number) o).doubleValue();
			txtTime.setValue(selectedTM.convertToTimeFromMetric(km));
		}
	}

	/** Converts from real units to map units */
	private void butToMapActionPerformed(java.awt.event.ActionEvent evt)
	{
		float scale = ((Number) textScale.getValue()).floatValue();
		float realUnits = ((Number) textReal.getValue()).floatValue();
		float result = 0;
		result = realUnits / scale;
		textMap.setValue(result);
	}

	/** Converts from map units to real units */
	private void butToRealActionPerformed(java.awt.event.ActionEvent evt) //GEN-FIRST:event_butToRealActionPerformed
	{
		float scale = ((Number) textScale.getValue()).floatValue();
		float mapUnits = ((Number) textMap.getValue()).floatValue();
		float result = 0;
		result = scale * mapUnits;
		textReal.setValue(result);
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		java.awt.GridBagConstraints gridBagConstraints;

		panelScaleConv = new javax.swing.JPanel();
		jLabel2 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		textScale = new JFormattedTextField(nf);
		textScale.setColumns(3);
		jLabel4 = new javax.swing.JLabel();
		jLabel5 = new javax.swing.JLabel();
		textReal = new JFormattedTextField(nf);
		textMap = new JFormattedTextField(nf);
		butToMap = new javax.swing.JButton();
		butToReal = new javax.swing.JButton();
		panelTravelTime = new javax.swing.JPanel();
		imperialSpeedLabel = new javax.swing.JLabel();
		metricSpeedLabel = new JLabel();
		cmbFile = new javax.swing.JComboBox();
		txtDist = new JFormattedTextField(nf);
		txtDist.setColumns(4);
		txtDistMetric = new JFormattedTextField(nf);
		jLabel11 = new javax.swing.JLabel();
		txtTime = new JFormattedTextField(nf);
		txtTime.setColumns(4);
		butToTime = new javax.swing.JButton();
		butToTime2 = new JButton();
		butToDist = new javax.swing.JButton();
		lblSpeed = new JLabel();
		panelRoomBoard = new javax.swing.JPanel();
		jLabel15 = new javax.swing.JLabel();
		jLabel16 = new javax.swing.JLabel();
		jLabel17 = new javax.swing.JLabel();
		jLabel18 = new javax.swing.JLabel();
		jLabel20 = new javax.swing.JLabel();
		jLabel21 = new javax.swing.JLabel();
		jLabel22 = new javax.swing.JLabel();
		txtDayFood = new JFormattedTextField(gp);
		txtDayInn = new JFormattedTextField(gp);
		txtDayAnimal = new JFormattedTextField(gp);
		txtWeekFood = new JLabel();
		txtWeekInn = new JLabel();
		txtWeekAnimal = new JLabel();
		txtDays = new JFormattedTextField(nf);
		txtTotal = new JLabel();
		cmbFood = new javax.swing.JComboBox();
		cmbInn = new javax.swing.JComboBox();
		cmbAnimal = new javax.swing.JComboBox();
		jSeparator1 = new javax.swing.JSeparator();
		jLabel23 = new javax.swing.JLabel();
		jLabel24 = new javax.swing.JLabel();
		txtPeop = new JFormattedTextField(nf);
		txtAnim = new JFormattedTextField(nf);
		txtDayTotal = new JFormattedTextField(nf);
		txtWeekTotal = new JLabel();
		jLabel25 = new javax.swing.JLabel();
		JPanel panel = new JPanel(new java.awt.GridBagLayout());

		int gap = 3;
		Insets stdInsets = new Insets(gap, gap, gap, gap);

		panelScaleConv.setLayout(new java.awt.GridBagLayout());

		panelScaleConv.setBorder(
			BorderFactory.createTitledBorder(
				LanguageBundle.getString("in_plugin_overland_scaleConverter"))); //$NON-NLS-1$

		jLabel2.setText(LanguageBundle.getString("in_plugin_overland_realUnits")); //$NON-NLS-1$
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = stdInsets;
		panelScaleConv.add(jLabel2, gridBagConstraints);

		jLabel3.setText("1"); //$NON-NLS-1$
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = stdInsets;
		panelScaleConv.add(jLabel3, gridBagConstraints);

		textScale.setHorizontalAlignment(SwingConstants.CENTER);
		textScale.setValue(1);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = stdInsets;
		panelScaleConv.add(textScale, gridBagConstraints);

		jLabel4.setText("="); //$NON-NLS-1$
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = stdInsets;
		panelScaleConv.add(jLabel4, gridBagConstraints);

		jLabel5.setText(LanguageBundle.getString("in_plugin_overland_mapUnits")); //$NON-NLS-1$
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = stdInsets;
		panelScaleConv.add(jLabel5, gridBagConstraints);

		textReal.addKeyListener(new KeyListenerImplementation(butToMap));
		textReal.setHorizontalAlignment(SwingConstants.CENTER);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = stdInsets;
		panelScaleConv.add(textReal, gridBagConstraints);

		textMap.addKeyListener(new KeyListenerImplementation(butToReal));
		textMap.setHorizontalAlignment(SwingConstants.CENTER);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = stdInsets;
		panelScaleConv.add(textMap, gridBagConstraints);

		butToMap.setText(LanguageBundle.getString("in_plugin_overland_leftArrow")); //$NON-NLS-1$
		butToMap.addActionListener(this::butToMapActionPerformed);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		panelScaleConv.add(butToMap, gridBagConstraints);

		butToReal.setText(LanguageBundle.getString("in_plugin_overland_rightArrow")); //$NON-NLS-1$
		butToReal.addActionListener(this::butToRealActionPerformed);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		panelScaleConv.add(butToReal, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
		panel.add(panelScaleConv, gridBagConstraints);

		// Travel time panel

		panelTravelTime.setLayout(new java.awt.GridBagLayout());

		panelTravelTime.setBorder(
			BorderFactory.createTitledBorder(
				LanguageBundle.getString("in_plugin_overland_travelTime"))); //$NON-NLS-1$

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
		gridBagConstraints.insets = new Insets(0, 2 * gap, 2 * gap, 2 * gap);
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		panelTravelTime.add(cmbFile, gridBagConstraints);

		method = new JComboBox();
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = stdInsets;
		gridBagConstraints.gridwidth = 2;
		panelTravelTime.add(method, gridBagConstraints);

		JPanel terrainRoute = new JPanel(new GridBagLayout());

		terrain = new JComboBox();
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = stdInsets;
		terrainRoute.add(terrain, gridBagConstraints);

		route = new JComboBox();
		terrainRoute.add(route, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.gridwidth = 2;
		panelTravelTime.add(terrainRoute, gridBagConstraints);

		percent = new JLabel();
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.insets = stdInsets;
		panelTravelTime.add(percent, gridBagConstraints);

		JPanel paceChoice = new JPanel(new GridBagLayout());
		pace = new JComboBox();
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = stdInsets;
		paceChoice.add(pace, gridBagConstraints);
		choice = new JComboBox();
		paceChoice.add(choice, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = GridBagConstraints.LINE_START;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridheight = 2;
		panelTravelTime.add(paceChoice, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = stdInsets;
		panelTravelTime.add(imperialSpeedLabel, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.insets = stdInsets;
		panelTravelTime.add(metricSpeedLabel, gridBagConstraints);

		// "Special rules stuff will go there. This is used as default column name."
		ruleComment = new JTextArea();
		ruleComment.setRows(3);
		ruleComment.setEditable(false);
		ruleComment.setFocusable(false);
		ruleComment.setLineWrap(true);
		// TODO i18n this. this is not correct in non spaced language like Japanese,
		// unless it is done correctly by Java?
		ruleComment.setWrapStyleWord(true);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		panelTravelTime.add(new JScrollPane(ruleComment), gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		// XXX use a line or a component that make more sense than this menu component
		panelTravelTime.add(new JSeparator(), gridBagConstraints);

		imperialSpeed = new JLabel();
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 7;
		panelTravelTime.add(imperialSpeed, gridBagConstraints);

		metricSpeed = new JLabel();
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 8;
		panelTravelTime.add(metricSpeed, gridBagConstraints);

		JPanel conversion = new JPanel(new GridBagLayout());

		txtDist.addKeyListener(new KeyListenerImplementation(butToTime));
		txtDist.setHorizontalAlignment(SwingConstants.CENTER);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = stdInsets;
		conversion.add(txtDist, gridBagConstraints);

		JLabel miles = new JLabel(LanguageBundle.getString("in_plugin_overland_fieldMiles")); //$NON-NLS-1$
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(gap, 0, gap, gap);
		conversion.add(miles, gridBagConstraints);

		butToTime.setText(LanguageBundle.getString("in_plugin_overland_rightArrow")); //$NON-NLS-1$
		butToTime.setEnabled(false);
		butToTime.addActionListener(new java.awt.event.ActionListener()
		{
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				butImperialToTimeActionPerformed();
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		conversion.add(butToTime, gridBagConstraints);

		txtDistMetric.addKeyListener(new KeyListenerImplementation(butToTime2));
		txtDistMetric.setHorizontalAlignment(SwingConstants.CENTER);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = stdInsets;
		conversion.add(txtDistMetric, gridBagConstraints);

		lblSpeed.setText(LanguageBundle.getString("in_plugin_overland_fieldKm")); //$NON-NLS-1$
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
		gridBagConstraints.insets = new java.awt.Insets(gap, gap, gap, gap);
		conversion.add(lblSpeed, gridBagConstraints);

		butToTime2.setText(LanguageBundle.getString("in_plugin_overland_rightArrow")); //$NON-NLS-1$
		butToTime2.setEnabled(false);
		butToTime.addActionListener(new java.awt.event.ActionListener()
		{
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				butMetricToTimeActionPerformed();
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		conversion.add(butToTime2, gridBagConstraints);

		txtTime.addKeyListener(new KeyListenerImplementation(butToDist));
		txtTime.setHorizontalAlignment(SwingConstants.CENTER);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 4;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(gap, gap, gap, 0);
		conversion.add(txtTime, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 5;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.insets = stdInsets;
		conversion.add(jLabel11, gridBagConstraints);

		butToDist.setText(LanguageBundle.getString("in_plugin_overland_leftArrow")); //$NON-NLS-1$
		butToDist.addActionListener(new java.awt.event.ActionListener()
		{
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				butToDistActionPerformed();
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 2;
		conversion.add(butToDist, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 8;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridheight = 4;
		panelTravelTime.add(conversion, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		panel.add(panelTravelTime, gridBagConstraints);

		// Room and board

		panelRoomBoard.setLayout(new java.awt.GridBagLayout());

		panelRoomBoard.setBorder(
			BorderFactory.createTitledBorder(
				LanguageBundle.getString("in_plugin_overland_roomAndBoard"))); //$NON-NLS-1$

		jLabel15.setText(LanguageBundle.getString("in_plugin_overland_perDay")); //$NON-NLS-1$
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.insets = stdInsets;
		panelRoomBoard.add(jLabel15, gridBagConstraints);

		jLabel16.setText(LanguageBundle.getString("in_plugin_overland_food")); //$NON-NLS-1$
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = stdInsets;
		panelRoomBoard.add(jLabel16, gridBagConstraints);

		jLabel17.setText(LanguageBundle.getString("in_plugin_overland_perWeek")); //$NON-NLS-1$
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.insets = stdInsets;
		panelRoomBoard.add(jLabel17, gridBagConstraints);

		jLabel18.setText(LanguageBundle.getString("in_plugin_overland_lodging")); //$NON-NLS-1$
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = stdInsets;
		panelRoomBoard.add(jLabel18, gridBagConstraints);

		// some space between top and middle
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = stdInsets;
		panelRoomBoard.add(new JPanel(), gridBagConstraints);

		jLabel20.setText(LanguageBundle.getString("in_plugin_overland_animals")); //$NON-NLS-1$
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = stdInsets;
		panelRoomBoard.add(jLabel20, gridBagConstraints);

		jLabel21.setText(LanguageBundle.getString("in_plugin_overland_days")); //$NON-NLS-1$
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 10;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = stdInsets;
		panelRoomBoard.add(jLabel21, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 11;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		panelRoomBoard.add(new JSeparator(), gridBagConstraints);

		jLabel22.setText(LanguageBundle.getString("in_plugin_overland_total")); //$NON-NLS-1$
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 12;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = stdInsets;
		panelRoomBoard.add(jLabel22, gridBagConstraints);

		txtDayFood.setHorizontalAlignment(SwingConstants.CENTER);
		txtDayFood.addPropertyChangeListener(VALUE_PROPERTY, new PropertyChangeListener()
		{

			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				txtDayFoodActionPerformed();
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = stdInsets;
		panelRoomBoard.add(txtDayFood, gridBagConstraints);

		txtDayInn.setHorizontalAlignment(SwingConstants.CENTER);
		txtDayInn.addPropertyChangeListener(VALUE_PROPERTY, new PropertyChangeListener()
		{

			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				txtDayFoodActionPerformed();
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = stdInsets;
		panelRoomBoard.add(txtDayInn, gridBagConstraints);

		txtDayAnimal.setHorizontalAlignment(SwingConstants.CENTER);
		txtDayAnimal.addPropertyChangeListener(VALUE_PROPERTY, new PropertyChangeListener()
		{

			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				txtDayFoodActionPerformed();
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = stdInsets;
		panelRoomBoard.add(txtDayAnimal, gridBagConstraints);

		txtWeekFood.setHorizontalAlignment(SwingConstants.CENTER);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.insets = stdInsets;
		panelRoomBoard.add(txtWeekFood, gridBagConstraints);

		txtWeekInn.setHorizontalAlignment(SwingConstants.CENTER);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.insets = stdInsets;
		panelRoomBoard.add(txtWeekInn, gridBagConstraints);

		txtWeekAnimal.setHorizontalAlignment(SwingConstants.CENTER);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.insets = stdInsets;
		panelRoomBoard.add(txtWeekAnimal, gridBagConstraints);

		txtDays.setHorizontalAlignment(SwingConstants.CENTER);
		txtDays.addPropertyChangeListener(VALUE_PROPERTY, new PropertyChangeListener()
		{

			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				txtDaysActionPerformed();
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 10;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = stdInsets;
		panelRoomBoard.add(txtDays, gridBagConstraints);

		txtTotal.setHorizontalAlignment(SwingConstants.CENTER);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 12;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = stdInsets;
		panelRoomBoard.add(txtTotal, gridBagConstraints);

		cmbFood.addActionListener(new java.awt.event.ActionListener()
		{
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				txtPeopActionPerformed();
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = stdInsets;
		panelRoomBoard.add(cmbFood, gridBagConstraints);

		cmbInn.addActionListener(new java.awt.event.ActionListener()
		{
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				txtPeopActionPerformed();
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = stdInsets;
		panelRoomBoard.add(cmbInn, gridBagConstraints);

		cmbAnimal.addActionListener(new java.awt.event.ActionListener()
		{
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				txtPeopActionPerformed();
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = stdInsets;
		panelRoomBoard.add(cmbAnimal, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 8;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		panelRoomBoard.add(jSeparator1, gridBagConstraints);

		jLabel23.setText(LanguageBundle.getString("in_plugin_overland_people")); //$NON-NLS-1$
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.LINE_START;
		gridBagConstraints.insets = stdInsets;
		panelRoomBoard.add(jLabel23, gridBagConstraints);

		jLabel24.setText(LanguageBundle.getString("in_plugin_overland_animals")); //$NON-NLS-1$
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = GridBagConstraints.LINE_START;
		gridBagConstraints.insets = stdInsets;
		panelRoomBoard.add(jLabel24, gridBagConstraints);

		txtPeop.setHorizontalAlignment(SwingConstants.CENTER);
		txtPeop.setColumns(3);
		txtPeop.addPropertyChangeListener(VALUE_PROPERTY, new PropertyChangeListener()
		{

			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				txtPeopActionPerformed();
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.LINE_START;
		gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
		gridBagConstraints.insets = stdInsets;
		panelRoomBoard.add(txtPeop, gridBagConstraints);

		txtAnim.setHorizontalAlignment(SwingConstants.CENTER);
		txtAnim.setColumns(3);
		txtAnim.addPropertyChangeListener(VALUE_PROPERTY, new PropertyChangeListener()
		{

			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				txtPeopActionPerformed();
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = GridBagConstraints.LINE_START;
		gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
		gridBagConstraints.insets = stdInsets;
		panelRoomBoard.add(txtAnim, gridBagConstraints);

		txtDayTotal.setHorizontalAlignment(SwingConstants.CENTER);
		txtDayTotal.addPropertyChangeListener(VALUE_PROPERTY, new PropertyChangeListener()
		{

			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				txtDaysActionPerformed();
			}
		});

		txtDayTotal.setEditable(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 9;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = stdInsets;
		panelRoomBoard.add(txtDayTotal, gridBagConstraints);

		txtWeekTotal.setHorizontalAlignment(SwingConstants.CENTER);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 9;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = stdInsets;
		panelRoomBoard.add(txtWeekTotal, gridBagConstraints);

		jLabel25.setText(LanguageBundle.getString("in_plugin_overland_total")); //$NON-NLS-1$
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 9;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = stdInsets;
		panelRoomBoard.add(jLabel25, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		panel.add(panelRoomBoard, gridBagConstraints);

		setLayout(new BorderLayout());
		add(new JScrollPane(panel), BorderLayout.CENTER);
	}

	private void initData()
	{
		nf.setMaximumFractionDigits(2); //This will display other numbers
		gp.setMaximumFractionDigits(3); //This will correctly display currency

		aModel = new DefaultComboBoxModel<>(tms);
		cmbFile.setModel(aModel);
		cmbFile.addItemListener(new ItemListener()
		{

			@Override
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() == ItemEvent.DESELECTED)
				{
					return;
				}
				changedTM();
			}
		});
		cmbFile.setSelectedItem(tms.get(0));
		// For some reason the panel is not updated by calling setSelectedItem
		changedTM();

		txtPeop.setValue(1);
		txtAnim.setValue(1);
		txtDays.setValue(1);
		//Begin costs setup
		//the data is loaded into the data structures, now just load the combo boxes
		for (int i = 0; i < rb.getInns().getCount(); i++)
		{
			cmbInn.addItem(rb.getInns().get(i).getName());
		}
		cmbInn.setSelectedIndex(0);

		for (int i = 0; i < rb.getFoods().getCount(); i++)
		{
			cmbFood.addItem(rb.getFoods().get(i).getName());
		}
		cmbFood.setSelectedIndex(0);

		for (int i = 0; i < rb.getAnimals().getCount(); i++)
		{
			cmbAnimal.addItem(rb.getAnimals().get(i).getName());
		}
		cmbAnimal.setSelectedIndex(0);
	}

	private void changedTM()
	{
		// remove previous listener
		if (selectedTM != null)
		{
			selectedTM.removeTravelMethodListener(listener);
		}
		selectedTM = (TravelMethod) aModel.getSelectedItem();
		// XXX correct?
		if (selectedTM == null)
		{
			return;
		}

		method.setModel(selectedTM.getMethodsModel());
		method.setSelectedIndex(0);
		pace.setModel(selectedTM.getPaceModel());
		choice.setModel(selectedTM.getChoiceModel());
		terrain.setModel(selectedTM.getTerrainsModel());
		route.setModel(selectedTM.getRoutesModel());
		selectedTM.addTravelMethodListener(listener);

		pace.setSelectedIndex(0);
		choice.setSelectedIndex(0);
		terrain.setSelectedIndex(0);
		route.setSelectedIndex(0);
	}

	private TravelMethodListener listener = new TravelMethodListener()
	{
		private static final String NEWLINE = "\n"; //$NON-NLS-1$

		@Override
		public void multUpdated(TravelSpeedEvent e)
		{
			String changed = e.getChanged();
			// HTMLize the string
			if (changed != null && changed.contains(NEWLINE))
			{
				changed = "<html>" //$NON-NLS-1$ 
						+ changed.replaceAll(NEWLINE, "<br>")//$NON-NLS-1$ 
						+ "</html>"; //$NON-NLS-1$
			}
			percent.setText(changed);
		}

		@Override
		public void unmodifiedSpeedUpdated(EventObject e)
		{
			imperialSpeedLabel.setText(selectedTM.getUnmodifiedImperialSpeedString());
			metricSpeedLabel.setText(selectedTM.getUnmodifiedMetricSpeedString());
		}

		@Override
		public void speedUpdated(EventObject e)
		{
			String imperialSpeedString = selectedTM.getImperialSpeedString();
			imperialSpeed.setText(imperialSpeedString);
			butToTime.setEnabled(imperialSpeedString != null);
			String metricSpeedString = selectedTM.getMetricSpeedString();
			metricSpeed.setText(metricSpeedString);
			butToTime2.setEnabled(metricSpeedString != null);
			butToDist.setEnabled(imperialSpeedString != null && metricSpeedString != null);
			// Updates other text fields based on the last edited one
			if (lastEdited != null)
			{
				switch (lastEdited)
				{
					case IMPERIAL_DISTANCE:
						butImperialToTimeActionPerformed();
						break;
					case METRIC_DISTANCE:
						butMetricToTimeActionPerformed();
						break;
					case TIME:
						butToDistActionPerformed();
						break;
					default:
						//Case not caught, should this cause an error?
						break;
				}
			}
		}

		@Override
		public void useDaysChanged(TravelSpeedEvent e)
		{
			jLabel11.setText(e.getChanged());
		}

		@Override
		public void commentChanged(TravelSpeedEvent e)
		{
			ruleComment.setText(e.getChanged());
		}
	};

	private void loadData(File aDataDir)
	{
		//Populate Travel Methods
		tms = TravelMethodFactory.load(aDataDir);

		//Populate Room and Board
		rb = RoomBoardFactory.load(aDataDir);
	}

	private void txtDayFoodActionPerformed()
	{
		updateMidUI();
	}

	private void txtDaysActionPerformed()
	{
		updateBottomUI();
	}

	private void txtPeopActionPerformed()
	{
		updateTopUI();
	}

	/** This method updates the Bottom portions of the UI based on changes in the total cost
	 *  and number of days.  It sets the value into the total box
	 */
	private void updateBottomUI()
	{
		float DayTotal = ((Number) txtDayTotal.getValue()).floatValue();
		Object value = txtDays.getValue();
		if (value instanceof Number)
		{
			float Days = ((Number) value).floatValue();
			float result = DayTotal * Days;

			txtTotal.setText(gp.format(result));
		}
		else
		{
			txtTotal.setText(""); //$NON-NLS-1$
		}
	}

	/** This method updates the middle portions of the UI based on changes in the daily costs
	 *  It sets the value into the daily total boxes
	 */
	private void updateMidUI()
	{
		Object inn = txtDayInn.getValue();
		float DayInn = inn instanceof Number ? ((Number) inn).floatValue() : 0.0f;
		Object food = txtDayFood.getValue();
		float DayFood = food instanceof Number ? ((Number) food).floatValue() : 0.0f;
		Object animal = txtDayAnimal.getValue();
		float DayAnimal = animal instanceof Number ? ((Number) txtDayAnimal.getValue()).floatValue() : 0.0f;
		float result = DayInn + DayFood + DayAnimal;

		txtDayTotal.setValue(result);
		result *= 7; //Compute weekly
		txtWeekTotal.setText(gp.format(result));

		updateBottomUI(); //propagate changes down
	}

	/** This method updates the top portions of the UI based on changes in number of people or animals
	 *  or changes in quality of RB.  It sets the values into the daily and weekly cost boxes
	 */
	private void updateTopUI()
	{
		//First, retrieve the costs of everything

		int i1 = cmbFood.getSelectedIndex();

		float food = i1 >= 0 ? rb.getFoods().get(i1).getCost() : 0;

		int i2 = cmbInn.getSelectedIndex();

		float inn = i2 >= 0 ? rb.getInns().get(i2).getCost() : 0;

		int i3 = cmbAnimal.getSelectedIndex();

		float animal = i3 >= 0 ? rb.getAnimals().get(i3).getCost() : 0;

		float result = 0;

		Number people = (Number) txtPeop.getValue();
		Number value = (Number) txtAnim.getValue();
		//now set them all
		if (people != null)
		{
			int numPeople = people.intValue();

			result = food * numPeople;
			txtDayFood.setValue(result);
			result *= 7;
			txtWeekFood.setText(gp.format(result)); //but here we use gp

			result = inn * numPeople;
			txtDayInn.setValue(result);
			result *= 7;
			txtWeekInn.setText(gp.format(result)); //but here we use gp
		}
		if (value != null)
		{
			int numAnimal = value.intValue();

			result = animal * numAnimal;
			txtDayAnimal.setValue(result);
			result *= 7;
			txtWeekAnimal.setText(gp.format(result)); //but here we use gp
		}

		updateMidUI(); //propagate changes down
	}

	private final class KeyListenerImplementation extends KeyAdapter
	{
		private JButton button;

		/**
		 * @param button
		 */
		public KeyListenerImplementation(JButton button)
		{
			this.button = button;
		}

		@Override
		public void keyReleased(KeyEvent e)
		{
			if (KeyEvent.VK_ENTER == e.getKeyCode())
			{
				button.doClick();
			}
		}

	}

}
