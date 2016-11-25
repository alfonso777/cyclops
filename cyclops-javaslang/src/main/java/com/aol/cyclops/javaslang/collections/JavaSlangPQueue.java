package com.aol.cyclops.javaslang.collections;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.jooq.lambda.tuple.Tuple2;
import org.pcollections.PQueue;


import com.aol.cyclops.Reducer;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.reactor.collections.extensions.persistent.LazyPOrderedSetX;
import com.aol.cyclops.reactor.collections.extensions.persistent.LazyPQueueX;

import javaslang.collection.Queue;
import javaslang.collection.SortedSet;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.Wither;
import reactor.core.publisher.Flux;
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JavaSlangPQueue<T> extends AbstractQueue<T> implements PQueue<T> {
    
    /**
     * Create a LazyPQueueX from a Stream
     * 
     * @param stream to construct a LazyQueueX from
     * @return LazyPQueueX
     */
    public static <T> LazyPQueueX<T> fromStream(Stream<T> stream) {
        return new LazyPQueueX<T>(
                                   Flux.from(ReactiveSeq.fromStream(stream)),toPQueue());
    }

    /**
     * Create a LazyPQueueX that contains the Integers between start and end
     * 
     * @param start
     *            Number of range to start from
     * @param end
     *            Number for range to end at
     * @return Range ListX
     */
    public static LazyPQueueX<Integer> range(int start, int end) {
        return fromStream(ReactiveSeq.range(start, end));
    }

    /**
     * Create a LazyPQueueX that contains the Longs between start and end
     * 
     * @param start
     *            Number of range to start from
     * @param end
     *            Number for range to end at
     * @return Range ListX
     */
    public static LazyPQueueX<Long> rangeLong(long start, long end) {
        return fromStream(ReactiveSeq.rangeLong(start, end));
    }

    /**
     * Unfold a function into a ListX
     * 
     * <pre>
     * {@code 
     *  LazyPQueueX.unfold(1,i->i<=6 ? Optional.of(Tuple.tuple(i,i+1)) : Optional.empty());
     * 
     * //(1,2,3,4,5)
     * 
     * }</pre>
     * 
     * @param seed Initial value 
     * @param unfolder Iteratively applied function, terminated by an empty Optional
     * @return ListX generated by unfolder function
     */
    public static <U, T> LazyPQueueX<T> unfold(U seed, Function<? super U, Optional<Tuple2<T, U>>> unfolder) {
        return fromStream(ReactiveSeq.unfold(seed, unfolder));
    }

    /**
     * Generate a LazyPQueueX from the provided Supplier up to the provided limit number of times
     * 
     * @param limit Max number of elements to generate
     * @param s Supplier to generate ListX elements
     * @return ListX generated from the provided Supplier
     */
    public static <T> LazyPQueueX<T> generate(long limit, Supplier<T> s) {

        return fromStream(ReactiveSeq.generate(s)
                                      .limit(limit));
    }

    /**
     * Create a LazyPQueueX by iterative application of a function to an initial element up to the supplied limit number of times
     * 
     * @param limit Max number of elements to generate
     * @param seed Initial element
     * @param f Iteratively applied to each element to generate the next element
     * @return ListX generated by iterative application
     */
    public static <T> LazyPQueueX<T> iterate(long limit, final T seed, final UnaryOperator<T> f) {
        return fromStream(ReactiveSeq.iterate(seed, f)
                                      .limit(limit));
    }

    /**
     * <pre>
     * {@code 
     * PQueue<Integer> q = JavaSlangPQueue.<Integer>toPQueue()
                                          .mapReduce(Stream.of(1,2,3,4));
     * 
     * }
     * </pre>
     * @return Reducer for PQueue
     */
    public static <T> Reducer<PQueue<T>> toPQueue() {
        return Reducer.<PQueue<T>> of(JavaSlangPQueue.empty(), (final PQueue<T> a) -> b -> a.plusAll(b), (final T x) -> JavaSlangPQueue.singleton(x));
    }
    
    public static <T> LazyPQueueX<T> empty(){
        return LazyPQueueX.fromPQueue(new JavaSlangPQueue<>(Queue.empty()),toPQueue());
       
    }
    public static <T> LazyPQueueX<T> singleton(T t){
        return  LazyPQueueX.fromPQueue(new JavaSlangPQueue<>(Queue.of(t)),toPQueue());
    }
    public static <T> LazyPQueueX<T> of(T... t){
       return  LazyPQueueX.fromPQueue(new JavaSlangPQueue<>(Queue.of(t)),toPQueue());
    }
    public static <T> LazyPQueueX<T> PQueue(Queue<T> q) {
        return LazyPQueueX.fromPQueue(new JavaSlangPQueue<>(q), toPQueue());
    }
    @SafeVarargs
    public static <T> LazyPQueueX<T> PQueue(T... elements){
        return LazyPQueueX.fromPQueue(of(elements),toPQueue());
    }
    @Wither
    private final Queue<T> list;

    @Override
    public PQueue<T> plus(T e) {
        return withList(list.prepend(e));
    }

    @Override
    public PQueue<T> plusAll(Collection<? extends T> l) {
        return withList(list.prependAll(l));
    }

  

    @Override
    public PQueue<T> minus(Object e) {
        return withList(list.remove((T)e));
    }

    @Override
    public PQueue<T> minusAll(Collection<?> l) {
        return withList(list.removeAll((Collection)l));
    }

   

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public T peek() {
       return list.peek();
    }

    @Override
    public org.pcollections.PQueue<T> minus() {
        return withList(list.drop(1));
    }

    @Override
    public boolean offer(T o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T poll() {
        return list.get();
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    

   
}