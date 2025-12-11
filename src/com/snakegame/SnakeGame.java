package com.snakegame;

public class SnakeGame {
    public static void main(String[] args) {
        // 1. Connect to Database first
        GameDatabase.initialize();
        
        // 2. Launch the Window
        new GameFrame();
    }
}