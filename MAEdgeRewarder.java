import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

public class MAEdgeRewarder {
    static Random random = new Random();
    static Robot robot;
    Thread brainThread;
    volatile boolean isPaused = false;
    volatile boolean isRunning = false;
    static JButton btn2 = new JButton("Pause");
    static JLabel statusLabel = new JLabel("Status: Ready", SwingConstants.CENTER);
    static JProgressBar progressBar = new JProgressBar(0, 100);
    static int percentage = 0;
    static boolean started = false;
    static {
        try {
            robot = new Robot();
            robot.setAutoDelay(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MAEdgeRewarder() {
        ui();
    }

    public static void main(String[] args) {
        new MAEdgeRewarder();
    }

    void brain() {
        progressBar.setValue(percentage);
        int iteration = 40;
        isRunning = true;

        try {
            Runtime.getRuntime().exec("microsoft-edge");
            statusLabel.setText("Initializing Edge...");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                return;
            }
        } catch (IOException ex) {
            statusLabel.setText("Opening Edge Error 51!");
            JOptionPane.showMessageDialog(
                    null,
                    "This is a warning message!",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (int i = 0; i < iteration && isRunning; i++) {
            percentage = (int) (((i + 1) / (double) iteration) * 100);
            progressBar.setValue(percentage);
            while (isPaused && isRunning) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    return;
                }
            }

            statusLabel.setText("Opening Safety tab...");
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_T);
            robot.keyRelease(KeyEvent.VK_T);
            robot.keyRelease(KeyEvent.VK_CONTROL);

            String word = genWord();
            StringSelection selection = new StringSelection(word);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);

            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);

            int closingTimer = random.nextInt(4) + 4;

            for (int h = closingTimer; h > 0 && isRunning; h--) {
                statusLabel.setText("<html>Pasting Word: " + word + "<br>Closing tab in: " + h + " seconds</html>");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    return;
                }
            }

            statusLabel.setText("Tab closed.");

            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_W);
            robot.keyRelease(KeyEvent.VK_W);
            robot.keyRelease(KeyEvent.VK_CONTROL);

            statusLabel.setText("New tab opening...");

        }

        isRunning = false;
        statusLabel.setText("Automation complete.");
    }

    void btn1() {
        if (started) {
            statusLabel.setText("<html><b>Already Running!</b></html>");
            return;
        }

        started = false;

        if (percentage < 100) {
            percentage++;
            progressBar.setValue(percentage);
        }

        statusLabel.setText("<html><b>Opening Edge...</b></html>");

        if (brainThread == null || !brainThread.isAlive()) {
            brainThread = new Thread(() -> brain());
            brainThread.start();
        }
    }

    void btn2() {
        if (isRunning) {
            isPaused = !isPaused;

            if (isPaused) {
                btn2.setText("Continue");
            } else {
                btn2.setText("Pause");
            }
        }
    }

    void btn3() {
        btn2();
        int choice = JOptionPane.showConfirmDialog(
                null,
                "Do you want to kill the program?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            isRunning = false;
            isPaused = false;
            if (brainThread != null) {
                brainThread.interrupt();
            }
            System.out.println("Program killed.");
            System.exit(0);
        } else {
            btn2();
            System.out.println("Kill canceled. Program continues.");
        }
    }

    public String genWord() {
        String[] wpool = {"jablle", "apple", "grape", "stone", "flint", "crisp", "bloom", "shade", "glint", "brisk", "qz", "vyn", "kuro", "zeb", "miv", "trop", "wex", "laz", "nuv", "sni", "krel", "zorn", "plix", "drax", "blen", "vexi", "quon", "zint", "morb", "traz", "yep", "xul", "gri", "huz", "wom", "nex", "zog", "fli", "bex", "jup", "spro", "klim", "vorn", "zarp", "mink", "drel", "snub", "twix", "blor", "cren", "ziv", "lurk", "mash", "drip", "snag", "twap", "glen", "zook", "vlim", "krax", "jint", "plom", "snek", "bram", "wint", "zlep", "murf", "klen", "vrop", "dink", "slog", "trem", "blim", "gron", "zuff", "norb", "klap", "wrex", "dazz", "morb", "snix", "trug", "plaz", "blux", "crug", "vint", "zram", "grop", "twaz", "klon", "mib", "zebs", "krop", "flaz", "snuv", "grib", "womp", "drelz", "brop", "zlim", "jorn", "vlek", "squib", "dran", "pluv", "krez", "twob", "zintar", "morbix", "flarn", "grint", "snorb", "kluv", "wazz", "blern", "zooki", "vlimp", "druff", "slink", "twarp", "blint", "glorp", "zebul", "marn", "klink", "vropo", "dazzle", "snirp", "twint", "klarn", "mibbo", "zebro", "kroft", "flarny", "snuvy", "gribble", "womple", "drelzo", "bropp", "zlimp", "jib", "vexor", "squint", "dribble", "plonk", "krezz", "twibble", "zinter", "morbit", "florx", "grindle", "snorx", "kluvo", "wazzle", "blerno", "zookie", "vlimbo", "druffy", "slinky", "tworx", "blinto", "glorpo", "zebulo", "marno", "klinko", "vropix", "dazzlo", "snirpo", "twinto", "klarno", "mibber", "zebrix", "krofty", "flarnix", "snuvix", "gribber", "wompler", "drelzor", "broppo", "zlimbo", "jibber", "vexoro", "squinto", "dribbly", "plonko", "krezzo", "twibbly", "zintero", "morbito", "florxo", "grindel", "snorxo", "kluvon", "wazzly", "blernon", "zookin", "vlimber", "druffin", "slinker", "tworxo", "blinter", "glorpex", "zebulon", "marnix", "klinker", "vropper", "dazzrix", "snirple", "twintor", "klarnix", "mibz", "zebtron", "krofto", "flarnor", "snuvon", "gribzor", "womplix", "drelvex", "bropple", "zlimzor", "jibzo", "vextron", "squibbo", "dranix", "pluvon", "krezzoid", "twobble", "zintrex", "morbex", "flarnex", "grintor", "snorber", "kluvix", "wazzor", "blerzon", "zookar", "vlimzor", "druffor", "slinkor", "twarple", "blintor", "glorpix", "zeblix", "marnor", "klinkor", "vropzor", "dazzor", "snirzor", "twintix", "klarnor", "mibzor", "zeblark", "kroftix", "flarnzor", "snuvlor", "griblor", "womplor", "drelzorx", "broppix", "zlimlor", "jiblor", "vexlor", "squiblor", "dranlor", "pluvlor", "krezlor", "twoblor", "zintlor", "morblor", "flarnlor", "grintlor", "snorlor", "kluvlor", "wazzlor", "blerlor", "zooklor", "vlimlor", "drufflor", "slinklor", "twarlor", "blintlor", "glorplor", "zeblor", "marnlor", "klinklor", "vroplor", "dazzlor", "snirplor", "twintlor", "klarnlor", "miblor", "zeblorn", "kroftlor", "flarnlorn", "snuvlorn", "griblorn", "womplorn", "drelzorn", "bropporn", "zlimlorn", "jibnex", "vexnix", "squibn", "dranex", "pluvex", "krezex", "twobex", "zintrex", "morbix", "flarnik", "grintik", "snorbix", "kluvik", "wazzik", "blernik", "zookik", "vlimik", "druffik", "slinkik", "twarnik", "blintik", "glorpik", "zebrik", "marnik", "klinkik", "vropik", "dazzik", "snirpik", "twintik", "klarnik", "mibnik", "zebnik", "kroftik", "flarnik", "snuvik", "gribnik", "womplik", "drelvik", "broppik", "zlimnik", "jiblik", "vexlik", "squiblik", "dranlik", "pluvlik", "krezlik", "twoblik", "zintlik", "morblik", "flarnlik", "grintlik", "snorlik", "kluvlik", "wazzlik", "blerlik", "zooklik", "vlimlik", "drufflik", "slinklik", "twarlik", "blintlik", "glorplik", "zeblik", "marnlik", "klinklik", "vroplik", "dazzlik", "snirplik", "twintlik", "klarnlik", "miblik", "zeblarky", "kroftly", "flarnly", "snuvly", "gribly", "womply", "drelly", "bropply", "zlimly", "jibly", "vexly", "squibly", "dranly", "pluvly", "krezly", "twobly", "zintly", "morby", "flarny", "grinty", "snory", "kluvy", "wazzy", "blerry", "zooky", "vlimy", "druffy", "slinky", "twarly", "blinty", "glorpy", "zebby", "marny", "klinky", "vropy", "dazzy", "snirpy", "twinty", "klarny", "mibby", "zebzor", "krofty", "flarnor", "snuvor", "gribzo", "womplo", "drelzo", "broppo", "zlimpo", "jibzo", "vexzo", "squibzo", "dranzo", "pluvzo", "krezzo", "twobzo", "zintzo", "morbzo", "flarnzo", "grintzo", "snorzo", "kluvzo", "wazzzo", "blerzo", "zookzo", "vlimzo", "druffzo", "slinkzo", "twarrzo", "blintzo", "glorpzo", "zebzo", "marnzo", "klinkzo", "vropzo", "dazzzo", "snirzo", "twintzo", "klarnzo", "mibzo", "zebqu", "kroftqu", "flarnqu", "snuvqu", "gribqu", "womplqu", "drelqu", "broppqu", "zlimqu", "jibqu", "vexqu", "squibqu", "dranqu", "pluvqu", "krezqu", "twobqu", "zintqu", "morbqu", "flarnqu", "grintqu", "snorqu", "kluvqu", "wazzqu", "blerqu", "zookqu", "vlimqu", "druffqu", "slinkqu", "twarrqu", "blintqu", "glorpqu", "zebqu", "marnqu", "klinkqu", "vropqu", "dazzqu", "snirqu", "twintqu", "klarnqu", "mibqu", "zebex", "kroftex", "flarnex", "snuvex", "gribex", "womplex", "drelex", "broppex", "zlimex", "jibex", "vexex", "squibex", "dranexx", "pluvexx", "krezexx", "twobexx", "zintexx", "morbexx", "flarnexx", "grintexx", "snorbex", "kluvex", "wazzex", "blerex", "zookex", "vlimex", "druffex", "slinkex", "twarrex", "blintex", "glorpex", "zebrex", "marnex", "klinkex", "vropex", "dazzex", "snirpex", "twintex", "klarnex", "mibexx", "zeblux", "kroftux", "flarnux", "snuvux", "gribux", "womplux", "drelux", "broppux", "zlimux", "jibux", "vexux", "squibux", "dranux", "pluvux", "krezux", "twobux", "zintux", "morbux", "flarnuxx", "grintux", "snorux", "kluvux", "wazzux", "blerux", "zookux", "vlimuxx", "druffux", "slinkux", "twarrux", "blintux", "glorpux", "zebux", "marnux", "klinkux", "vropux", "dazzux", "snirux", "twintux", "klarnux", "mibuxx", "zebvox", "kroftvox", "flarnvox", "snuvvox", "gribvox", "womplvox", "drelvox", "broppvox", "zlimvox", "jibvox", "vexvox", "squibvox", "dranvox", "pluvvox", "krezvox", "twobvox", "zintvox", "morbvox", "flarnvox", "grintvox", "snorvox", "kluvvox", "wazzvox", "blervox", "zookvox", "vlimvox", "druffvox", "slinkvox", "twarrvox", "blintvox", "glorpvox", "zebvox", "marnvox", "klinkvox", "vropvox", "dazzvox", "snirvox", "twintvox", "klarnvox", "mibvox", "zebrixx", "kroftyx", "flarnyx", "snuvyx", "gribyx", "womplyx", "drelyx", "broppyx", "zlimyx", "jibyxx", "vexyxx", "squibyxx", "dranyxx", "pluvyxx", "krezyxx", "twobyxx", "zintyxx", "morbyxx", "flarnyxx", "grintyxx", "snoryxx", "kluvyxx", "wazzyxx", "bleryxx", "zookyxx", "vlimyxx", "druffyxx", "slinkyxx", "twarryxx", "blintyxx", "glorpyxx", "zebyxx", "marnyxx", "klinkyxx", "vropyxx", "dazzyxx", "snirpyxx", "twintyxx", "klarnyxx", "mibyxx", "zebmax", "kroftmax", "flarnmax", "snuvmax", "gribmax", "womplmax", "drelmax", "broppmax", "zlimmax", "jibmax", "vexmax", "squibmax", "dranmax", "pluvmax", "krezmax", "twobmax", "zintmax", "morbmax", "flarnmaxx", "grintmax", "snormax", "kluvmax", "wazzmax", "blermax", "zookmax", "vlimmax", "druffmax", "slinkmax", "twarrmax", "blintmax", "glorpmax", "zebmaxx", "marnmax", "klinkmax", "vropmax", "dazzmax", "snirmax", "twintmax", "klarnmax", "mibmaxx", "zebnex", "kroftnex", "flarnnex", "snuvnex", "gribnex", "womplnex", "drelnex", "broppnex", "zlimnex", "jibnexx", "vexnex", "squibnex", "drannex", "pluvnex", "krezznex", "twobnex", "zintnex", "morbnex", "flarnnexx", "grintnex", "snornex", "kluvnex", "wazznex", "blernex", "zooknex", "vlimnex", "druffnex", "slinknex", "twarrnex", "blintnex", "glorpnex", "zebnexx", "marnnex", "klinknex", "vropnex", "dazznex", "snirnex", "twintnex", "klarnnex", "mibnex", "zebtor", "kroftor", "flarnorx", "snuvtor", "gribtor", "womplorx", "dreltor", "bropptor", "zlimtor", "jibtor", "vextor", "squibtor", "drantor", "pluvtor", "krezztor", "twobtor", "zinttor", "morbtor", "flarntor", "grinttor", "snortor", "kluvtor", "wazztor", "blertor", "zooktor", "vlimtor", "drufftor", "slinktor", "twarrtor", "blinttor", "glorptor", "zebtorx", "marntor", "klinktor", "vroptor", "dazztor", "snirptor", "twinttor", "klarntor", "mibtor", "zebzorx", "kroftzor", "flarnzorx", "snuvzor", "gribzorx", "womplzor", "drelzorx", "broppzor", "zlimzorx", "jibzorx", "vexzor", "squibzor", "dranzor", "pluvzor", "krezzor", "twobzor", "zintzor", "morbzor", "flarnzor", "grintzor", "snorzor", "kluvzor", "wazzzor", "blerzor", "zookzor", "vlimzorx", "druffzor", "slinkzor", "twarrzor", "blintzor", "glorpzor", "zebzor", "marnzor", "klinkzor", "vropzorx", "dazzzor", "snirzorx", "twintzor", "klarnzor", "mibzorx", "zebluxx", "kroftlux", "flarnlux", "snuvlux", "griblux", "wompllux", "dreluux", "broppuxx", "zlimlux", "jiblux", "vexlux", "squiblux", "dranlux", "pluvlux", "krezlux", "twoblux", "zintlux", "morblux", "flarnluxx", "grintlux", "snorlux", "kluvlux", "wazzlux", "blerlux", "zooklux", "vlimlux", "drufflux", "slinklux", "twarrlux", "blintlux", "glorplux", "zebluxa", "marnlux", "klinklux", "vroplux", "dazzlux", "snirlux", "twintlux", "klarnlux", "miblux", "zebrixa", "kroftxa", "flarnxa", "snuvxa", "gribxa", "womplxa", "drelxa", "broppxa", "zlimxa", "jibxa", "vexxa", "squibxa", "dranxa", "pluvxa", "krezxa", "twobxa", "zintxa", "morbxa", "flarnxa", "grintxa", "snorxa", "kluvxa", "wazzxa", "blerxa", "zookxa", "vlimxa", "druffxa", "slinkxa", "twarrxa", "blintxa", "glorpxa", "zebxar", "marnxar", "klinkxar", "vropxar", "dazzxar", "snirxar", "twintxar", "klarnxar", "mibxar", "zebzar", "kroftzar", "flarnzar", "snuvzar", "gribzar", "womplzar", "drelzar", "broppzar", "zlimzar", "jibzar", "vexzar", "squibzar", "dranzar", "pluvzar", "krezzar", "twobzar", "zintzar", "morbzar", "flarnzarx", "grintzar", "snorzar", "kluvzar", "wazzzar", "blerzar", "zookzar", "vlimzarx", "druffzar", "slinkzar", "twarrzar", "blintzar", "glorpzar", "zebzarn", "marnzarn", "klinkzarn", "vropzarn", "dazzzarn", "snirzarn", "twintzarn", "klarnzarn", "mibzarn", "zebqux", "kroftqux", "flarnqux", "snuvqux", "gribqux", "womplqux", "drelqux", "broppqux", "zlimqux", "jibqux", "vexqux", "squibqux", "dranqux", "pluvqux", "krezqux", "twobqux", "zintqux", "morbqux", "flarnqux", "grintqux", "snorqux", "kluvqux", "wazzqux", "blerqux", "zookqux", "vlimqux", "druffqux", "slinkqux", "twarrqux", "blintqux", "glorpqux", "zebluxy", "marnluxy", "klinkluxy", "vropluxy", "dazzluxy", "snirluxy", "twintluxy", "klarnluxy", "mibluxy", "zebnux", "kroftnux", "flarnnux", "snuvnux", "gribnux", "womplnux", "drelnux", "broppnux", "zlimnux", "jibnux", "vexnux", "squibnux", "drannux", "pluvnux", "krezznux", "twobnux", "zintnux", "morbnux", "flarnnuxx", "grintnux", "snornux", "kluvnux", "wazznux", "blernux", "zooknux", "vlimnux", "druffnux", "slinknux", "twarrnux"};

        int wordCount = random.nextInt(7) + 12;
        String words = "";
        Set<Integer> usedIndices = new HashSet<>();

        while (usedIndices.size() < wordCount) {
            int ranIndex = random.nextInt(1000);
            if (!usedIndices.contains(ranIndex)) {
                usedIndices.add(ranIndex);
                words += wpool[ranIndex];
                if (usedIndices.size() < wordCount) {
                    words += " ";
                }
            }
        }

        return words;
    }

    public void ui() {
        JFrame frame = new JFrame("MAEDgeRewarder");
        frame.setAlwaysOnTop(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        Color bg = Color.BLACK;
        Color fg = Color.WHITE;

        progressBar.setValue(percentage);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(400, 60));
        progressBar.setBackground(bg);
        progressBar.setForeground(fg);

        statusLabel.setForeground(fg);
        statusLabel.setBackground(bg);
        statusLabel.setPreferredSize(new Dimension(400, 60));
        statusLabel.setOpaque(true);

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBackground(bg);
        topPanel.add(progressBar);
        topPanel.add(statusLabel);

        JButton btn1 = new JButton("Start");
        JButton btn3 = new JButton("Kill");

        Dimension buttonSize = new Dimension(100, 30);
        for (JButton btn : new JButton[] { btn1, btn2, btn3 }) {
            btn.setPreferredSize(buttonSize);
            btn.setBackground(bg);
            btn.setForeground(fg);
            btn.setFocusPainted(false);
        }

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(btn1, BorderLayout.WEST);
        frame.add(btn2, BorderLayout.CENTER);
        frame.add(btn3, BorderLayout.EAST);

        frame.getContentPane().setBackground(bg);
        frame.setVisible(true);

        btn1.addActionListener(e -> {
            btn1();

        });

        btn2.addActionListener(e -> {
            btn2();
        });

        btn3.addActionListener(e -> {
            btn3();
        });

    }

}
