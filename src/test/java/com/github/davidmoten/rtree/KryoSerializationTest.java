package com.github.davidmoten.rtree;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.github.davidmoten.rtree.kryo.SerializerKryo;
import org.junit.Ignore;
import org.junit.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;

public class KryoSerializationTest {

    @Test
    public void testRTree() {
        Serializer<String, Point> serializer = SerializerKryo.create(null, null);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        RTree<String, Point> tree = RTree.<String, Point> create()
                .add(Entries.entry("thing", Geometries.point(10, 20)))
                .add(Entries.entry("monster", Geometries.point(23, 45)));
        try {
            serializer.write(tree, bytes);
            InputStream input = new ByteArrayInputStream(bytes.toByteArray());
            RTree<String, Point> tree2 = serializer.read(input, 0, null);
            System.out.println("kryo deserialized: " + tree2.asString());
            assertEquals(2, (int) tree2.entries().count().toBlocking().single());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testKryo() {
        Kryo kryo = new Kryo();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Output output = new Output(bytes);
        Boo b = new Boo("hello");
        kryo.writeObject(output, b);
        output.close();
        Input input = new Input(new ByteArrayInputStream(bytes.toByteArray()));
        Boo b2 = kryo.readObject(input, Boo.class);
        assertEquals("hello", b2.name);
    }

    public static class Boo {

        public final String name;

        private Boo() {
            this("boo");
        }

        public Boo(String name) {
            this.name = name;
        }
    }

}
