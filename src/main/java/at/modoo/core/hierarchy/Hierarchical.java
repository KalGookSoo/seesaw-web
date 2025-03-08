package at.modoo.core.hierarchy;

public interface Hierarchical<T extends Hierarchical<T>> {

    T getParent();

    void addChild(T child);

    default boolean isRoot() {
        return getParent() == null;
    }

    default boolean hasParent() {
        return !isRoot();
    }

}
