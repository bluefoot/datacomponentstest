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
package info.bluefoot.componentsexample.dao.support;

import info.bluefoot.componentsexample.dao.Dao;
import info.bluefoot.componentsexample.dao.NotFoundException;
import info.bluefoot.componentsexample.model.Hero;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate3.HibernateTemplate;

@Named
@Scope("prototype")
public class HeroDao implements Dao<Hero> {

    @Inject
    HibernateTemplate ht;
    
    @Override
    public void add(Hero obj) {
        ht.save(obj);
    }

    @Override
    public void delete(Hero obj) {
        ht.delete(obj);
    }

    @Override
    public List<Hero> findAll(Integer firstResult, Integer pageSize,
            String sortField, Boolean sortOrder) {
        return findAll(firstResult, pageSize, sortField, sortOrder, null);
    }

    @Override
    public Integer count(Map<String, String> filter) {
        Long result = 0L;
        DetachedCriteria criteria = DetachedCriteria.forClass(Hero.class)
                .setProjection(Projections.rowCount());
        
        if(filter!=null) {
            for (String key : filter.keySet()) {
                criteria.add(Restrictions.ilike(key, filter.get(key)));
            }
        }

        List results = ht.findByCriteria(criteria);
        if (results != null  && results.size() > 0) {
            result = (Long) results.get(0);
        }
        
        return result.intValue();
    }

    @Override
    public List<Hero> findAll(Integer firstResult, Integer pageSize,
            String sortField, Boolean sortOrder, Map<String, String> filter) {
        List<Hero> result = new ArrayList<Hero>();
        
        DetachedCriteria criteria = DetachedCriteria.forClass(Hero.class);
        if(sortField != null) {
            if(sortOrder) {
                criteria.addOrder(Order.asc(sortField));
            } else {
                criteria.addOrder(Order.desc(sortField));
            }
        }
        
        if(filter!=null) {
            for (String key : filter.keySet()) {
                criteria.add(Restrictions.ilike(key, filter.get(key)));
            }
        }
        
        result.addAll(ht.findByCriteria(criteria, firstResult, pageSize));
        
        return result;
    }

    @Override
    public Hero findById(Integer id) {
        Hero hero = ht.get(Hero.class, id);
        if(hero == null) {
            throw new NotFoundException("Not found");
        }
        return hero;
    }

    @Override
    public void update(Hero obj) {
        ht.update(obj);
        ht.flush();
    }
}
