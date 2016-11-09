package com.YoungMoney;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

public class Main extends Application {

    static final int ANT_COUNT = 100;
    static final int WIDTH = 800;
    static final int HEIGHT = 600;

    ArrayList<Ant> ants;

    static ArrayList<Ant> createAnts() {
        ArrayList<Ant> ants = new ArrayList<>();
        for (int i = 0; i < ANT_COUNT; i++) {
            Random r = new Random();
            Ant a = new Ant(r.nextInt(WIDTH), r.nextInt(HEIGHT));
            ants.add(a);
        }
        return ants;
    }

    void drawAnts(GraphicsContext context) {
        context.clearRect(0, 0, WIDTH, HEIGHT);
        for (Ant ant : ants) {
            context.setFill(Color.BLACK);
            context.fillOval(ant.x, ant.y, 5, 5);
        }
    }

    static double randomStep() {
        return Math.random() * 2 - 1;
    }

    Ant moveAnt (Ant ant) {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ant.x += randomStep();
        ant.y += randomStep();
        return ant;
    }

    void moveAnts() {
        ants = ants.parallelStream()
                .map(this::moveAnt)
                .map(this::aggravateAnt)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    long lastTimeStamp = 0;

    int fps(long currentTimeStamp) {
        double diff = currentTimeStamp - lastTimeStamp;
        diff = diff / 1000000000;
        return (int) (1 / diff);
    }

    Ant aggravateAnt(Ant ant) {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayList<Ant> list =
                ants.parallelStream()
                .filter(nearAnt -> {
                    return (Math.abs(ant.x - nearAnt.x) < 10) &&
                            (Math.abs(ant.y - nearAnt.y) <10);
                })
                .collect(Collectors.toCollection(ArrayList::new));
        ant.color = (list.size() != 1) ? Color.RED : Color.BLACK;
        return ant;
    }

    void updateAnts() {
        ants = ants.parallelStream()
                .map(this::moveAnt)
                .map(this::aggravateAnt)
                .collect(Collectors.toCollection(ArrayList<Ant>::new));
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setTitle("Ants");
        primaryStage.setScene(scene);
        primaryStage.show();

        Canvas canvas = (Canvas) scene.lookup("#canvas");
        Label fpsLabel = (Label) scene.lookup("#fps");
        GraphicsContext context = canvas.getGraphicsContext2D();
        ants = createAnts();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateAnts();
                drawAnts(context);
                fpsLabel.setText(fps(now) + "");
                lastTimeStamp = now;
            }
        };
        timer.start();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
