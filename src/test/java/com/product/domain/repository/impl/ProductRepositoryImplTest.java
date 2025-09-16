package com.product.domain.repository.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.domain.entity.ProductEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryImplTest {

    @Test
    void constructor_loadsResources_cacheAndFindersWork() throws Exception {
        // Garantir que os recursos de teste estão no classpath
        PathMatchingResourcePatternResolver resolver =
                new PathMatchingResourcePatternResolver(getClass().getClassLoader());
        Resource[] resources = resolver.getResources("classpath*:data/products/*.json");
        assertTrue(resources.length >= 1,
                "Adicione ao menos 1 arquivo em src/test/resources/data/products/*.json");

        // Mock do ObjectMapper: devolve uma instância por chamada
        ObjectMapper mapper = mock(ObjectMapper.class);
        when(mapper.readValue(any(InputStream.class), eq(ProductEntity.class)))
                .thenAnswer(inv -> mock(ProductEntity.class));

        // Act: constrói o repo (executa loadAllFromClasspath)
        ProductRepositoryImpl repo = new ProductRepositoryImpl(mapper);

        // Assert: findAll deve ter o mesmo tamanho dos recursos
        List<ProductEntity> all = repo.findAll();
        assertEquals(resources.length, all.size(), "findAll() deve refletir todos os arquivos carregados");

        // Para cada arquivo, o id é o nome sem ".json" -> findById presente
        for (Resource res : resources) {
            String name = Objects.requireNonNull(res.getFilename());
            String productId = name.substring(0, name.length() - ".json".length());
            assertTrue(repo.findById(productId).isPresent(), "id esperado não localizado: " + productId);
        }

        // findById ausente
        assertTrue(repo.findById("NAO-EXISTE").isEmpty());

        // ObjectMapper chamado 1x por recurso
        verify(mapper, times(resources.length)).readValue(any(InputStream.class), eq(ProductEntity.class));
        verifyNoMoreInteractions(mapper);
    }

    @Test
    void constructor_whenMapperFails_wrapsInRuntimeException() throws Exception {
        // Garantir que existe ao menos 1 recurso para acionar o readValue
        PathMatchingResourcePatternResolver resolver =
                new PathMatchingResourcePatternResolver(getClass().getClassLoader());
        Resource[] resources = resolver.getResources("classpath*:data/products/*.json");
        assertTrue(resources.length >= 1,
                "Adicione ao menos 1 arquivo em src/test/resources/data/products/*.json");

        // Mock do ObjectMapper jogando IOException -> deve virar RuntimeException
        ObjectMapper mapper = mock(ObjectMapper.class);
        when(mapper.readValue(any(InputStream.class), eq(ProductEntity.class)))
                .thenThrow(new java.io.IOException("boom"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> new ProductRepositoryImpl(mapper));
        assertTrue(ex.getMessage().contains("Falha ao carregar produtos"));
    }
}
