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

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.damienwesterman.defensedrill.rest_api.entity.AbstractCategoryEntity;
import com.damienwesterman.defensedrill.rest_api.entity.CategoryEntity;
import com.damienwesterman.defensedrill.rest_api.entity.SubCategoryEntity;
import com.damienwesterman.defensedrill.rest_api.exception.DatabaseInsertException;
import com.damienwesterman.defensedrill.rest_api.repository.CategoryRepo;
import com.damienwesterman.defensedrill.rest_api.repository.SubCategoryRepo;

import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CategoriesService {
    private final CategoryRepo categoryRepo;
    private final SubCategoryRepo subCategoryRepo;
    // TODO: FINISH ME

    public void save(AbstractCategoryEntity abstractCategory) {
        try {
            if ( abstractCategory instanceof CategoryEntity) {
                categoryRepo.save((CategoryEntity) abstractCategory);
            } else if (abstractCategory instanceof SubCategoryEntity) {
                subCategoryRepo.save((SubCategoryEntity) abstractCategory);
            }
        } catch (ConstraintViolationException cve) {
            System.out.println("ERROR: " + ErrorMessageUtils.exceptionToErrorMessage(cve));
            throw new DatabaseInsertException(
                ErrorMessageUtils.exceptionToErrorMessage(cve), cve
            );
        } catch (DataIntegrityViolationException dive) {
            System.out.println("ERROR: " + ErrorMessageUtils.exceptionToErrorMessage(dive));
            throw new DatabaseInsertException(
                ErrorMessageUtils.exceptionToErrorMessage(dive), dive
            );
        }
    }
}
