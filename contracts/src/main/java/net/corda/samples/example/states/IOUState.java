package net.corda.samples.example.states;

import net.corda.samples.example.contracts.IOUContract;
import net.corda.samples.example.schema.IOUSchemaV1;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The states object recording IOU agreements between two parties.
 *
 * A states must implement [ContractState] or one of its descendants.
 */
@BelongsToContract(IOUContract.class)
public class IOUState implements LinearState {
    private final Integer value;
    private final Party owner;
    private final UniqueIdentifier linearId;
    private final List<Party> bidders;

    public IOUState(Integer value,
                    Party owner,
                    UniqueIdentifier linearId, List<Party> bidders)
    {
        this.value = value;
        this.owner = owner;
        this.linearId = linearId; this.bidders = bidders;
    }

    public Integer getValue() { return value; }

    public Party getOwner() {
        return owner;
    }

    public List<Party> getBidders() {
        return bidders;
    }

    @Override public UniqueIdentifier getLinearId() { return linearId; }
    @Override public List<AbstractParty> getParticipants() {
        List<AbstractParty> allParties = new ArrayList<>(bidders);
        allParties.add(owner);
        return allParties;
    }

//    @Override public PersistentState generateMappedObject(MappedSchema schema) {
//        if (schema instanceof IOUSchemaV1) {
//            return new IOUSchemaV1.PersistentIOU(
//                    this.owner.getName().toString(),
//                    this.value,
//                    this.linearId.getId());
//        } else {
//            throw new IllegalArgumentException("Unrecognised schema $schema");
//        }
//    }

//    @Override public Iterable<MappedSchema> supportedSchemas() {
//        return Arrays.asList(new IOUSchemaV1());
//    }

    @Override
    public String toString() {
        return String.format("IOUState(value=%s, lender=%s, borrower=%s, linearId=%s)", value, owner,   linearId);
    }
}