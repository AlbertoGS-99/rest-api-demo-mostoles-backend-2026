package com.example.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.example.entities.Presentation;
import com.example.entities.Product;
import com.example.services.ProductService;
import com.example.spring_security_jwt.payload.request.LogginRequest;

import tools.jackson.databind.ObjectMapper;

/**
 * Test de integracion al endpoint que lista todos los productos de una
 * presentacion concreta, cuyo nombre se recibe como parametro de la peticion:
 * 
 * http://localhost:8080/products/products-by-presentation?presentation=unidad
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@AutoConfigureMockMvc
class ProductControllerExamenPractico4Test {

	private static final String PRESENTATION = "unidad";
	private static final String ENDPOINT = "/products/products-by-presentation";

	@Autowired
	MockMvc mockMvc;

	@MockitoBean
	ProductService productService;

	@Autowired
	ObjectMapper objectMapper;

	List<Product> products = new ArrayList<>();
	Presentation presentation;
	Product product1, product2;

	String userToken;

	@BeforeEach
	void setUp() throws Exception {

		// Obtener un token valido de un usuario registrado (rol USER)
		this.userToken = obtenerToken("user1", "Temp2026$$##");

		presentation = Presentation.builder()
				.name(PRESENTATION)
				.description("por unidades")
				.build();
		presentation.setId(1);

		product1 = Product.builder()
				.name("rezma de papel")
				.description("Description")
				.price(new BigDecimal(3.75))
				.stock(10)
				.presentation(presentation)
				.build();
		product1.setId(1);

		product2 = Product.builder()
				.name("guitarra de juguete")
				.description("Description")
				.price(new BigDecimal(4.5))
				.stock(5)
				.presentation(presentation)
				.build();
		product2.setId(3);

		products.add(product1);
		products.add(product2);
	}

	/**
	 * Realiza el login contra /api/auth/signin y devuelve el token JWT listo para
	 * usarse en la cabecera Authorization.
	 */
	private String obtenerToken(String username, String password) throws Exception {

		LogginRequest logginRequest = LogginRequest.builder()
				.username(username)
				.password(password)
				.build();

		String jsonLogginRequest = objectMapper.writeValueAsString(logginRequest);

		ResultActions resultActions = this.mockMvc.perform(
				post("/api/auth/signin")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonLogginRequest));

		MvcResult mvcResult = resultActions.andReturn();

		String contentAsString = mvcResult.getResponse().getContentAsString();

		JSONObject jsonObject = new JSONObject(contentAsString);

		return "Bearer " + jsonObject.getString("token");
	}

	
	
	
	 // mvnw test -Dtest=ProductControllerExamenPractico4Test#testListarProductosPorPresentacionUsuarioNoAutenticado
	
	@Test
	@DisplayName("Listar productos por presentacion con usuario NO autenticado ")
	void testListarProductosPorPresentacionUsuarioNoAutenticado() throws Exception {

		// when

		MockHttpServletResponse response = mockMvc
				.perform(get(ENDPOINT)
						.param("presentation", PRESENTATION)
						.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isUnauthorized())
				.andReturn()
				.getResponse();

		// then

		assertEquals(401, response.getStatus());
	}

	
	
	
	 // mvnw test -Dtest=ProductControllerExamenPractico4Test#testListarProductosPorPresentacionUsuarioAutenticado
	  
	
	@Test
	@DisplayName("Listar productos por presentacion con usuario registrado")
	void testListarProductosPorPresentacionUsuarioAutenticado() throws Exception {

		// given
		given(productService.findByPresentationName(PRESENTATION)).willReturn(products);

		// when
		mockMvc.perform(get(ENDPOINT)
				.param("presentation", PRESENTATION)
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", this.userToken))
				.andDo(print())
		// then
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()", is(products.size())))
				.andExpect(jsonPath("$[0].name", is(product1.getName())))
				.andExpect(jsonPath("$[0].presentation.name", is(PRESENTATION)))
				.andExpect(jsonPath("$[0]._links").doesNotExist());
	}
}
