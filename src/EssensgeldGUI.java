import javax.swing.*;
import java.awt.*;
import java.io.*;

public class EssensgeldGUI extends JFrame {
    private double morgensSatz, mittagsSatz, abendsSatz;
    private final File satzDatei = new File("geldsaetze.txt");

    private JTextField tfMorgens, tfMittags, tfAbends;
    private JLabel lblMorgensSatz, lblMittagsSatz, lblAbendsSatz, lblGesamt;

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
        setSize(400, 290);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.setBackground(Color.WHITE);
        setContentPane(content);

        JPanel eingabePanel = new JPanel(new GridLayout(3, 3, 4, 4));
        eingabePanel.setBackground(Color.WHITE);

        tfMorgens = new JTextField();
        tfMittags = new JTextField();
        tfAbends = new JTextField();
        Dimension tfSize = new Dimension(60, 25);
        tfMorgens.setPreferredSize(tfSize);
        tfMittags.setPreferredSize(tfSize);
        tfAbends.setPreferredSize(tfSize);

        eingabePanel.add(new JLabel("Morgens:"));
        eingabePanel.add(tfMorgens);
        lblMorgensSatz = new JLabel("(0,00 €)");
        eingabePanel.add(lblMorgensSatz);

        eingabePanel.add(new JLabel("Mittags:"));
        eingabePanel.add(tfMittags);
        lblMittagsSatz = new JLabel("(0,00 €)");
        eingabePanel.add(lblMittagsSatz);

        eingabePanel.add(new JLabel("Abends:"));
        eingabePanel.add(tfAbends);
        lblAbendsSatz = new JLabel("(0,00 €)");
        eingabePanel.add(lblAbendsSatz);

        content.add(eingabePanel, BorderLayout.NORTH);

        lblGesamt = new JLabel(" ");
        lblGesamt.setHorizontalAlignment(SwingConstants.CENTER);
        lblGesamt.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(lblGesamt, BorderLayout.CENTER);
        content.add(centerPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        btnPanel.setBackground(Color.WHITE);

        JButton btnBerechnen = new JButton("Berechnen");
        btnBerechnen.setBackground(new Color(0, 120, 215));
        btnBerechnen.setForeground(Color.WHITE);
        btnBerechnen.setFocusPainted(false);

        JButton btnSaetze = new JButton("Essenssätze ändern");
        btnSaetze.setBackground(new Color(178, 34, 34));
        btnSaetze.setForeground(Color.WHITE);
        btnSaetze.setFocusPainted(false);

        btnPanel.add(btnBerechnen);
        btnPanel.add(btnSaetze);
        content.add(btnPanel, BorderLayout.SOUTH);

        // Aktionen
        btnBerechnen.addActionListener(e -> berechne());
        btnSaetze.addActionListener(e -> {
            if (ladeOderFrageGeldsaetze(true)) {
                JOptionPane.showMessageDialog(this, "Essenssätze wurden aktualisiert.");
            }
        });

        setVisible(true);
    }

    private void berechne() {
        try {
            int m = Integer.parseInt(tfMorgens.getText().trim());
            int mi = Integer.parseInt(tfMittags.getText().trim());
            int a = Integer.parseInt(tfAbends.getText().trim());

            if (m < 0 || m > 7 || mi < 0 || mi > 7 || a < 0 || a > 7)
                throw new NumberFormatException();

            double betrag = m * morgensSatz + mi * mittagsSatz + a * abendsSatz;
            lblGesamt.setText(String.format("Gesamtbetrag: %.2f €", betrag));

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
                abendsSatz = Double.parseDouble(br.readLine());

                int res = JOptionPane.showConfirmDialog(this,
                        String.format("Gespeicherte Geldsätze:\nMorgens: %.2f €\nMittags: %.2f €\nAbends: %.2f €\n\nStimmen diese noch?",
                                morgensSatz, mittagsSatz, abendsSatz),
                        "Geldsätze bestätigen",
                        JOptionPane.YES_NO_OPTION);

                if (res == JOptionPane.YES_OPTION) {
                    zeigeGeldsaetzeLabels();
                    return true;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Fehler beim Laden – neue Eingabe nötig.");
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
            morgensSatz = Double.parseDouble(tfMorgen.getText().replace(",", ".").trim());
            mittagsSatz = Double.parseDouble(tfMittag.getText().replace(",", ".").trim());
            abendsSatz = Double.parseDouble(tfAbend.getText().replace(",", ".").trim());

            speichereSaetze();
            zeigeGeldsaetzeLabels();
            return true;

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Bitte gültige Zahlen eingeben.", "Fehler", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void speichereSaetze() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(satzDatei))) {
            pw.println(morgensSatz);
            pw.println(mittagsSatz);
            pw.println(abendsSatz);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Fehler beim Speichern der Geldsätze.");
        }
    }

    private void zeigeGeldsaetzeLabels() {
        lblMorgensSatz.setText(String.format("(%.2f €)", morgensSatz));
        lblMittagsSatz.setText(String.format("(%.2f €)", mittagsSatz));
        lblAbendsSatz.setText(String.format("(%.2f €)", abendsSatz));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EssensgeldGUI::new);
    }
}
