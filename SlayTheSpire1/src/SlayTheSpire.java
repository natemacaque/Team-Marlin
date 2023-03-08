
/*
 * 3/7/23 
 */
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

public class SlayTheSpire {

	//we havent used final in class but it just means that the variable cannot be changed , 
	//the naming convention for final variables is all caps
	
	private static final int BOARD_SIZE = 8;

	private static final char EMPTY_SPACE = '#';

	private static final char PLAYER_SYMBOL = 'P';

	private static final char TREASURE_ROOM_SYMBOL = 'T';

	private static final int NUM_ENEMIES = 6;

	private static final int FINAL_BOSS_HEALTH = 50;

	private static final Random random = new Random();
	private static final Scanner scanner = new Scanner(System.in);

	private static char[][] board = new char[BOARD_SIZE][BOARD_SIZE];

	private static int playerRow, playerCol;

	private static double playerHealth = 100;
	private static int playerAttackDamage = 10 ;

	private static Stack<Enemy> enemies = new Stack<>();
	private static int currentEnemyIndex = 0;

	private static boolean hasVisitedTreasureRoom = false;
	private static boolean isGameOver = false;

	public static void main(String[] args)  {
		initializeBoard();
		initializeEnemies();

		while (!isGameOver) {
			printBoard();
			String direction = getUserInput();
			movePlayer(direction);
			checkPlayerEncounter();
		}

		System.out.println("Game over.");
	}

	private static void initializeBoard() {
		for (int row = 0; row < BOARD_SIZE; row++) {
			for (int col = 0; col < BOARD_SIZE; col++) {
				board[row][col] = EMPTY_SPACE;
			}
		}

		playerRow = BOARD_SIZE / 2;
		playerCol = BOARD_SIZE / 2;
		board[playerRow][playerCol] = PLAYER_SYMBOL;

		int treasureRoomRow = random.nextInt(BOARD_SIZE);
		int treasureRoomCol = random.nextInt(BOARD_SIZE);
		board[treasureRoomRow][treasureRoomCol] = TREASURE_ROOM_SYMBOL;
	}

	private static void initializeEnemies() {

		for (int i = 0; i < NUM_ENEMIES; i++) {

			String name = "Enemy " + (i + 1);
			double health = random.nextDouble() * 50 + 50; // Random health between 50 and 100
			int attackDamage = random.nextInt(10) + 10; // Random attack damage between 10 and 20
			enemies.push(new Enemy(name, health, attackDamage));

			// Place the enemy on the board
			int row, col;
			do {
				row = random.nextInt(BOARD_SIZE);
				col = random.nextInt(BOARD_SIZE);
			} while (board[row][col] != EMPTY_SPACE);
			board[row][col] = 'E';
		}

		enemies.push(new Enemy("Final Boss", FINAL_BOSS_HEALTH, 20));
	}


	private static void printBoard() {
		for (int row = 0; row < BOARD_SIZE; row++) {
			for (int col = 0; col < BOARD_SIZE; col++) {
				System.out.print(board[row][col] + " ");
			}
			System.out.println();
		}
	}

	private static String getUserInput() {
		System.out.print("Enter direction (left, right, up, down): ");
		return scanner.nextLine().toLowerCase();
	}

	private static void movePlayer(String direction) {
		int newRow = playerRow;
		int newCol = playerCol;

		if (direction.equals("left")) {
			newCol--;
		} 
		else if (direction.equals("right")) {
			newCol++;
		}
		else if (direction.equals("up")) {
			newRow--;
		} 
		else if (direction.equals("down")) {
			newRow++;
		}

		if (newRow < 0 || newRow >= BOARD_SIZE || newCol < 0 || newCol >= BOARD_SIZE) {

			System.out.println("Invalid move.");
		}
		else if (board[newRow][newCol] == EMPTY_SPACE) {

			board[playerRow][playerCol] = EMPTY_SPACE;
			board[newRow][newCol] = PLAYER_SYMBOL;
			playerRow = newRow;
			playerCol = newCol;
		}
		else if (board[newRow][newCol] == TREASURE_ROOM_SYMBOL && !hasVisitedTreasureRoom) {
			board[playerRow][playerCol] = EMPTY_SPACE;
			board[newRow][newCol] = PLAYER_SYMBOL;
			playerRow = newRow;
			playerCol = newCol;
			hasVisitedTreasureRoom = true;
			promptTreasureRoom();
		} else {
			// Player encounters an enemy
			Enemy enemy = enemies.pop();
			battle(enemy);
		}
	}

	private static void promptTreasureRoom() {

		System.out.println("You found the treasure room! Choose an upgrade:");
		System.out.println("1. Increase health");
		System.out.println("2. Upgrade weapon (increase attack damage)");

		String choice = scanner.nextLine();

		if (choice.equals("1")) {
			playerHealth += 20;
			System.out.println("Health increased by 20.");
		} 
		else if (choice.equals("2")) {
			playerAttackDamage += 5;
			System.out.println("Weapon upgraded. Attack damage increased by 5.");
		} else {
			System.out.println("Invalid choice.");
		}
	}

	private static void battle(Enemy enemy) {
		
		System.out.println("You encountered " + enemy.getName() + "!");
		
		while (playerHealth > 0 && enemy.getHealth() > 0) {
			System.out.println("Your health: " + playerHealth);
			System.out.println(enemy.getName() + "'s health: " + enemy.getHealth());

			// Player's turn
			System.out.println("Your turn. Enter your choice of action (1 for attack, 2 for item, 3 for run):");
			String choice = scanner.nextLine();
			switch (choice) {
			case "1":
				double playerDamage = playerAttackDamage;
				System.out.println("You dealt " + playerDamage + " damage to " + enemy.getName() + ".");
				enemy.takeDamage(playerDamage);
				break;
			case "2":
				System.out.println("You use a special card!");
				int card = random.nextInt(2);
				if (card == 0) {
					System.out.println("You use a defense buff card! The next opponent's attack has no effect on you.");
					// Increase the player's health by the enemy's attack damage
					playerHealth += enemy.getAttackDamage();
				} else {
					System.out.println("You use a boomerang trap card! The opponent's attack is reflected back to them.");
					// Inflict the enemy's attack damage to themselves
					enemy.takeDamage(enemy.getAttackDamage());
				}
				break;
			case "3":
				System.out.println("You run away.");
				isGameOver = true;
				return;
			default:
				System.out.println("Invalid choice.");
				break;
			}

			if (enemy.getHealth() <= 0) {
				System.out.println("You defeated " + enemy.getName() + "!");
				playerHealth += enemy.getHealth() * -1; // Player regains any excess damage dealt
				break;
			}

			// Enemy's turn
			double enemyDamage = enemy.getAttackDamage();
			System.out.println(enemy.getName() + " dealt " + enemyDamage + " damage to you.");
			playerHealth -= enemyDamage;

			if (playerHealth <= 0) {
				isGameOver = true;
				System.out.println("You have died.");
				return;
			}
		}
	}


	private static void checkPlayerEncounter() {
		if (currentEnemyIndex == NUM_ENEMIES && !isGameOver) {
			System.out.println("You won the game!");
			isGameOver = true;
		}
	}

	private static class Enemy {

		private String name;
		private double health;
		private int attackDamage;

		public Enemy(String name, double health, int attackDamage) {
			this.name = name;
			this.health = health;
			this.attackDamage = attackDamage;
		}

		public String getName() {
			return name;
		}

		public double getHealth() {
			return health;
		}

		public int getAttackDamage() {
			return attackDamage;
		}

		public void takeDamage(double damage) {
			health -= damage;
		}
	}
}