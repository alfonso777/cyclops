package cyclops.companion.vavr;

import com.aol.cyclops.vavr.hkt.SetKind;
import com.aol.cyclops2.hkt.Higher;
import com.aol.cyclops2.types.anyM.AnyMSeq;

import cyclops.control.Maybe;
import cyclops.function.Fn3;
import cyclops.function.Fn4;
import cyclops.function.Monoid;
import cyclops.monads.AnyM;
import cyclops.monads.VavrWitness.set;
import cyclops.monads.WitnessType;

import cyclops.stream.ReactiveSeq;
import cyclops.typeclasses.Active;
import cyclops.typeclasses.InstanceDefinitions;
import cyclops.typeclasses.Nested;
import cyclops.typeclasses.Pure;
import cyclops.typeclasses.comonad.Comonad;
import cyclops.typeclasses.foldable.Foldable;
import cyclops.typeclasses.foldable.Unfoldable;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.instances.General;
import cyclops.typeclasses.monad.*;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import lombok.experimental.UtilityClass;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;
import java.util.function.*;


public class Sets {

    public static <T> AnyMSeq<set,T> anyM(Set<T> option) {
        return AnyM.ofSeq(option, set.INSTANCE);
    }
    /**
     * Perform a For Comprehension over a Set, accepting 3 generating functions.
     * This results in a four level nested internal iteration over the provided Publishers.
     *
     *  <pre>
     * {@code
     *
     *   import static cyclops.Sets.forEach4;
     *
    forEach4(IntSet.range(1,10).boxed(),
    a-> Set.iterate(a,i->i+1).limit(10),
    (a,b) -> Set.<Integer>of(a+b),
    (a,b,c) -> Set.<Integer>just(a+b+c),
    Tuple::tuple)
     *
     * }
     * </pre>
     *
     * @param value1 top level Set
     * @param value2 Nested Set
     * @param value3 Nested Set
     * @param value4 Nested Set
     * @param yieldingFunction  Generates a result per combination
     * @return Set with an element per combination of nested publishers generated by the yielding function
     */
    public static <T1, T2, T3, R1, R2, R3, R> Set<R> forEach4(Set<? extends T1> value1,
                                                               Function<? super T1, ? extends Set<R1>> value2,
                                                               BiFunction<? super T1, ? super R1, ? extends Set<R2>> value3,
                                                               Fn3<? super T1, ? super R1, ? super R2, ? extends Set<R3>> value4,
                                                               Fn4<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {


        return value1.flatMap(in -> {

            Set<R1> a = value2.apply(in);
            return a.flatMap(ina -> {
                Set<R2> b = value3.apply(in,ina);
                return b.flatMap(inb -> {
                    Set<R3> c = value4.apply(in,ina,inb);
                    return c.map(in2 -> yieldingFunction.apply(in, ina, inb, in2));
                });

            });

        });

    }

    /**
     * Perform a For Comprehension over a Set, accepting 3 generating function.
     * This results in a four level nested internal iteration over the provided Publishers.
     * <pre>
     * {@code
     *
     *  import static com.aol.cyclops2.reactor.Setes.forEach4;
     *
     *  forEach4(IntSet.range(1,10).boxed(),
    a-> Set.iterate(a,i->i+1).limit(10),
    (a,b) -> Set.<Integer>just(a+b),
    (a,b,c) -> Set.<Integer>just(a+b+c),
    (a,b,c,d) -> a+b+c+d <100,
    Tuple::tuple);
     *
     * }
     * </pre>
     *
     * @param value1 top level Set
     * @param value2 Nested Set
     * @param value3 Nested Set
     * @param value4 Nested Set
     * @param filterFunction A filtering function, keeps values where the predicate holds
     * @param yieldingFunction Generates a result per combination
     * @return Set with an element per combination of nested publishers generated by the yielding function
     */
    public static <T1, T2, T3, R1, R2, R3, R> Set<R> forEach4(Set<? extends T1> value1,
                                                                 Function<? super T1, ? extends Set<R1>> value2,
                                                                 BiFunction<? super T1, ? super R1, ? extends Set<R2>> value3,
                                                                 Fn3<? super T1, ? super R1, ? super R2, ? extends Set<R3>> value4,
                                                                 Fn4<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
                                                                 Fn4<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {


        return value1.flatMap(in -> {

            Set<R1> a = value2.apply(in);
            return a.flatMap(ina -> {
                Set<R2> b = value3.apply(in,ina);
                return b.flatMap(inb -> {
                    Set<R3> c = value4.apply(in,ina,inb);
                    return c.filter(in2->filterFunction.apply(in,ina,inb,in2))
                            .map(in2 -> yieldingFunction.apply(in, ina, inb, in2));
                });

            });

        });
    }

    /**
     * Perform a For Comprehension over a Set, accepting 2 generating function.
     * This results in a three level nested internal iteration over the provided Publishers.
     *
     * <pre>
     * {@code
     *
     * import static Sets.forEach3;
     *
     * forEach(IntSet.range(1,10).boxed(),
    a-> Set.iterate(a,i->i+1).limit(10),
    (a,b) -> Set.<Integer>of(a+b),
    Tuple::tuple);
     *
     * }
     * </pre>
     *
     *
     * @param value1 top level Set
     * @param value2 Nested Set
     * @param value3 Nested Set
     * @param yieldingFunction Generates a result per combination
     * @return Set with an element per combination of nested publishers generated by the yielding function
     */
    public static <T1, T2, R1, R2, R> Set<R> forEach3(Set<? extends T1> value1,
                                                         Function<? super T1, ? extends Set<R1>> value2,
                                                         BiFunction<? super T1, ? super R1, ? extends Set<R2>> value3,
                                                         Fn3<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

        return value1.flatMap(in -> {

            Set<R1> a = value2.apply(in);
            return a.flatMap(ina -> {
                Set<R2> b = value3.apply(in,ina);
                return b.map(in2 -> yieldingFunction.apply(in, ina, in2));
            });


        });


    }

    /**
     * Perform a For Comprehension over a Set, accepting 2 generating function.
     * This results in a three level nested internal iteration over the provided Publishers.
     * <pre>
     * {@code
     *
     * import static Sets.forEach;
     *
     * forEach(IntSet.range(1,10).boxed(),
    a-> Set.iterate(a,i->i+1).limit(10),
    (a,b) -> Set.<Integer>of(a+b),
    (a,b,c) ->a+b+c<10,
    Tuple::tuple)
    .toSetX();
     * }
     * </pre>
     *
     * @param value1 top level Set
     * @param value2 Nested publisher
     * @param value3 Nested publisher
     * @param filterFunction A filtering function, keeps values where the predicate holds
     * @param yieldingFunction Generates a result per combination
     * @return
     */
    public static <T1, T2, R1, R2, R> Set<R> forEach3(Set<? extends T1> value1,
                                                         Function<? super T1, ? extends Set<R1>> value2,
                                                         BiFunction<? super T1, ? super R1, ? extends Set<R2>> value3,
                                                         Fn3<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
                                                         Fn3<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {


        return value1.flatMap(in -> {

            Set<R1> a = value2.apply(in);
            return a.flatMap(ina -> {
                Set<R2> b = value3.apply(in,ina);
                return b.filter(in2->filterFunction.apply(in,ina,in2))
                        .map(in2 -> yieldingFunction.apply(in, ina, in2));
            });



        });
    }

    /**
     * Perform a For Comprehension over a Set, accepting an additonal generating function.
     * This results in a two level nested internal iteration over the provided Publishers.
     *
     * <pre>
     * {@code
     *
     *  import static Sets.forEach2;
     *  forEach(IntSet.range(1, 10).boxed(),
     *          i -> Set.range(i, 10), Tuple::tuple)
    .forEach(System.out::println);

    //(1, 1)
    (1, 2)
    (1, 3)
    (1, 4)
    ...
     *
     * }</pre>
     *
     * @param value1 top level Set
     * @param value2 Nested publisher
     * @param yieldingFunction Generates a result per combination
     * @return
     */
    public static <T, R1, R> Set<R> forEach2(Set<? extends T> value1,
                                                Function<? super T, Set<R1>> value2,
                                                BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {


        return value1.flatMap(in -> {

            Set<R1> a = value2.apply(in);
            return a.map(in2 -> yieldingFunction.apply(in,  in2));
        });

    }

    /**
     *
     * <pre>
     * {@code
     *
     *   import static Sets.forEach2;
     *
     *   forEach(IntSet.range(1, 10).boxed(),
     *           i -> Set.range(i, 10),
     *           (a,b) -> a>2 && b<10,
     *           Tuple::tuple)
    .forEach(System.out::println);

    //(3, 3)
    (3, 4)
    (3, 5)
    (3, 6)
    (3, 7)
    (3, 8)
    (3, 9)
    ...

     *
     * }</pre>
     *
     *
     * @param value1 top level Set
     * @param value2 Nested publisher
     * @param filterFunction A filtering function, keeps values where the predicate holds
     * @param yieldingFunction Generates a result per combination
     * @return
     */
    public static <T, R1, R> Set<R> forEach2(Set<? extends T> value1,
                                                Function<? super T, ? extends Set<R1>> value2,
                                                BiFunction<? super T, ? super R1, Boolean> filterFunction,
                                                BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {


        return value1.flatMap(in -> {

            Set<R1> a = value2.apply(in);
            return a.filter(in2->filterFunction.apply(in,in2))
                    .map(in2 -> yieldingFunction.apply(in,  in2));
        });
    }

    public static <T> Active<set,T> allTypeclasses(Set<T> array){
        return Active.of(SetKind.widen(array), Sets.Instances.definitions());
    }
    public static <T,W2,R> Nested<set,W2,R> mapM(Set<T> array, Function<? super T,? extends Higher<W2,R>> fn, InstanceDefinitions<W2> defs){
        Set<Higher<W2, R>> e = array.map(fn);
        SetKind<Higher<W2, R>> lk = SetKind.widen(e);
        return Nested.of(lk, Sets.Instances.definitions(), defs);
    }
    /**
     * Companion class for creating Type Class instances for working with Sets
     *
     */
    @UtilityClass
    public static class Instances {

        public static InstanceDefinitions<set> definitions() {
            return new InstanceDefinitions<set>() {

                @Override
                public <T, R> Functor<set> functor() {
                    return Instances.functor();
                }

                @Override
                public <T> Pure<set> unit() {
                    return Instances.unit();
                }

                @Override
                public <T, R> Applicative<set> applicative() {
                    return Instances.zippingApplicative();
                }

                @Override
                public <T, R> Monad<set> monad() {
                    return Instances.monad();
                }

                @Override
                public <T, R> Maybe<MonadZero<set>> monadZero() {
                    return Maybe.just(Instances.monadZero());
                }

                @Override
                public <T> Maybe<MonadPlus<set>> monadPlus() {
                    return Maybe.just(Instances.monadPlus());
                }

                @Override
                public <T> Maybe<MonadPlus<set>> monadPlus(Monoid<Higher<set, T>> m) {
                    return Maybe.just(Instances.monadPlus(m));
                }

                @Override
                public <C2, T> Maybe<Traverse<set>> traverse() {
                    return Maybe.just(Instances.traverse());
                }

                @Override
                public <T> Maybe<Foldable<set>> foldable() {
                    return Maybe.just(Instances.foldable());
                }

                @Override
                public <T> Maybe<Comonad<set>> comonad() {
                    return Maybe.none();
                }

                @Override
                public <T> Maybe<Unfoldable<set>> unfoldable() {
                    return Maybe.just(Instances.unfoldable());
                }
            };
        }
        /**
         *
         * Transform a set, mulitplying every element by 2
         *
         * <pre>
         * {@code
         *  SetKind<Integer> set = Sets.functor().map(i->i*2, SetKind.widen(Set.of(1,2,3));
         *
         *  //[2,4,6]
         *
         *
         * }
         * </pre>
         *
         * An example fluent api working with Sets
         * <pre>
         * {@code
         *   SetKind<Integer> set = Sets.unit()
        .unit("hello")
        .then(h->Sets.functor().map((String v) ->v.length(), h))
        .convert(SetKind::narrowK);
         *
         * }
         * </pre>
         *
         *
         * @return A functor for Sets
         */
        public static <T,R>Functor<set> functor(){
            BiFunction<SetKind<T>,Function<? super T, ? extends R>,SetKind<R>> map = Instances::map;
            return General.functor(map);
        }
        /**
         * <pre>
         * {@code
         * SetKind<String> set = Sets.unit()
        .unit("hello")
        .convert(SetKind::narrowK);

        //Set.of("hello"))
         *
         * }
         * </pre>
         *
         *
         * @return A factory for Sets
         */
        public static <T> Pure<set> unit(){
            return General.<set,T>unit(Instances::of);
        }
        /**
         *
         * <pre>
         * {@code
         * import static com.aol.cyclops.hkt.jdk.SetKind.widen;
         * import static com.aol.cyclops.util.function.Lambda.l1;
         *
        Sets.zippingApplicative()
        .ap(widen(Set.of(l1(this::multiplyByTwo))),widen(Set.of(1,2,3)));
         *
         * //[2,4,6]
         * }
         * </pre>
         *
         *
         * Example fluent API
         * <pre>
         * {@code
         * SetKind<Function<Integer,Integer>> setFn =Sets.unit()
         *                                                  .unit(Lambda.l1((Integer i) ->i*2))
         *                                                  .convert(SetKind::narrowK);

        SetKind<Integer> set = Sets.unit()
        .unit("hello")
        .then(h->Sets.functor().map((String v) ->v.length(), h))
        .then(h->Sets.zippingApplicative().ap(setFn, h))
        .convert(SetKind::narrowK);

        //Set.of("hello".length()*2))
         *
         * }
         * </pre>
         *
         *
         * @return A zipper for Sets
         */
        public static <T,R> Applicative<set> zippingApplicative(){
            BiFunction<SetKind< Function<T, R>>,SetKind<T>,SetKind<R>> ap = Instances::ap;
            return General.applicative(functor(), unit(), ap);
        }
        /**
         *
         * <pre>
         * {@code
         * import static com.aol.cyclops.hkt.jdk.SetKind.widen;
         * SetKind<Integer> set  = Sets.monad()
        .flatMap(i->widen(SetX.range(0,i)), widen(Set.of(1,2,3)))
        .convert(SetKind::narrowK);
         * }
         * </pre>
         *
         * Example fluent API
         * <pre>
         * {@code
         *    SetKind<Integer> set = Sets.unit()
        .unit("hello")
        .then(h->Sets.monad().flatMap((String v) ->Sets.unit().unit(v.length()), h))
        .convert(SetKind::narrowK);

        //Set.of("hello".length())
         *
         * }
         * </pre>
         *
         * @return Type class with monad functions for Sets
         */
        public static <T,R> Monad<set> monad(){

            BiFunction<Higher<set,T>,Function<? super T, ? extends Higher<set,R>>,Higher<set,R>> flatMap = Instances::flatMap;
            return General.monad(zippingApplicative(), flatMap);
        }
        /**
         *
         * <pre>
         * {@code
         *  SetKind<String> set = Sets.unit()
        .unit("hello")
        .then(h->Sets.monadZero().filter((String t)->t.startsWith("he"), h))
        .convert(SetKind::narrowK);

        //Set.of("hello"));
         *
         * }
         * </pre>
         *
         *
         * @return A filterable monad (with default value)
         */
        public static <T,R> MonadZero<set> monadZero(){
            BiFunction<Higher<set,T>,Predicate<? super T>,Higher<set,T>> filter = Instances::filter;
            Supplier<Higher<set, T>> zero = ()-> SetKind.widen(HashSet.empty());
            return General.<set,T,R>monadZero(monad(), zero,filter);
        }
        /**
         * <pre>
         * {@code
         *  SetKind<Integer> set = Sets.<Integer>monadPlus()
        .plus(SetKind.widen(Set.of()), SetKind.widen(Set.of(10)))
        .convert(SetKind::narrowK);
        //Set.of(10))
         *
         * }
         * </pre>
         * @return Type class for combining Sets by concatenation
         */
        public static <T> MonadPlus<set> monadPlus(){
            Monoid<SetKind<T>> m = Monoid.of(SetKind.widen(HashSet.<T>empty()), Instances::concat);
            Monoid<Higher<set,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }
        /**
         *
         * <pre>
         * {@code
         *  Monoid<SetKind<Integer>> m = Monoid.of(SetKind.widen(Set.of()), (a,b)->a.isEmpty() ? b : a);
        SetKind<Integer> set = Sets.<Integer>monadPlus(m)
        .plus(SetKind.widen(Set.of(5)), SetKind.widen(Set.of(10)))
        .convert(SetKind::narrowK);
        //Set.of(5))
         *
         * }
         * </pre>
         *
         * @param m Monoid to use for combining Sets
         * @return Type class for combining Sets
         */
        public static <T> MonadPlus<set> monadPlus(Monoid<Higher<set,T>> m){
            Monoid<Higher<set,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }
        public static <T> MonadPlus<set> monadPlusK(Monoid<SetKind<T>> m){
            Monoid<Higher<set,T>> m2= (Monoid)m;
            return General.monadPlus(monadZero(),m2);
        }

        /**
         * @return Type class for traversables with traverse / sequence operations
         */
        public static <C2,T> Traverse<set> traverse(){
            BiFunction<Applicative<C2>,SetKind<Higher<C2, T>>,Higher<C2, SetKind<T>>> sequenceFn = (ap, set) -> {

                Higher<C2,SetKind<T>> identity = ap.unit(SetKind.widen(HashSet.empty()));

                BiFunction<Higher<C2,SetKind<T>>,Higher<C2,T>,Higher<C2,SetKind<T>>> combineToSet =   (acc, next) -> ap.apBiFn(ap.unit((a, b) -> concat(a, SetKind.just(b))),acc,next);

                BinaryOperator<Higher<C2,SetKind<T>>> combineSets = (a, b)-> ap.apBiFn(ap.unit((l1, l2)-> { return concat(l1,l2);}),a,b); ;

                return ReactiveSeq.fromIterable(set).reduce(identity,
                        combineToSet,
                        combineSets);


            };
            BiFunction<Applicative<C2>,Higher<set,Higher<C2, T>>,Higher<C2, Higher<set,T>>> sequenceNarrow  =
                    (a,b) -> SetKind.widen2(sequenceFn.apply(a, SetKind.narrowK(b)));
            return General.traverse(zippingApplicative(), sequenceNarrow);
        }

        /**
         *
         * <pre>
         * {@code
         * int sum  = Sets.foldable()
        .foldLeft(0, (a,b)->a+b, SetKind.widen(Set.of(1,2,3,4)));

        //10
         *
         * }
         * </pre>
         *
         *
         * @return Type class for folding / reduction operations
         */
        public static <T> Foldable<set> foldable(){
            BiFunction<Monoid<T>,Higher<set,T>,T> foldRightFn =  (m, l)-> ReactiveSeq.fromIterable(SetKind.narrow(l)).foldRight(m);
            BiFunction<Monoid<T>,Higher<set,T>,T> foldLeftFn = (m, l)-> ReactiveSeq.fromIterable(SetKind.narrow(l)).reduce(m);
            return General.foldable(foldRightFn, foldLeftFn);
        }

        private static  <T> SetKind<T> concat(SetKind<T> l1, SetKind<T> l2){
            return SetKind.widen(l1.addAll(l2));
        }
        private <T> SetKind<T> of(T value){
            return SetKind.widen(HashSet.of(value));
        }
        private static <T,R> SetKind<R> ap(SetKind<Function< T, R>> lt, SetKind<T> set){
            return SetKind.widen(lt.toReactiveSeq().zip(set,(a, b)->a.apply(b)));
        }
        private static <T,R> Higher<set,R> flatMap(Higher<set,T> lt, Function<? super T, ? extends  Higher<set,R>> fn){
            return SetKind.widen(SetKind.narrowK(lt).flatMap(fn.andThen(SetKind::narrowK)));
        }
        private static <T,R> SetKind<R> map(SetKind<T> lt, Function<? super T, ? extends R> fn){
            return SetKind.widen(lt.map(fn));
        }
        private static <T> SetKind<T> filter(Higher<set,T> lt, Predicate<? super T> fn){
            return SetKind.widen(SetKind.narrow(lt).filter(fn));
        }
        public static Unfoldable<set> unfoldable(){
            return new Unfoldable<set>() {
                @Override
                public <R, T> Higher<set, R> unfold(T b, Function<? super T, Optional<Tuple2<R, T>>> fn) {
                    return SetKind.widen(ReactiveSeq.unfold(b,fn).collect(HashSet.collector()));

                }
            };
        }
    }



}
