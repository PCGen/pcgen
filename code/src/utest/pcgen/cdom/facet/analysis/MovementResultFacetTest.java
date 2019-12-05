package pcgen.cdom.facet.analysis;

import org.junit.jupiter.api.Test;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.enumeration.MovementType;

import static org.junit.jupiter.api.Assertions.*;

class MovementResultFacetTest
{

    @Test
    void getMovementOfTypeHasZeroDefault()
    {
        MovementResultFacet movementResultFacet = new MovementResultFacet();
        MovementType movementType = MovementType.getConstant("CRAWL");
        DataSetID datasetID = DataSetID.getID();
        CharID charID = CharID.getID(datasetID);
        double movementOfType = movementResultFacet.getMovementOfType(charID, movementType);
        assertEquals(0.0, movementOfType, 0.1);
    }
}
