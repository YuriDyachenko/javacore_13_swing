package kurs;

public class Car implements Runnable {
    private static int CARS_COUNT;
    private final Race race;
    private final int speed;
    private final String name;
    private final int timeForReady;
    private int x;
    private final int y;
    private boolean inTunnel = false;

    public boolean isInTunnel() {
        return inTunnel;
    }

    public void setInTunnel(boolean inTunnel) {
        this.inTunnel = inTunnel;
    }

    public Car(String name, Race race, int speed) {
        this.race = race;
        this.speed = speed;
        timeForReady = 500 + (int) (Math.random() * 800);
        CARS_COUNT++;
        this.name = " " + name;
        y = (CARS_COUNT - 1) * GameWindow.HEIGHT + 20;
        x = 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void stepX() {
        x++;
    }

    public String getName() {
        return name;
    }

    public long calcTime(int length) {
        return (length / speed * 1000L);
    }

    public String getStartStageName(String stageName) {
        return name + " начал этап: " + stageName;
    }

    public String getFinishStageName(String stageName) {
        return name + " закончил этап: " + stageName;
    }

    public String getWaitStageName(String stageName) {
        return name + " готовится к этапу (ждет): " + stageName;
    }

    @Override
    public void run() {
        try {
            System.out.println(this.name + " готовится");
            Thread.sleep(timeForReady);
            System.out.println(this.name + " готов");

            //сообщаем, что этот участник готов, уменьшая счетчик
            race.latchForStartCountDown();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //ждем, пока все участники не будут готовы
        race.waitForStart();

        //сама гонка по всем участкам
        //там же определится и выведется победитель
        race.goAllStages(this);

        //сообщаем, что этот участник завершил гонку, уменьшая счетчик
        race.latchForFinishCountDown();
    }
}