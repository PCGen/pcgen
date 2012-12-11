package tokenmodel;

import org.junit.Test;

import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.TemplateLst;
import tokenmodel.testsupport.AbstractTokenModelTest;

public class TemplateLstTest extends AbstractTokenModelTest
{

	TemplateLst token = new TemplateLst();

	@Test
	public void testFromTemplate() throws PersistenceLayerException
	{
		PCTemplate source = create(PCTemplate.class, "Source");
		PCTemplate granted = create(PCTemplate.class, "Granted");
		ParseResult result = token.parseToken(context, source, "Granted");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		finishLoad();
		assertEquals(0, templateFacet.getCount(id));
		templateFacet.add(id, source, this);
		assertTrue(templateFacet.contains(id, source));
		assertTrue(templateFacet.contains(id, granted));
		assertEquals(2, templateFacet.getCount(id));
	}

	@Test
	public void testFromRace() throws PersistenceLayerException
	{
		Race source = create(Race.class, "Source");
		PCTemplate granted = create(PCTemplate.class, "Granted");
		ParseResult result = token.parseToken(context, source, "Granted");
		if (result != ParseResult.SUCCESS)
		{
			result.printMessages();
			fail("Test Setup Failed");
		}
		finishLoad();
		raceFacet.set(id, source);
		assertTrue(templateFacet.contains(id, granted));
		assertEquals(1, templateFacet.getCount(id));
	}

}
