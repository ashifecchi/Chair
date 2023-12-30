import java.util.Scanner;
import java.awt.Color;
import java.util.Scanner;


/**
 * This class is responsible for controlling the Treasure Hunter game.<p>
 * It handles the display of the menu and the processing of the player's choices.<p>
 * It handles all the display based on the messages it receives from the Town object. <p>
 *
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class TreasureHunter {
    public static boolean gameOver = false;
    public static boolean SAMURAIMODE = false;
    // static variables
    private static final Scanner SCANNER = new Scanner(System.in);

    // instance variables
    private Town currentTown;
    private Hunter hunter;

    private boolean hardMode;
    private String hard;
    private OutputWindow window;

    /**
     * Constructs the Treasure Hunter game.
     */
    public TreasureHunter() {
        window = new OutputWindow(); // only want one OutputWindow object
        // these will be initialized in the play method
        currentTown = null;
        hunter = null;
        hardMode = false;
    }

    /**
     * Starts the game; this is the only public method
     */
    public void play() {
        welcomePlayer();
        enterTown();
        showMenu();
    }

    /**
     * Creates a hunter object at the beginning of the game and populates the class member variable with it.
     */
    private void welcomePlayer() {
        window.addTextToWindow("Welcome to ", Color.black);
        window.addTextToWindow("TREASURE HUNTER", Color.cyan);
        window.addTextToWindow("!", Color.black);
        window.addTextToWindow("\nGoing hunting for the big treasure, eh?", Color.black);
        window.addTextToWindow("\nWhat's your name, Hunter? ", Color.black);
        String name = SCANNER.nextLine().toLowerCase();

        // set hunter instance variable
        hunter = new Hunter(name, 10, window);

        window.addTextToWindow("\nDifficulty? (e/n/h): ", Color.red);
        hard = SCANNER.nextLine().toLowerCase();
        if (hard.equals("h")) {
            hardMode = true;
        } else if (hard.equals("test")) {
            hardMode = false;
            hunter = new Hunter(name,  154, window);
            hunter.buyItem("water", 2);
            hunter.buyItem("rope", 4);
            hunter.buyItem("machete", 6);
            hunter.buyItem("horse", 12);
            hunter.buyItem("boat", 20);
            hunter.buyItem("boot", 10);
        } else if (hard.equals("s")){
            SAMURAIMODE = true;
        } else if (hard.equals("e")) {
            hardMode = false;
            hunter = new Hunter(name,  20, window);
        }
    }

    /**
     * Creates a new town and adds the Hunter to it.
     */
    private void enterTown() {
        double markdown = 0.5;
        double toughness = 0.4;
        if (hardMode) {
            // in hard mode, you get less money back when you sell items
            markdown = 0.25;

            // and the town is "tougher"
            toughness = 0.75;
        }
        if (hard.equals("e")) {
            markdown = 1;
        }

        // note that we don't need to access the Shop object
        // outside of this method, so it isn't necessary to store it as an instance
        // variable; we can leave it as a local variable
        Shop shop = new Shop(markdown, window);

        // creating the new Town -- which we need to store as an instance
        // variable in this class, since we need to access the Town
        // object in other methods of this class
        if (hard.equals("e")) {
            currentTown = new Town(shop, toughness, true, window);
        } else {
            currentTown = new Town(shop, toughness, false, window);
        }

        // calling the hunterArrives method, which takes the Hunter
        // as a parameter; note this also could have been done in the
        // constructor for Town, but this illustrates another way to associate
        // an object with an object of a different class
        currentTown.hunterArrives(hunter);

    }

    /**
     * Displays the menu and receives the choice from the user.<p>
     * The choice is sent to the processChoice() method for parsing.<p>
     * This method will loop until the user chooses to exit.
     */
    private void showMenu() {
            String choice = "";

            while (!choice.equals("x") && !gameOver) {
                window.addTextToWindow("\n", Color.pink);
                currentTown.getLatestNews();
                window.addTextToWindow("\n***", Color.pink);
                window.addTextToWindow("\nThis nice little town is surrounded by ", Color.pink);
                window.addTextToWindow(currentTown.terrain.getTerrainName(), Color.cyan);
                window.addTextToWindow(".\n", Color.pink);
                window.addTextToWindow(hunter.hunterName, Color.black);
                window.addTextToWindow(" has ", Color.black);
                String gold = "" + hunter.getGold();
                window.addTextToWindow(gold, Color.yellow);
                window.addTextToWindow("\n(B)uy something at the shop.", Color.red);
                window.addTextToWindow("\n(S)ell something at the shop.", Color.black);
                window.addTextToWindow("\n(M)ove on to a different town.", Color.cyan);
                window.addTextToWindow("\n(L)ook for trouble!", Color.pink);
                window.addTextToWindow("\n(H)unt for treasure!", Color.black);
                window.addTextToWindow("\n(D)ig for gold.", Color.yellow);
                window.addTextToWindow("\nGive up the hunt and e(X)it.", Color.DARK_GRAY);
                window.addTextToWindow("\n", Color.LIGHT_GRAY);
                window.addTextToWindow("\nWhat's your next move? ", Color.green);
                choice = SCANNER.nextLine().toLowerCase();
                window.clear();
                processChoice(choice);
                if (Hunter.getGold() < 0) {
                    window.addTextToWindow("\nYou lost all of your gold and died, dropping nothing, because you are poor.\n[GAME OVER]", Color.red);
                    gameOver = true;
                }
            }
    }


    /**
     * Takes the choice received from the menu and calls the appropriate method to carry out the instructions.
     * @param choice The action to process.
     */
    private void processChoice(String choice) {
        if (choice.equals("b") || choice.equals("s")) {
            currentTown.enterShop(choice);
        } else if (choice.equals("m")) {
            if (currentTown.leaveTown()) {
                // This town is going away so print its news ahead of time.
                currentTown.getLatestNews();
                enterTown();
            }
        } else if (choice.equals("l")) {
            if (hard.equals("e")) {
                currentTown.easyLookForTrouble();
            } else {
                currentTown.lookForTrouble();
            }
        } else if (choice.equals("x")) {
            window.addTextToWindow("\nFare thee well, ", Color.red);
            window.addTextToWindow(hunter.getHunterName(), Color.blue );
            window.addTextToWindow("!", Color.pink);
        } else if (choice.equals("h")) {
            currentTown.treasureTime();
            boolean fullOrNah = hunter.treasureCollectionIsFull();
            if (fullOrNah) {
                window.addTextToWindow("Congratulations, you have found the last of the three treasures, you win!", Color.yellow);
                gameOver = true;
            }
        } else if (choice.equals("d")){
            currentTown.digForTreasure(hunter);
        } else {
            window.addTextToWindow("Yikes! That's an invalid option! Try again.", Color.red);
        }
    }
}