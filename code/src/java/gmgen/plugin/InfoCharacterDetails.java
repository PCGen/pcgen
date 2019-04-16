/*
 *  Initiative - A role playing utility to track turns
 *  Copyright (C) 2002 Devon D Jones
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 */
package gmgen.plugin;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import pcgen.util.Logging;

/**
 * This class is a helper for the Combat Tracker.  This class helps display
 * all the statistics of a character.
 */
public class InfoCharacterDetails
{
	/** The specialised pane that holds all the stats. */
	private JTextPane mainOutput;

	/** A Character class to have access to all the stats. */
	private Combatant cbt;

	/**
	 * Creates an instance of this class taking in a character and a pane.
	 * @param cbt
	 * @param mainOutput the pane that the stats will be displayed on.
	 */
	public InfoCharacterDetails(Combatant cbt, JTextPane mainOutput)
	{
		setCombatant(cbt);
		setPane(mainOutput);
		setStatText();
	}

	/**
	 * Sets the default {@code Combatant} object used by this class.
	 * Made it final as it is called from constructor.
	 * @param cbt
	 */
	public final void setCombatant(Combatant cbt)
	{
		this.cbt = cbt;
	}

	/**
	 * Gets the default Combatant object used by this class.
	 * @return the character being used.
	 */
	public Combatant getCombatant()
	{
		return this.cbt;
	}

	/**
	 * Sets the pane field of this class.
	 * Made it final as it is called from constructor.
	 * @param o the new pane that will be used.
	 */
	public final void setPane(JTextPane o)
	{
		mainOutput = o;
	}

	/**
	 * Gets the pane that is used.
	 * @return the pane that is used.
	 */
	public JTextPane getPane()
	{
		return mainOutput;
	}

	/**
	 * Get the scroll pane
	 * @return scroll pane
	 */
	public JScrollPane getScrollPane()
	{
		JScrollPane scrollPane = new JScrollPane();
		mainOutput.setCaretPosition(0);
		scrollPane.setViewportView(mainOutput);

		return scrollPane;
	}

	/**
	 * Calls the {@code setStatText} and passes it the pane that is used
	 * for displaying.
	 * Made it final as it is called from constructor.
	 */
	private void setStatText()
	{
		new Renderer(getCombatant()).start();
	}

	/**
	 * Set the text on the pane to the details of the combatant
	 * @param cbt
	 * @param aPane
	 */
	private static void setStatText(Combatant cbt, JTextPane aPane)
	{
		aPane.setEditorKit(aPane.getEditorKitForContentType("text/html"));
		String htmlString = cbt.toHtmlString();
		try
		{
			aPane.setText(htmlString);
		}
		catch (Exception e)
		{
			Logging.errorPrint("InfoCharacterDetails.setStatText failed for text " + htmlString, e);
		}
	}

	private class Renderer extends Thread
	{
		private final Combatant combatant;

		/**
		 * Constructor
		 * @param cbt
		 */
		Renderer(Combatant cbt)
		{
			this.combatant = cbt;
		}

		@Override
		public void run()
		{
			setStatText(combatant, getPane());
		}
	}
}
