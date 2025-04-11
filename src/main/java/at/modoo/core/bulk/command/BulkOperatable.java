package at.modoo.core.bulk.command;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class BulkOperatable<ID> {
    private RowStatus _action;
    private Long _sequence;
    public abstract ID getId();
}
