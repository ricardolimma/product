## Executar a aplicação

## Gerando DTOs e Controllers

O `pom.xml` já contém o **openapi-generator-maven-plugin** configurado.

**Gerar fontes (Linux/Mac):**
```bash
./mvnw clean generate-sources
```

**Windows:**
```bash
mvn clean generate-sources
```

> O plugin adiciona `target/generated-sources/openapi` ao classpath automaticamente.

---


---

**Compilar e rodar:**

Linux/Mac:
```bash
./mvnw -U clean spring-boot:run
```

Windows:
```bash
mvn -U clean spring-boot:run
```

A API sobe em: **http://localhost:8080**

---

## Dados de exemplo (seed)

Crie arquivos JSON em `src/main/resources/data/products/`. Exemplo:

```json
{
  "productId": "MLB-5268050332",
  "title": "Copo Térmico Gigante 12L Inox com Tampa e Canudo Inox",
  "price": { "currency": "BRL", "amount": 199.90 },
  "images": ["https://http2.mlstatic.com/D_NQ_NP_2X_....jpg"],
  "description": "Copo térmico de aço inox...",
  "attributes": [{ "name": "Capacidade", "value": "12 L" }],
  "category": { "id": "MLB12345", "name": "Cozinha e Utensílios" },
  "seller": { "id": "123456789", "name": "Vendedor Exemplar", "reputation": { "level": "Platinum", "score": 99.8 } },
  "stock": { "available": true, "quantity": 15 },
  "shipping": { "free": false, "cost": { "currency": "BRL", "amount": 25.00 }, "estimatedDelivery": "3-5 dias úteis" },
  "reviews": { "rating": 4.8, "totalReviews": 250 }
}
```

---

## Endpoints

### `/products` — lista paginada

**Requisição**
```
GET /products?page=0&size=10
```

**Resposta 200 (exemplo)**
```json
{
  "items": [
    {
      "productId": "MLB-5268050332",
      "title": "Copo Térmico Gigante 12L Inox com Tampa e Canudo Inox",
      "price": { "currency": "BRL", "amount": 199.9, "originalAmount": 249.9, "discountPercentage": 20.0 },
      "images": ["https://http2.mlstatic.com/D_NQ_NP_2X_....jpg"],
      "description": "Copo térmico de aço inox...",
      "attributes": [{ "name": "Capacidade", "value": "12 L" }],
      "category": { "id": "MLB12345", "name": "Cozinha e Utensílios" },
      "seller": { "id": "123456789", "name": "Vendedor Exemplar", "reputation": { "level": "Platinum", "score": 99.8 } },
      "stock": { "available": true, "quantity": 15 },
      "shipping": { "free": false, "cost": { "currency": "BRL", "amount": 25.0 }, "estimatedDelivery": "3-5 dias úteis" },
      "reviews": { "rating": 4.8, "totalReviews": 250 }
    }
  ],
  "page": 0,
  "size": 10,
  "totalItems": 1,
  "totalPages": 1,
  "hasNext": false,
  "hasPrevious": false
}
```

**cURL**
```bash
curl "http://localhost:8080/product/v1/products?page=0&size=10"
```

### `/products/{id}` — detalhe

**Observação importante:** para testar este endpoint, **liste os produtos primeiro** (`GET /products`) e utilize um `productId` válido obtido de `items[].productId`.

**Requisição**
```
GET /products/MLB-5268050332
```

**Resposta 200 (exemplo)**
```json
{
  "productId": "MLB-5268050332",
  "title": "Copo Térmico Gigante 12L Inox com Tampa e Canudo Inox",
  "price": { "currency": "BRL", "amount": 199.90 },
  "images": ["https://http2.mlstatic.com/D_NQ_NP_2X_....jpg"],
  "description": "Copo térmico de aço inox...",
  "attributes": [{ "name": "Capacidade", "value": "12 L" }],
  "category": { "id": "MLB12345", "name": "Cozinha e Utensílios" },
  "seller": { "id": "123456789", "name": "Vendedor Exemplar", "reputation": { "level": "Platinum", "score": 99.8 } },
  "stock": { "available": true, "quantity": 15 },
  "shipping": { "free": false, "cost": { "currency": "BRL", "amount": 25.00 }, "estimatedDelivery": "3-5 dias úteis" },
  "reviews": { "rating": 4.8, "totalReviews": 250 }
}
```

**cURL**
```bash
curl "http://localhost:8080/product/v1/products/MLB-5268050332"
```
