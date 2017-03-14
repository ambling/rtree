package com.github.davidmoten.rtree;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.HasGeometry;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.github.davidmoten.rtree.internal.Comparators;
import com.github.davidmoten.rtree.internal.util.PriorityQueue;
import rx.Emitter;
import rx.Subscription;
import rx.functions.Action1;

import java.util.Comparator;

/**
 * @author ambling
 */
final class NearestEmitter<T, S extends Geometry> implements Action1<Emitter<Entry<T, S>>> {

    private final Node<T, S> node;
    private final Rectangle rect;
    private final double maxDistance;
    private final int maxCount;

    NearestEmitter(Node<T, S> node, Rectangle rect, double maxDistance, int maxCount) {
        this.node = node;
        this.rect = rect;
        this.maxDistance = maxDistance;
        this.maxCount = maxCount;
    }

    /**
     * Best-first search of k nearest neighbor query on RTree
     */
    @Override
    @SuppressWarnings("unchecked")
    public void call(Emitter<Entry<T, S>> entryEmitter) {
        // TODO this method is very tricky, but we have no other method yet
        // to get the subscription status from emitter.
        // Maybe we have to use other method to create the Observerable than `fromEmitter`
        Subscription subscription = (Subscription) entryEmitter;

        Comparator<HasGeometry> comparator = Comparators.ascendingGeometryDistance(rect);
        PriorityQueue<HasGeometry> queue = new PriorityQueue<HasGeometry>(comparator);
        queue.add(node);
        int found = 0;
        while (!queue.isEmpty() && found < maxCount && !subscription.isUnsubscribed()) {
            HasGeometry next = queue.poll();
            if (next instanceof NonLeaf) {
                NonLeaf<T, S> nonLeaf = (NonLeaf<T, S>) next;
                queue.addAll(nonLeaf.children());
            } else if (next instanceof Leaf) {
                Leaf<T, S> leaf = (Leaf<T, S>) next;
                queue.addAll(leaf.entries());
            } else if (next instanceof Entry) {
                Entry<T, S> entry = (Entry<T, S>) next;
                double dist = entry.geometry().distance(rect);
                if (dist <= maxDistance) {
                    ++ found;
                    entryEmitter.onNext(entry);
                } else {
                    break;
                }
            } else {
                entryEmitter.onError(new RuntimeException("Unsupported type: " + next));
                return;
            }
        }

        entryEmitter.onCompleted();
    }
}
