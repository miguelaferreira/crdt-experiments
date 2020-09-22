package com.github.miguelaferreira.crdt;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sheet {
    private static final Logger log = LoggerFactory.getLogger(Sheet.class);

    private Map<Character, Set<Cell>> columns;

    public Sheet() {
        this.columns = new HashMap<>(Map.of(
                'A', Set.of(new Cell('A', 1), new Cell('A', 2)),
                'B', Set.of(new Cell('B', 1), new Cell('B', 2))
        ));
    }

    @Override
    public String toString() {
        return columns.entrySet().stream()
                      .sorted(Map.Entry.comparingByKey())
                      .map(Map.Entry::getValue)
                      .map(col -> col.stream().map(Cell::toString))
                      .map(col -> col.collect(Collectors.joining(" | ", "| ", " |")))
                      .collect(Collectors.joining("\n"));
    }

    public void update(Cell cellUpdate) {
        final char column = cellUpdate.getColumn();
        if (columns.containsKey(column)) {
            log.info("Updating column {}", column);
            columns.put(column, columns.get(column).stream().map(cell -> update(cell, cellUpdate)).collect(Collectors.toSet()));
        } else {
            log.warn("Discarding update for invalid column {}", column);
        }
    }

    private Cell update(final Cell cell, final Cell cellUpdate) {
        final boolean willUpdate = cell.hasSameCoordinates(cellUpdate);
        log.info("Updating row {}", cellUpdate.getRow());
        return willUpdate ? cellUpdate : cell;
    }
}
