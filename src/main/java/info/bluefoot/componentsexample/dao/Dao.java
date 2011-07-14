/*
 * Copyright 2011 http://bluefoot.info
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package info.bluefoot.componentsexample.dao;

import java.util.List;
import java.util.Map;


public interface Dao<T> {
    void add(T obj);
    void delete(T obj);
    List<T> findAll(Integer firstResult, Integer pageSize, String sortField, Boolean sortOrder);
    Integer count(Map<String, String> filter);
    List<T> findAll(Integer firstResult, Integer pageSize, String sortField,
            Boolean sortOrder, Map<String, String> filter);
    T findById(Integer id) throws NotFoundException;
    void update(T obj);
}
