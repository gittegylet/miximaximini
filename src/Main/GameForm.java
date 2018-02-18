package Main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.List;

import java.util.Timer;
import java.util.TimerTask;


public class GameForm extends JFrame {

    countDown myCountDowner;
    boolean tippClicked;

    int exX;
    int exY;


    public class countDown {

        int interval;

        Timer timer;

        JButton[][] fields;
        int[][] matrix;

        public countDown(JButton[][] fields, int[][] matrix) {

            this.fields = fields;
            this.matrix = matrix;

            main(new String[]{String.valueOf((gameTime > 10 ? gameTime-- : gameTime))});

        }

        public void main(String[] args) {

            int delay = 1000;
            int period = 1000;
            timer = new Timer();
            interval = Integer.parseInt(args[0]);

            timer.scheduleAtFixedRate(new TimerTask() {

                public void run() {

                    if (!tippClicked && exPoints == points) {

                        int time = setInterval();
                        if (time > 5) fields[matrix.length][matrix[0].length - 1].setText(String.valueOf(time));
                        else if (time > 0) {

                            fields[matrix.length][matrix[0].length - 1].setBackground(Color.orange);
                            fields[matrix.length][matrix[0].length - 1].setForeground(Color.RED);
                            fields[matrix.length][matrix[0].length - 1].setFont(new Font("Monaco", Font.BOLD, 22));
                            fields[matrix.length][matrix[0].length - 1].setText(String.valueOf(time));
                        }
                        else {

                            fields[matrix.length][matrix[0].length - 1].setText("zero");

                            timer.cancel();
                            popUp("Letelt az időd!! (-1 pont)", false);

                            newTable();
                        }

                    }
                    else if (tippClicked || exPoints != points) timer.cancel();
                    //{
                        ///int notGrayFields = (int) Arrays.stream(fields).flatMap(r -> Arrays.stream(r)).filter(f -> f.getBackground() != Color.gray).count();

                        ///int fullFieldsCount = (int) Arrays.stream(fields).flatMap(r -> Arrays.stream(r)).count();
                        ///if (notGrayFields < fullFieldsCount - 1) timer.cancel();
                    //}

                }
            }, delay, period);
        }


        private final int setInterval() {
            if (interval == 1)
                timer.cancel();
            return --interval;
        }
    }

    int gameTime;

    Integer[] rowSums;
    Integer[] colSums;

    int[][] matrix;
    JButton[][] fields;
    int size, difficulty, minValue, maxValue;

    boolean isSquare;
    int selectedFields;

    int points;
    int exPoints;

    boolean isMaxRow,
            isMinRow,
            isMaxCol,
            isMinCol;

    int maxRow, minRow, maxCol, minCol;

    boolean rowMinHit, rowMaxHit, colMaxHit, colMinHit;

    boolean doubleTip;

    static Random vel = new Random();

    int clickButton = 0;

    Container area;


    public GameForm(int size, int difficulty, int minValue, int maxValue) {

        this.size = size;
        this.difficulty = difficulty;
        this.minValue = minValue;
        this.maxValue = maxValue;

        this.points = 0;
        this.gameTime = 31;

        initGUI(size, difficulty);
    }


    private void setRandFields(int minValue, int maxValue) {

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {

                matrix[i][j] = vel.nextInt(maxValue - minValue + 1) + minValue;
            }
        }

    }


    private int columnFullCount(int x) {

        int sum = 0;
        for (int i = 0; i < matrix.length; i++) {

            sum += matrix[i][x];
        }

        return sum;
    }


    private int rowFullCount(int y) {

        int sum = 0;
        for (int i = 0; i < matrix[0].length; i++) {

            sum += matrix[y][i];
        }

        return sum;
    }


    private void viewSelectedRowAndColumn(boolean deSelect, int x, int y, int X, int Y) {

        if (!deSelect) {

            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[0].length; j++) {

                    if (fields[i][j].getBackground() == Color.gray && (j == x || i == y) &&
                        //fields[i][j].getBackground() != Color.lightGray && fields[i][j].getBackground() != Color.pink &&
                        //fields[i][j].getBackground() != Color.white &&
                        ((selectedFields == 0 && !(j == x && i == y)) || (selectedFields > 0 && !(j == X && i == Y))))
                            fields[i][j].setBackground(Color.GREEN);
                    else if (fields[i][j].getBackground() == Color.gray &&
                            //fields[i][j].getBackground() != Color.lightGray &&
                            //fields[i][j].getBackground() != Color.pink &&
                            ((selectedFields == 0 && j == x && i == y) || (selectedFields > 0 && !(j == X && i == Y))))
                                fields[i][j].setBackground(Color.orange);
                }
            }
        }
        else {

            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[0].length; j++) {

                    if (fields[i][j].getBackground() != Color.gray || fields[i][j].getBackground() != Color.pink ||
                        fields[i][j].getBackground() != Color.lightGray)
                            fields[i][j].setBackground(Color.gray);
                }
            }
        }

    }


    private void unSetAllOtherFields(boolean unSetting, int x, int y) {

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {

                if ((fields[i][j].getBackground() != Color.lightGray || fields[i][j].isEnabled()) && !(i == y && j == x) && unSetting) {

                    fields[i][j].setBackground(Color.lightGray);
                    if (fields[i][j].isEnabled()) fields[i][j].setEnabled(false);
                }
                else if (unSetting && i == y && j == x) {

                    if (selectedFields == 0 || fields[i][j].getBackground() == Color.gray)
                        fields[i][j].setBackground(Color.pink);
                    else if (selectedFields == 1 && fields[i][j].getBackground() == Color.pink)
                        fields[i][j].setBackground(new Color(255, 128,64, 32));

                    if (!fields[i][j].isEnabled()) fields[i][j].setEnabled(true);
                }
                else if (!unSetting && ((i == minRow && (j != maxCol)) || (j == minCol && i != maxRow)) && !(j == x && i == y)) {

                    fields[i][j].setBackground(Color.blue);
                }
                else if (!unSetting && (i == maxRow || j == maxCol) && !(j == x && i == y)) {

                    fields[i][j].setBackground(Color.red);
                }
                else if (!unSetting && !(i == maxRow || i == minRow || j == maxCol || j == minCol) && !(j == x && i == y)) {

                    fields[i][j].setBackground(Color.gray);
                }

            }
        }
    }


    private void outPut() {

        String myTipWas = "";

        int diffPoints = points - exPoints;

        if (diffPoints >= 1) {

            if (diffPoints % 2 == 0) diffPoints = diffPoints / 2;

            if (myTipWas == "" && colMaxHit) { myTipWas = "legmagasabb értékű oszlopot (+" + diffPoints + "p)"; }
            else if (colMaxHit) { myTipWas += "\n és a legmagasabb értékű oszlopot (+" + diffPoints + "p)"; }
            if (myTipWas == "" && colMinHit) { myTipWas = "legkisebb értékű oszlopot (+" + diffPoints + "p)"; }
            else if (colMinHit) { myTipWas += "\n és a legkisebb értékű oszlopot (+" + diffPoints + "p)"; }
            if (myTipWas == "" && rowMaxHit) { myTipWas = "legmagasabb értékű sort (+" + diffPoints + "p)"; }
            else if (rowMaxHit) { myTipWas += "\n és a legmagasabb értékű sort (+" + diffPoints + "p)"; }
            if (myTipWas == "" && rowMinHit) { myTipWas = "legkisebb értékű sort (+" + diffPoints + "p)"; }
            else if (rowMinHit) { myTipWas += "\n és a legkisebb értékű sort (+" + diffPoints + "p)"; }

        }
        else if (diffPoints < 0) {

            if (diffPoints == -1) myTipWas = "Sajnos nem talált!! (" + diffPoints + "p)";
            else if (diffPoints == -2) myTipWas = "Egy találatod sem volt! (" + diffPoints + "p)";
            else if (diffPoints == -4) myTipWas = "Egy találatod sem volt! (" + diffPoints + "p)";
        }
        else if (doubleTip) {

            if (colMaxHit) { myTipWas += "a legmagasabb értékű OSZLOPON (a SOR mellétrafált)"; }
            else if (colMinHit) { myTipWas += "a legkisebb értékű OSZLOPON (a SOR mellétrafált)"; }
            else if (rowMaxHit) { myTipWas += "a legmagasabb értékű SORON (az OSZLOP mellétrafált)"; }
            else if (rowMinHit) { myTipWas += "a legkisebb értékű SORON (az OSZLOP mellétrafált)"; }
        }

        myTipWas += "\nPONTSZÁMOD: " + points;

        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        switch (diffPoints) {
            
            case 0:  popUp("Nem kapsz pontot! \nTalálat " + myTipWas, false);  break;
            case 1:  popUp("Eltaláltad a " + myTipWas, false);  break;
            case 2:  popUp("Eltaláltad a " + myTipWas, false);  break;
            case 3:  popUp("Eltaláltad a " + myTipWas, false);  break;
            case 4:  popUp("Eltaláltad a " + myTipWas, false);  break;
            case -1:  popUp(myTipWas, false);  break;
            case -2:  popUp(myTipWas, false);  break;
            case -4:  popUp(myTipWas, false);  break;
            default:  break;
        }
    }


    private void newPointsCalc(int x, int y) {

        rowSums = new Integer[matrix.length];
        colSums = new Integer[matrix[0].length];

        for (int Y = 0; Y < matrix.length; Y++) {

            rowSums[Y] = rowFullCount(Y);

        }

        for (int X = 0; X < matrix[0].length; X++) {

            colSums[X] = columnFullCount(X);

        }

        //Integer[] rowSums2 = rowSums.clone();
        //Integer[] colSums2 = colSums.clone();

        //Arrays.sort(rowSums2);
        //Arrays.sort(colSums2);

        //Person obama = new Person("Barack Obama", 53);
        //     Person bush2 = new Person("George Bush", 68);
        //     Person clinton = new Person("Bill Clinton", 68);
        //     Person bush1 = new Person("George HW Bush", 90);
        //
        //     Person[] personArray = new Person[] {obama, bush2, clinton, bush1};
        //     List<Person> personList = Arrays.asList(personArray);
        //
        //final Comparator<Person> comp = (p1, p2) -> Integer.compare( p1.getAge(), p2.getAge());
        //    Person oldest = personList.stream()
        //                              .max(comp)
        //                              .get();

        //int maxRow = Arrays.asList(rowSums).indexOf(rowSums2[rowSums2.length - 1]);
        // => megkeresi a legnagyobb összértékű sort (pontosabban annak az indexét a mátrixban!)
        //int minRow = Arrays.asList(rowSums).indexOf(rowSums2[0]);
        // => megkeresi a legkisebb összértékű sort...
        //int maxCol = Arrays.asList(colSums).indexOf(colSums2[colSums2.length - 1]);
        // => megkeresi a legnagyobb összértékű oszlopot...
        //int minCol = Arrays.asList(colSums).indexOf(colSums2[0]);
        // => megkeresi a legkisebb összértékű oszlopot...

        //int maxRow0 = Arrays.asList(Arrays.asList(rowSums).get(0)).indexOf(rowSums2[rowSums2.length - 1]);
        // => megkeresi a legnagyobb összértékű sort (pontosabban annak az indexét a mátrixban!)
        //int minRow0 = Arrays.asList(Arrays.asList(rowSums).get(0)).indexOf(rowSums2[0]);
        // => megkeresi a legkisebb összértékű sort...boolean rowMinHit = y == minRow ? true : false;
        //int maxCol0 = Arrays.asList(Arrays.asList(colSums).get(0)).indexOf(colSums2[colSums2.length - 1]);
        // => megkeresi a legnagyobb összértékű oszlopot...boolean colMinHit = x == minCol ? true : false;
        //int minCol0 = Arrays.asList(Arrays.asList(colSums).get(0)).indexOf(colSums2[0]);
        // => megkeresi a legkisebb összértékű oszlopot...

        maxRow = Arrays.asList(rowSums).indexOf(Arrays.stream(rowSums).max((a, b) -> Integer.compare(a, b)).get());
        // => megkeresi a legnagyobb összértékű sort (pontosabban annak az indexét a mátrixban!)
        minRow = Arrays.asList(rowSums).indexOf(Arrays.stream(rowSums).min((a, b) -> Integer.compare(a, b)).get());
        // => megkeresi a legkisebb összértékű sort...
        maxCol = Arrays.asList(colSums).indexOf(Arrays.stream(colSums).max((a, b) -> Integer.compare(a, b)).get());
        // => megkeresi a legnagyobb összértékű oszlopot...
        minCol = Arrays.asList(colSums).indexOf(Arrays.stream(colSums).min((a, b) -> Integer.compare(a, b)).get());
        // => megkeresi a legkisebb összértékű oszlopot...

        rowMaxHit = y == maxRow ? true : false;
        rowMinHit = y == minRow ? true : false;
        colMaxHit = x == maxCol ? true : false;
        colMinHit = x == minCol ? true : false;

        exPoints = points;

        if (doubleTip) {

            isMaxRow = true;
            isMinRow = true;
            isMaxCol = true;
            isMinCol = true;

            if (!rowMaxHit && !rowMinHit && !colMaxHit && !colMinHit) points -= 2;  // =>  Egyetlen legnagyobb v. legkisebb oszlop/sor sem talált!!
            else if (rowSums[maxRow] != colSums[maxCol] && rowSums[minRow] != colSums[minCol]) {

                if (rowMaxHit || rowMinHit) points++;
                else points--;

                if (colMaxHit || colMinHit) points++;
                else points--;
            }
            else {

                if (rowMaxHit && (rowSums[y] == rowSums[maxRow] && (rowSums[maxRow] == colSums[maxCol] ||
                    rowSums[maxRow] == colSums[minCol]))) points += 2;
                else if (rowMinHit && (rowSums[y] == rowSums[minRow] && (rowSums[minRow] == colSums[minCol] ||
                         rowSums[minRow] == colSums[maxCol]))) points += 2;
                else if (colMaxHit && (colSums[x] == colSums[maxCol] && (colSums[maxCol] == rowSums[maxRow] ||
                         colSums[maxCol] == rowSums[minRow]))) points += 2;
                else if (colMinHit && (colSums[x] == colSums[minCol] && (colSums[minCol] == rowSums[minRow] ||
                         colSums[minCol] == rowSums[maxRow]))) points += 2;

                if (colMaxHit && rowMaxHit && rowSums[maxRow] == colSums[maxCol]) points += 2;
                else if (colMinHit && rowMinHit && rowSums[minRow] == colSums[minCol]) points += 2;

            }

        }
        else {

            if (colMaxHit && isMaxCol) points++;
            else if (colMinHit && isMinCol) points++;
            else if (rowMaxHit && isMaxRow) points++;
            else if (rowMinHit && isMinCol) points++;
            else points--;

        }

        ///return points;
    }

    private void newTable() {

        ///this.setVisible(false);
        this.area.setVisible(false);

        //this.dispose();
        //Main.main(null);

        ////this(size, difficulty, minValue, maxValue);

        area.removeAll();
        initGUI(size, difficulty);

    }


    private void initGUI(int size, int difficulty) {

        this.matrix = new int[size][size];

        setRandFields(minValue, maxValue);

        this.tippClicked = false;
        this.isSquare = false;
        this.selectedFields = 0;

        this.exPoints = points;

        this.isMaxRow = false;
        this.isMinRow = false;
        this.isMaxCol = false;
        this.isMinCol = false;

        this.rowMaxHit = false;
        this.colMaxHit = false;
        this.rowMinHit = false;
        this.colMinHit = false;

        setTitle("Mixi-Maxi-Mini");
        setSize(450, 450);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        area = getContentPane();
        area.setVisible(false);

        GridLayout glay = new GridLayout(0, size);

        fields = new JButton[size + 1][size];
        area.setLayout(glay);

        myCountDowner = new countDown(fields, matrix);


        for (int i = 0; i <= matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {

                if (i < matrix.length) {

                    fields[i][j] = new JButton();
                    area.add(fields[i][j]);

                    fields[i][j].setForeground(new Color(128, 0,128, 205));
                    fields[i][j].setBackground(Color.gray);

                    fields[i][j].setFont(new Font("Monaco", Font.PLAIN, 25));
                    fields[i][j].setBorder(BorderFactory.createLineBorder(new Color(185, 122,87, 5)));

                    fields[i][j].setText(String.valueOf(matrix[i][j]));

                }
                else {

                    fields[i][j] = new JButton();
                    area.add(fields[i][j]);

                    fields[i][j].setForeground(Color.black);
                    if (j < matrix[0].length - 1) fields[i][j].setBackground(Color.black);
                    else  fields[i][j].setBackground(Color.lightGray);


                    fields[i][j].setFont(new Font("Tahoma", Font.PLAIN, 15));

                    fields[i][j].setBorderPainted(false);
                    if (j < matrix[0].length - 1) fields[i][j].setEnabled(false);
                }

                final int i2 = i;
                final int j2 = j;


                fields[i][j].addMouseListener(new MouseAdapter() {

                    public void mouseEntered(MouseEvent evt) {

                        if (((i2 != exY && j2 != exX) && exX != -1) && fields[i2][j2].getBackground() != Color.lightGray) {
                            viewSelectedRowAndColumn(false, j2, i2, exX, exY);

                            playSound("sounds/klakk.wav");
                        }
                    }

                    public void mouseExited(MouseEvent evt) {

                        if (((i2 != exY && j2 != exX) && exX != -1) && fields[i2][j2].getBackground() != Color.lightGray)
                            viewSelectedRowAndColumn(true, j2, i2, exX, exY);

                    }


                    public void mouseClicked(MouseEvent me) {

                    //if ((me.getModifiers() & InputEvent.BUTTON1_DOWN_MASK) != 0) ...
                    //==> ha MouseEvent, akkor így is használható??!  ->> event.isShiftDown()

                    boolean shiftDown = me.getModifiersEx() != 0 ? true : false;
                    clickButton = (me.getButton() == MouseEvent.BUTTON1 ? 1 : (me.getButton() > MouseEvent.BUTTON1 ? 2 : 0));
                    //int clickButton = InputEvent.BUTTON1_DOWN_MASK != 0 ? 1 : (InputEvent.BUTTON2_DOWN_MASK != 0 ? 2 : 0);
                        
                    if (clickButton != 0) {

                        int X = j2;
                        int Y = i2;

                        int specField = 4;
                        if (i2 != 0) specField--; else if (!shiftDown) isMaxCol = true;
                        if (j2 != 0) specField--; else if (!shiftDown) isMaxRow = true;
                        if (i2 != matrix.length - 1) specField--; else if (!shiftDown) isMinCol = true;
                        if (j2 != matrix[0].length - 1) specField--; else if (!shiftDown) isMinRow = true;

                        selectedFields = (int) Arrays.stream(fields).flatMap(r -> Arrays.stream(r)).filter(f -> f.getBackground() == Color.pink).count();


                        if (selectedFields > 0 && (exX != X || exY != Y)) {

                            Arrays.stream(fields).flatMap(r -> Arrays.stream(r)).forEach(f -> {
                                if (f.getBackground() != Color.gray) f.setBackground(Color.gray);
                            });
                            selectedFields = 0;
                            doubleTip = false;
                        }

                        exX = X;
                        exY = Y;

                        List<Boolean> trueMaxOrMin = new ArrayList<Boolean>();

                        if (isMaxRow) trueMaxOrMin.add(isMaxRow);
                        if (isMinRow) trueMaxOrMin.add(isMinRow);
                        if (isMaxCol) trueMaxOrMin.add(isMaxCol);
                        if (isMinCol) trueMaxOrMin.add(isMinCol);

                        if (selectedFields == 0 || trueMaxOrMin.size() > 1) {

                            doubleTip = false;
                            isMaxRow = false;
                            isMinRow = false;
                            isMaxCol = false;
                            isMinCol = false;
                        }


                        if (!doubleTip && specField == 1 && !shiftDown && clickButton == 1 &&
                            ((selectedFields == 0 && (fields[i2][j2].getBackground() == Color.gray ||
                            fields[i2][j2].getBackground() == (new Color(210, 111, 210, 12)) ||
                            fields[i2][j2].getBackground() == (new Color(210, 123, 210, 121)))) ||
                            (selectedFields == 1 && fields[i2][j2].getBackground() == Color.pink))) {

                            if (selectedFields == 0) {
                                unSetAllOtherFields(true, j2, i2);
                                playSound("sounds/kloty.wav");
                            }
                            else {
                                doubleTip = false;

                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                tippClicked = true;

                                newPointsCalc(X, Y);
                                unSetAllOtherFields(false, j2, i2);

                                fields[i2][j2].setBackground(Color.lightGray);
                                outPut();
                            }
                        }
                        else if (!doubleTip && specField == 2 && !shiftDown &&
                                ((selectedFields == 0 && fields[i2][j2].getBackground() != Color.pink) ||
                                        (selectedFields == 1 && fields[i2][j2].getBackground() == Color.pink))) {
                            // => valamelyik sarok esetén... a tippelésünk irányát (függőleges/vízszintes) a bal-jobb
                            //    egér kattintással lehet megerősíteni!!!

                            if (selectedFields == 1) {

                                doubleTip = false;  // egyszerre csak egy irányra /oszlopra VAGY sorra/ történő tippelés!!!

                                if (clickButton == 2) {   // ha a jobb egérgombot nyomtuk -> ekkor a sorra tippelünk!!

                                    if (isMaxCol) isMaxCol = false;
                                    if (isMinCol) isMinCol = false;
                                }
                                else {   // ha a bal egérgombot nyomtuk -> ekkor az oszlopra tippelünk!!

                                    if (isMaxRow) isMaxRow = false;
                                    if (isMinRow) isMinRow = false;
                                }

                            }

                            if (selectedFields == 0) {
                                unSetAllOtherFields(true, j2, i2);
                                playSound("sounds/kloty.wav");
                            }
                            else {
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                tippClicked = true;

                                newPointsCalc(X, Y);
                                unSetAllOtherFields(false, j2, i2);

                                fields[i2][j2].setBackground(Color.lightGray);
                                outPut();
                            }

                        }
                        else if (clickButton == 1 && shiftDown &&
                                ((selectedFields == 0 && fields[i2][j2].getBackground() != Color.pink) ||
                                        (selectedFields == 1 || fields[i2][j2].getBackground() == Color.pink))) {

                            if (selectedFields == 0) {
                                if (specField == 0) doubleTip = true;
                                unSetAllOtherFields(true, j2, i2);
                                playSound("sounds/kloty.wav");
                            }
                            else {

                                if (!doubleTip) doubleTip = true;

                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                tippClicked = true;

                                newPointsCalc(X, Y);
                                unSetAllOtherFields(false, j2, i2);

                                fields[i2][j2].setBackground(Color.lightGray);
                                outPut();
                            }

                        }


                        if (tippClicked || exPoints != points || (selectedFields > 0 && doubleTip && fields[i2][j2].getBackground() == Color.lightGray)) {

                            if (exPoints >= points)
                                playSound("sounds/klakk.wav");
                            else playSound("sounds/phuhhuuj.wav");

                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            exPoints = points;

                            myCountDowner.timer.cancel();

                            newTable();
                        }
                    }

                }});

                /*fields[i][j].addMouseListener(new MouseAdapter() {  //=>  egyszerre két külön mouseAdapter nem lehet!!??

                });*/
            }
        }

        if (!area.isVisible()) area.setVisible(true);

    }


    private boolean popUp(String message, boolean gameOver) {

        if (!gameOver) { JOptionPane.showMessageDialog(null, message); return false; }
        else {

            int reply = JOptionPane.showConfirmDialog(null, message + "\nJátsszunk még...?", "GAME OVER", JOptionPane.YES_NO_OPTION);

            if (reply == JOptionPane.YES_OPTION) {

                JOptionPane.showMessageDialog(null, "Indul a következő játék!");

                return true;
            }
            else {

                System.exit(0);
                return false;
            }
        }

    }


    public static synchronized void playSound(final String path) {
        new Thread(new Runnable() {

            public void run() {
                try {
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(path).getAbsoluteFile());
                    clip.open(audioInputStream);
                    clip.start();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }

}