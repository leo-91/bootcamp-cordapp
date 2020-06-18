package bootcamp2;

import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.List;

public class HouseContract implements Contract {

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        if (tx.getCommands().size() != 1) {
            throw new IllegalArgumentException("Transaction must have one command");
        }
        Command command = tx.getCommand(0);
        List<PublicKey> requiredSigners = command.getSigners();
        CommandData commandType = command.getValue();
        if (commandType instanceof Register) {
            // Shape Constraints
            if (tx.getInputStates().size() != 0) {
                throw new IllegalArgumentException("Register Txn must have no inputs");
            }
            if (tx.getOutputs().size() != 1) {
                throw new IllegalArgumentException("Register txn must have one output");
            }
            //Content Constraints
            ContractState outputState = tx.getOutput(0);
            if (!(outputState instanceof HouseState)) {
                throw new IllegalArgumentException("Output State must be HouseState");

            }
            HouseState houseState = (HouseState) outputState;
            if (houseState.getAddress().length() <= 3) {
                throw new IllegalArgumentException("Address must be more than 3 characters");
            }
            if (houseState.getOwner().getName().getCountry().equalsIgnoreCase("China")) {
                throw new IllegalArgumentException("Not Allowed for chinese citizen");
            }

            //Required Signers constraints
            Party owner = houseState.getOwner();
            PublicKey ownerKey = owner.getOwningKey();
            if (!(requiredSigners.contains(ownerKey))) {
                throw new IllegalArgumentException("Owner of house must sign the registration");
            }

        } else if (commandType instanceof Transfer) {
            // Shape Constraints
            if (tx.getInputStates().size() != 1) {
                throw new IllegalArgumentException("Transfer Txn must have one input");
            }
            if (tx.getOutputs().size() != 1) {
                throw new IllegalArgumentException("Transfer Txn must have one output");
            }

            //Content Constraints
            ContractState inputState = tx.getInput(0);
            ContractState outputState = tx.getOutput(0);
            if (!(inputState instanceof HouseState)) {
                throw new IllegalArgumentException("Input State must be HouseState");
            }
            if (!(outputState instanceof HouseState)) {
                throw new IllegalArgumentException("Output State must be HouseState");
            }
            HouseState inputHouse = (HouseState) inputState;
            HouseState outputHouse= (HouseState) outputState;
            if (!(inputHouse.getAddress().equalsIgnoreCase(outputHouse.getAddress()))) {
                throw new IllegalArgumentException("Input State and output State address must be same");
            }
            if(inputHouse.getOwner().equals(outputHouse.getOwner())){
                throw new IllegalArgumentException("Input And Out owner cant be same");
            }

            //Signer Constraints
            Party prevOwner = inputHouse.getOwner();
            Party newOwner=outputHouse.getOwner();
            if(!requiredSigners.contains(prevOwner.getOwningKey())){
                throw new IllegalArgumentException("Current Owner must Sign transfer");
            }
            if(!requiredSigners.contains(newOwner.getOwningKey())){
                throw new IllegalArgumentException("New Owner must Sign transfer");
            }

        } else {
            throw new IllegalArgumentException("Command Type not valid");
        }
    }

    public class Register implements CommandData {
    }

    public class Transfer implements CommandData {
    }
}
