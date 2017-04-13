package com.github.davidmoten.rtree.kv;

import com.github.davidmoten.guavamini.Optional;
import com.github.davidmoten.rtree.*;
import com.github.davidmoten.rtree.geometry.Geometry;

/**
 * A factory with functions of saving RTree to and loading RTree from KVStore.
 */
public final class KVRTreeFactory {

    public static String KEY_RTREE_NODE = "rtree:node:";

    public static String KEY_DATA = "data:";

    private KVRTreeFactory() {

    }

    public static <T, S extends Geometry> RTree<T, S> load(KVStore<T, S> kvStore) {
        Context<T, S> context = kvStore.getContext();
        if (! (context.factory() instanceof FactoryOnKV)) {
            context = new Context<T, S>(context.minChildren(), context.maxChildren(),
                    context.selector(), context.splitter(), new FactoryOnKV<T, S>(kvStore));
        }
        int size = kvStore.getSize();
        Node<T, S> root = kvStore.getNode(kvStore.rootKey());
        Optional<Node<T, S>> rootOpt = root == null ? Optional.<Node<T,S>>absent() : Optional.of(root);

        return RTree.create(rootOpt, size, context);
    }

    public static <T, S extends Geometry> void save(KVStore<T, S> kvStore, RTree<T, S> rtree) {
        kvStore.setSize(rtree.size());
        kvStore.setContext(rtree.context());
        if (rtree.root().isPresent()) {
            String rootKey = saveNode(kvStore, rtree.root().get());
            kvStore.setRootKey(rootKey);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T, S extends Geometry> String saveNode(KVStore<T, S> kvStore, Node<T, S> node) {
        if (node instanceof NodeOnKV && kvStore == ((NodeOnKV) node).kvStore()) {
            return ((NodeOnKV) node).key();
        } else {
            FactoryOnKV<T, S> factory = new FactoryOnKV<T, S>(kvStore);
            NodeOnKV<T, S> root;
            if (node instanceof Leaf) {
                root = (LeafOnKV<T, S>) factory.createLeaf(((Leaf) node).entries(), node.context());
            }
            else {
                root = (NonLeafOnKV<T, S>) factory.createNonLeaf(((NonLeaf) node).children(), node.context());
            }
            return root.key();
        }

    }

    public static <T, S extends Geometry> String nextNodeKey(KVStore<T, S> kvStore) {
        int cnt = kvStore.getNodeCnt();
        while (true) {
            String key = KEY_RTREE_NODE + cnt;
            if (kvStore.getNode(key) == null) return key;
            ++ cnt;
        }
    }

    public static <T, S extends Geometry> String nextDataKey(KVStore<T, S> kvStore) {
        int cnt = kvStore.getDataCnt();
        while (true) {
            String key = KEY_DATA + cnt;
            if (kvStore.getData(key) == null) return key;
            ++ cnt;
        }
    }
}
