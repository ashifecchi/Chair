import java.awt.*;
import java.util.Objects;

/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Town {
    // instance variables
    private Hunter hunter;
    private Shop shop;
    public Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    public static String treasure;
    private boolean isSearched;
    private boolean isDug;
    private boolean easy;
    private OutputWindow window;

    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness, boolean easy, OutputWindow window) {
        this.window = window;
        this.shop = shop;
        this.terrain = getNewTerrain();

        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;

        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);

        double rndForTown = Math.random();
        if (rndForTown < .2) {
            treasure = "dust";
        } else if (rndForTown < .4) {
            treasure = "trophy";
        } else if (rndForTown < .6) {
            treasure = "crown";
        } else {
            treasure = "gem";
        }
    }

    public void getLatestNews() {
        window.addTextToWindow(printMessage, Color.red);
    }

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";

        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown && easy) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + item + " to cross the " + terrain.getTerrainName() + ".";

            return true;
        } else if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + item + " to cross the " + terrain.getTerrainName() + ".";
            if (checkItemBreak()) {
                hunter.removeItemFromKit(item);
                printMessage += "\nUnfortunately, your " + item + " got stolen by a the holly jolly Local.";
            }

            return true;
        }

        printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + terrain.getNeededItem() + ".";
        return false;
    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";

        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
        shop.enter(hunter, choice);
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        double noTroubleChance;
        if (toughTown) {
            noTroubleChance = 0.66;
        } else {
            noTroubleChance = 0.33;
        }
        if (Math.random() > noTroubleChance) {
            printMessage = "You couldn't find any trouble";
        } else {
            printMessage = "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n" ;
            int goldDiff = (int) (Math.random() * 10) + 1;
            if (TreasureHunter.SAMURAIMODE){
                printMessage +=  "君の剣がひとを殺された. また我らの日本の大勝利." ;
                printMessage += "\nGlory to the Shogun!";
                hunter.changeGold(goldDiff);
            } else if (Math.random() > noTroubleChance) {
                printMessage += "Okay, stranger! You proved yer mettle. Here, take my gold.";
                printMessage +=  "\nYou won the brawl and receive " + goldDiff + " gold." ;
                hunter.changeGold(goldDiff);
            } else {
                printMessage += "That'll teach you to go lookin' fer trouble in MY town! Now pay up!" ;
                printMessage += "\nYou lost the brawl and pay " + goldDiff + " gold." ;
                hunter.changeGold(-goldDiff);
            }
        }
    }
    public void easyLookForTrouble() {
        double noTroubleChance;
        if (toughTown) {
            noTroubleChance = 0.66;
        } else {
            noTroubleChance = 0.33;
        }
        if (Math.random() > noTroubleChance) {
            printMessage = "You couldn't find any trouble";
        } else {
            printMessage = "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n" ;
            int goldDiff = (int) (Math.random() * 10) + 1;
            if (Math.random() > .2) {
                printMessage += "Okay, stranger! You proved yer mettle. Here, take my gold.";
                printMessage += "\nYou won the brawl and receive " + goldDiff + " gold." ;
                hunter.changeGold(goldDiff);
            } else {
                printMessage +=  "That'll teach you to go lookin' fer trouble in MY town! Now pay up!";
                printMessage += "\nYou lost the brawl and pay " + goldDiff + " gold." ;
                hunter.changeGold(-goldDiff);
            }
        }
    }

    public String toString() {
        return "This nice little town is surrounded by " + Color.CYAN + terrain.getTerrainName() + Color.black + ".";
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        double rnd = Math.random();
        if (rnd < .1) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd < .2) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd < .4) {
            return new Terrain("Plains", "Horse");
        } else if (rnd < .6) {
            return new Terrain("Desert", "Water");
        } else if (rnd < .8){
            return new Terrain("Jungle", "Machete");
        } else {
            return new Terrain("Marsh", "Boot");
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        double rand = Math.random();
        return (rand < 0.5);
    }

    public void treasureTime() {
        if (isSearched) {
            window.addTextToWindow("You already searched this town", Color.green);
        } else if (hunter.hasItemInTreasure(treasure)) {
            isSearched = true;
            String stuff = "\nYou found a(n) "+treasure + "...which you already have.\nYou half-heartedly rebury it in the dry soil.";
            window.addTextToWindow(stuff, Color.blue);
        } else if ((!treasure.equals("dust") && (!isSearched))) {
            String brooo = "\nYou found a " + treasure + "!";
            window.addTextToWindow(brooo, Color.pink);
            hunter.addTreasure(treasure);
            isSearched = true;
        } else {
            window.addTextToWindow("\nDust! It irritates your eyes mockingly.\n", Color.lightGray);
        }
    }
    public void digForTreasure(Hunter hunter){
        if (!hunter.hasItemInTreasure("shovel")){
            window.addTextToWindow("\nDespite how well you think you can, you fail miserably at making any progress digging with your hands.\nYou're going to need something bigger.\n", Color.blue);
        } else {
            if (isDug) {
                window.addTextToWindow("\nThe dirt has been completely displaced.\nNo point in digging more.\n", Color.gray);
            } else {
                double chance = Math.random();
                if (chance >= .5) {
                    int goldFound = (int) (Math.random() * 20 + 1);
                    hunter.changeGold(goldFound);
                    String bro =  "\n" + goldFound + " gold to your money sack.\n";
                    window.addTextToWindow("\nYour shovel hits something... gold!\nYou happily add the ", Color.black);
                    window.addTextToWindow(bro, Color.black);
                } else {
                    window.addTextToWindow("\nYou couldn't find a single thing despite overturning the entire town.\n", Color.red);
                }
                isDug = true;
            }
        }
    }
}