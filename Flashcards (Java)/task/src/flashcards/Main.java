package flashcards;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class Flashcards {
    private static Map<String, String> flashcards = new HashMap<>(); // Store cards and definitions
    private static Map<String, Integer> mistakes = new HashMap<>();  // Store mistakes for each card
    private static StringBuilder log = new StringBuilder();          // Store log data
    private static Scanner scanner = new Scanner(System.in);
    private static String importFile = null;
    private static String exportFile = null;

    private static void parseArguments(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if ("-import".equals(args[i]) && i + 1 < args.length) {
                importFile = args[i + 1];
            } else if ("-export".equals(args[i]) && i + 1 < args.length) {
                exportFile = args[i + 1];
            }
        }
    }

    private static void loadFlashcards(String filename) {
        try (Scanner fileScanner = new Scanner(new File(filename))) {
            int count = 0;
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    flashcards.put(parts[0].trim(), parts[1].trim());
                    count++;
                }
            }
            outputMsg(count + " cards have been loaded.");
        } catch (FileNotFoundException e) {
            outputMsg("File not found: " + filename);
        }
    }


    // Method for outputting messages and logging them
    public static void outputMsg(String msg) {
        System.out.println(msg);  // Print to console
        log.append(msg).append("\n");  // Log the message
    }

    // Method for getting user input and logging it
    public static String getUserInput() {
        String input = scanner.nextLine();  // Get input from user
        log.append("> ").append(input).append("\n");  // Log the input
        return input;
    }

    // Method to export log to a file
    public static void exportLog(String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(log.toString());
            outputMsg("The log has been saved.");
        } catch (IOException e) {
            outputMsg("An error occurred while saving the log.");
        }
    }

    // Method to add a new card
    public static void addCard() {
        outputMsg("The card:");
        String card = getUserInput();
        if (flashcards.containsKey(card)) {
            outputMsg("The card \"" + card + "\" already exists.");
            return;
        }

        outputMsg("The definition of the card:");
        String definition = getUserInput();
        if (flashcards.containsValue(definition)) {
            outputMsg("The definition \"" + definition + "\" already exists.");
            return;
        }

        flashcards.put(card, definition);
        mistakes.put(card, 0);  // Initialize mistakes for this card
        outputMsg("The pair (\"" + card + "\":\"" + definition + "\") has been added.");
    }

    // Method to ask questions based on cards
    public static void askCards() {
        outputMsg("How many times to ask?");
        int times = Integer.parseInt(getUserInput());

        Object[] keys = flashcards.keySet().toArray();
        for (int i = 0; i < times; i++) {
            String card = (String) keys[i % keys.length];
            outputMsg("Print the definition of \"" + card + "\":");
            String answer = getUserInput();

            if (flashcards.get(card).equals(answer)) {
                outputMsg("Correct answer.");
            } else {
                mistakes.put(card, mistakes.get(card) + 1);
                String correctDefinition = flashcards.get(card);
                String altTerm = findTermByDefinition(answer);
                if (altTerm != null) {
                    outputMsg("Wrong. The right answer is \"" + correctDefinition + "\", but your definition is correct for \"" + altTerm + "\".");
                } else {
                    outputMsg("Wrong. The right answer is \"" + correctDefinition + "\".");
                }
            }
        }
    }

    // Method to find a term by definition
    public static String findTermByDefinition(String definition) {
        for (Map.Entry<String, String> entry : flashcards.entrySet()) {
            if (entry.getValue().equals(definition)) {
                return entry.getKey();
            }
        }
        return null;
    }

    // Method to handle user commands
    public static void handleUserAction(String action) {
        switch (action) {
            case "add":
                addCard();
                break;
            case "ask":
                askCards();
                break;
            case "log":
                outputMsg("File name:");
                String filename = getUserInput();
                exportLog(filename);
                break;
            case "exit":
                outputMsg("Bye bye!");
                System.exit(0);
                break;
            default:
                outputMsg("Unknown command: " + action);
        }
    }

    // Main method
    public static void main(String[] args) {
        parseArguments(args);

        if (importFile != null) {
            loadFlashcards(importFile);
        }

        while (true) {
            outputMsg("Input the action (add, ask, log, exit):");
            String action = getUserInput();
            handleUserAction(action);

        }
    }
}
