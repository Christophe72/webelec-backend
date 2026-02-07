package com.webelec.client.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webelec.client.ClientEntity;
import com.webelec.client.dto.ClientCreateRequest;
import com.webelec.client.dto.ClientUpdateRequest;
import com.webelec.repository.ClientRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ClientControllerIT {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ClientRepository clientRepository;

	@Autowired
	ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		clientRepository.deleteAll();

		ClientEntity client1 = new ClientEntity();
		client1.setNom("Dupont");
		client1.setPrenom("Alain");
		client1.setCreatedAt(Instant.parse("2024-01-01T10:00:00Z"));
		clientRepository.save(client1);

		ClientEntity client2 = new ClientEntity();
		client2.setNom("Dupont");
		client2.setPrenom("Bernard");
		client2.setCreatedAt(Instant.parse("2024-01-02T10:00:00Z"));
		clientRepository.save(client2);

		ClientEntity client3 = new ClientEntity();
		client3.setNom("Martin");
		client3.setPrenom("Alice");
		client3.setCreatedAt(Instant.parse("2024-01-03T10:00:00Z"));
		clientRepository.save(client3);
	}

	// ------------------------------------------------------------------
	// Multi-field sorting
	// ------------------------------------------------------------------

	@Test
	void shouldSortByLastNameThenFirstNameAsc() throws Exception {
		mockMvc.perform(get("/api/clients").param("sort", "nom,asc").param("sort", "prenom,asc")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].nom").value("Dupont"))
				.andExpect(jsonPath("$.content[0].prenom").value("Alain"))
				.andExpect(jsonPath("$.content[1].nom").value("Dupont"))
				.andExpect(jsonPath("$.content[1].prenom").value("Bernard"))
				.andExpect(jsonPath("$.content[2].nom").value("Martin"));
	}

	@Test
	void shouldSortByLastNameAscThenCreatedAtDesc() throws Exception {
		mockMvc.perform(get("/api/clients").param("sort", "nom,asc").param("sort", "createdAt,desc"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.content[0].nom").value("Dupont"))
				.andExpect(jsonPath("$.content[0].prenom").value("Bernard"))
				.andExpect(jsonPath("$.content[1].prenom").value("Alain"));
	}

	@Test
	void shouldKeepSortingWhenPaginating() throws Exception {
		mockMvc.perform(get("/api/clients").param("sort", "nom,asc").param("sort", "prenom,asc").param("page", "0")
				.param("size", "2").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.content.length()").value(2))
				.andExpect(jsonPath("$.content[0].nom").value("Dupont"))
				.andExpect(jsonPath("$.content[0].prenom").value("Alain"))
				.andExpect(jsonPath("$.content[1].prenom").value("Bernard"));
	}

	@Test
	void shouldFailWhenSortingByUnknownField() throws Exception {
		mockMvc.perform(get("/api/clients").param("sort", "unknownField,asc"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_SORT_FIELD"))
            .andExpect(jsonPath("$.field").value("unknownField"));
	}

	@Test
	void shouldPaginateClients() throws Exception {
		mockMvc.perform(get("/api/clients").param("page", "0").param("size", "2").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content.length()").value(2)).andExpect(jsonPath("$.size").value(2))
				.andExpect(jsonPath("$.number").value(0)).andExpect(jsonPath("$.totalElements").value(3));
	}

	@Test
	void shouldGetClientById() throws Exception {
        ClientEntity client = clientRepository.findAll().get(0);

        mockMvc.perform(get("/api/clients/{id}", client.getId())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(client.getId().toString()))
            .andExpect(jsonPath("$.nom").value(client.getNom()))
            .andExpect(jsonPath("$.prenom").value(client.getPrenom()));
    }

    @Test
    void shouldCreateClient() throws Exception {
        ClientCreateRequest request = new ClientCreateRequest(
                "Petit",
                "Luc",
                "luc.petit@webelec.be",
                "0488123456"
        );

        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(jsonPath("$.nom").value("Petit"))
            .andExpect(jsonPath("$.prenom").value("Luc"));
    }

    @Test
    void shouldUpdateClient() throws Exception {
        ClientEntity client = clientRepository.findAll().get(0);
        ClientUpdateRequest request = new ClientUpdateRequest(
                "Dubois",
                "Claire",
                "claire.dubois@webelec.be",
                "0477001122"
        );

        mockMvc.perform(put("/api/clients/{id}", client.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(client.getId().toString()))
            .andExpect(jsonPath("$.nom").value("Dubois"))
            .andExpect(jsonPath("$.prenom").value("Claire"));
    }

    @Test
    void shouldDeleteClient() throws Exception {
        ClientEntity client = clientRepository.findAll().get(0);

        mockMvc.perform(delete("/api/clients/{id}", client.getId()))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/clients/{id}", client.getId()))
            .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

}