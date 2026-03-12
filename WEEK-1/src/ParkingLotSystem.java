class ParkingSpot {

    String licensePlate;
    long entryTime;
    String status; // EMPTY, OCCUPIED, DELETED

    public ParkingSpot() {
        status = "EMPTY";
    }
}

public class ParkingLotSystem {

    private ParkingSpot[] table;
    private int capacity = 500;
    private int totalProbes = 0;
    private int parkOperations = 0;

    public ParkingLotSystem() {
        table = new ParkingSpot[capacity];
        for (int i = 0; i < capacity; i++) {
            table[i] = new ParkingSpot();
        }
    }

    // Custom hash function
    private int hash(String licensePlate) {
        return Math.abs(licensePlate.hashCode()) % capacity;
    }

    // Park vehicle
    public void parkVehicle(String licensePlate) {

        int index = hash(licensePlate);
        int probes = 0;

        while (!table[index].status.equals("EMPTY") &&
                !table[index].status.equals("DELETED")) {

            index = (index + 1) % capacity; // linear probing
            probes++;
        }

        table[index].licensePlate = licensePlate;
        table[index].entryTime = System.currentTimeMillis();
        table[index].status = "OCCUPIED";

        totalProbes += probes;
        parkOperations++;

        System.out.println("Vehicle " + licensePlate +
                " assigned spot #" + index +
                " (" + probes + " probes)");
    }

    // Exit vehicle
    public void exitVehicle(String licensePlate) {

        int index = hash(licensePlate);

        while (!table[index].status.equals("EMPTY")) {

            if (table[index].status.equals("OCCUPIED") &&
                    table[index].licensePlate.equals(licensePlate)) {

                long duration = System.currentTimeMillis() -
                        table[index].entryTime;

                double hours = duration / (1000.0 * 60 * 60);
                double fee = hours * 5; // $5 per hour

                table[index].status = "DELETED";

                System.out.println("Vehicle " + licensePlate +
                        " exited from spot #" + index);

                System.out.println("Duration: " +
                        String.format("%.2f", hours) + " hours");

                System.out.println("Fee: $" +
                        String.format("%.2f", fee));

                return;
            }

            index = (index + 1) % capacity;
        }

        System.out.println("Vehicle not found");
    }

    // Parking statistics
    public void getStatistics() {

        int occupied = 0;

        for (ParkingSpot spot : table) {
            if (spot.status.equals("OCCUPIED")) {
                occupied++;
            }
        }

        double occupancy = (occupied * 100.0) / capacity;

        double avgProbes = (parkOperations == 0)
                ? 0
                : (double) totalProbes / parkOperations;

        System.out.println("\nParking Statistics");
        System.out.println("Occupancy: " +
                String.format("%.2f", occupancy) + "%");

        System.out.println("Average Probes: " +
                String.format("%.2f", avgProbes));
    }

    public static void main(String[] args) {

        ParkingLotSystem parking = new ParkingLotSystem();

        parking.parkVehicle("ABC-1234");
        parking.parkVehicle("ABC-1235");
        parking.parkVehicle("XYZ-9999");

        try {
            Thread.sleep(2000);
        } catch (Exception e) {}

        parking.exitVehicle("ABC-1234");

        parking.getStatistics();
    }
}
