package com.github.miguelaferreira.crdt;

public class Message {
    private Cell cell;

    public Message(final String message) {
        this.cell = new Cell(message.charAt(0), Character.getNumericValue(message.charAt(1)));
        this.cell.setValue(message.substring(3).trim());
    }

    public Cell getCell() {
        return cell;
    }
}
