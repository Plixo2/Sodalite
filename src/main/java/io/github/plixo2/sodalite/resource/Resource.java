package io.github.plixo2.sodalite.resource;


/// Any lambda or class that implements this interface
/// should never refererence the owner it is registers with, directly or indirectly,
/// as this prevents the owner from being garbage collected,
/// when used with [ResourceSet#ofAuto].
///
/// In practice, pay attention to the following:
/// - Captured variables in lambdas
/// - Non-static inner classes, where the outer class is the owner
/// - Any fields in classes that implements this interface
///
public interface Resource {

    /// Any exception thrown by this method will NOT be caught to avoid
    /// subsequent errors (e.g. use-after-free or double-free's) that might crash the jvm
    void free();

}
