/*
 * CharacterSheetModel.java
 *
 * Created on February 2, 2004, 7:19 PM
 */

package plugin.charactersheet;

import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import plugin.charactersheet.gui.CharacterInfoTabPanel;
import plugin.charactersheet.gui.CharacterPanel;

import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.util.ArrayList;

/**
 *
 * @author  soulcatcher
 */
public class CharacterSheetModel {
	private JTabbedPane masterPane = new JTabbedPane();
	private ArrayList characters = new ArrayList();
	private ArrayList infoPanes = new ArrayList();
	private CharacterInfoTabPanel infoPanel;

	/** Creates a new instance of CharacterSheetModel */
	public CharacterSheetModel() {
		applyPrefs();
		masterPane.setTabPlacement(SwingConstants.BOTTOM);
		masterPane.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				refresh();
			}
		});
	}

	/**
	 * Get the component
	 * @return masterPane
	 */
	public Component getComponent() {
		return masterPane;
	}

	/**
	 * Get the character component
	 * @return CharacterPanel
	 */
	public CharacterPanel getCharacterComponent() {
		CharacterPanel cp = new CharacterPanel();
		infoPanes.add(cp);
		return cp;
	}

	/**
	 * Get the info panel
	 * @return info panel
	 */
	public Component getInfoPanel() {
		if(infoPanel == null) {
			infoPanel = new CharacterInfoTabPanel(this);
		}
		return infoPanel;
	}

	/**
	 * Add the pc
	 * @param pc
	 */
	public void addPc(PlayerCharacter pc) {
		characters.add(pc);
		CharacterPanel pcPanel = new CharacterPanel();
		pcPanel.setPc(pc);
		masterPane.add(pc.getDisplayName(), pcPanel);
	}


	/**
	 * @return Returns the infoPanes.
	 */
	public ArrayList getInfoPanes() {
		return infoPanes;
	}

	/**
	 * Remove the pc
	 * @param pc
	 */
	public void removePc(PlayerCharacter pc) {
		characters.remove(pc);
		for(int i = 0; i < masterPane.getTabCount(); i++) {
			Object testPanel = masterPane.getComponent(i);
			if(testPanel instanceof CharacterPanel) {
				CharacterPanel cp = (CharacterPanel)testPanel;
				if(cp.getPc() == pc) {
					cp.flushPrefs();
					masterPane.remove(cp);
					break;
				}
			}
		}
		/*ArrayList removeList = new ArrayList();
		for(int i = 0; i < infoPanes.size(); i++) {
			CharacterPanel panel = (CharacterPanel)infoPanes.get(i);
			if(panel.getPc() == pc) {
				removeList.add(panel);
			}
		}
		infoPanes.removeAll(removeList);
		removeList.clear();*/
	}

	/**
	 * Save the pc
	 * @param pc
	 */
	public void savePc(PlayerCharacter pc) {
		characters.remove(pc);
		for(int i = 0; i < masterPane.getTabCount(); i++) {
			Object testPanel = masterPane.getComponent(i);
			if(testPanel instanceof CharacterPanel) {
				CharacterPanel cp = (CharacterPanel)testPanel;
				if(cp.getPc() == pc) {
					cp.save();
					break;
				}
			}
		}
	}

	/**
	 * Refresh
	 */
	public void refresh() {
		Object testPanel = masterPane.getSelectedComponent();
		if(testPanel instanceof CharacterPanel) {
			((CharacterPanel) testPanel).refresh();
		}
	}

	/**
	 * Close the window
	 */
	public void closeWindow() {
		for(int i = 0; i < masterPane.getTabCount(); i++) {
			Object testPanel = masterPane.getComponent(i);
			if(testPanel instanceof CharacterPanel) {
				CharacterPanel cp = (CharacterPanel)testPanel;
				cp.flushPrefs();
				break;
			}
		}
	}

	/**
	 * Apply the preferences
	 */
	public void applyPrefs() {
		int value = SettingsHandler.getGMGenOption(CharacterSheetPlugin.LOG_NAME + ".color", CharacterPanel.BLUE);
		switch (value) {
			case CharacterPanel.BLUE:
				CharacterPanel.setColorBlue();
				break;
			case CharacterPanel.LIGHTBLUE:
				CharacterPanel.setColorLightBlue();
				break;
			case CharacterPanel.GREEN:
				CharacterPanel.setColorGreen();
				break;
			case CharacterPanel.LIGHTGREEN:
				CharacterPanel.setColorLightGreen();
				break;
			case CharacterPanel.RED:
				CharacterPanel.setColorRed();
				break;
			case CharacterPanel.LIGHTRED:
				CharacterPanel.setColorLightRed();
				break;
			case CharacterPanel.YELLOW:
				CharacterPanel.setColorYellow();
				break;
			case CharacterPanel.LIGHTYELLOW:
				CharacterPanel.setColorLightYellow();
				break;
			case CharacterPanel.GREY:
				CharacterPanel.setColorGrey();
				break;
			case CharacterPanel.LIGHTGREY:
				CharacterPanel.setColorLightGrey();
				break;
			default:
				CharacterPanel.setColorBlue();
				break;
		}
		for(int i = 0; i < masterPane.getTabCount(); i++) {
			Object testPanel = masterPane.getComponent(i);
			if(testPanel instanceof CharacterPanel) {
				CharacterPanel cp = (CharacterPanel)testPanel;
				cp.setColor();
			}
		}
		for(int i = 0; i < infoPanes.size(); i++) {
			CharacterPanel cp = (CharacterPanel)infoPanes.get(i);
			cp.setColor();
		}
	}

	/**
	 * Set the refresh
	 * @param refresh
	 */
	public void setRefresh(boolean refresh) {
		for(int i = 0; i < masterPane.getTabCount(); i++) {
			Object testPanel = masterPane.getComponent(i);
			if(testPanel instanceof CharacterPanel) {
				CharacterPanel cp = (CharacterPanel)testPanel;
				cp.setSystemRefresh(refresh);
			}
		}
		for(int i = 0; i < infoPanes.size(); i++) {
			CharacterPanel cp = (CharacterPanel)infoPanes.get(i);
			cp.setSystemRefresh(refresh);
		}
	}
}

