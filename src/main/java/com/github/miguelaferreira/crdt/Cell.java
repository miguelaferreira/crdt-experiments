package com.github.miguelaferreira.crdt;

import java.util.Objects;

public class Cell implements Comparable<Cell> {
    private char column;
    private int row;
    private String value;

    public Cell(final char column, final int row) {
        this.column = column;
        this.row = row;
    }

    public boolean hasSameCoordinates(Cell other) {
        return column == other.column && row == other.row;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public char getColumn() {
        return column;
    }

    public void setColumn(final char column) {
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(final int row) {
        this.row = row;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Cell cell = (Cell) o;

        if (column != cell.column) {
            return false;
        }
        if (row != cell.row) {
            return false;
        }
        return Objects.equals(value, cell.value);
    }

    @Override
    public int hashCode() {
        int result = column;
        result = 31 * result + row;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("%c%d: %10s", column, row, value);
    }

    @Override
    public int compareTo(final Cell o) {
        return Integer.compare(row, o.row);
    }
}
