/*
 * Waltz - Enterprise Architecture
 * Copyright (C) 2016  Khartec Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.khartec.waltz.jobs.generators;

import com.khartec.waltz.common.ListUtilities;
import com.khartec.waltz.data.server_information.ServerInformationDao;
import com.khartec.waltz.model.LifecycleStatus;
import com.khartec.waltz.model.server_information.ImmutableServerInformation;
import com.khartec.waltz.model.server_information.ServerInformation;
import org.jooq.DSLContext;
import org.springframework.context.ApplicationContext;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;

import static com.khartec.waltz.common.ArrayUtilities.randomPick;
import static com.khartec.waltz.schema.tables.ServerInformation.SERVER_INFORMATION;


public class ServerGenerator implements SampleDataGenerator {

    private static final Random rnd = new Random();
    private static final Set<String> commonHostNames = new HashSet<>();


    private static String mkHostName(int i) {
        // have some hosts serving multiple apps
        if (rnd.nextInt(10) > 8) {
            String hostName = SampleData.serverPrefixes[rnd.nextInt(SampleData.serverPrefixes.length - 1)]
                    + "00"
                    + SampleData.serverPostfixes[SampleData.serverPostfixes.length - 1];
            commonHostNames.add(hostName);
            return hostName;
        }

        return randomPick(SampleData.serverPrefixes)
                    + i
                    + randomPick(SampleData.serverPostfixes);
    }

    @Override
    public Map<String, Integer> create(ApplicationContext ctx) {

        ServerInformationDao serverDao = ctx.getBean(ServerInformationDao.class);
        DSLContext dsl = ctx.getBean(DSLContext.class);


        commonHostNames.clear();

        List<ServerInformation> servers = ListUtilities.newArrayList();

        IntStream.range(0, 10_000)
                .forEach(i -> {
                    String hostName = mkHostName(i);
                    boolean isCommonHost = commonHostNames.contains(hostName);

                    servers.add(
                            ImmutableServerInformation.builder()
                                    .hostname(hostName)
                                    .environment(isCommonHost ? SampleData.environments[0] : randomPick(SampleData.environments))
                                    .location(isCommonHost ? SampleData.locations[0] : randomPick(SampleData.locations))
                                    .operatingSystem(isCommonHost ? SampleData.operatingSystems[0] : randomPick(SampleData.operatingSystems))
                                    .operatingSystemVersion(isCommonHost ? SampleData.operatingSystemVersions[0] : randomPick(SampleData.operatingSystemVersions))
                                    .country("UK")
                                    .assetCode("wltz-0" + rnd.nextInt(4000))
                                    .hardwareEndOfLifeDate(
                                            rnd.nextInt(10) > 5
                                                    ? Date.valueOf(LocalDate.now().plusMonths(rnd.nextInt(12 * 6) - (12 * 3)))
                                                    : null)
                                    .operatingSystemEndOfLifeDate(
                                            rnd.nextInt(10) > 5
                                                    ? Date.valueOf(LocalDate.now().plusMonths(rnd.nextInt(12 * 6) - (12 * 3)))
                                                    : null)
                                    .virtual(isCommonHost || rnd.nextInt(10) > 7)
                                    .provenance(SAMPLE_DATA_PROVENANCE)
                                    .lifecycleStatus(randomPick(LifecycleStatus.values()))
                                    .build());
                });

        // servers.forEach(System.out::println);
        serverDao.bulkSave(servers);

        return null;
    }

    @Override
    public boolean remove(ApplicationContext ctx) {

        getDsl(ctx)
                .delete(SERVER_INFORMATION)
                .where(SERVER_INFORMATION.PROVENANCE.eq(SAMPLE_DATA_PROVENANCE))
                .execute();
        return true;
    }
}
