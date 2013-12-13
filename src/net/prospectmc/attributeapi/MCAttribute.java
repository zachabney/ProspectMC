package net.prospectmc.attributeapi;

/**
 * An MCAttribute is an Attribute which is included in vanilla Minecraft
 *
 * @author Codisimus
 */
public class MCAttribute extends Attribute {
    private String attributeName; //id that Minecraft/Bukkit uses

    /**
     * Constructs a new Attribute
     *
     * @param attributeName The id name
     * @param name The full name
     * @param alias The friendly shorthand name
     */
    public MCAttribute(String attributeName, String name, String alias) {
        super(name, alias);
        this.attributeName = attributeName;
    }

    /**
     * Returns false because this is not a custom Attribute
     *
     * @return False because this is not a custom Attribute
     */
    @Override
    public boolean isCustom() {
        return false;
    }

    /**
     * Returns the id name of the Attribute
     *
     * @return The id name of the Attribute
     */
    @Override
    public String getAttributeName() {
        return attributeName;
    }
}
