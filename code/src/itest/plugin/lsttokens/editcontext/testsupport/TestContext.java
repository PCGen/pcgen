package plugin.lsttokens.editcontext.testsupport;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pcgen.persistence.lst.CampaignSourceEntry;

public class TestContext
{

	private Map<URI, List<String>> map = new HashMap<URI, List<String>>();

	public void putText(URI testCampaign, String[] str)
	{
		map.put(testCampaign, str == null ? null : Arrays.asList(str));
	}

	public List<String> getText(URI cse)
	{
		return map.get(cse);
	}

	public int getSize()
	{
		return map.size();
	}

	/*
	 * TODO Today this is LinkedHashMap to preserve order; but that shouldn't be
	 * necessary.
	 */
	private Map<URI, CampaignSourceEntry> cm =
			new LinkedHashMap<URI, CampaignSourceEntry>();

	public void putCampaign(URI uri, CampaignSourceEntry testCampaign)
	{
		cm.put(uri, testCampaign);
	}

	public CampaignSourceEntry getCampaign(URI uri)
	{
		return cm.get(uri);
	}

	public Set<URI> getURIs()
	{
		return cm.keySet();
	}

	@Override
	public String toString()
	{
		return super.toString() + " " + map.toString();
	}
}
