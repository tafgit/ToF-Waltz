
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


function store($http, BaseApiUrl) {

    const BASE = `${BaseApiUrl}/entity-named-note-type`;


    const findAll = () =>
        $http.get(BASE)
            .then(result => result.data);

    const create = (cmd) =>
        $http.post(BASE, cmd)
            .then(result => result.data);

    const update = (id, cmd) =>
        $http.put(`${BASE}/${id}`, cmd)
            .then(result => result.data);

    const remove = (id) =>
        $http.delete(`${BASE}/${id}`)
            .then(result => result.data);

    return {
        findAll,
        create,
        update,
        remove
    };
}


store.$inject = [
    '$http',
    'BaseApiUrl'
];


export default store;
