package io.github.plixo2.sodalite.memory;

public class IntList {
    private int[] data;
    private int length;
    private int size;

    public IntList() {
        this.length = 16;
        this.data = new int[this.length];
        this.size = 0;
    }

    public void clear() {
        this.size = 0;
    }

    public int size() {
        return this.size;
    }
    public int get(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.size);
        }
        return this.data[index];
    }

    public void add(int value) {
        if (this.size >= this.length) {
            resize();
        }
        this.data[this.size++] = value;
    }

    private void resize() {
        int newSize = this.length * 2;
        int[] newData = new int[newSize];
        System.arraycopy(this.data, 0, newData, 0, this.length);
        this.data = newData;
        this.length = newSize;
    }

}
