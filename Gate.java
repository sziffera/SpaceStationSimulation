
public class Gate {

    private int id;
    private boolean used;

    public Gate(int id){
        this.id = id;
        used = false;
    }

    public int getId() {
        return id;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
