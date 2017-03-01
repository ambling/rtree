package com.github.davidmoten.rtree;

import com.github.davidmoten.rtree.internal.ContextDefault;
import org.junit.Test;

import com.github.davidmoten.rtree.geometry.Geometry;

public class ContextTest {

    @Test(expected = RuntimeException.class)
    public void testContextIllegalMinChildren() {
        new ContextDefault<Object, Geometry>(0, 4, new SelectorMinimalAreaIncrease(),
                new SplitterQuadratic(), Factories.defaultFactory());
    }

    @Test(expected = RuntimeException.class)
    public void testContextIllegalMaxChildren() {
        new ContextDefault<Object, Geometry>(1, 2, new SelectorMinimalAreaIncrease(),
                new SplitterQuadratic(), Factories.defaultFactory());
    }

    @Test(expected = RuntimeException.class)
    public void testContextIllegalMinMaxChildren() {
        new ContextDefault<Object, Geometry>(4, 3, new SelectorMinimalAreaIncrease(),
                new SplitterQuadratic(), Factories.defaultFactory());
    }

    @Test
    public void testContextLegalChildren() {
        new ContextDefault<Object, Geometry>(2, 4, new SelectorMinimalAreaIncrease(),
                new SplitterQuadratic(), Factories.defaultFactory());
    }

    @Test(expected = NullPointerException.class)
    public void testContextSelectorNullThrowsNPE() {
        new ContextDefault<Object, Geometry>(2, 4, null, new SplitterQuadratic(),
                Factories.defaultFactory());
    }

    @Test(expected = NullPointerException.class)
    public void testContextSplitterNullThrowsNPE() {
        new ContextDefault<Object, Geometry>(2, 4, new SelectorMinimalAreaIncrease(), null,
                Factories.defaultFactory());
    }

    @Test(expected = NullPointerException.class)
    public void testContextNodeFactoryNullThrowsNPE() {
        new ContextDefault<Object, Geometry>(2, 4, new SelectorMinimalAreaIncrease(),
                new SplitterQuadratic(), null);
    }
}
