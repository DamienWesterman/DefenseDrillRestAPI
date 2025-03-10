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

package com.damienwesterman.defensedrill.rest_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@SpringBootApplication
@EnableDiscoveryClient
public class DefenseDrillRestApiApplication {
	// TODO: The following using TDD
	// TODO: Add last modified timestamp (UTC) to drills, categories, and sub-categories in the DB and entities
	// TODO: See what other entities need to be modified for the timestamp
	// TODO: Update endpoints (create/update) to include the now time, maybe using UTC
	// TODO: Create and update endpoint for drills, categories, and sub-categories that has a parameter for last updated and return anything updated after that point
	// TODO: Add the above endpoint to the docs and update the docs
	// TODO: Re-create the rest-api docs

	// TODO: The following using TDD
	// TODO: Returns instructions alphabetically (adjust docs?)

	// TODO: The following using TDD
	// TODO: Add an endpoint that takes a list of drills, categories, or sub-categoies ID's and returns the list
	// TODO: Add the above endpoint to the docs and update the docs
	// TODO: Re-create the rest-api docs

	public static void main(String[] args) {
		SpringApplication.run(DefenseDrillRestApiApplication.class, args);
	}

	/*
	 * In production, swagger is unavailable behind the gateway. We do allow Redocly as the
	 * API documentation in production, found at /docs/index.html. In order to update, do
	 * the following:
	 * 		1. Make sure to run the supporting docker containers
	 * 			- Run 'make run-dev-local' from the main directory
	 * 		2. Start up at least the config-server
	 * 		3. Run the following commands:
	 * 			- npm install -g redoc-cli
	 * 			- redoc-cli bundle http://localhost:5433/v3/api-docs -o static-swagger.html
	 * 			- mv static-swagger.html src/main/resources/static/docs/index.html
	 * 		4. Push the changes
	 */

    @Bean
    OpenAPI swaggerHeader() {
		return new OpenAPI()
			.info(new Info()
				.title("DefenseDrill Rest API")
				.description("Rest API for managing drills, categories, and instructions.")
				.version("1.0.0")
			);
	}
}
