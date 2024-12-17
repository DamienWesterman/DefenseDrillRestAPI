/****************************\
 *      ________________      *
 *     /  _             \     *
 *     \   \ |\   _  \  /     *
 *      \  / | \ / \  \/      *
 *      /  \ | / | /  /\      *
 *     /  _/ |/  \__ /  \     *
 *     \________________/     *
 *                            *
 \****************************/
/*
 * Copyright 2024 Damien Westerman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.damienwesterman.defensedrill.rest_api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;

import com.damienwesterman.defensedrill.rest_api.entity.AbstractCategoryEntity;
import com.damienwesterman.defensedrill.rest_api.exception.DatabaseInsertException;
import com.damienwesterman.defensedrill.rest_api.repository.AbstractCategoryRepo;

/**
 * Abstract service superclass for interacting with {@link AbstractCategoryEntity} objects in the database.
 */
public abstract class AbstractCategoryService<E extends AbstractCategoryEntity, R extends AbstractCategoryRepo<E>> {
    protected final R repo;

    public AbstractCategoryService(R repo) {
        this.repo = repo;
    }

    /**
     * Save an AbstractCategoryEntity into the database.
     * <br><br>
     * This can be used for create OR update operations.
     *
     * @param abstractCategory Entity to save.
     * @return The saved entity.
     * @throws DatabaseInsertException Thrown when there is any issue saving the entity.
     */
    @NonNull
    public E save(@NonNull E abstractCategory) throws DatabaseInsertException {
        return ErrorMessageUtils.trySave(abstractCategory, repo);
    }

    /**
     * Find an entity in the database by ID - if it exists.
     *
     * @param id ID of the AbstractCategoryEntity.
     * @return Optional containing the returned entity - if it exists.
     */
    public Optional<E> find(@NonNull Long id) {
        return repo.findById(id);
    }

    /**
     * Find an entity in the database by name (case insensitive) - if it exists.
     *
     * @param name Name of the AbstractCategoryEntity.
     * @return Optional containing the returned entity - if it exists.
     */
    public Optional<E> find(@NonNull String name) {
        return repo.findByNameIgnoreCase(name);
    }

    /**
     * Return all entities in the database.
     *
     * @return List of AbstractCategoryEntity objects.
     */
    @NonNull
    public List<E> findAll() {
        return repo.findAll();
    }

    /**
     * Return all entities in the database that are in the list of IDs.
     *
     * @param ids List of AbstractCategoryEntity IDs.
     * @return List of AbstractCategoryEntity objects.
     */
    @NonNull
    public List<E> findAll(@NonNull List<Long> ids) {
        return repo.findAllById(ids);
    }

    /**
     * Delete an entity from the database by its ID - if it exists.
     *
     * @param id ID of the AbstractCategoryEntity.
     */
    public void delete(@NonNull Long id) {
        repo.deleteById(id);
    }
}
