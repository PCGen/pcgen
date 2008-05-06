package pcgen.cdom.base;

public interface CDOMList<T extends CDOMObject> extends PrereqObject
{
	public Class<T> getListClass();
}
