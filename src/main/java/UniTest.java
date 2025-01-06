import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import model.Card;

class UniTest {

    @Test
    void testCardDefaultConstructor() {
        Card card = new Card();
        assertNull(card.getName(), "Name should be null by default");
        assertEquals(0, card.getDamage(), "Damage should be 0 by default");
        assertNull(card.getId(), "ID should be null by default");
    }

    @Test
    void testCardParameterizedConstructor() {
        Card card = new Card("Fire Dragon", 50, "card123");
        assertEquals("Fire Dragon", card.getName(), "Name should match the input");
        assertEquals(50, card.getDamage(), "Damage should match the input");
        assertEquals("card123", card.getId(), "ID should match the input");
    }

    @Test
    void testSetName() {
        Card card = new Card();
        card.setName("Water Spirit");
        assertEquals("Water Spirit", card.getName(), "Name should be updated correctly");
    }

    @Test
    void testSetDamage() {
        Card card = new Card();
        card.setDamage(70);
        assertEquals(70, card.getDamage(), "Damage should be updated correctly");
    }

    @Test
    void testSetId() {
        Card card = new Card();
        card.setId("unique123");
        assertEquals("unique123", card.getId(), "ID should be updated correctly");
    }

    @Test
    void testSetNameNull() {
        Card card = new Card();
        card.setName(null);
        assertNull(card.getName(), "Name should be null when set to null");
    }

    @Test
    void testSetDamageNegative() {
        Card card = new Card();
        card.setDamage(-10);
        assertEquals(-10, card.getDamage(), "Damage should allow negative values if not restricted");
    }

    @Test
    void testSetIdEmptyString() {
        Card card = new Card();
        card.setId("");
        assertEquals("", card.getId(), "ID should be empty string when set to empty");
    }

    @Test
    void testCardEqualitySameProperties() {
        Card card1 = new Card("Fire Dragon", 50, "card123");
        Card card2 = new Card("Fire Dragon", 50, "card123");
        assertEquals(card1.getName(), card2.getName(), "Names should be equal");
        assertEquals(card1.getDamage(), card2.getDamage(), "Damage values should be equal");
        assertEquals(card1.getId(), card2.getId(), "IDs should be equal");
    }

    @Test
    void testCardInequalityDifferentProperties() {
        Card card1 = new Card("Fire Dragon", 50, "card123");
        Card card2 = new Card("Water Spirit", 70, "card456");
        assertNotEquals(card1.getName(), card2.getName(), "Names should not be equal");
        assertNotEquals(card1.getDamage(), card2.getDamage(), "Damage values should not be equal");
        assertNotEquals(card1.getId(), card2.getId(), "IDs should not be equal");
    }

    @Test
    void testSetNameSpecialCharacters() {
        Card card = new Card();
        card.setName("Dragon#123");
        assertEquals("Dragon#123", card.getName(), "Name should accept special characters");
    }

    @Test
    void testSetDamageUpperBoundary() {
        Card card = new Card();
        card.setDamage(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, card.getDamage(), "Damage should handle upper boundary");
    }

    @Test
    void testSetDamageLowerBoundary() {
        Card card = new Card();
        card.setDamage(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, card.getDamage(), "Damage should handle lower boundary");
    }

    @Test
    void testSetIdLongString() {
        String longId = "a".repeat(1000);
        Card card = new Card();
        card.setId(longId);
        assertEquals(longId, card.getId(), "ID should handle long strings");
    }

    @Test
    void testCardToString() {
        Card card = new Card("Fire Dragon", 50, "card123");
        String expected = "Card{name='Fire Dragon', damage=50, id='card123'}";
        assertEquals(expected, card.toString(), "toString should return correct format");
    }

    @Test
    void testCardHashCode() {
        Card card1 = new Card("Fire Dragon", 50, "card123");
        Card card2 = new Card("Fire Dragon", 50, "card123");
        assertEquals(card1.hashCode(), card2.hashCode(), "Hash codes should match for equal cards");
    }

    

    @Test
    void testSetNameUnicode() {
        Card card = new Card();
        card.setName("\u5317\u9f3e Dragon");
        assertEquals("\u5317\u9f3e Dragon", card.getName(), "Name should accept Unicode characters");
    }

    @Test
    void testCardDamageAfterSetName() {
        Card card = new Card("Fire Dragon", 50, "card123");
        card.setName("Water Spirit");
        assertEquals(50, card.getDamage(), "Changing name should not affect damage");
    }

    @Test
    void testCardIdAfterSetDamage() {
        Card card = new Card("Fire Dragon", 50, "card123");
        card.setDamage(75);
        assertEquals("card123", card.getId(), "Changing damage should not affect ID");
    }

    @Test
    void testSetNameEmptyString() {
        Card card = new Card();
        card.setName("");
        assertEquals("", card.getName(), "Name should be empty when set to an empty string");
    }
}
