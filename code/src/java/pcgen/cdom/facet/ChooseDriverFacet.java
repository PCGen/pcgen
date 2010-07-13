package pcgen.cdom.facet;

import java.util.ArrayList;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.CharID;
import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.ChooseActivation;
import pcgen.core.chooser.ChoiceManagerList;
import pcgen.core.chooser.ChooserUtilities;

public class ChooseDriverFacet extends
		AbstractSingleSourceListFacet<CDOMObject, String> implements
		DataFacetChangeListener<CDOMObject>
{
	/*
	 * Note this is a BIT of a hack in using the "source" to hold the
	 * "associated data"
	 */

	private PlayerCharacterTrackingFacet trackingFacet = FacetLibrary
			.getFacet(PlayerCharacterTrackingFacet.class);

	/**
	 * Triggered when one of the Facets to which ChooseDriverFacet listens fires
	 * a DataFacetChangeEvent to indicate a CDOMObject was added to a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.DataFacetChangeListener#dataAdded(pcgen.cdom.facet.DataFacetChangeEvent)
	 */
	public void dataAdded(DataFacetChangeEvent<CDOMObject> dfce)
	{
		PlayerCharacter pc = trackingFacet.getPC(dfce.getCharID());
		CDOMObject cdo = dfce.getCDOMObject();
		if (pc.isImporting())
		{
			String fullassoc = getSource(pc.getCharID(), cdo);
			if (fullassoc != null)
			{
				ChoiceManagerList<Object> controller = ChooserUtilities
						.getConfiguredController(cdo, pc, null,
								new ArrayList<String>());
				String[] assoc = fullassoc.split(Constants.COMMA, -1);
				for (String string : assoc)
				{
					if (string.startsWith("FEAT?"))
					{
						int openloc = string.indexOf('(');
						int closeloc = string.lastIndexOf(')');
						string = string.substring(openloc + 1, closeloc);
					}
					controller.restoreChoice(pc, cdo, string);
				}
			}
		}
		else
		{
			if (ChooseActivation.hasChooseToken(cdo))
			{
				ChooserUtilities.modChoices(cdo, new ArrayList<Object>(),
						new ArrayList<Object>(), true, pc, true, null);
			}
		}
	}

	/**
	 * Triggered when one of the Facets to which ChooseDriverFacet listens fires
	 * a DataFacetChangeEvent to indicate a CDOMObject was removed from a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.DataFacetChangeListener#dataRemoved(pcgen.cdom.facet.DataFacetChangeEvent)
	 */
	public void dataRemoved(DataFacetChangeEvent<CDOMObject> dfce)
	{
	}

	public void addAssociation(CharID id, CDOMObject cdo, String fullassoc)
	{
		add(id, cdo, fullassoc);
	}
}
