package com.codenjoy.dojo.snake.client;

import com.codenjoy.dojo.snake.client.colored.Ansi;
import com.codenjoy.dojo.snake.client.colored.Attribute;
import com.codenjoy.dojo.snake.client.colored.Colored;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Lee {

    private final int EMPTY = 0;
    private final int START = 1;
    private final int OBSTACLE = -9;
    private final int width;
    private final int height;
    private final int[][] board;

    public Lee(int width, int height) {
        this.width = width;
        this.height = height;
        this.board = new int[height][width];
    }

    private int get(int x, int y) {
        return board[y][x];
    }

    private void set(int x, int y, int value) {
        board[y][x] = value;
    }

    private int get(Pointer pointer) {
        return get(pointer.x(), pointer.y());
    }

    private void set(Pointer pointer, int value) {
        set(pointer.x(), pointer.y(), value);
    }

    private boolean isOnBoard(Pointer pointer) {
        return pointer.x() >= 0 && pointer.x() < width &&
                pointer.y() >= 0 && pointer.y() < height;
    }

    private boolean isUnvisited(Pointer pointer) {
        return get(pointer) == EMPTY;
    }

    private Supplier<Stream<Pointer>> deltas() {
        return () -> Stream.of(
                Pointer.of(-1, 0), // offset, not a point
                Pointer.of(1, 0),  // offset, not a point
                Pointer.of(0, 1),  // offset, not a point
                Pointer.of(0, -1)  // offset, not a point
        );
    }

    private Stream<Pointer> neighbours(Pointer pointer) {
        return deltas().get()
                .map(d -> pointer.move(d.x(), d.y()))
                .filter(this::isOnBoard);
    }

    private Stream<Pointer> neighboursUnvisited(Pointer pointer) {
        return neighbours(pointer)
                .filter(this::isUnvisited);
    }

    private List<Pointer> neighboursByValue(Pointer pointer, int value) {
        return neighbours(pointer)
                .filter(p -> get(p) == value)
                .toList();
    }

    private void initializeBoard(List<Pointer> obstacles) {
        obstacles.forEach(p -> set(p, OBSTACLE));
    }

    public Optional<List<Pointer>> trace(Pointer start, Pointer finish, List<Pointer> obstacles) {
        // 1. initialization
        initializeBoard(obstacles);
        int[] counter = {START}; // HEAP due to lambda
        set(start, counter[0]);
        counter[0]++;
        boolean found = false;
        // 2. fill the board
        for (Set<Pointer> curr = new HashSet<>(Set.of(start)); !(found || curr.isEmpty()); counter[0]++) {
            Set<Pointer> next = curr.stream()
                    .flatMap(this::neighboursUnvisited)
                    .collect(Collectors.toSet());

            next.forEach(p -> set(p, counter[0]));
            found = next.contains(finish);
            curr = next;
        }
        // 3. backtrace (reconstruct the path)
        if (!found) return Optional.empty();
//        if (!found) return null;
        LinkedList<Pointer> path = new LinkedList<>();
        path.add(finish);
        counter[0]--;

        Pointer curr = finish;
        while (counter[0] > START) {
            counter[0]--;
            Pointer prev = neighboursByValue(curr, counter[0]).get(0);
            if (prev == start) break;
            path.addFirst(prev);
            curr = prev;
        }
        return Optional.of(path);
    }

    public String cellFormatted(Pointer p, List<Pointer> path) {
        int value = get(p);
        String valueF = String.format("%3d", value);

        if (value == OBSTACLE) {
            Attribute a = new Attribute(Ansi.ColorFont.BLUE);
            return Colored.build(" XX", a);
        }

        if (path.isEmpty()) return valueF;

        if (path.contains(p)) {
            Attribute a = new Attribute(Ansi.ColorFont.RED);
            return Colored.build(valueF, a);
        }
        return valueF;//" --";
    }

    public String boardFormatted(List<Pointer> path) {
        return IntStream.range(0, height).mapToObj(y ->
                        IntStream.range(0, width)
                                .mapToObj(x -> Pointer.of(x, y))
                                .map(p -> cellFormatted(p, path))
                                .collect(Collectors.joining())
                )
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String toString() {
        return boardFormatted(Collections.emptyList());
    }
}
