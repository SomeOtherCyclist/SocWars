package com.soc.game.manager;

import com.soc.lib.SocWarsLib;
import com.soc.lib.json.Time;
import net.minecraft.text.Text;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Consumer;

public class EventQueue<T extends AbstractGameManager<?, ?, ?>> {
    private final SortedSet<Event<T>> events;

    public EventQueue() {
        this.events = new TreeSet<>();
    }

    public EventQueue<T> addEvent(int time, Consumer<T> event, Text name) {
        this.events.add(new Event<>(time, event, name));
        return this;
    }

    public EventQueue<T> addEvent(Time time, Consumer<T> event, Text name) {
        this.events.add(new Event<>(time.ticks(), event, name));
        return this;
    }

    public Collection<Text> peekEventsNames(int time) {
        final ArrayList<Text> events = new ArrayList<>();

        while (time >= this.events.getFirst().time()) {
            Event<T> event = this.events.getFirst();
            events.add(Text.translatable("event.name_and_time", event.name(), SocWarsLib.getTimeFromSeconds(event.time(), false)));
        }

        return events;
    }

    public Collection<Pair<Consumer<T>, Text>> tryPopEvents(int time) {
        final ArrayList<Pair<Consumer<T>, Text>> events = new ArrayList<>();

        while (!this.events.isEmpty() && time >= this.events.getFirst().time()) {
            Event<T> event = this.events.removeFirst();
            events.add(Pair.of(event.callback(), event.name()));
        }

        return events;
    }

    public Collection<Text> tryPopAndRunEvents(int time, T context) {
        return this.tryPopEvents(time).stream().map(event -> {
            event.getLeft().accept(context);
            return event.getRight();
        }).toList();
    }

    public void cancelEvents() {
        this.events.clear();
    }

    public SortedSet<Event<T>> getEvents() {
        return this.events;
    }

    public boolean isEmpty() {
        return this.events.isEmpty();
    }
}
