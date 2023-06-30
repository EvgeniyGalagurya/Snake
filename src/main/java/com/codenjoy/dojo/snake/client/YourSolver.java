package com.codenjoy.dojo.snake.client;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.RandomDice;
import com.codenjoy.dojo.snake.model.Elements;


import java.io.*;
import java.util.*;

/**
 * User: your name
 */
public class YourSolver implements Solver<Board> {

    private Dice dice;
    private Board board;

    public YourSolver(Dice dice) {
        this.dice = dice;
    }

    @Override
    public String get(Board board) {
        this.board = board;
        System.out.println(board.toString());

        List<Point> apples = board.getApples();
        Point appleAt = apples.get(0);
        Point headAt = board.getHead();

        List<Point> barriers = board.getStones();
        Point stone = barriers.get(0);

        List<Point> snake = board.getSnake();
        Point tail = snake.get(snake.size() - 1);

        Lee lee = new Lee(15, 15);
        Lee leeStone = new Lee(15, 15);
        Lee leeSnake = new Lee(15, 15);

        Pointer from = Pointer.of(headAt.getX(), headAt.getY());
        Pointer to = Pointer.of(appleAt.getX(), appleAt.getY());
        Pointer toStone = Pointer.of(stone.getX(), stone.getY());
        Pointer toTail = Pointer.of(tail.getX(), tail.getY());

        List<Pointer> obstaclesSnake = new ArrayList<>();
        for (int i = 0; i < snake.size(); i++) {
            int a = snake.get(i).getX();
            int b = snake.get(i).getY();
            List<Pointer> obstacles2 = List.of(Pointer.of(a, b));
            obstaclesSnake.addAll(obstacles2);
        }
        List<Pointer> obstaclesWalls = new ArrayList<>(List.of(
                Pointer.of(0, 0),
                Pointer.of(0, 1),
                Pointer.of(0, 2),
                Pointer.of(0, 3),
                Pointer.of(0, 4),
                Pointer.of(0, 5),
                Pointer.of(0, 6),
                Pointer.of(0, 7),
                Pointer.of(0, 8),
                Pointer.of(0, 9),
                Pointer.of(0, 10),
                Pointer.of(0, 11),
                Pointer.of(0, 12),
                Pointer.of(0, 13),
                Pointer.of(0, 14),

                Pointer.of(14, 0),
                Pointer.of(14, 1),
                Pointer.of(14, 2),
                Pointer.of(14, 3),
                Pointer.of(14, 4),
                Pointer.of(14, 5),
                Pointer.of(14, 6),
                Pointer.of(14, 7),
                Pointer.of(14, 8),
                Pointer.of(14, 9),
                Pointer.of(14, 10),
                Pointer.of(14, 11),
                Pointer.of(14, 12),
                Pointer.of(14, 13),
                Pointer.of(14, 14),

                Pointer.of(0, 0),
                Pointer.of(1, 0),
                Pointer.of(2, 0),
                Pointer.of(3, 0),
                Pointer.of(4, 0),
                Pointer.of(5, 0),
                Pointer.of(6, 0),
                Pointer.of(7, 0),
                Pointer.of(8, 0),
                Pointer.of(9, 0),
                Pointer.of(10, 0),
                Pointer.of(11, 0),
                Pointer.of(12, 0),
                Pointer.of(13, 0),
                Pointer.of(14, 0),

                Pointer.of(0, 14),
                Pointer.of(1, 14),
                Pointer.of(2, 14),
                Pointer.of(3, 14),
                Pointer.of(4, 14),
                Pointer.of(5, 14),
                Pointer.of(6, 14),
                Pointer.of(7, 14),
                Pointer.of(8, 14),
                Pointer.of(9, 14),
                Pointer.of(10, 14),
                Pointer.of(11, 14),
                Pointer.of(12, 14),
                Pointer.of(13, 14),
                Pointer.of(14, 14)
        ));
        List<Pointer> obstaclesStone = new ArrayList<>(List.of(
                Pointer.of(stone.getX(), stone.getY())));

        List<Pointer> obstaclesAll = new ArrayList<>();
        obstaclesAll.addAll(obstaclesWalls);
        obstaclesAll.addAll(obstaclesStone);
        obstaclesAll.addAll(obstaclesSnake);

        List<Pointer> obstaclesWithOutStone = new ArrayList<>();
        obstaclesAll.addAll(obstaclesWalls);
        obstaclesAll.addAll(obstaclesSnake);

        Optional<List<Pointer>> trace = lee.trace(from, to, obstaclesAll);
        Optional<List<Pointer>> traceToStone = leeStone.trace(from, toStone, obstaclesWithOutStone);
        Optional<List<Pointer>> traceToTailSnake = leeSnake.trace(from, toTail, obstaclesWithOutStone);

        try {
            if (trace.isPresent()) {
                if (headAt.getX() < trace.get().get(1).x()) return Direction.RIGHT.toString();
                if (headAt.getY() > trace.get().get(1).y()) return Direction.DOWN.toString();
                if (headAt.getY() < trace.get().get(1).y()) return Direction.UP.toString();
                if (headAt.getX() > trace.get().get(1).x()) return Direction.LEFT.toString();
            } else if (traceToStone.isPresent()) {
                if (headAt.getX() < traceToStone.get().get(1).x()) return Direction.RIGHT.toString();
                if (headAt.getY() > traceToStone.get().get(1).y()) return Direction.DOWN.toString();
                if (headAt.getY() < traceToStone.get().get(1).y()) return Direction.UP.toString();
                if (headAt.getX() > traceToStone.get().get(1).x()) return Direction.LEFT.toString();
            } else {
                if (headAt.getX() < traceToTailSnake.get().get(1).x()) return Direction.RIGHT.toString();
                if (headAt.getY() > traceToTailSnake.get().get(1).y()) return Direction.DOWN.toString();
                if (headAt.getY() < traceToTailSnake.get().get(1).y()) return Direction.UP.toString();
                if (headAt.getX() > traceToTailSnake.get().get(1).x()) return Direction.LEFT.toString();
            }
        } catch (Exception e) {
        }
        if (headAt.getX() == 13) return Direction.LEFT.toString();
        if (headAt.getX() == 1) return Direction.RIGHT.toString();
        if (headAt.getY() ==13) return Direction.DOWN.toString();
        if (headAt.getY() ==1) return Direction.UP.toString();
        return Direction.UP.toString();
    }

    public static void main(String[] args) {
        String url = "http://64.226.126.93/codenjoy-contest/board/player/0ggc7e7fqpevds953d8n?code=3087155049296447096";
        WebSocketRunner.runClient(
                url,
                new YourSolver(new RandomDice()),
                new Board());
    }

}
