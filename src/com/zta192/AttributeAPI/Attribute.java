package com.zta192.AttributeAPI;

import java.util.UUID;

import net.minecraft.server.v1_6_R3.NBTTagCompound;

public class Attribute {
	
	public static enum Operation {
		
		ADD(0), MULTIPLY_PERCENTAGE(1), ADD_PERCENTAGE(2);
		
		private int operationID;
		
		Operation(int operationID) {
			this.operationID = operationID;
		}
		
		public static Operation getOperationFromID(int id) {
			for(Operation operation : Operation.values()) {
				if(operation.operationID == id) return operation;
			}
			return null;
		}
		
	}
	
	private String attributeName;
	private String name;
	private double amount;
	private Operation operation;
	private UUID uuid;
	
	public Attribute(String attributeName, String name, double amount, Operation operation, UUID uuid) {
		this.attributeName = attributeName;
		this.name = name;
		this.amount = amount;
		this.operation = operation;
		this.uuid = uuid;
	}
	
	public Attribute(String attributeName, String name, double amount, Operation operation) {
		this(attributeName, name, amount, operation, UUID.randomUUID());
	}
	
	public Attribute(NBTTagCompound compound) {
		this.attributeName = compound.getString("AttributeName");
		this.name = compound.getString("Name");
		this.amount = compound.getDouble("Amount");
		this.operation = Operation.getOperationFromID(compound.getInt("Operation"));
		this.uuid = new UUID(compound.getLong("UUIDMost"), compound.getLong("UUIDLeast"));
	}
	
	public String getAttributeName() {
		return attributeName;
	}
	
	public String getName() {
		return name;
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
		NBTTagCompound attribute = new NBTTagCompound();
		attribute.setString("AttributeName", attributeName);
		attribute.setString("Name", name);
		attribute.setDouble("Amount", amount);
		attribute.setInt("Operation", operation.operationID);
		attribute.setLong("UUIDMost", uuid.getMostSignificantBits());
		attribute.setLong("UUIDLeast", uuid.getLeastSignificantBits());
		return attribute;
	}
	
}
