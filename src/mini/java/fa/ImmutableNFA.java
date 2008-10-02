package mini.java.fa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ImmutableNFA implements NFA {
    // fake inputs used to represent epsilon transitions
    private Set<Object> _epsilons;
    private DFA _dfa;
    
    // private constructor
    private ImmutableNFA(DFA dfa_, Set<Object> epsilons_) {
        _dfa = dfa_;
        // defensive copy to protect the immutable instance
        _epsilons = new HashSet<Object>(epsilons_);
    }

    @Override
    public DFA buildDFA() {
        // mapping from set of NFA states (closures) to new DFA states
        // NOTE: this also serves as the "flags" for checked states
        Map<Set<State>, State> newStates = new HashMap<Set<State>, State>();
        // the "todo" list, containing unchecked states
        List<Set<State>> uncheckedStates = new LinkedList<Set<State>>();
        
        DFABuilder builder = new ImmutableDFA.Builder();
        
        // start from the initial state
        InitialState initialState = getInitialState();
        // get the closure for the initial state
        Set<State> initialClosure = closure(initialState);
        // create a new state for the initial closure
        newStates.put(initialClosure, isAcceptable(initialClosure)
                ? new AcceptableInitialState() : new InitialState());
        // add the initial closure to the "todo" list
        uncheckedStates.add(initialClosure);
        
        while (!uncheckedStates.isEmpty()) {
            Set<State> sourceClosure = uncheckedStates.remove(0);
            State sourceState = newStates.get(sourceClosure);
            
            // walk through all possible inputs
            for (Object input : getInputs(sourceClosure)) {
                // get the target closure
                Set<State> targetClosure = closure(sourceClosure, input);
                // create new state for target closure if there isn't one
                if (!newStates.containsKey(targetClosure)) {
                    //newStates.put(targetClosure, createState(targetClosure));
                    newStates.put(targetClosure, isAcceptable(targetClosure)
                            ? new AcceptableState() : new State());
                    uncheckedStates.add(targetClosure);
                }
                State targetState = newStates.get(targetClosure);
                // add the corresponding transition in the DFA
                builder.addTransition(sourceState, targetState, input);
            }
        }
        
        return builder.buildDFA();
    }
    
     // helper function for checking whether a closure contains acceptable state
    private final static boolean isAcceptable(Set<State> closure_) {
        for (State state : closure_) {
            if (state instanceof Acceptable)
                return true;
        }
        return false;
    }

    @Override
    public Set<State> closure(State from, Object input) {
        return closure(closure(from), input);
    }
    
    // helper function used to get the target closure from other closure
    private Set<State> closure(Set<State> closure_, Object input_) {
        assert(closure_ != null);
        assert(input_   != null);
        
        Set<State> targetClosure = new HashSet<State>();
        for (State state : closure_) {
            // valid transitions from the closure            
            State target = _dfa.getState(state, input_);
            
            // target state cannot be null;
            if (target != null) {
                // add the target state and its closure
                // NOTE: closure will include the target state itself
                targetClosure.addAll(closure(target));
            }
        }
        
        return targetClosure;
    }


    @Override
    public Set<State> closure(State from) {
        assert(from != null); // source state cannot be null
        
        Set<State> ret = new HashSet<State>();
        Set<State> done = new HashSet<State>();
        List<State> todo = new LinkedList<State>();
       
        todo.add(from); // start from the source state
        while (!todo.isEmpty()) {
            State source = todo.remove(0); // remove the head

            Set<Object> inputs = _dfa.getInputs(source);
            // retain only epsilon transitions
            inputs.retainAll(_epsilons);

            // add target states for epsilon transitions
            for (Object input : inputs) {
                State target = _dfa.getState(source, input);
                // add the target to the todo list
                if (!done.contains(target)) {
                    todo.add(target);
                }
            }
            // add source state to the result set
            ret.add(source);
            // mark source state as "done"
            done.add(source);
        }
        
        return ret;
    }


    @Override
    public InitialState getInitialState() {
        return _dfa.getInitialState();
    }


    @Override
    public Set<Object> getInputs(State from) {
        assert(from != null); // source state cannot be null;
        return getInputs(closure(from));
    }
    
    // helper function used to get possible inputs for a closure
    private Set<Object> getInputs(Set<State> closure_) {
        assert(closure_ != null);
        
        Set<Object> inputs = new HashSet<Object>();
        // first get the closure of the source state
        for (State state : closure_) {
            // then add all valid inputs from the closure
            inputs.addAll(_dfa.getInputs(state));
        }
        // remove fake inputs for epsilon transitions
        inputs.removeAll(_epsilons);
        
        return inputs;
    }

    /**
     * Nested builder class for constructing ImmutableNFA objects.
     * NFA.Builder uses DFA.Builder internally as composition instead
     * of inherit from DFA.Builder.
     *
     * @author Alex
     */
    public static final class Builder {
        private DFABuilder _dfaBuilder;
        private ImmutableNFA _cachedInstance;
        
        // fake inputs used to represent epsilon transitions
        private Set<Object> _epsilons;
        
        public Builder() {
            _dfaBuilder = new ImmutableDFA.Builder();
            _epsilons = new HashSet<Object>();
        }
        
        /**
         * Add a normal transition to the NFA being built. Will call the underlying
         * DFA.Builder to add the transition. See DFA.Builder for details.
         */
        public void addTransition(State from_, State to_, Object input_) {
            assert from_  != null;
            assert to_    != null;
            assert input_ != null;
            
            _dfaBuilder.addTransition(from_, to_, input_);
            // clear the cache, so new instance can be created
            _cachedInstance = null;
        }
        
        /**
         * Add an epsilon transition to the NFA being built. A fake input will be
         * created for this transition to simulate an epsilon transition.
         */
        public void addTransition(State from_, State to_) {
            assert from_ != null;
            assert to_   != null;
            
            Object epsilon = new Object();
            _epsilons.add(epsilon);
            _dfaBuilder.addTransition(from_, to_, epsilon);
            // clear the cache, so new instance can be created
            _cachedInstance = null;
        }
        
        /**
         * Factory method which creats an ImmutableNFA instance. Since this NFA
         * implementaion is immutable, it's possible to use "==" to check the equality
         * of two NFA. So the same NFA will be returned if there are no new valid
         * transitions are added bewteen two calls of this method.
         * 
         * @return an instance of ImmutableNFA; null if no valid NFA can be created.
         */
        public ImmutableNFA buildNFA() {
            // if a new instance needs to be created
            if (_cachedInstance == null) {
                DFA dfa = _dfaBuilder.buildDFA();
                
                // "_cachedInstance" will remain "null" if no valid DFA and NFA
                // can be built; so a "null" value will be returned
                if (dfa != null) {
                    _cachedInstance = new ImmutableNFA(dfa, _epsilons);
                }
            }
            return _cachedInstance;
        }
    }

}
