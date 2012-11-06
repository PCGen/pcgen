package gmgen.plugin;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;

/**
 * This class is a helper for the Combat Tracker.  This class helps display
 * all the statistics of a character.
 * @author Expires 2003
 * @version $Revision$
 *
 * <p>Current Ver: $Revision$</p>
 * <p>Last Editor: $Author$</p>
 * <p>Last Edited: $Date$</p>
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
	 * Sets the default <code>Combatant</code> object used by this class.
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
	 * Calls the <code>setStatText</code> and passes it the pane that is used
	 * for displaying.
	 * Made it final as it is called from constructor.
	 */
	public final void setStatText()
	{
		new Renderer(getCombatant()).start();
	}

	/**
	 * Sets the HTML text used to display calculated stats such as AC, BAB,
	 * saves, etc.
	 * @param cbt
	 */
	public void setStatText(Combatant cbt)
	{
		new Renderer(cbt).start();
	}

	/**
	 * Set the text on the pane to the details of the combatant
	 * @param cbt
	 * @param aPane
	 */
	public void setStatText(Combatant cbt, JTextPane aPane)
	{
		aPane.setEditorKit(aPane.getEditorKitForContentType("text/html"));
		aPane.setText(cbt.toHtmlString());
	}

	private class Renderer extends Thread
	{
		private Combatant combatant;
		
		/**
		 * Constructor
		 * @param cbt
		 */
		public Renderer(Combatant cbt) {
			this.combatant = cbt;
		}
		
        @Override
		public void run() {
			setStatText(combatant, getPane());
		}
	}
}

