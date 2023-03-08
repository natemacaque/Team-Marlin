import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

/*
 * SlayTheSpire Clone
 * 
 * TODO:(In order of priority)
 * 		Code getting new cards at the end of combat
 * 		Add Map Method
 * 			-Add Healing Rooms
 * 			-Add Random events
 * 			-Map UI
 * 			-Viewing Map in Combat?
 * 		Add New Cards/Enemies
 * 		Add Level Functionality aka Scaling difficulty & boss
 * 		Update UI
 */
public class Enemy {
	static Scanner s = new Scanner(System.in);
	private static int maxHP = 80;
	private static int playerHP = maxHP;
	//                            str dex
	private static int[] pBuffs = {0, 0};
	private static Queue<Integer> deck = new LinkedList<Integer>();
	private static Queue<Integer> discard = new LinkedList<Integer>();
	private static int maxEnergy = 3;
	private static int energy = maxEnergy;
	private static int[] hand = new int[8];
	private static int cardsInHand = 0;


	public static void main(String[] args) {
		//starting deck
		for (int i = 0; i < 6; i++) {
			deck.add(1);
		}
		for (int i = 0; i < 8; i++) {
			deck.add(2);
		}
		deck.add(3);
		deck.add(4);
		deck.add(4);
		System.out.println(deck.size());
		combat(1);
		

	}


	public static void combat(int level) {
		//intializing combat
		int[][] enemy = getEnemy(level); //gets enemy
		shuffle(deck); //shuffles deck
		int enemyHP = enemy[0][0]; //gets enemy Max HP
		int[] buffReset = {0, 0}; //resets buffs
		pBuffs = buffReset; 


		//recursive boolean function that ends when combat ends
		//true occurs when the player beats the enemy
		if(turn(enemy, enemyHP, 0, 0, 0)) {
			//TODO add map method
			System.out.println("You beat the enemy");
			level++;
			
			//begins a new combat
			combat(level);
		}
		// false occurs when the players HP reaches zero
		else {
			System.out.println("HP: " + 0);
			System.out.println("-----------You Died--------");
		}



	}

	//returns true if you beat enemy, returns false if you died
	private static boolean turn(int[][] eID, int eHP, int turnCount, int enemyLoop, int enemyBlock) {
		//base case, when the enemy HP lowers to zero
		if(eHP <= 0) {
			return true;
		}
		//base case, when the player's HP lowers to zero
		if(playerHP <= 0) {
			return false;
		}

		energy = maxEnergy;
		int playerBlock = 0;
		cardsInHand = 0;
		//draw a new hand
		for (int i = 0; i < 5; i++) {
			drawCard();
		}

		
		while(true) {
			//allows the enemy to loop actions
			if(eHP <= 0) {
				break;
			}
			if(turnCount - enemyLoop >= eID[2].length) {
				enemyLoop = enemyLoop + eID[2].length;
			}
			printCombat(eID, eHP, turnCount, enemyLoop, enemyBlock, playerBlock);
			if(s.hasNextInt() == true) {
				int toPlay = s.nextInt();
				if(toPlay <= cardsInHand && toPlay >= 0) {
					//player ends turn by pressing zero
					if(toPlay == 0) {
						break;
					}
					else {
						int[] cardInfo = playCard(hand[toPlay - 1]);
						//if the player has enough energy
						if(energy >= cardInfo[2]) {
							discard.add(hand[toPlay-1]);
							hand[toPlay - 1] = 0;
							//player attack logic
							if(cardInfo[0] == 1) {
								//if player will deal more damage than block
								if(enemyBlock <= cardInfo[1]) {
									//deals damage to health
									eHP = eHP + enemyBlock - cardInfo[1];
									enemyBlock = 0;
								}
								else {
									//just decreases enemy block;
									enemyBlock = enemyBlock - cardInfo[1];
								}
							}
							//player defend logic
							if(cardInfo[0] == 2) {
								playerBlock = playerBlock + cardInfo[1];
							}
							//ADD LOGIC
							energy = energy - cardInfo[2];
							continue;
						}
						else {
							System.out.println("You don't have enough Energy!");
							s.nextLine();
							continue;
						}
					}

				}
				else {
					System.out.println("Not a valid move! Try again.");
					s.nextLine();
					continue;
				}
			}
			else {
				System.out.println("Not a valid move! Try again.");
				s.nextLine();
				continue;
			}
		}
		
		
		//enemy turn
		
		//resets enemyBlock
		enemyBlock = 0;
		//takes the info from the enemyID to decide what it will do that turn
		int enemyActionID = eID[2][turnCount - enemyLoop];
		int enemyActionValue = eID[1][turnCount - enemyLoop];
		//0 represents an attack towards the player
		if(enemyActionID == 0) {
			//checks if the players block can block all the damage
			if(playerBlock <= enemyActionValue) {
				//if not then the player takes some damage to their HP
				playerHP = playerHP + playerBlock - enemyActionValue;
				playerBlock = 0;
			}
			else {
				//otherwise their block just gets damaged
				playerBlock = playerBlock - enemyActionValue;
			}
		}
		//1 represents a enemy gaining block
		if(enemyActionID == 1) {
			enemyBlock = enemyActionValue;
		}
		//discards the hand at the end of the turn
		for (int i = 0; i < hand.length; i++) {
			if(hand[i] != 0) {
				discard.add(hand[i]);
				hand[i] = 0;
			}
		}
		cardsInHand = 0;

		//starts a new turn
		return turn(eID, eHP, turnCount + 1, enemyLoop, enemyBlock);
	}

	private static int[][] getEnemy(int level){
		//TODO: make randomly selected enemy

		//enemy info:     Max HP  ID   Action Values  Action ID's
		int[][] testEnemy = {{25, 00}, {5, 6, 9, 1}, {0, 1, 0, 1}};
		//                                   |              |
		//                                   V              V
		//						   How much atk/def | 0 = Atk, 1 = Def etc.

		return testEnemy;
	}

	private static Queue<Integer> shuffle(Queue<Integer> Deck){
		Collections.shuffle((List<?>) Deck);
		return Deck;
	}

	private static int[] playCard(int id) {
		int[] toPlay = {0, 0, 0};
		/*
		 * The toPlay array represents a card,
		 * toPlay[0] represents the action itself:
		 * 		1 = Attack
		 * 		2 = Defend
		 * 		0 = default/powers
		 * toPlay[1] represents the value of the action
		 * toPlay[2] represents the energy cost of the card
		 * 
		 */
		//Jab Card: deals 8 damage
		if(id == 1) {
			toPlay[0] = 1;
			toPlay[1] = attack(8);
			toPlay[2] = 1;
		}
		//Reflect Card: Blocks 5 damage
		if(id == 2) {
			toPlay[0] = 2;
			toPlay[1] = defend(5);
			toPlay[2] = 1;
		}
		//Lift Card: Increases Strength by 2
		if(id == 3) {
			pBuffs[0] = pBuffs[0] + 2;
			toPlay[2] = 2;
		}
		//Polish Card: Increases Dexterity by 1
		if(id == 4) {
			pBuffs[1] = pBuffs[1] + 1;
			toPlay[1] = 1;
		}
		//Draw Card: draws 2 cards
		if(id == 5) {
			drawCard();
			drawCard();
			toPlay[2] = 2;
		}
		return toPlay;
	}

	private String[] cardToString(int id){
		//String array where [0] is the name of the card, [1] is the description, and [2] is the energy cost
		String[] cardInfo = {"Error No Title", "Error No Description", "Error No Cost"}; 
		//Absence of a card
		if(id == 0) {
			cardInfo[0] = "";
			cardInfo[1] = "";
			cardInfo[2] = "";
		}
		if(id == 1) {
			cardInfo[0] = "Jab";
			cardInfo[1] = "Deals " + (8+ pBuffs[1]) + " Damage";
			cardInfo[2] = "1";
		}
		if(id == 2) {
			cardInfo[0] = "Reflect";
			cardInfo[1] = "Blocks " + (5 + pBuffs[2]) +  "Damage";
			cardInfo[2] = "1";
		}
		if(id == 3) {
			cardInfo[0] = "Lift";
			cardInfo[1] = "Increases Strength by 2";
			cardInfo[2] = "2";
		}
		if(id == 4) {
			cardInfo[0] = "Polish";
			cardInfo[1] = "Increases Dexterity by 1";
			cardInfo[2] = "1";
		}
		if(id == 5) {
			cardInfo[0] = "Focus";
			cardInfo[1] = "Draw 2 cards";
			cardInfo[2] = "1";
		}

		return cardInfo;
	}

	private static int newCard() {
		//int representing cards designed
		int allCards = 3;
		//generates a random number between one and the amount of cards designed
		return (int) (Math.random() * allCards) + 1;
	}

	private static boolean drawCard() {
		if(deck.size() == 0) {
			deck = discard;
			discard = new LinkedList<Integer>();
		}
		if(cardsInHand < hand.length) {
			hand[cardsInHand] = deck.remove();
			cardsInHand++;
			return true;
		}
		else {
			return false;
		}
		
	}

	//handles the amount of damage to be dealt
	private static int attack(int damage){
		return damage + pBuffs[0];
	}

	//handles the amount of block to be created
	private static int defend(int block) {
		return block + pBuffs[1];
	}

	private static void printCombat(int[][] eID, int eHP, int turnCount, int enemyLoop, int enemyBlock, int playerBlock) {
		System.out.println("Enemy intends to: " + eID[2][turnCount - enemyLoop] + "  for: " + eID[1][turnCount - enemyLoop]);
		System.out.println("EnemyHP: " + eHP + "/" + eID[0][0] + " EnemyBlock: " + enemyBlock);
		System.out.println("Turn: " + turnCount + " Deck: " + deck.size() + " Discard: " + discard.size());
		System.out.println("Hand : " + Arrays.toString(hand));
		System.out.println("        1  2  3  4  5");
		System.out.println("HP:" + playerHP + " Block:" + playerBlock + " Energy:" + energy + " Str:" + pBuffs[0] + " Dex:" + pBuffs[1]);
		System.out.println("Pick a Card 1 - " + cardsInHand + " or enter zero to end your turn");
		System.out.println();
	}
}
