package com.zta192.AttributeAPI;

import java.util.UUID;
import net.minecraft.server.v1_7_R1.NBTTagCompound;

/**
 * An Attribute modifier may be applied to an ItemStack
 * When an Entity is holding/wearing an ItemStack, all modifiers apply to them
 *
 * @author Zach Abney
 */
public class AttributeModifier {
    /**
     * An Operation determines how the attribute value should be modified
     */
    public static enum Operation {
        ADD(0, '+'),
        MULTIPLY_PERCENTAGE(1, '*'),
        ADD_PERCENTAGE(2, '%');

        private int operationID;
        private int symbol;

        /**
         * Constructs a new Operation
         *
         * @param operationID The Minecraft id for the operation
         * @param symbol The (easier to remember) symbol to represent the operation
         */
        private Operation(int operationID, char symbol) {
            this.operationID = operationID;
            this.symbol = symbol;
        }

        /**
         * Returns the Operation with the given id
         *
         * @param id The given id
         * @return The Operation or null if the id is invalid
         */
        public static Operation getOperation(int id) {
            for (Operation operation : Operation.values()) {
                if (operation.operationID == id) {
                    return operation;
                }
            }
            return null;
        }

        /**
         * Returns the Operation with the given symbol
         *
         * @param c The given symbol
         * @return The Operation or null if the symbol is invalid
         */
        public static Operation getOperation(char c) {
            for (Operation operation : Operation.values()) {
                if (operation.symbol == c) {
                    return operation;
                }
            }
            return null;
        }
    }

    private Attribute attribute; //The Attribute to be modified
    private double amount; //The amount to modify the Attribute by (may be negative)
    private Operation operation; //The Operation to apply on the Attribute
    private UUID uuid;

    /**
     * Constructs a new AttributeModifier
     *
     * @param attribute The Attribute to be modified
     * @param amount The amount to modify the Attribute by (may be negative)
     * @param operation The Operation to apply on the Attribute
     */
    public AttributeModifier(Attribute attribute, double amount, Operation operation) {
        this(attribute, amount, operation, UUID.randomUUID());
    }

    /**
     * Constructs a new AttributeModifier
     *
     * @param attribute The Attribute to be modified
     * @param amount The amount to modify the Attribute by (may be negative)
     * @param operation The Operation to apply on the Attribute
     * @param uuid The unique id for the AttributeModifier
     */
    public AttributeModifier(Attribute attribute, double amount, Operation operation, UUID uuid) {
        this.attribute = attribute;
        this.amount = amount;
        this.operation = operation;
        this.uuid = uuid;
    }

    /**
     * Constructs a new AttributeModifier from an existing NBTTagCompound
     *
     * @param compound The existing NBTTagCompound which represents an AttributeModifier
     */
    public AttributeModifier(NBTTagCompound compound) {
        attribute = Attribute.getAttribute(compound.getString("Name"));
        this.amount = compound.getDouble("Amount");
        this.operation = Operation.getOperation(compound.getInt("Operation"));
        this.uuid = new UUID(compound.getLong("UUIDMost"), compound.getLong("UUIDLeast"));
    }

    /**
     * Returns the Attribute to be modified
     *
     * @return The Attribute to be modified
     */
    public Attribute getAttribute() {
        return attribute;
    }

    /**
     * Returns the amount to modify the Attribute by (may be negative)
     *
     * @return The amount to modify the Attribute by (may be negative)
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Returns the Operation to apply on the Attribute
     *
     * @return The Operation to apply on the Attribute
     */
    public Operation getOperation() {
        return operation;
    }

    /**
     * Returns the unique id for the AttributeModifier
     *
     * @return The unique id for the AttributeModifier
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Builds the AttributeModifier into an NBTTagCompound
     *
     * @return NBTTagCompound representation of the AttributeModifier
     */
    public NBTTagCompound build() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("AttributeName", attribute.getAttributeName());
        tag.setString("Name", attribute.getName());
        tag.setDouble("Amount", amount);
        tag.setInt("Operation", operation.operationID);
        tag.setLong("UUIDMost", uuid.getMostSignificantBits());
        tag.setLong("UUIDLeast", uuid.getLeastSignificantBits());
        return tag;
    }
}
