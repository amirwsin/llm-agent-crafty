package crafty;
/*
 * ==========================================
 * Class: CellSet
 * Package: crafty
 * ==========================================
 * Purpose:
 * - Represents a specialized set of `AbstractCell` instances, extending
 *   `HashSet` to manage collections of land cells within a land-use
 *   model framework.
 * - Provides additional functionality for indexing cells by coordinates
 *   (x, y) using a `HashMap`, allowing for quick lookup of specific cells.
 *
 * Main Components:
 * - Fields:
 *   - `cellHashMap`: A `HashMap` storing cells indexed by their (x, y)
 *      coordinates, represented as concatenated strings.
 * - Methods:
 *   - `addCellToMap(int x, int y, AbstractCell landCell)`: Adds a cell
 *     to the `cellHashMap` with a unique coordinate-based key.
 *   - `getCell(int x, int y)`: Retrieves a cell by its (x, y) coordinates.
 *   - `getCellHashMap()`: Returns the entire `cellHashMap` for broader access.
 * - Overrides:
 *   - `addAll(Collection<? extends AbstractCell> c)`: Overrides the `addAll`
 *     method but currently returns `false` by default (to be implemented).
 *
 * Usage:
 * - This class is intended to be used within the `crafty` package as a
 *   container and lookup structure for cells, supporting operations
 *   that require spatial indexing.
 *
 * Dependencies:
 * - Imports classes from `modelRunner` and `SimState` for integration
 *   within the model simulation environment.
 * ==========================================
 */

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class CellSet extends HashSet<AbstractCell> {

	private HashMap<String, AbstractCell> cellHashMap = new HashMap<>();

	public void addCellToMap(int x, int y, AbstractCell landCell) {
		cellHashMap.put(x + "," + y, landCell);
	}

	public AbstractCell getCell(int x, int y) {

		return cellHashMap.get(x + "," + y);
	}

	@Override
	public boolean addAll(Collection<? extends AbstractCell> c) {
		// TODO Auto-generated method stub
		return false;
	}


	public HashMap<String, AbstractCell> getCellHashMap() {
		return cellHashMap;
	}


}
