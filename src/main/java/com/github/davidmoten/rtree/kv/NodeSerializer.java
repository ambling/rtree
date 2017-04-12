package com.github.davidmoten.rtree.kv;

import com.github.davidmoten.rtree.Node;
import com.github.davidmoten.rtree.geometry.Geometry;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The serializer that serialize RTree nodes (leaf or non-leaf).
 */
public interface NodeSerializer<T, S extends Geometry> {
    void write(Node<T, S> node, OutputStream os) throws IOException;

    Node<T, S> read(InputStream is, long sizeBytes) throws IOException;
}
