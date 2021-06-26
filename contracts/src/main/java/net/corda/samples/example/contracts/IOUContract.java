package net.corda.samples.example.contracts;

import net.corda.core.contracts.Command;
import net.corda.samples.example.states.IOUState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.AbstractParty;
import net.corda.core.transactions.LedgerTransaction;

import java.security.PublicKey;
import java.util.List;
import java.util.stream.Collectors;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

/**
 * A implementation of a basic smart contracts in Corda.
 * <p>
 * This contracts enforces rules regarding the creation of a valid [IOUState], which in turn encapsulates an [IOU].
 * <p>
 * For a new [IOU] to be issued onto the ledger, a transaction is required which takes:
 * - Zero input states.
 * - One output states: the new [IOU].
 * - An Create() command with the public keys of both the lender and the borrower.
 * <p>
 * All contracts must sub-class the [Contract] interface.
 */
public class IOUContract implements Contract {
    public static final String ID = "net.corda.samples.example.contracts.IOUContract";

    /**
     * The verify() function of all the states' contracts must not throw an exception for a transaction to be
     * considered valid.
     */
    @Override
    public void verify(LedgerTransaction tx) {

        if (tx.getCommands().size() == 0) {
            throw new IllegalArgumentException("One command Expected");
        }
        Command command = tx.getCommand(0);

        if (command.getValue() instanceof Commands.Update) {
            update(tx);
        } else if (command.getValue() instanceof Commands.Create) {

            create(tx);
        } else {
            throw new IllegalArgumentException("Invalid Command");
        }
//        requireThat(require -> {
//            // Generic constraints around the IOU transaction.
//            require.using("No inputs should be consumed when issuing an IOU.",
//                    tx.getInputs().isEmpty());
//            require.using("Only one output states should be created.",
//                    tx.getOutputs().size() == 1);
//            final IOUState out = tx.outputsOfType(IOUState.class).get(0);
//            require.using("The lender and the borrower cannot be the same entity.",
//                    !out.getLender().equals(out.getBorrower()));
//            require.using("All of the participants must be signers.",
//                    command.getSigners().containsAll(out.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList())));
//
//            // IOU-specific constraints.
//            require.using("The IOU's value must be non-negative.",
//                    out.getValue() > 0);
//
//            return null;
//        });
    }

    private void create(LedgerTransaction tx) {
//        Command command = tx.getCommand(0);
//        IOUState auctionState = (IOUState) tx.getInput(0);
    }

    private void update(LedgerTransaction tx) {
        Command command = tx.getCommand(0);
        IOUState auctionState = (IOUState) tx.getInput(0);



//        Asset asset = (Asset) tx.getReferenceInput(0);

//        if(auctionState.getActive())
//            throw new IllegalArgumentException("Auction is Active");
//
//        if(auctionState.getWinner() != null) {
//            if (!(command.getSigners().contains(auctionState.getAuctioneer().getOwningKey())) &&
//                    (auctionState.getWinner() != null
//                            && command.getSigners().contains(auctionState.getWinner().getOwningKey())))
//                throw new IllegalArgumentException("Auctioneer and Winner must Sign");
//
//            if (!(asset.getOwner().getOwningKey().equals(auctionState.getWinner().getOwningKey())))
//                throw new IllegalArgumentException("Auction not settled yet");
//        }else{
//            if (!(command.getSigners().contains(auctionState.getAuctioneer().getOwningKey())))
//                throw new IllegalArgumentException("Auctioneer must Sign");
//        }
    }

    /**
     * This contracts only implements one command, Create.
     */
    public interface Commands extends CommandData {
        class Create implements Commands {
        }

        class Update implements Commands {
        }
    }
}