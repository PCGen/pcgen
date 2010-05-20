package pcgen.cdom.base;

public interface BasicChoice<T>
{

	public void setChoiceActor(ChoiceActor<T> ca);

	public ChoiceActor<T> getChoiceActor();

}
