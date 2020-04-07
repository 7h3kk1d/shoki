package com.jnape.palatable.shoki.api;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.shoki.api.Natural.NonZero;
import com.jnape.palatable.shoki.api.Natural.Zero;
import org.junit.Test;

import java.math.BigInteger;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Downcast.downcast;
import static com.jnape.palatable.lambda.functions.builtin.fn2.CmpEq.cmpEq;
import static com.jnape.palatable.lambda.functions.builtin.fn2.GT.gt;
import static com.jnape.palatable.lambda.functions.builtin.fn2.LT.lt;
import static com.jnape.palatable.shoki.api.Natural.one;
import static com.jnape.palatable.shoki.api.Natural.zero;
import static java.math.BigInteger.ONE;
import static java.math.BigInteger.TEN;
import static java.math.BigInteger.ZERO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class NaturalTest {

    @Test
    public void value() {
        assertEquals(ZERO, zero().bigIntegerValue());
        assertEquals(ONE, Natural.abs(ONE).projectB().orElseThrow(AssertionError::new).bigIntegerValue());
    }

    @Test
    public void number() {
        Zero zero = zero();
        assertEquals((byte) 0, zero.byteValue());
        assertEquals((short) 0, zero.shortValue());
        assertEquals(0, zero.intValue());
        assertEquals(0L, zero.longValue());
        assertEquals(0F, zero.floatValue(), 0F);
        assertEquals(0D, zero.doubleValue(), 0D);

        NonZero nonZero = new NonZero.B(ONE);
        assertEquals((byte) 1, nonZero.byteValue());
        assertEquals((short) 1, nonZero.shortValue());
        assertEquals(1, nonZero.intValue());
        assertEquals(1L, nonZero.longValue());
        assertEquals(1F, nonZero.floatValue(), 0F);
        assertEquals(1D, nonZero.doubleValue(), 0D);
    }

    @Test
    public void zeroSingleton() {
        assertSame(zero(), zero());
    }

    @Test
    public void natural() {
        assertEquals(just(zero()), Natural.natural(ZERO));
        assertEquals(just(new NonZero.B(ONE)), Natural.natural(ONE));
        assertEquals(nothing(), Natural.natural(ONE.negate()));

        assertEquals(just(1), Natural.natural(1).<NonZero>fmap(downcast()).fmap(NonZero::value));
        assertEquals(just(1), Natural.natural(1L).<NonZero>fmap(downcast()).fmap(NonZero::value));
        assertEquals(just(1), Natural.natural(ONE).<NonZero>fmap(downcast()).fmap(NonZero::value));

        assertEquals(just(Integer.MAX_VALUE + 1L),
                     Natural.natural(Integer.MAX_VALUE + 1L).<NonZero>fmap(downcast()).fmap(NonZero::value));
        assertEquals(just(Integer.MAX_VALUE + 1L),
                     Natural.natural(BigInteger.valueOf(Integer.MAX_VALUE + 1L))
                             .<NonZero>fmap(downcast())
                             .fmap(NonZero::value));

        assertEquals(just(BigInteger.valueOf(Long.MAX_VALUE).add(ONE)),
                     Natural.natural(BigInteger.valueOf(Long.MAX_VALUE).add(ONE))
                             .<NonZero>fmap(downcast())
                             .fmap(NonZero::value));
    }

    @Test
    public void abs() {
        assertEquals(zero(), Natural.abs(ZERO));
        assertEquals(zero(), Natural.abs(ZERO.negate()));
        assertEquals(zero(), Natural.abs(BigInteger.valueOf(0L).negate()));
        assertEquals(zero(), Natural.abs(new BigInteger("0")));
        assertEquals(new NonZero.B(ONE), Natural.abs(ONE));
        assertEquals(new NonZero.B(ONE), Natural.abs(ONE.negate()));

        assertEquals(1, ((NonZero) Natural.abs(1)).value());
        assertEquals(1, ((NonZero) Natural.abs(1L)).value());
        assertEquals(1, ((NonZero) Natural.abs(ONE)).value());

        assertEquals(Integer.MAX_VALUE + 1L, ((NonZero) Natural.abs(Integer.MAX_VALUE + 1L)).value());
        assertEquals(Integer.MAX_VALUE + 1L,
                     ((NonZero) Natural.abs(BigInteger.valueOf(Integer.MAX_VALUE + 1L))).value());

        assertEquals(BigInteger.valueOf(Long.MAX_VALUE).add(ONE),
                     ((NonZero) Natural.abs(BigInteger.valueOf(Long.MAX_VALUE).add(ONE))).value());
    }

    @Test
    public void clampZero() {
        assertEquals(zero(), Natural.clampZero(ZERO));
        assertEquals(new NonZero.B(ONE), Natural.clampZero(ONE));
        assertEquals(zero(), Natural.clampZero(ONE.negate()));

        assertEquals(1, ((NonZero) Natural.clampZero(1)).value());
        assertEquals(1, ((NonZero) Natural.clampZero(1L)).value());
        assertEquals(1, ((NonZero) Natural.clampZero(ONE)).value());

        assertEquals(Integer.MAX_VALUE + 1L, ((NonZero) Natural.clampZero(Integer.MAX_VALUE + 1L)).value());
        assertEquals(Integer.MAX_VALUE + 1L,
                     ((NonZero) Natural.clampZero(BigInteger.valueOf(Integer.MAX_VALUE + 1L))).value());

        assertEquals(BigInteger.valueOf(Long.MAX_VALUE).add(ONE),
                     ((NonZero) Natural.clampZero(BigInteger.valueOf(Long.MAX_VALUE).add(ONE))).value());

    }

    @Test
    public void clampOne() {
        assertEquals(new NonZero.B(ONE), Natural.clampOne(ZERO));
        assertEquals(new NonZero.B(ONE), Natural.clampOne(ONE));
        assertEquals(new NonZero.B(TEN), Natural.clampOne(TEN));
        assertEquals(new NonZero.B(ONE), Natural.clampOne(ONE.negate()));

        assertEquals(2, Natural.clampOne(2).value());
        assertEquals(2, Natural.clampOne(2L).value());
        assertEquals(2, Natural.clampOne(ONE.add(ONE)).value());

        assertEquals(Integer.MAX_VALUE + 1L, Natural.clampOne(Integer.MAX_VALUE + 1L).value());
        assertEquals(Integer.MAX_VALUE + 1L, Natural.clampOne(BigInteger.valueOf(Integer.MAX_VALUE + 1L)).value());

        assertEquals(BigInteger.valueOf(Long.MAX_VALUE).add(ONE),
                     Natural.clampOne(BigInteger.valueOf(Long.MAX_VALUE).add(ONE)).value());
    }

    @Test
    public void addition() {
        Natural zero      = Natural.zero();
        Natural one       = Natural.one();
        Natural stillZero = zero.plus(zero);
        Natural stillOne  = one.plus(zero);
        Natural alsoOne   = zero.plus(one);

        assertEquals(zero, stillZero);
        assertEquals(one, stillOne);
        assertEquals(stillOne, alsoOne);
    }

    @Test
    public void specializedZeroAddition() {
        Zero    zero      = Natural.zero();
        NonZero one       = Natural.one();
        Zero    stillZero = zero.plus(zero);
        NonZero stillOne  = one.plus(zero);
        NonZero alsoOne   = zero.plus(one);
        assertEquals(zero, stillZero);
        assertEquals(one, stillOne);
        assertEquals(stillOne, alsoOne);
    }

    @Test
    public void specializedNonZeroAddition() {
        Zero    zero     = Natural.zero();
        NonZero one      = Natural.one();
        NonZero stillOne = one.plus(zero);
        NonZero alsoOne  = zero.plus(one);
        assertEquals(one, stillOne);
        assertEquals(stillOne, alsoOne);

        assertEquals(Integer.MAX_VALUE, one.plus(new NonZero.I(Integer.MAX_VALUE - 1)).value());
        assertEquals(Integer.MAX_VALUE + 1L, one.plus(new NonZero.I(Integer.MAX_VALUE)).value());
        assertEquals(Integer.MAX_VALUE + 2L, one.plus(new NonZero.L(Integer.MAX_VALUE + 1L)).value());
        assertEquals(BigInteger.valueOf(Long.MAX_VALUE).add(ONE), one.plus(new NonZero.L(Long.MAX_VALUE)).value());
        assertEquals(BigInteger.valueOf(Long.MAX_VALUE).add(ONE).add(ONE),
                     one.plus(new NonZero.B(BigInteger.valueOf(Long.MAX_VALUE).add(ONE))).value());
    }

    @Test
    public void subtraction() {
        Natural        zero         = Natural.zero();
        Natural        one          = Natural.one();
        Maybe<Natural> stillZero    = zero.minus(zero);
        Maybe<Natural> lessThanZero = zero.minus(one);
        Maybe<Natural> stillOne     = one.minus(zero);

        assertEquals(just(zero), stillZero);
        assertEquals(nothing(), lessThanZero);
        assertEquals(just(one), stillOne);
        assertEquals(zero, zero.minus(zero()));
    }

    @Test
    public void specializedZeroSubtraction() {
        Zero           zero         = Natural.zero();
        NonZero        one          = Natural.one();
        Zero           stillZero    = zero.minus(zero);
        Maybe<Natural> lessThanZero = zero.minus(one);

        assertEquals(zero, stillZero);
        assertEquals(nothing(), lessThanZero);
    }

    @Test
    public void specializedNonZeroSubtraction() {
        Zero           zero         = Natural.zero();
        NonZero        one          = Natural.one();
        NonZero        stillOne     = one.minus(zero);
        Maybe<Natural> alsoZero     = one.minus(one);
        Maybe<Natural> lessThanZero = one.minus(one.plus(one));

        assertEquals(one, stillOne);
        assertEquals(just(zero), alsoZero);
        assertEquals(nothing(), lessThanZero);

        assertEquals(just(1), one.plus(one).minus(one).<NonZero>fmap(downcast()).fmap(NonZero::value));
        assertEquals(just(Integer.MAX_VALUE),
                     one.plus(Natural.clampZero(Integer.MAX_VALUE)).minus(one)
                             .<NonZero>fmap(downcast())
                             .fmap(NonZero::value));
        assertEquals(just(Integer.MAX_VALUE + 1L),
                     one.plus(Natural.clampZero(Integer.MAX_VALUE + 1L)).minus(one)
                             .<NonZero>fmap(downcast())
                             .fmap(NonZero::value));
        assertEquals(just(Long.MAX_VALUE),
                     one.plus(Natural.clampZero(Long.MAX_VALUE)).minus(one)
                             .<NonZero>fmap(downcast())
                             .fmap(NonZero::value));
        assertEquals(just(BigInteger.valueOf(Long.MAX_VALUE).add(ONE)),
                     one.plus(Natural.clampZero(BigInteger.valueOf(Long.MAX_VALUE).add(ONE))).minus(one)
                             .<NonZero>fmap(downcast())
                             .fmap(NonZero::value));

        assertEquals(nothing(), Natural.abs(Integer.MAX_VALUE).minus(Natural.abs(Integer.MAX_VALUE).inc()));
        assertEquals(nothing(), Natural.abs(Long.MAX_VALUE).minus(Natural.abs(Long.MAX_VALUE).inc()));
    }

    @Test
    public void nonZeroAdditionNeverOverflows() {
        assertEquals(127, new NonZero.I(Byte.MAX_VALUE - 1).plus(one()).value());
        assertEquals(128, new NonZero.I(Byte.MAX_VALUE).plus(one()).value());
        assertEquals(32767, new NonZero.I(Short.MAX_VALUE - 1).plus(one()).value());
        assertEquals(32768, new NonZero.I(Short.MAX_VALUE).plus(one()).value());
        assertEquals(Integer.MAX_VALUE, new NonZero.I(Integer.MAX_VALUE - 1).plus(one()).value());
        assertEquals(Integer.MAX_VALUE + 1L, new NonZero.I(Integer.MAX_VALUE).plus(one()).value());
        assertEquals(Long.MAX_VALUE, new NonZero.L(Long.MAX_VALUE - 1).plus(one()).value());
        assertEquals(BigInteger.valueOf(Long.MAX_VALUE).add(ONE), new NonZero.L(Long.MAX_VALUE).plus(one()).value());

        assertEquals(BigInteger.valueOf(Long.MAX_VALUE).add(ONE)
                             .add(BigInteger.valueOf(Long.MAX_VALUE).add(ONE)),
                     new NonZero.B(BigInteger.valueOf(Long.MAX_VALUE).add(ONE))
                             .plus(new NonZero.B(BigInteger.valueOf(Long.MAX_VALUE).add(ONE)))
                             .value());
    }

    @Test
    public void inc() {
        assertEquals(one(), zero().inc());
        assertEquals(Natural.abs(2), one().inc());
    }

    @Test
    public void dec() {
        assertEquals(nothing(), zero().dec());
        assertEquals(just(zero()), one().dec());
        assertEquals(just(one()), Natural.abs(2).dec());
    }

    @Test
    public void equalsAndHashCode() {
        assertEquals(zero(), zero());
        assertEquals(new NonZero.B(ONE), new NonZero.B(ONE));
        assertNotEquals(zero(), new NonZero.B(ONE));
        assertNotEquals(new NonZero.B(ONE), zero());
        assertNotEquals(new NonZero.B(ONE), new NonZero.B(BigInteger.valueOf(2)));
        assertNotEquals(new NonZero.B(ONE), new Object());

        assertTrue(zero().hashCode() < one().hashCode());
        assertTrue(one().hashCode() < one().plus(one()).hashCode());
        assertEquals(zero().hashCode(), zero().hashCode());
        assertEquals(new NonZero.B(ONE).hashCode(), new NonZero.B(ONE).hashCode());
        assertNotEquals(new NonZero.B(ONE).hashCode(), new NonZero.B(BigInteger.valueOf(2)).hashCode());

        assertEquals(new NonZero.B(ONE).hashCode(), new NonZero.L(1L).hashCode());
        assertEquals(new NonZero.L(1L).hashCode(), new NonZero.I(1).hashCode());
    }

    @Test
    public void comparison() {
        assertTrue(lt(one(), zero()));
        assertTrue(gt(zero(), one()));
        assertTrue(cmpEq(zero(), zero()));
    }

    @Test
    public void toStringIsUseful() {
        assertEquals("Zero{}", zero().toString());
        assertEquals("NonZero{value=1}", new NonZero.B(BigInteger.valueOf(1)).toString());
    }
}