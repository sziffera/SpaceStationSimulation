
public class Main {
    public static void main(String[] args) {
        ISS iss = new ISS();
        CentralComputer centralComputer = new CentralComputer(iss,true);
        centralComputer.start();
    }
}
