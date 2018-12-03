package com.khartec.waltz.jobs.generators;

import com.khartec.waltz.model.app_group.AppGroupKind;
import com.khartec.waltz.schema.tables.records.ApplicationGroupRecord;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.khartec.waltz.schema.tables.ApplicationGroup.APPLICATION_GROUP;
import static com.khartec.waltz.schema.tables.ApplicationGroupEntry.APPLICATION_GROUP_ENTRY;
import static java.lang.String.format;

public class AppGroupGenerator implements SampleDataGenerator {

    private static final String[] names = {
            "Risk Working Group",
            "Finance Initiative '17",
            "Strategy .Next",
            "Ad hoc 32",
    };


    @Override
    public Map<String, Integer> create(ApplicationContext ctx) {
        DSLContext dsl = getDsl(ctx);

        List<ApplicationGroupRecord> groupRecords = Arrays
                .stream(names)
                .map(n -> {
                    ApplicationGroupRecord record = dsl.newRecord(APPLICATION_GROUP);
                    record.setName(n);
                    record.setKind(AppGroupKind.PUBLIC.name());
                    record.setDescription(format("%s : Description of %s", SAMPLE_DATA_PROVENANCE, n));

                    return record;
                })
                .collect(Collectors.toList());

        dsl.batchStore(groupRecords).execute();

        return null;
    }

    @Override
    public boolean remove(ApplicationContext ctx) {
        DSLContext dsl = getDsl(ctx);

        dsl.deleteFrom(APPLICATION_GROUP_ENTRY)
                .where(APPLICATION_GROUP_ENTRY.GROUP_ID.in(
                        DSL.select(APPLICATION_GROUP.ID)
                                .from(APPLICATION_GROUP)
                                .where(APPLICATION_GROUP.DESCRIPTION.startsWith(SAMPLE_DATA_PROVENANCE))
                ))
                .execute();

        dsl.deleteFrom(APPLICATION_GROUP)
                .where(APPLICATION_GROUP.DESCRIPTION.startsWith(SAMPLE_DATA_PROVENANCE))
                .execute();

        return false;
    }
}
