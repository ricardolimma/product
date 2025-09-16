package com.product.app.controller;

import com.product.app.service.ProductService;
import com.product.contract.model.Product;
import com.product.contract.model.ProductPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController controller;

    @Test
    void constructor_injectsDependency() {
        // também cobre a linha do construtor explicitamente
        ProductController local = new ProductController(productService);
        assertNotNull(local);
    }

    @Test
    void getProductById_returnsOkWithBody() {
        // arrange
        String productId = "MLB-5268050332";
        Product product = new Product(); // objeto simples; vamos só verificar a mesma instância
        when(productService.getProduct(productId)).thenReturn(product);

        // act
        ResponseEntity<Product> response = controller.getProductById(productId);

        // assert
        assertEquals(200, response.getStatusCode().value());
        assertSame(product, response.getBody());
        verify(productService, times(1)).getProduct(productId);
        verifyNoMoreInteractions(productService);
    }

    @Test
    void listProducts_returnsOkWithBody() {
        // arrange
        Integer page = 1;
        Integer size = 5;
        ProductPage pageDto = new ProductPage();
        when(productService.listProducts(page, size)).thenReturn(pageDto);

        // act
        ResponseEntity<ProductPage> response = controller.listProducts(page, size);

        // assert
        assertEquals(200, response.getStatusCode().value());
        assertSame(pageDto, response.getBody());
        verify(productService, times(1)).listProducts(page, size);
        verifyNoMoreInteractions(productService);
    }
}
