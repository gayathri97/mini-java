package mini.java.fa.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import mini.java.TestHelperV2;
import mini.java.fa.AcceptableNFAState;
import mini.java.fa.NFAState;
import mini.java.fa.v3.DFA;

import org.junit.Test;

public class HelperTest {

    @Test
    public void testCollapseAcceptableState() {
        Object input = new Object();
        NFAState A = new NFAState();
        NFAState B = new NFAState();
        NFAState C = new AcceptableNFAState();
        
        A.addTransition(B, input);
        B.addTransition(C);
        
        NFAState dfa = Helper.collapse(A);
        NFAState got = dfa.getState(input);
        assertTrue(got instanceof AcceptableNFAState);
    }
    
    @Test
    public void testCollapseInitialAcceptableState() {
        NFAState A = new NFAState();
        NFAState B = new AcceptableNFAState();
        
        A.addTransition(B);
        NFAState got = Helper.collapse(A);
        assertTrue(got instanceof AcceptableNFAState);
    }
    
    @Test
    public void testConvert() {
        TestHelperV2 helper = new TestHelperV2();
        helper.addNFAStates("AB");
        helper.addTransitions("ABa,BAa");
        
        NFAState v4 = helper.getNFAState('A');
        DFA v3 = Helper.convert(v4);
        assertEquals(Helper.dump(v4), Helper.dumpString(v3));
    }
    
    @Test
    public void testFindAll() {
        NFAState[] S = new NFAState[4];
        for (int i = 0; i<S.length; ++i) {
            S[i] = new NFAState();
        }
        
        S[0].addTransition(S[1]);
        S[1].addTransition(S[2], new Object());
        S[2].addTransition(S[0]); // a loop
        // S[3] is disconnected
        
        Set<NFAState> got = Helper.findAll(S[0]);
        Set<NFAState> expected = new HashSet<NFAState>(Arrays.asList(S[0], S[1], S[2]));
        assertEquals(expected, got);
        
    }
    
    @Test
    public void testFindAllSingleton() {
        NFAState root = new NFAState();
        assertEquals(
                Collections.singleton(root), Helper.findAll(root));
    }
}
