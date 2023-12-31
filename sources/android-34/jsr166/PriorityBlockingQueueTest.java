/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 * Other contributors include Andrew Wright, Jeffrey Hayes,
 * Pat Fisher, Mike Judd.
 */

package jsr166;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;

import junit.framework.Test;

public class PriorityBlockingQueueTest extends JSR166TestCase {

    // android-note: These tests have been moved into their own separate
    // classes to work around CTS issues.
    //
    // public static class Generic extends BlockingQueueTest {
    //     protected BlockingQueue emptyCollection() {
    //         return new PriorityBlockingQueue();
    //     }
    // }

    // public static class InitialCapacity extends BlockingQueueTest {
    //     protected BlockingQueue emptyCollection() {
    //         return new PriorityBlockingQueue(SIZE);
    //     }
    // }

    // android-note: Removed because the CTS runner does a bad job of
    // retrying tests that have suite() declarations.
    //
    // public static void main(String[] args) {
    //     main(suite(), args);
    // }
    // public static Test suite() {
    //     return newTestSuite(PriorityBlockingQueueTest.class,
    //                         new Generic().testSuite(),
    //                         new InitialCapacity().testSuite());
    // }

    /** Sample Comparator */
    static class MyReverseComparator implements Comparator {
        public int compare(Object x, Object y) {
            return ((Comparable)y).compareTo(x);
        }
    }

    /**
     * Returns a new queue of given size containing consecutive
     * Integers 0 ... n.
     */
    private PriorityBlockingQueue<Integer> populatedQueue(int n) {
        PriorityBlockingQueue<Integer> q =
            new PriorityBlockingQueue<Integer>(n);
        assertTrue(q.isEmpty());
        for (int i = n - 1; i >= 0; i -= 2)
            assertTrue(q.offer(Integer.valueOf(i)));
        for (int i = (n & 1); i < n; i += 2)
            assertTrue(q.offer(Integer.valueOf(i)));
        assertFalse(q.isEmpty());
        assertEquals(Integer.MAX_VALUE, q.remainingCapacity());
        assertEquals(n, q.size());
        return q;
    }

    /**
     * A new queue has unbounded capacity
     */
    public void testConstructor1() {
        assertEquals(Integer.MAX_VALUE,
                     new PriorityBlockingQueue(SIZE).remainingCapacity());
    }

    /**
     * Constructor throws IAE if capacity argument nonpositive
     */
    public void testConstructor2() {
        try {
            new PriorityBlockingQueue(0);
            shouldThrow();
        } catch (IllegalArgumentException success) {}
    }

    /**
     * Initializing from null Collection throws NPE
     */
    public void testConstructor3() {
        try {
            new PriorityBlockingQueue(null);
            shouldThrow();
        } catch (NullPointerException success) {}
    }

    /**
     * Initializing from Collection of null elements throws NPE
     */
    public void testConstructor4() {
        Collection<Integer> elements = Arrays.asList(new Integer[SIZE]);
        try {
            new PriorityBlockingQueue(elements);
            shouldThrow();
        } catch (NullPointerException success) {}
    }

    /**
     * Initializing from Collection with some null elements throws NPE
     */
    public void testConstructor5() {
        Integer[] ints = new Integer[SIZE];
        for (int i = 0; i < SIZE - 1; ++i)
            ints[i] = i;
        Collection<Integer> elements = Arrays.asList(ints);
        try {
            new PriorityBlockingQueue(elements);
            shouldThrow();
        } catch (NullPointerException success) {}
    }

    /**
     * Queue contains all elements of collection used to initialize
     */
    public void testConstructor6() {
        Integer[] ints = new Integer[SIZE];
        for (int i = 0; i < SIZE; ++i)
            ints[i] = i;
        PriorityBlockingQueue q = new PriorityBlockingQueue(Arrays.asList(ints));
        for (int i = 0; i < SIZE; ++i)
            assertEquals(ints[i], q.poll());
    }

    /**
     * The comparator used in constructor is used
     */
    public void testConstructor7() {
        MyReverseComparator cmp = new MyReverseComparator();
        PriorityBlockingQueue q = new PriorityBlockingQueue(SIZE, cmp);
        assertEquals(cmp, q.comparator());
        Integer[] ints = new Integer[SIZE];
        for (int i = 0; i < SIZE; ++i)
            ints[i] = Integer.valueOf(i);
        q.addAll(Arrays.asList(ints));
        for (int i = SIZE - 1; i >= 0; --i)
            assertEquals(ints[i], q.poll());
    }

    /**
     * isEmpty is true before add, false after
     */
    public void testEmpty() {
        PriorityBlockingQueue q = new PriorityBlockingQueue(2);
        assertTrue(q.isEmpty());
        assertEquals(Integer.MAX_VALUE, q.remainingCapacity());
        q.add(one);
        assertFalse(q.isEmpty());
        q.add(two);
        q.remove();
        q.remove();
        assertTrue(q.isEmpty());
    }

    /**
     * remainingCapacity() always returns Integer.MAX_VALUE
     */
    public void testRemainingCapacity() {
        BlockingQueue q = populatedQueue(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(Integer.MAX_VALUE, q.remainingCapacity());
            assertEquals(SIZE - i, q.size());
            assertEquals(i, q.remove());
        }
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(Integer.MAX_VALUE, q.remainingCapacity());
            assertEquals(i, q.size());
            assertTrue(q.add(i));
        }
    }

    /**
     * Offer of comparable element succeeds
     */
    public void testOffer() {
        PriorityBlockingQueue q = new PriorityBlockingQueue(1);
        assertTrue(q.offer(zero));
        assertTrue(q.offer(one));
    }

    /**
     * Offer of non-Comparable throws CCE
     */
    public void testOfferNonComparable() {
        PriorityBlockingQueue q = new PriorityBlockingQueue(1);
        try {
            q.offer(new Object());
            q.offer(new Object());
            shouldThrow();
        } catch (ClassCastException success) {}
    }

    /**
     * add of comparable succeeds
     */
    public void testAdd() {
        PriorityBlockingQueue q = new PriorityBlockingQueue(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, q.size());
            assertTrue(q.add(Integer.valueOf(i)));
        }
    }

    /**
     * addAll(this) throws IAE
     */
    public void testAddAllSelf() {
        PriorityBlockingQueue q = populatedQueue(SIZE);
        try {
            q.addAll(q);
            shouldThrow();
        } catch (IllegalArgumentException success) {}
    }

    /**
     * addAll of a collection with any null elements throws NPE after
     * possibly adding some elements
     */
    public void testAddAll3() {
        PriorityBlockingQueue q = new PriorityBlockingQueue(SIZE);
        Integer[] ints = new Integer[SIZE];
        for (int i = 0; i < SIZE - 1; ++i)
            ints[i] = Integer.valueOf(i);
        try {
            q.addAll(Arrays.asList(ints));
            shouldThrow();
        } catch (NullPointerException success) {}
    }

    /**
     * Queue contains all elements of successful addAll
     */
    public void testAddAll5() {
        Integer[] empty = new Integer[0];
        Integer[] ints = new Integer[SIZE];
        for (int i = SIZE - 1; i >= 0; --i)
            ints[i] = Integer.valueOf(i);
        PriorityBlockingQueue q = new PriorityBlockingQueue(SIZE);
        assertFalse(q.addAll(Arrays.asList(empty)));
        assertTrue(q.addAll(Arrays.asList(ints)));
        for (int i = 0; i < SIZE; ++i)
            assertEquals(ints[i], q.poll());
    }

    /**
     * all elements successfully put are contained
     */
    public void testPut() {
        PriorityBlockingQueue q = new PriorityBlockingQueue(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            Integer x = Integer.valueOf(i);
            q.put(x);
            assertTrue(q.contains(x));
        }
        assertEquals(SIZE, q.size());
    }

    /**
     * put doesn't block waiting for take
     */
    public void testPutWithTake() throws InterruptedException {
        final PriorityBlockingQueue q = new PriorityBlockingQueue(2);
        final int size = 4;
        Thread t = newStartedThread(new CheckedRunnable() {
            public void realRun() {
                for (int i = 0; i < size; i++)
                    q.put(Integer.valueOf(0));
            }});

        awaitTermination(t);
        assertEquals(size, q.size());
        q.take();
    }

    /**
     * timed offer does not time out
     */
    public void testTimedOffer() throws InterruptedException {
        final PriorityBlockingQueue q = new PriorityBlockingQueue(2);
        Thread t = newStartedThread(new CheckedRunnable() {
            public void realRun() {
                q.put(Integer.valueOf(0));
                q.put(Integer.valueOf(0));
                assertTrue(q.offer(Integer.valueOf(0), SHORT_DELAY_MS, MILLISECONDS));
                assertTrue(q.offer(Integer.valueOf(0), LONG_DELAY_MS, MILLISECONDS));
            }});

        awaitTermination(t);
    }

    /**
     * take retrieves elements in priority order
     */
    public void testTake() throws InterruptedException {
        PriorityBlockingQueue q = populatedQueue(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, q.take());
        }
    }

    /**
     * Take removes existing elements until empty, then blocks interruptibly
     */
    public void testBlockingTake() throws InterruptedException {
        final PriorityBlockingQueue q = populatedQueue(SIZE);
        final CountDownLatch pleaseInterrupt = new CountDownLatch(1);
        Thread t = newStartedThread(new CheckedRunnable() {
            public void realRun() throws InterruptedException {
                for (int i = 0; i < SIZE; ++i) {
                    assertEquals(i, q.take());
                }

                Thread.currentThread().interrupt();
                try {
                    q.take();
                    shouldThrow();
                } catch (InterruptedException success) {}
                assertFalse(Thread.interrupted());

                pleaseInterrupt.countDown();
                try {
                    q.take();
                    shouldThrow();
                } catch (InterruptedException success) {}
                assertFalse(Thread.interrupted());
            }});

        await(pleaseInterrupt);
        assertThreadStaysAlive(t);
        t.interrupt();
        awaitTermination(t);
    }

    /**
     * poll succeeds unless empty
     */
    public void testPoll() {
        PriorityBlockingQueue q = populatedQueue(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, q.poll());
        }
        assertNull(q.poll());
    }

    /**
     * timed poll with zero timeout succeeds when non-empty, else times out
     */
    public void testTimedPoll0() throws InterruptedException {
        PriorityBlockingQueue q = populatedQueue(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, q.poll(0, MILLISECONDS));
        }
        assertNull(q.poll(0, MILLISECONDS));
    }

    /**
     * timed poll with nonzero timeout succeeds when non-empty, else times out
     */
    public void testTimedPoll() throws InterruptedException {
        PriorityBlockingQueue<Integer> q = populatedQueue(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            long startTime = System.nanoTime();
            assertEquals(i, (int) q.poll(LONG_DELAY_MS, MILLISECONDS));
            assertTrue(millisElapsedSince(startTime) < LONG_DELAY_MS);
        }
        long startTime = System.nanoTime();
        assertNull(q.poll(timeoutMillis(), MILLISECONDS));
        assertTrue(millisElapsedSince(startTime) >= timeoutMillis());
        checkEmpty(q);
    }

    /**
     * Interrupted timed poll throws InterruptedException instead of
     * returning timeout status
     */
    public void testInterruptedTimedPoll() throws InterruptedException {
        final BlockingQueue<Integer> q = populatedQueue(SIZE);
        final CountDownLatch aboutToWait = new CountDownLatch(1);
        Thread t = newStartedThread(new CheckedRunnable() {
            public void realRun() throws InterruptedException {
                long startTime = System.nanoTime();
                for (int i = 0; i < SIZE; ++i) {
                    assertEquals(i, (int) q.poll(LONG_DELAY_MS, MILLISECONDS));
                }
                aboutToWait.countDown();
                try {
                    q.poll(LONG_DELAY_MS, MILLISECONDS);
                    shouldThrow();
                } catch (InterruptedException success) {
                    assertTrue(millisElapsedSince(startTime) < LONG_DELAY_MS);
                }
            }});

        aboutToWait.await();
        waitForThreadToEnterWaitState(t, LONG_DELAY_MS);
        t.interrupt();
        awaitTermination(t);
    }

    /**
     * peek returns next element, or null if empty
     */
    public void testPeek() {
        PriorityBlockingQueue q = populatedQueue(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, q.peek());
            assertEquals(i, q.poll());
            assertTrue(q.peek() == null ||
                       !q.peek().equals(i));
        }
        assertNull(q.peek());
    }

    /**
     * element returns next element, or throws NSEE if empty
     */
    public void testElement() {
        PriorityBlockingQueue q = populatedQueue(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, q.element());
            assertEquals(i, q.poll());
        }
        try {
            q.element();
            shouldThrow();
        } catch (NoSuchElementException success) {}
    }

    /**
     * remove removes next element, or throws NSEE if empty
     */
    public void testRemove() {
        PriorityBlockingQueue q = populatedQueue(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, q.remove());
        }
        try {
            q.remove();
            shouldThrow();
        } catch (NoSuchElementException success) {}
    }

    /**
     * contains(x) reports true when elements added but not yet removed
     */
    public void testContains() {
        PriorityBlockingQueue q = populatedQueue(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertTrue(q.contains(Integer.valueOf(i)));
            q.poll();
            assertFalse(q.contains(Integer.valueOf(i)));
        }
    }

    /**
     * clear removes all elements
     */
    public void testClear() {
        PriorityBlockingQueue q = populatedQueue(SIZE);
        q.clear();
        assertTrue(q.isEmpty());
        assertEquals(0, q.size());
        q.add(one);
        assertFalse(q.isEmpty());
        assertTrue(q.contains(one));
        q.clear();
        assertTrue(q.isEmpty());
    }

    /**
     * containsAll(c) is true when c contains a subset of elements
     */
    public void testContainsAll() {
        PriorityBlockingQueue q = populatedQueue(SIZE);
        PriorityBlockingQueue p = new PriorityBlockingQueue(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertTrue(q.containsAll(p));
            assertFalse(p.containsAll(q));
            p.add(Integer.valueOf(i));
        }
        assertTrue(p.containsAll(q));
    }

    /**
     * retainAll(c) retains only those elements of c and reports true if changed
     */
    public void testRetainAll() {
        PriorityBlockingQueue q = populatedQueue(SIZE);
        PriorityBlockingQueue p = populatedQueue(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            boolean changed = q.retainAll(p);
            if (i == 0)
                assertFalse(changed);
            else
                assertTrue(changed);

            assertTrue(q.containsAll(p));
            assertEquals(SIZE - i, q.size());
            p.remove();
        }
    }

    /**
     * removeAll(c) removes only those elements of c and reports true if changed
     */
    public void testRemoveAll() {
        for (int i = 1; i < SIZE; ++i) {
            PriorityBlockingQueue q = populatedQueue(SIZE);
            PriorityBlockingQueue p = populatedQueue(i);
            assertTrue(q.removeAll(p));
            assertEquals(SIZE - i, q.size());
            for (int j = 0; j < i; ++j) {
                Integer x = (Integer)(p.remove());
                assertFalse(q.contains(x));
            }
        }
    }

    /**
     * toArray contains all elements
     */
    public void testToArray() throws InterruptedException {
        PriorityBlockingQueue q = populatedQueue(SIZE);
        Object[] o = q.toArray();
        Arrays.sort(o);
        for (int i = 0; i < o.length; i++)
            assertSame(o[i], q.take());
    }

    /**
     * toArray(a) contains all elements
     */
    public void testToArray2() throws InterruptedException {
        PriorityBlockingQueue<Integer> q = populatedQueue(SIZE);
        Integer[] ints = new Integer[SIZE];
        Integer[] array = q.toArray(ints);
        assertSame(ints, array);
        Arrays.sort(ints);
        for (int i = 0; i < ints.length; i++)
            assertSame(ints[i], q.take());
    }

    /**
     * toArray(incompatible array type) throws ArrayStoreException
     */
    public void testToArray1_BadArg() {
        PriorityBlockingQueue q = populatedQueue(SIZE);
        try {
            q.toArray(new String[10]);
            shouldThrow();
        } catch (ArrayStoreException success) {}
    }

    /**
     * iterator iterates through all elements
     */
    public void testIterator() {
        PriorityBlockingQueue q = populatedQueue(SIZE);
        Iterator it = q.iterator();
        int i;
        for (i = 0; it.hasNext(); i++)
            assertTrue(q.contains(it.next()));
        assertEquals(i, SIZE);
        assertIteratorExhausted(it);
    }

    /**
     * iterator of empty collection has no elements
     */
    public void testEmptyIterator() {
        assertIteratorExhausted(new PriorityBlockingQueue().iterator());
    }

    /**
     * iterator.remove removes current element
     */
    public void testIteratorRemove() {
        final PriorityBlockingQueue q = new PriorityBlockingQueue(3);
        q.add(Integer.valueOf(2));
        q.add(Integer.valueOf(1));
        q.add(Integer.valueOf(3));

        Iterator it = q.iterator();
        it.next();
        it.remove();

        it = q.iterator();
        assertEquals(it.next(), Integer.valueOf(2));
        assertEquals(it.next(), Integer.valueOf(3));
        assertFalse(it.hasNext());
    }

    /**
     * toString contains toStrings of elements
     */
    public void testToString() {
        PriorityBlockingQueue q = populatedQueue(SIZE);
        String s = q.toString();
        for (int i = 0; i < SIZE; ++i) {
            assertTrue(s.contains(String.valueOf(i)));
        }
    }

    /**
     * timed poll transfers elements across Executor tasks
     */
    public void testPollInExecutor() {
        final PriorityBlockingQueue q = new PriorityBlockingQueue(2);
        final CheckedBarrier threadsStarted = new CheckedBarrier(2);
        final ExecutorService executor = Executors.newFixedThreadPool(2);
        try (PoolCleaner cleaner = cleaner(executor)) {
            executor.execute(new CheckedRunnable() {
                public void realRun() throws InterruptedException {
                    assertNull(q.poll());
                    threadsStarted.await();
                    assertSame(one, q.poll(LONG_DELAY_MS, MILLISECONDS));
                    checkEmpty(q);
                }});

            executor.execute(new CheckedRunnable() {
                public void realRun() throws InterruptedException {
                    threadsStarted.await();
                    q.put(one);
                }});
        }
    }

    /**
     * A deserialized serialized queue has same elements
     */
    public void testSerialization() throws Exception {
        Queue x = populatedQueue(SIZE);
        Queue y = serialClone(x);

        assertNotSame(x, y);
        assertEquals(x.size(), y.size());
        while (!x.isEmpty()) {
            assertFalse(y.isEmpty());
            assertEquals(x.remove(), y.remove());
        }
        assertTrue(y.isEmpty());
    }

    /**
     * drainTo(c) empties queue into another collection c
     */
    public void testDrainTo() {
        PriorityBlockingQueue q = populatedQueue(SIZE);
        ArrayList l = new ArrayList();
        q.drainTo(l);
        assertEquals(0, q.size());
        assertEquals(SIZE, l.size());
        for (int i = 0; i < SIZE; ++i)
            assertEquals(l.get(i), Integer.valueOf(i));
        q.add(zero);
        q.add(one);
        assertFalse(q.isEmpty());
        assertTrue(q.contains(zero));
        assertTrue(q.contains(one));
        l.clear();
        q.drainTo(l);
        assertEquals(0, q.size());
        assertEquals(2, l.size());
        for (int i = 0; i < 2; ++i)
            assertEquals(l.get(i), Integer.valueOf(i));
    }

    /**
     * drainTo empties queue
     */
    public void testDrainToWithActivePut() throws InterruptedException {
        final PriorityBlockingQueue q = populatedQueue(SIZE);
        Thread t = new Thread(new CheckedRunnable() {
            public void realRun() {
                q.put(Integer.valueOf(SIZE + 1));
            }});

        t.start();
        ArrayList l = new ArrayList();
        q.drainTo(l);
        assertTrue(l.size() >= SIZE);
        for (int i = 0; i < SIZE; ++i)
            assertEquals(l.get(i), Integer.valueOf(i));
        t.join();
        assertTrue(q.size() + l.size() >= SIZE);
    }

    /**
     * drainTo(c, n) empties first min(n, size) elements of queue into c
     */
    public void testDrainToN() {
        PriorityBlockingQueue q = new PriorityBlockingQueue(SIZE * 2);
        for (int i = 0; i < SIZE + 2; ++i) {
            for (int j = 0; j < SIZE; j++)
                assertTrue(q.offer(Integer.valueOf(j)));
            ArrayList l = new ArrayList();
            q.drainTo(l, i);
            int k = (i < SIZE) ? i : SIZE;
            assertEquals(k, l.size());
            assertEquals(SIZE - k, q.size());
            for (int j = 0; j < k; ++j)
                assertEquals(l.get(j), Integer.valueOf(j));
            do {} while (q.poll() != null);
        }
    }

    /**
     * remove(null), contains(null) always return false
     */
    public void testNeverContainsNull() {
        Collection<?>[] qs = {
            new PriorityBlockingQueue<Object>(),
            populatedQueue(2),
        };

        for (Collection<?> q : qs) {
            assertFalse(q.contains(null));
            assertFalse(q.remove(null));
        }
    }

}
