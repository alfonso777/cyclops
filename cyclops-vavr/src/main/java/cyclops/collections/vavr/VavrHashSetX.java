package cyclops.collections.vavr;


import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import com.aol.cyclops2.data.collections.extensions.CollectionX;
import com.aol.cyclops2.data.collections.extensions.lazy.immutable.LazyPSetX;
import com.aol.cyclops2.types.Unwrapable;
import com.aol.cyclops2.types.foldable.Evaluation;
import cyclops.collections.immutable.LinkedListX;
import cyclops.collections.immutable.OrderedSetX;
import cyclops.collections.immutable.PersistentSetX;
import cyclops.function.Reducer;
import cyclops.stream.ReactiveSeq;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import org.jooq.lambda.tuple.Tuple2;
import org.pcollections.PSet;



import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.Wither;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VavrHashSetX<T> extends AbstractSet<T> implements PSet<T>, Unwrapable {

    public static <T> PersistentSetX<T> listX(ReactiveSeq<T> stream){
        return fromStream(stream);
    }
    public static <T> PersistentSetX<T> copyFromCollection(CollectionX<? extends T> vec) {
        PersistentSetX<T> res = VavrHashSetX.<T>empty()
                .plusAll(vec);
        return res;
    }
    @Override
    public <R> R unwrap() {
        return (R)set;
    }
    /**
     * Create a LazyPSetX from a Stream
     * 
     * @param stream to construct a LazyQueueX from
     * @return LazyPSetX
     */
    public static <T> LazyPSetX<T> fromStream(Stream<T> stream) {
        return new LazyPSetX<T>(null, ReactiveSeq.fromStream(stream),toPSet(), Evaluation.LAZY);
    }

    /**
     * Create a LazyPSetX that contains the Integers between start and end
     * 
     * @param start
     *            Number of range to start from
     * @param end
     *            Number for range to end at
     * @return Range ListX
     */
    public static LazyPSetX<Integer> range(int start, int end) {
        return fromStream(ReactiveSeq.range(start, end));
    }

    /**
     * Create a LazyPSetX that contains the Longs between start and end
     * 
     * @param start
     *            Number of range to start from
     * @param end
     *            Number for range to end at
     * @return Range ListX
     */
    public static LazyPSetX<Long> rangeLong(long start, long end) {
        return fromStream(ReactiveSeq.rangeLong(start, end));
    }

    /**
     * Unfold a function into a ListX
     * 
     * <pre>
     * {@code 
     *  LazyPSetX.unfold(1,i->i<=6 ? Optional.of(Tuple.tuple(i,i+1)) : Optional.empty());
     * 
     * //(1,2,3,4,5)
     * 
     * }</pre>
     * 
     * @param seed Initial value 
     * @param unfolder Iteratively applied function, terminated by an empty Optional
     * @return ListX generated by unfolder function
     */
    public static <U, T> LazyPSetX<T> unfold(U seed, Function<? super U, Optional<Tuple2<T, U>>> unfolder) {
        return fromStream(ReactiveSeq.unfold(seed, unfolder));
    }

    /**
     * Generate a LazyPSetX from the provided Supplier up to the provided limit number of times
     * 
     * @param limit Max number of elements to generate
     * @param s Supplier to generate ListX elements
     * @return ListX generated from the provided Supplier
     */
    public static <T> LazyPSetX<T> generate(long limit, Supplier<T> s) {

        return fromStream(ReactiveSeq.generate(s)
                                      .limit(limit));
    }

    /**
     * Create a LazyPSetX by iterative application of a function to an initial element up to the supplied limit number of times
     * 
     * @param limit Max number of elements to generate
     * @param seed Initial element
     * @param f Iteratively applied to each element to generate the next element
     * @return ListX generated by iterative application
     */
    public static <T> LazyPSetX<T> iterate(long limit, final T seed, final UnaryOperator<T> f) {
        return fromStream(ReactiveSeq.iterate(seed, f)
                                      .limit(limit));
    }

    /**
     * <pre>
     * {@code 
     * PSet<Integer> q = VavrHashSetX.<Integer>toPSet()
                                      .mapReduce(Stream.of(1,2,3,4));
     * 
     * }
     * </pre>
     * @return Reducer for PSet
     */
    public static <T> Reducer<PSet<T>> toPSet() {
        return Reducer.<PSet<T>> of(VavrHashSetX.emptyPSet(), (final PSet<T> a) -> b -> a.plusAll(b), (final T x) -> VavrHashSetX.singleton(x));
    }

    public static <T> LazyPSetX<T> PSet(Set<T> q) {
        return fromPSet(new VavrHashSetX<>(q), toPSet());
    }
    public static <T> VavrHashSetX<T> emptyPSet(){
        return  new VavrHashSetX<>(HashSet.empty());
    }
    public static <T> LazyPSetX<T> empty(){
        return fromPSet( new VavrHashSetX<>(HashSet.empty()), toPSet());
    }
    private static <T> LazyPSetX<T> fromPSet(PSet<T> ts, Reducer<PSet<T>> pSetReducer) {
        return new LazyPSetX<T>(ts,null,pSetReducer, Evaluation.LAZY);
    }
    public static <T> LazyPSetX<T> singleton(T t){
        return fromPSet(new VavrHashSetX<>(HashSet.of(t)), toPSet());
    }
    public static <T> LazyPSetX<T> of(T... t){
        return fromPSet( new VavrHashSetX<>(HashSet.of(t)), toPSet());
    }
    public static <T> LazyPSetX<T> ofAll(Set<T> q) {
        return fromPSet(new VavrHashSetX<>(q), toPSet());
    }
    @SafeVarargs
    public static <T> LazyPSetX<T> PSet(T... elements){
        return  of(elements);
    }
    @Wither
    private final Set<T> set;

    @Override
    public PSet<T> plus(T e) {
        return withSet(set.add(e));
    }

    @Override
    public PSet<T> plusAll(Collection<? extends T> l) {
        return withSet(set.addAll(l));
    }

  

    @Override
    public PSet<T> minus(Object e) {
        return withSet(set.remove((T)e));
    }

    @Override
    public PSet<T> minusAll(Collection<?> l) {
        return withSet(set.removeAll((Collection)l));
    }

   

 
    @Override
    public int size() {
        return set.size();
    }

    @Override
    public Iterator<T> iterator() {
        return set.iterator();
    }

   

   
}
