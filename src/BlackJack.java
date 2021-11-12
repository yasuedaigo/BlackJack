import java.util.Scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlackJack {
    private static final Random RANDOM = new Random();
    private static final Scanner STDIN = new Scanner(System.in);
    private static enum Players {
        Player("あなた"), Dealer("ディーラー");
        private String name;
        private Players(String name) {
            this.name = name;
        }
        private String getName() {
            return name;
        }
    }
    private static final int FIRSTHANDNUMBER = 2;
    private static final int OFFSET = 1;
    private static final int MAXCARDNUMBER = 12;
    private static final int DROWBORDER = 17;
    private static final int BURSTBORDER = 21;
    private static final String YESKEY = "Y";
    private static final String NOKEY = "N";
    private static List<Integer> playersHandList = new ArrayList<>(){};
    private static List<Integer> dealersHandList = new ArrayList<>(){};
    private static final List<String> CARDLIST = new ArrayList<>() {
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

    private static final String TURNSTART_MESSAGE = "%sのターンです";
    private static final String DEALCARD_MESSAGE = "%sに「%s」が配られました";
    private static final String NOTDROW_MESSAGE = "%sはカードを引きませんでした";
    private static final String DROWQUESTION_MESSAGE = "もう１枚カードを引きますか？(Y/N)：";
    private static final String SUM_MESSAGE = "%sの合計は%dです";
    private static final String WIN_MESSAGE = "%sの勝ちです";
    private static final String LOSE_MESSAGE = "%sの負けです";
    private static final String DRAW_MESSAGE = "引き分けです";

    public static void main(String[] args) {
        firstDrow(dealersHandList,playersHandList);
        Boolean isSelectDrow = true;
        while(isNotFinish(dealersHandList,isSelectDrow)){
            playRound(isSelectDrow);
        };
        showResult();
    }

    private static Boolean isNotFinish(List<Integer> dealersHandList,Boolean isSelectDrow){
        if(isNotOver(DROWBORDER,dealersHandList) || isCanDrow(isSelectDrow,playersHandList)){
            return true;
        }
        return false;
    }

    private static boolean isCanDrow(Boolean isSelectDraw,List<Integer> playersHandList){
        if(isBurst(playersHandList)){
            return false;
        }
        if(!isSelectDraw){
            return false; 
        }
        return true;
    }

    private static void firstDrow(List<Integer> dealersHandList,List<Integer> playersHandList){
        for(int i=0; i < FIRSTHANDNUMBER; i++){
            dealCard(Players.Player,playersHandList);
            dealCard(Players.Dealer,dealersHandList);
        }
    }

    private static void playRound(Boolean isSelectDrow) {
        dealerTurn(Players.Dealer,dealersHandList);
        playerTurn(Players.Player,playersHandList,isSelectDrow);
    }

    private static void dealCard(Players player,List<Integer> handList){
        int cardNumber = RANDOM.nextInt(MAXCARDNUMBER);
        String card = CARDLIST.get(cardNumber);
        showDealCardMessage(player,card);
        handList.add(cardNumber + OFFSET);
    }

    private static void dealerTurn(Players dealer,List<Integer> handList){
        turnStartMessage(dealer);
        if(isOver(DROWBORDER,handList)){
            showNotDrowMessage(dealer);
            return;
        }
        dealCard(dealer,handList);
    }

    private static void playerTurn(Players player,List<Integer> handList,Boolean isSelectDrow){
        turnStartMessage(player);
        showSumMessage(player,handList);
        if(isBurst(handList)){
            return;
        }
        drowQuestuon(isSelectDrow);
        if(!isSelectDrow){
            showNotDrowMessage(player);
            return;
        }
        dealCard(player,handList);
        showSumMessage(player,handList);
    }

    private static boolean isOver(int border,List<Integer> handList){
        int sum = getSum(handList);
        if(sum > border){
            return true;
        }
        return false;
    }

    private static boolean isNotOver(int border,List<Integer> handList){
        return !isOver(border,handList);
    }

    private static boolean isBurst(List<Integer> handList){
        return isOver(BURSTBORDER,handList);
    }

    private static int getSum(List<Integer> handList){
        int sum = 0;
        for(int number : handList){
            sum = sum + number;
        }
        return sum;
    }

    private static Boolean drowQuestuon(Boolean isSelectDrow){
        showDROWQUESTION_MESSAGE();
        String inputCommand = receiveYorN();
        if(inputCommand.equals(YESKEY)){
            isSelectDrow = true;
        }
        if(inputCommand.equals(NOKEY)){
            isSelectDrow = false;
        }
        return isSelectDrow;
    }

    private static String receiveYorN() {
        String inputStr;
        inputStr = STDIN.nextLine();
        if (!isCorrectCommand(inputStr)) {
            return receiveYorN();
        }
        return inputStr;
    }

    private static boolean isCorrectCommand(String inputCommand){
        if(inputCommand.equals(YESKEY) || inputCommand.equals(NOKEY)){
            return true;
        }
        return false;
    }

    private static void showDealCardMessage(Players player,String card) {
        System.out.println(String.format(DEALCARD_MESSAGE,player.getName(),card));
    }

    private static void showNotDrowMessage(Players player) {
        System.out.println(String.format(NOTDROW_MESSAGE,player.getName()));
    }

    private static void showDROWQUESTION_MESSAGE() {
        System.out.println(DROWQUESTION_MESSAGE);
    }

    private static void showSumMessage(Players player,List<Integer> handList) {
        System.out.println(String.format(SUM_MESSAGE,player.getName(),getSum(handList)));
    }

    private static void showDrawMessage() {
        System.out.println(DRAW_MESSAGE);
    }

    private static void showWinMessage() {
        System.out.println(String.format(WIN_MESSAGE,Players.Player.getName()));
    }

    private static void showLoseMessage() {
        System.out.println(String.format(LOSE_MESSAGE,Players.Player.getName()));
    }

    private static void turnStartMessage(Players player) {
        System.out.println(String.format(TURNSTART_MESSAGE,player.getName()));
    }

    private static void showResult(){
        showSumMessage(Players.Player,playersHandList);
        showSumMessage(Players.Dealer,dealersHandList);
        if(isDraw()){
            showDrawMessage();
        }
        if(isWin()){
            showWinMessage();
        }
        showLoseMessage();
    }

    private static boolean isDraw(){
        if(getSum(playersHandList) == getSum(dealersHandList)){
            return true;
        }
        if(isBurst(playersHandList) && isBurst(dealersHandList)){
            return true;
        }
        return false;
    }
    
    private static boolean isWin(){
        if(isBurst(playersHandList)){
            return false;
        }
        if(getSum(playersHandList) > getSum(dealersHandList)){
            return true;
        }
        return false;
    }
}
