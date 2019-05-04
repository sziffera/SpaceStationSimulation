import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Random;

/**
 * Központi számítógép osztály.
 * Dokkoltat, kaput keres, ellenőrzi a feltételeket.
 * Teli űrhajókat hoz létre
 */

public class CentralComputer extends Thread {

    private LinkedList<SpaceShip> spaceShips = new LinkedList<>();
    private ISS iss;
    private static String destination = "issnaplo.txt";
    private boolean optimization; //optimalizációs flag
    private volatile int spaceShipId;
    private FileWriter fw;
    private BufferedWriter bufferedWriter;

    public CentralComputer(ISS iss, boolean optimization) {

        spaceShipId = 0;
        this.optimization = optimization;
        this.iss = iss;
        try {
            fw = new FileWriter(destination);
            bufferedWriter = new BufferedWriter(fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * fájlba írást végző metődus
     * @param data
     *          mit írjon ki
     */

    public synchronized void writeToFile(String data) {

        try {
            bufferedWriter.write(data);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return spaceShipId
     *          az űrhajók azonosítóihoz
     */

    public synchronized int addSpaceShipId() {
        spaceShipId++;
        return spaceShipId;
    }

    /**
     * Az optimalizációs részfeladathoz
     * @return
     */

    public synchronized int getFirstSpaceShipPassengerNumber() {
        return spaceShips.getFirst().getPassengersNumbers();
    }


    public synchronized boolean isOptimization() {
        return optimization;
    }

    public synchronized int freeSpaces() {
        return iss.getMaxCapacity() - iss.getActualCapacity();
    }


    public synchronized int freeWeight() {
        return iss.getMaxWeight() - iss.getActualWeight();
    }


    public int getSpaceShipsSize(){
        return spaceShips.size();
    }

    /**
     * Megnezi, hogy a dokkolni kivano urhajo megfelel-e a felteteleknek
     * @param spaceShip
     *      dokkolni akaró úrhajó
     * @return
     */

    public boolean ableToDock(SpaceShip spaceShip) {

        if (spaceShip.getPassengersNumbers() <= freeSpaces() && spaceShip.getWeight() <= freeWeight())
            return true;
        return false;

    }

    /**
     * Szabad kaput keres az urhajonak, a hazaszallito urhajok reszere.
     * Mivel a hazautazok elonyben vannak, ezert van vegtelen while-ban.
     *
     * Ha talalt kaput, az urhajonak atadja, hogy a dokkolas vegeztevel
     * szabadda tudja tenni.
     *
     * @param spaceShip
     *      dokkolni kivano urhajo
     */

    public synchronized void lookingForGate(SpaceShip spaceShip) {

        while (true) {
            for (Gate g : iss.getGates()) {
                if (!g.isUsed()) {
                    g.setUsed(true);
                    spaceShip.getGate(g);
                    return;
                }
            }
        }
    }


    public synchronized boolean lookingForGate2(SpaceShip spaceShip) {

        for (Gate g : iss.getGates()) {
            if (!g.isUsed()) {
                g.setUsed(true);
                spaceShip.getGate(g);
                return true;
            }
        }
        return false;
    }

    /**
     * Dokkolast vegzo metodus.
     * Ha van szabad kapu es az urhajo megfelel akkor a spaceShips lista elso
     * urhajojat ertesiti, aki megkezdi a dokkolast.
     * Majd eltavoltija a listabol
     */

    public void startDocking() {
        if (ableToDock(spaceShips.getFirst()) && lookingForGate2(spaceShips.getFirst())) {
            synchronized (spaceShips.getFirst()) {
                spaceShips.getFirst().setState(SpaceShip.State.DOCKING);
                spaceShips.getFirst().notify();
                spaceShips.removeFirst();
            }
        }
    }

    /**
     * A teli urhajokat inditja el a Foldrol
     */

    @Override
    public void run() {

        //fajlba iras
        DateFormat dateTimeInstance = SimpleDateFormat.getDateTimeInstance();
        writeToFile(dateTimeInstance.format(Calendar.getInstance().getTime()));
        System.out.println("A kozponti szamitogep elkezdett futni");
        writeToFile("A központi számítógép elkezdett futni");

        //elinditja a hazautazast vegzo szalat.
        CentralComputer2 centralComputer2 = new CentralComputer2(iss, this);
        centralComputer2.start();

        Random rand = new Random();

        while (true) {

            int passengerNumber = rand.nextInt(90) + 10;
            int weight = rand.nextInt(900) + 100;

            if (spaceShips.size() <= 10) {
                SpaceShip spaceShip = new SpaceShip(spaceShipId++, weight, passengerNumber, passengerNumber, this, iss);
                spaceShip.start();
                spaceShips.addLast(spaceShip);
            }

            startDocking();

            try {
                sleep(rand.nextInt(5000) + 5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
