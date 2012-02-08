package pcgen.cdom.base;

public interface QualifiedActor<T extends QualifyingObject>
{

	public void act(T object, Object source);
}
