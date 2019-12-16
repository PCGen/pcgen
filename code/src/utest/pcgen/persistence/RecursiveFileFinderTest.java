package pcgen.persistence;

import java.io.File;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import org.junit.Test;
import pcgen.system.ConfigurationSettings;

public class RecursiveFileFinderTest {

	@Test
	public void parseMainPCCDir(){
		List<URI> files = new LinkedList<>();
		new RecursiveFileFinder().findFiles(new File(ConfigurationSettings.getPccFilesDir()), files);

		assertThat(files, hasSize(greaterThan(605)));
	}

	@Test
	public void fakeFile(){
		List<URI> files = new LinkedList<>();
		new RecursiveFileFinder().findFiles(new File("FakeFile"), files);

		assertThat(files, hasSize(0));
	}
}
