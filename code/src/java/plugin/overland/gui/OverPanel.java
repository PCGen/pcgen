/*
 * NameGenPanel.java
 *
 * Created on April 24, 2003, 1:03 PM
 */
package plugin.overland.gui;

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import pcgen.util.Logging;
import plugin.overland.util.RBCost;
import plugin.overland.util.RBCosts;
import plugin.overland.util.TravelMethod;
import plugin.overland.util.TravelMethods;

import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ListIterator;

/**
 *
 * @author  Juliean Galak
 */
public class OverPanel extends javax.swing.JPanel
{
	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton butToDist;
	private javax.swing.JButton butToMap;
	private javax.swing.JButton butToReal;
	private javax.swing.JButton butToTime;
	private javax.swing.JComboBox cmbAnimal;
	private javax.swing.JComboBox cmbFood;
	private javax.swing.JComboBox cmbInn;
	private javax.swing.JComboBox cmbMethod;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel10;
	private javax.swing.JLabel jLabel11;
	private javax.swing.JLabel jLabel12;
	private javax.swing.JLabel jLabel13;
	private javax.swing.JLabel jLabel14;
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
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JLabel jLabel8;
	private javax.swing.JLabel jLabel9;
	private javax.swing.JLabel lblDebug;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JPanel jPanel5;
	private javax.swing.JPanel jPanel6;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JTextField lblSpeed;
	private javax.swing.JTextField textMap;
	private javax.swing.JTextField textReal;
	private javax.swing.JTextField textScale;
	private javax.swing.JTextField txtAnim;
	private javax.swing.JTextField txtDayAnimal;
	private javax.swing.JTextField txtDayFood;
	private javax.swing.JTextField txtDayInn;
	private javax.swing.JTextField txtDayTotal;
	private javax.swing.JTextField txtDays;
	private javax.swing.JTextField txtDist;
	private javax.swing.JTextField txtPeop;
	private javax.swing.JTextField txtTime;
	private javax.swing.JTextField txtTotal;
	private javax.swing.JTextField txtWeekAnimal;
	private javax.swing.JTextField txtWeekFood;
	private javax.swing.JTextField txtWeekInn;
	private javax.swing.JTextField txtWeekTotal;
	private NumberFormat gp = NumberFormat.getNumberInstance();
	private NumberFormat nf = NumberFormat.getNumberInstance();
	private RBCosts animals; //holds animal costs
	private RBCosts foods; //holds inn costs
	private RBCosts inns; //holds inn costs

	// End of variables declaration//GEN-END:variables

	/* public Preferences namePrefs = Preferences.userNodeForPackage(NameGenPanel.class);
	   private HashMap categories = new HashMap();
	   private VariableHashMap allVars = new VariableHashMap();
	 */
	private TravelMethods tms; //holds the travel methods list
	private boolean StupidKludge = true; /* This is a stupid kludge!
	 * the room & board combo boxes fire events when changed,
	 * and these events trigger updateTopUI
	 * Unfortunately, when I first populate the
	 * comboboxes, the same event gets triggeresd, meessing
	 * things up.  Since I can't find a way to differentiate
	 * Between a "changed selection" event and a
	 * "adding things to the combo box" event, I
	 * have to use this. */

	/** Creates new form NameGenPanel
	 * @param DataDir
	 */
	public OverPanel(String DataDir)
	{
		initComponents();
		loadData(DataDir);
		initData();
	}

	/**
	 *  Initialization of the bulk of preferences.  sets the defaults
	 *  if this is the first time you have used this version
	 *  @deprecated Code here is commented out and therefore not run
	 *  to be removed in 5.9.6
	 */
	public void initPrefs()
	{
/*        boolean prefsSet = namePrefs.getBoolean("arePrefsSet", false);
   if (!prefsSet) {
       namePrefs.putBoolean("arePrefsSet", true);
   }
   double version = namePrefs.getDouble("Version", 0);
   if (version < 0.5 || !prefsSet) {
       namePrefs.putDouble("Version", 0.5);
   }
   namePrefs.putDouble("SubVersion", 0);
 */
	}

	private void butToDistActionPerformed(java.awt.event.ActionEvent evt)
	{ //GEN-FIRST:event_butToDistActionPerformed

		int i;
		i = cmbMethod.getSelectedIndex();

		try
		{
			float time = nf.parse(txtTime.getText()).floatValue();
			int speed = tms.getMethodAtI(i).getSpeed();
			float result = 0;
			result = time * speed;
			txtDist.setText(nf.format(result));
		}
		catch (NumberFormatException e1)
		{
			JOptionPane.showMessageDialog(null, "Invalid number format, try again.");
		}
		catch (ParseException e1)
		{
			JOptionPane.showMessageDialog(null, "Invalid number format, try again.");
		}
	}
	 //GEN-LAST:event_butToDistActionPerformed

	private void butToMapActionPerformed(java.awt.event.ActionEvent evt)
	{ //GEN-FIRST:event_butToMapActionPerformed

		//Converts from real units to map units
		try
		{
			float scale = nf.parse(textScale.getText()).floatValue();
			float realUnits = nf.parse(textReal.getText()).floatValue();
			float result = 0;
			result = realUnits / scale;
			textMap.setText(nf.format(result));
		}
		catch (NumberFormatException e1)
		{
			JOptionPane.showMessageDialog(null, "Invalid number format, try again.");
		}
		catch (ParseException e1)
		{
			JOptionPane.showMessageDialog(null, "Invalid number format, try again.");
		}
	}
	 //GEN-LAST:event_butToMapActionPerformed

	private void butToRealActionPerformed(java.awt.event.ActionEvent evt) //GEN-FIRST:event_butToRealActionPerformed
	{ //GEN-HEADEREND:event_butToRealActionPerformed

		//Converts from map units to real units
		try
		{
			float scale = nf.parse(textScale.getText()).floatValue();
			float mapUnits = nf.parse(textMap.getText()).floatValue();
			float result = 0;
			result = scale * mapUnits;
			textReal.setText(nf.format(result));
		}
		catch (NumberFormatException e1)
		{
			JOptionPane.showMessageDialog(null, "Invalid number format, try again.");
		}
		catch (ParseException e1)
		{
			JOptionPane.showMessageDialog(null, "Invalid number format, try again.");
		}
	}
	 //GEN-LAST:event_butToRealActionPerformed

	private void butToTimeActionPerformed(java.awt.event.ActionEvent evt)
	{ //GEN-FIRST:event_butToTimeActionPerformed

		int i;
		i = cmbMethod.getSelectedIndex();

		try
		{
			float dist = nf.parse(txtDist.getText()).floatValue();
			int speed = tms.getMethodAtI(i).getSpeed();
			float result = 0;
			result = dist / speed;
			txtTime.setText(nf.format(result));
		}
		catch (NumberFormatException e1)
		{
			JOptionPane.showMessageDialog(null, "Invalid number format, try again.");
		}
		catch (ParseException e1)
		{
			JOptionPane.showMessageDialog(null, "Invalid number format, try again.");
		}
	}
	 //GEN-LAST:event_butToTimeActionPerformed

	private void cmbMethodActionPerformed(java.awt.event.ActionEvent evt)
	{ //GEN-FIRST:event_cmbMethodActionPerformed

		// This is for debugging purposes only
		int speed;
		int i;
		i = cmbMethod.getSelectedIndex();
		speed = tms.getMethodAtI(i).getSpeed();
		lblSpeed.setText(Integer.toString(speed));
	}
	 //GEN-LAST:event_cmbMethodActionPerformed

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	private void initComponents() //GEN-BEGIN:initComponents
	{
		java.awt.GridBagConstraints gridBagConstraints;

		jPanel5 = new javax.swing.JPanel();
		jPanel1 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		textScale = new javax.swing.JTextField();
		jLabel4 = new javax.swing.JLabel();
		jLabel5 = new javax.swing.JLabel();
		jLabel6 = new javax.swing.JLabel();
		textReal = new javax.swing.JTextField();
		textMap = new javax.swing.JTextField();
		jPanel2 = new javax.swing.JPanel();
		butToMap = new javax.swing.JButton();
		butToReal = new javax.swing.JButton();
		jLabel7 = new javax.swing.JLabel();
		jPanel3 = new javax.swing.JPanel();
		jLabel8 = new javax.swing.JLabel();
		jLabel9 = new javax.swing.JLabel();
		cmbMethod = new javax.swing.JComboBox();
		jLabel10 = new javax.swing.JLabel();
		txtDist = new javax.swing.JTextField();
		jLabel11 = new javax.swing.JLabel();
		txtTime = new javax.swing.JTextField();
		jPanel4 = new javax.swing.JPanel();
		butToTime = new javax.swing.JButton();
		butToDist = new javax.swing.JButton();
		jLabel12 = new javax.swing.JLabel();
		jLabel13 = new javax.swing.JLabel();
		lblSpeed = new javax.swing.JTextField();
		jPanel6 = new javax.swing.JPanel();
		jLabel14 = new javax.swing.JLabel();
		jLabel15 = new javax.swing.JLabel();
		jLabel16 = new javax.swing.JLabel();
		jLabel17 = new javax.swing.JLabel();
		jLabel18 = new javax.swing.JLabel();
		jLabel20 = new javax.swing.JLabel();
		jLabel21 = new javax.swing.JLabel();
		jLabel22 = new javax.swing.JLabel();
		txtDayFood = new javax.swing.JTextField();
		txtDayInn = new javax.swing.JTextField();
		txtDayAnimal = new javax.swing.JTextField();
		txtWeekFood = new javax.swing.JTextField();
		txtWeekInn = new javax.swing.JTextField();
		txtWeekAnimal = new javax.swing.JTextField();
		txtDays = new javax.swing.JTextField();
		txtTotal = new javax.swing.JTextField();
		cmbFood = new javax.swing.JComboBox();
		cmbInn = new javax.swing.JComboBox();
		cmbAnimal = new javax.swing.JComboBox();
		jSeparator1 = new javax.swing.JSeparator();
		jLabel23 = new javax.swing.JLabel();
		jLabel24 = new javax.swing.JLabel();
		txtPeop = new javax.swing.JTextField();
		txtAnim = new javax.swing.JTextField();
		txtDayTotal = new javax.swing.JTextField();
		txtWeekTotal = new javax.swing.JTextField();
		jLabel25 = new javax.swing.JLabel();
		lblDebug = new javax.swing.JLabel();

		setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

		jPanel5.setLayout(new java.awt.GridBagLayout());

		jPanel1.setLayout(new java.awt.GridBagLayout());

		jPanel1.setBorder(new javax.swing.border.EtchedBorder());
		jLabel1.setFont(new java.awt.Font("Dialog", 1, 18));
		jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jLabel1.setText("Scale Converter");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
		jPanel1.add(jLabel1, gridBagConstraints);

		jLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
		jLabel2.setText("Real Units");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 3);
		jPanel1.add(jLabel2, gridBagConstraints);

		jLabel3.setText("Real Units");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 3);
		jPanel1.add(jLabel3, gridBagConstraints);

		textScale.setHorizontalAlignment(SwingConstants.CENTER);
		textScale.setText("1");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(0, 3, 3, 3);
		jPanel1.add(textScale, gridBagConstraints);

		jLabel4.setFont(new java.awt.Font("Dialog", 0, 12));
		jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		jLabel4.setText("1 Map Unit =");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 0);
		jPanel1.add(jLabel4, gridBagConstraints);

		jLabel5.setText("Map Units");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 3);
		jPanel1.add(jLabel5, gridBagConstraints);

		jLabel6.setText("Scale:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
		jPanel1.add(jLabel6, gridBagConstraints);

		textReal.setHorizontalAlignment(SwingConstants.CENTER);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
		gridBagConstraints.insets = new java.awt.Insets(0, 3, 3, 3);
		jPanel1.add(textReal, gridBagConstraints);

		textMap.setHorizontalAlignment(SwingConstants.CENTER);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
		gridBagConstraints.insets = new java.awt.Insets(0, 3, 3, 3);
		jPanel1.add(textMap, gridBagConstraints);

		jPanel2.setLayout(new java.awt.GridBagLayout());

		butToMap.setText("<-");
		butToMap.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					butToMapActionPerformed(evt);
				}
			});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		jPanel2.add(butToMap, gridBagConstraints);

		butToReal.setText("->");
		butToReal.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					butToRealActionPerformed(evt);
				}
			});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		jPanel2.add(butToReal, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		jPanel1.add(jPanel2, gridBagConstraints);

		jLabel7.setText("                   ");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 3);
		jPanel1.add(jLabel7, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
		jPanel5.add(jPanel1, gridBagConstraints);

		jPanel3.setLayout(new java.awt.GridBagLayout());

		jPanel3.setBorder(new javax.swing.border.EtchedBorder());
		jLabel8.setFont(new java.awt.Font("Dialog", 1, 18));
		jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jLabel8.setText("Travel Time");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 5;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(3, 3, 5, 3);
		jPanel3.add(jLabel8, gridBagConstraints);

		jLabel9.setText("Method");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new java.awt.Insets(0, 3, 4, 3);
		jPanel3.add(jLabel9, gridBagConstraints);

		cmbMethod.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					cmbMethodActionPerformed(evt);
				}
			});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
		jPanel3.add(cmbMethod, gridBagConstraints);

		jLabel10.setText("Dist (Miles)");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
		jPanel3.add(jLabel10, gridBagConstraints);

		txtDist.setHorizontalAlignment(SwingConstants.CENTER);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
		gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
		jPanel3.add(txtDist, gridBagConstraints);

		jLabel11.setText("Time (Days)");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
		jPanel3.add(jLabel11, gridBagConstraints);

		txtTime.setHorizontalAlignment(SwingConstants.CENTER);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
		gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
		jPanel3.add(txtTime, gridBagConstraints);

		jPanel4.setLayout(new java.awt.GridBagLayout());

		butToTime.setText("->");
		butToTime.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					butToTimeActionPerformed(evt);
				}
			});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
		jPanel4.add(butToTime, gridBagConstraints);

		butToDist.setText("<-");
		butToDist.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					butToDistActionPerformed(evt);
				}
			});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
		jPanel4.add(butToDist, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		jPanel3.add(jPanel4, gridBagConstraints);

		jLabel12.setText("                   ");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 3);
		jPanel3.add(jLabel12, gridBagConstraints);

		jLabel13.setFont(new java.awt.Font("Dialog", 0, 12));
		jLabel13.setText("mpd");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 4;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 3);
		jPanel3.add(jLabel13, gridBagConstraints);

		lblSpeed.setEditable(false);
		lblSpeed.setHorizontalAlignment(SwingConstants.CENTER);
		lblSpeed.setText("         ");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.ipadx = 2;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 4, 2);
		jPanel3.add(lblSpeed, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		jPanel5.add(jPanel3, gridBagConstraints);

		jPanel6.setLayout(new java.awt.GridBagLayout());

		jPanel6.setBorder(new javax.swing.border.EtchedBorder());
		jLabel14.setFont(new java.awt.Font("Dialog", 1, 18));
		jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jLabel14.setText("Room and Board");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(3, 3, 5, 3);
		jPanel6.add(jLabel14, gridBagConstraints);

		jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jLabel15.setText("  Per Day  ");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 3;
		jPanel6.add(jLabel15, gridBagConstraints);

		jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		jLabel16.setText("Food");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 2);
		jPanel6.add(jLabel16, gridBagConstraints);

		jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jLabel17.setText(" Per Week ");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 3;
		jPanel6.add(jLabel17, gridBagConstraints);

		jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		jLabel18.setText("Inn");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 2);
		jPanel6.add(jLabel18, gridBagConstraints);

		jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		jLabel20.setText("Animals ");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 2);
		jPanel6.add(jLabel20, gridBagConstraints);

		jLabel21.setText("Days:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 10;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
		jPanel6.add(jLabel21, gridBagConstraints);

		jLabel22.setText("Total");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 11;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
		jPanel6.add(jLabel22, gridBagConstraints);

		txtDayFood.setHorizontalAlignment(SwingConstants.CENTER);
		txtDayFood.setText("0");
		txtDayFood.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					txtDayFoodActionPerformed(evt);
				}
			});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		jPanel6.add(txtDayFood, gridBagConstraints);

		txtDayInn.setHorizontalAlignment(SwingConstants.CENTER);
		txtDayInn.setText("0");
		txtDayInn.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					txtDayFoodActionPerformed(evt);
				}
			});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		jPanel6.add(txtDayInn, gridBagConstraints);

		txtDayAnimal.setHorizontalAlignment(SwingConstants.CENTER);
		txtDayAnimal.setText("0");
		txtDayAnimal.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					txtDayFoodActionPerformed(evt);
				}
			});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		jPanel6.add(txtDayAnimal, gridBagConstraints);

		txtWeekFood.setEditable(false);
		txtWeekFood.setHorizontalAlignment(SwingConstants.CENTER);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		jPanel6.add(txtWeekFood, gridBagConstraints);

		txtWeekInn.setEditable(false);
		txtWeekInn.setHorizontalAlignment(SwingConstants.CENTER);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		jPanel6.add(txtWeekInn, gridBagConstraints);

		txtWeekAnimal.setEditable(false);
		txtWeekAnimal.setHorizontalAlignment(SwingConstants.CENTER);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		jPanel6.add(txtWeekAnimal, gridBagConstraints);

		txtDays.setHorizontalAlignment(SwingConstants.CENTER);
		txtDays.setText("1");
		txtDays.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					txtDaysActionPerformed(evt);
				}
			});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 10;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		jPanel6.add(txtDays, gridBagConstraints);

		txtTotal.setEditable(false);
		txtTotal.setHorizontalAlignment(SwingConstants.CENTER);
		txtTotal.setText("1");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 11;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		jPanel6.add(txtTotal, gridBagConstraints);

		cmbFood.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					txtPeopActionPerformed(evt);
				}
			});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		jPanel6.add(cmbFood, gridBagConstraints);

		cmbInn.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					txtPeopActionPerformed(evt);
				}
			});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		jPanel6.add(cmbInn, gridBagConstraints);

		cmbAnimal.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					txtPeopActionPerformed(evt);
				}
			});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		jPanel6.add(cmbAnimal, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 9;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
		jPanel6.add(jSeparator1, gridBagConstraints);

		jLabel23.setText("People:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 2);
		jPanel6.add(jLabel23, gridBagConstraints);

		jLabel24.setText("Animals:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 2);
		jPanel6.add(jLabel24, gridBagConstraints);

		txtPeop.setHorizontalAlignment(SwingConstants.CENTER);
		txtPeop.setText("1");
		txtPeop.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					txtPeopActionPerformed(evt);
				}
			});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		jPanel6.add(txtPeop, gridBagConstraints);

		txtAnim.setHorizontalAlignment(SwingConstants.CENTER);
		txtAnim.setText("1");
		txtAnim.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					txtPeopActionPerformed(evt);
				}
			});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		jPanel6.add(txtAnim, gridBagConstraints);

		txtDayTotal.setHorizontalAlignment(SwingConstants.CENTER);
		txtDayTotal.setText("0");
		txtDayTotal.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					txtDaysActionPerformed(evt);
				}
			});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 8;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
		jPanel6.add(txtDayTotal, gridBagConstraints);

		txtWeekTotal.setEditable(false);
		txtWeekTotal.setHorizontalAlignment(SwingConstants.CENTER);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 8;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
		jPanel6.add(txtWeekTotal, gridBagConstraints);

		jLabel25.setText("Totals");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 8;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 2);
		jPanel6.add(jLabel25, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		jPanel5.add(jPanel6, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		jPanel5.add(lblDebug, gridBagConstraints);

		add(jPanel5);
	}
	 //GEN-END:initComponents

	private void initData()
	{
		StupidKludge = false; //prevent updating of ui for now

		nf.setMaximumFractionDigits(2); //This will display other numbers
		gp.setMaximumFractionDigits(2); //This will correctly display currency

		for (int i = 0; i < tms.getCount(); i++)
		{
			cmbMethod.addItem(tms.getMethodAtI(i).getName());
		}

		//End travel methods setup
		//Begin costs setup
		//the data is loaded into the data structures, now just load the combo boxes
		for (int i = 0; i < inns.getCount(); i++)
		{
			cmbInn.addItem(inns.getRBCostAtI(i).getName());
		}

		for (int i = 0; i < foods.getCount(); i++)
		{
			cmbFood.addItem(foods.getRBCostAtI(i).getName());
		}

		for (int i = 0; i < animals.getCount(); i++)
		{
			cmbAnimal.addItem(animals.getRBCostAtI(i).getName());
		}

		//End costs setup
		StupidKludge = true; //reenable updating
		updateTopUI(); //force an update.
	}

	private void loadData(String aDataDir)
	{
		//Populate Travel Methods
		loadTM(aDataDir + "/travel_methods");

		//Populate Room and Board
		loadRB(aDataDir + "/rnbprice");
	}

	private void loadRB(String DataPath)
	{
		//Create a new list for the room and board
		inns = new RBCosts();
		foods = new RBCosts();
		animals = new RBCosts();

		File path = new File(DataPath);

		if (path.isDirectory())
		{
			File[] dataFiles = path.listFiles(new XMLFilter());
			SAXBuilder builder = new SAXBuilder();

			for (int i = 0; i < dataFiles.length; i++)
			{
				try
				{
					Document methodSet = builder.build(dataFiles[i]);
					DocType dt = methodSet.getDocType();

					if (dt.getElementName().equals("RNBPRICE"))
					{
						//Do work here
						loadRBData(methodSet);
					}

					methodSet = null;
					dt = null;
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(this, "XML Error with file " + dataFiles[i].getName());
					Logging.errorPrint(e.getMessage(), e);
				}
			}
		}
		else
		{
			JOptionPane.showMessageDialog(this, "No data files in directory " + path.getPath());
		}
	}

	private void loadRBData(Document methodSet)
	{
		Element table = methodSet.getRootElement();
		java.util.List methods = table.getChildren("item");
		ListIterator listIterator = methods.listIterator();

		String type;
		String name;
		String priceS;
		float priceF = 999; //999 is the debugging value

		while (listIterator.hasNext())
		{
			Element method = (Element) listIterator.next();

			if (method.getName().equals("item"))
			{
				type = method.getChild("type").getTextTrim();
				name = method.getChild("name").getTextTrim();
				priceS = method.getChild("speed").getTextTrim();

				try
				{
					priceF = nf.parse(priceS).floatValue();
				}
				catch (NumberFormatException e1)
				{
					JOptionPane.showMessageDialog(null, "Invalid number formatin XML File");
				}
				catch (ParseException e1)
				{
					JOptionPane.showMessageDialog(null, "Invalid number formatin XML File");
				}

				/*These if-else statements are OK for now.  Eventually, I would
				 *like to make it so that if new types are present in the data
				 *file, the system will automatically add new drop-down boxes.
				 *That, however, is a long-term project.
				 */
				if (type.equals("Inn"))
				{
					inns.addRBCost(new RBCost(name, priceF));
				}
				else if (type.equals("Food"))
				{
					foods.addRBCost(new RBCost(name, priceF));
				}
				else if (type.equals("Animal"))
				{
					animals.addRBCost(new RBCost(name, priceF));
				}
			}
		}
	}

	private void loadTM(String DataPath)
	{
		//Create a new list for the travel methods
		tms = new TravelMethods();

		File path = new File(DataPath);

		if (path.isDirectory())
		{
			File[] dataFiles = path.listFiles(new XMLFilter());
			SAXBuilder builder = new SAXBuilder();

			for (int i = 0; i < dataFiles.length; i++)
			{
				try
				{
					Document methodSet = builder.build(dataFiles[i]);
					DocType dt = methodSet.getDocType();

					if (dt.getElementName().equals("TRMETHOD"))
					{
						//Do work here
						loadTMData(methodSet);
					}

					methodSet = null;
					dt = null;
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(this, "XML Error with file " + dataFiles[i].getName());
					Logging.errorPrint(e.getMessage(), e);
				}
			}
		}
		else
		{
			JOptionPane.showMessageDialog(this, "No data files in directory " + path.getPath());
		}
	}

	private void loadTMData(Document methodSet)
	{
		Element table = methodSet.getRootElement();
		java.util.List methods = table.getChildren("method");
		ListIterator listIterator = methods.listIterator();

		String name;
		String speedS;
		int speedI = 999; //999 is the debugging value

		while (listIterator.hasNext())
		{
			Element method = (Element) listIterator.next();

			if (method.getName().equals("method"))
			{
				name = method.getChild("name").getTextTrim();
				speedS = method.getChild("speed").getTextTrim();

				try
				{
					speedI = nf.parse(speedS).intValue();
				}
				catch (NumberFormatException e1)
				{
					JOptionPane.showMessageDialog(null, "Invalid number formatin XML File");
				}
				catch (ParseException e1)
				{
					JOptionPane.showMessageDialog(null, "Invalid number formatin XML File");
				}

				tms.addTravelMethod(new TravelMethod(name, speedI));
			}
		}
	}

	private void txtDayFoodActionPerformed(java.awt.event.ActionEvent evt)
	{ //GEN-FIRST:event_txtDayFoodActionPerformed
		updateMidUI();
	}
	 //GEN-LAST:event_txtDayFoodActionPerformed

	private void txtDaysActionPerformed(java.awt.event.ActionEvent evt)
	{ //GEN-FIRST:event_txtDaysActionPerformed
		updateBottomUI();
	}
	 //GEN-LAST:event_txtDaysActionPerformed

	private void txtPeopActionPerformed(java.awt.event.ActionEvent evt)
	{ //GEN-FIRST:event_txtPeopActionPerformed

		/*    int i,j;
		   String er;
		   er=evt.getActionCommand();
		   i=evt.getID();
		   j=15;
		 */
		if (StupidKludge)
		{
			updateTopUI();
		}
	}
	 //GEN-LAST:event_txtPeopActionPerformed

	/** This method updates the Bottom portions of the UI based on changes in the total cost
	 *  and number of days.  It sets the value into the total box
	 *
	 */
	private void updateBottomUI()
	{
		try
		{
			float DayTotal = gp.parse(txtDayTotal.getText()).floatValue();
			float Days = gp.parse(txtDays.getText()).floatValue();
			float result = DayTotal * Days;

			txtTotal.setText(gp.format(result));
		}
		catch (NumberFormatException e1)
		{
			JOptionPane.showMessageDialog(null, "Invalid number format, try again.");
		}
		catch (ParseException e1)
		{
			JOptionPane.showMessageDialog(null, "Invalid number format, try again.");
		}
	}

	/** This method updates the middle portions of the UI based on changes in the daily costs
	 *  It sets the value into the daily total boxes
	 *
	 */
	private void updateMidUI()
	{
		try
		{
			float DayInn = gp.parse(txtDayInn.getText()).floatValue();
			float DayFood = gp.parse(txtDayFood.getText()).floatValue();
			float DayAnimal = gp.parse(txtDayAnimal.getText()).floatValue();
			float result = DayInn + DayFood + DayAnimal;

			txtDayTotal.setText(gp.format(result));
			result *= 7; //Compute weekly
			txtWeekTotal.setText(gp.format(result));
		}
		catch (NumberFormatException e1)
		{
			JOptionPane.showMessageDialog(null, "Invalid number format, try again.");
		}
		catch (ParseException e1)
		{
			JOptionPane.showMessageDialog(null, "Invalid number format, try again.");
		}

		updateBottomUI(); //propagate changes down
	}

	/** This method updates the top portions of the UI based on changes in number of people or animals
	 *  or changes in quality of RB.  It sets the values into the daily and weekly cost boxes
	 *
	 */
	private void updateTopUI()
	{
		//First, retrieve the costs of everything
		int i1;

		//First, retrieve the costs of everything
		int i2;

		//First, retrieve the costs of everything
		int i3;
		i1 = cmbFood.getSelectedIndex();

		float food = foods.getRBCostAtI(i1).getCost();

		i2 = cmbInn.getSelectedIndex();

		float inn = inns.getRBCostAtI(i2).getCost();

		i3 = cmbAnimal.getSelectedIndex();

		float animal = animals.getRBCostAtI(i3).getCost();

		try
		{
			int numPeople = nf.parse(txtPeop.getText()).intValue(); //note using nf, not gp
			int numAnimal = nf.parse(txtAnim.getText()).intValue();

			//now set them all
			float result = 0;

			result = food * numPeople;
			txtDayFood.setText(gp.format(result)); //but here we use gp
			result *= 7;
			txtWeekFood.setText(gp.format(result)); //but here we use gp

			result = inn * numPeople;
			txtDayInn.setText(gp.format(result)); //but here we use gp
			result *= 7;
			txtWeekInn.setText(gp.format(result)); //but here we use gp

			result = animal * numAnimal;
			txtDayAnimal.setText(gp.format(result)); //but here we use gp
			result *= 7;
			txtWeekAnimal.setText(gp.format(result)); //but here we use gp
		}
		catch (NumberFormatException e1)
		{
			JOptionPane.showMessageDialog(null, "Invalid number format, try again.");
		}
		catch (ParseException e1)
		{
			JOptionPane.showMessageDialog(null, "Invalid number format, try again.");
		}

		updateMidUI(); //propagate changes down
	}
}
