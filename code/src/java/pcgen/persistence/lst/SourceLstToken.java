package pcgen.persistence.lst;

import java.util.Map;

public interface SourceLstToken extends LstToken {
	public boolean parse(Map sourceMap, String value);

}
