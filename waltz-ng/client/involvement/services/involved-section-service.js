/*
 *
 *  * Waltz - Enterprise Architecture
 *  * Copyright (C) 2017  Khartec Ltd.
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU Lesser General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU Lesser General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU Lesser General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

import {mkChangeCommand} from '../involvement-utils';


function service(involvementStore, notification) {

    const addInvolvement = (entityRef, entityInvolvement) => {

        return involvementStore.changeInvolvement(
                entityRef,
                mkChangeCommand('ADD', entityInvolvement.entity, entityInvolvement.involvement))
            .then(result => {
                if(result) {
                    notification.success("Involvement added successfully");
                } else {
                    notification.warning("Failed to add involvement")
                }
            });
    };


    const removeInvolvement = (entityRef, entityInvolvement) => {

        return involvementStore.changeInvolvement(
                entityRef,
                mkChangeCommand('REMOVE', entityInvolvement.entity, entityInvolvement.involvement))
            .then(result => {
                if(result) {
                    notification.success("Involvement removed successfully");
                } else {
                    notification.warning("Failed to remove involvement")
                }
            });
    };


    return {
        addInvolvement,
        removeInvolvement
    };
}


service.$inject = [
    'InvolvementStore',
    'Notification'
];


export default service;
