import java.util.ArrayList;
import java.util.LinkedList;

/**
 * ISS
 */

public class ISS {

    /**
     * @param waitersNumber
     *      A kovetkezo problema kikuszobolesere:
     *      a hazaszallito urhajo fel masodpercenkent
     *      vette ki a varakozasi listabol az utasokat, de kozben gyorsabban jelentkeztek
     *      hazafele az utasok, ami miatt ujabb ures urhajo jott letre. Melyek tobb utast
     *      akartak hazaszallitani, mint amennyit kellett volna.
     *      A valtozot egyben csokkenti a hazaszallito urhajo, így nem jon letre elobb ures
     *      urhajo, mint kene.
     */

    private static int maxCapacity = 250;
    private int actualCapacity;
    private static int maxWeight = 2500;
    private int actualWeight;
    private int waitersNumber;

    private ArrayList<Gate> gates = new ArrayList<>();
    private LinkedList<Passenger> waiters = new LinkedList<>();

    public ISS() {
        actualCapacity = 0;
        waitersNumber = 0;
        actualWeight = 0;
        for (int i = 0; i < 5; i++) {
            Gate gate = new Gate(i + 1);
            gates.add(gate);
        }
    }


    public synchronized Passenger removeWaiter() {
        return waiters.removeFirst();
    }

    public synchronized int getWaitersNumber() {
        return waitersNumber;
    }

    public void addWaitersNumber(int add) {
        waitersNumber += add;
    }

    public synchronized int getActualCapacity() {
        return actualCapacity;
    }

    public static int getMaxCapacity() {
        return maxCapacity;
    }

    public static int getMaxWeight() {
        return maxWeight;
    }


    public synchronized ArrayList<Gate> getGates() {
        return gates;
    }

    public synchronized void addWaiter(Passenger passenger) {
        waiters.addLast(passenger);
    }

    public synchronized int waiterSize() {
        return waiters.size();
    }

    public synchronized Passenger firstWaiter() {
        return waiters.getFirst();
    }


    public synchronized int getActualWeight() {
        return actualWeight;
    }

    public synchronized void changeActualCapacity(int add) {
        actualCapacity += add;
    }

    public synchronized void changeActualWeight(int add) {
        actualWeight += add;
    }
}
