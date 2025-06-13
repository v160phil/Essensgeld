import javax.swing.*;
import java.awt.*;
import java.io.*;

public class EssensgeldGUI extends JFrame {

    private double morgensSatz, mittagsSatz, abendsSatz;
    private final File satzDatei = new File("geldsaetze.txt");

    private JTextField tfMorgens, tfMittags, tfAbends;
    private JLabel lblMorgensTeil, lblMittagsTeil, lblAbendsTeil, lblGesamt;

    public EssensgeldGUI() {
        baueGUI();
        SwingUtilities.invokeLater(() -> {
            if (!ladeOderFrageGeldsaetze(false)) {
                dispose();
                System.exit(0);
            }
        });
    }

    private void baueGUI() {
        setTitle("Essensgeld Rechner");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(430, 330);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 249, 255));

        JPanel grid = new JPanel(new GridLayout(6, 2, 8, 4));
        grid.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        grid.setBackground(getContentPane().getBackground());

        grid.add(new JLabel("Morgens:"));
        tfMorgens = new JTextField();
        grid.add(tfMorgens);
        lblMorgensTeil = new JLabel("(0 €)");
        grid.add(lblMorgensTeil);
        grid.add(new JLabel());

        grid.add(new JLabel("Mittags:"));
        tfMittags = new JTextField();
        grid.add(tfMittags);
        lblMittagsTeil = new JLabel("(0 €)");
        grid.add(lblMittagsTeil);
        grid.add(new JLabel());

        grid.add(new JLabel("Abends:"));
        tfAbends = new JTextField();
        grid.add(tfAbends);
        lblAbendsTeil = new JLabel("(0 €)");
        grid.add(lblAbendsTeil);
        grid.add(new JLabel());

        add(grid, BorderLayout.CENTER);

        lblGesamt = new JLabel(" ");
        lblGesamt.setHorizontalAlignment(SwingConstants.CENTER);
        lblGesamt.setFont(new Font("Segoe UI", Font.BOLD, 16));
        add(lblGesamt, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        btnPanel.setBackground(getContentPane().getBackground());

        JButton btnBerechnen = new JButton("Berechnen");
        btnBerechnen.setBackground(new Color(0, 120, 215));
        btnBerechnen.setForeground(Color.WHITE);
        btnBerechnen.addActionListener(e -> berechne());

        JButton btnSaetze = new JButton("Essenssätze ändern");
        btnSaetze.setBackground(new Color(178, 34, 34));
        btnSaetze.setForeground(Color.WHITE);
        btnSaetze.addActionListener(e -> {
            if (ladeOderFrageGeldsaetze(true)) {
                JOptionPane.showMessageDialog(this, "Essenssätze wurden aktualisiert.");
            }
        });

        btnPanel.add(btnBerechnen);
        btnPanel.add(btnSaetze);

        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(getContentPane().getBackground());
        south.add(btnPanel, BorderLayout.CENTER);

        JLabel copy = new JLabel("Code by Philipp");
        copy.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 0));
        south.add(copy, BorderLayout.WEST);

        add(south, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void berechne() {
        try {
            int m = Integer.parseInt(tfMorgens.getText().trim());
            int mi = Integer.parseInt(tfMittags.getText().trim());
            int a = Integer.parseInt(tfAbends.getText().trim());

            if (m < 0 || m > 7 || mi < 0 || mi > 7 || a < 0 || a > 7)
                throw new NumberFormatException();

            double betragMorgens = m * morgensSatz;
            double betragMittags = mi * mittagsSatz;
            double betragAbends  = a * abendsSatz;
            double gesamt        = betragMorgens + betragMittags + betragAbends;

            lblMorgensTeil.setText(String.format("(%.2f €)", betragMorgens));
            lblMittagsTeil.setText(String.format("(%.2f €)", betragMittags));
            lblAbendsTeil.setText(String.format("(%.2f €)", betragAbends));
            lblGesamt.setText(String.format("Gesamtbetrag: %.2f €", gesamt));

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Bitte ganze Zahlen zwischen 0 und 7 eingeben.",
                    "Eingabefehler",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean ladeOderFrageGeldsaetze(boolean immerFragen) {
        if (satzDatei.exists() && !immerFragen) {
            try (BufferedReader br = new BufferedReader(new FileReader(satzDatei))) {
                morgensSatz = Double.parseDouble(br.readLine());
                mittagsSatz = Double.parseDouble(br.readLine());
                abendsSatz  = Double.parseDouble(br.readLine());

                String msg = String.format(
                        "Gespeicherte Geldsätze:\n" +
                                "Morgens: %.2f €\n" +
                                "Mittags: %.2f €\n" +
                                "Abends:  %.2f €\n\n" +
                                "Stimmen diese noch?",
                        morgensSatz, mittagsSatz, abendsSatz);

                int res = JOptionPane.showConfirmDialog(this, msg,
                        "Geldsätze bestätigen",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (res == JOptionPane.YES_OPTION)
                    return true;

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Fehler beim Lesen der Datei – neue Eingabe nötig.");
            }
        }

        JTextField tfMorgen = new JTextField();
        JTextField tfMittag = new JTextField();
        JTextField tfAbend = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Neuer Betrag für morgens (€):"));
        panel.add(tfMorgen);
        panel.add(new JLabel("Neuer Betrag für mittags (€):"));
        panel.add(tfMittag);
        panel.add(new JLabel("Neuer Betrag für abends (€):"));
        panel.add(tfAbend);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Essenssätze eingeben", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (result != JOptionPane.OK_OPTION) return false;

        try {
            morgensSatz = Double.parseDouble(tfMorgen.getText().trim().replace(",", "."));
            mittagsSatz = Double.parseDouble(tfMittag.getText().trim().replace(",", "."));
            abendsSatz = Double.parseDouble(tfAbend.getText().trim().replace(",", "."));

            speichereSaetze();
            return true;

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Ungültige Eingabe – bitte gültige Zahlen verwenden.",
                    "Fehler", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void speichereSaetze() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(satzDatei))) {
            pw.println(morgensSatz);
            pw.println(mittagsSatz);
            pw.println(abendsSatz);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Konnte Geldsätze nicht speichern.",
                    "Warnung",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EssensgeldGUI::new);
    }
}
