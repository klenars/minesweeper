package com.javarush.games.minesweeper;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private int countFlags;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private boolean isGameStopped;
    private int countClosedTiles = SIDE * SIDE;
    private int score;

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        if (isGameStopped){
           restart();
        }
        else {
            openTile(x, y);
        }
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x, y);
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                    setCellValue(x, y, "");

                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.ORANGE);
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }

    private void countMineNeighbors(){
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                GameObject gameObject = gameField[y][x];
                if (!gameObject.isMine){
                    for (GameObject neig : getNeighbors(gameObject)){
                        if (neig.isMine){
                            gameObject.countMineNeighbors++;
                        }
                    }
                }
            }
        }
    }

    private void openTile(int x, int y){

        GameObject gameObject = gameField[y][x];

        if (gameObject.isOpen || gameObject.isFlag || isGameStopped){
            return;
        }

        gameObject.isOpen = true;
        countClosedTiles--;

        if (gameObject.isMine){
            //setCellValue(x, y, MINE);
            setCellValueEx(x, y, Color.RED, MINE);
            gameOver();
        }
        else {
            setCellColor(x, y, Color.WHITE);

            if (gameObject.countMineNeighbors == 0) {
                setCellValue(x, y, "");
                for (GameObject gO : getNeighbors(gameObject)){
                    if (!gO.isOpen) {
                        openTile(gO.x, gO.y);
                    }
                }
            }
            else {
                setCellColor(x, y, Color.GREEN);
                setCellNumber(x, y, gameObject.countMineNeighbors);
            }
            score += 5;
        }
        if (countMinesOnField == countClosedTiles && !isGameStopped){
            win();
        }
        setScore(score);
    }

    private void markTile(int x, int y){

        if (isGameStopped){
            return;
        }

        GameObject gameObject = gameField[y][x];
        if (gameObject.isOpen || (countFlags == 0 && !gameObject.isFlag)) {
            return;
        }

        if (!gameObject.isFlag) {
            countFlags--;
            setCellValue(x, y, FLAG);
            gameObject.isFlag = true;
            setCellColor(x, y, Color.YELLOW);
        }
        else {
            gameObject.isFlag = false;
            countFlags++;
            setCellValue(x, y, "");
            setCellColor(x, y,Color.ORANGE);
        }
    }

    private void gameOver(){
        isGameStopped = true;
        showMessageDialog(Color.RED, "GAME OVER!", Color.BLACK, 40);
    }

    private void win(){
        isGameStopped = true;
        showMessageDialog(Color.GREEN, "Your WINNER!", Color.BLACK, 40);
    }

    private void restart(){
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        score = 0;
        countMinesOnField = 0;
        setScore(score);
        createGame();
    }
}