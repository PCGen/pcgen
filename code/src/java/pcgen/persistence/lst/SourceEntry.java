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

        @Override
		public Campaign getCampaign()
		{
			return null;
		}

        @Override
		public URI getURI()
		{
			return u;
		}

        @Override
		public List<String> getExcludeItems()
		{
			return Collections.emptyList();
		}

        @Override
		public List<String> getIncludeItems()
		{
			return Collections.emptyList();
		}

	}

}
