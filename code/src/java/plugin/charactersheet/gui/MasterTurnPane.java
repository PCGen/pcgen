package plugin.charactersheet.gui;

import java.util.Properties;

import pcgen.core.PlayerCharacter;

/**
 * The pane for the master turning (e.g. Turn Undead) 
 */
public class MasterTurnPane extends javax.swing.JPanel {
	private PlayerCharacter pc;
	private int serial = 0;

	private TurnPane turnUndeadPane;
	private TurnPane turnAirPane;
	private TurnPane turnEarthPane;
	private TurnPane turnFirePane;
	private TurnPane turnWaterPane;
	private TurnPane turnPlantPane;
	private TurnPane turnScalyPane;

	/**
	 * Constructor
	 */
	public MasterTurnPane() {
		initComponents();
	}
	
	private void initComponents() {
		turnUndeadPane = new TurnPane();
		turnAirPane = new TurnPane();
		turnEarthPane = new TurnPane();
		turnFirePane = new TurnPane();
		turnWaterPane = new TurnPane();
		turnPlantPane = new TurnPane();
		turnScalyPane = new TurnPane();
		
		setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));
		add(turnUndeadPane);
		add(turnAirPane);
		add(turnEarthPane);
		add(turnFirePane);
		add(turnWaterPane);
		add(turnPlantPane);
		add(turnScalyPane);
	}

	/**
	 * Set color for this pane
	 */
	public void setColor() {
		turnAirPane.setColor();
		turnEarthPane.setColor();
		turnFirePane.setColor();
		turnWaterPane.setColor();
		turnPlantPane.setColor();
		turnScalyPane.setColor();
		turnUndeadPane.setColor();
	}

	/**
	 * Set PC for this pane
	 * @param pc
	 * @param pcProperties
	 */
	public void setPc(PlayerCharacter pc, Properties pcProperties) {
		if(this.pc != pc) {
			this.pc = pc;
			serial = 0;
			turnUndeadPane.setPc(pc, pcProperties, "Undead");
			turnAirPane.setPc(pc, pcProperties, "Air");
			turnEarthPane.setPc(pc, pcProperties, "Earth");
			turnFirePane.setPc(pc, pcProperties, "Fire");
			turnWaterPane.setPc(pc, pcProperties, "Water");
			turnPlantPane.setPc(pc, pcProperties, "Plant");
			turnScalyPane.setPc(pc, pcProperties, "Scalykind");
		}
	}

	/**
	 * Refresh this pane
	 */
	public void refresh() {
		if(serial < pc.getSerial()) {
			turnAirPane.refresh();
			turnEarthPane.refresh();
			turnFirePane.refresh();
			turnWaterPane.refresh();
			turnPlantPane.refresh();
			turnScalyPane.refresh();
			turnUndeadPane.refresh();
			serial = pc.getSerial();
		}
	}

	/**
	 * Update the properties for this pane
	 */
	public void updateProperties() {
		turnAirPane.updateProperties();
		turnEarthPane.updateProperties();
		turnFirePane.updateProperties();
		turnWaterPane.updateProperties();
		turnPlantPane.updateProperties();
		turnScalyPane.updateProperties();
		turnUndeadPane.updateProperties();
	}

	/**
	 * Destroy this pane
	 */
	public void destruct() {
		turnAirPane.destruct();
		turnEarthPane.destruct();
		turnFirePane.destruct();
		turnWaterPane.destruct();
		turnPlantPane.destruct();
		turnScalyPane.destruct();
		turnUndeadPane.destruct();

		turnAirPane = null;
		turnEarthPane = null;
		turnFirePane = null;
		turnWaterPane = null;
		turnPlantPane = null;
		turnScalyPane = null;
		turnUndeadPane = null;
		removeAll();
	}
}

