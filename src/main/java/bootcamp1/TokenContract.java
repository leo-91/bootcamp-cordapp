package bootcamp1;

import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;

import java.security.PublicKey;
import java.util.List;

/* Our contract, governing how our state will evolve over time.
 * See src/main/java/examples/ArtContract.java for an example. */
public class TokenContract implements Contract {
    public static String ID = "bootcamp1.TokenContract";


    @Override
    public void verify(LedgerTransaction tx) throws IllegalArgumentException {
        //shape
        if (tx.getCommands().size() != 1)
            throw new IllegalArgumentException("Transaction must have one command");
        Command command = tx.getCommand(0);
        List<PublicKey> requiredSigners = command.getSigners();
        CommandData commandType = command.getValue();
        if (!(commandType instanceof Commands.Issue))
            throw new IllegalArgumentException("There must be only issue command");
        if (requiredSigners.size() != 2)
            throw new IllegalArgumentException("There must 2 signers only");

        if (tx.getInputStates().size() != 0)
            throw new IllegalArgumentException("Input must be zero");
        if (tx.getOutputs().size() != 1)
            throw new IllegalArgumentException("Only one output State allowed");
        ContractState tokenOutput = tx.getOutput(0);
        if (!(tokenOutput instanceof TokenState))
            throw new IllegalArgumentException("Not a valid token state");
        //content
        TokenState token = (TokenState) tokenOutput;
        if (token.getAmount() <= 0)
            throw new IllegalArgumentException("amount must be positive");
        //signer
        Party issuer=token.getIssuer();
        if(!requiredSigners.contains(issuer.getOwningKey()))
            throw new IllegalArgumentException("Issuer key must be in command");

    }


    public interface Commands extends CommandData {
        class Issue implements Commands {
        }
    }
}
