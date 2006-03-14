/*
 * Created on Jul 28, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package pcgen.gui;

import pcgen.core.PlayerCharacter;

import javax.swing.JComponent;
import java.util.List;

/**
 * @author soulcatcher
 */
public interface CharacterInfoTab {

	/**
	 * @param pc
	 */
	public void setPc(PlayerCharacter pc);

	/**
	 * @return PlayerCharacter
	 */
	public PlayerCharacter getPc();

	/**
	 * @return tab order
	 */
	public int getTabOrder();

	/**
	 * @param order
	 */
	public void setTabOrder(int order);

	/**
	 * @return tab name
	 */
	public String getTabName();

	/**
	 * @return TRUE if is shown
	 */
	public boolean isShown();

	/**
	 * refresh
	 */
	public void refresh();

	/**
	 * force the refresh
	 */
	public void forceRefresh();

	/**
	 * @return JComponent - a view
	 */
	public JComponent getView();

	/**
	 * Retrieve the list of tasks to be done on the tab.
	 * @return List of task descriptions as Strings.
	 */
	public List getToDos();
}
