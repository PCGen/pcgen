package pcgen.rules.persistence.token;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.PrimitiveCollection;
import pcgen.cdom.reference.SelectionCreator;
import pcgen.persistence.lst.LstToken;
import pcgen.rules.context.LoadContext;

/**
 * A QualifierToken is an object that can select and filter zero or more objects
 * of a specific type of object.
 * 
 * This is typically processed as part of a CHOOSE. The syntax of a Qualifier is
 * !Key=Condition[Value]. The Key is returned from the LstToken interface, the
 * condition and value are provided to the initialize method.
 * 
 * @param <T>
 *            The Type of object processed by the QualifierToken
 */
public interface QualifierToken<T extends CDOMObject> extends LstToken,
		PrimitiveCollection<T>
{
	/**
	 * Initializes the QualifierToken with the content of the
	 * PrimitiveCollection as defined by the arguments.
	 * 
	 * This method returns true if initialization was successful. If
	 * initialization is not successful, then it should not be used as a
	 * PrimitiveCollection.
	 * 
	 * Note that any qualifier may or may not support conditions and negation;
	 * that is up to the implementation. However, any non-support should be
	 * identified by returning false, rather than throwing an exception.
	 * 
	 * @param context
	 *            The LoadContext to be used to get necessary information to
	 *            initialize the QualifierToken
	 * @param cl
	 *            The SelectionCreator used to get objects on which this
	 *            QualifierToken is operating
	 * @param condition
	 *            The condition of the qualifier; may be null if no condition
	 * @param value
	 *            The value of the qualifier
	 * @param negated
	 *            true if the PrimitiveCollection should be negated; false
	 *            otherwise
	 * @return true if initialization was successful; false otherwise
	 */
	public boolean initialize(LoadContext context, SelectionCreator<T> cl,
		String condition, String value, boolean negated);
}
