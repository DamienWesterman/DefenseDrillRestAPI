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

package com.damienwesterman.defensedrill.rest_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.damienwesterman.defensedrill.rest_api.entity.DrillEntity;

/**
 * Database repository for {@link DrillEntity}.
 */
@Repository
public interface DrillRepo extends JpaRepository<DrillEntity, Long> {
    Optional<DrillEntity> findByNameIgnoreCase(String name);
    List<DrillEntity> findByUpdateTimestampGreaterThan(Long updateTimestamp, Sort sort);
    List<DrillEntity> findByCategoriesIdIn(List<Long> categoryIds, Sort sort);
    List<DrillEntity> findBySubCategoriesIdIn(List<Long> subCategoryIds, Sort sort);
}
