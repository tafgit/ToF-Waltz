package com.khartec.waltz.service.taxonomy_management;

import com.khartec.waltz.model.EntityReference;
import com.khartec.waltz.model.HierarchyQueryScope;
import com.khartec.waltz.model.IdSelectionOptions;
import com.khartec.waltz.model.Severity;
import com.khartec.waltz.model.measurable.Measurable;
import com.khartec.waltz.model.taxonomy_management.ImmutableTaxonomyChangeImpact;
import com.khartec.waltz.model.taxonomy_management.ImmutableTaxonomyChangePreview;
import com.khartec.waltz.model.taxonomy_management.TaxonomyChangeCommand;
import com.khartec.waltz.service.measurable.MeasurableService;
import com.khartec.waltz.service.measurable_rating.MeasurableRatingService;

import java.util.Set;
import java.util.stream.Collectors;

import static com.khartec.waltz.common.Checks.checkNotNull;
import static com.khartec.waltz.common.Checks.checkTrue;
import static com.khartec.waltz.model.IdSelectionOptions.mkOpts;

public class TaxonomyManagementUtilities {


    public static Measurable validateMeasurable(MeasurableService measurableService,
                                                TaxonomyChangeCommand cmd) {
        long measurableId = cmd.primaryReference().id();
        Measurable measurable = measurableService.getById(measurableId);

        checkNotNull(
                measurable,
                "Cannot find measurable [%d]",
                measurableId);

        checkTrue(
                cmd.changeDomain().id() == measurable.categoryId(),
                "Measurable [%s / %d] is not in category [%d], instead it is in category [%d]",
                measurable.name(),
                measurable.id(),
                cmd.changeDomain().id(),
                measurable.categoryId());

        return measurable;
    }


    public static Set<EntityReference> findCurrentRatingMappings(MeasurableRatingService measurableRatingService,
                                                                 TaxonomyChangeCommand cmd) {
        IdSelectionOptions selectionOptions = mkOpts(cmd.primaryReference(), HierarchyQueryScope.EXACT);
        return measurableRatingService
                .findByMeasurableIdSelector(selectionOptions)
                .stream()
                .map(r -> r.entityReference())
                .collect(Collectors.toSet());
    }


    /**
     * Optionally add an impact to the given preview and return it.
     * Whether to add the impact is determined by the presence of references.
     *
     * @param preview The preview builder to update
     * @param refs Set of references, if empty no impact will be added to the preview
     * @param severity Severity of the impact
     * @param msg Description of the impact
     * @return The preview builder for convenience
     */
    public static ImmutableTaxonomyChangePreview.Builder addToPreview(ImmutableTaxonomyChangePreview.Builder preview,
                                                                      Set<EntityReference> refs,
                                                                      Severity severity,
                                                                      String msg) {
        return refs.isEmpty()
            ? preview
            : preview
                .addImpacts(ImmutableTaxonomyChangeImpact.builder()
                .impactedReferences(refs)
                .description(msg)
                .severity(severity)
                .build());
    }


    public static String getNameParam(TaxonomyChangeCommand cmd) {
        return cmd.param("name");
    }


    public static String getDescriptionParam(TaxonomyChangeCommand cmd) {
        return cmd.param("description");
    }


    public static String getExternalIdParam(TaxonomyChangeCommand cmd) {
        return cmd.param("externalId");
    }


    public static boolean getConcreteParam(TaxonomyChangeCommand cmd, boolean dflt) {
        return cmd.paramAsBoolean("concrete", dflt);
    }
}
