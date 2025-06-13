import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class EssensgeldGUI extends JFrame {

    private JTextField morgensAnzahlFeld, mittagsAnzahlFeld, abendsAnzahlFeld;
    private JLabel ergebnisLabel;
    private double morgensSatz, mittagsSatz, abendsSatz;
    private final File satzDatei = new File("geldsaetze.txt");

    public EssensgeldGUI() {
        erzeugeGUI(); // GUI zuerst
        SwingUtilities.invokeLater(() -> {
            boolean geladen = ladeOderFrageGeldsaetze(false);
            if (!geladen) {
                dispose();
                System.exit(0);
            }
        });
    }

    private boolean ladeOderFrageGeldsaetze(boolean immerFragen) {
        if (satzDatei.exists() && !immerFragen) {
            try (BufferedReader reader = new BufferedReader(new FileReader(satzDatei))) {
                morgensSatz = Double.parseDouble(reader.readLine());
                mittagsSatz = Double.parseDouble(reader.readLine());
                abendsSatz = Double.parseDouble(reader.readLine());

                String message = String.format(
                        "Gespeicherte Geldsätze:\nMorgens: %.2f €\nMittags: %.2f €\nAbends: %.2f €\n\nStimmen diese noch?",
                        morgensSatz, mittagsSatz, abendsSatz
                );

                int antwort = JOptionPane.showConfirmDialog(this, message, "Geldsätze bestätigen", JOptionPane.YES_NO_OPTION);
                if (antwort == JOptionPane.YES_OPTION) return true;

            } catch (IOException | NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Fehler beim Lesen der Datei. Neue Eingabe erforderlich.");
            }
        }

        Double neuerMorgens = frageSatz("Neuer Betrag für morgens (€):");
        if (neuerMorgens == null) return false;

        Double neuerMittags = frageSatz("Neuer Betrag für mittags (€):");
        if (neuerMittags == null) return false;

        Double neuerAbends = frageSatz("Neuer Betrag für abends (€):");
        if (neuerAbends == null) return false;

        morgensSatz = neuerMorgens;
        mittagsSatz = neuerMittags;
        abendsSatz = neuerAbends;
        speichereGeldsaetze();
        return true;
    }

    private Double frageSatz(String text) {
        while (true) {
            String eingabe = JOptionPane.showInputDialog(this, text);
            if (eingabe == null) return null;
            try {
                return Double.parseDouble(eingabe);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Bitte eine gültige Zahl eingeben.");
            }
        }
    }

    private void speichereGeldsaetze() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(satzDatei))) {
            writer.println(morgensSatz);
            writer.println(mittagsSatz);
            writer.println(abendsSatz);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Fehler beim Speichern der Geldsätze.");
        }
    }

    private void erzeugeGUI() {
        setTitle("Essensgeld Rechner");
        setSize(450, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 250, 255));

        JPanel eingabePanel = new JPanel(new GridLayout(3, 2, 10, 10));
        eingabePanel.setBorder(BorderFactory.createTitledBorder("Wie oft gegessen? (0–7)"));
        eingabePanel.setBackground(new Color(245, 250, 255));

        eingabePanel.add(new JLabel("Morgens:"));
        morgensAnzahlFeld = new JTextField();
        eingabePanel.add(morgensAnzahlFeld);

        eingabePanel.add(new JLabel("Mittags:"));
        mittagsAnzahlFeld = new JTextField();
        eingabePanel.add(mittagsAnzahlFeld);

        eingabePanel.add(new JLabel("Abends:"));
        abendsAnzahlFeld = new JTextField();
        eingabePanel.add(abendsAnzahlFeld);

        add(eingabePanel, BorderLayout.CENTER);

        JPanel untenPanel = new JPanel(new BorderLayout(8, 8));
        untenPanel.setBackground(new Color(245, 250, 255));
        untenPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        ergebnisLabel = new JLabel("Gesamtbetrag: ", SwingConstants.CENTER);
        ergebnisLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ergebnisLabel.setForeground(new Color(0, 100, 160));
        untenPanel.add(ergebnisLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        buttonPanel.setBackground(new Color(245, 250, 255));

        JButton berechnenButton = new JButton("Berechnen");
        berechnenButton.setBackground(new Color(0, 120, 215));
        berechnenButton.setForeground(Color.WHITE);
        berechnenButton.setFocusPainted(false);
        berechnenButton.addActionListener(e -> berechneEssensgeld());
        buttonPanel.add(berechnenButton);

        JButton saetzeAendernButton = new JButton("Essenssätze ändern");
        saetzeAendernButton.setBackground(new Color(180, 80, 80));
        saetzeAendernButton.setForeground(Color.WHITE);
        saetzeAendernButton.setFocusPainted(false);
        saetzeAendernButton.addActionListener(e -> {
            boolean geaendert = ladeOderFrageGeldsaetze(true);
            if (geaendert) {
                JOptionPane.showMessageDialog(this, "Sätze wurden aktualisiert.");
            }
        });
        buttonPanel.add(saetzeAendernButton);

        untenPanel.add(buttonPanel, BorderLayout.CENTER);

        JLabel copyright = new JLabel("Code by Philipp");
        copyright.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        untenPanel.add(copyright, BorderLayout.SOUTH);

        add(untenPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void berechneEssensgeld() {
        try {
            int morgensAnz = Integer.parseInt(morgensAnzahlFeld.getText());
            int mittagsAnz = Integer.parseInt(mittagsAnzahlFeld.getText());
            int abendsAnz = Integer.parseInt(abendsAnzahlFeld.getText());

            if (morgensAnz < 0 || morgensAnz > 7 || mittagsAnz < 0 || mittagsAnz > 7 || abendsAnz < 0 || abendsAnz > 7) {
                ergebnisLabel.setText("lAnzahl muss zwischen 0 und 7 liegen!");
                return;
            }

            double gesamt = (morgensSatz * morgensAnz) + (mittagsSatz * mittagsAnz) + (abendsSatz * abendsAnz);
            ergebnisLabel.setText(String.format("Gesamtbetrag: %.2f €", gesamt));

        } catch (NumberFormatException ex) {
            ergebnisLabel.setText("Bitte gültige Zahlen eingeben!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EssensgeldGUI::new);
    }
}
