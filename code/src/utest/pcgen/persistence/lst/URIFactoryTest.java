package pcgen.persistence.lst;

import org.junit.jupiter.api.Test;
import pcgen.system.ConfigurationSettings;
import pcgen.system.PCGenSettings;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class URIFactoryTest
{
	@Test
	void testGetURI_WithValidRootAndOffset() throws URISyntaxException
	{
		URI rootURI = new URI("file://example/root/");
		String offset = "data/file.txt";
		URIFactory uriFactory = new URIFactory(rootURI, offset);

		URI expected = new URI("file:" + System.getProperty("user.dir") + "/data/file.txt");
		URI result = uriFactory.getURI();

		assertEquals(expected, result);
	}

	@Test
	void testGetURI_WithOffsetStartingWithAtSymbol() throws URISyntaxException
	{
		URI rootURI = new URI("file:/example/root/");
		String offset = "@subdir/file.txt";
		URIFactory uriFactory = new URIFactory(rootURI, offset);

		File expectedFile = new File(ConfigurationSettings.getPccFilesDir(), "subdir/file.txt");
		URI result = uriFactory.getURI();

		assertEquals(expectedFile.toURI(), result);
	}

	@Test
	void testGetURI_WithOffsetStartingWithAmpersand() throws URISyntaxException
	{
		URI rootURI = new URI("file:/example/root/");
		String offset = "&subdir/file.txt";
		URIFactory uriFactory = new URIFactory(rootURI, offset);

		File expectedFile = new File(PCGenSettings.getVendorDataDir(), "subdir/file.txt");
		URI result = uriFactory.getURI();

		assertEquals(expectedFile.toURI(), result);
	}

	@Test
	void testGetURI_WithOffsetStartingWithDollarSign() throws URISyntaxException
	{
		URI rootURI = new URI("file:/example/root/");
		String offset = "$subdir/file.txt";
		URIFactory uriFactory = new URIFactory(rootURI, offset);

		File expectedFile = new File(PCGenSettings.getHomebrewDataDir(), "subdir/file.txt");
		URI result = uriFactory.getURI();

		assertEquals(expectedFile.toURI(), result);
	}

	@Test
	void testGetURI_WithOffsetStartingWithAsterisk() throws URISyntaxException
	{
		URI rootURI = new URI("file:/example/root/");
		String offset = "*subdir/file.txt";
		URIFactory uriFactory = new URIFactory(rootURI, offset);

		File homebrewFile = new File(PCGenSettings.getHomebrewDataDir(), "subdir/file.txt");
		File vendorFile = new File(PCGenSettings.getVendorDataDir(), "subdir/file.txt");

		URI result = uriFactory.getURI();

		if (homebrewFile.exists())
		{
			assertEquals(homebrewFile.toURI(), result);
		} else if (vendorFile.exists())
		{
			assertEquals(vendorFile.toURI(), result);
		} else
		{
			assertEquals(new File(ConfigurationSettings.getPccFilesDir(), "subdir/file.txt").toURI(), result);
		}
	}

	@Test
	void testGetURI_WithEmptyOffset() throws URISyntaxException
	{
		URI rootURI = new URI("file:/example/root/");
		String offset = "";

		assertThrows(IllegalArgumentException.class, () -> {
			new URIFactory(rootURI, offset).getURI();
		});
	}

	@Test
	void testGetURI_WithInvalidURI() throws URISyntaxException
	{
		URI rootURI = new URI("file:/example/root/");
		String offset = "http://:@/\\invalid/uri";
		URIFactory uriFactory = new URIFactory(rootURI, offset);

		URI result = uriFactory.getURI();

		assertEquals(URIFactory.FAILED_URI, result);
	}

	@Test
	void testGetURI_WithOffsetContainingProtocol() throws URISyntaxException
	{
		URI rootURI = new URI("file:/example/root/");
		String offset = "http://example.com/data/file.txt";
		URIFactory uriFactory = new URIFactory(rootURI, offset);

		URI result = uriFactory.getURI();

		URI expected = new URI("http", "example.com", "/data/file.txt", null);
		assertEquals(expected, result);
	}
}
