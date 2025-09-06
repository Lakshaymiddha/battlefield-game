# Battleship Game – Java Implementation

A **clean, extensible, and test-driven implementation** of the classic Battleship game in Java.  
Designed with **SOLID principles** and **well-known design patterns** for scalability, maintainability, and flexibility.

---

## Features

- **BattleFieldGameService API** – Initialize battlefield, add ships, start the game.
- **Ships via Factory Method + Builder Pattern** – Create flexible ship objects with validation.
- **Missile Firing via Strategy Pattern** – Support for multiple firing strategies (Random, Smart, etc.).
- **Observer Pattern** – Notify listeners of hits, misses, and destroyed ships (extensible for UI or logs).
- **Graceful Exception Handling** – Invalid actions (duplicate ship, invalid placement, firing outside bounds) do not
  crash the game but return meaningful results.
- **Full Testing** – Unit tests ensure correctness and resilience.

---

## Game Rules

1. **Battlefield** – Square NxN grid (even size between 4 and 20).
2. **Players** – Two players (A and B), each controls half the grid.
3. **Ships** – Square-shaped ships placed inside each player’s territory.
4. **Gameplay** – Players take turns firing missiles (strategy-driven).
5. **Winning** – First player to destroy all opponent ships wins.

---

## Design Patterns Used

### 1. Builder Pattern

- Used in `ShipBuilder` and `PlayerBuilder` for flexible creation with validation.

2. Factory Method
   Used in ShipFactory to create ships uniformly and encapsulate construction logic.

3. Strategy Pattern
   FireStrategy interface with multiple implementations like RandomFireStrategy, SmartFireStrategy etc.
Allows switching firing logic without changing game core.

4. Observer Pattern
   GameObserver interface notified on hits, misses, or ship destruction. Supports logging, or multiplayer notifications.

### Usage Example

```code
BattleshipGame game = new BattleshipGame();

// Initialize 6x6 battlefield
game.initGame(6);

// Add ships for both players
game.addShip("SH1", 2, 1, 1, 4, 4);  // 2x2 ship
game.addShip("SH2", 1, 0, 0, 5, 0);  // 1x1 ship

// View battlefield (optional)
game.viewBattleField();

// Start the game
game.startGame();
```

## Building & Running

### Prerequisites

- Java 11+
- Maven 3.6+

## Compile project

```
mvn compile
```

## Package the application

```
    mvn package
```

## Run application

```
mvn exec:java -Dexec.mainClass="org.battleship.BattleshipApplication"
```

## Testing

- Unit Tests: Verify individual components (Ship, GameEngine, MissileService)

- Integration Tests: Simulate full game scenarios

- Exception Handling Tests: Ensure graceful recovery on invalid inputs

- Boundary & Corner Cases: Validate battlefield size, ship placement, and missile firing

## Run all tests

```
mvn test
```

## Run only unit tests

```
mvn test -Dtest="*Test"
```

## Run only integration tests

```
mvn test -Dtest="*IntegrationTest"
```