package com.product.app.service.impl;

import com.product.app.mapper.ProductMapper;
import com.product.contract.model.Product;
import com.product.contract.model.ProductPage;
import com.product.domain.entity.ProductEntity;
import com.product.domain.repository.ProductRepository;
import com.product.infra.exception.ProductException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl service;

    @Test
    void getProduct_found_returnsMappedProduct() {
        // arrange
        String id = "MLB-5268050332";
        ProductEntity entity = new ProductEntity();
        Product dto = new Product();

        when(productRepository.findById(id)).thenReturn(Optional.of(entity));
        when(productMapper.toProduct(entity)).thenReturn(dto);

        // act
        Product result = service.getProduct(id);

        // assert
        assertSame(dto, result);
        verify(productRepository, times(1)).findById(id);
        verify(productMapper, times(1)).toProduct(entity);
        verifyNoMoreInteractions(productRepository, productMapper);
    }

    @Test
    void getProduct_notFound_throwsProductException() {
        // arrange
        String id = "NAO-EXISTE";
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        // act + assert
        assertThrows(ProductException.class, () -> service.getProduct(id));
        verify(productRepository, times(1)).findById(id);
        verifyNoMoreInteractions(productRepository);
        verifyNoInteractions(productMapper);
    }

    @Test
    void listProducts_nullPageAndSize_usesDefaultsAndMaps() {
        // arrange (page=null, size=null -> p=0, s=10)
        ProductEntity e1 = new ProductEntity();
        ProductEntity e2 = new ProductEntity();
        ProductEntity e3 = new ProductEntity();
        List<ProductEntity> all = Arrays.asList(e1, e2, e3);

        List<Product> items = Arrays.asList(new Product(), new Product(), new Product());
        ProductPage pageDto = new ProductPage();

        when(productRepository.findAll()).thenReturn(all);
        when(productMapper.toDTOList(all)).thenReturn(items);
        when(productMapper.toProductPage(eq(items), eq(0), eq(10), eq(3L))).thenReturn(pageDto);

        // act
        ProductPage result = service.listProducts(null, null);

        // assert
        assertSame(pageDto, result);
        verify(productRepository, times(1)).findAll();
        verify(productMapper, times(1)).toDTOList(all);
        verify(productMapper, times(1)).toProductPage(eq(items), eq(0), eq(10), eq(3L));
        verifyNoMoreInteractions(productRepository, productMapper);
    }

    @Test
    void listProducts_outOfRange_returnsEmptySlice() {
        // arrange: 3 itens; com page=5,size=2 => offset=10 >= total(3) => slice vazio
        int page = 5, size = 2;
        List<ProductEntity> all = Arrays.asList(new ProductEntity(), new ProductEntity(), new ProductEntity());
        List<Product> emptyItems = List.of();
        ProductPage pageDto = new ProductPage();

        when(productRepository.findAll()).thenReturn(all);
        when(productMapper.toDTOList(emptyList())).thenReturn(emptyItems);
        when(productMapper.toProductPage(eq(emptyItems), eq(page), eq(size), eq(3L))).thenReturn(pageDto);

        // act
        ProductPage result = service.listProducts(page, size);

        // assert
        assertSame(pageDto, result);
        verify(productRepository, times(1)).findAll();
        verify(productMapper, times(1)).toDTOList(emptyList());
        verify(productMapper, times(1)).toProductPage(eq(emptyItems), eq(page), eq(size), eq(3L));
        verifyNoMoreInteractions(productRepository, productMapper);
    }

    @Test
    void listProducts_negativeValues_areClamped() {
        // arrange: page=-2 => 0; size=0 => 1
        Integer page = -2, size = 0;
        List<ProductEntity> all = List.of(new ProductEntity());
        List<Product> items = List.of(new Product());
        ProductPage pageDto = new ProductPage();

        when(productRepository.findAll()).thenReturn(all);
        when(productMapper.toDTOList(all)).thenReturn(items);
        when(productMapper.toProductPage(eq(items), eq(0), eq(1), eq(1L))).thenReturn(pageDto);

        // act
        ProductPage result = service.listProducts(page, size);

        // assert
        assertSame(pageDto, result);
        verify(productRepository, times(1)).findAll();
        verify(productMapper, times(1)).toDTOList(all);
        verify(productMapper, times(1)).toProductPage(eq(items), eq(0), eq(1), eq(1L));
        verifyNoMoreInteractions(productRepository, productMapper);
    }
}
