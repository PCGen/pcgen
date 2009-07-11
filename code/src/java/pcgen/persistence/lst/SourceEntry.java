package pcgen.persistence.lst;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import pcgen.core.Campaign;

public interface SourceEntry
{

	URI getURI();

	Campaign getCampaign();

	List<String> getIncludeItems();

	List<String> getExcludeItems();

	public class URIOnly implements SourceEntry
	{
		private final URI u;

		public URIOnly(URI uri)
		{
			u = uri;
		}

		public Campaign getCampaign()
		{
			return null;
		}

		public URI getURI()
		{
			return u;
		}

		public List<String> getExcludeItems()
		{
			return Collections.emptyList();
		}

		public List<String> getIncludeItems()
		{
			return Collections.emptyList();
		}

	}

}
