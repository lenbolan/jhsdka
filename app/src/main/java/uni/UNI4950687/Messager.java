package uni.UNI4950687;

import java.util.Observable;

public class Messager extends Observable {

    private static Messager messager = null;

    private Messager() {

    }

    public static Messager getInstance() {
        if (messager == null) {
            synchronized (Messager.class) {
                if (messager == null) {
                    messager = new Messager();
                }
            }
        }
        return messager;
    }

    public void postMessage(String eventType) {
        setChanged();
        notifyObservers(eventType);
    }

}
