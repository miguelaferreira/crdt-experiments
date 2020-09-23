package com.github.miguelaferreira.crdt;

import java.util.Set;

import io.dmitryivanov.crdt.LWWSet;

public class ColumnCrdt {
    private LWWSet<Cell> cells;

    public ColumnCrdt(long timestamp, final Set<Cell> cells) {
        this.cells = new LWWSet<>();
        cells.forEach(cell -> this.cells.add(wrap(timestamp, cell)));
    }

    private ColumnCrdt(LWWSet<Cell> cells) {
        this.cells = new LWWSet<Cell>().merge(cells);
    }

    private LWWSet.ElementState<Cell> wrap(final long timestamp, final Cell cell) {
        return new LWWSet.ElementState<>(timestamp, cell);
    }

    public ColumnCrdt merge(final ColumnCrdt columnCrdtUpdate) {
        cells = cells.merge(columnCrdtUpdate.cells);
        return this;
    }

    public void add(final long timestamp, final Cell cellUpdate) {
        cells.add(wrap(timestamp, cellUpdate));
    }

    public Set<Cell> lookup() {
        return cells.lookup();
    }

    @Override
    public ColumnCrdt clone() {
        return new ColumnCrdt(this.cells);
    }
}
