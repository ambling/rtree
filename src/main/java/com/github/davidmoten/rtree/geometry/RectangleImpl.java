package com.github.davidmoten.rtree.geometry;

import com.github.davidmoten.guavamini.Objects;
import com.github.davidmoten.guavamini.Optional;
import com.github.davidmoten.guavamini.Preconditions;
import com.github.davidmoten.rtree.internal.util.ObjectsHelper;

final class RectangleImpl extends Rectangle {
    private final float x1, y1, x2, y2;

    protected RectangleImpl(float x1, float y1, float x2, float y2) {
        Preconditions.checkArgument(x2 >= x1);
        Preconditions.checkArgument(y2 >= y1);
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.davidmoten.rtree.geometry.RectangleI#x1()
     */
    @Override
    public float x1() {
        return x1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.davidmoten.rtree.geometry.RectangleI#y1()
     */
    @Override
    public float y1() {
        return y1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.davidmoten.rtree.geometry.RectangleI#x2()
     */
    @Override
    public float x2() {
        return x2;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.davidmoten.rtree.geometry.RectangleI#y2()
     */
    @Override
    public float y2() {
        return y2;
    }

    @Override
    public boolean equals(Object obj) {
        Optional<RectangleImpl> other = ObjectsHelper.asClass(obj, RectangleImpl.class);
        if (other.isPresent()) {
            return Objects.equal(x1, other.get().x1) && Objects.equal(x2, other.get().x2)
                    && Objects.equal(y1, other.get().y1) && Objects.equal(y2, other.get().y2);
        } else
            return false;
    }

}