package com.github.davidmoten.rtree.fbs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

import com.github.davidmoten.rtree.GreekEarthquakes;
import com.github.davidmoten.rtree.Leaf;
import com.github.davidmoten.rtree.Node;
import com.github.davidmoten.rtree.NonLeaf;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.flatbuffers.Box_;
import com.github.davidmoten.rtree.flatbuffers.Context_;
import com.github.davidmoten.rtree.flatbuffers.Node_;
import com.github.davidmoten.rtree.flatbuffers.Tree_;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.google.flatbuffers.FlatBufferBuilder;

import rx.functions.Func1;

public class FlatBuffersSerializer {

    public <T, S extends Geometry> void serialize(RTree<T, S> tree, Func1<T, byte[]> serializer,
            OutputStream os) throws IOException {
        FlatBufferBuilder builder = new FlatBufferBuilder();
        int n = addNode(tree.root().get(), builder, serializer);

        Rectangle mbb = tree.root().get().geometry().mbr();
        int b = Box_.createBox_(builder, mbb.x1(), mbb.y1(), mbb.x2(), mbb.y2());
        Context_.startContext_(builder);
        Context_.addBounds(builder, b);
        Context_.addMaxChildren(builder, tree.context().maxChildren());
        Context_.addMinChildren(builder, tree.context().minChildren());
        int c = Context_.endContext_(builder);

        int t = Tree_.createTree_(builder, c, n);
        Tree_.finishTree_Buffer(builder, t);
        os.write(builder.dataBuffer().array());
    }

    private static <T, S extends Geometry> int addNode(Node<T, S> node, FlatBufferBuilder builder,
            Func1<T, byte[]> serializer) {
        if (node instanceof Leaf) {
            Leaf<T, S> leaf = (Leaf<T, S>) node;
            return FlatBuffersHelper.addEntries(leaf.entries(), builder, serializer);
        } else {
            NonLeaf<T, S> nonLeaf = (NonLeaf<T, S>) node;
            int[] nodes = new int[nonLeaf.count()];
            for (int i = 0; i < nonLeaf.count(); i++) {
                Node<T, S> child = nonLeaf.children().get(i);
                nodes[i] = addNode(child, builder, serializer);
            }
            int ch = Node_.createChildrenVector(builder, nodes);
            Node_.startNode_(builder);
            Node_.addChildren(builder, ch);
            Rectangle mbb = nonLeaf.geometry().mbr();
            int b = Box_.createBox_(builder, mbb.x1(), mbb.y1(), mbb.x2(), mbb.y2());
            Node_.addMbb(builder, b);
            return Node_.endNode_(builder);
        }
    }

    public <T, S extends Geometry> RTree<T, S> deserialize(InputStream is) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Test
    public void testSerialize() throws IOException {
        RTree<Object, Point> tree = RTree.star().maxChildren(4).create();
        tree = tree.add(GreekEarthquakes.entries()).last().toBlocking().single();
        long t = System.currentTimeMillis();
        File output = new File("target/file");
        FileOutputStream os = new FileOutputStream(output);
        new FlatBuffersSerializer().serialize(tree, new Func1<Object, byte[]>() {
            @Override
            public byte[] call(Object o) {
                return "boo".getBytes();
            }
        }, os);
        os.close();
        System.out.println("written in " + (System.currentTimeMillis() - t) + "ms, " + "file size="
                + output.length());
    }

}
