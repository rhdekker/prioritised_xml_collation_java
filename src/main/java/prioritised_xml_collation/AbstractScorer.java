package prioritised_xml_collation;

/**
 * Created by ellibleeker on 30/01/2017.
 */
public abstract class AbstractScorer {
    public abstract Match match(XMLToken tokenA, XMLToken tokenB);

    // the method gap returns a new object Cell
    // Cell has the following fields: match (boolean), x + y (coordinates of EditTable), parent, global score
    public Cell gap(int x, int y, Cell parent) {
        return new Cell(Boolean.FALSE, x, y, parent, parent.globalScore - 1);
    }

    public Cell score(int x, int y, Cell parent, Match typeOfMatch) {
        if (typeOfMatch == Match.match) {
            return new Cell(Boolean.TRUE, x, y, parent, parent.globalScore+1);
        }
        if (typeOfMatch == Match.semanticMatch) {
            return new Cell(Boolean.FALSE, x, y, parent, parent.globalScore);
        }
        // "replacement" means omission + addition
        return new Cell(Boolean.FALSE, x, y, parent, parent.globalScore - 2);
    }

    public enum Match {
        match, not_matched, semanticMatch
    }
}
