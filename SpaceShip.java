import java.util.LinkedList;

public class SpaceShip extends Thread {

    public enum State {
        WAIT_FOR_DOCKING, DOCKING
    }

    private int id;
    private int weight;
    private int capacity;
    private int passengersNumber;
    private State state;
    private CentralComputer centralComputer;
    private ISS iss;
    private Gate gate;

    private LinkedList<Passenger> passengers = new LinkedList<>();

    public SpaceShip(int id, int suly, int capacity, int passengersNumber, CentralComputer centralComputer, ISS iss) {

        this.id = id;
        this.passengersNumber = passengersNumber;
        this.centralComputer = centralComputer;
        this.iss = iss;
        this.weight = suly;
        this.capacity = capacity;
        this.state = State.WAIT_FOR_DOCKING;
        for (int i = 0; i < passengersNumber; i++) {
            Passenger utas = new Passenger(i, iss, this, centralComputer);
            utas.start();
            passengers.addLast(utas);
        }

    }

    public int getSpaceShipId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    public synchronized void getGate(Gate gate) {
        this.gate = gate;
    }

    public synchronized int getWeight() {
        return weight;
    }

    public void setState(State state) {
        this.state = state;
    }

    public synchronized int getPassengersNumbers() {
        return passengersNumber;
    }



    private synchronized void transportHome() {

        //az iss sulyahoz hozzaadja a sajatjat
        iss.changeActualWeight(this.weight);
        for (int i = 0; i < capacity; i++) {

            try {
                sleep(500);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (iss.waiterSize() == 0) break;

            //felveszi az utast, majd ertesiti
            synchronized (iss.firstWaiter()) {
                passengers.addLast(iss.firstWaiter());
                iss.firstWaiter().setState(Passenger.State.SPACE_SHIP);
                iss.firstWaiter().notify();
                System.out.println("Utas " + iss.firstWaiter().getPassengerId() + " dokkolt");
                centralComputer.writeToFile("Utas " + iss.firstWaiter().getPassengerId() + " dokkolt hazafele");
                iss.removeWaiter();
            }

            iss.changeActualCapacity(-1);

        }
        //szabadda teszi a kaput es visszaallitja a sulyt.
        gate.setUsed(false);
        iss.changeActualWeight(-this.weight);

        System.out.println("Urhajo " + id + " dokkolt, elindult a Földre");
        System.out.println("Az ISS új súlya: " + iss.getActualWeight());
        System.out.println("Az utasok száma: " + iss.getActualCapacity());

        centralComputer.writeToFile("Urhajo " + id + " dokkolt, elindult a Földre");
        centralComputer.writeToFile("Az ISS új súlya: " + iss.getActualWeight());
        centralComputer.writeToFile("Az utasok száma: " + iss.getActualCapacity());

    }

    private synchronized void docking() {


        //mivel mindegyik utas kiszall egyben növeli az iss-n tartozkodo utasok szamat
        iss.changeActualWeight(this.weight);
        iss.changeActualCapacity(this.passengersNumber);

        for (int i = 0; i < passengersNumber; i++) {

            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (passengers.getFirst()) {
                passengers.getFirst().setState(Passenger.State.ISS);
                passengers.getFirst().notify();
                passengers.removeFirst();
            }
        }
        gate.setUsed(false);
        iss.changeActualWeight(-this.weight);

        System.out.println("Urhajo " + id + " dokkolt az ISS-en");
        System.out.println("Az ISS új súlya: " + iss.getActualWeight());
        System.out.println("Az utasok száma: " + iss.getActualCapacity());

        centralComputer.writeToFile("Urhajo " + id + " dokkolt");
        centralComputer.writeToFile("Az ISS új súlya: " + iss.getActualWeight());
        centralComputer.writeToFile("Az utasok száma: " + iss.getActualCapacity());

    }

    @Override
    public void run() {

        System.out.println("Urhajo " + id + " elkezdett futni");
        centralComputer.writeToFile("Urhajo " + id + " elkezdett futni");
        System.out.println("Urhajo " + id + " dokkolasra var " + passengersNumber + " utassal, " + weight + " sullyal");
        centralComputer.writeToFile("Urhajo " + id + " dokkolasra var " + passengersNumber + " utassal, " + weight + " sullyal");

        //Dokkolási engedélyre vár
        while (state == State.WAIT_FOR_DOCKING) {
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Urhajo " + id + " elkezdett dokkolni a " + gate.getId() + ". kapunál");
        centralComputer.writeToFile("Urhajo " + id + " elkezdett dokkolni a " + gate.getId() + ". kapunál");

        //dokkol az utasszám alapján (Földre vagy ISS-re)
        if (passengersNumber == 0) {
            transportHome();
        } else {
            docking();
        }

        //Dokkolás után még fut
        try {
            sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Urhajo " + id + " befejezte futasat");
        centralComputer.writeToFile("Urhajo " + id + " befejezte futasat");

    }
}
