package catan.components;

/**
 * Represents a development card in the game.
 */
public class DevelopmentCard {

    /**
     * Enum for different types of development cards.
     */
    public enum CardType {
        KNIGHT,
        VICTORY_POINT,
        ROAD_BUILDING,
        YEAR_OF_PLENTY,
        MONOPOLY
    }

    private final CardType type;

    /**
     * Constructs a DevelopmentCard with a specified type.
     * @param type The type of the development card.
     */
    public DevelopmentCard(CardType type) {
        if (type == null) {
            throw new IllegalArgumentException("Card type cannot be null");
        }
        this.type = type;
    }

    /**
     * Gets the type of the development card.
     * @return The card type.
     */
    public CardType getType() {
        return type;
    }
}
