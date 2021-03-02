package kurs;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GameWindow extends JFrame {
    public static final int CARS_COUNT = 4;
    public static final boolean CENTER_WINDOW = true;
    public static final boolean OUT_STAGES = false;
    public static final int STEP_WIDTH = 7;
    public static final int HEIGHT = 80;
    public static Race race;
    public static Car[] cars = new Car[CARS_COUNT];

    public static final GameWindow gameWindow = new GameWindow();
    private static final Image[] carImages = new Image[CARS_COUNT];

    public static void main(String[] args) {

        race = new Race(
                new Road(60),
                new Tunnel(),
                new Road(40));

        cars[0] = new Car("СИНИЙ", race, 30);
        cars[1] = new Car("ЖЕЛТЫЙ", race, 15);
        cars[2] = new Car("КРАСНЫЙ", race, 40);
        cars[3] = new Car("ЗЕЛЕНЫЙ", race, 20);

        try {
            carImages[0] = ImageIO.read(GameWindow.class.getResourceAsStream("blue.png"));
            carImages[1] = ImageIO.read(GameWindow.class.getResourceAsStream("yellow.png"));
            carImages[2] = ImageIO.read(GameWindow.class.getResourceAsStream("red.png"));
            carImages[3] = ImageIO.read(GameWindow.class.getResourceAsStream("green.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        gameWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gameWindow.setResizable(false);

        setRealSize(race.getLength() * STEP_WIDTH + 20 + 140, HEIGHT * CARS_COUNT + 40);

        if (CENTER_WINDOW)
            gameWindow.setLocationRelativeTo(null);

        GameField gameField = new GameField();

        gameWindow.add(gameField);
        gameWindow.setVisible(true);

        for (Car car: cars) {
            new Thread(car).start();
        }

        //гонка начнется, когда все участники подготовятся
        race.waitForReady();

        //вот тут сначала нужно дождаться, когда приедут все
        race.waitForFinish();
    }

    private static void setRealSize(int w, int h) {
        Dimension dimension = new Dimension();
        dimension.setSize(w, h);
        gameWindow.setPreferredSize(dimension);
        gameWindow.pack();
        int realW = gameWindow.getContentPane().getWidth();
        int realH = gameWindow.getContentPane().getHeight();
        int addW = dimension.width - realW;
        int addH = dimension.height - realH;
        dimension.width += addW;
        dimension.height += addH;
        gameWindow.setPreferredSize(dimension);
        gameWindow.pack();
    }

    private static void onRepaint(Graphics g) {
        int yMax = gameWindow.getHeight();
        int y35 = yMax - 50;
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, gameWindow.getWidth(), yMax);

        g.setColor(Color.BLUE);
        int x = 15;
        int x1 = x + race.getLength(0) * STEP_WIDTH + 140;
        g.drawLine(x, 10, x1, 10);
        g.drawLine(x, y35, x1, y35);
        g.drawLine(x1, 10, x1, 10 + HEIGHT + HEIGHT);
        g.drawLine(x, 10, x, y35);
        x = x1;
        x1 = x + race.getLength(1) * STEP_WIDTH - 140;
        g.drawLine(x, 10 + HEIGHT + HEIGHT, x1, 10 + HEIGHT + HEIGHT);
        g.drawLine(x, y35, x1, y35);
        g.drawLine(x1, 10, x1, 10 + HEIGHT + HEIGHT);
        int t1 = x - 140;
        int t2 = x1;
        x = x1;
        x1 = x + race.getLength(2) * STEP_WIDTH - 10 + 140;
        g.drawLine(x, 10, x1, 10);
        g.drawLine(x, y35, x1, y35);
        g.drawLine(x1, 10, x1, y35);

        for (int i = 0; i < CARS_COUNT; i++) {
            int xx = cars[i].getX() * STEP_WIDTH + 20;
            //в туннеле нам нужно подвинуть машины 1 и 2 вниз
            //прежде всего нужно определить, когда начинается тунель и когда он заканчивается
            boolean busy3 = cars[2].isInTunnel();
            boolean busy4 = cars[3].isInTunnel();
            int yy = cars[i].getY();
            if (i == 0 && cars[0].isInTunnel()) {
                if (busy3) yy += HEIGHT * 3;
                else yy += HEIGHT * 2;
            }
            if (i == 1 && cars[1].isInTunnel()) {
                if (busy3) yy += HEIGHT * 2;
                else yy += HEIGHT * 1;
            }
            //нужно знать, заехала ли третья и четвертая машина в тоннель
            g.drawImage(carImages[i], xx, yy, null);
        }
    }

    private static class GameField extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            onRepaint(g);
        }
    }
}
