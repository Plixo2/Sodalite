package io.github.plixo2.sodalite.memory;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class IntList implements Iterable<Integer> {
    private int[] data;
    private int length;
    private int size;

    public IntList() {
        this.length = 16;
        this.data = new int[this.length];
        this.size = 0;
    }

    /// Removes all elements from the list
    public void clear() {
        this.size = 0;
    }

    /// @return if the list is empty
    public boolean isEmpty() {
        return this.size == 0;
    }

    /// @return the number of elements in the list
    public int size() {
        return this.size;
    }

    /// @return the index of the first occurrence of the value, or -1 if not found
    public int indexOf(int value) {
        for (int i = 0; i < this.size; i++) {
            if (this.data[i] == value) {
                return i;
            }
        }
        return -1;
    }

    /// @return the index of the last occurrence of the value, or -1 if not found
    public int lastIndexOf(int value) {
        for (int i = this.size - 1; i >= 0; i--) {
            if (this.data[i] == value) {
                return i;
            }
        }
        return -1;
    }

    /// @return if the value is in the list
    public boolean contains(int value) {
        return indexOf(value) != -1;
    }

    /// Set the element at the specified index to the given value
    /// @return the old value at the index
    /// @throws IndexOutOfBoundsException if the index is out of bounds
    public int set(int index, int value) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.size);
        }
        int old = this.data[index];
        this.data[index] = value;
        return old;
    }

    /// Gets the element at the specified index
    /// @throws IndexOutOfBoundsException if the index is out of bounds
    public int get(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.size);
        }
        return this.data[index];
    }

    /// Add a value to the end of the list
    public boolean add(int value) {
        if (this.size >= this.length) {
            resize();
        }
        this.data[this.size++] = value;
        return true;
    }

    /// Add multiple values to the end of the list
    public void add(int[] values) {
        if (this.size + values.length >= this.length) {
            resize(this.size + values.length);
        }
        System.arraycopy(values, 0, this.data, this.size, values.length);
        this.size += values.length;
    }

    /// Add multiple values to the end of the list
    public void add(int[] values, int from, int size) {
        if (from < 0 || size < 0 || from + size > values.length) {
            throw new IndexOutOfBoundsException("From: " + from + ", Size: " + size + ", Length: " + values.length);
        }
        if (this.size + size >= this.length) {
            resize(this.size + size);
        }
        System.arraycopy(values, from, this.data, this.size, size);
        this.size += size;
    }

    /// Add all values from another IntList to the end of the list
    public void add(IntList values) {
        if (this.size + values.size >= this.length) {
            resize(this.size + values.size);
        }
        System.arraycopy(values.data, 0, this.data, this.size, values.size);
        this.size += values.size;
    }

    /// @return the added value
    public int push(int value) {
        add(value);
        return value;
    }

    /// Removes the last element
    /// @return the removed element
    /// @throws NoSuchElementException if the list is empty
    public int removeLast() {
        if (this.size == 0) {
            throw new NoSuchElementException("List is empty");
        }
        return this.data[--this.size];
    }
    /// Removes the last element
    /// @return the removed element
    /// @throws NoSuchElementException if the list is empty
    public int pop() {
        return removeLast();
    }
    public int peek() {
        if (this.size == 0) {
            throw new NoSuchElementException("List is empty");
        }
        return this.data[this.size - 1];
    }


    public void forEach(IntConsumer consumer) {
        for (int i = 0; i < this.size; i++) {
            consumer.accept(this.data[i]);
        }
    }

    @Override
    public void forEach(Consumer<? super Integer> consumer) {
        for (int i = 0; i < this.size; i++) {
            consumer.accept(this.data[i]);
        }
    }


    private void resize() {
        int newSize = this.length * 2;
        int[] newData = new int[newSize];
        System.arraycopy(this.data, 0, newData, 0, this.length);
        this.data = newData;
        this.length = newSize;
    }
    private void resize(int min) {
        int newSize = this.length * 2;
        while (newSize < min) {
            newSize *= 2;
        }
        int[] newData = new int[newSize];
        System.arraycopy(this.data, 0, newData, 0, this.length);
        this.data = newData;
        this.length = newSize;
    }


    @Override
    public @NotNull Iterator<Integer> iterator() {
        return new IntListIterator();
    }

    @RequiredArgsConstructor
    private final class IntListIterator implements Iterator<Integer> {
        private int index = 0;

        @Override
        public boolean hasNext() {
            return this.index < IntList.this.size;
        }

        @Override
        public Integer next() {
            return IntList.this.get(this.index++);
        }
    }


}
