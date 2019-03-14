package com.example.justin.symbilityintersecthearthstone;


/**
 * A class to contain information on a Hearthstone card.
 */
public class Card {

    /** The name of the card. */
    private String name;

    /** The image URL of the card. */
    private String imgURL;

    /** The type of the card. */
    private String type;

    /** The player class of the card. */
    private String playerClass;

    /** The default constructor.
     *
     * @param name The name of the card
     * @param type The type of the card
     * @param playerClass The player class of the card
     * @param imgURL The image URL of the card
     */
    public Card(String name, String type, String playerClass, String imgURL) {
        this.name = name;
        this.type = type;
        this.playerClass = playerClass;
        this.imgURL = imgURL;
    }

    /**
     * Returns the name of the card.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the type of the card.
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the player class of the card.
     */
    public String getPlayerClass() {
        return playerClass;
    }

    /**
     * Returns the image URL of the card.
     */
    public String getImgURL() {
        return imgURL;
    }
}
