package arndt.com.peggame;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class PegGame {

    public static void main(String[] args) throws Exception {
//        getAllSolutions();

        /*
        PegGame p = new PegGame(PEGS &~ EACH_POSITION[4]);
        StringBuilder output = new StringBuilder();
        output.append(getPrint(p)+"\n");
        solve(p).forEach(a->{
            p.jumpPeg(a.start,a.end);
            output.append(getPrint(p)+"\n");
        });
        */
    }
/*
    public static void getAllSolutions() throws IOException {
        String dir = "peg-game-solutions";
        new File(dir).mkdirs();
        for (int i = 0; i < EACH_POSITION.length; i++) {
            String fileName = dir+"/solution_"+i+".txt";
            if(new File(fileName).exists()) continue;
            PegGame p = new PegGame(PEGS &~ EACH_POSITION[i]);
            StringBuilder output = new StringBuilder();
            output.append(getPrint(p)+"\n");
            solve(p).forEach(a->{
                p.jumpPeg(a.start,a.end);
                output.append(p.pegs+"\n"+getPrint(p)+"\n");
            });
            writeToFileOverWrite(fileName, output.toString());
        }
    }
*/

    public static void writeToFileOverWrite(String filePath, String contents) throws IOException {
        FileOutputStream out = new FileOutputStream(filePath);
        out.write(contents.getBytes());
        out.close();
    }

    public static void randomPlay(){
        PegGame p = new PegGame();
        Random r = new Random();
        List<Pair> moves = p.getAllMoves();
        int i = 0;
        while (moves.size() > 0) {
            Pair pair = moves.get(r.nextInt(moves.size()));
            p.jumpPeg(pair.start, pair.end);
            System.out.println(i++);
            System.out.println(p.pegs);
            print(p);
            moves = p.getAllMoves();
        }
    }

    //    private static Random r = new Random();
    public static List<Pair> solve(PegGame pg){
//        System.out.println("thinking");
//        for (int i = 1; i < 9; i++) {
//            List<Pair> r = _solve(pg, new ArrayList<Pair>(), i);
//            if(r !=null) {
//                System.out.println("done");
//                return r;
//            }
//        }
//        System.out.println("done");
        return _solve(pg,new ArrayList<Pair>());
    }private static List<Pair> _solve(PegGame pg, List<Pair> allMoves){
        // TODO: 10/10/18 add memoizing
//        System.out.println(pg.pegs);
//        print(pg);
        List<Pair> moves = pg.getAllMoves();
//        Collections.shuffle(moves);
//        long best = 0b000010000000101000001010100010101010101010101L;
        long best = -1;
        List<Pair> bestPair = allMoves;
        for (int i = 0; i < moves.size(); i++) {
            PegGame p = pg.clone();
            p.jumpPeg(moves.get(i).start,moves.get(i).end);
            List<Pair> pairs = new ArrayList<>(allMoves);
            pairs.add(moves.get(i));
            List<Pair> m = _solve(p, pairs);
            if(m.size() > best){
                best = m.size();
                bestPair = m;
            }
        }
        return bestPair;
    }

    public static long     PEGS            = 0b000010000000101000001010100010101010101010101L,
            EMPTIES         = 0b000010000000000000000000000000000000000000000L,
            OUT_OF_BOUNDS   = 0b111101111111010111110101011101010101010101010L,
            CLEAR_TOP       = 0b111111111111111111000000000000000000000000000L&~OUT_OF_BOUNDS,
            CLEAR_BOTTOM    = 0b000000000000000000000000000111111111111111111L&~OUT_OF_BOUNDS,
            CLEAR_LEFT      = 0b111000000111000000111000000111000000111000000L&~OUT_OF_BOUNDS,
            CLEAR_LEFT_1    = 0b100000000100000000100000000100000000100000000L&~OUT_OF_BOUNDS,
            CLEAR_RIGHT     = 0b000000111000000111000000111000000111000000111L&~OUT_OF_BOUNDS,
            CLEAR_RIGHT_1   = 0b000000001000000001000000001000000001000000001L&~OUT_OF_BOUNDS;

    public static long[] EACH_POSITION = new long[]{
            0b000010000000000000000000000000000000000000000L,
            0b000000000000100000000000000000000000000000000L,
            0b000000000000001000000000000000000000000000000L,
            0b000000000000000000001000000000000000000000000L,
            0b000000000000000000000010000000000000000000000L,
            0b000000000000000000000000100000000000000000000L,
            0b000000000000000000000000000010000000000000000L,
            0b000000000000000000000000000000100000000000000L,
            0b000000000000000000000000000000001000000000000L,
            0b000000000000000000000000000000000010000000000L,
            0b000000000000000000000000000000000000100000000L,
            0b000000000000000000000000000000000000001000000L,
            0b000000000000000000000000000000000000000010000L,
            0b000000000000000000000000000000000000000000100L,
            0b000000000000000000000000000000000000000000001L
    };

    private long pegs,empties,outOfBounds;

    public PegGame(){
        pegs = PEGS &~ EMPTIES;
        empties = EMPTIES;
        outOfBounds = OUT_OF_BOUNDS;
    }
    public PegGame(long pegs) {
        this.pegs = pegs;
        empties = EMPTIES;
        outOfBounds = OUT_OF_BOUNDS;
    }

    @Override
    public PegGame clone(){
        return new PegGame(pegs);
    }

    public long getPegs() {
        return pegs;
    }

    public void setPegs(long pegs) {
        this.pegs = pegs;
    }

    public boolean jumpPeg(long s, long e){
        if(!check(s,e)) return false;
        pegs = ((s &~ CLEAR_BOTTOM) >> 16) == e ? ((pegs &~ s) | e) &~ (s >> 8) :
                ((s &~ CLEAR_BOTTOM) >> 20) == e ? ((pegs &~ s) | e) &~ (s >> 10) :
                        ((s &~CLEAR_TOP ) << 16) == e ? ((pegs &~ s) | e) &~ (s << 8) :
                                ((s &~ CLEAR_TOP) << 20) == e ? ((pegs &~ s) | e) &~ (s << 10) :
                                        ((s &~ CLEAR_RIGHT) >> 4) == e ? ((pegs &~ s) | e) &~ (s >> 2) :
                                                ((s &~ CLEAR_LEFT) << 4) == e ? ((pegs &~ s) | e) &~ (s << 2) : 0;

        return true;
    }

    private long _moveLeftJump(long v, int a, long clear) {
        long e = (((v &~ clear) << a) &~ OUT_OF_BOUNDS &~ v);
        return ((e >> a/2) & v) != 0 ? e : 0;
    }private long _moveRightJump(long v, int a, long clear) {
        long e = (((v &~ clear) >> a) &~ OUT_OF_BOUNDS &~ v);
        return ((e << a/2) & v) != 0 ? e : 0;
    }private long moveJump(long v){
        return _moveLeftJump(v,16, CLEAR_TOP) | _moveRightJump(v, 16, CLEAR_BOTTOM) |
                _moveLeftJump(v,20, CLEAR_TOP) | _moveRightJump(v, 20, CLEAR_BOTTOM) |
                _moveLeftJump(v,4, CLEAR_LEFT) | _moveRightJump(v, 4, CLEAR_RIGHT);
    }
    private long _moveLeftBack(long v, int a, long clear) {
        long e = (((v &~ clear) << a) &~ OUT_OF_BOUNDS &~ v & pegs);
        return ((e >> a/2) & pegs)<<a/2;
    }private long _moveRightBack(long v, int a, long clear) {
        long e = (((v &~ clear) >> a) &~ OUT_OF_BOUNDS &~ v & pegs);
        return ((e << a/2) & pegs)>>a/2;
    }private long moveBack(long v){
        return _moveLeftBack(v,16, CLEAR_TOP) | _moveRightBack(v, 16, CLEAR_BOTTOM) |
                _moveLeftBack(v,20, CLEAR_TOP) | _moveRightBack(v, 20, CLEAR_BOTTOM) |
                _moveLeftBack(v,4, CLEAR_LEFT) | _moveRightBack(v, 4, CLEAR_RIGHT);
    }
    public long getAllMovesEnd(){
        return moveJump(pegs);
    }
    public long getAllMovesStart(){
        return moveBack(getAllMovesEnd());
    }

    public static long getAllMovesFrom(long v){
        return ((v &~ CLEAR_TOP &~ CLEAR_RIGHT_1) << 16 | (v &~ CLEAR_BOTTOM) >> 16 |
                (v &~ CLEAR_TOP) << 20 | (v &~ CLEAR_BOTTOM) >> 20 |
                (v &~ CLEAR_LEFT) << 4 | (v &~ CLEAR_RIGHT) >> 4) &~ OUT_OF_BOUNDS;
    }

    public List<Pair> getAllMoves(){
        long a = getAllMovesStart();
        List<Pair> pairs = new ArrayList<>();
        for (int i = 0; i < EACH_POSITION.length; i++) //TODO can be improved by randomly searching until len of bits are found
            if((EACH_POSITION[i]|a)==a) {
                long ss = getAllMovesFrom(EACH_POSITION[i]);
                for (int j = 0; j < EACH_POSITION.length; j++) //TODO can be improved by randomly searching until len of bits are found
                    if((EACH_POSITION[j]|ss)==ss)
                        if(check(EACH_POSITION[i],EACH_POSITION[j]))
                            pairs.add(new Pair(EACH_POSITION[i],EACH_POSITION[j]));
            }
        return pairs;
    }

    private boolean _checkLeft1(long s, long e, long clear, int v) {
        //if start goes to end and there is a peg between start and end
        return (((s & ~clear) << v) & ~OUT_OF_BOUNDS & ~s) == e;
    }private boolean _checkLeft2(long s, long e, long clear, int v) {
        //if start goes to end and there is a peg between start and end
        return (((s << (v / 2)) & ~OUT_OF_BOUNDS) | pegs) == pegs;
    }private boolean _checkRight1(long s, long e, long clear, int v) {
        //if start goes to end and there is a peg between start and end
        return (((s & ~clear) >> v) & ~OUT_OF_BOUNDS & ~s) == e;
    }private boolean _checkRight2(long s, long e, long clear, int v) {
        //if start goes to end and there is a peg between start and end
        return (((s >> (v / 2)) & ~OUT_OF_BOUNDS) | pegs) == pegs;
    }

    public boolean check(long s, long e){
        //check start and end have just one value each
        if(Long.bitCount(s) != 1 && Long.bitCount(e) != 1) return false;
        //check for start having a peg and end not having a peg
        if((s|pegs)==pegs && (e|~pegs)!=~pegs) return false;
        //check if there is a peg between start and end location
        if(_checkLeft1(s,e,CLEAR_TOP,16)) return _checkLeft2(s,e,CLEAR_TOP,16);
        if(_checkLeft1(s,e,CLEAR_TOP,20)) return _checkLeft2(s,e,CLEAR_TOP,20);
        if(_checkLeft1(s,e,CLEAR_LEFT,4)) return _checkLeft2(s,e,CLEAR_LEFT,4);

        if(_checkRight1(s,e,CLEAR_BOTTOM,16)) return _checkRight2(s,e,CLEAR_BOTTOM,16);
        if(_checkRight1(s,e,CLEAR_BOTTOM,20)) return _checkRight2(s,e,CLEAR_BOTTOM,20);
        if(_checkRight1(s,e,CLEAR_RIGHT,4)) return _checkRight2(s,e,CLEAR_RIGHT,4);

        // TODO: 10/10/18 What to do here
        return false;
    }

    public static void print(PegGame pegGame){
        System.out.println(getPrint(pegGame));
    }
    public static String getPrint(PegGame pegGame){
        String  pegs        = fill(Long.toBinaryString(pegGame.pegs)),
                empties     = fill(Long.toBinaryString(pegGame.empties)),
                outOfBounds = fill(Long.toBinaryString(pegGame.outOfBounds));
        String s = "";
        for (int i = 0; i < pegs.toCharArray().length; i++) {
            if(pegs.toCharArray()[i] == '1') s+= "1";
            else if(empties.toCharArray()[i]=='1') s+="0";
            else if(outOfBounds.toCharArray()[i]=='1')s+="-";
            else if(outOfBounds.toCharArray()[i]=='0' && pegs.toCharArray()[i]=='0')s+="0";
            if((i+1)%9==0)s+="\n";
        }
        return s;
    }

    public static class Pair{
        long start,end;

        public Pair(long start, long end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair pair = (Pair) o;
            return start == pair.start &&
                    end == pair.end;
        }

        @Override
        public int hashCode() {
            return Objects.hash(start, end);
        }

        @Override
        public String toString() {
            return "Pair{" +
                    "start=" + start +
                    ", end=" + end +
                    '}';
        }
    }

    /**
     * HELPERS
     *
     */


    public static String fill(String v){
        if(v.length() == 45)
            return v;
        StringBuilder vBuilder = new StringBuilder(v);
        while (vBuilder.length()<45)
            vBuilder.insert(0, 0);
        v = vBuilder.toString();
        return v;
    }

    public static void printLong(long v){
        String s = fill(Long.toBinaryString(v)), ss = "";
        for (int i = 0; i < s.toCharArray().length; i++) {
            ss += s.toCharArray()[i];
            if((i+1)%9==0)ss += "\n";
        }
        System.out.println(ss);
    }

    private static String getStringRepresentationPegs(){
        String s = "";
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5-i-1; j++)
                s+="0";
            for (int j = 0; j < i*2+1; j++)
                if(j%2==0) s+= "1";
                else s+="0";
            for (int j = 0; j < 5-i-1; j++)
                s+="0";
        }
        return s;
    }
    private static String getStringRepresentationOutOfBounds(){
        String s = "";
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5-i-1; j++)
                s+="1";
            for (int j = 0; j < i*2+1; j++)
                if(j%2==1)
                    s+="1";
                else s+="0";
            for (int j = 0; j < 5-i-1; j++)
                s+="1";
        }
        return s;
    }
    private static String getStringRepresentationEmpties(){
        int x = 0,y = 4;
        String s = "";
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 9; j++)
                if (i == x && j == y)
                    s += "1";
                else s += "0";
        return s;
    }
    private static String getStringRepresentationClearTop(){
        String s = "";
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 9; j++)
                if (i < 2)
                    s += "1";
                else s += "0";
        return s;
    }
    private static String getStringRepresentationClearSide(){
        String s = "";
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 9; j++)
                if (j < 3)
                    s += "1";
                else s += "0";
        return s;
    }

    private static void getStringRepresentationEachPosition(){
        for (int l = 0; l < 15; l++) {
            int k = 0;
            String s = "";
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5 - i - 1; j++)
                    s += "0";
                for (int j = 0; j < i * 2 + 1; j++)
                    if (j % 2 == 0) {
                        if (k++ == l)
                            s += "1";
                        else s += "0";
                    } else s += "0";
                for (int j = 0; j < 5 - i - 1; j++)
                    s += "0";
            }
            System.out.println("0b"+s+"L,");
        }
    }
}