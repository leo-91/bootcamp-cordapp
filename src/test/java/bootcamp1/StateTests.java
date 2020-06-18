package bootcamp1;

import net.corda.core.contracts.ContractState;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.testing.core.TestIdentity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StateTests {
    private final Party alice = new TestIdentity(new CordaX500Name("Alice", "", "GB")).getParty();
    private final Party bob = new TestIdentity(new CordaX500Name("Bob", "", "GB")).getParty();

    @Test
    public void tokenStateHasIssuerOwnerAndAmountParamsOfCorrectTypeInConstructor() {
        new TokenState(alice, bob, 1L);
    }

    @Test
    public void tokenStateHasGettersForIssuerOwnerAndAmount() {
        TokenState tokenState = new TokenState(alice, bob, 1L);
        assertEquals(alice, tokenState.getIssuer());
        assertEquals(bob, tokenState.getOwner());
        assertEquals(1L, tokenState.getAmount().longValue());
    }

    @Test
    public void tokenStateImplementsContractState() {
        assertTrue(new TokenState(alice, bob, 1L) instanceof ContractState);
    }

    @Test
    public void tokenStateHasTwoParticipantsTheIssuerAndTheOwner() {
        TokenState tokenState = new TokenState(alice, bob, 1L);
        assertEquals(2, tokenState.getParticipants().size());
        assertTrue(tokenState.getParticipants().contains(alice));
        assertTrue(tokenState.getParticipants().contains(bob));
    }
}