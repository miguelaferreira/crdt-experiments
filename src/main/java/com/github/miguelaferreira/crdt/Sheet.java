package com.github.miguelaferreira.crdt;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import io.vavr.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sheet {
    private static final Logger log = LoggerFactory.getLogger(Sheet.class);

    private Map<Character, ColumnCrdt> columns;

    public Sheet() {
        final long currentTimeMillis = System.currentTimeMillis();

        this.columns = new HashMap<>(Map.of(
                'A', new ColumnCrdt(currentTimeMillis, Set.of(new Cell('A', 1), new Cell('A', 2))),
                'B', new ColumnCrdt(currentTimeMillis, Set.of(new Cell('B', 1), new Cell('B', 2)))
        ));
    }

    private Sheet(Map<Character, ColumnCrdt> columns) {
        this.columns = io.vavr.collection.HashMap.ofAll(columns)                         // clone the map
                                                 .map((k, v) -> Tuple.of(k, v.clone()))  // clone the Columns
                                                 .toJavaMap();
    }

    @Override
    public String toString() {
        return columns.entrySet().stream()
                      .sorted(Map.Entry.comparingByKey())
                      .map(Map.Entry::getValue)
                      .map(col -> col.lookup().stream().map(Cell::toString))
                      .map(col -> col.collect(Collectors.joining(" | ", "| ", " |")))
                      .collect(Collectors.joining("\n"));
    }

    public void update(char column, ColumnCrdt columnCrdtUpdate) {
        if (columns.containsKey(column)) {
            log.info("Updating column {}", column);
            final ColumnCrdt columnCrdtObject = columns.get(column);
            columns.put(column, columnCrdtObject.merge(columnCrdtUpdate));
        } else {
            log.warn("Discarding update for invalid column {}", column);
        }
    }

    public Optional<ColumnCrdt> getColumn(char column) {
        return Optional.ofNullable(columns.get(column));
    }

    @Override
    public Sheet clone() {
        return new Sheet(this.columns);
    }
}
