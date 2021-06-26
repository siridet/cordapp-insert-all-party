package net.corda.samples.example.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import net.corda.core.utilities.ProgressTracker.Step;
import net.corda.samples.example.contracts.IOUContract;
import net.corda.samples.example.states.IOUState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static net.corda.core.contracts.ContractsDSL.requireThat;

/**
 * This flow allows two parties (the [Initiator] and the [Acceptor]) to come to an agreement about the IOU encapsulated
 * within an [IOUState].
 * <p>
 * In our simple example, the [Acceptor] always accepts a valid IOU.
 * <p>
 * These flows have deliberately been implemented by using only the call() method for ease of understanding. In
 * practice we would recommend splitting up the various stages of the flow into sub-routines.
 * <p>
 * All methods called within the [FlowLogic] sub-class need to be annotated with the @Suspendable annotation.
 */
public class UpdateExampleFlow {
//    @InitiatingFlow
//    @StartableByRPC
//    public static class Initiator extends FlowLogic<SignedTransaction> {
//
//        private final UniqueIdentifier id;
//
//        private final Step GENERATING_TRANSACTION = new Step("Generating transaction based on new IOU.");
//        private final Step VERIFYING_TRANSACTION = new Step("Verifying contract constraints.");
//        private final Step SIGNING_TRANSACTION = new Step("Signing transaction with our private key.");
//        private final Step GATHERING_SIGS = new Step("Gathering the counterparty's signature.") {
//            @Override
//            public ProgressTracker childProgressTracker() {
//                return CollectSignaturesFlow.Companion.tracker();
//            }
//        };
//        private final Step FINALISING_TRANSACTION = new Step("Obtaining notary signature and recording transaction.") {
//            @Override
//            public ProgressTracker childProgressTracker() {
//                return FinalityFlow.Companion.tracker();
//            }
//        };
//
//        // The progress tracker checkpoints each stage of the flow and outputs the specified messages when each
//        // checkpoint is reached in the code. See the 'progressTracker.currentStep' expressions within the call()
//        // function.
//        private final ProgressTracker progressTracker = new ProgressTracker(
//                GENERATING_TRANSACTION,
//                VERIFYING_TRANSACTION,
//                SIGNING_TRANSACTION,
//                GATHERING_SIGS,
//                FINALISING_TRANSACTION
//        );
//
//        public Initiator(UniqueIdentifier id) {
//            this.id = id;
//        }
//
//        @Override
//        public ProgressTracker getProgressTracker() {
//            return progressTracker;
//        }
//
//        /**
//         * The flow logic is encapsulated within the call() method.
//         */
//        @Suspendable
//        @Override
//        public SignedTransaction call() throws FlowException {
//            try {
//                List<UUID> listOfLinearIds = new ArrayList<>();
//                listOfLinearIds.add(id.getId());
//                // Obtain a reference to a notary we wish to use.
//                /** METHOD 1: Take first notary on network, WARNING: use for test, non-prod environments, and single-notary networks only!*
//                 *  METHOD 2: Explicit selection of notary by CordaX500Name - argument can by coded in flow or parsed from config (Preferred)
//                 *
//                 *  * - For production you always want to use Method 2 as it guarantees the expected notary is returned.
//                 */
//                final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0); // METHOD 1
//                // final Party notary = getServiceHub().getNetworkMapCache().getNotary(CordaX500Name.parse("O=Notary,L=London,C=GB")); // METHOD 2
//
//                QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, listOfLinearIds);
//
//                Vault.Page<IOUState> rs = getServiceHub().getVaultService()
//                        .queryBy(IOUState.class, queryCriteria);
//                System.out.println("Size : " + rs.getStates().size());
//                StateAndRef<IOUState> inputState = rs.getStates().get(0);
//
//                System.out.println("==========================================================");
//                System.out.println("ID : " + this.id);
//                System.out.println("OurIdentity : " + getOurIdentity().getName().toString());
//                System.out.println("In : " + inputState);
//
//                IOUState outputState = new IOUState(9, inputState.getState().getData().getLender(),
//                        inputState.getState().getData().getBorrower(), inputState.getState().getData().getLinearId());
//                System.out.println("Out : " + outputState);
//
//                System.out.println("==========================================================");
//                // Stage 1.
//                progressTracker.setCurrentStep(GENERATING_TRANSACTION);
//                // Generate an unsigned transaction.
//                Party me = getOurIdentity();
////            IOUState iouState = new IOUState(iouValue, me, otherParty, new UniqueIdentifier());
//                final Command<IOUContract.Commands.Create> txCommand = new Command<>(
//                        new IOUContract.Commands.Create(),
//                        Arrays.asList(outputState.getLender().getOwningKey(), outputState.getBorrower().getOwningKey()));
//                final TransactionBuilder txBuilder = new TransactionBuilder(notary)
//                        .addOutputState(outputState)
//                        .addCommand(new IOUContract.Commands.Update(), getOurIdentity().getOwningKey())
//                        .addInputState(inputState);
//
//                // Stage 2.
//                progressTracker.setCurrentStep(VERIFYING_TRANSACTION);
//                // Verify that the transaction is valid.
//                txBuilder.verify(getServiceHub());
//
//                // Stage 3.
//                progressTracker.setCurrentStep(SIGNING_TRANSACTION);
//                // Sign the transaction.
//                final SignedTransaction partSignedTx = getServiceHub().signInitialTransaction(txBuilder);
//
//                // Stage 4.
//                progressTracker.setCurrentStep(GATHERING_SIGS);
//                List<FlowSession> sessions = new ArrayList<>();
//                System.out.println("Party all >>>>>>>>>> "+ ((ContractState) outputState).getParticipants().toString());
//
//                for (AbstractParty participant : ((ContractState) outputState).getParticipants()) {
//                    Party partyToInitiateFlow = (Party) participant;
//                    System.out.println("Party >>>>>>>>>> "+partyToInitiateFlow);
//                    if (!partyToInitiateFlow.getOwningKey().equals(getOurIdentity().getOwningKey())) {
//                        System.out.println("^ Signed");
//                        sessions.add(initiateFlow(partyToInitiateFlow));
//                    }
//                }
//                SignedTransaction stx = subFlow(new CollectSignaturesFlow(partSignedTx, sessions, CollectSignaturesFlow.Companion.tracker()));
//
//                // Stage 5 - Finalize tx
//                progressTracker.setCurrentStep(FINALISING_TRANSACTION);
//                return subFlow(new FinalityFlow(stx, sessions));
//
//
//
//
//
////                // Send the state to the counterparty, and receive it back with their signature.
////                List<FlowSession> bidderSessions = new ArrayList<>();
////                Party bidder = inputState.getState().getData().getLender();
////                bidderSessions.add(initiateFlow(bidder));
////
////                final SignedTransaction fullySignedTx = subFlow(
////                        new CollectSignaturesFlow(partSignedTx, Arrays.asList(bidderSessions.get(0)), CollectSignaturesFlow.Companion.tracker()));
////
////                // Stage 5.
////                progressTracker.setCurrentStep(FINALISING_TRANSACTION);
////                // Notarise and record the transaction in both parties' vaults.
////                return subFlow(new FinalityFlow(fullySignedTx, Arrays.asList(bidderSessions.get(0))));
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println("Error " + e.getMessage());
//                return null;
//            }
//        }
//    }
//
//    @InitiatedBy(Initiator.class)
//    public static class Acceptor extends FlowLogic<SignedTransaction> {
//
//        private final FlowSession otherPartySession;
//
//        public Acceptor(FlowSession otherPartySession) {
//            this.otherPartySession = otherPartySession;
//        }
//
//        @Suspendable
//        @Override
//        public SignedTransaction call() throws FlowException {
//            class SignTxFlow extends SignTransactionFlow {
//                private SignTxFlow(FlowSession otherPartyFlow, ProgressTracker progressTracker) {
//                    super(otherPartyFlow, progressTracker);
//                }
//
//                @Override
//                protected void checkTransaction(SignedTransaction stx) {
//                    requireThat(require -> {
//                        ContractState output = stx.getTx().getOutputs().get(0).getData();
//                        require.using("This must be an IOU transaction.", output instanceof IOUState);
//                        IOUState iou = (IOUState) output;
//                        require.using("I won't accept IOUs with a value over 100.", iou.getValue() <= 100);
//                        return null;
//                    });
//                }
//            }
//            final SignTxFlow signTxFlow = new SignTxFlow(otherPartySession, SignTransactionFlow.Companion.tracker());
//            final SecureHash txId = subFlow(signTxFlow).getId();
//
//            return subFlow(new ReceiveFinalityFlow(otherPartySession, txId));
//        }
//    }
}
