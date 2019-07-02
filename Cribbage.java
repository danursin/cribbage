// Created by Dan Ursin
// May 12, 2012
// Brewers beat the Cubs today

import java.util.Arrays;
import java.util.Scanner;

public class CribbageAlphaMay12
{
	public static void main(String[] args)
	{
		
		int[] piScore = {0, 0}; //new int[2];
		int deal = 0;
		
		System.out.println("Welcome to Cribbage!\n*****************");	
		
		while (piScore[0] < 121 && piScore[1] < 121)
		{
			
			/////////////////////////////////////////////////////////////////////////
			// ********** Generate deck, Player's hands, Crib, Peg hand ********** //
			/////////////////////////////////////////////////////////////////////////
			
			String[] deckOfCards = new String[52];
				deckOfCards = deckOfCards();                  // Game Deck
			String[] roundHand = new String[13];
				roundHand = drawNCards(deckOfCards, 13);      // All cards necessary for a round
			String[] p1Hand = new String[6];
				System.arraycopy(roundHand, 0, p1Hand, 0, 6); // Player 1's Hand
			String[] p2Hand = new String [6];
				System.arraycopy(roundHand, 6, p2Hand, 0, 6); // Player 2's Hand
			String[] crib = new String[5];                  
				crib[4] = roundHand[12];                      // crib[4] is Cut Card
			String[] pegHand = {"ZZZ", "ZZZ","ZZZ","ZZZ","ZZZ", "ZZZ","ZZZ","ZZZ"};
			
			/////////////////////////////////////////////////////////////////////////
			// ************* Display Dealer and Owner of Crib ******************** //
			// ************** deal % 2 == 0 means P1's Crib ********************** //
			/////////////////////////////////////////////////////////////////////////
			
			if (deal % 2 == 0)
				System.out.println("\nPlayer 1 deals. Player 1's Crib");
			else
				System.out.println("\nPlayer 2 deals. Player 2's Crib");
			
			/////////////////////////////////////////////////////////////////////////
			// *************** Show piHand, get discard choices ****************** //
			// ******************** Update Hands and Crib ************************ //
			/////////////////////////////////////////////////////////////////////////
			
			showPiHand(p1Hand, 1);
			String discard = "";
			discard = isValidEntry(discard);                  
			crib = sendToCrib(crib, p1Hand, 1, discard);
			p1Hand[Integer.parseInt(discard.substring(0, discard.indexOf(',')))] = "ZZZ";
			p1Hand[Integer.parseInt(discard.substring(discard.indexOf(',') + 1))] = "ZZZ";  
			
			showPiHand(p2Hand, 2);
			discard = "";
			discard = isValidEntry(discard);              
			crib = sendToCrib(crib, p2Hand, 2, discard);   
			p2Hand[Integer.parseInt(discard.substring(0, discard.indexOf(',')))] = "ZZZ";
			p2Hand[Integer.parseInt(discard.substring(discard.indexOf(',') + 1))] = "ZZZ";
			
			//////////////////////////////////////////////////////////////////////////
			// *************** Make copies of piHands and sort Hands ************** //
			//  EffSize of piHands is 4. EffSize of crib is 5. Cut card is crib[4]  //
			//////////////////////////////////////////////////////////////////////////
			
			Arrays.sort(p1Hand); Arrays.sort(p2Hand);
			
			String[] p1HandCopy = new String[5];
			String[] p2HandCopy = new String[5];
			for (int i = 0; i < 5; ++i)
				{
					p1HandCopy[i] = p1Hand[i];
					p2HandCopy[i] = p2Hand[i];
				}
			
			/////////////////////////////////////////////////////////////////////////
			// *********************  Begin Pegging Sequence  ******************** //
			//   Reveal cut card, display appropriate knobs message, give points   //
			/////////////////////////////////////////////////////////////////////////
			
			System.out.println("\nCut card is " + crib[4]);
			if (determineValue(crib[4], 1) == 11)
				{
					System.out.println("Knobs for 1");
					if (deal % 2 == 0)
						{
							piScore[0] += 1;
							System.out.println("Player 1's score : " + piScore[0]);
						}
					else 
						{
							piScore[1] += 1;
							System.out.println("Player 2's score : " + piScore[1]);
						}
				}
			
			/////////////////////////////////////////////////////////////////////////
			// ******** Offer Player i choice to play a card based on deal ******* //
			// *********** Deal % 2 == 0 means Player 2 plays first ************** //
			/////////////////////////////////////////////////////////////////////////
			int turnCnt = deal;
			int[] pegScore = {0};
			
			while (effSize(p1Hand) > 0 || effSize(p2Hand) > 0 && piScore[0] < 120 && piScore[1] < 120)
				{
					if (pegScore[0] == 0)
						{   // Resets pegHand if pegScore is zero
							for (int i = 0; i < 8; ++i)
								pegHand[i] = "ZZZ";
						}
					
					if (turnCnt % 2 == 1 && autoGo(p1Hand, pegScore) == false)
						{ // player 1 attempts to play a card
							showPegHand(pegHand, pegScore);
							playACard(p1Hand, 1, pegHand, pegScore);
							pegging(pegHand, piScore, 1, pegScore);
							if (autoGo(p2Hand, pegScore) == true)
								{
									while (autoGo(p1Hand, pegScore) == false)
										{
											playACard(p1Hand, 1, pegHand, pegScore);
											pegging(pegHand, piScore, 1, pegScore);
										}
									piScore[0] += 1;
									System.out.println("Go for 1\nPlayer 1's score = " + piScore[0]);
								}
						}
					else if (turnCnt % 2 == 0 && autoGo(p2Hand, pegScore) == false)
						{	// Player 2 attempts to play a card
							showPegHand(pegHand, pegScore);
							playACard(p2Hand, 2, pegHand, pegScore);
							pegging(pegHand, piScore, 2, pegScore);
							if (autoGo(p1Hand, pegScore) == true)
								{
									while (autoGo(p2Hand, pegScore) == false)
										{
											playACard(p2Hand, 2, pegHand, pegScore);
											pegging(pegHand, piScore, 2, pegScore);
										}
									piScore[1] += 1;
									System.out.println("Go for 1\nPlayer 2's score = " + piScore[1]);
								}
						}
					
					if (autoGo(p2Hand, pegScore) == true && autoGo(p1Hand, pegScore) == true)
						{    
							pegScore[0] = 0;
							for (int i = 0; i < pegHand.length; ++i)
								pegHand[i] = "ZZZ";
						}
					
					turnCnt += 1;   // Alternate turns	
					
				} // End Pegging Loop
		
			/////////////////////////////////////////////////////////////////////
			// ****************** Begin Scoring Sequence ********************* //
			// ***** Scoring Sequence occurs unless a player pegged out ****** // 
			/////////////////////////////////////////////////////////////////////
			
			if (piScore[0] < 121 && piScore[1] < 121)
				{
					p1HandCopy[4] = crib[4];       // Assign cut card to p1Hand
					p2HandCopy[4] = crib[4];	   // Assign cut card to p2hand
					System.out.println("\n*************\n   Scoring\n*************");
					
					/////////////////////////////////////////
					// ******** Player 1 Dealt *********** //
					/////////////////////////////////////////
					if (deal % 2 == 0)
						{
							System.out.println("\nPlayer 2 Scores\n**************");
							scoreReport(p2HandCopy, 5);
							piScore[1] += totalScores(p2HandCopy);
							
							if (piScore[1] < 121)
								{
									System.out.println("\nPlayer 1 Scores\n**************");
									scoreReport(p1HandCopy, 5);
									piScore[0] += totalScores(p1HandCopy);
								}
						}
					
					/////////////////////////////////////////
					// ********** Player 2 Dealt ********* //
					/////////////////////////////////////////
					else
						{
							System.out.println("\nPlayer 1 Scores\n**************");
							scoreReport(p1HandCopy, 5);
							piScore[0] += totalScores(p1HandCopy);
							
							if (piScore[0] < 121)
								{
									System.out.println("\nPlayer 2 Scores\n**************");
									scoreReport(p2HandCopy, 5);
									piScore[1] += totalScores(p2HandCopy);
								}
						}
					
					////////////////////////////////////////
					// **********  Crib Score *********** //
					////////////////////////////////////////
					if (piScore[0] < 121 && piScore[1] < 121)
						{
							System.out.println("\nCrib Scores\n***********");
							if (deal % 2 == 0)
								{
									scoreReport(crib, 5);
									piScore[0] += nOfAKind(crib, 5) + runs(crib) + count15s(crib);
									if (sameSuit(crib) == 5)
										{
											piScore[0] += 5;
											System.out.println("Same suit bonus: +5");
										}
								}
							else
								{
									scoreReport(crib, 5);
									piScore[1] += nOfAKind(crib, 5) + runs(crib) + count15s(crib);
									if (sameSuit(crib) == 5)
										{
											piScore[1] += 5;
											System.out.println("Same suit bonus: +5");
										}
								}
						}
				} // End Scoring Block
			
			//////////////////////////////////////////
			// ******** Show current scores ******* //
			//////////////////////////////////////////
			
					showpiScore(piScore);
			
			//////////////////////////////////////////
			// ********* Display Peg Board ******** //
			//////////////////////////////////////////
					System.out.println();
					gameBoard(piScore);
					System.out.println();
			 
			/////////////////////////////////////////
			// *********** END ROUND ************* //
			/////////////////////////////////////////
			
		deal += 1;	
		
		} // End Game Loop
		
		////////////////////////////////////
		// ******* Display Winner ******* //
		////////////////////////////////////
		
				displayWinner(piScore);
		
		////////////////////////////////////
		// ****** Ride away clean ******* //
		////////////////////////////////////
				
	} // End Main
	
	public static int[] Deck()
	{   // Assigns a ## to each element in the deck
		int[] Deck = new int[52];
		for (int i = 0; i < 52; ++i)
			Deck[i] = i;

		return Deck;
	}
	public static String cardValue(int x)
	{   // Assigns a suit
		String suit;    
		if (x / 13 == 0)
			suit = "Diamonds";
			else if (x / 13 == 1)
				suit = "Hearts";
				else if (x / 13 == 2)
					suit = "Clubs";
					else
						suit = "Spades";
		// Assigns rank within suit
		String rank;    
		if (x % 13 == 0)
			rank = "Ace";
			else if ((x % 13) >= 1 && (x % 13) <= 9)
				rank = Integer.toString((x % 13) + 1);
				else if(x % 13 == 10)
					rank = "Jack";
					else if (x % 13 == 11)
						rank = "Queen";
						else
							rank ="King";

		String cardValue = rank + " of " + suit;
		return cardValue;
	}
	public static String[] deckOfCards()
	{   // Generates a deck of cards with suit and rank
		String[] deckOfCards = new String[52];
		int[] deck = new int[52];
		deck = Deck();
		for (int i = 0; i < 52; ++i)
			deckOfCards[i] = cardValue(deck[i]);
		return deckOfCards;
	}
	public static String[] drawNCards(String[] deck, int n)
	{   // Draws n cards without replacement
		boolean[] alreadyDrawn = new boolean[52];
		String[] hand = new String[n];
		int draw = 0; 
		int i = 1;
		while (i <= n )
			{
				draw = (int)( 52 * (Math.random()) ); 
				if (alreadyDrawn[draw] == false)     // ie card is not yet drawn
					{
						hand[i-1] = deck[draw];
						alreadyDrawn[draw] = true;
						++i;	
					}
			}
		return hand;
	}
	public static void showPiHand(String[] piHand, int playerNumber)
	{
		if (playerNumber == 1)
			{
				System.out.print("\nPlayer 1's hand is");
				for (int i = 0; i < 6; ++i)
					{
						if (piHand[i] != "ZZZ")
							System.out.print("\nCard " + (i) + ": " +piHand[i]);
					}
			}
		else 
			{
				System.out.print("\nPlayer 2's hand is");
				for (int i = 0; i < 6; ++i)
					{
						if (piHand[i] != "ZZZ")
							System.out.print("\nCard " + (i) + ": " +piHand[i]);
					}
			}
	}
	public static String isValidEntry(String discard)
	{
		Scanner stdIn = new Scanner(System.in);
		boolean validEntry = false;
		while (validEntry == false)
			{
				System.out.print("\n\nYou must discard two cards into the crib. \nEnter the indicated numbers of the cards you wish to discard." +
					"\nUse the format ex:  2,4   \nEntry: ");
				discard = stdIn.nextLine();
				if (discard.indexOf(',') > 0)
					if (discard.length() == 3)
						if (Integer.parseInt(discard.substring(0, discard.indexOf(','))) >= 0 &&  Integer.parseInt(discard.substring(0, discard.indexOf(','))) < 6)
							if (Integer.parseInt(discard.substring(discard.indexOf(',') + 1)) >= 0 &&  Integer.parseInt(discard.substring(discard.indexOf(',') + 1)) < 6)
								if (Integer.parseInt(discard.substring(0, discard.indexOf(','))) != Integer.parseInt(discard.substring(discard.indexOf(',') + 1)))
									validEntry = true;
				if (validEntry == false)
						System.out.println("Invalid entry. Only use the digits 0-5 and a comma. No Spaces!");
			}
		return discard;
	}
	public static String[] sendToCrib(String[] crib, String[] piHand, int playerNumber, String discard)
	{            // In first part of game, takes user's choices to crib
		if (playerNumber == 1)
			{
				crib[0] = piHand[Integer.parseInt(discard.substring(0, discard.indexOf(',')))];
				crib[1] = piHand[Integer.parseInt(discard.substring(discard.indexOf(',') + 1))];
				
			}
		else
			{
				crib[2] = piHand[Integer.parseInt(discard.substring(0, discard.indexOf(',')))];
				crib[3] = piHand[Integer.parseInt(discard.substring(discard.indexOf(',') + 1))];
			}
		
		
		return crib;
	}
	public static int determineValue(String S, int version)
	{   // Determine values for cards w/o regard to suit
		// Version 1 counts face cards as jack = 11, queen = 12, king = 13
		// Version 2 counts all face cards as 10
		int value = 0;
		int locSpace = S.indexOf(' ');
	if (locSpace != -1)
	{
		if ((S.substring(0, locSpace)).equalsIgnoreCase("Ace"))
			value = 1;
			else if ((S.substring(0, locSpace)).equalsIgnoreCase("2"))
				value = 2;
				else if ((S.substring(0, locSpace)).equalsIgnoreCase("3"))
					value = 3;
					else if ((S.substring(0, locSpace)).equalsIgnoreCase("4"))
						value = 4;
						else if ((S.substring(0, locSpace)).equalsIgnoreCase("5"))
							value = 5;
							else if ((S.substring(0, locSpace)).equalsIgnoreCase("6"))
								value = 6;
								else if ((S.substring(0, locSpace)).equalsIgnoreCase("7"))
									value = 7;
									else if ((S.substring(0, locSpace)).equalsIgnoreCase("8"))
										value = 8;
										else if ((S.substring(0, locSpace)).equalsIgnoreCase("9"))
											value = 9;
											else if ((S.substring(0, locSpace)).equalsIgnoreCase("10"))
												value = 10;
												else if ((S.substring(0, locSpace)).equalsIgnoreCase("Jack"))
													{
														if (version == 1)
															value = 11;
														else
															value = 10;
													}
													
													else if ((S.substring(0, locSpace)).equalsIgnoreCase("Queen"))
														{
															if (version == 1)
																value = 12;
															else
																value = 10;
														}
														else if ((S.substring(0, locSpace)).equalsIgnoreCase("King"))
															{
																if (version == 1)
																	value = 13;
																else 
																	value = 10;
															}
															else  
																value = 20;
	    }
		return value;
	}
	public static int effSize(String[] hand)
	{
		int effSize = 0;
		for (int i = 0; i < hand.length ; ++i)
			{
				if (!hand[i].equals("ZZZ"))
					effSize += 1;
			}
			
		return effSize;	
	}
	public static boolean autoGo(String[] piHand, int[] pegScore)
	{	
		boolean autoGo = true;
		for (int i = 0; i < effSize(piHand) && autoGo == true; ++i)
			{
				if (determineValue(piHand[i], 2) + pegScore[0] <= 31 )
					autoGo = false;
			}
		return autoGo;
	}
	public static void playACard(String[] piHand, int playerNumber, String[] pegHand, int[] pegScore)
	{
		Scanner stdIn = new Scanner(System.in);
		int piEffSize = effSize(piHand);
		int choice = 10;
		Arrays.sort(piHand);
		boolean autoGo = autoGo(piHand, pegScore);
		if (autoGo == false)
			{
				showPiHand(piHand, playerNumber);
				System.out.print("\n**********************\nPlay a card [ 0 - " + (piEffSize - 1) + " ]: ");
				choice = stdIn.nextInt();
				while (choice < 0 || choice >= piEffSize || determineValue(piHand[choice], 2) + pegScore[0] > 31)
					{
						System.out.print(choice + " is not an option. Enter your choice again : ");
						choice = stdIn.nextInt();
					}
				System.out.println("**********************");
				pegHand[effSize(pegHand)] = piHand[choice];
				pegScore[0] += determineValue(piHand[choice], 2); 
				piHand[choice] = "ZZZ";
			}
		else
			{
				System.out.println("Player " + playerNumber + " says 'Go'");
				
			}
	}
	public static void pegging(String[] pegHand, int[] piScore, int playerNumber, int[] pegScore)
	{
		int effSize = effSize(pegHand);
		if (effSize == 2)
			{
				if (pegScore[0] == 15)
					{
						piScore[playerNumber - 1] += 2;
						System.out.println("15 for 2\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
					}
				if (determineValue(pegHand[0], 1) == determineValue(pegHand[1], 1))
					{
						piScore[playerNumber - 1] += 2;
						System.out.println("Pair for 2\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
					}
			}
		if (effSize == 3)
			{
				if (pegRuns(pegHand) > 0)
					{
						piScore[playerNumber - 1] += pegRuns(pegHand);
						System.out.println("Run of " + pegRuns(pegHand) +" for " + pegRuns(pegHand) 
								+ "\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
					}							
				if (pegScore[0] == 15)
					{
						piScore[playerNumber - 1] += 2;
						System.out.println("15 for 2\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
					}
				if (determineValue(pegHand[1], 1) == determineValue(pegHand[2], 1))
					{
						if (determineValue(pegHand[0], 1) == determineValue(pegHand[2], 1))
							{
								piScore[playerNumber - 1] += 6;
								System.out.println("Three of a kind for 6\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
							}
						else
							{
								piScore[playerNumber - 1] += 2;
								System.out.println("Pair for 2\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
							}
					}		
			 }
			if (effSize == 4)
			{
				if (pegRuns(pegHand) > 0)
					{
						piScore[playerNumber - 1] += pegRuns(pegHand);
						System.out.println("Run of " + pegRuns(pegHand) +" for " + pegRuns(pegHand) 
							+ "\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
					}
				if (pegScore[0] == 15)
					{
						piScore[playerNumber - 1] += 2;
						System.out.println("15 for 2\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
					} 
				if (pegScore[0] == 31)
					{
						piScore[playerNumber - 1] += 2;
						System.out.println("31 for 2\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
						pegScore[0] = 0;
					}
				if (determineValue(pegHand[2], 1) == determineValue(pegHand[3], 1))
					{
						if (determineValue(pegHand[0], 1) == determineValue(pegHand[1], 1) 
								&& determineValue(pegHand[1], 1) == determineValue(pegHand[2], 1))
							{
								piScore[playerNumber - 1] += 12;
								System.out.println("Four of a kind for 12\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
							}
						else if (determineValue(pegHand[1], 1) == determineValue(pegHand[2], 1))
							{
								piScore[playerNumber - 1] += 6;
								System.out.println("Three of a kind for 6\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
							}
						
						else
							{
								piScore[playerNumber - 1] += 2;
								System.out.println("Pair for 2\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
							}
					}		
			}
			if (effSize == 5)
				{
					if (pegRuns(pegHand) > 0)
						{
							piScore[playerNumber - 1] += pegRuns(pegHand);
							System.out.println("Run of " + pegRuns(pegHand) +" for " + pegRuns(pegHand) 
									+ "\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
						}
					if (pegScore[0] == 15)
						{	
							piScore[playerNumber - 1] += 2;
							System.out.println("15 for 2\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
						} 
					if (pegScore[0] == 31)
					{
						piScore[playerNumber - 1] += 2;
						System.out.println("31 for 2\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
						pegScore[0] = 0;
					}
					if (determineValue(pegHand[3], 1) == determineValue(pegHand[4], 1))
						{
							if (determineValue(pegHand[1], 1) == determineValue(pegHand[2], 1) 
									&& determineValue(pegHand[2], 1) == determineValue(pegHand[3], 1))
								{
									piScore[playerNumber - 1] += 12;
									System.out.println("Four of a kind for 12\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
								}
							else if (determineValue(pegHand[2], 1) == determineValue(pegHand[3], 1))
								{
									piScore[playerNumber - 1] += 6;
									System.out.println("Three of a kind for 6\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
								}
						
							else	
								{
									piScore[playerNumber - 1] += 2;
									System.out.println("Pair for 2\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
								}
						}		
				}
			if (effSize == 6)
			{
				if (pegRuns(pegHand) > 0)
					{
						piScore[playerNumber - 1] += pegRuns(pegHand);
						System.out.println("Run of " + pegRuns(pegHand) +" for " + pegRuns(pegHand) 
								+ "\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
					}
				
				if (pegScore[0] == 15)
					{	
						piScore[playerNumber - 1] += 2;
						System.out.println("15 for 2\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
					} 
				if (pegScore[0] == 31)
				{
					piScore[playerNumber - 1] += 2;
					System.out.println("31 for 2\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
					pegScore[0] = 0;
				}
				if (determineValue(pegHand[5], 1) == determineValue(pegHand[6], 1))
					{
						if (determineValue(pegHand[3], 1) == determineValue(pegHand[4], 1)
								&& determineValue(pegHand[4], 1) == determineValue(pegHand[5], 1))
							{
								piScore[playerNumber - 1] += 12;
								System.out.println("Four of a kind for 12\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
							}
						else if (determineValue(pegHand[4], 1) == determineValue(pegHand[5], 1))
							{
								piScore[playerNumber - 1] += 6;
								System.out.println("Three of a kind for 6\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
							}
						else	
							{
								piScore[playerNumber - 1] += 2;
								System.out.println("Pair for 2\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
							}
					}		
			}
			if (effSize == 7)
				{
				if (pegRuns(pegHand) > 0)
					{
						piScore[playerNumber - 1] += pegRuns(pegHand);
						System.out.println("Run of " + pegRuns(pegHand) +" for " + pegRuns(pegHand) 
								+ "\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
					}
				if (pegScore[0] == 15)
					{	
						piScore[playerNumber - 1] += 2;
						System.out.println("15 for 2\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
					} 
				if (pegScore[0] == 31)
					{
						piScore[playerNumber - 1] += 2;
						System.out.println("31 for 2\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
						pegScore[0] = 0;
					}
				if (determineValue(pegHand[5], 1) == determineValue(pegHand[6], 1))
					{
						if (determineValue(pegHand[4], 1) == determineValue(pegHand[3], 1)
							&& determineValue(pegHand[4], 1) == determineValue(pegHand[5], 1))
							{
								piScore[playerNumber - 1] += 12;
								System.out.println("Four of a kind for 12\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
							}
						else if (determineValue(pegHand[4], 1) == determineValue(pegHand[5], 1))
							{
								piScore[playerNumber - 1] += 6;
								System.out.println("Three of a kind for 6\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
							}
						else	
							{
								piScore[playerNumber - 1] += 2;
								System.out.println("Pair for 2\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
							}
					}		
				}
			if (effSize == 8)
			{
				if (pegRuns(pegHand) > 0)
					{
						piScore[playerNumber - 1] += pegRuns(pegHand);
						System.out.println("Run of " + pegRuns(pegHand) +" for " + pegRuns(pegHand) 
							+ "\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
					}
				if (pegScore[0] == 15)
					{	
						piScore[playerNumber - 1] += 2;
						System.out.println("15 for 2\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
					} 
				if (pegScore[0] == 31)
					{
						piScore[playerNumber - 1] += 2;
						System.out.println("31 for 2\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
						pegScore[0] = 0;
					}
				if (determineValue(pegHand[6], 1) == determineValue(pegHand[7], 1))
					{
						if (determineValue(pegHand[4], 1) == determineValue(pegHand[5], 1)
								&& determineValue(pegHand[5], 1) == determineValue(pegHand[6], 1))
							{
								piScore[playerNumber - 1] += 12;
								System.out.println("Four of a kind for 12\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
							}
						else if (determineValue(pegHand[5], 1) == determineValue(pegHand[6], 1))
							{
								piScore[playerNumber - 1] += 6;
								System.out.println("Three of a kind for 6\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
							}
					else	
						{
							piScore[playerNumber - 1] += 2;
							System.out.println("Pair for 2\nPlayer " + playerNumber + "'s Score: " + piScore[playerNumber - 1]);
						}
				}		
			}
	}
	public static int sumHand(int[] vals, int start, int end)
	{	// Sums an array over [start, end)  the half open interval
		int sumHand = 0;
		for (int i = start; i < end; ++i)
			sumHand += vals[i];
		return sumHand;
	}
	public static int pegRuns(String[] pegHand)
	{
		int score = 0;
		boolean goOn = false;
		int effSize = effSize(pegHand);
		int[] vals = new int[effSize];
		for (int i = 0; i < effSize; ++i)
			vals[i] = determineValue(pegHand[i], 1);
		if (sumHand(vals, effSize - 3, effSize) == 3 * minValue(vals, effSize - 3, effSize) + 3)
			{
					goOn = true;
					score += 3;
			}
		int n = 4;
		int test = effSize - n;
		while (goOn == true && test > -1)
			{
				if (sumHand(vals, test, effSize) == n * minValue(vals, test, effSize) + (n*n - n) / 2)
					{
						score += 1;
						goOn = true;
						n += 1;
					}
			}
		return score;
	}
	public static int minValue(int[] vals, int start, int end)
	{
			int min = vals[start];
			for (int i = start; i < end; ++i)
				{
					if (vals[i] < min)
						min = vals[i];
				}
		
			return min;
	}
	public static void showPegHand(String[] pegHand, int[] pegScore)
	{
		System.out.println("\nPeg Score: " + pegScore[0]);
		System.out.print("Peg Hand: [ " + pegHand[0]);
		for (int i = 1; i < effSize(pegHand); ++i)		
			System.out.print(" , " + pegHand[i]);
		System.out.println(" ]");
	}
	public static int totalScores(String[] piHandCopy)
	{
		int total = 0;
		total = nOfAKind(piHandCopy, 5) + runs(piHandCopy) + count15s(piHandCopy) + sameSuit(piHandCopy);
		return total;
	}
	public static int nOfAKind(String[] hand, int effSize)
	{   // inputs a hand and outputs score based on having n of a kind
		int[] cardValue = new int[effSize];
		for (int i = 0; i < effSize; ++i)
				cardValue[i] = determineValue(hand[i], 1);    // identifies each card by its rank
		int score = 0;
		int cnti = 0;
		for (int i = 1; i <= 13 ; ++i)
			{	
				cnti = 0;                         // Resets cnt i so it counts properly
				for (int j = 0; j < effSize; ++j)
					{
						if (cardValue[j] == i)
							cnti += 1;
					}
				if (cnti == 2)
					score += 2;
				else if (cnti == 3)
					score += 6;
				else if (cnti == 4)
					score += 12;
			}
		return score;
	}
	public static int runs(String[] hand)
	{   // Determines if user has runs of any kind
		int score = 0;
		int[] scores = new int[5];
		for (int i = 0; i < 5; ++i)
			scores[i] = determineValue(hand[i], 1);
		Arrays.sort(scores);
				for (int i = 0; i < 3; ++i)
					{   // Checks effective cards 0-4 by threes for runs
						for (int j = i + 1; j < 4; ++j)
							{
								for (int k = j + 1; k < 5; ++k)
									{
										if (scores[i] + scores[j] + scores[k] ==
												3 * (Math.min(Math.min(scores[i], scores[j]), scores[k])) + 3)
											{
												if (scores [j] == scores[i] + 1 && scores[k] == scores[j] + 1)
													score += 3;
											}
									}
							}
					}
		
		if (score >= 3)
			{    // 0123
				if (scores[0] == scores[1] - 1 && scores[1] == scores[2] - 1
						&& scores[2] == scores[3] - 1)
							{	
								score = 4;
								if (sumHand(scores, 0, 5) == 5 * scores[0] + 10)
									score = 5;    // Run of 5
							}
				// 0124
				if (scores[0] == scores[1] - 1 && scores[1] == scores[2] - 1 
						&& scores[2] == scores[4] - 1)
							{	
								score = 4;
								if (sumHand(scores, 0, 5) == 5 * scores[0] + 10)
									score = 5;    
							}
				//0134
				if (scores[0] == scores[1] - 1 && scores[1] == scores[3] - 1 
						&& scores[3] == scores[4] - 1)
							{	
								score = 4;
								if (sumHand(scores, 0, 5) == 5 * scores[0] + 10)
									score = 5;    
							}
				//0234
				if (scores[0] == scores[2] - 1 && scores[2] == scores[3] - 1 
						&& scores[3] == scores[4] - 1)
							{	
								score = 4;
								if (sumHand(scores, 0, 5) == 5 * scores[0] + 10)
									score = 5;    
							}
				//1234
				if (scores[1] == scores[2] - 1 && scores[2] == scores[3] - 1 
						&& scores[3] == scores[4] - 1)
							{	
								score = 4;
								if (sumHand(scores, 0, 5) == 5 * scores[0] + 10)
									score = 5;    
							}
			}	
		return score;
	}
	public static int count15s(String[] hand)
		{   // Counts 15's by trying all pairs, 3's, 4's and full hand
			int score = 0;
			int[] scores = new int[5];
			for (int i = 0; i < 5; ++i)
				scores[i] = determineValue(hand[i], 2);
			
			for (int i = 0; i < 4 ; ++i)
				{   // Checks effective cards 0-4 pairwise for 15's
					for (int j = i + 1; j < 5; ++j)
						{
							if (scores[i] + scores[j] == 15)
								score += 2;
						}
				}
			for (int i = 0; i < 3; ++i)
				{   // Checks effective cards 0-4 by threes for 15's
					for (int j = i + 1; j < 4; ++j)
						{
							for (int k = j + 1; k < 5; ++k)
								{
									if (scores[i] + scores[j] + scores[k] == 15)
										score += 2;
								}
						}
				}
			if (scores[0] + scores[1] + scores[2] + scores[3] == 15)
				score += 2;
			if (scores[0] + scores[1] + scores[2] + scores[4] == 15)
				score += 2;
			if (scores[1] + scores[2] + scores[3] + scores[4] == 15)
				score += 2;
			if (scores[0] + scores[1] + scores[2] + scores[3] + scores[4] == 15)
				score += 2;
			return score;
		}
	public static int sameSuit(String[] hand)
	{   // effSize implicitly 5
		int score = 0;
		if(hand[0].indexOf("Spades") > 0 )
			if(hand[1].indexOf("Spades") > 0)
				if(hand[2].indexOf("Spades") > 0)
					if(hand[3].indexOf("Spades") > 0)
						{	score += 4;
							if(hand[4].indexOf("Spades") > 0)
							score += 1;
						}
		if(hand[0].indexOf("Clubs") > 0 )
			if(hand[1].indexOf("Clubs") > 0)
				if(hand[2].indexOf("Clubs") > 0)
					if(hand[3].indexOf("Clubs") > 0)
						{	score += 4;
							if(hand[4].indexOf("Clubs") > 0)
							score += 1;
						}
		if(hand[0].indexOf("Diamonds") > 0 )
			if(hand[1].indexOf("Diamonds") > 0)
				if(hand[2].indexOf("Diamonds") > 0)
					if(hand[3].indexOf("Diamonds") > 0)
						{	score += 4;
							if(hand[4].indexOf("Diamonds") > 0)
							score += 1;
						}
		if(hand[0].indexOf("Hearts") > 0 )
			if(hand[1].indexOf("Hearts") > 0)
				if(hand[2].indexOf("Hearts") > 0)
					if(hand[3].indexOf("Hearts") > 0)
						{	score += 4;
							if(hand[4].indexOf("Hearts") > 0)
							score += 1;
						}
		return score;
	}
	public static void scoreReport(String[] hand, int effSize)
	{
		System.out.print("Hand: [ " + hand[0]);
		for (int i = 1; i < effSize(hand); ++i)		// Reports Hand and score
			System.out.print(" , " + hand[i]);
		System.out.println(" ]");
		System.out.print("15's yield: " + count15s(hand) + "\nRuns yield: " + runs(hand)
				+ "\nN of a kind for " + nOfAKind(hand, effSize));
		if (sameSuit(hand) > 0)
			System.out.print("Same suit bonus: " + sameSuit(hand));
		int total = nOfAKind(hand, effSize) + runs(hand) + count15s(hand) + sameSuit(hand);
		System.out.println("\nTotal for round: " + total );
	}
	public static void showpiScore(int[] piScore)
		{
			System.out.println("\n*******************\nPlayer 1 Score: " + piScore[0]
				+ "\nPlayer 2 Score: " + piScore[1] + "\n*******************");
		}
	public static void printArray(String[][] board)
	{
		for (int i = 0; i < board.length; ++i)
			{
				for (int j = 0; j < board[1].length; ++j)
					System.out.print(board[i][j]);
				System.out.println();
			}
	}
	public static void gameBoard(int[] piScore)
		{
			int rowP1 = 0;
			int columnP1 = 0;
			int rowP2 = 0;
			int columnP2 = 0;
			
			// Assign rowp1
			if (piScore[0] < 41)
				rowP1 = 0;
			else if (piScore[0] > 40 && piScore[0] < 81)
				rowP1 = 4;
			else 
				rowP1 = 8;
			
			// Assign rowP2
			if (piScore[1] < 41)
				rowP2 = 2;
			else if (piScore[1] > 40 && piScore[1] < 81)
				rowP2 = 6;
			else 
				rowP2 = 10;
			
			// Assign columnP1
			if (piScore[0] > 0  && piScore[0] < 41)
				{
					if (piScore[0] % 5 == 0)                   
						columnP1 = piScore[0] / 5;
					else
						columnP1 = (piScore[0] / 5) + 1;
				}
			else if (piScore[0] > 40 && piScore[0] < 81)       
				{
					if (piScore[0] % 5 == 0)                 
						columnP1 = 17 - (piScore[0] / 5);
					else
						columnP1 = 16 - (piScore[0] / 5);
				}
			else if (piScore[0] > 80 && piScore[0] < 121)
				{
					if (piScore[0] % 5 == 0)
						columnP1 = (piScore[0] / 5) - 16;
					else
						columnP1 = (piScore[0] / 5) - 15;
				}
			// Assign columnP2
			if (piScore[1] > 0  && piScore[1] < 41)
				{
					if (piScore[1] % 5 == 0)
						columnP2 = piScore[1] / 5;         
					else
						columnP2 = (piScore[1] / 5) + 1;
				}
			else if (piScore[1] > 40 && piScore[1] < 81)
				{
					if (piScore[1] % 5 == 0)
						columnP2 = 17 - (piScore[1] / 5);   
					else
						columnP2 = 16 - (piScore[1] / 5);
				}
			else if (piScore[1] > 80 && piScore[1] < 121)
				{
					if (piScore[1] % 5 == 0)
						columnP2 = (piScore[1] / 5) - 16;
					else
						columnP2 = (piScore[1] / 5) - 15;
				}
			
			//////////////////////////////////////////////////
			//************** Create Generic Board *********///
			//////////////////////////////////////////////////
			String[][] board = genericBoard();
			
			/////////////////////////////////////////////////////
			// ********** Update Board from piScore ********** //
			/////////////////////////////////////////////////////
	
			if (piScore[0] > 0)
				board[rowP1][columnP1] = piPeg(piScore, 1, '*');
			if (piScore[1] > 0)
				board[rowP2][columnP2] = piPeg(piScore, 2, '$');
			if (piScore[0] < 121 && piScore[1] < 121)
				{
					drawLine('=', 45);
					printArray(board);
					drawLine('=', 45);
				}
			else
				System.out.println("\n********************\n      WINNER\n********************");
		}
	public static String piPeg(int[] piScore, int pNum, char c)
		{
			String piPeg = "";
			if ( (piScore[pNum - 1] > 0 && piScore[pNum - 1] < 41) 
					|| (piScore[pNum - 1] > 80 && piScore[pNum - 1] < 121) ) 
				{  	 
					if (piScore[pNum - 1] % 5 == 0)
						piPeg = "    " + c;
					else
						{	
							for (int i = 1; i < (piScore[pNum - 1] % 5) ; ++i)
								piPeg += " ";
							piPeg += c;
							for (int i = 0; i < 5 - (piScore[pNum - 1] % 5) ; ++i)
								piPeg += " ";
						}	
				}
			else if (piScore[pNum - 1] > 40 && piScore[pNum - 1] < 81)
				{
					if (piScore[pNum - 1] % 5 == 0)
						piPeg = c + "    ";
					else
						{
							for (int i = 0; i < (5 - (piScore[pNum - 1] % 5)); ++i)
								piPeg += " ";
							piPeg += c;
							for (int i = 1; i < piScore[pNum - 1] % 5; ++i)
								piPeg += " ";
						}
				}
			else
				piPeg = "XXXXX";
			return piPeg;
		}
	public static String[][] genericBoard()
		{
		// Declare board 2-D Array
		String[][] board = new String[11][10];
		
		// First Row: Player 1's pegs
		board[0][0] = "* |";
		for (int i = 1; i < 9; ++i)
			board[0][i] = "     ";
		board[0][9] = " | ";
		
		// Second Row: Peg Scores [1 - 40]
		board[1][0] = "  |"; 
		board[1][1] = "    5";
		for (int i = 2; i < 9; ++i)
			board[1][i] = "   " + (5 * i); 
		board[1][9] = " | ";
		
		// Third Row: Player 2's pegs
		board[2][0] = "$ |";
		for (int i = 1; i < 9; ++i)
			board[2][i] = "     ";
		board[2][9] = " | ";
		
		// Fourth Row: Gap row
		board[3][0] = "  |";
		for (int i = 1; i < 9; ++i)
			board[3][i] = "=====";
		board[3][9] = " | ";
		
		// Fifth Row: Player 1's Pegs
		board[4][0] = "  |"; 
		for (int i = 1; i < 9; ++i)
			board[4][i] = "     ";
		board[4][9] = " | ";
		
		// Sixth Row: Peg Scores [80 - 41]
		board[5][0] = "  |";
		for (int i = 1; i < 9; ++i)
			board[5][i] = (80 - (5 * (i - 1))) + "   ";
		board[5][9] = " | ";
		
		// Seventh Row: Player 2's Pegs
		board[6][0] = "  |";
		for (int i = 1; i < 9; ++i)
			board[6][i] = "     ";
		board[6][9] = " | ";
		
		// Eighth Row: Gap Row
		board[7][0] = "  |";
		for (int i = 1; i < 9; ++i)
			board[7][i] = "=====";
		board[7][9] = " | ";
		
		// Ninth Row: Player 1's pegs
		board[8][0] = "  |";
		for (int i = 1; i < 9; ++i)
			board[8][i] = "     ";
		board[8][9] = " | ";
		
		// Tenth Row: Peg Scores [81 - 120]
		board[9][0] = "  |";
		for (int i = 1; i < 4; ++i)
			board[9][i] = "   " + (80 + (5 * i));
		for (int i = 4; i < 9; ++i)
			board[9][i] = "  " + (80 + (5 * i));
		board[9][9] = " | ";
		
		// Eleventh Row: Player 2's pegs
		board[10][0] = "  |";
		for (int i = 1; i < 9; ++i)
			board[10][i] = "     ";
		board[10][9] = " | ";
		
		return board;
		}
	public static void drawLine(char c, int length)
		{
			for (int i = 0; i < length; ++i)
				System.out.print(c);
			System.out.println();
		}
	public static void displayWinner(int[] piScore)
		{
			skunk(piScore);
		if (piScore[0] > piScore[1])
			System.out.print("Congratulations Player 1!");
		else 
			System.out.print("Congratulations Player 2!");
		}
	public static void skunk(int[] piScore)
	{
		if (piScore[0] - piScore[1] > 30)
			System.out.println("Player 2 got skunked!");
		else if (piScore[1] - piScore[0] > 30)
			System.out.println("Player 1 got skunked!");
	}
}
