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
package info.bluefoot.componentsexample.web;

import java.util.HashMap;
import java.util.Map;

import info.bluefoot.componentsexample.dao.Dao;
import info.bluefoot.componentsexample.model.Hero;
import info.bluefoot.datamodel.LazyDataModel;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.validator.GenericValidator;
import org.springframework.context.annotation.Scope;

@Named
@Scope("request")
public class HeroBean extends GenericCrudBean<Hero> {

    @Inject
    public HeroBean(Dao<Hero> dao, LazyDataModel<Hero> data) {
        super(dao, data);
    }

    // ~ =================================================================
    
    @Override
    public Hero newEntity() {
        return new Hero();
    }

    @Override
    protected Map<String, String> getSearchFields(Hero searchObj) {
        Map<String, String> filters = new HashMap<String, String>();
        if (searchObj != null) {
            if (!GenericValidator.isBlankOrNull(searchObj.getName())) {
                filters.put("name", searchObj.getName());
            }
            if (!GenericValidator.isBlankOrNull(searchObj.getLocation())) {
                filters.put("location", searchObj.getLocation());
            }
        }
        return filters;
    }

}
