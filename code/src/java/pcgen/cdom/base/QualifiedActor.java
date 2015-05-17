package pcgen.cdom.base;

public interface QualifiedActor<T extends QualifyingObject, R>
{

	public R act(T object, Object source);
}
