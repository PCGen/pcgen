/*
 * Copyright 2012 Vincent Lhote
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
package pcgen.gui2.dialog;

import javax.swing.JFrame;

import pcgen.system.LanguageBundle;

/**
 * Factorized Preferences Dialog.
 */
public abstract class AbstractPreferencesDialog extends AbstractDialog
{

	private static final long serialVersionUID = 5806276119355582867L;

	private static final String LB_TITLE = "in_Prefs_title"; //$NON-NLS-1$

	/**
	 * Set the title ([applicationName] Preferences like but localized) and center the dialog on the parent.
	 * @param parent
	 * @param applicationName application name for the preference 
	 * @param modal
	 */
	protected AbstractPreferencesDialog(JFrame parent, String applicationName, boolean modal)
	{
		super(parent, LanguageBundle.getFormattedString(AbstractPreferencesDialog.LB_TITLE, applicationName), modal);
	}

	@Override
	protected boolean includeApplyButton()
	{
		return true;
	}
}
