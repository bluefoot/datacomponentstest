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
import info.bluefoot.componentsexample.dao.NotFoundException;
import info.bluefoot.datamodel.LazyDataModel;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean created to control the flow of CRUD pages.
 * @author bluefoot
 *
 * @param <P> model type of entity in which this bean will act on CRUD operations
 */
public abstract class GenericCrudBean<P> {
    private static final long serialVersionUID = -7267945082923779446L;
    private static final int DEFAULT_PAGE_SIZE = 10;
    protected static final String MESSAGE_HASH = "#msg";
    protected static final String FLASH_MESSAGE_KEY = "msg";
    protected P obj;
    protected P searchObj;
    protected LazyDataModel<P> data;
    protected Dao<P> dao;
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    // ~ Parameters ==========================================================

    protected Integer page;
    protected String sortField;
    protected String sortOrder;
    /**
     * The current id of the current entity that it's being done something with it.
     * setCurrentId will update this property AND perform a database query to
     * set the current obj
     */
    protected Integer currentId;
    /**
     * the current id that it's being updated. Why don't we use currentId
     * instead? Because setCurrentId must search the database to set the current
     * obj. The setter method of this property only sets the property. nothing
     * more
     */
    protected Integer updatedId;

    // ~ Construtor  ==========================================================

    public GenericCrudBean(Dao<P> dao, LazyDataModel<P> data) {
        resetEntity();
        resetSearchEntity();
        this.dao = dao;
        this.page = 1;
        this.data = data;
        this.data.setPageSize(GenericCrudBean.DEFAULT_PAGE_SIZE);
    }

    /**
     * This method will set the search filters. It should be called <em>before</em>
     * any view component calls {@link LazyDataModel#getWrappedData()} on
     * {@link #getData()}.
     */
    public void applyFilters() {
        data.setFilters(getSearchFields(this.searchObj));
    }

    protected void resetEntity() {
        setObj(newEntity());
    }

    protected void resetSearchEntity() {
        setSearchObj(newEntity());
    }

    // ~ Getters/setters   ====================================================

    public Integer getUpdatedId() {
        return updatedId;
    }

    public void setUpdatedId(Integer updatedId) {
        this.updatedId = updatedId;
    }

    public Integer getCurrentId() {
        return currentId;
    }

    /**
     * Sets {@link #currentId} and performs a search by calling {@link Dao#findById(Integer)}. <br />
     * If nothing is found, sets {@value HttpServletResponse#SC_NOT_FOUND} as the
     * HTTP status, and sets the current object as <tt>null</tt>.
     * @param currentId a valid id of an entity
     */
    public void setCurrentId(Integer currentId) {
        this.currentId = currentId;
        try {
            this.obj = dao.findById(currentId);
        } catch (NotFoundException e) {
            FacesContext.getCurrentInstance().getExternalContext().setResponseStatus(HttpServletResponse.SC_NOT_FOUND);
            //FIXME this behavior is strange. Normally it sets as a new entity.
            //probably the new entity behavior is the one that is strange.
            this.obj = null;
        }
    }

    public P getSearchObj() {
        return searchObj;
    }

    public void setSearchObj(P searchObj) {
        this.searchObj = searchObj;
    }

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

    // ~ Actions  =============================================================

    /**
     * Updates the current {@link #getObj()} by calling {@link Dao#update(Object)}. <br />
     * Then, puts a flash and redirects back to the view page.
     */
    public String update() {
        dao.update(obj);
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getFlash().put(GenericCrudBean.FLASH_MESSAGE_KEY, "Updated successfully");

        // ~ The code below is for adding a hash into the returning url
        // ~ the hash must point to an anchor of the returning message.
        // ~ The problem with that solution is that we must know what
        // ~ is mapped for the FacesServlet, and if we change it there...
        try {
            ec.redirect(String.format("%s/f/view.xhtml?i=%s%s",
                    ec.getRequestContextPath(), getUpdatedId(),
                    GenericCrudBean.MESSAGE_HASH));
        } catch (IOException e) {
            //if we get an error, just load the page without the hash
            //TODO log this
            return "view?faces-redirect=true&includeViewParams=true";
        }
        return null;
    }

    // ~ Abstract methods  ====================================================

    /**
     * Deletes the object passed as parameter, by calling {@link Dao#delete(Object)}.
     */
    public String delete(P h) {
        dao.delete(h);
        data.deleteItem(h);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put(GenericCrudBean.FLASH_MESSAGE_KEY, "Deleted successfully");
        return "index";
    }

    /**
     * Creates a new instance of <tt>P</tt>.
     * @return the new instance created
     */
    public abstract P newEntity();

    /**
     * <em>Parses</em> the {@link #getSearchObj()} into a {@link Map} of
     * the fields that should be part of the searches. Is not guaranteed that
     * all the fields will be returned. The implementation can customize it
     * the way it wants.
     * @param searchObj the search object
     * @return the Map
     */
    protected abstract Map<String, String> getSearchFields(P searchObj);
}
