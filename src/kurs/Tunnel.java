package kurs;

import java.util.concurrent.Semaphore;
import static kurs.GameWindow.CARS_COUNT;

public class Tunnel extends Stage {
    //пропускная способность - половина всех машин
    Semaphore smp = new Semaphore(CARS_COUNT / 2);

    public Tunnel() {
        this.length = 80;
        this.description = "Тоннель " + length + " метров";
    }

    @Override
    public void go(Car car) {
        try {
            try {
                //сообщение об ожидании
                System.out.println(car.getWaitStageName(description));
                //проехать может только 2 машины сразу, ждем "место"
                //захватиываем туннель, если осободился/свободен
                smp.acquire();

                car.setInTunnel(true);

                //сообщение о начале этапа
                if (GameWindow.OUT_STAGES)
                    System.out.println(car.getStartStageName(description));
                //ждем время
                long time = car.calcTime(length) / length;
                for (int i = 0; i < length; i++) {
                    Thread.sleep(time);
                    race.stepAndRepaintWithLock(car);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {

                car.setInTunnel(false);

                //освобождаем туннель
                smp.release();
                //сообщение об окончании этапа с блокировкой для проверки победителя
                race.checkWinner(car, isLast, car.getFinishStageName(description));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
