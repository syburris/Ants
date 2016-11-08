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
        ant.x += randomStep();
        ant.y += randomStep();
        return ant;
    }

    void moveAnts() {
        ants = ants.stream()
                .map(this::moveAnt)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    long lastTimeStamp = 0;

    int fps(long currentTimeStamp) {
        double diff = currentTimeStamp - lastTimeStamp;
        diff = diff / 1000000000;
        return (int) (1 / diff);
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
                moveAnts();
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
