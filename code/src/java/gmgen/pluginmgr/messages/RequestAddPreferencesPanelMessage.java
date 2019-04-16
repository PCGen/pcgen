/*
 * Copyright James Dempsey, 2014
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
 *
 */
package gmgen.pluginmgr.messages;

import gmgen.gui.PreferencesPanel;
import pcgen.pluginmgr.PCGenMessage;

/**
 * The Class {@code RequestAddPreferencesPanelMessage} encapsulates
 * a request to add a panel to the GMGen preferences.
 *
 * 
 */

@SuppressWarnings("serial")
public class RequestAddPreferencesPanelMessage extends PCGenMessage
{
	private final String name;
	private final PreferencesPanel prefsPanel;

	/**
	 * Create a new instance of RequestAddPreferencesPanelMessage
	 * @param source The object requesting to add a panel.
	 * @param name The name of the panel.
	 * @param prefsPanel The panel to be added.
	 */
	public RequestAddPreferencesPanelMessage(Object source, String name, PreferencesPanel prefsPanel)
	{
		super(source);
		this.name = name;
		this.prefsPanel = prefsPanel;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return the prefsPanel
	 */
	public PreferencesPanel getPrefsPanel()
	{
		return prefsPanel;
	}

}
