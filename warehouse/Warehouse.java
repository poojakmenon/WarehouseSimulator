package warehouse;

/*
 * This class implements a warehouse on a Hash Table like structure 
 * where each entry of the table stores a priority queue 
 */ 
public class Warehouse {
    private Sector[] sectors;
    
    // Initializes every sector to an empty sector
    public Warehouse() {
        sectors = new Sector[10];

        for (int i = 0; i < 10; i++) {
            sectors[i] = new Sector();
        }
    }

    public void addProduct(int id, String name, int stock, int day, int demand) {
        evictIfNeeded(id);
        addToEnd(id, name, stock, day, demand);
        fixHeap(id);
    }

    /**
     * Add new product to sector
     */
    private void addToEnd(int id, String name, int stock, int day, int demand) {

        int i = id % 10;
        Sector newSector = sectors[i];

        if( newSector == null ) {

            newSector = new Sector();
            sectors[i] = newSector;
        }

        newSector.add(new Product(id, name, stock, day, demand));
    }

    /**
     * Fix the heap structure of the sector
     */
    private void fixHeap(int id) {

        if ( sectors[ id % 10 ].getSize() == 1) {
            return;
        }

        sectors[ id % 10 ].swim(sectors[ id % 10 ].getSize());
    }

    /**
     * Delete the least popular item in the correct sector, only if its size is 5 while maintaining heap
     */
    private void evictIfNeeded(int id) {

        int i = id % 10;

        if ( sectors[i].getSize() == 5 ) {

            sectors[i].swap(1,5);

            sectors[i].deleteLast();

            sectors[i].sink(1);
        }
    }

    /**
     * Update the stock of some item by some amount
     */
    public void restockProduct(int id, int amount) {

        int sectorNum = id % 10;
        int size = sectors[sectorNum].getSize();

        for ( int i = 1; i <= size; i++ ) {
            if (sectors[sectorNum].get(i).getId() == id ) {
                sectors[sectorNum].get(i).updateStock(amount);
                break;
            }
        } 
    }
    
    /**
     * Delete some arbitrary product while maintaining the heap structure in O(logn)
     */
    public void deleteProduct(int id) {

        int sectorNum = id % 10;
        int size = sectors[sectorNum].getSize();

        for ( int i = 1; i <= size; i++ ) {
            if (sectors[sectorNum].get(i).getId() == id ) {
                sectors[sectorNum].swap(i, size);
                sectors[sectorNum].deleteLast();
                sectors[sectorNum].sink(i);
                break;
            }
        } 
    }
    
    /**
     * Simulate a purchase order for some product
     */
    public void purchaseProduct(int id, int day, int amount) {
        int index = 1;
        int sectorNum = id % 10;
        int size = sectors[sectorNum].getSize();
        while ( index <= size && sectors[sectorNum].get(index).getId() != id ) {
            index ++;
        } 
        
        if ( index <= size && sectors[sectorNum].get(index).getStock() >= amount ) {
            sectors[sectorNum].get(index).setLastPurchaseDay(day);
            sectors[sectorNum].get(index).updateStock(-amount);
            sectors[sectorNum].get(index).updateDemand(amount);
            sectors[sectorNum].sink(index);
        }
    }

    /*
     * string representation
     */
    public String toString() {
        String warehouseString = "[\n";

        for (int i = 0; i < 10; i++) {
            warehouseString += "\t" + sectors[i].toString() + "\n";
        }
        
        return warehouseString + "]";
    }

}
