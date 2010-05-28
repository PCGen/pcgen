package pcgen.cdom.choiceset;

import java.util.Collection;

public interface ObjectContainer<T>
{

	Class<T> getReferenceClass();

	Collection<? extends T> getContainedObjects();

}
