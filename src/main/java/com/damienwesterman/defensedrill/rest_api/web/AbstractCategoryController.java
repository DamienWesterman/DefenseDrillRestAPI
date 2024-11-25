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

package com.damienwesterman.defensedrill.rest_api.web;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.damienwesterman.defensedrill.rest_api.entity.AbstractCategoryEntity;
import com.damienwesterman.defensedrill.rest_api.repository.AbstractCategoryRepo;
import com.damienwesterman.defensedrill.rest_api.service.AbstractCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// TODO: DOC COMMENTS
// TODO: Swagger Comments (on the DTOs?)
@RequiredArgsConstructor
public abstract class AbstractCategoryController
        <E extends AbstractCategoryEntity,
        S extends AbstractCategoryService<E, ? extends AbstractCategoryRepo<E>>> {
    protected final S service;

    @GetMapping
    public ResponseEntity<List<E>> getAll() {
        List<E> abstractCategories = service.findAll();

        if (abstractCategories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(abstractCategories);
    }

    @PostMapping
    public ResponseEntity<E> insertNewAbstractCategory(@RequestBody @Valid E abstractCategory) {
        E createdAbstractCategory = service.save(abstractCategory);
        return ResponseEntity
            .created(URI.create(getEndpoint() + "/" + createdAbstractCategory.getId()))
            .body(createdAbstractCategory);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<E> getAbstractCategoryById(@PathVariable Long id) {
        return service.find(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<E> updateAbstractCategoryById(
            @PathVariable Long id, @RequestBody @Valid E abstractCategory) {
        if (abstractCategory.getId() == null || abstractCategory.getId() != id) {
            return ResponseEntity.badRequest().build();
        }

        if (!service.find(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        E updatedAbstractCategory = service.save(abstractCategory);
        return ResponseEntity.ok(updatedAbstractCategory);
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<String> deleteAbstractCategoryById(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<E> getAbstractCategoryByName(@PathVariable String name) {
        return service.find(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    protected abstract String getEndpoint();
}
