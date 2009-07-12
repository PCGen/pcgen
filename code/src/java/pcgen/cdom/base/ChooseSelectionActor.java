package pcgen.cdom.base;

import pcgen.core.PlayerCharacter;
import pcgen.persistence.PersistenceLayerException;

/**
 * A ChooseSelectionActor is an object that can apply and remove choices (based
 * on the CHOOSE token) to a PlayerCharacter. This is an object that will act
 * after a selection has been made by a user through through the chooser system.
 */
public interface ChooseSelectionActor<T>
{

	/**
	 * Applies the given choice to the given PlayerCharacter.
	 * 
	 * @param obj
	 *            The CDOMObject to which the choice was applied (the CDOMObject
	 *            on which the CHOOSE token was present)
	 * @param choice
	 *            The choice being applied to the given PlayerCharacter
	 * @param pc
	 *            The PlayerCharacter to which the given choice should be
	 *            applied.
	 */
	void applyChoice(CDOMObject obj, T choice, PlayerCharacter pc);

	/**
	 * Removes the given choice from the given PlayerCharacter.
	 * 
	 * @param obj
	 *            The CDOMObject to which the choice was applied (the CDOMObject
	 *            on which the CHOOSE token was present)
	 * @param choice
	 *            The choice being removed from the given PlayerCharacter
	 * @param pc
	 *            The PlayerCharacter from which the given choice should be
	 *            removed.
	 */
	void removeChoice(CDOMObject obj, T choice, PlayerCharacter pc);

	/**
	 * Returns the source of this ChooseSelectionActor. Provided primarily to
	 * allow the Token/Loader system to properly identify the source of
	 * ChooseSelectionActors for purposes of unparsing.
	 * 
	 * @return The source of this ChooseSelectionActor
	 */
	String getSource();

	/**
	 * Returns the LST format for this ChooseSelectionActor. Provided primarily
	 * to allow the Token/Loader system to properly unparse the
	 * ChooseSelectionActor.
	 * 
	 * @return The LST format of this ChooseSelectionActor
	 */
	String getLstFormat() throws PersistenceLayerException;

	/**
	 * Returns the class that theis ChooseSelectionActor can act upon
	 * 
	 * @return The class that theis ChooseSelectionActor can act upon
	 */
	Class<T> getChoiceClass();

}
