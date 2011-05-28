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

import info.bluefoot.componentsexample.dao.Dao;
import info.bluefoot.datamodel.LazyDataModel;

import javax.faces.bean.ManagedProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GenericCrudBean<P> {
    private static final long serialVersionUID = -7267945082923779446L;
    private static final int DEFAULT_PAGE_SIZE = 10;
    protected P obj;
	protected LazyDataModel<P> data;
	protected Dao<P> dao;
	protected Logger log = LoggerFactory.getLogger(this.getClass());
	
    // ~ Parameters ==========================================================
	
    @ManagedProperty("#{param.page}")
    protected Integer page;
	@ManagedProperty("#{param.sort}")
	protected String sortField;
	@ManagedProperty("#{param.order}")
	protected String sortOrder;
	
    // ~ Construtor  ==========================================================
	
	public GenericCrudBean(Dao<P> dao, LazyDataModel<P> data) {
		this.obj = newEntity();
		this.dao = dao;
		this.page = 1;
		this.data = data;
		this.data.setPageSize(DEFAULT_PAGE_SIZE);
	}

	protected void resetEntity() {
	    setObj(newEntity());
	}
	
    // ~ Getters/setters   ====================================================
	
	public P getObj() {
		return obj;
	}

	public void setObj(P obj) {
		this.obj = obj;
	}

    public LazyDataModel<P> getData() {
        return data;
    }
    
    public void setData(LazyDataModel<P> data) {
        this.data = data;
    }
    
    public Integer getPage() {
        return page;
    }
    
    public void setPage(Integer page) {
        this.page = page;
        data.setCurrentPage(page);
    }
    
    public String getSortField() {
        return sortField;
    }
    
    public void setSortField(String sortField) {
        this.sortField = sortField;
        data.setSortField(sortField);
    }

    public String getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
        data.setSortOrder(sortOrder.equals("asc") ? true : false);
    }
    
	// ~ Abstract methods  ====================================================

    public abstract P newEntity();
}
