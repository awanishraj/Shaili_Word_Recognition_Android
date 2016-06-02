package in.ac.iitm.shaili;

/**
 * Created by Awanish Raj on 20/05/16.
 */
public class TimingUtil {
    private long lastTime;

    public TimingUtil() {
        init();
    }

    public void init() {
        lastTime = getNowTime();
    }

    public void pitch(String tag) {
        System.out.println(tag + " : " + (getNowTime() - lastTime) + "us");
        lastTime = getNowTime();
    }


    private long getNowTime() {
        return System.nanoTime()/1000;
    }
}
