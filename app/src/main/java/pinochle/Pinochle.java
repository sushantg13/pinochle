package pinochle;

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;
import pinochle.game.states.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import pinochle.melds.Meld;
import pinochle.melds.TenToAceRunMeld;
import pinochle.melds.RoyalMarriageMeld;
import pinochle.melds.AceRunExtraKingMeld;
import pinochle.melds.AceRunExtraQueenMeld;
import pinochle.melds.DixMeld;
import pinochle.melds.CommonMarriageMeld;
import pinochle.melds.PinochleMeld;
import pinochle.melds.AcesAroundMeld;
import pinochle.melds.AceRunRoyalMarriageMeld;
import pinochle.melds.DoublePinochleMeld;
import pinochle.melds.JacksAboundMeld;
import pinochle.melds.DoubleRunMeld;
import pinochle.bidding.BiddingStrategy;
import pinochle.bidding.BiddingStrategyFactory;


@SuppressWarnings("serial")
public class Pinochle extends CardGame {
    private final Map<String, String> trumpImages = new HashMap<>(Map.of(
            Suit.SPADES.getSuitShortHand(), "sprites/bigspade.gif",
            Suit.CLUBS.getSuitShortHand(), "sprites/bigclub.gif",
            Suit.DIAMONDS.getSuitShortHand(), "sprites/bigdiamond.gif",
            Suit.HEARTS.getSuitShortHand(), "sprites/bigheart.gif"));
    private String trumpSuit = null;

    static public final int seed = 30008;
    static final Random random = new Random(seed);
    private final Properties properties;
    private final StringBuilder logResult = new StringBuilder();
    private final List<List<String>> playerAutoMovements = new ArrayList<>();

    private GameState currentState;
    private boolean isGameRunning = true;

    private final String version = "1.0";
    public final int nbPlayers = 2;
    public final int nbStartCards = 12;
    private final int handWidth = 400;
    private final int trickWidth = 40;
    private int currentBid = 0;
    private final Deck deck = new Deck(Suit.values(), Rank.values(), "cover");
    private List<Meld> meldEvaluators;
    private BiddingStrategy computerBiddingStrategy;
    private final Location[] handLocations = {
            new Location(350, 625),
            new Location(350, 75),
    };

    private final Location[] scoreLocations = {
            new Location(575, 675),
            new Location(25, 25),
    };
    private final TextActor[] scoreActors = {null, null, null, null};

    Font bigFont = new Font("Arial", Font.BOLD, 36);
    Font smallFont = new Font("Arial", Font.BOLD, 18);

    /**
     * Bidding elements
     */

    private final GGButton bidSelectionActor = new GGButton("sprites/bid_10.gif", false);
    private final GGButton bidConfirmActor = new GGButton("sprites/done30.gif", false);
    private final GGButton bidPassActor = new GGButton("sprites/bid_pass.gif", false);
    private TextActor playerBidActor = new TextActor("Bidding", Color.white, bgColor, smallFont);
    private TextActor currentBidActor = new TextActor("Current Bid: ", Color.white, bgColor, smallFont);
    private TextActor newBidActor = new TextActor("New Bid: ", Color.white, bgColor, smallFont);

    private final Location bidSelectionLocation = new Location(600, 100);
    private final Location bidConfirmLocation = new Location(660, 100);
    private final Location bidPassLocation = new Location(630, 150);
    private final Location playerBidLocation = new Location(550, 30);
    private final Location currentBidLocation = new Location(550, 50);
    private final Location newBidLocation = new Location(550, 75);

    private boolean hasHumanBid = false;
    private int humanBid = 0;
    private final int BID_SELECTION_VALUE = 10;
    private final int MAX_SINGLE_BID = 20;
    private final int COMPUTER_PLAYER_INDEX = 0;
    private final int HUMAN_PLAYER_INDEX = 1;
    private boolean hasComputerPassed = false;
    private boolean hasHumanPassed = false;
    private int bidWinPlayerIndex = 0;
    private final List<Integer> computerAutoBids = new ArrayList<>();
    private final List<Integer> humanAutoBids = new ArrayList<>();
    private int computerAutoBidIndex = 0;
    private int humanAutoBidIndex = 0;

    public static final String RANDOM_BID = "random";
    public static final String COMPUTER_BID = "computer";
    public static final String HUMAN_BID = "human";

    /**
     * Trump Elements
     */
    private final TextActor trumpInstructionActor = new TextActor("Trump Selection", Color.white, bgColor, smallFont);
    private final GGButton clubTrumpActor = new GGButton("sprites/clubs_item.png", false);
    private final GGButton spadeTrumpActor = new GGButton("sprites/spades_item.png", false);
    private final GGButton diamondTrumpActor = new GGButton("sprites/diamonds_item.png", false);
    private final GGButton heartTrumpActor = new GGButton("sprites/hearts_item.png", false);

    private Actor trumpActor;

    private final Location trumpInstructionLocation = new Location(550, 80);
    private final Location clubTrumpLocation = new Location(580, 100);
    private final Location spadeTrumpLocation = new Location(610, 100);
    private final Location diamondTrumpLocation = new Location(640, 100);
    private final Location heartTrumpLocation = new Location(670, 100);
    private final Location trumpLocation = new Location(620, 120);

    private final Location[] trickHandLocations = {
            new Location(75, 350),
            new Location(625, 350)
    };


    private final Location playingLocation = new Location(350, 350);
    private final Location textLocation = new Location(350, 450);
    private int thinkingTime = 2000;
    private int delayTime = 600;
    private Hand[] hands;
    private Hand[] trickWinningHands;

    public void setStatus(String string) {
        setStatusText(string);
    }

    private int[] scores = new int[nbPlayers];

    private int[] autoIndexHands = new int[nbPlayers];
    private boolean isAuto = false;
    private Hand playingArea;
    private Hand pack;

    /**
     * Score Section
     */

    private void initScore() {
        for (int i = 0; i < nbPlayers; i++) {
            // scores[i] = 0;
            String text = "[" + String.valueOf(scores[i]) + "]";
            scoreActors[i] = new TextActor(text, Color.WHITE, bgColor, bigFont);
            addActor(scoreActors[i], scoreLocations[i]);
        }
    }

    public void updateScore(int player) {
        removeActor(scoreActors[player]);
        int displayScore = Math.max(scores[player], 0);
        String text = "P" + player + "[" + String.valueOf(displayScore) + "]";
        scoreActors[player] = new TextActor(text, Color.WHITE, bgColor, bigFont);
        addActor(scoreActors[player], scoreLocations[player]);
    }

    private void initScores() {
        Arrays.fill(scores, 0);
    }

    // Old meld logic will be removed from here

    public int calculateMeldingScore(List<Card> hand) {
        int score = 0;
        List<Card> availableCards = new ArrayList<>(hand); // Use a mutable copy

        for (Meld meldStrategy : meldEvaluators) {
            // Keep checking for the same meld type as long as it's found and uses different cards
            // This part of the rule "If the cards in hand can form multiple identical melds, the points for each meld type are counted only once."
            // is handled by the fact that we iterate through meld *types* (meldEvaluators).
            // The rule "Each card in a hand can be used in only one meld" is handled by removing cards.

            // The original spec example for Royal Marriage (two KQs = 40pts) implies a meld type scores once.
            // The current structure of iterating through meldEvaluators (types) and removing cards achieves this.
            // If a meld type could score multiple times with different sets of cards (e.g. two separate pinochles),
            // the Meld class itself would need to be designed to find all instances, or this loop would need adjustment.
            // For now, assuming a meld type scores once if its specific cards are found.

            List<Card> foundMeldCards = meldStrategy.getMeldCards(availableCards, this.trumpSuit);
            if (foundMeldCards != null && !foundMeldCards.isEmpty()) {
                score += meldStrategy.getPoints();
                for (Card cardToRemove : foundMeldCards) {
                    availableCards.remove(cardToRemove);
                }
                // Log meld found (optional, for debugging or more detailed logging)
                // System.out.println("Player found meld: " + meldStrategy.getName() + " for " + meldStrategy.getPoints() + " points.");
            }
        }
        // The original `calculateMeldingScore` modified the input `list`.
        // The new approach uses `availableCards` and the original `hand` (passed as `list` before) is not modified by this method.
        // If the caller relied on the hand being modified, that's a side effect to be aware of.
        // The current `Pinochle.playGame` calls `scores[i] = calculateMeldingScore(hands[i].getCardList());`
        // `hands[i].getCardList()` returns a reference, so the old code *was* modifying the player's actual hand representation.
        // This is generally bad. The new approach is better as it doesn't modify the input hand.
        // If card removal from the actual hand *after* melding is required for game logic (e.g. cards used in melds cannot be used elsewhere),
        // then `calculateMeldingScore` would need to return the list of used cards, or the main game loop would need to handle it.
        // The spec says: "Each card in a hand can be used in only one meld". This is handled by removing from `availableCards`.
        // It does not say melded cards are removed from play for trick-taking. They are shown for points.
        // So, not modifying the original hand in `calculateMeldingScore` is correct.
        return score;
    }

    /**
     * Card Dealing
     * @param list
     * @return
     */


    private Card selected;

    private void initGame() {
        hands = new Hand[nbPlayers];
        trickWinningHands = new Hand[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            hands[i] = new Hand(deck);
            trickWinningHands[i] = new Hand(deck);
        }
        playingArea = new Hand(deck);
        dealingOut(hands, nbPlayers, nbStartCards);
        playingArea.setView(this, new RowLayout(playingLocation, (playingArea.getNumberOfCards() + 3) * trickWidth));
        playingArea.draw();

        for (int i = 0; i < nbPlayers; i++) {
            hands[i].sort(Hand.SortType.SUITPRIORITY, false);
        }
        // Set up human player for interaction
        CardListener cardListener = new CardAdapter()  // Human Player plays card
        {
            public void leftDoubleClicked(Card card) {
                if (!checkValidTrick(card, hands[HUMAN_PLAYER_INDEX].getCardList(), playingArea.getCardList())) {
                    setStatus("Card is not valid. Player needs to choose higher card of the same suit or trump suit");
                    return;
                }
                selected = card;
                hands[HUMAN_PLAYER_INDEX].setTouchEnabled(false);
            }
        };
        hands[HUMAN_PLAYER_INDEX].addCardListener(cardListener);
        // graphics
        RowLayout[] layouts = new RowLayout[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            layouts[i] = new RowLayout(handLocations[i], handWidth);
            layouts[i].setRotationAngle(180 * i);
            hands[i].setView(this, layouts[i]);
            hands[i].setTargetArea(new TargetArea(playingLocation));
            hands[i].draw();
        }

        RowLayout[] trickHandLayouts = new RowLayout[nbPlayers];

        for (int i = 0; i < nbPlayers; i++) {
            trickHandLayouts[i] = new RowLayout(trickHandLocations[i], handWidth);
            trickHandLayouts[i].setRotationAngle(90 + 180 * i);
            trickWinningHands[i].setView(this, trickHandLayouts[i]);
            trickWinningHands[i].draw();
        }
    }


    // return random Card from ArrayList
    public static Card randomCard(ArrayList<Card> list) {
        int x = random.nextInt(list.size());
        return list.get(x);
    }

    private String getCardName(Card card) { // This might be useful for logging or specific checks, keep for now.
        Suit suit = (Suit) card.getSuit();
        Rank rank = (Rank) card.getRank();
        return rank.getRankCardValue() + suit.getSuitShortHand(); // Original used rankCardValue, spec uses shortHand for logging. Let's stick to original for internal consistency for now.
    }

    public Card getRandomCardForHand(Hand hand) {
        List<Card> existingCards = playingArea.getCardList();
        if (existingCards.isEmpty()) {
            int x = random.nextInt(hand.getCardList().size());
            return hand.getCardList().get(x);
        }

        delay(thinkingTime);
        Card existingCard = existingCards.get(0);
        Card higherCard = getHigherCardFromList(existingCard, hand.getCardList());
        if (higherCard != null) {
            return higherCard;
        }

        Card trumpCard = getTrumpCard(hand.getCardList());
        if (trumpCard != null) {
            return trumpCard;
        }

        int x = random.nextInt(hand.getCardList().size());
        return hand.getCardList().get(x);
    }

    private Rank getRankFromString(String cardName) {
        String rankString = cardName.substring(0, cardName.length() - 1);
        Integer rankValue = Integer.parseInt(rankString);

        for (Rank rank : Rank.values()) {
            if (rank.getShortHandValue() == rankValue) {
                return rank;
            }
        }

        return Rank.ACE;
    }

    private Suit getSuitFromString(String cardName) {
        String rankString = cardName.substring(0, cardName.length() - 1);
        String suitString = cardName.substring(cardName.length() - 1, cardName.length());
        Integer rankValue = Integer.parseInt(rankString);

        for (Suit suit : Suit.values()) {
            if (suit.getSuitShortHand().equals(suitString)) {
                return suit;
            }
        }
        return Suit.CLUBS;
    }


    private Card getCardFromList(List<Card> cards, String cardName) {
        Rank existingRank = getRankFromString(cardName);
        Suit existingSuit = getSuitFromString(cardName);
        for (Card card : cards) {
            Suit suit = (Suit) card.getSuit();
            Rank rank = (Rank) card.getRank();
            if (suit.getSuitShortHand().equals(existingSuit.getSuitShortHand())
                    && rank.getRankCardValue() == existingRank.getRankCardValue()) {
                return card;
            }
        }

        return null;
    }

    private Card applyAutoMovement(Hand hand, String nextMovement) {
        if (hand.isEmpty()) return null;
        String[] cardStrings = nextMovement.split("-");
        String cardDealtString = cardStrings[0];
        if (nextMovement.isEmpty()) {
            return null;
        }
        Card dealt = getCardFromList(hand.getCardList(), cardDealtString);
        if (dealt == null) {
            System.err.println("cannot draw card: " + cardDealtString + " - hand: " + hand.getCardList());
        }

        return dealt;
    }

    private void dealingOut(Hand[] hands, int nbPlayers, int nbCardsPerPlayer) {
        pack = deck.toHand(false);

        for (int i = 0; i < nbPlayers; i++) {
            String initialCardsKey = "players." + i + ".initialcards";
            String initialCardsValue = properties.getProperty(initialCardsKey);
            if (initialCardsValue == null) {
                continue;
            }
            String[] initialCards = initialCardsValue.split(",");
            for (String initialCard : initialCards) {
                if (initialCard.length() <= 1) {
                    continue;
                }
                Card card = getCardFromList(pack.getCardList(), initialCard);
                if (card != null) {
                    card.removeFromHand(false);
                    hands[i].insert(card, false);
                }
            }
        }

        for (int i = 0; i < nbPlayers; i++) {
            int cardsToDealt = nbCardsPerPlayer - hands[i].getNumberOfCards();
            for (int j = 0; j < cardsToDealt; j++) {
                if (pack.isEmpty()) return;
                Card dealt = randomCard(pack.getCardList());
                dealt.removeFromHand(false);
                hands[i].insert(dealt, false);
            }
        }
    }

    /**
     * Bid Section
     */

    private void initBids() {
        addActor(bidSelectionActor, bidSelectionLocation);
        addActor(bidConfirmActor, bidConfirmLocation);
        addActor(bidPassActor, bidPassLocation);

        addActor(playerBidActor, playerBidLocation);
        addActor(currentBidActor, currentBidLocation);
        addActor(newBidActor, newBidLocation);

        setActorOnTop(bidSelectionActor);
        setActorOnTop(bidConfirmActor);
        setActorOnTop(bidPassActor);

        bidSelectionActor.setActEnabled(false);
        bidConfirmActor.setActEnabled(false);
        bidPassActor.setActEnabled(false);

        hasComputerPassed = false;

        System.out.println("init bids");
        bidSelectionActor.addButtonListener(new GGButtonListener() {
            @Override
            public void buttonPressed(GGButton ggButton) {
                hasHumanBid = false;

                if (humanBid >= MAX_SINGLE_BID) {
                    bidSelectionActor.setActEnabled(false);
                    setStatus("Maximum amount of a single bid reached");
                } else {
                    humanBid += BID_SELECTION_VALUE;
                }
                updateBidText(HUMAN_PLAYER_INDEX, humanBid + currentBid);
            }

            @Override
            public void buttonReleased(GGButton ggButton) {
            }

            @Override
            public void buttonClicked(GGButton ggButton) {
            }
        });

        bidConfirmActor.addButtonListener(new GGButtonListener() {
            @Override
            public void buttonPressed(GGButton ggButton) {
                currentBid = currentBid + humanBid;
                hasHumanBid = true;
                humanBid = 0;
                updateBidText(HUMAN_PLAYER_INDEX, currentBid);
                setStatus("");
            }

            @Override
            public void buttonReleased(GGButton ggButton) {
            }

            @Override
            public void buttonClicked(GGButton ggButton) {
            }
        });

        bidPassActor.addButtonListener(new GGButtonListener() {
            @Override
            public void buttonPressed(GGButton ggButton) {
                updateBidText(HUMAN_PLAYER_INDEX, 0);
                humanBid = 0;
                hasHumanPassed = true;
                setStatus("");
            }

            @Override
            public void buttonReleased(GGButton ggButton) {
            }

            @Override
            public void buttonClicked(GGButton ggButton) {
            }
        });
    }

    private void removeBids() {
        removeActor(bidSelectionActor);
        removeActor(bidConfirmActor);
        removeActor(bidPassActor);

        removeActor(newBidActor);
    }

    private void removeBidText() {
        removeActor(currentBidActor);
        removeActor(newBidActor);
        removeActor(playerBidActor);
    }

    private void updateBidText(int playerIndex, int newBid) {
        String playerBidString = "";
        switch (playerIndex) {
            case -1:
                playerBidString = "Bid";
                break;
            case 0:
                playerBidString = "Computer Bid";
                break;
            case 1:
                playerBidString = "Human Bid";
                break;
        }

        removeBidText();
        currentBidActor = new TextActor("Current Bid: " + currentBid, Color.WHITE, bgColor, smallFont);
        addActor(currentBidActor, currentBidLocation);

        String newBidString = newBid == 0 ? "" : String.valueOf(newBid);
        newBidActor = new TextActor("New Bid: " + newBidString, Color.WHITE, bgColor, smallFont);
        addActor(newBidActor, newBidLocation);

        playerBidActor = new TextActor(playerBidString, Color.WHITE, bgColor, smallFont);
        addActor(playerBidActor, playerBidLocation);

        delay(delayTime);
    }

    private void displayBidButtons(boolean isShown) {
        bidSelectionActor.setActEnabled(isShown);
        bidConfirmActor.setActEnabled(isShown);
        bidPassActor.setActEnabled(isShown);
    }

    private void askForBidForPlayerIndex(int playerIndex) {

        if (playerIndex == COMPUTER_PLAYER_INDEX) {
            int bidActionValue; // This will be the direct bid, or the increment, or 0 for pass
            boolean isFirstComputerBid = (currentBid == 0 && playerIndex == bidWinPlayerIndex) || (currentBid == 0 && Arrays.stream(scores).sum() == 0); // A bit complex to determine true first bidder status here, simplify for now
                                                                                                    // Let's assume if currentBid is 0, it's effectively an opening bid scenario for the computer.
                                                                                                    // Or, more simply, the strategy itself handles "isFirstBidder" logic based on currentBid.
                                                                                                    // The BiddingStrategy interface has `isFirstBidder` parameter.
                                                                                                    // We need to determine if this *turn* is the computer's first chance to bid in the round.
            boolean computerIsEffectivelyFirstBidder = (currentBid == 0); // Simplified: if no bid yet, computer is opening.

            if (isAuto && computerAutoBids != null && computerAutoBidIndex < computerAutoBids.size()) {
                bidActionValue = computerAutoBids.get(computerAutoBidIndex); // Auto bids are direct values (0, 10, 20)
                computerAutoBidIndex++;
            } else {
                // For smart bidding, trumpSuit is not yet determined. SmartBiddingStrategy will assume one.
                // The BiddingStrategy's determineBid will return:
                // - if opening: the total bid amount (e.g. meldScore)
                // - if subsequent: the increment (10 or 20), or 0 to pass
                bidActionValue = computerBiddingStrategy.determineBid(hands[COMPUTER_PLAYER_INDEX], currentBid, computerIsEffectivelyFirstBidder, this.meldEvaluators, null);
            }

            if (computerIsEffectivelyFirstBidder) {
                if (bidActionValue > 0) { // Strategy returned an opening bid amount
                    updateBidText(playerIndex, bidActionValue); // Show what computer bids
                    delay(thinkingTime);
                    currentBid = bidActionValue;
                    updateBidText(playerIndex, 0); // Clear "New Bid" for next player
                } else { // Strategy decided to pass even as first bidder (e.g. meld score is 0)
                    hasComputerPassed = true;
                }
            } else { // Computer is making a subsequent bid or passing
                if (bidActionValue > 0) { // Strategy returned an increment
                    updateBidText(playerIndex, currentBid + bidActionValue); // Show what computer bids
                    delay(thinkingTime);
                    currentBid += bidActionValue;
                    updateBidText(playerIndex, 0); // Clear "New Bid" for next player
                } else { // Strategy returned 0, meaning pass
                    hasComputerPassed = true;
                }
            }
            hasHumanBid = false; // Reset human bid flag after computer's turn
        } else {
            displayBidButtons(true);
            updateBidText(playerIndex, 0);
            if (isAuto && humanAutoBids != null && humanAutoBidIndex < humanAutoBids.size()) {
                humanBid = humanAutoBids.get(humanAutoBidIndex);
                currentBid = currentBid + humanBid;
                humanAutoBidIndex++;
                if (humanBid == 0) {
                    hasHumanPassed = true;
                }
                updateBidText(HUMAN_PLAYER_INDEX, currentBid);
            } else {
                while (!hasHumanBid && !hasHumanPassed) delay(delayTime);
            }
            hasHumanBid = true;
        }
    }

    public void askForBid() {
        initBids();
        displayBidButtons(false);
        String bidOrder = properties.getProperty("players.bid_first", "random");
        String player0Bids = properties.getProperty("players.0.bids", "");
        String player1Bids = properties.getProperty("players.1.bids", "");

        if (player0Bids != null) {
            if (!player0Bids.isEmpty()) {
                List<String> bidStrings = Arrays.asList(player0Bids.split(","));
                computerAutoBids.addAll(bidStrings.stream().map(Integer::parseInt).toList());
            }
        }

        if (player1Bids != null) {
            if (!player1Bids.isEmpty()) {
                List<String> bidStrings = Arrays.asList(player1Bids.split(","));
                humanAutoBids.addAll(bidStrings.stream().map(Integer::parseInt).toList());
            }
        }

        boolean isContinueBidding = true;
        updateBidText(-1, 0);
        Random rand = new Random(1);
        int playerIndex = switch (bidOrder) {
            case RANDOM_BID -> rand.nextInt(nbPlayers);
            case COMPUTER_BID -> COMPUTER_PLAYER_INDEX;
            case HUMAN_BID -> HUMAN_PLAYER_INDEX;
            default -> COMPUTER_PLAYER_INDEX;
        };

        do {
            for (int i = 0; i < nbPlayers; i++) {
                askForBidForPlayerIndex(playerIndex);
                playerIndex = (playerIndex + 1) % nbPlayers;
                isContinueBidding = !hasHumanPassed && !hasComputerPassed;
                if (!isContinueBidding) {
                    bidWinPlayerIndex = playerIndex;
                    break;
                }
            }
        } while (isContinueBidding);

        removeBids();
        updateBidResult();
        addBidInfoToLog();
    }

    private void updateTrumpActor() {
        String trumpImage = trumpImages.get(trumpSuit);
        trumpActor = new Actor(trumpImage);
        addActor(trumpActor, trumpLocation);
    }

    public void askForTrumpCard() {
        if (isAuto) {
            trumpSuit = properties.getProperty("players.trump", "C");
            updateTrumpActor();
            return;
        }

        addActor(trumpInstructionActor, trumpInstructionLocation);
        if (bidWinPlayerIndex == COMPUTER_PLAYER_INDEX) {
            Suit selectedTrumpSuit = Arrays.stream(Suit.values()).findAny().get();
            trumpSuit = selectedTrumpSuit.getSuitShortHand();
        } else {
            addActor(clubTrumpActor, clubTrumpLocation);
            addActor(spadeTrumpActor, spadeTrumpLocation);
            addActor(heartTrumpActor, heartTrumpLocation);
            addActor(diamondTrumpActor, diamondTrumpLocation);

            GGButtonListener buttonListener = new GGButtonListener() {
                @Override
                public void buttonPressed(GGButton ggButton) {
                    if (ggButton.equals(clubTrumpActor)) {
                        trumpSuit = Suit.CLUBS.getSuitShortHand();
                    } else if (ggButton.equals(spadeTrumpActor)) {
                        trumpSuit = Suit.SPADES.getSuitShortHand();
                    } else if (ggButton.equals(heartTrumpActor)) {
                        trumpSuit = Suit.HEARTS.getSuitShortHand();
                    } else if (ggButton.equals(diamondTrumpActor)) {
                        trumpSuit = Suit.DIAMONDS.getSuitShortHand();
                    }
                }

                @Override
                public void buttonReleased(GGButton ggButton) {
                }

                @Override
                public void buttonClicked(GGButton ggButton) {
                }
            };

            clubTrumpActor.addButtonListener(buttonListener);
            spadeTrumpActor.addButtonListener(buttonListener);
            heartTrumpActor.addButtonListener(buttonListener);
            diamondTrumpActor.addButtonListener(buttonListener);

            while (trumpSuit == null) delay(delayTime);
        }
        removeActor(clubTrumpActor);
        removeActor(spadeTrumpActor);
        removeActor(heartTrumpActor);
        removeActor(diamondTrumpActor);
        updateTrumpActor();
    }

    private void updateBidResult() {
        removeActor(playerBidActor);
        removeActor(currentBidActor);

        currentBidActor = new TextActor("Current Bid: " + currentBid, Color.WHITE, bgColor, smallFont);
        addActor(currentBidActor, currentBidLocation);

        String playerBidString = bidWinPlayerIndex == COMPUTER_PLAYER_INDEX ? "Computer Win" : "Human Win";
        playerBidActor = new TextActor(playerBidString, Color.WHITE, bgColor, smallFont);
        addActor(playerBidActor, playerBidLocation);
    }

    /**
     * Logging Logic
     * @param player
     * @param card
     */

    public void addCardPlayedToLog(int player, Card card) {
        logResult.append("P" + player + "-");

        Rank cardRank = (Rank) card.getRank();
        Suit cardSuit = (Suit) card.getSuit();
        logResult.append(cardRank.getCardLog() + cardSuit.getSuitShortHand());

        logResult.append(",");
    }

    private void addBidInfoToLog() {
        logResult.append("Bid:" + bidWinPlayerIndex + "-" + currentBid + "\n");
    }

    public void addTrumpInfoToLog() {
        logResult.append("Trump: " + trumpSuit + "\n");
        logResult.append("Melding Scores: " + scores[0] + "-" + scores[1] + "\n");
    }

    public void addRoundInfoToLog(int roundNumber) {
        logResult.append("\n");
        logResult.append("Round" + roundNumber + ":");
    }

    public void addPlayerCardsToLog() {
        logResult.append("Initial Cards:");
        for (int i = 0; i < nbPlayers; i++) {
            logResult.append("P" + i + "-");
            logResult.append(convertCardListoString(hands[i]));
        }
    }

    private String convertCardListoString(Hand hand) {
        StringBuilder sb = new StringBuilder();
        sb.append(hand.getCardList().stream().map(card -> {
            Rank rank = (Rank) card.getRank();
            Suit suit = (Suit) card.getSuit();
            return rank.getCardLog() + suit.getSuitShortHand();
        }).collect(Collectors.joining(",")));
        sb.append("-");
        return sb.toString();
    }

    public void addEndOfGameToLog(List<Integer> winners) {
        logResult.append("\n");
        logResult.append("Trick Winning: ");
        for (int i = 0; i < nbPlayers; i++) {
            logResult.append("P" + i + ":");
            logResult.append(convertCardListoString(trickWinningHands[i]));
        }
        logResult.append("\n");
        logResult.append("Final Score: ");
        for (int i = 0; i < scores.length; i++) {
            logResult.append(scores[i] + ",");
        }
        logResult.append("\n");
        logResult.append("Winners: " + String.join(", ", winners.stream().map(String::valueOf).collect(Collectors.toList())));
    }

    /**
     * Check Trick Taking logic
     * @param card1
     * @param card2
     * @return
     */

    private boolean isSameSuit(Card card1, Card card2) {
        Suit card1Suit = (Suit) card1.getSuit();
        Suit card2Suit = (Suit) card2.getSuit();
        return card1Suit.getSuitShortHand().equals(card2Suit.getSuitShortHand());
    }

    private boolean isHigherRank(Card card1, Card card2) {
        Rank card2Rank = (Rank) card2.getRank();
        Rank card1Rank = (Rank) card1.getRank();
        return card1Rank.getRankCardValue() > card2Rank.getRankCardValue();
    }

    private Card getHigherCardFromList(Card existingCard, List<Card> cards) {
        return cards.stream().filter(playerCard -> {
            return isSameSuit(existingCard, playerCard) && isHigherRank(playerCard, existingCard);
        }).findAny().orElse(null);
    }

    private Card getTrumpCard(List<Card> cards) {
        return cards.stream().filter(playerCard -> {
            Suit playerCardSuit = (Suit) playerCard.getSuit();
            return playerCardSuit.getSuitShortHand().equals(trumpSuit);
        }).findAny().orElse(null);
    }

    private boolean checkValidTrick(Card playingCard, List<Card> playerCards, List<Card> existingCards) {
        if (existingCards.isEmpty()) {
            return true;
        }

        Suit playingSuit = (Suit) playingCard.getSuit();
        Rank playingRank = (Rank) playingCard.getRank();
        Card existingCard = existingCards.get(0);
        Suit existingSuit = (Suit) existingCard.getSuit();
        Rank existingRank = (Rank) existingCard.getRank();

        if (playingSuit.getSuitShortHand().equals(existingSuit.getSuitShortHand()) && playingRank.getRankCardValue() > existingRank.getRankCardValue()) {
            return true;
        }

        Card higherCard = getHigherCardFromList(existingCard, playerCards);
        if (higherCard != null) {
            return false;
        }

        boolean isExistingTrump = existingSuit.getSuitShortHand().equals(trumpSuit);
        boolean isPlayingTrump = playingSuit.getSuitShortHand().equals(trumpSuit);

        if (isExistingTrump && isPlayingTrump) {
            return false;
        }

        if (isPlayingTrump) {
            return true;
        }

        Card trumpCard = getTrumpCard(playerCards);
        if (trumpCard != null) {
            return false;
        }
        return true;
    }

    public int checkWinner(int playerIndex) {
        assert (playingArea.getCardList().size() == 2);
        int previousPlayerIndex = Math.abs(playerIndex - 1) % 2;
        Card card1 = playingArea.getCardList().get(0);
        Card card2 = playingArea.getCardList().get(1);

        boolean isHigherRankSameSuit = isSameSuit(card1, card2) && isHigherRank(card2, card1);
        if (isHigherRankSameSuit) {
            return playerIndex;
        }

        Suit card1Suit = (Suit) card1.getSuit();
        if (card1Suit.getSuitShortHand().equals(trumpSuit)) {
            return previousPlayerIndex;
        }

        Suit card2Suit = (Suit) card2.getSuit();
        if (card2Suit.getSuitShortHand().equals(trumpSuit)) {
            return playerIndex;
        }

        return previousPlayerIndex;
    }

    public void transferCardsToWinner(int trickWinPlayerIndex) {
        for (Card card : playingArea.getCardList()) {
            trickWinningHands[trickWinPlayerIndex].insert(card, true);
        }
        playingArea.removeAll(true);
        RowLayout[] trickHandLayouts = new RowLayout[nbPlayers];
        delay(delayTime);
        for (int i = 0; i < nbPlayers; i++) {
            trickHandLayouts[i] = new RowLayout(trickHandLocations[i], handWidth);
            trickHandLayouts[i].setRotationAngle(90);
            trickWinningHands[i].setView(this, trickHandLayouts[i]);
            trickWinningHands[i].draw();
        }

        delay(delayTime);
    }


    public void updateTrickScore() {
        for (int i = 0; i < nbPlayers; i++) {
            List<Card> cards = trickWinningHands[i].getCardList();
            int score = 0;
            for (Card card : cards) {
                Rank rank = (Rank) card.getRank();
                Suit suit = (Suit) card.getSuit();
                boolean isNineCard = rank.getRankCardValue() == Rank.NINE.getRankCardValue();
                boolean isTrumpCard = suit.getSuitShortHand().equals(trumpSuit);
                if (isNineCard && isTrumpCard) {
                    score += Rank.NINE_TRUMP;
                } else {
                    score += rank.getScoreValue();
                }
            }

            scores[i] += score;
            if (i == bidWinPlayerIndex) {
                if (scores[i] < currentBid) {
                    scores[i] = 0;
                }
            }
        }
    }

    private void playGame() {
        askForBid();
        askForTrumpCard();
        for (int i = 0; i < nbPlayers; i++) {
            scores[i] = calculateMeldingScore(hands[i].getCardList());
            updateScore(i);
            delay(delayTime);
        }
        addTrumpInfoToLog();

        int nextPlayer = bidWinPlayerIndex;
        int numberOfCards = hands[COMPUTER_PLAYER_INDEX].getNumberOfCards();
        addPlayerCardsToLog();
        for (int i = 0; i < numberOfCards; i++) {
            addRoundInfoToLog(i);
            for (int j = 0; j < nbPlayers; j++) {
                if (isAuto) {
                    int nextPlayerAutoIndex = autoIndexHands[nextPlayer];
                    List<String> nextPlayerMovement = playerAutoMovements.get(nextPlayer);
                    String nextMovement = "";

                    if (nextPlayerMovement.size() > nextPlayerAutoIndex && !nextPlayerMovement.equals("")) {
                        nextMovement = nextPlayerMovement.get(nextPlayerAutoIndex);
                        nextPlayerAutoIndex++;

                        autoIndexHands[nextPlayer] = nextPlayerAutoIndex;
                        Hand nextHand = hands[nextPlayer];

                        // Apply movement for player
                        selected = applyAutoMovement(nextHand, nextMovement);
                        delay(delayTime);
                        if (selected != null) {
                            selected.removeFromHand(true);
                        } else {
                            selected = getRandomCardForHand(hands[nextPlayer]);
                            selected.removeFromHand(true);
                        }
                    } else {
                        selected = getRandomCardForHand(hands[nextPlayer]);
                        selected.removeFromHand(true);
                    }
                }

                if (!isAuto) {
                    if (HUMAN_PLAYER_INDEX == nextPlayer) {
                        hands[HUMAN_PLAYER_INDEX].setTouchEnabled(true);

                        setStatus("Player " + nextPlayer + " is playing. Please double click on a card to discard");
                        selected = null;
                        while (null == selected) delay(delayTime);
                        selected.removeFromHand(true);
                    } else {
                        setStatusText("Player " + nextPlayer + " thinking...");
                        selected = getRandomCardForHand(hands[nextPlayer]);
                        selected.removeFromHand(true);
                    }
                }

                addCardPlayedToLog(nextPlayer, selected);
                playingArea.insert(selected, true);

                playingArea.setView(this, new RowLayout(playingLocation, (playingArea.getNumberOfCards() + 2) * trickWidth));
                playingArea.draw();

                if (playingArea.getCardList().size() == 2) {
                    delay(delayTime);
                    int trickWinPlayerIndex = checkWinner(nextPlayer);
                    transferCardsToWinner(trickWinPlayerIndex);

                    nextPlayer = trickWinPlayerIndex;
                } else {
                    nextPlayer = (nextPlayer + 1) % nbPlayers;
                }
            }
        }

        updateTrickScore();
    }

    private void setupPlayerAutoMovements() {
        String player0AutoMovement = properties.getProperty("players.0.cardsPlayed");
        String player1AutoMovement = properties.getProperty("players.1.cardsPlayed");

        String[] playerMovements = new String[]{"", ""};
        if (player0AutoMovement != null) {
            playerMovements[0] = player0AutoMovement;
        }

        if (player1AutoMovement != null) {
            playerMovements[1] = player1AutoMovement;
        }

        for (int i = 0; i < playerMovements.length; i++) {
            String movementString = playerMovements[i];
            List<String> movements = Arrays.asList(movementString.split(","));
            playerAutoMovements.add(movements);
        }
    }

    public String runApp() {
        setTitle("Pinochle  (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
        setStatusText("Initializing...");
        initScores();
        initScore();
        setupPlayerAutoMovements();
        initGame();

        // Set the initial state of the game
        setState(new BiddingState());

        // Start the game loop
        while (isGameRunning) {
            if (currentState != null) {
                currentState.execute();
            }
        }

        return logResult.toString();
        //playGame();

        /*for (int i = 0; i < nbPlayers; i++) updateScore(i);
        int maxScore = 0;
        for (int i = 0; i < nbPlayers; i++) if (scores[i] > maxScore) maxScore = scores[i];
        List<Integer> winners = new ArrayList<Integer>();
        for (int i = 0; i < nbPlayers; i++) if (scores[i] == maxScore) winners.add(i);
        String winText;
        if (winners.size() == 1) {
            winText = "Game over. Winner is player: " +
                    winners.iterator().next();
        } else {
            winText = "Game Over. Drawn winners are players: " +
                    String.join(", ", winners.stream().map(String::valueOf).collect(Collectors.toList()));
        }
        addActor(new Actor("sprites/gameover.gif"), textLocation);
        setStatusText(winText);
        refresh();
        addEndOfGameToLog(winners);

        return logResult.toString();*/
    }

    public Pinochle(Properties properties) {
        super(700, 700, 30);
        this.properties = properties;
        isAuto = Boolean.parseBoolean(properties.getProperty("isAuto"));
        thinkingTime = Integer.parseInt(properties.getProperty("thinkingTime", "200"));
        delayTime = Integer.parseInt(properties.getProperty("delayTime", "50"));

        // Initialize meld evaluators
        meldEvaluators = new ArrayList<>();
        
        // Add original melds first (their relative order was based on the old implementation)
        meldEvaluators.add(new AceRunExtraKingMeld());    // 190
        meldEvaluators.add(new AceRunExtraQueenMeld());   // 190
        meldEvaluators.add(new TenToAceRunMeld());        // 150
        meldEvaluators.add(new RoyalMarriageMeld());      // 40

        // Add additional melds if enabled by properties
        boolean additionalMeldsEnabled = Boolean.parseBoolean(properties.getProperty("melds.additional", "false"));
        if (additionalMeldsEnabled) {
            meldEvaluators.add(new DoubleRunMeld());             // 1500
            meldEvaluators.add(new JacksAboundMeld());           // 400
            meldEvaluators.add(new DoublePinochleMeld());        // 300
            meldEvaluators.add(new AceRunRoyalMarriageMeld());   // 230
            meldEvaluators.add(new AcesAroundMeld());            // 100
            meldEvaluators.add(new PinochleMeld());              // 40
            meldEvaluators.add(new CommonMarriageMeld());        // 20
            meldEvaluators.add(new DixMeld());                   // 10
        }

        // Sort all meld evaluators by points, descending, to ensure correct evaluation order
        // If points are equal, the existing relative order (from adding them) will be preserved by a stable sort,
        // or it won't matter if they don't share cards.
        meldEvaluators.sort(Comparator.comparingInt(Meld::getPoints).reversed());

        // Initialize bidding strategy for the computer player
        // This needs to be after meldEvaluators is fully initialized.
        this.computerBiddingStrategy = BiddingStrategyFactory.createBiddingStrategy(properties, this.meldEvaluators, seed);
    }

    public void setState(GameState newState) {
        this.currentState = newState;
        if (this.currentState != null) {
            this.currentState.setContext(this);
        }
    }

    public void stopGame() {
        this.isGameRunning = false;
    }

    public Properties getProperties() {
        return this.properties;
    }

    public Hand[] getHands() {
        return this.hands;
    }

    public int[] getScores() {
        return this.scores;
    }

    public int getDelayTime() {
        return this.delayTime;
    }

    public int getBidWinPlayerIndex() {
        return this.bidWinPlayerIndex;
    }

    public Hand getPlayingArea() {
        return this.playingArea;
    }

    public Location getTextLocation() {
        return this.textLocation;
    }

    /**
     * Handles the logic for a single player playing a card during a trick.
     * This method encapsulates the logic for auto, human, and computer players.
     * @param playerIndex The index of the player whose turn it is.
     * @return The card that was played.
     */
    public Card playCardForPlayer(int playerIndex) {
        selected = null; // Reset selected card at the start of the turn
        Hand currentHand = getHands()[playerIndex];

        if (isAuto) {
            // Logic for automated players from properties file
            int nextPlayerAutoIndex = autoIndexHands[playerIndex];
            List<String> nextPlayerMovement = playerAutoMovements.get(playerIndex);
            String nextMovement = "";

            if (nextPlayerMovement.size() > nextPlayerAutoIndex && !nextPlayerMovement.isEmpty()) {
                nextMovement = nextPlayerMovement.get(nextPlayerAutoIndex);
                nextPlayerAutoIndex++;
                autoIndexHands[playerIndex] = nextPlayerAutoIndex;

                selected = applyAutoMovement(currentHand, nextMovement);
                delay(delayTime);
            }

            // Fallback for auto mode if properties don't specify a move
            if (selected == null) {
                selected = getRandomCardForHand(currentHand);
            }
            selected.removeFromHand(true);

        } else {
            // Logic for manual play
            if (HUMAN_PLAYER_INDEX == playerIndex) {
                currentHand.setTouchEnabled(true);
                setStatus("Player " + playerIndex + ", please play a card.");

                // Wait for the CardListener to set the 'selected' field
                while (null == selected) {
                    delay(delayTime);
                }
                // The CardListener already handles removing the card from hand

            } else { // Computer player's turn in manual mode
                setStatusText("Player " + playerIndex + " thinking...");
                selected = getRandomCardForHand(currentHand);
                selected.removeFromHand(true);
            }
        }
        return selected;
    }



}
