package kurs;

public class Road extends Stage {

    public Road(int length) {
        this.length = length;
        this.description = "Дорога " + length + " метров";
    }

    @Override
    public void go(Car car) {
        try {
            //сообщение о начале этапа
            if (GameWindow.OUT_STAGES)
                System.out.println(car.getStartStageName(description));
            //ждем время
            long time = car.calcTime(length) / length;
            for (int i = 0; i < length; i++) {
                Thread.sleep(time);
                race.stepAndRepaintWithLock(car);
            }
            //сообщение об окончании этапа с блокировкой для проверки победителя
            race.checkWinner(car, isLast, car.getFinishStageName(description));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}