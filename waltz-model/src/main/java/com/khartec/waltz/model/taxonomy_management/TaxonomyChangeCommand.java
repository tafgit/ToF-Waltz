package com.khartec.waltz.model.taxonomy_management;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.khartec.waltz.model.*;
import com.khartec.waltz.model.command.Command;
import org.immutables.value.Value;

import java.util.Map;

import static com.khartec.waltz.common.Checks.checkTrue;

@Value.Immutable
@JsonSerialize(as =  ImmutableTaxonomyChangeCommand.class)
@JsonDeserialize(as =  ImmutableTaxonomyChangeCommand.class)
public abstract class TaxonomyChangeCommand implements
        Command,
        IdProvider,
        CreatedProvider,
        LastUpdatedProvider {

    public abstract TaxonomyChangeType changeType();

    @Value.Default
    public TaxonomyChangeLifecycleStatus status() {
        return TaxonomyChangeLifecycleStatus.DRAFT;
    }

    public abstract EntityReference changeDomain();

    public abstract EntityReference primaryReference();

    @Nullable
    public abstract Map<String, String> params();


    /**
     * Ensures that the `domain` matches `a` and (potentially) `b`
     */
    public void validate() {
        checkDomain(changeDomain().kind());
        checkRefKind(primaryReference().kind(), "a");
    }


    private void checkDomain(EntityKind domainKind) {
        checkTrue(
                domainKind == EntityKind.MEASURABLE_CATEGORY || domainKind == EntityKind.DATA_TYPE,
                "Taxonomy change command must be either a [MEASURABLE_CATEGORY] or a [DATA_TYPE], instead is is a [%s]",
                domainKind);
    }


    private void checkRefKind(EntityKind kind, String label) {
        switch (changeDomain().kind()) {
            case MEASURABLE_CATEGORY:
                checkTrue(
                        kind == EntityKind.MEASURABLE,
                        "If domain is [MEASURABLE_CATEGORY] then '%s' must also be a [MEASURABLE], instead it is a [%s]",
                        label,
                        kind);
                break;
            case DATA_TYPE:
                checkTrue(
                        primaryReference().kind() == EntityKind.DATA_TYPE,
                        "If domain is [DATA_TYPE] then 'a' must also be a [DATA_TYPE], instead it is a [%s]",
                        primaryReference().kind());
                break;
        }
    }


    public boolean paramAsBoolean(String key, boolean dflt) {
        return Boolean.valueOf(params().getOrDefault(key, Boolean.toString(dflt)));
    }


    public String param(String key) {
        return params().get(key);
    }
}

