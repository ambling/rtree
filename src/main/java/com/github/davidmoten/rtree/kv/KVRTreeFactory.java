package com.github.davidmoten.rtree.kv;

import com.esotericsoftware.kryo.io.Output;
import com.github.davidmoten.rtree.*;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.kryo.SerializerKryo;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * A factory with functions to save and load RTree from KVStore.
 */
public final class KVRTreeFactory<T, S extends Geometry> {

    private final SerializerKryo<T, S> serializer;

    public static String KEY_RTREE_CONTEXT = "rtree:context";

    public static String KEY_RTREE_NODE = "rtree:node:";

    public static String KEY_DATA = "data:";

    public KVRTreeFactory(SerializerKryo<T, S> serializer) {
        this.serializer = serializer;
    }

    public RTree<T, S> load(KVStore kvStore) {
        return null;
    }


    private final static class IDCount {
        private int nodeCnt;
        private int dataCnt;

        IDCount() {
            nodeCnt = 0;
            dataCnt = 0;
        }

        int getAndIncNodeCnt() {
            return nodeCnt ++;
        }

        int getAndIncDataCnt() {
            return dataCnt ++;
        }
    }
    public void save(KVStore kvStore, RTree<T, S> rtree) {

        OutputStream os = new ByteArrayOutputStream();
        Output output = new Output(os);
        serializer.writeContext(rtree.context(), output);
        kvStore.set(KEY_RTREE_CONTEXT, os.toString());

        if (rtree.root().isPresent()) {
            saveNode(kvStore, rtree.root().get(), new IDCount());
        }
    }

    private String saveNode(KVStore kvStore, Node<T, S> node, IDCount count) {

        OutputStream os = new ByteArrayOutputStream();
        Output output = new Output(os);
        // allocate key for pre-order traversal
        String key = KEY_RTREE_NODE + count.getAndIncNodeCnt();

        boolean isLeaf = node instanceof Leaf;
        output.writeBoolean(isLeaf);
        if (isLeaf) {
            Leaf<T, S> leaf = (Leaf<T, S>) node;
            serializer.writeBounds(output, leaf.geometry().mbr());
            output.writeInt(leaf.count());
            for (Entry<T, S> entry : leaf.entries()) {
                S g = entry.geometry();
                serializer.writeGeometry(output, g);
                String entryKey = saveData(kvStore, entry.value(), count);
                output.writeAscii(entryKey);
            }
        } else {
            NonLeaf<T, S> nonLeaf = (NonLeaf<T, S>) node;
            serializer.writeBounds(output, nonLeaf.geometry().mbr());
            output.writeInt(nonLeaf.count());
            for (Node<T, S> nd : nonLeaf.children()) {
                // write mbr of child
                serializer.writeBounds(output, nd.geometry().mbr());
                String childrenKey = saveNode(kvStore, nd, count);
                output.writeAscii(childrenKey);
            }
        }

        kvStore.set(key, os.toString());
        return key;
    }

    private String saveData(KVStore kvStore, T value, IDCount count) {
        OutputStream os = new ByteArrayOutputStream();
        Output output = new Output(os);
        serializer.writeValue(output, value);
        String key = KEY_DATA + count.getAndIncDataCnt();
        kvStore.set(key, os.toString());
        return key;
    }
}
