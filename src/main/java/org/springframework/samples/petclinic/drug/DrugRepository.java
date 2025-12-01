/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.drug;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository class for <code>Drug</code> domain objects. All method names are compliant
 * with Spring Data naming conventions so this interface can easily be extended for Spring
 * Data. See:
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-creation
 *
 * @author PetClinic
 */
public interface DrugRepository extends JpaRepository<Drug, Integer> {

	/**
	 * Retrieve {@link Drug}s from the data store by name, returning all drugs whose name
	 * <i>starts</i> with the given name.
	 * @param name Value to search for
	 * @param pageable Pagination information
	 * @return a Page of matching {@link Drug}s (or an empty Page if none found)
	 */
	Page<Drug> findByNameStartingWith(String name, Pageable pageable);

	/**
	 * Retrieve a {@link Drug} from the data store by id.
	 * @param id the id to search for
	 * @return an {@link Optional} containing the {@link Drug} if found, or an empty
	 * {@link Optional} if not found.
	 */
	Optional<Drug> findById(Integer id);

}
