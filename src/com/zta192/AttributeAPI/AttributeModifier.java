package com.zta192.AttributeAPI;

import java.util.UUID;
import net.minecraft.server.v1_7_R1.NBTTagCompound;

public class AttributeModifier {
    public static enum Operation {
        ADD(0, '+'),
        MULTIPLY_PERCENTAGE(1, '*'),
        ADD_PERCENTAGE(2, '%');

        private int operationID;
        private int symbol;

        Operation(int operationID, char symbol) {
            this.operationID = operationID;
            this.symbol = symbol;
        }

        public static Operation getOperation(int id) {
            for (Operation operation : Operation.values()) {
                if (operation.operationID == id) {
                    return operation;
                }
            }
            return null;
        }

        public static Operation getOperation(char c) {
            for (Operation operation : Operation.values()) {
                if (operation.symbol == c) {
                    return operation;
                }
            }
            return null;
        }
    }
    private Attribute attribute;
    private double amount;
    private Operation operation;
    private UUID uuid;

    public AttributeModifier(Attribute attribute, double amount, Operation operation) {
        this(attribute, amount, operation, UUID.randomUUID());
    }

    public AttributeModifier(Attribute attribute, double amount, Operation operation, UUID uuid) {
        this.attribute = attribute;
        this.amount = amount;
        this.operation = operation;
        this.uuid = uuid;
    }

    public AttributeModifier(NBTTagCompound compound) {
        attribute = Attribute.getAttribute(compound.getString("Name"));
        this.amount = compound.getDouble("Amount");
        this.operation = Operation.getOperation(compound.getInt("Operation"));
        this.uuid = new UUID(compound.getLong("UUIDMost"), compound.getLong("UUIDLeast"));
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public double getAmount() {
        return amount;
    }

    public Operation getOperation() {
        return operation;
    }

    public UUID getUUID() {
        return uuid;
    }

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
