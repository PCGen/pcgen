/*
 * Copyright 2002 (C) Chris Ryan
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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.table.AbstractTableModel;

import pcgen.cdom.base.Constants;
import pcgen.core.CustomData;
import pcgen.core.Globals;
import pcgen.core.PointBuyCost;
import pcgen.core.PointBuyMethod;
import pcgen.core.RuleConstants;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui3.GuiUtility;
import pcgen.gui3.JFXPanelFromResource;
import pcgen.gui3.component.OKCloseButtonBar;
import pcgen.gui3.dialog.NewPurchaseMethodDialogController;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.system.LanguageBundle;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Tooltip;

/**
 * The Class {@code PurchaseModeFrame} is responsible for displaying
 * the character stats purchase mode (aka point buy) configuration dialog.  
 */
public final class PurchaseModeFrame extends JDialog
{
	private static final String TITLE = LanguageBundle.getString("in_Prefs_purModConf"); //$NON-NLS-1$
	private static final int STANDARD_MIN_PURCHASE_SCORE = 8;
	private static final int STANDARD_MAX_PURCHASE_SCORE = 18;
	private JButton removeMethodButton;
	private JComboBox currentPurchaseMethods;
	private JLabel statusBar;

	private JScrollPane jScrollPane1;
	private JTextField purchaseMethodPointsEdit;
	private JTextField purchaseScoreMaxEdit;
	private JTextField purchaseScoreMinEdit;
	private PurchaseModel purchaseModel;

	private int statMin = PurchaseModeFrame.STANDARD_MIN_PURCHASE_SCORE;
	private int statMax = PurchaseModeFrame.STANDARD_MAX_PURCHASE_SCORE;

	/** Creates new form PurchaseModeFrame
	 */
	PurchaseModeFrame()
	{
		initComponents();
	}

	//
	// Pop up a window to get information about a new purchase method
	//
	private void addMethodButtonActionPerformed()
	{
		var npmd = new JFXPanelFromResource<>(
				NewPurchaseMethodDialogController.class,
				"NewPurchaseMethodDialog.fxml"
		);

		// todo: i18n
		npmd.showAndBlock("New Purchase Method");
		npmd.getController().isCancelled();

		if (!npmd.getController().isCancelled())
		{
			final String methodName = npmd.getController().getEnteredName();

			if (SettingsHandler.getGameAsProperty().get().getModeContext().getReferenceContext()
				.silentlyGetConstructedCDOMObject(PointBuyMethod.class, methodName) == null)
			{
				PointBuyMethod pbm = new PointBuyMethod();
				pbm.setName(methodName);
				pbm.setPointFormula(Integer.toString(npmd.getController().getEnteredPoints()));
				currentPurchaseMethods.addItem(pbm);
				currentPurchaseMethods.setSelectedItem(pbm);
			}
			else
			{
				ShowMessageDelegate.showMessageDialog(LanguageBundle.getString("in_Prefs_cannotAdd"), //$NON-NLS-1$
					Constants.APPLICATION_NAME, MessageType.ERROR);
			}
		}
	}

	private void cancelButtonActionPerformed()
	{
		dispose();
	}

	private static int convertStringToInt(String valueString)
	{
		int value;

		try
		{
			value = Integer.parseInt(valueString);
		}
		catch (final NumberFormatException nfe)
		{
			// bad value
			value = -1;
		}

		return value;
	}

	/**
	 * Display info about the selected purchase method.
	 */
	private void currentPurchaseMethodsActionPerformed()
	{
		final PointBuyMethod method = (PointBuyMethod) currentPurchaseMethods.getSelectedItem();

		if (method == null)
		{
			removeMethodButton.setEnabled(false);
			purchaseMethodPointsEdit.setText(""); //$NON-NLS-1$
		}
		else
		{
			purchaseMethodPointsEdit.setText(method.getPointFormula());
			removeMethodButton.setEnabled(true);
		}
	}

	/** Exit Purchase Mode Frame */
	private void exitForm()
	{
		// TODO
	}

	private void initComponents()
	{

		purchaseScoreMinEdit = new JTextField(3);
		purchaseScoreMaxEdit = new JTextField(3);
		statusBar = new JLabel();
		currentPurchaseMethods = new JComboBox<>();
		purchaseMethodPointsEdit = new JTextField(4);
		removeMethodButton = new JButton();

		AbstractButton okButton = new JButton();
		okButton.addActionListener(e -> CustomData.writePurchaseModeConfiguration());

		jScrollPane1 = new JScrollPane();

		getContentPane().setLayout(new GridBagLayout());

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle(PurchaseModeFrame.TITLE);
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent evt)
			{
				exitForm();
			}
		});

		Container jPanel1 = new JPanel();
		jPanel1.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 5));

		JLabel purchaseScoreMinLabel = new JLabel();
		purchaseScoreMinLabel.setText(LanguageBundle.getString("in_Prefs_purchMin")); //$NON-NLS-1$
		purchaseScoreMinLabel.setToolTipText(LanguageBundle.getString("in_Prefs_purchMinTip")); //$NON-NLS-1$
		purchaseScoreMinLabel.setPreferredSize(new Dimension(140, 15));
		jPanel1.add(purchaseScoreMinLabel);

		purchaseScoreMinEdit.setHorizontalAlignment(SwingConstants.RIGHT);
		purchaseScoreMinEdit.addActionListener(evt -> purchaseScoreMinValueActionPerformed());
		purchaseScoreMinEdit.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				purchaseScoreMinValueActionPerformed();
			}

		});
		jPanel1.add(purchaseScoreMinEdit);

		AbstractButton purchaseScoreMinIncreaseButton = new JButton();
		purchaseScoreMinIncreaseButton.setText(LanguageBundle.getString("in_Prefs_plus")); //$NON-NLS-1$
		purchaseScoreMinIncreaseButton.setToolTipText(LanguageBundle.getString("in_Prefs_incMin")); //$NON-NLS-1$
		purchaseScoreMinIncreaseButton.addActionListener(evt -> purchaseScoreMinIncreaseButtonActionPerformed());

		jPanel1.add(purchaseScoreMinIncreaseButton);

		AbstractButton purchaseScoreMinDecreaseButton = new JButton();
		purchaseScoreMinDecreaseButton.setText(LanguageBundle.getString("in_Prefs_minus")); //$NON-NLS-1$
		purchaseScoreMinDecreaseButton.setToolTipText(LanguageBundle.getString("in_Prefs_decMin")); //$NON-NLS-1$
		purchaseScoreMinDecreaseButton.addActionListener(evt -> purchaseScoreMinDecreaseButtonActionPerformed());

		jPanel1.add(purchaseScoreMinDecreaseButton);

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		getContentPane().add(jPanel1, gridBagConstraints);

		Container jPanel2 = new JPanel();
		jPanel2.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 5));

		JLabel purchaseScoreMaxLabel = new JLabel();
		purchaseScoreMaxLabel.setText(LanguageBundle.getString("in_Prefs_purchMax")); //$NON-NLS-1$
		purchaseScoreMaxLabel.setToolTipText(LanguageBundle.getString("in_Prefs_purchMaxTip")); //$NON-NLS-1$
		purchaseScoreMaxLabel.setPreferredSize(new Dimension(140, 15));
		jPanel2.add(purchaseScoreMaxLabel);

		purchaseScoreMaxEdit.setHorizontalAlignment(SwingConstants.RIGHT);
		purchaseScoreMaxEdit.addActionListener(evt -> purchaseScoreMaxValueActionPerformed());
		purchaseScoreMaxEdit.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				purchaseScoreMaxValueActionPerformed();
			}

		});
		jPanel2.add(purchaseScoreMaxEdit);

		AbstractButton purchaseScoreMaxIncreaseButton = new JButton();
		purchaseScoreMaxIncreaseButton.setText(LanguageBundle.getString("in_Prefs_plus")); //$NON-NLS-1$
		purchaseScoreMaxIncreaseButton.setToolTipText(LanguageBundle.getString("in_Prefs_incMax")); //$NON-NLS-1$
		purchaseScoreMaxIncreaseButton.addActionListener(evt -> purchaseScoreMaxIncreaseButtonActionPerformed());

		jPanel2.add(purchaseScoreMaxIncreaseButton);

		AbstractButton purchaseScoreMaxDecreaseButton = new JButton();
		purchaseScoreMaxDecreaseButton.setText(LanguageBundle.getString("in_Prefs_minus")); //$NON-NLS-1$
		purchaseScoreMaxDecreaseButton.setToolTipText(LanguageBundle.getString("in_Prefs_decMax")); //$NON-NLS-1$
		purchaseScoreMaxDecreaseButton.addActionListener(evt -> purchaseScoreMaxDecreaseButtonActionPerformed());

		jPanel2.add(purchaseScoreMaxDecreaseButton);

		GridBagConstraints bagConstraints = new GridBagConstraints();
		bagConstraints.gridx = 0;
		bagConstraints.gridy = 2;
		bagConstraints.fill = GridBagConstraints.HORIZONTAL;
		bagConstraints.anchor = GridBagConstraints.NORTHWEST;
		bagConstraints.weightx = 1.0;
		getContentPane().add(jPanel2, bagConstraints);

		JComponent purchaseMethodPanel = new JPanel();
		purchaseMethodPanel.setLayout(new GridBagLayout());
		purchaseMethodPanel
			.setBorder(
				BorderFactory.createTitledBorder(LanguageBundle.getString("in_Prefs_allowPoints"))); //$NON-NLS-1$
		bagConstraints = new GridBagConstraints();
		bagConstraints.gridx = 0;
		bagConstraints.gridy = 3;
		bagConstraints.fill = GridBagConstraints.HORIZONTAL;
		bagConstraints.anchor = GridBagConstraints.NORTHWEST;
		bagConstraints.weightx = 1.0;
		getContentPane().add(purchaseMethodPanel, bagConstraints);

		Container purchaseMethodNamePanel = new JPanel();
		purchaseMethodNamePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 5));
		JLabel savedMethodLabel = new JLabel();
		savedMethodLabel.setText(LanguageBundle.getString("in_Prefs_savedMethods")); //$NON-NLS-1$
		savedMethodLabel.setPreferredSize(new Dimension(140, 15));
		purchaseMethodNamePanel.add(savedMethodLabel);
		purchaseMethodNamePanel.add(currentPurchaseMethods);

		bagConstraints = new GridBagConstraints();
		bagConstraints.gridx = 0;
		bagConstraints.gridy = 0;
		bagConstraints.fill = GridBagConstraints.HORIZONTAL;
		bagConstraints.anchor = GridBagConstraints.NORTHWEST;
		bagConstraints.weightx = 1.0;
		getContentPane().add(purchaseMethodNamePanel, bagConstraints);
		purchaseMethodPanel.add(purchaseMethodNamePanel, bagConstraints);

		Container purchaseMethodPointsPanel = new JPanel();
		purchaseMethodPointsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 5));
		JLabel methodPointsLabel = new JLabel();
		methodPointsLabel.setText(LanguageBundle.getString("in_Prefs_points")); //$NON-NLS-1$
		methodPointsLabel.setPreferredSize(new Dimension(140, 15));
		purchaseMethodPointsPanel.add(methodPointsLabel);
		purchaseMethodPointsEdit.setHorizontalAlignment(SwingConstants.RIGHT);
		purchaseMethodPointsEdit.setEditable(false);

		//purchaseMethodPointsEdit.setText("10");
		purchaseMethodPointsPanel.add(purchaseMethodPointsEdit);

		bagConstraints = new GridBagConstraints();
		bagConstraints.gridx = 0;
		bagConstraints.gridy = 1;
		bagConstraints.fill = GridBagConstraints.HORIZONTAL;
		bagConstraints.anchor = GridBagConstraints.NORTHWEST;
		bagConstraints.weightx = 1.0;

		//		getContentPane().add(purchaseMethodPointsPanel, gridBagConstraints);
		purchaseMethodPanel.add(purchaseMethodPointsPanel, bagConstraints);

		currentPurchaseMethods.setPreferredSize(new Dimension(140, 21));
		currentPurchaseMethods.addItemListener(evt -> currentPurchaseMethodsActionPerformed());

		Container purchaseMethodButtonPanel = new JPanel();
		purchaseMethodButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		AbstractButton addMethodButton = new JButton();
		addMethodButton.setText(LanguageBundle.getString("in_Prefs_new")); //$NON-NLS-1$
		addMethodButton.addActionListener(evt -> addMethodButtonActionPerformed());
		purchaseMethodButtonPanel.add(addMethodButton);
		removeMethodButton.setText(LanguageBundle.getString("in_Prefs_remove")); //$NON-NLS-1$
		removeMethodButton.addActionListener(evt -> removeMethodButtonActionPerformed());
		purchaseMethodButtonPanel.add(removeMethodButton);
		bagConstraints = new GridBagConstraints();
		bagConstraints.gridx = 0;
		bagConstraints.gridy = 2;
		bagConstraints.fill = GridBagConstraints.HORIZONTAL;
		bagConstraints.anchor = GridBagConstraints.NORTHWEST;
		bagConstraints.weightx = 1.0;
		purchaseMethodPanel.add(purchaseMethodButtonPanel, bagConstraints);

		statusBar.setText(LanguageBundle.getString("in_Prefs_setCost")); //$NON-NLS-1$
		statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
		bagConstraints = new GridBagConstraints();
		bagConstraints.gridx = 0;
		bagConstraints.gridy = 6;
		bagConstraints.fill = GridBagConstraints.HORIZONTAL;
		bagConstraints.ipadx = 1;
		bagConstraints.ipady = 1;
		bagConstraints.insets = new Insets(1, 1, 1, 1);
		bagConstraints.anchor = GridBagConstraints.NORTHWEST;
		bagConstraints.weightx = 1.0;
		getContentPane().add(statusBar, bagConstraints);

		OKCloseButtonBar buttonBar = new OKCloseButtonBar(
				evt -> okButtonActionPerformed(),
				evt -> cancelButtonActionPerformed()
		);

		Button resetButton = new Button();
		resetButton.setText(LanguageBundle.getString("in_Prefs_Reset"));
		resetButton.setOnAction(evt -> resetButtonActionPerformed());
		resetButton.setTooltip(new Tooltip(LanguageBundle.getString("in_Prefs_ResetTip")));
		ButtonBar.setButtonData(resetButton, ButtonBar.ButtonData.BACK_PREVIOUS);
		buttonBar.getButtons().add(resetButton);
		buttonBar.getOkButton().setTooltip(new Tooltip(LanguageBundle.getString("in_Prefs_OKTip")));
		okButton.addActionListener(evt -> okButtonActionPerformed());
		buttonBar.getCancelButton().setTooltip(new Tooltip(LanguageBundle.getString("in_Prefs_CancelTip")));

		/////////////////////////////////////////////////
		bagConstraints = new GridBagConstraints();
		bagConstraints.gridx = 0;
		bagConstraints.gridy = 5;
		bagConstraints.fill = GridBagConstraints.HORIZONTAL;
		bagConstraints.anchor = GridBagConstraints.EAST;
		bagConstraints.weightx = 1.0;
		getContentPane().add(GuiUtility.wrapParentAsJFXPanel(buttonBar), bagConstraints);

		jScrollPane1.setViewportBorder(new BevelBorder(BevelBorder.LOWERED));
		jScrollPane1.setPreferredSize(new Dimension(100, 200));

		purchaseModel = new PurchaseModel();
		renewAbilityScoreCostTable();

		bagConstraints = new GridBagConstraints();
		bagConstraints.gridx = 0;
		bagConstraints.gridy = 0;
		bagConstraints.fill = GridBagConstraints.BOTH;
		bagConstraints.anchor = GridBagConstraints.NORTHWEST;
		bagConstraints.weightx = 1.0;
		bagConstraints.weighty = 1.0;
		getContentPane().add(jScrollPane1, bagConstraints);

		pack();

		initializeCurrentPurchaseMethods();
	}

	private void initializeCurrentPurchaseMethods()
	{
		//
		// Set up the current methods combo's contents
		//
		Collection<PointBuyMethod> methods = SettingsHandler.getGameAsProperty().get().getModeContext().getReferenceContext()
			.getConstructedCDOMObjects(PointBuyMethod.class);
		if (!methods.isEmpty())
		{
			currentPurchaseMethods.setModel(new DefaultComboBoxModel<>(methods.toArray()));
		}
		currentPurchaseMethodsActionPerformed(); // Get into correct state
	}

	private void okButtonActionPerformed()
	{
		purchaseModel.keepNewValues();
		dispose();
	}

	private void purchaseScoreMaxDecreaseButtonActionPerformed()
	{
		int oldValue = purchaseModel.getPurchaseScoreMax();

		// get the current value from the edit field
		String valueString = purchaseScoreMaxEdit.getText();

		// convert it to an integer
		int value = PurchaseModeFrame.convertStringToInt(valueString);
		if (!validateNewMaxValue(value - 1))
		{
			return;
		}

		// decrease the value in the model
		statusBar.setText(""); //$NON-NLS-1$
		if (!purchaseModel.setPurchaseScoreMax(value - 1))
		{
			// set a status message
			statusBar.setText(LanguageBundle.getString("in_Prefs_maxBelowMin")); //$NON-NLS-1$
		}

		// ensure the edit value gets updated correctly
		updatePurchaseScoreMax(oldValue);
	}

	private void purchaseScoreMaxIncreaseButtonActionPerformed()
	{
		int oldValue = purchaseModel.getPurchaseScoreMax();

		// get the current value from the edit field
		String valueString = purchaseScoreMaxEdit.getText();

		// convert it to an integer
		int value = PurchaseModeFrame.convertStringToInt(valueString);
		if (!validateNewMaxValue(value + 1))
		{
			return;
		}

		// increase the value in the model
		statusBar.setText(""); //$NON-NLS-1$
		boolean updateOk = purchaseModel.setPurchaseScoreMax(value + 1);

		// ensure the edit value gets updated correctly
		updatePurchaseScoreMax(oldValue);

		if (updateOk)
		{
			purchaseModel.setValueAt(purchaseModel.predictNextPurchaseCostMax(), purchaseModel.getRowCount() - 1, 1);
		}
	}

	private void purchaseScoreMaxValueActionPerformed()
	{
		int oldValue = purchaseModel.getPurchaseScoreMax();

		// get the current value from the edit field
		String valueString = purchaseScoreMaxEdit.getText();

		// convert it to an integer
		int value = PurchaseModeFrame.convertStringToInt(valueString);
		if (!validateNewMaxValue(value))
		{
			return;
		}

		// increase the value in the model
		statusBar.setText(""); //$NON-NLS-1$
		boolean updateOk = purchaseModel.setPurchaseScoreMax(value);

		// ensure the edit value gets updated correctly
		updatePurchaseScoreMax(oldValue);

		if (updateOk)
		{
			purchaseModel.setValueAt(purchaseModel.predictNextPurchaseCostMax(), purchaseModel.getRowCount() - 1, 1);
		}
	}

	private boolean validateNewMaxValue(int value)
	{
		if (!Globals.checkRule(RuleConstants.ABILRANGE))
		{
			if (value > statMax)
			{
				statusBar.setText(LanguageBundle.getFormattedString("in_Prefs_mayNotInc", //$NON-NLS-1$
					statMax));

				return false;
			}
		}
		return true;
	}

	private void purchaseScoreMinDecreaseButtonActionPerformed()
	{
		int oldValue = purchaseModel.getPurchaseScoreMin();

		// get the current value from the edit field
		String valueString = purchaseScoreMinEdit.getText();

		// convert it to an integer
		int value = PurchaseModeFrame.convertStringToInt(valueString);
		if (!validateNewMinValue(value - 1))
		{
			return;
		}
		boolean updateOk = purchaseModel.setPurchaseScoreMin(value - 1);

		// decrease the value in the model
		if (updateOk)
		{
			statusBar.setText("");
		}
		else
		{
			// set a status message
			statusBar.setText(LanguageBundle.getString("in_Prefs_noMinBelow0")); //$NON-NLS-1$
		}

		// ensure the edit value gets updated correctly
		updatePurchaseScoreMin(oldValue);

		if (updateOk)
		{
			purchaseModel.setValueAt(purchaseModel.predictNextPurchaseCostMin(), 0, 1);
		}
	}

	private void purchaseScoreMinIncreaseButtonActionPerformed()
	{
		int oldValue = purchaseModel.getPurchaseScoreMin();

		// get the current value from the edit field
		String valueString = purchaseScoreMinEdit.getText();

		// convert it to an integer
		int value = PurchaseModeFrame.convertStringToInt(valueString);
		if (!validateNewMinValue(value + 1))
		{
			return;
		}

		// increase the value in the model
		statusBar.setText(""); //$NON-NLS-1$
		if (!purchaseModel.setPurchaseScoreMin(value + 1))
		{
			// TODO Disable buttons (then no need for those messages)
			// set a status message
			statusBar.setText(LanguageBundle.getString("in_Prefs_minExceedMax")); //$NON-NLS-1$
		}

		// ensure the edit value gets updated correctly
		updatePurchaseScoreMin(oldValue);
	}

	private void purchaseScoreMinValueActionPerformed()
	{
		int oldValue = purchaseModel.getPurchaseScoreMin();

		// get the current value from the edit field
		String valueString = purchaseScoreMinEdit.getText();

		// convert it to an integer
		int value = PurchaseModeFrame.convertStringToInt(valueString);
		if (!validateNewMinValue(value))
		{
			return;
		}

		// change the value in the model
		statusBar.setText(""); //$NON-NLS-1$
		if (!purchaseModel.setPurchaseScoreMin(value))
		{
			// set a status message
			statusBar.setText(LanguageBundle.getString("in_Prefs_minExceedMax")); //$NON-NLS-1$
		}

		// ensure the edit value gets updated correctly
		updatePurchaseScoreMin(oldValue);
	}

	private boolean validateNewMinValue(int value)
	{
		int unconditionalMin = Math.min(0, statMin);

		if (!Globals.checkRule(RuleConstants.ABILRANGE))
		{
			if (value < statMin)
			{
				statusBar.setText(LanguageBundle.getFormattedString("in_Prefs_mayNotDec", //$NON-NLS-1$
					statMin));

				return false;
			}
		}

		// bad value?
		if (value < unconditionalMin)
		{
			// set a status message
			statusBar.setText(LanguageBundle.getFormattedString("in_Prefs_mayNotDec", //$NON-NLS-1$
				unconditionalMin));
			return false;
		}
		return true;
	}

	/**
	 * Remove the current selection from the list of purchase methods.
	 */
	private void removeMethodButtonActionPerformed()
	{
		final PointBuyMethod method = (PointBuyMethod) currentPurchaseMethods.getSelectedItem();

		if (method != null)
		{
			currentPurchaseMethods.removeItem(method);
		}
	}

	private void renewAbilityScoreCostTable()
	{
		JTable abilityScoreCostTable = new JTable();

		abilityScoreCostTable.setBorder(new BevelBorder(BevelBorder.LOWERED));
		abilityScoreCostTable.setModel(purchaseModel);
		abilityScoreCostTable.setToolTipText(LanguageBundle.getString("in_Prefs_setCost")); //$NON-NLS-1$
		jScrollPane1.setViewportView(abilityScoreCostTable);
	}

	private void resetButtonActionPerformed()
	{
		//renewAbilityScoreCostTable();
		purchaseModel.copySavedToCurrent();
		updatePurchaseScoreMin(purchaseModel.getPurchaseScoreMin());
		updatePurchaseScoreMax(purchaseModel.getPurchaseScoreMax());
		purchaseModel.fireTableStructureChanged();

		initializeCurrentPurchaseMethods();
	}

	private void updatePurchaseScoreMax(int oldValue)
	{
		int score = purchaseModel.getPurchaseScoreMax();
		purchaseScoreMaxEdit.setText(Integer.toString(score));

		if (oldValue != score)
		{
			purchaseModel.appendRows(score - oldValue);
			purchaseModel.fireTableStructureChanged();
		}
	}

	private void updatePurchaseScoreMin(int oldValue)
	{
		int score = purchaseModel.getPurchaseScoreMin();
		purchaseScoreMinEdit.setText(Integer.toString(score));

		if (oldValue != score)
		{
			purchaseModel.prependRows(score - oldValue);
			purchaseModel.resetAllCosts();
			purchaseModel.fireTableStructureChanged();
		}
	}

	/**
	 * @param statMin The new lowest value a purchase mode can take a stat to.
	 */
	void setStatMin(int statMin)
	{
		this.statMin = statMin;
	}

	/**
	 * @param statMax The new highest value a purchase mode can take a stat to.
	 */
	void setStatMax(int statMax)
	{
		this.statMax = statMax;
	}

	private final class PurchaseModel extends AbstractTableModel
	{
		private static final long serialVersionUID = 8257526994109828957L;
		private final boolean[] canEdit = {false, true};
		private final String[] columnHeaders = {LanguageBundle.getString("in_Prefs_abScore"), //$NON-NLS-1$
			LanguageBundle.getString("in_Prefs_cost")}; //$NON-NLS-1$
		private Object[][] currentValues;
		private Object[][] savedValues;
		private final Class<?>[] types = new Class[]{Integer.class, Integer.class};
		private int currentPurchaseScoreMax = 10;
		private int currentPurchaseScoreMin = 10; // Start at the average stat
		private int savedPurchaseScoreMax = 0;
		private int savedPurchaseScoreMin = 0;

		private PurchaseModel()
		{

			// Initialize the saved values
			initValues();

			// copy the saved values to the current values
			copySavedToCurrent();
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return canEdit[columnIndex];
		}

		@Override
		public Class<?> getColumnClass(int columnIndex)
		{
			return types[columnIndex];
		}

		@Override
		public int getColumnCount()
		{
			return columnHeaders.length;
		}

		@Override
		public String getColumnName(int param)
		{
			return columnHeaders[param];
		}

		@Override
		public int getRowCount()
		{
			return currentValues.length;
		}

		@Override
		public void setValueAt(Object obj, int row, int column)
		{
			if ((row < 0) || (row >= currentValues.length))
			{
				throw new ArrayIndexOutOfBoundsException(
					LanguageBundle.getFormattedString("in_Prefs_rowOutBound", row)); //$NON-NLS-1$
			}

			if ((column == 0) || (column == 1))
			{
				currentValues[row][column] = obj;
				fireTableCellUpdated(row, column);
			}
			else
			{
				throw new ArrayIndexOutOfBoundsException(
					LanguageBundle.getFormattedString("in_Prefs_columnOutBound", column)); //$NON-NLS-1$
			}
		}

		@Override
		public Object getValueAt(int row, int column)
		{
			if ((row < 0) || (row >= currentValues.length))
			{
				throw new ArrayIndexOutOfBoundsException(
					LanguageBundle.getFormattedString("in_Prefs_rowOutBound", row)); //$NON-NLS-1$
			}

			if ((column == 0) || (column == 1))
			{
				return currentValues[row][column];
			}
			throw new ArrayIndexOutOfBoundsException(
				LanguageBundle.getFormattedString("in_Prefs_columnOutBound", column)); //$NON-NLS-1$
		}

		/**
		 * Copy the saved purchase mode to the current one
		 */
		private void copySavedToCurrent()
		{
			if (savedValues != null)
			{
				currentPurchaseScoreMin = savedPurchaseScoreMin;
				currentPurchaseScoreMax = savedPurchaseScoreMax;

				final int nrEntries = (currentPurchaseScoreMax - currentPurchaseScoreMin) + 1;

				currentValues = new Object[nrEntries][2];

				for (int i = 0; i < nrEntries; ++i)
				{
					currentValues[i][0] = savedValues[i][0];
					currentValues[i][1] = savedValues[i][1];
				}
			}
		}

		/**
		 * Initialise the values
		 */
		private void initValues()
		{
			// get the ability score costs from settings
			int[] scoreCosts = SettingsHandler.getGameAsProperty().get().getAbilityScoreCost();

			if (scoreCosts != null)
			{
				// get the save values from the settings
				savedPurchaseScoreMin = SettingsHandler.getGameAsProperty().get().getPurchaseScoreMin();
				savedPurchaseScoreMax = SettingsHandler.getGameAsProperty().get().getPurchaseScoreMax();

				savedValues = new Object[scoreCosts.length][2];

				for (int i = savedPurchaseScoreMin; i <= savedPurchaseScoreMax; ++i)
				{
					int index = i - savedPurchaseScoreMin;
					savedValues[index][0] = i;
					savedValues[index][1] = scoreCosts[index];
				}
			}
			else
			{
				savedPurchaseScoreMin = 10;
				savedPurchaseScoreMax = 10;

				savedValues = new Object[1][2];
				savedValues[0][0] = 10;
				savedValues[0][1] = 0;
			}

			//
			// Make sure the min/max buttons have the correct info
			//
			purchaseScoreMinEdit.setText(Integer.toString(savedPurchaseScoreMin));
			purchaseScoreMaxEdit.setText(Integer.toString(savedPurchaseScoreMax));
		}

		/** Scale rises in the maximum purchase cost <strong>after</strong> a new, empty cost row has been added.
		 * @return int
		 */
		private int predictNextPurchaseCostMax()
		{
			int maxIndex = getRowCount() - 2; // have already added the new row
			int max = (Integer) getValueAt(maxIndex, 1);

			if (getRowCount() == 2) // initial and one empty
			{
				return max + 1;
			}

			int penultimate = (Integer) getValueAt(maxIndex - 1, 1);

			return max + (max - penultimate);
		}

		/** Scale drops in the minimum purchase cost <strong>after</strong> a new, empty cost row has been added.
		 * @return int
		 */
		private int predictNextPurchaseCostMin()
		{
			int minIndex = 1; // Have already added the new row
			int min = (Integer) getValueAt(minIndex, 1);

			if (getRowCount() == 2) // initial and one empty
			{
				return min - 1;
			}

			int penultimate = (Integer) getValueAt(minIndex + 1, 1);

			return min - (penultimate - min);
		}

		/** Setter for property purchaseScoreMax.
		 * @param purchaseScoreMax New value of property purchaseScoreMax.
		 * @return true or false
		 */
		private boolean setPurchaseScoreMax(int purchaseScoreMax)
		{
			if ((purchaseScoreMax >= 0) && (purchaseScoreMax >= currentPurchaseScoreMin))
			{
				currentPurchaseScoreMax = purchaseScoreMax;

				return true;
			}

			return false;
		}

		/** Getter for property purchaseScoreMax.
		 * @return Value of property purchaseScoreMax.
		 */
		private int getPurchaseScoreMax()
		{
			return currentPurchaseScoreMax;
		}

		/** Setter for property purchaseScoreMin.
		 * @param purchaseScoreMin New value of property purchaseScoreMin.
		 * @return true or false
		 */
		private boolean setPurchaseScoreMin(int purchaseScoreMin)
		{
			if (purchaseScoreMin <= currentPurchaseScoreMax)
			{
				currentPurchaseScoreMin = purchaseScoreMin;

				return true;
			}

			return false;
		}

		/** Getter for property purchaseScoreMin.
		 * @return Value of property purchaseScoreMin.
		 */
		private int getPurchaseScoreMin()
		{
			return currentPurchaseScoreMin;
		}

		private void appendRows(int nrRows)
		{
			final int nrEntries = (currentPurchaseScoreMax - currentPurchaseScoreMin) + 1;

			final int preLength = currentValues.length;
			Object[][] newValues = Arrays.copyOf(currentValues, nrEntries);

			// Only happens if adding rows
			for (int i = 0; i < nrRows; ++i)
			{
				final int score = ((i + currentPurchaseScoreMax) - nrRows) + 1;
				newValues[i + preLength][0] = score;

				int preVal = -1;
				if ((i + preLength) != 0)
				{
					preVal = (Integer) newValues[(i + preLength) - 1][1];
				}

				newValues[i + preLength][1] = preVal + 1;
			}

			currentValues = newValues;
		}

		@SuppressWarnings("PMD.OneDeclarationPerLine")
		private void keepNewValues()
		{
			// set the current values into the settings
			SettingsHandler.getGameAsProperty().get().clearPointBuyStatCosts();

			for (int i = currentPurchaseScoreMin; i <= currentPurchaseScoreMax; ++i)
			{
				PointBuyCost pbc = new PointBuyCost();
				pbc.setName(Integer.toString(i));
				pbc.setBuyCost((Integer) currentValues[i - currentPurchaseScoreMin][1]);
				SettingsHandler.getGameAsProperty().get().addPointBuyStatCost(pbc);
			}

			AbstractReferenceContext ref = SettingsHandler.getGameAsProperty().get().getModeContext().getReferenceContext();
			Collection<PointBuyMethod> methods = new ArrayList<>(ref.getConstructedCDOMObjects(PointBuyMethod.class));
			for (int i = 0, x = currentPurchaseMethods.getItemCount(); i < x; ++i)
			{
				final PointBuyMethod pbm = (PointBuyMethod)currentPurchaseMethods.getItemAt(i);
				PointBuyMethod masterPBM = ref.silentlyGetConstructedCDOMObject(PointBuyMethod.class, pbm.getKeyName());
				if (masterPBM == null)
				{
					ref.importObject(pbm);
				}
				else
				{
					methods.remove(masterPBM);
					masterPBM.setPointFormula(pbm.getPointFormula());
				}
			}
			for (final PointBuyMethod pbm : methods)
			{
				ref.forget(pbm);
			}
		}

		private void prependRows(int nrRows)
		{
			final int nrEntries = (currentPurchaseScoreMax - currentPurchaseScoreMin) + 1;

			Object[][] newValues = new Object[nrEntries][2];

			if (nrRows > 0)
			{
				// removing rows
				System.arraycopy(currentValues, nrRows, newValues, 0, nrEntries);
			}
			else
			{
				// adding rows
				nrRows = Math.abs(nrRows);
				System.arraycopy(currentValues, 0, newValues, nrRows, currentValues.length);

				for (int i = 0; i < nrRows; ++i)
				{
					final int score = i + currentPurchaseScoreMin;
					newValues[i][0] = score;
				}
			}

			currentValues = newValues;
		}

		/**
		 * Reset the cost of all rows, starting from 0 for the lowest.
		 */
		private void resetAllCosts()
		{
			int cost = 0;
			for (int i = 0; i < currentValues.length; i++)
			{
				currentValues[i][1] = cost;
				cost++;
			}
		}
	}
}
