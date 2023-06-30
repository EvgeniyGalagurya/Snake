package com.codenjoy.dojo.snake.client;

public record Pointer(int x, int y) {

    public static Pointer of(int x, int y) {
        return new Pointer(x, y);
    }

    @Override
    public String toString() {
        return String.format("[%2d, %2d]", x, y);
    }

    public Pointer move(int dx, int dy) {
        return new Pointer(x + dx, y + dy);
    }

}
