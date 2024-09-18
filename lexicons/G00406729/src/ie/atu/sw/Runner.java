package ie.atu.sw;

import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.io.*;

/**
 * 
 * @author Eoin Concannon
 * @version 1.0
 * @since 1.9
 * 
 * The Runner is the <b>main</b> class of this application.
 * It has the FileManager <i>object</i> associated with it which 
 * stores file names and file path names. The runner class itself
 * does most of the work. The user enter the file paths
 * for both a lexicon and twitter/X user. 
 * @see addFilePath
 * The program then calculates the overall sentiment 
 * from the twitter/X file.
 * @see processSentiment
 * @see displaySentiment
 *
 */
public class Runner {

	public static void main(String[] args) throws FileNotFoundException {
		// Changes font colour using ConsoleColour.java
		System.out.print(ConsoleColour.CYAN_BRIGHT);

		TweetManager tweetObj = new TweetManager();
		FileManager lexiconObj = new LexiconManager();
		Scanner scan = new Scanner(System.in);
		tweetObj.setPath("");
		lexiconObj.setPath("");
		
		// Thread not set up correctly.
		// However if I change the return type of processSentiment
		// to runnable with a null return type, the thread
		// will run and display the sentiment, and then crash.
		// Thread virtualThread = Thread.ofVirtual().unstarted(processSentiment(lexiconObj, lexiconObj));

		// Map<StringBuffer, FileManager test = new FileManager()>;

		int menuNav = 0;
		Scanner menu = new Scanner(System.in);// Scanner for menu navigation

		while (menuNav != -1) {
			// Menu displayed via method
			displayMenu();
			System.out.print(">");
			menuNav = menu.nextInt();

			if (menuNav == 1) {
				// Example user input = "C:\Users\Owner\Desktop\tweets\gen22.txt"
				// User can enter 0 to not affect the path name.
				System.out.println("Paste the file path of the twitter/X account you will be using.(Enter 0 to exit.)");
				System.out.print(">");
				String tweetPath = scan.nextLine();

				if (!"0".equals(tweetPath)) {
					try (var varVirtualThread = Executors.newVirtualThreadPerTaskExecutor()) {
						varVirtualThread.submit(() -> addFilePath(tweetPath, tweetObj));
					}
				}
			} else if (menuNav == 2) {
				// Not implemented
				System.out.println(tweetObj.getUrl());
			} else if (menuNav == 3) {
				// Example user input = "C:\Users\Owner\Desktop\lexicons\afinn.txt"
				System.out.println("Paste the file path of the lexicon you will be using.(Enter 0 to exit.)");
				System.out.print(">");
				String lexiconPath = scan.nextLine();

				if (!"0".equals(lexiconPath)) {
					try (var varVirtualThread = Executors.newVirtualThreadPerTaskExecutor()) {
						varVirtualThread.submit(() -> addFilePath(lexiconPath, lexiconObj));
					}
				}
			} else if (menuNav == 4) {
				System.out.println("Loading...");
				
				//virtualThread.start();
				try (var varVirtualThread = Executors.newVirtualThreadPerTaskExecutor()) {
					varVirtualThread.submit(() -> {
						try {
							processSentiment(tweetObj, lexiconObj);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					});
				}
			} else if (menuNav == -1) {
				System.out.println("Shutting Down...");
			} else {
				System.out.println("Invalid Input");
			}
		}

	}

	/**
	 * 
	 * Gets the user's input for the file path 
	 * and assigns it to the object.
	 * 
	 * @param path the user input
	 * @param obj the text file is assigned
	 */
	public static void addFilePath(String path, FileManager obj) {
		// Assigns the path name and text file to object's variables
		File newFile = new File(path);
		obj.setFile(newFile);
		obj.setPath(path);
	}

	/**
	 * 
	 * The sentiment is calculated.
	 * 
	 * @param tweetObj the text file value is requested
	 * @param lexiconObj the lexicon text file value is requested
	 * @throws FileNotFoundException
	 */
	public static void processSentiment(FileManager tweetObj, FileManager lexiconObj) throws FileNotFoundException {
		double sentimentGood = 0.0;
		double sentimentBad = 0.0;
		double scoreSum = 0.0;
		int wordCount = 0;
		Scanner scan = new Scanner(tweetObj.getFile());

		// [0] is the word / [1] is the sentiment score
		String[] splitLexicon = new String[2];

		while (scan.hasNext()) {
			String word = scan.next();

			try (BufferedReader br = new BufferedReader(new FileReader(lexiconObj.getFile()))) {
				String line;
				while ((line = br.readLine()) != null) {
					splitLexicon = line.split(",");

					// Looks through the lexicon to see if the word is there.
					// If the lexicon has the word, assign the lexicon value to one of the sentiment variables.
					if (word.equalsIgnoreCase(splitLexicon[0])) {
						if (Double.parseDouble(splitLexicon[1]) < 0) {
							sentimentBad = sentimentBad + Double.parseDouble(splitLexicon[1]);
							scoreSum = sentimentBad - scoreSum;
						} else {
							sentimentGood = sentimentGood + Double.parseDouble(splitLexicon[1]);
							scoreSum = sentimentGood + scoreSum;
						}
						wordCount++;
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		displaySentiment(sentimentGood, sentimentBad, wordCount, scoreSum);
		//return null;
	}

	/**
	 * 
	 * Displays the sentiment after it was calculated.
	 * 
	 * @param sentimentGood is all positive numbers
	 * @param sentimentBad is all negative numbers
	 * @param wordCount was added by 1 each time
	 * @param scoreSum will be used for calculation
	 */
	public static void displaySentiment(double sentimentGood, double sentimentBad, int wordCount, double scoreSum) {
		// Calculates the Score from Total(SfT)
		double sentimentScore = ((sentimentGood - sentimentBad) / wordCount);
		String overallSentiment = "";

		if (sentimentScore >= 1) {
			overallSentiment = "Positive";
		} else if (sentimentScore <= -1) {
			overallSentiment = "Negative";
		} else {
			overallSentiment = "Neutral";
		}

		System.out.println("Score Sum: " + scoreSum);
		//System.out.println("Score Sum: " + (sentimentBad + sentimentGood));
		System.out.println("Sentiment Score: " + sentimentScore);
		System.out.println("Overall Sentiment is " + overallSentiment);
	}
	
	/**
	 * 
	 * A simple method which displays the main menu
	 * using a few System.out.println commands
	 * 
	 */
	public static void displayMenu() {
		System.out.println("\n************************************************************");
		System.out.println("*     ATU - Dept. of Computer Science & Applied Physics    *");
		System.out.println("*                                                          *");
		System.out.println("*             Virtual Threaded Sentiment Analyser          *");
		System.out.println("*                                                          *");
		System.out.println("************************************************************");
		System.out.println("(1) Specify a Text File");
		System.out.println("(2) Specify a URL");
		System.out.println("(3) Configure Lexicons");
		System.out.println("(4) Execute, Analyse and Report");
		System.out.println("(-1) Quit");
	}
}
