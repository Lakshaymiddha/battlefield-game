//package org.battleship.service;
//
//import org.battleship.model.Player;
//import org.battleship.model.Ship;
//
//import java.util.Random;
//import org.battleship.model.Position;
//
//
//import java.util.*;
//
//
//import org.battleship.model.Player;
//import org.battleship.model.Position;
//import org.battleship.model.Ship;
//
//import java.util.*;
//
//public class Game {
//    private final List<Ship> playerAShips = new ArrayList<>();
//    private final List<Ship> playerBShips = new ArrayList<>();
//    private Player currentPlayer = Player.PLAYER_A;
//    private boolean gameOver = false;
//    private Player winner;
//
//    private final Set<Position> firedPositions = new HashSet<>();
//    private final int battlefieldSize;
//
//    public Game(int size) {
//        if (size < 4 || size > 20 || size % 2 != 0)
//            throw new IllegalArgumentException("Battlefield size must be even and 4-20");
//        this.battlefieldSize = size;
//    }
//
//    public void addShip(Ship shipA, Ship shipB) {
//        // Simple validation: positions within battlefield
//        if (!isValidShip(shipA, Player.PLAYER_A) || !isValidShip(shipB, Player.PLAYER_B)) {
//            System.out.println("Invalid ship placement for " + shipA.getId() + " or " + shipB.getId() + ". Discarded.");
//            return;
//        }
//
//        playerAShips.add(shipA);
//        playerBShips.add(shipB);
//    }
//
//    private boolean isValidShip(Ship ship, Player player) {
//        for (Position pos : ship.getOccupiedPositions()) {
//            if (pos.getX() < 0 || pos.getX() >= battlefieldSize) return false;
//            if (pos.getY() < 0 || pos.getY() >= battlefieldSize / 2) return false;
//            // TODO: check overlap with existing ships
//            List<Ship> ships = (player == Player.PLAYER_A) ? playerAShips : playerBShips;
//            for (Ship s : ships) {
//                for (Position p : s.getOccupiedPositions()) {
//                    if (p.equals(pos)) return false;
//                }
//            }
//        }
//        return true;
//    }
//
//    public void startGame() {
//        Random random = new Random();
//
//        while (!gameOver) {
//            List<Ship> opponentShips = (currentPlayer == Player.PLAYER_A) ? playerBShips : playerAShips;
//            int x, y;
//            Position target;
//            do {
//                x = random.nextInt(battlefieldSize);
//                y = random.nextInt(battlefieldSize / 2);
//                target = new Position(x, y);
//            } while (firedPositions.contains(target));
//
//            firedPositions.add(target);
//            System.out.println(currentPlayer.getName() + " fires at " + target.getX() + "," + target.getY());
//
//            boolean hit = false;
//            for (Ship s : opponentShips) {
//                if (!s.isDestroyed() && s.occupies(target)) {
//                    s.hitAt(target);
//                    hit = true;
//                    System.out.println("Hit! Ship " + s.getId() + " destroyed? " + s.isDestroyed());
//                    break;
//                }
//            }
//
//            if (playerAShips.stream().allMatch(Ship::isDestroyed)) {
//                gameOver = true;
//                winner = Player.PLAYER_B;
//            } else if (playerBShips.stream().allMatch(Ship::isDestroyed)) {
//                gameOver = true;
//                winner = Player.PLAYER_A;
//            }
//
//            currentPlayer = (currentPlayer == Player.PLAYER_A) ? Player.PLAYER_B : Player.PLAYER_A;
//        }
//
//        System.out.println("Game Over! Winner: " + winner.getName());
//    }
//}
