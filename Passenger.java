import java.util.Random;

public class Passenger extends Thread {

    public enum State {
        SPACE_SHIP, ISS, WANT_TO_GO;
    }

    private int id;
    private SpaceShip spaceShip;
    private CentralComputer centralComputer;
    private State state;
    private ISS iss;


    public Passenger(int id, ISS iss, SpaceShip spaceShip, CentralComputer centralComputer) {
        this.centralComputer = centralComputer;
        this.iss = iss;
        this.spaceShip = spaceShip;
        this.id = id;
        state = State.SPACE_SHIP;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getPassengerId(){
        return id;
    }

    @Override
    public void run() {
        Random rand = new Random();

        //varakozasi allapot, amig az urhajon van odafele
        synchronized (this) {
            while (state == State.SPACE_SHIP) {
                try {
                    this.wait();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Utas " + id + " dokkolt az " + spaceShip.getSpaceShipId() + " Urhajóról");
            centralComputer.writeToFile("Utas " + id + " dokkolt az " + spaceShip.getSpaceShipId() + " Urhajóról");

        }

        //ISS allapot
        try {
            sleep(rand.nextInt(40000) + 20000);
            state = State.WANT_TO_GO;
            System.out.println("Utas " + id + " vissza kíván térni a Földre");
            centralComputer.writeToFile("Utas " + id + " vissza kíván térni a Földre");
            iss.addWaiter(this);
            iss.addWaitersNumber(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //haza akar menni, var a dokkolasra
        synchronized (this) {
            while (state == State.WANT_TO_GO) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        //mar dokkolt hazafele, fut mig a hazaszallito urhajo meg nem szunik
        try {
            spaceShip.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }

    }
}
