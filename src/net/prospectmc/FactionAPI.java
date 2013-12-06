package net.prospectmc;

/**
 * The Faction API is made up of static methods regarding Players and their Factions
 *
 * @author Cody
 */
public class FactionAPI {
    public static Faction findFaction(String playerName) {
        for (Faction faction : Faction.values()) {
            if (faction.hasMember(playerName)) {
                return faction;
            }
        }
        FactionsListener.playerLoggedOn(playerName);
        return Faction.UNDECIDED;
    }

    public static boolean areSameFaction(String playerOne, String playerTwo) {
        return findFaction(playerOne).hasMember(playerTwo);
    }
}
