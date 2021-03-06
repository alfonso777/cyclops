package cyclops.monads.transformers;


import com.aol.cyclops2.types.foldable.ConvertableSequence;
import cyclops.companion.rx.Observables;
import cyclops.monads.Witness;


public class StreamTSeqConvertableSequenceTest extends AbstractConvertableSequenceTest {

    @Override
    public <T> ConvertableSequence<T> of(T... elements) {

        return Observables.of(elements).liftM(Witness.list.INSTANCE).to();
    }

    @Override
    public <T> ConvertableSequence<T> empty() {

        return Observables.<T>empty().liftM(Witness.list.INSTANCE).to();
    }

}
