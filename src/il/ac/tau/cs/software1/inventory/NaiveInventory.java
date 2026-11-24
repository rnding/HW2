package il.ac.tau.cs.software1.inventory;

/*
 * A simple array-based Inventory (Question 6).
 */
public class NaiveInventory implements Inventory {

	private static final int INITIAL_CAPACITY = 100;

	private Collectible[] collectibles;
	private int collectiblesCount;
	//       - an array of Collectibles (it may be larger than the number of items actually stored)
	//       - an int that counts how many items are actually stored

	public NaiveInventory() {
		this.collectibles = new Collectible[INITIAL_CAPACITY];
		this.collectiblesCount = 0;
	}

	@Override
	public void collect(Collectible collectible) {
		if (this.collectiblesCount < this.collectibles.length)
		{
			this.collectibles[collectiblesCount] = collectible;
		}
		else
		{
			Collectible[] newCollectibles = new Collectible[this.collectibles.length * 2];
			for (int i=0; i<collectiblesCount; i++)
			{
				newCollectibles[i] = this.collectibles[i];
			}
			newCollectibles[collectiblesCount] = collectible;
			this.collectibles = newCollectibles;
		}

		this.collectiblesCount++;
	}

	@Override
	public float getTotalWeight() {
		float totalWeight = 0;
		for (int i=0; i<collectiblesCount; i++)
		{
			totalWeight += this.collectibles[i].getWeight();
		}
		return totalWeight;
	}

	@Override
	public int getCurrentCount() {
		return this.collectiblesCount;
	}

	// Returns the array of Collectibles that is stored in the field of this class
	public Collectible[] getCollArr() {
		return this.collectibles;
	}
}
