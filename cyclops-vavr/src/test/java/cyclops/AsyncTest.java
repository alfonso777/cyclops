package cyclops;

import com.aol.cyclops2.types.anyM.AnyMValue;
import cyclops.companion.vavr.Trys;
import cyclops.monads.VavrWitness.future;
import cyclops.monads.VavrWitness.tryType;
import cyclops.monads.WitnessType;
import io.vavr.concurrent.Future;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static cyclops.companion.vavr.Futures.anyM;
import static org.junit.Assert.assertTrue;


public class AsyncTest {

    static interface Work{
        Future<String> load();
        Future<Boolean> save(String data);
    }



    @Test
    public void testCode(){

        System.out.println("Run asynchronously..");
        Capitalizer<future> processorAsync = new Capitalizer<>(new AsyncWork());
        assertTrue(processorAsync.process().get());

        System.out.println("Run synchronously..");
        Capitalizer<tryType> processorSync = new Capitalizer<>(new SyncWork());
        assertTrue(processorSync.process().get());



    }
    static interface GenericWork<W extends WitnessType<W>>{
        AnyMValue<W,String> get();
        AnyMValue<W,Boolean> save(String data);
    }

    @AllArgsConstructor
    static class Capitalizer<W extends WitnessType<W>>{

        GenericWork<W> worker;

        public AnyMValue<W,Boolean> process(){
            return worker.get()
                    .map(s->s.toUpperCase())
                    .peek(System.out::println)
                    .flatMap(worker::save)
                    .peek(System.out::println);
        }
    }

    static class AsyncWork implements GenericWork<future>{

        private ExecutorService ex = Executors.newFixedThreadPool(2);

        @Override
        public AnyMValue<future, String> get(){
            return  anyM(Future.ofSupplier(ex, () -> "load data asynchronously"));
        }

        @Override
        public AnyMValue<future, Boolean> save(String data){
            return anyM(Future.ofSupplier(ex,()->true));
        }
    }
    static class SyncWork implements GenericWork<tryType>{

        @Override
        public AnyMValue<tryType, String> get() {
                return Trys.anyM(Try.success("load data synchronously"));
        }

        @Override
        public AnyMValue<tryType, Boolean> save(String data) {
            return Trys.anyM(Try.success(true));
        }
    }

}

