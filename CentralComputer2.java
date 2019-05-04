import java.util.Random;

/**
 * Hazautazasokat kezelo szal.
 */

public class CentralComputer2 extends Thread {

    private ISS iss;
    private CentralComputer centralComputer;

    public CentralComputer2(ISS iss, CentralComputer centralComputer) {
        this.centralComputer = centralComputer;
        this.iss = iss;
    }

    /**
     * Hogyha a varakozok listaja eleri az otvenet, akkor egy ures urhajot indit,
     * ami rogton dokkol is, ha megfelel a felteteleknek.
     *
     * Optimilazacios reszfeladat:
     *      megnezi, hogy az elso teli urhajo kepes e dokkolni és hogy tobben vannak-e
     *      rajta, mint ahanyan haza akarnak menni.
     *      Ha ez teljesul, akkor dokkoltatja az elso teli urhajot, ha nem, akkor
     *      ugyanugy az ures dokkol hazafale, mivel, ha az elso ugyse tud dokkolni
     *      feleslegesen varakoztatna meg a haza menni akarokat.
     */

    @Override
    public void run() {
        Random rand = new Random();

        while (true) {

            if (iss.getWaitersNumber() >= 50) {
                System.out.println("C2 AKTIV");

                SpaceShip transportHome = new SpaceShip(centralComputer.addSpaceShipId(), rand.nextInt(900) + 100, iss.getWaitersNumber(), 0, centralComputer, iss);
                transportHome.start();

                //egyben csokkenti a varakozok szamat, feltetelezve, hogy ugyis mindegyik beszall.
                iss.addWaitersNumber(-transportHome.getCapacity());
                if (centralComputer.isOptimization()) {

                    if (centralComputer.freeSpaces() >= centralComputer.getFirstSpaceShipPassengerNumber() && iss.getWaitersNumber() < centralComputer.getFirstSpaceShipPassengerNumber()) {
                        System.out.println("OPTIMALIZÁCIÓ");
                        centralComputer.writeToFile("OPTIMALIZÁCIÓ");
                        if (centralComputer.getSpaceShipsSize() > 0) {
                            centralComputer.startDocking();
                        }
                    }
                }

                if (centralComputer.ableToDock(transportHome)) {
                    synchronized (transportHome) {
                        centralComputer.lookingForGate(transportHome);
                        transportHome.setState(SpaceShip.State.DOCKING);
                        transportHome.notify();
                    }
                }

                try {
                    sleep(rand.nextInt(5000) + 5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}