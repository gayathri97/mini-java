package mini.java.fa;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NFAState {
    private Set<NFAState>         _epsilons;
    private Map<Object, NFAState> _transitions;

    public NFAState() {
        _epsilons = new HashSet<NFAState>();
        _transitions = new HashMap<Object, NFAState>();
    }

    /**
     * Creates a new transition from this NFAState to the target NFAState.
     * Target state and input object cannot be null. <b>NOTE:</b> There will be
     * only one target NFAState for a given input; new transitions will override
     * existing one.
     */
    public void addTransition(NFAState state_, Object input_) {
        assert (state_ != null);
        assert (input_ != null);

        _transitions.put(input_, state_);
    }

    /**
     * Creates a new epsilon transition from this NFAState to the target
     * NFAState. Target state cannot be null.
     */
    public void addTransition(NFAState state_) {
        assert (state_ != null);

        _epsilons.add(state_);
    }

    /**
     * Returns all target states of the epsilon transitions from this NFAState.
     */
    public Set<NFAState> getStates() {
        return Collections.unmodifiableSet(_epsilons);
    }

    /**
     * Returns the target state associated with the given input object. Null
     * will be returned if no such target state.
     */
    public NFAState getState(Object input_) {
        return _transitions.get(input_);
    }

    /**
     * Returns all input objects this NFAState can accept.
     */
    public Set<Object> getInputs() {
        return _transitions.keySet();
    }
    
//    public Set<Object> getOrderedInputs() {
//        Set<Object> inputs = new TreeSet<Object>(new Comparator<Object>(){
//            public int compare(Object o1, Object o2) {
//                return o1.toString().compareTo(o2.toString());
//            }
////            public boolean equal(Object o1, Object o2) {
////                return o1.toString().equals(o2.toString());
////            }
//        });
//        inputs.addAll(getInputs());
//        
//        return inputs;
//    }

    /**
     * Returns the closure of this NFAState.
     */
    public NFAClosure getClosure() {
        return new NFAClosure(this);
    }
}