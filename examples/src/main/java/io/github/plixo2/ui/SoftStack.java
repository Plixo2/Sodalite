package io.github.plixo2.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class SoftStack<T> {

    private final Supplier<? extends T> factory;
    private final List<T> items = new ArrayList<>();
    private int count = 0;

    public SoftStack(Supplier<? extends T> factory) {
        this.factory = Objects.requireNonNull(factory, "factory");
    }

    public T pop() {
        if (this.count == 0) {
            throw new IllegalStateException("Stack is empty");
        }
        this.count--;
        return this.items.get(this.count);
    }
    public T push() {
        if (this.count < this.items.size()) {
            return this.items.get(this.count++);
        }
        T value = this.factory.get();
        this.items.add(value);
        this.count++;
        return value;
    }
    public T peek() {
        if (this.count == 0) {
            throw new IllegalStateException("Stack is empty");
        }
        return this.items.get(this.count - 1);
    }

    public void clear() {
        this.count = 0;
    }

    public int count() {
        return this.items.size();
    }

    public boolean isEmpty() {
        return this.count == 0;
    }

    public T get(int index) {
        if (index < 0 || index >= this.count) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Count: " + this.count);
        }
        return this.items.get(index);
    }

}
