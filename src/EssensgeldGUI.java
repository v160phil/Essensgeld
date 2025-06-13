import javax.swing.*;
import java.awt.*;
import java.io.*;

public class EssensgeldGUI extends JFrame {

    private double morgensSatz, mittagsSatz, abendsSatz;
    private final File satzDatei = new File("geldsaetze.txt");

    private JTextField tfMorgens, tfMittags, tfAbends;
    private JLabel lblMorgensRate, lblMittagsRate, lblAbendsRate;
    private JLabel lblGesamt;
    private JButton btnBerechnen, btnSaetze;

    public EssensgeldGUI() {
        if (!ladeOderFrageGeldsaetze(false)) System.exit(0);
        baueGUI();
    }

    private void baueGUI() {
        setTitle("Essensgeld Rechner");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(320, 420);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 249, 255));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,10,5,10);
        c.fill = GridBagConstraints.HORIZONTAL;
        Font font = new Font("Segoe UI", Font.PLAIN, 14);
        Font boldFont = new Font("Segoe UI", Font.BOLD, 16);

        // ===== Morgens =====
        c.gridx=0; c.gridy=0; c.gridwidth=1;
        JLabel lblMorgens = new JLabel("Morgens:");
        lblMorgens.setFont(boldFont);
        panel.add(lblMorgens, c);

        c.gridy=1;
        lblMorgensRate = new JLabel("("+format(morgensSatz)+" €)");
        lblMorgensRate.setFont(font);
        panel.add(lblMorgensRate, c);

        c.gridx=1;
        JPanel pM = createInputPanel(font);
        tfMorgens = new JTextField(4);
        tfMorgens.setFont(font);
        pM.add(tfMorgens);
        pM.add(new JLabel("mal"));
        panel.add(pM, c);

        c.gridx=0; c.gridy=2; c.gridwidth=2;
        panel.add(new JSeparator(), c);

        // ===== Mittags =====
        c.gridwidth=1; c.gridx=0; c.gridy=3;
        JLabel lblMittags = new JLabel("Mittags:");
        lblMittags.setFont(boldFont);
        panel.add(lblMittags, c);

        c.gridy=4;
        lblMittagsRate = new JLabel("("+format(mittagsSatz)+" €)");
        lblMittagsRate.setFont(font);
        panel.add(lblMittagsRate, c);

        c.gridx=1;
        JPanel pMi = createInputPanel(font);
        tfMittags = new JTextField(4);
        tfMittags.setFont(font);
        pMi.add(tfMittags);
        pMi.add(new JLabel("mal"));
        panel.add(pMi, c);

        c.gridx=0; c.gridy=5; c.gridwidth=2;
        panel.add(new JSeparator(), c);

        // ===== Abends =====
        c.gridwidth=1; c.gridx=0; c.gridy=6;
        JLabel lblAbends = new JLabel("Abends:");
        lblAbends.setFont(boldFont);
        panel.add(lblAbends, c);

        c.gridy=7;
        lblAbendsRate = new JLabel("("+format(abendsSatz)+" €)");
        lblAbendsRate.setFont(font);
        panel.add(lblAbendsRate, c);

        c.gridx=1;
        JPanel pA = createInputPanel(font);
        tfAbends = new JTextField(4);
        tfAbends.setFont(font);
        pA.add(tfAbends);
        pA.add(new JLabel("mal"));
        panel.add(pA, c);

        c.gridx=0; c.gridy=8; c.gridwidth=2;
        panel.add(new JSeparator(), c);

        // ===== Gesamtbetrag =====
        lblGesamt = new JLabel("Gesamtbetrag: 0,00 €", SwingConstants.CENTER);
        lblGesamt.setFont(boldFont);
        c.gridx=0; c.gridy=9; c.gridwidth=2;
        panel.add(lblGesamt, c);

        // ===== Leerzeile =====
        c.gridy=10;
        panel.add(Box.createVerticalStrut(10), c);

        // ===== Buttons =====
        c.gridy=11; c.gridwidth=1; c.gridx=0;
        btnBerechnen = createButton("Berechnen", font, new Color(0,120,215));
        btnBerechnen.addActionListener(e -> berechne());
        panel.add(btnBerechnen, c);

        btnSaetze = createButton("Essenssätze ändern", font, new Color(178,34,34));
        btnSaetze.addActionListener(e -> {
            if (ladeOderFrageGeldsaetze(true)) updateRateLabels();
        });
        c.gridx=1;
        panel.add(btnSaetze, c);

        // ===== Code by Philipp =====
        c.gridx=0; c.gridy=12; c.gridwidth=2;
        JLabel copy = new JLabel("Coded by Philipp N.", SwingConstants.CENTER);
        copy.setFont(font);
        panel.add(copy, c);

        add(panel);
        setVisible(true);
    }

    private JPanel createInputPanel(Font f) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT,5,0));
        p.setBackground(new Color(245,249,255));
        return p;
    }

    /** Hilfsmethode für Buttons */
    private JButton createButton(String text, Font f, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(f);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        return btn;
    }

    private boolean ladeOderFrageGeldsaetze(boolean immer) {
        if (satzDatei.exists() && !immer) {
            try (BufferedReader br = new BufferedReader(new FileReader(satzDatei))) {
                morgensSatz = parseDouble(br.readLine());
                mittagsSatz = parseDouble(br.readLine());
                abendsSatz  = parseDouble(br.readLine());
                int r = JOptionPane.showConfirmDialog(this,
                        String.format("Gespeicherte Geldsätze:%nMorgens: %.2f €%nMittags: %.2f €%nAbends:  %.2f €%n%nStimmen diese noch?",
                                morgensSatz, mittagsSatz, abendsSatz),
                        "Geldsätze bestätigen", JOptionPane.YES_NO_OPTION);
                if (r == JOptionPane.YES_OPTION) {
                    return true;
                } else if (r == JOptionPane.NO_OPTION) {
                    return ladeOderFrageGeldsaetze(true);
                } else {
                    return false;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Fehler beim Lesen – neue Eingabe nötig.",
                        "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
        JTextField m = new JTextField(), mi = new JTextField(), a = new JTextField();
        JPanel p = new JPanel(new GridLayout(3,2,5,5));
        p.add(new JLabel("Betrag morgens (€):")); p.add(m);
        p.add(new JLabel("Betrag mittags (€):"));  p.add(mi);
        p.add(new JLabel("Betrag abends (€):"));   p.add(a);
        if (JOptionPane.showConfirmDialog(this,p,
                "Essenssätze eingeben", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)
                != JOptionPane.OK_OPTION) return false;
        try (PrintWriter pw = new PrintWriter(new FileWriter(satzDatei))) {
            morgensSatz = parseDouble(m.getText()); pw.println(morgensSatz);
            mittagsSatz = parseDouble(mi.getText());  pw.println(mittagsSatz);
            abendsSatz  = parseDouble(a.getText());   pw.println(abendsSatz);
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Ungültige Eingabe.",
                    "Fehler", JOptionPane.ERROR_MESSAGE);
            return ladeOderFrageGeldsaetze(true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Konnte nicht speichern.",
                    "Warnung", JOptionPane.WARNING_MESSAGE);
            return true;
        }
    }

    private void updateRateLabels() {
        lblMorgensRate.setText("("+format(morgensSatz)+" €)");
        lblMittagsRate.setText("("+format(mittagsSatz)+" €)");
        lblAbendsRate.setText("("+format(abendsSatz)+" €)");
    }

    private void berechne() {
        try {
            int m  = Integer.parseInt(tfMorgens.getText().trim());
            int mi = Integer.parseInt(tfMittags.getText().trim());
            int a  = Integer.parseInt(tfAbends.getText().trim());
            if (m<0||m>7||mi<0||mi>7||a<0||a>7) throw new NumberFormatException();
            double gesamt = m*morgensSatz + mi*mittagsSatz + a*abendsSatz;
            lblGesamt.setText("Gesamtbetrag: "+format(gesamt)+" €");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Bitte ganze Zahlen zwischen 0 und 7 eingeben.",
                    "Eingabefehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double parseDouble(String s) {
        return Double.parseDouble(s.trim().replace(',','.'));
    }

    private String format(double b) {
        return String.format("%.2f",b).replace('.',',');
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EssensgeldGUI::new);
    }
}
