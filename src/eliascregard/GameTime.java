package eliascregard;

public class GameTime {

    public static final int NANO_TIME_CONVERTER_CONSTANT = 1_000_000_000;

    long lastTime;
    long currentTime;

    public GameTime() {
        this.lastTime = System.nanoTime();
        this.currentTime = lastTime;
    }

    public long getLastTime() {
        return lastTime;
    }
    public long getCurrentTime() {
        this.currentTime = System.nanoTime();
        this.lastTime = currentTime;
        return currentTime;
    }

    public double getNanoDeltaTime() {
        this.currentTime = System.nanoTime();
        long deltaT = currentTime - lastTime;
        this.lastTime = currentTime;
        return deltaT;
    }
    public double getDeltaTime() {
        return this.getNanoDeltaTime() / NANO_TIME_CONVERTER_CONSTANT;
    }

    public int getFPS(double deltaTime) {
        if (deltaTime > 0) {
            return (int) (1 / deltaTime);
        }
        return 0;
    }
}