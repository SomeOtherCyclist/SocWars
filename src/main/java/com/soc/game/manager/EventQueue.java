package com.soc.game.manager;

import net.minecraft.text.Text;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;
import java.util.function.Consumer;

public class EventQueue {
    private final SortedSet<Triple<Integer, Consumer<AbstractGameManager>, String>> events;

    public EventQueue() {
        this.events = new TreeSet<>();
    }

    public EventQueue(Set<Triple<Integer, Consumer<AbstractGameManager>, String>> events) {
        this();
        this.events.addAll(events);
    }

    public void addEvent(int time, Consumer<AbstractGameManager> event, String name) {
        events.add(Triple.of(time, event, name));
    }

    public void addEventSeconds(int time, Consumer<AbstractGameManager> event, String name) {
        events.add(Triple.of(time * 20, event, name));
    }

    public void addEventMinutesSeconds(int minutes, int seconds, Consumer<AbstractGameManager> event, String name) {
        events.add(Triple.of(minutes * 1200 + seconds * 20, event, name));
    }

    public int peekTime() {
        return this.events.getFirst().getLeft();
    }

    public Consumer<AbstractGameManager> peekEvent() {
        return this.events.getFirst().getMiddle();
    }

    public Collection<Pair<Integer, String>> peekEvents(int time) {
        final ArrayList<Pair<Integer, String>> events = new ArrayList<>();

        while (time >= this.events.getFirst().getLeft()) {
            Triple<Integer, Consumer<AbstractGameManager>, String> event = this.events.getFirst();
            events.add(Pair.of(event.getLeft(), event.getRight()));
        }

        return events;
    }

    public Collection<Text> peekEventsText(int time) {
        final ArrayList<Text> events = new ArrayList<>();

        while (time >= this.events.getFirst().getLeft()) {
            Triple<Integer, Consumer<AbstractGameManager>, String> event = this.events.getFirst();
            //This also needs fixing because currently it uses the event id as a translation key which is really not good
            events.add(Text.translatable(event.getRight(), timeToText(event.getLeft())));
        }

        return events;
    }

    private static Text timeToText(int time) {
        //Really should write a proper thing here to convert to an actually nice looking time
        return Text.translatable("time.seconds", time / 20);
    }

    public Collection<Pair<Consumer<AbstractGameManager>, String>> tryPopEvents(int time) {
        final ArrayList<Pair<Consumer<AbstractGameManager>, String>> events = new ArrayList<>();

        while (time >= this.events.getFirst().getLeft()) {
            Triple<Integer, Consumer<AbstractGameManager>, String> event = this.events.removeFirst();
            events.add(Pair.of(event.getMiddle(), event.getRight()));
        }

        return events;
    }
}
