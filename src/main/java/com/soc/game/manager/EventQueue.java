package com.soc.game.manager;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Consumer;

public class EventQueue {
    private final SortedSet<Pair<Integer, Consumer<AbstractGameManager>>> events = new TreeSet<>();

    public EventQueue(Set<Pair<Integer, Consumer<AbstractGameManager>>> events) {
        this.events.addAll(events);
    }

    public void addEvent(int time, Consumer<AbstractGameManager> event) {
        events.add(Pair.of(time, event));
    }

    public int peekTime() {
        return this.events.getFirst().getLeft();
    }

    public Consumer<AbstractGameManager> peekEvent() {
        return this.events.getFirst().getRight();
    }

    public Collection<Consumer<AbstractGameManager>> tryPopEvents(int time) {
        ArrayList<Consumer<AbstractGameManager>> events = new ArrayList<>();

        while (time >= this.events.getFirst().getLeft()) {
            events.add(this.events.removeFirst().getRight());
        }

        return events;
    }
}
