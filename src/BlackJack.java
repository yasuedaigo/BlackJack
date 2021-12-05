import java.util.Scanner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlackJack {
    private static final Scanner STDIN = new Scanner(System.in);
    private static boolean isSelectDrow = true;

    private static enum Players {
        PLAYER("あなた"), DEALER("ディーラー");

        private final String name;

        private Players(String name) {
            this.name = name;
        }

        private String getName() {
            return name;
        }
    }

    private static final int FIRST_HAND_NUMBER = 2;
    private static final int MAX_CARD_NUMBER = 12;
    private static final int DROW_BORDER = 17;
    private static final int BURST_BORDER = 21;
    private static final int DECK_TOP_INDEX = 0;
    private static final String YES_KEY = "Y";
    private static final String NO_KEY = "N";
    private static final int CARD_NUMBER_INDEX = 0;
    private static final int CARD_MARK_INDEX = 1;
    private static final int START_COINS = 100;
    private static final int BET_COINS = 10;
    private static final int BLACKJACK_COINS = 30;
    private static final int WIN_COINS = 20;

    private static int handCoin = START_COINS;
    private static List<String[]> playersHandList = new ArrayList<>() {
    };
    private static List<String[]> dealersHandList = new ArrayList<>() {
    };
    private static final List<String> NUMBERLIST = new ArrayList<>() {
        {
            add("A");
            add("2");
            add("3");
            add("4");
            add("5");
            add("6");
            add("7");
            add("8");
            add("9");
            add("10");
            add("J");
            add("Q");
            add("K");
        }
    };

    private static final List<Integer> BLACKJACK_CARD_LIST = new ArrayList<>() {
        {
            add(1);
            add(10);
            add(11);
            add(12);
            add(13);
        }
    };

    private static List<String[]> deckList = new ArrayList(){};


    private static enum Marks {
        HEART("ハート"), SPADE("スペード"), DIA("ダイア"), CLUB("クラブ");

        private final String name;

        private Marks(String name) {
            this.name = name;
        }

        private String getName() {
            return name;
        }
    }

    private static enum IRREGULAR_CARDS {
        A(1), J(11), Q(12), K(13);

        private final int number;

        private IRREGULAR_CARDS(int number) {
            this.number = number;
        }

        private int getNumber() {
            return number;
        }
    }

    private static final String TURNSTART_MESSAGE = "%sのターンです";
    private static final String DEALCARD_MESSAGE = "%sに「%sの%s」が配られました";
    private static final String NOTDROW_MESSAGE = "%sはカードを引きませんでした";
    private static final String DROWQUESTION_MESSAGE = "もう１枚カードを引きますか？(Y/N)：";
    private static final String SUM_MESSAGE = "%sの合計は%dです";
    private static final String WIN_MESSAGE = "%sの勝ちです";
    private static final String LOSE_MESSAGE = "%sの負けです";
    private static final String DRAW_MESSAGE = "引き分けです";
    private static final String NOTCORRECTCOMMAND_MESSAGE = "入力が正しくありません";
    private static final String COIN_MESSAGE = "手持ちコインは%dです";

    public static void main(String[] args) {
        while (!isFinishGame()) {
            playersHandList.clear();
            dealersHandList.clear();
            betCoin();
            playRound();
            showResult();
        }
        ;
    }

    private static void betCoin() {
        handCoin = handCoin - BET_COINS;
    }

    private static boolean isFinishGame() {
        if (handCoin < BET_COINS || handCoin >= 200) {
            return true;
        }
        return false;
    }

    private static boolean playRound() {
        firstDrow(dealersHandList, playersHandList);
        while (isNotFinish(dealersHandList)) {
            playOneTurn();
        }
        ;
        return true;
    }

    private static boolean isNotFinish(List<String[]> dealersHandList) {
        if (isNotOver(DROW_BORDER, dealersHandList) || isCanDrow(playersHandList)) {
            return true;
        }
        return false;
    }

    private static boolean isCanDrow(List<String[]> playersHandList) {
        if (isBurst(playersHandList)) {
            return false;
        }
        if (!isSelectDrow) {
            return false;
        }
        return true;
    }

    private static void firstDrow(List<String[]> dealersHandList, List<String[]> playersHandList) {
        for (int i = 0; i < FIRST_HAND_NUMBER; i++) {
            dealCard(Players.PLAYER, playersHandList);
            dealCard(Players.DEALER, dealersHandList);
        }
    }

    private static void playOneTurn() {
        dealerTurn(Players.DEALER, dealersHandList);
        playerTurn(Players.PLAYER, playersHandList);
    }

    private static void dealCard(Players player, List<String[]> handList) {
        if (isDeckEmpty()) {
            prepareDeck();
        }
        String drawCard[] = deckList.get(DECK_TOP_INDEX);
        deckList.remove(DECK_TOP_INDEX);
        showDealCardMessage(player, drawCard);
        handList.add(drawCard);
    }

    private static void dealerTurn(Players dealer, List<String[]> handList) {
        turnStartMessage(dealer);
        if (isOver(DROW_BORDER, handList)) {
            showNotDrowMessage(dealer);
            return;
        }
        dealCard(dealer, handList);
    }

    private static void playerTurn(Players player, List<String[]> handList) {
        turnStartMessage(player);
        showSumMessage(player, handList);
        if (isBurst(handList)) {
            return;
        }
        drowQuestion();
        if (!isSelectDrow) {
            showNotDrowMessage(player);
            return;
        }
        dealCard(player, handList);
        showSumMessage(player, handList);
    }

    private static boolean isOver(int border, List<String[]> handList) {
        int sum = getSum(handList);
        if (sum > border) {
            return true;
        }
        return false;
    }

    private static boolean isNotOver(int border, List<String[]> handList) {
        return !isOver(border, handList);
    }

    private static boolean isBurst(List<String[]> handList) {
        return isOver(BURST_BORDER, handList);
    }

    private static int getSum(List<String[]> handList) {
        int sum = 0;
        for (String[] card : handList) {

            sum = sum + getCardNumber(card);
        }
        return sum;
    }

    private static int getCardNumber(String[] card) {
        if (isIrregularcard(card)) {
            return IRREGULAR_CARDS.valueOf(card[CARD_NUMBER_INDEX]).getNumber();
        }
        return Integer.parseInt(card[CARD_NUMBER_INDEX]);
    }

    private static boolean isIrregularcard(String[] card) {
        for (IRREGULAR_CARDS irregularCard : IRREGULAR_CARDS.values()) {
            if (irregularCard.toString().equals(card[CARD_NUMBER_INDEX])) {
                return true;
            }
        }
        return false;
    }

    private static void drowQuestion() {
        showDROWQUESTION_MESSAGE();
        String inputCommand = receiveYorN();
        if (inputCommand.equals(YES_KEY)) {
            isSelectDrow = true;
        }
        if (inputCommand.equals(NO_KEY)) {
            isSelectDrow = false;
        }
    }

    private static String receiveYorN() {
        String inputStr;
        inputStr = STDIN.nextLine();
        if (!isCorrectCommand(inputStr)) {
            showNotCorrectCommandMessagee();
            return receiveYorN();
        }
        return inputStr;
    }

    private static boolean isCorrectCommand(String inputCommand) {
        if (inputCommand.equals(YES_KEY) || inputCommand.equals(NO_KEY)) {
            return true;
        }
        return false;
    }

    private static void showDealCardMessage(Players player, String[] card) {
        System.out.println(
                String.format(DEALCARD_MESSAGE, player.getName(), card[CARD_NUMBER_INDEX], card[CARD_MARK_INDEX]));
    }

    private static void showNotCorrectCommandMessagee() {
        System.out.println(NOTCORRECTCOMMAND_MESSAGE);
    }

    private static void showNotDrowMessage(Players player) {
        System.out.println(String.format(NOTDROW_MESSAGE, player.getName()));
    }

    private static void showDROWQUESTION_MESSAGE() {
        System.out.println(DROWQUESTION_MESSAGE);
    }

    private static void showSumMessage(Players player, List<String[]> handList) {
        System.out.println(String.format(SUM_MESSAGE, player.getName(), getSum(handList)));
    }

    private static void showDrawMessage() {
        System.out.println(DRAW_MESSAGE);
    }

    private static void showWinMessage() {
        System.out.println(String.format(WIN_MESSAGE, Players.PLAYER.getName()));
    }

    private static void showLoseMessage() {
        System.out.println(String.format(LOSE_MESSAGE, Players.PLAYER.getName()));
    }

    private static void showCoinMessage() {
        System.out.println(String.format(COIN_MESSAGE, handCoin));
    }

    private static void turnStartMessage(Players player) {
        System.out.println(String.format(TURNSTART_MESSAGE, player.getName()));
    }

    private static void showResult() {
        showSumMessage(Players.PLAYER, playersHandList);
        showSumMessage(Players.DEALER, dealersHandList);
        if (isDraw()) {
            getCoin();
            showDrawMessage();
            showCoinMessage();
            return;
        }
        if (isWin()) {
            getCoin();
            showWinMessage();
            showCoinMessage();
            return;
        }
        showLoseMessage();
        showCoinMessage();
    }

    private static void getCoin() {
        if (isDraw()) {
            handCoin = handCoin + BET_COINS;
            return;
        }
        if (isBlackJack(playersHandList)) {
            handCoin = handCoin + BLACKJACK_COINS;
            return;
        }
        handCoin = handCoin + WIN_COINS;
    }

    private static boolean isBlackJack(List<String[]> handList) {
        if (getSum(handList) != BURST_BORDER) {
            return false;
        }
        for (String[] card : handList) {
            if (!BLACKJACK_CARD_LIST.contains(getCardNumber(card))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isDraw() {
        if (getSum(playersHandList) == getSum(dealersHandList)) {
            if (isBlackJack(playersHandList)) {
                if (isBlackJack(dealersHandList)) {
                    return true;
                }
                return false;
            }
            if (isBlackJack(dealersHandList)) {
                if (isBlackJack(playersHandList)) {
                    return true;
                }
                return false;
            }
            return true;
        }
        if (isBurst(playersHandList) && isBurst(dealersHandList)) {
            return true;
        }
        return false;
    }

    private static boolean isWin() {
        if (isBurst(playersHandList)) {
            return false;
        }
        if (isBurst(dealersHandList)) {
            return true;
        }
        if (getSum(playersHandList) == getSum(dealersHandList)) {
            if (!isDraw()) {
                if (isBlackJack(playersHandList)) {
                    return true;
                }
                if (isBlackJack(dealersHandList)) {
                    return false;
                }
            }
        }
        if (getSum(playersHandList) > getSum(dealersHandList)) {
            return true;
        }
        return false;
    }

    private static void prepareDeck() {
        deckList.clear();
        for (Marks mark : Marks.values()) {
            for (int i = 0; i < MAX_CARD_NUMBER; i++) {
                String card[] = new String[2];
                card[0] = NUMBERLIST.get(i);
                card[1] = mark.getName();
                deckList.add(card);
            }
        }
        Collections.shuffle(deckList);
    }

    private static boolean isDeckEmpty() {
        if (deckList.size() == 0) {
            return true;
        }
        return false;
    }
}
