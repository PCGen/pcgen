/*
 * Created on Dec 9, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package plugin.charactersheet.gui;

import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.gui.CharacterInfoTab;
import pcgen.system.LanguageBundle;
import plugin.charactersheet.CharacterSheetModel;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * @author djones4
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CharacterInfoTabPanel extends JPanel implements CharacterInfoTab
{
	private PlayerCharacter pc;
	private CharacterPanel cp;
	private CharacterSheetModel model;

	/**
	 * Constructor
	 * @param model
	 */
	public CharacterInfoTabPanel(CharacterSheetModel model)
	{
		this.model = model;
		setLayout(new BorderLayout());
	}

    @Override
	public void setPc(PlayerCharacter pc)
	{
		/*if(this.pc != pc) {
		 ArrayList panes = model.getInfoPanes();
		 this.pc = pc;
		 boolean found = false;
		 for(int i = 0; i < panes.size(); i++) {
		 CharacterPanel panel = (CharacterPanel)panes.get(i);
		 if(panel.getPc() == pc) {
		 setCharacterPanel(panel);
		 found = true;
		 break;
		 }
		 }

		 if(!found) {
		 setCharacterPanel(model.getCharacterComponent());
		 cp.setPc(pc);
		 }
		 }*/
		if (cp == null)
		{
			setCharacterPanel(model.getCharacterComponent());
		}
		cp.setPc(pc);
	}

    @Override
	public PlayerCharacter getPc()
	{
		//TODO Should this be cp.getPC()?? - thpr 10/27/06
		return pc;
	}

    @Override
	public int getTabOrder()
	{
		return SettingsHandler
			.getGMGenOption(".Panel.CharacterPanel.Order", 20);
	}

    @Override
	public void setTabOrder(int order)
	{
		SettingsHandler.setGMGenOption(".Panel.CharacterPanel.Order", order);
	}

    @Override
	public String getTabName()
	{
		return LanguageBundle.getString("in_preview");
	}

    @Override
	public boolean isShown()
	{
		return SettingsHandler.getGMGenOption(".Panel.CharacterPanel.Show",
			true);
	}

	/**
	 * Retrieve the list of tasks to be done on the tab.
	 * @return List of task descriptions as Strings.
	 */
    @Override
	public List<String> getToDos()
	{
		return new ArrayList<String>();
	}

    @Override
	public void refresh()
	{
		if (cp != null)
		{
			cp.refresh();
		}
	}

    @Override
	public void forceRefresh()
	{
		if (cp != null)
		{
			cp.forceRefresh();
		}
	}

    @Override
	public JComponent getView()
	{
		return this;
	}

	/**
	 * Set the panel
	 * @param cp
	 */
	public void setCharacterPanel(CharacterPanel cp)
	{
		this.cp = cp;
		removeAll();
		add(cp, BorderLayout.CENTER);
	}
}
