package arndt.com.peggame;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private boolean isEdit = false;
    PegGame pegGame;
    private Drawable baseColor;
    HashMap<Integer,PegButton> buttons = new HashMap<>();
    HashMap<Long,PegButton> buttonsByLong = new HashMap<>();
    private long start = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createBoard();
        baseColor = findViewById(R.id.reset).getBackground();
        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createBoard();
            }
        });
        findViewById(R.id.help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<PegGame.Pair> ss = PegGame.solve(pegGame);
                if(ss != null && ss.size() > 0) {
                    PegGame.Pair p = ss.get(0);
                    jump(p);
                }
            }
        });
        findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEdit = !isEdit;
                if(isEdit) ((Button)findViewById(R.id.edit)).setText("Done");
                else ((Button)findViewById(R.id.edit)).setText("Edit");
            }
        });
    }

    private void jump(PegGame.Pair p) {
        pegGame.jumpPeg(p.start,p.end);
        draw(pegGame);
    }

    private void createBoard() {
        start=-1;
        pegGame = new PegGame();
        unhighlightEverything();
        buttons.clear();
        draw(pegGame);
    }

    private void draw(final PegGame pegGame) {
        final String n = "button";
        final char[] pegs = PegGame.fill(Long.toBinaryString(pegGame.getPegs())).toCharArray();
        char[] outOfBounds = PegGame.fill(Long.toBinaryString(PegGame.OUT_OF_BOUNDS)).toCharArray();
        int j = 1;
        for (int i = 0; i < pegs.length; i++) {
            if(outOfBounds[i] == '1') continue;
            String name = n + "" + j;
            final Button b = findViewById(getResId(name, R.id.class));
            b.setText(pegs[i] == '0'?"":"X");
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isEdit) {
                        boolean isX = b.getText().equals("X");
                        b.setText(isX ? "" : "X");
                        PegButton button = buttons.get(b.getId());
                        long v = getLongByLoc(button.getOloc());
                        if(isX)
                            pegGame.setPegs(pegGame.getPegs()&~v);
                        else
                            pegGame.setPegs(pegGame.getPegs()|v);
                        draw(pegGame);
                    }else {
                        PegButton button = buttons.get(b.getId());
                        long v = getLongByLoc(button.getOloc());
                        List<PegGame.Pair> allMoves = pegGame.getAllMoves();
                        //if this is start then unhighlight everything
                        if(v==start){
                            unhighlightEverything();
                            start=-1;
                            return;
                        }boolean isAllowed = false;
                        for(PegGame.Pair pair : allMoves)
                            if(start == -1 && pair.start == v)
                                isAllowed = true;
                            else if(start != -1 && pair.end == v)
                                isAllowed = true;
                        if(!isAllowed) return;
                        if(pegGame.check(start,v)) {
                            //else if this is a moveable spot then jump to it and set start to -1 and unhighlight everything
                            jump(new PegGame.Pair(start,v));
                            start = -1;
                            unhighlightEverything();
                            return;
                        }
                        //if start is -1 highlight start and highlight all movables
                        if(start == -1) {
                            start = v;
                            b.getBackground().setColorFilter(0xFFAA4400, PorterDuff.Mode.MULTIPLY);
                            //filter by start
                            for(PegGame.Pair pair : allMoves)
                                if(pair.start == start)
                                    buttonsByLong.get(pair.end).getButton().getBackground()
                                            .setColorFilter(0xFF6393D6, PorterDuff.Mode.MULTIPLY);
                        }
                    }
                }
            });
            buttons.put(b.getId(),new PegButton(j,i,name,b));
            buttonsByLong.put(getLongByLoc(i),new PegButton(j,i,name,b));
            j++;
        }
    }

    private long getLongByLoc(int oloc) {
        final char[] pegs = PegGame.fill(Long.toBinaryString(pegGame.getPegs())).toCharArray();
        String s = "";
        for (int k = 0; k < pegs.length; k++)
            if (oloc == k) s += "1";
            else s += "0";
        for (int i = 0; i < PegGame.EACH_POSITION.length; i++)
            if(s.equals(PegGame.fill(Long.toBinaryString(PegGame.EACH_POSITION[i]))))
                return PegGame.EACH_POSITION[i];
        return -1;
    }

    private void unhighlightEverything() {
        for(PegButton b : buttons.values())
            b.getButton().getBackground().clearColorFilter();
    }

    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private class PegButton{
        boolean isPressed = false;
        int loc, oloc;
        String name;
        Button button;

        public PegButton(int loc, int oloc, String name, Button button) {
            this.loc = loc;
            this.oloc = oloc;
            this.name = name;
            this.button = button;
        }

        public int getOloc() {
            return oloc;
        }

        public void setOloc(int oloc) {
            this.oloc = oloc;
        }

        public boolean isPressed() {
            return isPressed;
        }

        public void setPressed(boolean pressed) {
            isPressed = pressed;
        }

        public int getLoc() {
            return loc;
        }

        public void setLoc(int loc) {
            this.loc = loc;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Button getButton() {
            return button;
        }

        public void setButton(Button button) {
            this.button = button;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PegButton pegButton = (PegButton) o;
            return loc == pegButton.loc &&
                    oloc == pegButton.oloc &&
                    Objects.equals(name, pegButton.name);
        }

        @Override
        public int hashCode() {

            return Objects.hash(loc, oloc, name);
        }

        @Override
        public String toString() {
            return "PegButton{" +
                    "isPressed=" + isPressed +
                    ", loc=" + loc +
                    ", name='" + name + '\'' +
                    ", button=" + button +
                    '}';
        }
    }
}
