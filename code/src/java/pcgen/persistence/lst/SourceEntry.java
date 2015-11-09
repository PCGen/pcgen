package pcgen.persistence.lst;

import java.net.URI;
import java.util.List;

import pcgen.core.Campaign;

public interface SourceEntry
{

	URI getURI();

	Campaign getCampaign();

	List<String> getIncludeItems();

	List<String> getExcludeItems();

}
