package com.aol.cyclops.javaslang.comprehenders;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;

import com.aol.cyclops.types.extensability.Comprehender;

import javaslang.collection.Queue;

public class QueueComprehender implements Comprehender<Queue> {

	@Override
	public Object map(Queue t, Function fn) {
		return t.map(s -> fn.apply(s));
	}
	@Override
	public Object executeflatMap(Queue t, Function fn){
		return flatMap(t,input -> unwrapOtherMonadTypes(this,fn.apply(input)));
	}
	@Override
	public Object flatMap(Queue t, Function fn) {
		return t.flatMap(s->fn.apply(s));
	}

	@Override
	public Queue of(Object o) {
		return Queue.of(o);
	}

	@Override
	public Queue empty() {
		return Queue.empty();
	}

	@Override
	public Class getTargetClass() {
		return Queue.class;
	}
	static Queue unwrapOtherMonadTypes(Comprehender<Queue> comp,Object apply){
		if(apply instanceof java.util.stream.Stream)
			return Queue.of( ((java.util.stream.Stream)apply).iterator());
		if(apply instanceof Iterable)
			return Queue.of( ((Iterable)apply).iterator());
		
		if(apply instanceof Collection){
			return Queue.ofAll((Collection)apply);
		}
		
		return Comprehender.unwrapOtherMonadTypes(comp,apply);
		
	}
	@Override
	public Queue fromIterator(Iterator o) {
		return  Queue.ofAll(()->o);
	}
}
