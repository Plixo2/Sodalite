package io.github.plixo2.uiiii;


import org.joml.Vector4i;


public class StagedScissorStack {

    private final SoftStack<Vector4i> scissorStack = new SoftStack<>(Vector4i::new);

    public void reset(int width, int height) {
        this.scissorStack.clear();
        push(0, 0, width, height);
    }

    public void push(int x, int y, int x2, int y2) {
        if (!this.scissorStack.isEmpty()) {
            var pre = this.scissorStack.peek();
            x = Math.max(pre.x, x);
            y = Math.max(pre.y, y);
            x2 = Math.min(pre.z, x2);
            y2 = Math.min(pre.w, y2);
        }
        var frame = this.scissorStack.push().set(x, y, x2, y2);
    }

    public void pop() {
        this.scissorStack.pop();
    }

    public Vector4i peek() {
        return this.scissorStack.peek();
    }

}
