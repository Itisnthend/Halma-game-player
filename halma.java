import javax.swing.*;
import java.lang.reflect.Array;
import java.util.*;
import java.io.*;
import static java.lang.Math.*;



public class halma {


    public static void main(String[] args){
        int boardSize = 16;
        int single = -1;
        int player = 0;
        double totalTime = 0.0;
        int depth = 0;
        double[] depthTime  = new double[]{0.007,0.012,0.043,0.187,2.349,19.707};
        char[][] boardMatrix = new char[16][16];

        String filedir = System.getProperty("user.dir")+"/input.txt";
        File file = new File(filedir);
        BufferedReader reader = null;
        try{
            reader = new BufferedReader(new FileReader(file));
            int line = 1;
            //single move or Game
            String tempStr = reader.readLine();
            System.out.println("Single or Game:");
            System.out.println(tempStr);
            if(tempStr.equals("SINGLE")){
                single = 1;
            }else{
                single = 0;
            }
            line++;

            tempStr = reader.readLine();
            System.out.println("Our piece:");
            System.out.print(tempStr);
            if(tempStr.equals("WHITE")){
                player = 2;
            }
            else{
                player = 1;
            }
            //System.out.println(player);
            line++;

            tempStr = reader.readLine();
            System.out.println("totalTime:");
            System.out.println(tempStr);
            totalTime = Double.parseDouble(tempStr);
            line++;

            char[] arrayTemp = null;
            //List<char[]> arrayTemp = new ArrayList<>();
            for(int i = 0; i<16; i++){
                tempStr = reader.readLine();
                line++;
                arrayTemp = tempStr.toCharArray();
                for(int j = 0; j<16;j++){
                    boardMatrix[i][j] = arrayTemp[j];
                }
            }
            reader.close();
        }catch (IOException e) {
            e.printStackTrace();
        }

        filedir = System.getProperty("user.dir")+"/calibrate.txt";
        file = new File(filedir);
        reader = null;
        try{
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            for(int i = 0; i<6; i++){
                tempStr = reader.readLine();
                depthTime[i] = Double.parseDouble(tempStr);
            }
            reader.close();
        }catch (IOException e) {
            e.printStackTrace();
        }

        if(single==1) {
            if (totalTime > depthTime[5]*5){
                depth = 5;
            }
            else if(totalTime > depthTime[4]*2){
                depth = 4;
            }
            else if(totalTime > depthTime[3]){
                depth = 3;
            }
            else if(totalTime > depthTime[2]){
                depth = 2;
            }
            else if(totalTime > depthTime[1]){
                depth = 1;
            }
            else{
                depth = 0;
            }

        }

        if(single==0){
            if(totalTime>299.9){
                depth = 2;
            }
            else if(totalTime>85.0){
                depth = 4;
            }
            else if(totalTime>3.0){
                depth = 3;
            }
            else{
                depth = 2;
            }
        }
        System.out.print("depth =  ");
        System.out.print(depth);
        System.out.println();
        long startTime=System.currentTimeMillis();
        MinimaxABAgent HalmaAgent = new MinimaxABAgent(boardMatrix, totalTime, player, single, depth);
        Queue path = HalmaAgent.ABsearch(startTime);
        long endTime=System.currentTimeMillis();
        double duration = (double)(endTime-startTime)/1000.0;
        System.out.print("One step cost: ");
        System.out.print(duration);
        System.out.println(" s");
        //path.printAll();
        StringBuffer buffer;
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("output.txt"));

            for (int j = 1; j < path.size(); j++) {
                //System.out.println(j);
                if (Math.abs(path.get(j).loc[0]-path.get(j-1).loc[0])<2&&Math.abs(path.get(j).loc[1]-path.get(j-1).loc[1])<2) {
                    buffer = new StringBuffer("E ");
                }else{
                    buffer = new StringBuffer("J ");
                }
                buffer.append(path.get(j - 1).loc[0]);
                buffer.append(',');
                buffer.append(path.get(j - 1).loc[1]);
                buffer.append(' ');
                buffer.append(path.get(j).loc[0]);
                buffer.append(',');
                buffer.append(path.get(j).loc[1]);
                if(j<path.size()-1) {
                    buffer.append("\r\n");
                }
                //System.out.println(buffer.toString());
                out.write(buffer.toString());
            }
            out.close();
            //System.out.println("文件写入成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

class pathTile{
    public int parent = -1;
    public int id = 0;
    public int[] loc;                                  //loc {x,y}

    public pathTile(){
        this.parent = 0;
        this.id = 0;
        this.loc = new int[]{0, 0};
    }

    public pathTile(int parent, int id, int[] loc){
        this.parent = parent;
        this.id = id;
        this.loc = loc;
    }

    public pathTile(pathTile tile){
        this.parent = tile.parent;
        this.id = tile.id;
        this.loc = tile.loc;
    }

    public void setPathTile(int parent, int id, int[] loc){
        this.parent = parent;
        this.id = id;
        this.loc = loc;
    }

}
class Queue {
    LinkedList<pathTile> ll = new LinkedList<pathTile>();

    public void push(pathTile n) {
        ll.addLast(n);
    }

    public pathTile pop() {
        return ll.removeFirst();
    }

    public pathTile peek() {
        return ll.getFirst();
    }

    public boolean isEmpty() {
        return ll.isEmpty();
    }

    public int size(){
        return ll.size();
    }

    public pathTile get(int i){
        return ll.get(i);
    }

    public void printAll(){
        for(int i = ll.size()-1; i>=0; i--){
            System.out.print(ll.get(i).id);
            System.out.print(",");
            System.out.print(ll.get(i).parent);
            System.out.print(",");
            System.out.print(ll.get(i).loc[0]);
            System.out.print(",");
            System.out.print(ll.get(i).loc[1]);
            System.out.print(" ");
        }
        System.out.println(" done");
    }
}
//********************board***********************
class Board {
    public final int boardSize = 16;
    public final int NONE = 0;
    public final int black = 1;
    public final int white = 2;
    public Tile[][] boardMatrix;
    public int player;
    //public Map<int[], ArrayList<pathTile>> validMoves;

    public Board() {
        initializeBoard();
        //initializevalidMoves();
        this.player = 0;

    }

    public Board(Board board) {
        Tile[][] matrixToCopy = board.getBoardMatrix();
        this.boardMatrix = new Tile[matrixToCopy.length][matrixToCopy.length];
        //this.validMoves = new HashMap<int[], ArrayList<pathTile>>();
        for (int i = 0; i < matrixToCopy.length; i++) {
            for (int j = 0; j < matrixToCopy.length; j++) {
                this.boardMatrix[i][j].loc = matrixToCopy[i][j].loc;
                this.boardMatrix[i][j].piece = matrixToCopy[i][j].piece;
                this.boardMatrix[i][j].type = matrixToCopy[i][j].type;
            }
        }
        this.player = board.player;
    }

    public Board(char[][] boardMatrix, int player) {
        initializeBoard();
        for (int i = 0; i < this.boardSize; i++) {
            for (int j = 0; j < this.boardSize; j++) {
                if (boardMatrix[i][j] == 'B') {
                    this.boardMatrix[i][j].piece = 1;
                } else if (boardMatrix[i][j] == 'W') {
                    this.boardMatrix[i][j].piece = 2;
                } else {
                    this.boardMatrix[i][j].piece = 0;
                }
            }
        }
        this.player = player;
        //upDatevalidMoves();
    }

    public void initializeBoard() {
        this.boardMatrix = new Tile[this.boardSize][this.boardSize];
        for (int i = 0; i < this.boardSize; i++) {
            for (int j = 0; j < this.boardSize; j++) {
                if (i + j < 6) {
                    this.boardMatrix[i][j] = new Tile();
                    this.boardMatrix[i][j].type = 1;
                    this.boardMatrix[i][j].loc = new int[]{j, i};
                } else if (i + j > 24) {
                    this.boardMatrix[i][j] = new Tile();
                    this.boardMatrix[i][j].type = 2;
                    this.boardMatrix[i][j].loc = new int[]{j, i};
                } else {
                    this.boardMatrix[i][j] = new Tile();
                    this.boardMatrix[i][j].type = 0;
                    this.boardMatrix[i][j].loc = new int[]{j, i};
                }
            }
        }
        this.boardMatrix[0][5].type = 0;
        this.boardMatrix[5][0].type = 0;
        this.boardMatrix[15][10].type = 0;
        this.boardMatrix[10][15].type = 0;
    }
    //        public void initializevalidMoves(){
//            this.validMoves = new Hash();
//        }
    public void executeMove(int[] from, int[] to){
        int TempPiece = 0;
        TempPiece = this.boardMatrix[from[1]][from[0]].piece;
        this.boardMatrix[from[1]][from[0]].piece = 0;
        this.boardMatrix[to[1]][to[0]].piece = TempPiece;
    }
    public void check(){
        for (int i = 0; i < this.boardSize; i++) {
            for (int j = 0; j < this.boardSize; j++) {
                System.out.print(boardMatrix[i][j].piece);
            }
            System.out.println();
        }
    }
    public boolean validPosition(Tile tile){
        return (tile.loc[0] >= 0 && tile.loc[1] >= 0 && tile.loc[0] < boardSize && tile.loc[1] < boardSize);
    }

    public int FindWinner(){
        int countWhite = 0;
        int countBlack = 0;
        //int count = 0;
        for (int i = 10; i < this.boardSize; i++) {
            for (int j = this.boardSize-1; j>=25-i; j--) {
                if(i==10||j==10){
                    continue;
                }
//                if(this.boardMatrix[i][j].type==2) {
//                    count++;
//                }
                if(this.boardMatrix[i][j].piece != 0){
                    if(this.boardMatrix[i][j].piece == 1){
                        countBlack++;
                    }else{
                        countWhite++;
                    }
                }
            }
        }
        if(countBlack+countWhite==19&&countBlack!=0){
            return this.black;
        }
        countBlack = 0;
        countWhite = 0;
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j<6-i; j++) {
                if(i==5||j==5){
                    continue;
                }
                if(this.boardMatrix[i][j].piece != 0){
                    if(this.boardMatrix[i][j].piece == 1){
                        countBlack++;
                    }else{
                        countWhite++;
                    }
                }
            }
        }
        if(countBlack+countWhite==19&&countWhite!=0){
            return this.white;
        }
//        System.out.println("In the FindWinner:");
//        System.out.print("number of black: ");
//        System.out.println(count);
        return this.NONE;
    }

    public Tile[][] getBoardMatrix() {
        return this.boardMatrix;
    }

    public int getBoardSize() {
        return this.boardSize;
    }

}
//******************tile***********************
class Tile{
    public int type;
    public int piece;
    public int[] loc;                                  //loc {x,y}

    public Tile(){
        this.type = 0;
        this.piece = 0;
        this.loc = new int[]{0, 0};
    }

    public Tile(int type, int piece, int[] loc){
        this.type = type;
        this.piece = piece;
        this.loc = loc;
    }

    public Tile(Tile tile){
        this.type = tile.type;
        this.piece = tile.piece;
        this.loc = tile.loc;
    }

    public void setTile(int type, int piece, int[] loc){
        this.type = type;
        this.piece = piece;
        this.loc = loc;
    }

}

//******************MinimaxAgent****************
class MinimaxABAgent{
    public Board board;
    public double tLimit;
    public int single;                    //0: multiple    1:single
    public int depth;
    public int[][] myGoals;
    public int[][] opponentGoals;
    //public ArrayList<int[]> myPieses;
    private int validMovesize = 0;
    public int player;                //0: nonknown; 1:black; 2:white
    public int opponent;
    public int sumMoveSize;


    public MinimaxABAgent(char[][] boardMatrix, double tLimit, int player, int single, int depth){
        this.board = new Board(boardMatrix, player);
        this.tLimit = tLimit;
        this.player = player;
        this.opponent = 2/player;
        this.single = single;
        this.depth = depth;
        this.validMovesize = 0;
        this.sumMoveSize = 0;
        if(player==1){
            this.myGoals = new int[][]{{14,11},{15,11},{13,12},{14,12},{15,12},{12,13},{13,13},{14,13},{15,13},{11,14},{12,14},{13,14},{14,14}
                    ,{15,14},{11,15},{12,15},{13,15},{14,15},{15,15}};
            this.opponentGoals = new int[][]{{0,0},{1,0},{2,0},{3,0},{4,0},{0,1},{1,1},{2,1},{3,1},{4,1},{0,2},{1,2},{2,2}
                    ,{3,2},{0,3},{1,3},{2,3},{0,4},{1,4}};
        }
        if(player==2){
            this.myGoals = new int[][]{{0,0},{1,0},{2,0},{3,0},{4,0},{0,1},{1,1},{2,1},{3,1},{4,1},{0,2},{1,2},{2,2}
                    ,{3,2},{0,3},{1,3},{2,3},{0,4},{1,4}};
            this.opponentGoals = new int[][]{{14,11},{15,11},{13,12},{14,12},{15,12},{12,13},{13,13},{14,13},{15,13},{11,14},{12,14},{13,14},{14,14}
                    ,{15,14},{11,15},{12,15},{13,15},{14,15},{15,15}};
        }
    }

    public Queue ABsearch(long startTime){
        //long startTime=System.currentTimeMillis();
        //long maxTime = startTime + this.tLimit;
        this.board.chack();
        double a = Double.NEGATIVE_INFINITY;
        double b = Double.POSITIVE_INFINITY;
        int CurDepth = this.depth;
        int count2 = 0;

        pathTile bestMove = null;
        double bestVal = Double.NEGATIVE_INFINITY;
        pathTile temp = null;
        int tempKey = 0;
        int countMovesArray = 0;
        ArrayList<ArrayList<pathTile>> allMoves = GetValidMoves(this.player);

        for(ArrayList<pathTile> moves: allMoves){
            //System.out.println(moves.size()-1);
            for(int i = moves.size()-1; i>0;i--){
                pathTile move = moves.get(i);
                Long limit = System.currentTimeMillis() - startTime;
                if(limit.doubleValue()/1000.0>this.tLimit){
                    Queue path = new Queue();
                    if(temp==null){
                        GetPath(moves, path, move.id);
                        return path;
                    }
                    GetPath(allMoves.get(tempKey), path, temp.id);
                    return path;
                }

//                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//                System.out.print("from: ");
//                System.out.print(moves.get(0).loc[0]);
//                System.out.print(",");
//                System.out.println(moves.get(0).loc[1]);
//                System.out.print("to: ");
//                System.out.print(move.loc[0]);
//                System.out.print(",");
//                System.out.println(move.loc[1]);
//
//                System.out.print("size: ");
//                System.out.println(moves.size());
                board.executeMove(moves.get(0).loc, move.loc);
                //board.check();
                double score = MinValue(CurDepth, a, b, startTime);
                //System.out.println("*********************************************************");
                board.executeMove(move.loc, moves.get(0).loc);
                //board.check();
                //System.out.print("score in Agent: ");
                //System.out.println(score);
                if(score>bestVal){
                    bestVal = score;
                    temp = move;
                    tempKey = countMovesArray;
                }
                a = Max(a, bestVal);
            }
            countMovesArray++;
        }
//        System.out.print("key: ");
//        System.out.print(tempKey[0]);
//        System.out.print(",");
//        System.out.print(tempKey[1]);
        Queue path = new Queue();
        GetPath(allMoves.get(tempKey), path, temp.id);
        return path;
    }

    public void GetPath( ArrayList<pathTile> moves, Queue path, int id)
    {
        if (moves.get(id).parent != -1)
            GetPath(moves, path, moves.get(id).parent);
        path.push(moves.get(id));
    }

    public double MaxValue(int CurDepth, double a, double b, long startTime){
        //System.out.println("Current agent is Max: ");
        //System.out.print("Current Level agent at: ");
        //System.out.println(CurDepth);
        Long limit = System.currentTimeMillis() - startTime;
        if(CurDepth == 0|| board.FindWinner()>0||limit.doubleValue()/1000.0>this.tLimit){
            //System.out.print("score is:");
            //System.out.println(getScore());
            return getScore(CurDepth);
        }
        //int count = 0;
        pathTile bestMove = null;
        double bestVal = Double.NEGATIVE_INFINITY;
        double temp;

        ArrayList<ArrayList<pathTile>> allMoves = GetValidMoves(this.player);

        for(int i = 0; i<allMoves.size(); i++){
            //count = 0;
            ArrayList<pathTile> moves = allMoves.get(i);
            for(int j = moves.size()-1; j>0;j--){
                pathTile move = moves.get(j);
                limit = System.currentTimeMillis()-startTime;
                if(limit.doubleValue()/1000.0>this.tLimit){
                    return getScore(CurDepth);
                }
                board.executeMove(moves.get(0).loc, move.loc);
                bestVal = Max(MinValue(CurDepth-1, a,b,startTime), bestVal);
                board.executeMove(move.loc,moves.get(0).loc);
                if(bestVal>=b){
                    return bestVal;
                }
                a = Max(bestVal, a);
            }
        }
        return bestVal;
    }

    public double MinValue(int CurDepth, double a, double b, long startTime){
        //System.out.println("Current agent is Min: ");
        //System.out.print("Current Level agent at: ");
        //System.out.println(CurDepth);
        Long limit = System.currentTimeMillis() - startTime;
        if(CurDepth == 0|| board.FindWinner()>0||limit.doubleValue()/1000.0>this.tLimit){
            //System.out.print("score is:");
            //System.out.println(getScore());
            return getScore(CurDepth);
        }
        pathTile bestMove = null;
        double worseVal = Double.POSITIVE_INFINITY;
        double temp;
        ArrayList<ArrayList<pathTile>> allMoves = GetValidMoves(this.opponent);

        for(int i = 0; i<allMoves.size(); i++){

            ArrayList<pathTile> moves = allMoves.get(i);
            for(int j = moves.size()-1; j>0;j--){
                pathTile move = moves.get(j);
                limit = System.currentTimeMillis()-startTime;
                if(limit.doubleValue()/1000.0>this.tLimit){
                    return getScore(CurDepth);
                }

                board.executeMove(moves.get(0).loc, move.loc);
                worseVal = Min(MaxValue(CurDepth-1, a,b,startTime), worseVal);
                board.executeMove(move.loc,moves.get(0).loc);
                if(worseVal<=a){
                    return worseVal;
                }
                b = Min(worseVal, b);
            }
        }
        return worseVal;

    }

    public double Min(double a, double b){
        if(a>b){
            return b;
        }else{
            return a;
        }
    }

    public double Max(double a, double b){
        if(a<b){
            return b;
        }else{
            return a;
        }
    }

    public ArrayList<ArrayList<pathTile>> GetValidMoves(int playerTurn) {
        Tile CurrentTile;
        //int count = 0;                                                                        //可删！！！！！！！！！！！！！
        ArrayList<pathTile> moves = null;
        ArrayList<ArrayList<pathTile>> validMoves = new ArrayList<ArrayList<pathTile>>();
        if(playerTurn==2){
            for (int i = board.boardSize-1; i >= 0; i--) {
                for (int j = board.boardSize-1; j >= 0; j--) {
                    CurrentTile = board.boardMatrix[i][j];
                    if (CurrentTile.piece == playerTurn) {
                        //count++;
                        //System.out.print("count:");
                        //System.out.print(count);
                        moves = validMove(-1, playerTurn, CurrentTile, moves, true);
                        this.sumMoveSize = this.sumMoveSize + this.validMovesize;
                        this.validMovesize = 0;
                        if (moves == null) {
                            continue;
                        }
                        validMoves.add(moves);
                        moves = null;

                    }
                }
            }
        }
        if(playerTurn==1){
            for (int i = 0; i < board.boardSize; i++) {
                for (int j = 0; j < board.boardSize; j++) {
                    CurrentTile = board.boardMatrix[i][j];
                    if (CurrentTile.piece == playerTurn) {
                        //count++;
//                        System.out.print(i);
//                        System.out.print(",");
//                        System.out.println(j);
                        moves = validMove(-1, playerTurn, CurrentTile, moves, true);
                        this.sumMoveSize = this.sumMoveSize + this.validMovesize;
                        this.validMovesize = 0;
                        if (moves == null) {
                            continue;
                        }
                        validMoves.add(moves);
                        moves = null;
                    }
                }
            }
        }
        return validMoves;
    }

    public ArrayList<pathTile> validMove(int parent, int playerTurn, Tile tile, ArrayList<pathTile> moves,  Boolean adj){

        int row = 0;
        int col = 0;
        int new_row = 0;
        int new_col = 0;
        int flag = 0;

        if(moves==null){
            moves = new ArrayList<pathTile>();
            pathTile temp = new pathTile(parent, this.validMovesize, new int[]{tile.loc[0], tile.loc[1]});
            moves.add(temp);
            this.validMovesize = this.validMovesize+1;
            parent = parent+1;
        }

        ArrayList<Integer> valid_tiles = new ArrayList<>(3);         //0: none  1: black  2:white
        valid_tiles.add(0);
        valid_tiles.add(1);
        valid_tiles.add(2);
        //System.out.print("size: ");
        //System.out.println(valid_tiles.size());

        row = tile.loc[1];
        col = tile.loc[0];

        if(tile.type != playerTurn)
            valid_tiles.remove(playerTurn);                                   // Moving back into your own goal
        if(tile.type != 0 && tile.type != playerTurn)
            valid_tiles.remove(0);                                  // Moving out of the enemy's goal

        for(int i = -1; i<2; i++){
            for(int j = -1; j<2; j++){
                new_row = row + i;
                new_col = col + j;

                //Skip checking degenerate values
                if ((new_row == row && new_col == col) || new_row < 0 || new_col < 0 ||
                        new_row >= board.boardSize || new_col >= board.boardSize){
                    continue;
                }

                //Handle moves out of/in to goals
                Tile newTile = board.boardMatrix[new_row][new_col];
                if(!isValidTiles(valid_tiles, newTile.type)){
                    continue;
                }

                if(newTile.piece == 0){
                    if(adj){
                        pathTile temp = new pathTile(parent, this.validMovesize, new int[]{new_col,new_row});
                        moves.add(temp);
                        this.validMovesize = this.validMovesize+1;
                    }
                    continue;
                }

                new_row = new_row + i;
                new_col = new_col + j;

                //Skip checking degenerate values
                if (new_row < 0 || new_col < 0 ||
                        new_row >= board.boardSize || new_col >= board.boardSize) {
                    continue;
                }
                //Handle returning moves and moves out of/in to goals
                flag = 0;
                newTile = board.boardMatrix[new_row][new_col];
                for(pathTile a : moves){
                    if(a.loc[0]==newTile.loc[0]&&a.loc[1]==newTile.loc[1]){
                        flag = 1;
                    }
                }
                if(flag==1||!isValidTiles(valid_tiles, newTile.type)){
                    continue;
                }

                if (newTile.piece == 0){
                    pathTile temp = new pathTile(parent, this.validMovesize, new int[]{new_col, new_row});

                    moves.add(temp);
                    this.validMovesize = this.validMovesize+1;
                    validMove(temp.id, playerTurn, newTile, moves ,false);
                }

            }
        }
        if(moves.size()<=1){
            moves = null;
        }
        return moves;
    }

    private boolean isValidTiles(ArrayList<Integer> valid_tiles, int type){
        int flag = 0;
        for(int a : valid_tiles){
            if(type==a){
                flag = 1;
            }
        }
        if(flag == 1){
            return true;
        }
        return false;
    }

    private double pointDistance(int[] p0, int[] p1){
        return Math.sqrt(Math.pow(p1[0] - p0[0],2) + Math.pow((p1[1] - p0[1]),2));
    }

    public double getScore(int CurDepth){
        double value = 0.0;
        double distance = 0.0;
        double largest = 0.0;
        //double my = 0;                                     //可删！！！！！！！！！！！！！！！！
        //double op = 0;                                     //可删！！！！！！！！！！！！！！！！
        int win = board.FindWinner();
        //System.out.println("In getScore Function: ");
        //System.out.println(win);
        if(win != 0){
            if(win == this.player){
                return 2000.0+((double)CurDepth);
            }
            if(win == this.opponent){
                return -2000.0-((double)CurDepth);
            }
        }
        for(int i = 0; i<board.boardSize; i++){
            for(int j = 0; j<board.boardSize; j++){
                if(board.boardMatrix[i][j].piece==this.player){
                    for(int n = 0; n<19; n++) {
                        if(board.boardMatrix[this.myGoals[n][1]][this.myGoals[n][0]].piece==this.player){
                            continue;
                        }
                        distance = pointDistance(board.boardMatrix[i][j].loc, this.myGoals[n]);
                        if(largest<distance){
                            largest = distance;
                        }

                    }
                    value -= largest;
                    //my -= largest;
                }
                largest = 0;
                if(board.boardMatrix[i][j].piece==this.opponent){
                    for(int n = 0; n<19; n++) {
                        if(board.boardMatrix[this.opponentGoals[n][1]][this.opponentGoals[n][0]].piece==this.opponent){
                            //System.out.println("good");
                            continue;
                        }
                        distance = pointDistance(board.boardMatrix[i][j].loc, this.opponentGoals[n]);
                        //System.out.println(distance);
                        if(largest<distance){
                            largest = distance;
                        }
                        //System.out.print("value: ");
                        //System.out.println(value);
                    }
                    value += largest;
                    //op += largest;
                }
                largest = 0;
            }
        }
//        System.out.print("my score: ");
//        System.out.print(my);
//        System.out.print("op score: ");
//        System.out.println(op);
        return value;
    }
}