package com.github.davidmoten.rtree.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.davidmoten.rtree.InternalStructure;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.Serializer;
import com.github.davidmoten.rtree.geometry.Geometry;
import rx.functions.Func0;
import rx.functions.Func1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerializerKryo<T, S extends Geometry> implements Serializer<T, S> {

	private final Func0<Kryo> kryoFactory;

	public SerializerKryo(Func0<Kryo> kryoFactory) {
		this.kryoFactory = kryoFactory;
	}

	@Override
	public void write(RTree<T, S> tree, OutputStream os) throws IOException {
		Output output = new Output(os);
		Kryo kryo = kryoFactory.call();
		kryo.writeClassAndObject(output, tree);
		output.flush();
		output.close();
	}

	@Override
	@SuppressWarnings("unchecked")
	public RTree<T, S> read(InputStream is, long sizeBytes, InternalStructure structure) throws IOException {
		Input input = new Input(is);
		Kryo kryo = kryoFactory.call();
		input.close();
		return (RTree<T, S>) kryo.readClassAndObject(input);
	}

	public static <T, S extends Geometry> Serializer<T, S> create(Func1<? super T, byte[]> serializer,
			Func1<byte[], ? extends T> deserializer) {
		Func0<Kryo> factory = new KryoFactoryDefault<T, S>(serializer, deserializer);
		return new SerializerKryo<T, S>(factory);
	}

}
