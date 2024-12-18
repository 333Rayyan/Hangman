import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import javax.swing.*;

public class Main {
    private static ArrayList<String> targetWords = new ArrayList<>();
    private static String currentWord;
    private static String currentGuess;
    private static int guessesRemaining;
    private static ArrayList<String> incorrectGuesses = new ArrayList<>();
    private static Timer timer;
    private static int timeLeft; 
    private static JFrame frame;

    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(new File("wordlist.txt"));
        while (in.hasNext()) {
            targetWords.add(in.next());
        }
        in.close();

        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    public static String getWord() {
        Random r = new Random();
        String word = targetWords.get(r.nextInt(targetWords.size()));
        System.out.println("The target word is: " + word);
        return word;
    }

    private static void createAndShowGUI() {
        frame = new JFrame("Hangman");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        currentWord = getWord();
        currentGuess = new String(new char[currentWord.length()]).replace("\0", "_");
        guessesRemaining = 7;
        incorrectGuesses.clear();
        timeLeft = 120;

        JLabel guessLabel = new JLabel("Guess:");
        JTextField guessField = new JTextField(1);
        JButton submitButton = new JButton("Submit Guess");
        JLabel wordLabel = new JLabel(currentGuess.replaceAll(".(?!$)", "$0 "));
        wordLabel.setFont(new Font("Arial", Font.PLAIN, 44));
        wordLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JLabel guessesLabel = new JLabel("Guesses remaining: " + guessesRemaining);
        JLabel incorrectGuessesLabel = new JLabel("Incorrect guesses: " + String.join(", ", incorrectGuesses));
        JLabel timerLabel = new JLabel("Time left: " + formatTime(timeLeft));

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String guess = guessField.getText().toLowerCase();
                guessField.setText("");

                if (guess.length() != 1) {
                    JOptionPane.showMessageDialog(frame, "Please enter a single letter.");
                    return;
                }

                if (currentWord.contains(guess)) {
                    for (int i = 0; i < currentWord.length(); i++) {
                        if (currentWord.charAt(i) == guess.charAt(0)) {
                            currentGuess = currentGuess.substring(0, i) + guess + currentGuess.substring(i + 1);
                        }
                    }
                } else {
                    guessesRemaining--;
                    incorrectGuesses.add(guess);
                }

                wordLabel.setText(currentGuess.replaceAll(".(?!$)", "$0 "));
                guessesLabel.setText("Guesses remaining: " + guessesRemaining);
                incorrectGuessesLabel.setText("Incorrect guesses: " + String.join(", ", incorrectGuesses));

                if (currentGuess.equals(currentWord)) {
                    JOptionPane.showMessageDialog(frame, "You won! The word was: " + currentWord);
                    askToPlayAgain();
                } else if (guessesRemaining == 0) {
                    JOptionPane.showMessageDialog(frame, "You lost! The word was: " + currentWord);
                    askToPlayAgain();
                }
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());
        inputPanel.add(guessLabel);
        inputPanel.add(guessField);
        inputPanel.add(submitButton);

        frame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        frame.add(timerLabel, c);
        c.gridy++;
        frame.add(inputPanel, c);
        c.gridy++;
        frame.add(wordLabel, c);
        c.gridy++;
        frame.add(guessesLabel, c);
        c.gridy++;
        frame.add(incorrectGuessesLabel, c);
        frame.setSize(400, 300);
        frame.setVisible(true);

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                timerLabel.setText("Time left: " + formatTime(timeLeft));
                if (timeLeft <= 0) {
                    timer.stop();
                    JOptionPane.showMessageDialog(frame, "Time's up! The word was: " + currentWord);
                    askToPlayAgain();
                }
            }

        });

        timer.start();
    }

    private static String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);

    }

    private static void askToPlayAgain() {
        int choice = JOptionPane.showConfirmDialog(frame, "Do you want to play again?", "Game Over", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            frame.dispose(); 
            createAndShowGUI(); 
        } else {
            frame.dispose(); 
        }
    }
}