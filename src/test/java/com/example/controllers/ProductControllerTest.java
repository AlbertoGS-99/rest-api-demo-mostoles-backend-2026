package com.example.controllers;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.example.entities.Presentation;
import com.example.entities.Product;
import com.example.services.ProductService;
import com.example.utilities.FileDownloadUtil;
import com.example.utilities.FileUploadUtil;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(ProductController.class)

/**
 * La anotacion anterior es la recomendada para implementar test de Integracion,
 * a la capa de controladores que conlleva la realizacion de peticiones HTTP.
 * 
 * Esta anotacion no serviria si tuviesemos implementada la seguridad con Spring
 * Security porque no carga todo el contexto de Spring. Cuando se implemente la
 * seguridad, comentaremos esta anotacion y utilizaremos @SpringBootTest
 */

/*
 * La siguiente anotacion se utiliza cuando queremos utilizar una base de datos
 * real, que no sea H2 Database, MySQL por ejemplo, y que al terminar la prueba
 * se deje la base de datos tal y como estaba
 */
@AutoConfigureTestDatabase(replace = Replace.NONE)

/**
 * Se necesita MockMvc para realizar peticiones a los end points, lo cual
 * suministra y configura la anotacion siguiente
 */
@AutoConfigureMockMvc
class ProductControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockitoBean
	ProductService productService;

	@MockitoBean
	FileUploadUtil fileUploadUtil;

	@MockitoBean
	FileDownloadUtil fileDownloadUtil;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	@DisplayName("Controller Test que recupera todos los productos")
	void testFindAll() throws Exception {

		// given
		List<Product> products = new ArrayList<>();

		Presentation presetation = Presentation.builder().name("decenas").description("Por decenas").build();

		Product product = Product.builder().name("Camara").description("HP Camara").price(new BigDecimal(500))
				.stock(1900).productImage(null).presentation(presetation).build();

		Presentation presetation1 = Presentation.builder().name("unidades").description("Por unidades").build();

		Product product1 = Product.builder().name("Frigorifico").description("General Electric")
				.price(new BigDecimal(2500)).stock(3900).productImage(null).presentation(presetation1).build();

		products.add(product);
		products.add(product1);

		given(productService.findAll(Sort.by("name"))).willReturn(products);

		// when => Realizar la peticion (request) HTTP, mediante el metodo GET
		// al end point de products ("/products"). Aqui se utiliza MockMvc

		ResultActions response = mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON));
		// then

		response.andExpect(status().isOk()).andDo(print())
				.andExpect(jsonPath("$.products.size()", is(products.size())));

	}

}
