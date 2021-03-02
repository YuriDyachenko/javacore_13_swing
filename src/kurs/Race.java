package kurs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static kurs.GameWindow.CARS_COUNT;
import static kurs.GameWindow.gameWindow;

public class Race {
    private final ArrayList<Stage> stages;

    //этот объект только для того, чтобы в главном потоке вывести "гонка началась"
    //когда все участники будут готовы
    private final CountDownLatch latchForStart = new CountDownLatch(CARS_COUNT);

    //этот объект для того, чтобы в главном потоке вывести "гонка закончилась"
    private  final CountDownLatch latchForFinish = new CountDownLatch(CARS_COUNT);

    //а вот этот объект уже для реального старта всех машин
    //когда все будут готовы и, надеюсь, выведется сообщение "гонка началась"
    private  final CyclicBarrier barrierForStart = new CyclicBarrier(CARS_COUNT);

    //победителя будем проверять и сохранять в этой переменной
    //а так как это критический блок кода, то используем блокировку
    private Car winner = null;
    private final Lock winnerLock = new ReentrantLock();

    public Race(Stage... stages) {
        this.stages = new ArrayList<>(Arrays.asList(stages));

        //пусть каждый этап гонки сам знает, последний он или нет
        //и имеет ссылку на саму гонку
        int size = this.stages.size();
        for (int i = 0; i < size; i++) {
            this.stages.get(i).setLast(i == (size - 1));
            this.stages.get(i).setRace(this);
        }

        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
    }

    public int countOfStages() {
        return stages.size();
    }

    public int getLength(int stageNum) {
        return stages.get(stageNum).length;
    }

    public int getLength() {
        int res = 0;
        for (Stage stage: stages) {
            res += stage.length;
        }
        return res;
    }

    public void latchForStartCountDown() {
        latchForStart.countDown();
    }

    public void latchForFinishCountDown() {
        latchForFinish.countDown();
    }

    public void waitForReady() {
        //ждем, пока все участники не будут готовы, чтобы вывести сообщение о старте
        try {
            latchForStart.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");
    }

    public void waitForStart() {
        //ждем тоже, пока все участники не будут готовы
        //но это уже чтобы стартовать все этапы
        try {
            barrierForStart.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    public void waitForFinish() {
        //ждем, пока все участники не завершат гонку
        try {
            latchForFinish.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");
    }

    public void checkWinner(Car car, boolean isLast, String msg) {
        //в блокировке критического блока кода
        //выводим сообщение о завершении этапа
        //и если он последний и победитель еще не определен, фиксируем и выводим победителя
        try {
            winnerLock.lock();
            if (GameWindow.OUT_STAGES)
                System.out.println(msg);
            if (isLast && winner == null) {
                winner = car;
                System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>>" + car.getName() + " - ПОБЕДИТЕЛЬ!!!");
            }
        } finally {
            winnerLock.unlock();
        }
    }

    public void goAllStages(Car car) {
        //просто проходим все этапы
        for (Stage stage : stages) {
            stage.go(car);
        }
    }

    public void stepAndRepaintWithLock(Car car) {
        try {
            //winnerLock.lock();
            car.stepX();
            gameWindow.repaint();
        } finally {
            //winnerLock.unlock();
        }
    }
}
