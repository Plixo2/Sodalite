package io.github.plixo2.docs;

import io.github.plixo2.sodalite.Internal;
import io.github.plixo2.sodalite.category.clipboard.Clipboard;
import io.github.plixo2.sodalite.category.cpuinfo.CPUInfo;
import io.github.plixo2.sodalite.category.error.Error;
import io.github.plixo2.sodalite.category.gpu.TextureFormat;
import io.github.plixo2.sodalite.category.init.AppResult;
import io.github.plixo2.sodalite.category.init.Init;
import io.github.plixo2.sodalite.category.init.InitFlags;
import io.github.plixo2.sodalite.category.locale.Locale;
import io.github.plixo2.sodalite.category.log.Log;
import io.github.plixo2.sodalite.category.log.LogCategory;
import io.github.plixo2.sodalite.category.log.LogPriority;
import io.github.plixo2.sodalite.category.main.Callbacks;
import io.github.plixo2.sodalite.category.main.Main;
import io.github.plixo2.sodalite.category.pixels.PixelFormat;
import io.github.plixo2.sodalite.category.platform.Platform;
import io.github.plixo2.sodalite.category.power.Power;
import io.github.plixo2.sodalite.category.properties.Properties;
import io.github.plixo2.sodalite.category.properties.PropertyKey;
import io.github.plixo2.sodalite.category.rect.FRect;
import io.github.plixo2.sodalite.category.rect.Rect;
import io.github.plixo2.sodalite.category.thread.ThreadID;
import io.github.plixo2.sodalite.category.thread.Threads;
import io.github.plixo2.sodalite.category.timer.Timer;
import io.github.plixo2.sodalite.category.version.Version;
import io.github.plixo2.sodalite.category.version.VersionNumber;
import io.github.plixo2.sodalite.category.version.VersionTarget;
import io.github.plixo2.sodalite.resource.ResourceObject;
import io.github.plixo2.sodalite.resource.ResourceSet;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.joml.Vector4i;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.foreign.Arena;
import java.lang.foreign.ValueLayout;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class BasicTests {

    @AfterEach
    void quit() {
        Init.quit();
    }


    @Test
    void checkClipboardTest() {
        Init.ensureInit(InitFlags.VIDEO);
        Clipboard.setText("Hello, world!");
        assertTrue(Clipboard.hasText());
        assertEquals("Hello, world!", Clipboard.getText());
    }

    @Test
    void checkClipboardData() {
        Init.ensureInit(InitFlags.VIDEO);
        Clipboard.setData(
            (arena, mime) -> {
                if (mime.equals("text/plain")) {
                    return arena.allocateFrom("Hello Bytes");
                } else {
                    assertEquals("soda/lite", mime);
                    var allocate = arena.allocate(ValueLayout.JAVA_INT);
                    allocate.set(ValueLayout.JAVA_INT, 0, 0xCAFEBABE);
                    return allocate;
                }
            },
            "text/plain",
            "soda/lite"
        );
        assertTrue(Clipboard.hasText());
        assertEquals("Hello Bytes", Clipboard.getText());

        var type = Clipboard.getMimeTypes();
        assertTrue(type.contains("text/plain"), "contains text/plain");
        assertTrue(type.contains("soda/lite"),  "contains soda/lite");

        var data = Clipboard.getData(ResourceSet.ofAuto(), "soda/lite");
        assertEquals(0xCAFEBABE, data.segment().get(ValueLayout.JAVA_INT, 0));

        Clipboard.clearData();
        assertFalse(Clipboard.hasData("soda/lite"), "data should be cleared");
        assertFalse(Clipboard.hasData("text/plain"), "data should be cleared");
        assertThrows(Internal.SDL3Exception.class, () -> Clipboard.getData(ResourceSet.ofAuto(), "soda/lite"));
        assertEquals("", Clipboard.getText());
    }

    @Test
    void cpuInfo() {
        assertTrue(CPUInfo.cpuCacheLineSize() >= 0, "cpuCacheLineSize");
        assertTrue(CPUInfo.numLogicalCores() >= 0, "numLogicalCores");
        assertTrue(CPUInfo.systemPageSize().or(0) >= 0, "systemPageSize");
        assertTrue(CPUInfo.systemRAM() >= 0, "systemRAM");


    }

    @Test
    void errorMethods() {
        Error.clearError();
        Error.setError("Test error");
        assertEquals("Test error", Error.getError());
        Error.outOfMemory();
        assertEquals("Out of memory", Error.getError());
        Error.clearError();
        assertEquals("", Error.getError());
    }

    @Test
    void checkInit() {
        Init.ensureInit(InitFlags.EVENTS);
    }

    @Test
    void locale() {
        var _ = Locale.preferredLocales();
    }

    @Test
    void log() {
        Log.setLogPriority(LogCategory.APPLICATION, LogPriority.DEBUG);
    }

    @Test
    void mainCallback() {
        class TestCallbacks implements Callbacks {
            boolean initCalled = false;
            boolean iterCalled = false;
            boolean quitCalled = false;
            boolean throwException = false;
            @Override
            public AppResult init(String[] args) throws Exception {
                this.initCalled = true;
                if (this.throwException) {
                    throw new RuntimeException("Test exception");
                }
                return AppResult.CONTINUE;
            }

            @Override
            public AppResult iterate() throws Exception {
                this.iterCalled = true;
                return AppResult.SUCCESS;
            }

            @Override
            public void quit(AppResult result) {
                this.quitCalled = true;
            }
        };
        var instance = new TestCallbacks();
        assertTrue(assertDoesNotThrow(() -> Main.enterAppMainCallbacks(instance, new String[]{})));
        assertTrue(instance.initCalled);
        assertTrue(instance.iterCalled);
        assertTrue(instance.quitCalled);
        var throwInstance = new TestCallbacks();
        throwInstance.throwException = true;
        assertThrows(RuntimeException.class, () -> Main.enterAppMainCallbacks(throwInstance, new String[]{}));
        assertTrue(throwInstance.initCalled);
        assertFalse(throwInstance.iterCalled, "iterate should not be called if init throws");
        assertTrue(throwInstance.quitCalled, "quit should be called even if init throws");
    }

    @Test
    void pixels() {
        for (var value : PixelFormat.values()) {
            var to = value.toTextureFormat(TextureFormat.INVALID);
            if (to == TextureFormat.INVALID) {
                continue;
            }
            var from = to.toPixelFormat(PixelFormat.UNKNOWN);
            if (from == PixelFormat.UNKNOWN) {
                continue;
            }
            assertEquals(value, from, "PixelFormat " + value + " should round-trip through TextureFormat");
        }
        for (var value : TextureFormat.values()) {
            var to = value.toPixelFormat(PixelFormat.UNKNOWN);
            if (to == PixelFormat.UNKNOWN) {
                continue;
            }
            var from = to.toTextureFormat(TextureFormat.INVALID);
            if (from == TextureFormat.INVALID) {
                continue;
            }
            assertEquals(value, from, "TextureFormat " + value + " should round-trip through PixelFormat");
        }

    }

    @Test
    void platform() {
        var _ = Platform.getPlatform();
    }

    @Test
    void power() {
        var info = Power.getPowerInfo();
        assertTrue(info.percent().or(0) >= 0, "percent should be non-negative");
        assertTrue(info.seconds().or(0) >= 0, "seconds should be non-negative");
    }

    @Test
    void properties() {
        try (var resources = ResourceSet.ofConfined()) {
            var group = Properties.createProperties(resources);
            assertEquals(false, group.get(PropertyKey.ofBoolean("bool"), false));
            assertEquals(0L, group.get(PropertyKey.ofNumber("num"), 0L));
            assertEquals("", group.get(PropertyKey.ofString("str"), ""));
            assertEquals(0f, group.get(PropertyKey.ofFloat("flt"), 0f));
            assertNull(group.get(PropertyKey.ofPointer("ptr"), null));

            group.set(PropertyKey.ofBoolean("bool"), true);
            group.set(PropertyKey.ofNumber("num"), 123L);
            group.set(PropertyKey.ofString("str"), "Hello");
            group.set(PropertyKey.ofFloat("flt"), 3.14f);
            var ptrValue = resources.allocateFrom("Pointer Value");
            group.set(PropertyKey.ofPointer("ptr"), ptrValue);

            assertEquals(true, group.get(PropertyKey.ofBoolean("bool"), false));
            assertEquals(123L, group.get(PropertyKey.ofNumber("num"), 0L));
            assertEquals("Hello", group.get(PropertyKey.ofString("str"), ""));
            assertEquals(3.14f, group.get(PropertyKey.ofFloat("flt"), 0f));
            assertEquals("Pointer Value",
                    Objects.requireNonNull(group.get(PropertyKey.ofPointer("ptr"), null)).getString(0)
            );

            group.properties().forEach(property -> {
                assertTrue(group.has(property), "group should have property " + property.asString());
                assertNotNull(group.typeOf(property), "property type should not be null for " + property.asString());
            });
            assertTrue(group.properties().contains(PropertyKey.ofBoolean("bool")));
            assertTrue(group.properties().contains(PropertyKey.ofNumber("num")));
            assertTrue(group.properties().contains(PropertyKey.ofString("str")));
            assertTrue(group.properties().contains(PropertyKey.ofFloat("flt")));
            assertTrue(group.properties().contains(PropertyKey.ofPointer("ptr")));
        }
    }
    @Test
    void lockProperties() throws InterruptedException {
        try (var resources = ResourceSet.ofConfined()) {
            var group = Properties.createProperties(resources);
            group.set(PropertyKey.ofBoolean("bool"), true);
            group.set(PropertyKey.ofNumber("num"), 123L);
            group.lock();
            group.set(PropertyKey.ofString("str"), "Hello");
            group.unlock();

            assertEquals(true, group.get(PropertyKey.ofBoolean("bool"), false));
            assertEquals(123L, group.get(PropertyKey.ofNumber("num"), 0L));
            assertEquals("Hello", group.get(PropertyKey.ofString("str"), ""));

            group.lock();
            Thread newThread;
            try {
                var id = Threads.getCurrentThreadID();
                newThread = new Thread(() -> {
                    var otherID = Threads.getCurrentThreadID();
                    if (!otherID.equals(id)) {
                        group.lock();
                        try {
                            group.set(PropertyKey.ofString("str"), "OtherThread");
                        } finally {
                            group.unlock();
                        }
                    }
                });
                newThread.setDaemon(false);
                newThread.start();
                newThread.join(500);
                if (!newThread.isAlive()) {
                    fail("Failed to lock properties from another thread");
                }
                assertEquals("Hello", group.get(PropertyKey.ofString("str"), ""));
            } finally {
                group.unlock();
            }
            newThread.join(500);
            if (newThread.isAlive()) {
                fail("Failed to unlock properties from another thread");
            }
            assertEquals("OtherThread", group.get(PropertyKey.ofString("str"), ""));

        }

    }

    @Test
    void threadIDs() throws InterruptedException {
        var id = Threads.getCurrentThreadID();

        class Holder {
            volatile ThreadID otherThreadID = null;
        }
        var holder = new Holder();

        var newThread = new Thread(() -> {
            holder.otherThreadID = Threads.getCurrentThreadID();
        });
        newThread.setDaemon(false);
        newThread.start();
        var start = Timer.getTicksMS();
        while (holder.otherThreadID == null) {
            var now = Timer.getTicksMS();
            if (now - start > 2000) {
                fail("Timeout waiting for other thread to set its ID");
            }
            Thread.onSpinWait();
        }
        if (id.value() != 0 && holder.otherThreadID.value() != 0) {
            assertNotEquals(id, holder.otherThreadID, "Thread IDs should be different");
        }

        newThread.join(2000);
    }

    @Test
    void rect() {
        Rect r1 = Rect.of(10, 20, 30, 40);
        Rect r2 = Rect.of(r1);
        Rect empty = Rect.zero();

        r1.x(5);
        assertEquals(5, r1.x());
        r1.y(7);
        assertEquals(7, r1.y());
        r1.width(15);
        assertEquals(15, r1.width());
        r1.height(25);
        assertEquals(25, r1.height());

        assertNotEquals(r1, r2);

        assertEquals(0, empty.x());
        assertEquals(0, empty.y());
        assertEquals(0, empty.width());
        assertEquals(0, empty.height());

        assertTrue(Rect.zero().empty());
        assertTrue(Rect.of(0, 0, 0, 5).empty());
        assertTrue(Rect.of(0, 0, 5, 0).empty());
        assertFalse(Rect.of(0, 0, 5, 5).empty());

        assertNotNull(r1.toFRect());

        Rect box = Rect.of(10, 10, 10, 10);
        assertTrue(box.isInside(10, 10));
        assertTrue(box.isInside(15, 15));
        assertTrue(box.isInside(20, 20));
        assertFalse(box.isInside(9, 10));
        assertFalse(box.isInside(21, 10));

        assertTrue(box.isInside(new Vector2i(12, 12)));
        assertFalse(box.isInside(new Vector2i(0, 0)));

        Rect a = Rect.of(0, 0, 10, 10);
        Rect b = Rect.of(5, 5, 10, 10);
        Rect c = Rect.of(20, 20, 5, 5);
        assertTrue(a.intersects(b));
        assertFalse(a.intersects(c));

        Rect u1 = a.union(b);
        assertEquals(0, u1.x());
        assertEquals(0, u1.y());
        assertEquals(15, u1.width());
        assertEquals(15, u1.height());

        assertEquals(b, Rect.zero().union(b));
        assertEquals(a, a.union(Rect.zero()));

        Rect inter = a.intersection(b);
        assertNotNull(inter);
        assertEquals(5, inter.x());
        assertEquals(5, inter.y());
        assertEquals(5, inter.width());
        assertEquals(5, inter.height());

        assertNull(a.intersection(c));

        Rect same = Rect.of(0, 0, 10, 10);
        assertEquals(a, same);
        assertEquals(a.hashCode(), same.hashCode());
        assertNotEquals(a, b);

        Vector4i lineRes = new Vector4i();
        Vector4i li = a.lineIntersection(lineRes, 0, 0, 10, 10);
        if (li != null) {
            assertSame(lineRes, li);
        }

        Vector4i li2 = a.lineIntersection(0, 0, 10, 10);
        assertNotNull(li2);

        Vector4i li3 = a.lineIntersection(new Vector2i(0, 0), new Vector2i(10, 10));
        assertNotNull(li3);

        List<Vector2i> points = List.of(
                new Vector2i(0, 0),
                new Vector2i(10, 10),
                new Vector2i(5, 2)
        );

        Rect enc = Rect.enclosingPoints(points, null);
        if (enc != null) {
            assertTrue(enc.width() >= 0);
            assertTrue(enc.height() >= 0);
        }

        assertNull(Rect.enclosingPoints(List.of(), null));
    }

    @Test
    void fRect() {
        FRect a = FRect.of(10f, 20f, 30f, 40f);
        FRect b = FRect.of(a);
        FRect empty = FRect.zero();

        a.x(1f);
        assertEquals(1f, a.x(), 0.00001f);
        a.y(2f);
        assertEquals(2f, a.y(), 0.00001f);
        a.width(3f);
        assertEquals(3f, a.width(), 0.00001f);
        a.height(4f);
        assertEquals(4f, a.height(), 0.00001f);

        assertNotEquals(a, b);

        assertEquals(0f, empty.x(), 0.00001f);
        assertEquals(0f, empty.y(), 0.00001f);
        assertEquals(0f, empty.width(), 0.00001f);
        assertEquals(0f, empty.height(), 0.00001f);

        assertTrue(FRect.of(0f, 0f, -1f, 10f).empty());
        assertTrue(FRect.of(0f, 0f, 10f, -1f).empty());
        assertFalse(FRect.of(0f, 0f, 10f, 10f).empty());

        FRect fr = FRect.of(1.9f, 2.1f, 3.6f, 4.4f);
        Rect floored = fr.floor();
        assertEquals(1, floored.x());
        assertEquals(2, floored.y());
        assertEquals(3, floored.width());
        assertEquals(4, floored.height());

        Rect rounded = fr.round();
        assertEquals(2, rounded.x());
        assertEquals(2, rounded.y());
        assertEquals(4, rounded.width());
        assertEquals(4, rounded.height());

        FRect box = FRect.of(10f, 10f, 10f, 10f);
        assertTrue(box.isInside(10f, 10f));
        assertTrue(box.isInside(15f, 15f));
        assertTrue(box.isInside(20f, 20f));
        assertFalse(box.isInside(9f, 10f));
        assertFalse(box.isInside(21f, 10f));

        assertTrue(box.isInside(new Vector2f(12f, 12f)));
        assertFalse(box.isInside(new Vector2f(0f, 0f)));

        FRect c = FRect.of(0f, 0f, 10f, 10f);
        FRect d = FRect.of(5f, 5f, 10f, 10f);
        FRect e = FRect.of(20f, 20f, 5f, 5f);

        assertTrue(c.intersects(d));
        assertFalse(c.intersects(e));

        FRect u = c.union(d);
        assertEquals(0f, u.x(), 0.00001f);
        assertEquals(0f, u.y(), 0.00001f);
        assertEquals(15f, u.width(), 0.00001f);
        assertEquals(15f, u.height(), 0.00001f);


        FRect inter = c.intersection(d);
        assertNotNull(inter);
        assertEquals(5f, inter.x(), 0.00001f);
        assertEquals(5f, inter.y(), 0.00001f);
        assertEquals(5f, inter.width(), 0.00001f);
        assertEquals(5f, inter.height(), 0.00001f);

        assertNull(c.intersection(e));

        FRect approx = FRect.of(10.0001f, 20.0001f, 30.0001f, 40.0001f);
        assertTrue(FRect.of(10f, 20f, 30f, 40f).equals(approx, 0.001f));
        assertFalse(FRect.of(10f, 20f, 30f, 40f).equals(approx, 0.0000001f));

        FRect same = FRect.of(0f, 0f, 10f, 10f);
        assertEquals(c, same);
        assertEquals(c.hashCode(), same.hashCode());
        assertNotEquals(c, d);

        Vector4f res = new Vector4f();
        Vector4f li = c.lineIntersection(res, 0f, 0f, 10f, 10f);

        if (li != null) {
            assertSame(res, li);
        }

        Vector4f li2 = c.lineIntersection(0f, 0f, 10f, 10f);
        assertNotNull(li2);

        Vector4f li3 = c.lineIntersection(new Vector2f(0f, 0f), new Vector2f(10f, 10f));
        assertNotNull(li3);

        List<Vector2f> points = List.of(
                new Vector2f(0f, 0f),
                new Vector2f(10f, 10f),
                new Vector2f(5f, 2f)
        );

        FRect enc = FRect.enclosingPoints(points, null);
        if (enc != null) {
            assertTrue(enc.width() >= 0f);
            assertTrue(enc.height() >= 0f);
        }

        assertNull(FRect.enclosingPoints(List.of(), null));
    }

    @Test
    void timer() throws InterruptedException {
        var start = Timer.getTicksNS();
        Thread.sleep(10);
        var end = Timer.getTicksNS();
        assertTrue(end > start, "end time should be greater than start time");
    }

    @Test
    void version() {
        var versionL = Version.getVersion(VersionTarget.LINKED);
        var versionC = Version.getVersion(VersionTarget.COMPILED);
        assertEquals(3, versionL.major());
        assertEquals(3, versionC.major());
        assertTrue(versionL.minor() >= 4);
        assertTrue(versionC.minor() >= 4);
        assertTrue(versionL.compareTo(VersionNumber.of(3, 4, 0)) >= 0);
        assertTrue(versionC.compareTo(VersionNumber.of(3, 4, 0)) >= 0);
        assertTrue(versionL.compareTo(VersionNumber.of(9, 4, 0)) < 0);
        assertTrue(versionC.compareTo(VersionNumber.of(9, 4, 0)) < 0);
    }

    @Test
    void resources() {
        class Dummy extends ResourceObject {
            boolean closed = false;
            Dummy(ResourceSet set) {
                set.register(this, () -> this.closed = true);
            }
        }

        Dummy o;
        Dummy o0;

        try (var confined = ResourceSet.ofConfined()) {
            o = new Dummy(confined);
            o0 = new Dummy(ResourceSet.ofConfined(confined));
            Dummy o01;
            try (var confinedInner = ResourceSet.ofConfined(confined)) {
                o01 = new Dummy(confinedInner);
                assertFalse(o01.closed, "object 01 should not be closed yet");
            }
            assertTrue(o01.closed, "object 01 should be closed after inner ResourceSet is closed");
            assertFalse(o.closed, "object should not be closed yet");
            assertFalse(o0.closed, "object 0 should not be closed yet");
        }
        assertTrue(o.closed, "object should be closed after ResourceSet is closed");
        assertTrue(o0.closed, "object 0 should be closed after parent is closed");

        Dummy o2 = new Dummy(ResourceSet.ofAuto());
        assertFalse(o2.closed, "object 2 should not be closed yet");

        Dummy o3 = new Dummy(ResourceSet.global());
        assertFalse(o3.closed, "object 3 should not be closed yet");

        Init.quit();
        assertTrue(o2.closed, "object 2 should be closed after quit");
        assertTrue(o3.closed, "object 3 should be closed after quit");

        assertNotNull(o2);
    }

    @Test
    void testMainThread() throws InterruptedException {
        var other = new Thread(() -> {
            Init.ensureInit(InitFlags.VIDEO);
            assertTrue(Init.isMainThread());
            Init.quit();
        });
        other.start();
        other.join();

        Init.ensureInit(InitFlags.VIDEO);
        assertTrue(Init.isMainThread());
        other = new Thread(() -> {
            assertFalse(Init.isMainThread());
        });
        other.start();
        other.join();
        assertTrue(Init.isMainThread());


    }


}
