# Product API

API Spring Boot (Java 21) que expõe informações de produtos. O projeto segue abordagem **Design First** com **OpenAPI 3.0.3**, gera **DTOs e controllers** automaticamente e padroniza **exceções** para respostas de erro consistentes.

---

## Sumário
- [Visão geral](#visão-geral)
- [Por que Design First?](#por-que-design-first)
- [Exceções padronizadas](#exceções-padronizadas)
- [Por que Java + Spring?](#por-que-java--spring)
- [Stack & ferramentas](#stack--ferramentas)
- [Pré-requisitos](#pré-requisitos)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Contrato OpenAPI](#contrato-openapi)
- [Dados de exemplo (seed)](#dados-de-exemplo-seed)
- [Endpoints](#endpoints)
    - [/products — lista paginada](#products--lista-paginada)
    - [/products/{id} — detalhe](#productsid--detalhe)
- [Formato de erro](#formato-de-erro)
- [Por que **MapStruct**? (vantagens práticas)](#por-que-mapstruct-vantagens-práticas)
- [Arquitetura escolhida — e por quê](#arquitetura-escolhida--e-por-quê)
- [Boas práticas com MapStruct (checklist)](#boas-práticas-com-mapstruct-checklist)
- [Boas práticas arquiteturais (checklist)](#boas-práticas-arquiteturais-checklist)
- [Observações (escopo de exemplo)](#observações-escopo-de-exemplo)

---

## Visão geral

- **Design First**: o contrato `openapi.yaml` é a **fonte da verdade**.
- **Geração de código**: modelos (DTOs) e controllers Spring gerados a partir do contrato.
- **Somente leitura**: dados vêm de `resources/data/products/*.json`, embarcados no pacote.
- **Paginação**: suporte a `page` e `size`.
- **Erros consistentes**: corpo padronizado com `code`, `message` e `timestamp`.

---

## Por que Design First?

- **Clareza de contrato**: consumidores e produtores concordam primeiro no **que** a API entrega.
- **Geração automática**: reduz _boilerplate_ (DTOs, controllers, interfaces) e acelera desenvolvimento.
- **Documentação viva**: o contrato é legível por humanos e ferramentas (Swagger UI, linters, etc.).
- **Evolução controlada**: versionamento e _diffs_ no contrato evitam _breaking changes_ involuntárias.

---

## Exceções padronizadas

Erros seguem um **template único**: `code`, `message`, `timestamp`.

Benefícios:
- **Consistência**: clientes tratam erros de forma previsível.
- **Observabilidade**: `code` facilita métricas e dashboards por tipo de falha.
- **Auditoria**: `timestamp` em formato `date-time` (RFC 3339).

**Exemplo (404):**
```json
{
  "code": "PRODUCT_NOT_FOUND",
  "message": "Produto não encontrado",
  "timestamp": "2025-09-16T10:45:12.345-03:00"
}
```

---

## Por que Java + Spring?

- **Maturidade & Ecossistema**: Spring Boot oferece starters, auto-configuração e integração com bibliotecas (Jackson, validação, etc.).
- **Desempenho & Manutenibilidade**: Java 21 (LTS) garante performance, linguagem moderna e longo suporte.
- **Padrões de Mercado**: times back-end frequentemente já dominam Spring/Java, reduzindo curva de aprendizado.
- **Ferramental**: excelente suporte IDE, testes, CI/CD e observabilidade.

---

## Stack & ferramentas

- **Java 21**, **Spring Boot 3**
- **OpenAPI 3.0.3** + **openapi-generator-maven-plugin**
- **Jackson** para JSON
- **MapStruct** para mapeamento `Entity → DTO`
- **Spring Data Commons** (`Page`, `Pageable`, `PageImpl`) — paginação em memória

---

## Pré-requisitos

- Java 21
- Maven 3.9+
- IDE com *annotation processing* habilitado (Lombok/MapStruct)

---

## Estrutura do projeto

```
src/
├─ main/java
│  ├─ com/product/ProductApplication.java         # Main Spring Boot
│  ├─ com/product/api/ProductApiDelegateImpl.java # Implementação dos endpoints gerados
│  ├─ com/product/app/...                         # Services
│  ├─ com/product/domain/...                      # Entities + repositório (JSON)
│  ├─ com/product/app/mapper/...                  # MapStruct
│  └─ com/product/infra/exception/...             # Exceções + @RestControllerAdvice
├─ main/resources
│  ├─ application.yml
│  ├─ openapi.yaml                                # Contrato (Design First)
│  └─ data/products/*.json                        # Dados embarcados (somente leitura)
└─ target/generated-sources/openapi/...           # Código gerado (DTOs/controllers)
```

---

## Contrato OpenAPI

Arquivo: `src/main/resources/openapi.yaml` (OpenAPI 3.0.3).  
Principais endpoints definidos:
- `GET /products` — lista paginada
- `GET /products/{id}` — detalhe por ID

Schemas incluem `Product`, `ProductPage`, `Price`, etc., e o schema `Error` com `code`, `message`, `timestamp`.

Para visualizar o Swagger da API acesse o site https://editor.swagger.io/ e cole o conteúdo do arquivo `src/main/resources/openapi.yaml`.
---


## Formato de erro

**Schema**: `Error`  
Campos: `code` (string), `message` (string), `timestamp` (date-time).

**Exemplo 404**
```json
{
  "code": "PRODUCT_NOT_FOUND",
  "message": "Produto não encontrado",
  "timestamp": "2025-09-16T10:45:12.345-03:00"
}
```

---

## Por que **MapStruct**? (vantagens práticas)

**MapStruct** é um gerador de _mappers_ em tempo de **compilação**. Em vez de refletir em runtime (como ModelMapper), ele **gera código Java** simples e altamente performático. Principais ganhos:

1) **Performance e baixo overhead**
    - Zero reflexão em tempo de execução; métodos de mapeamento viram chamadas diretas.
    - Ideal para endpoints de **alta taxa de chamadas** (ex.: busca de produtos em marketplace).

2) **Segurança de tipos / _fail fast_**
    - Se um campo existe no DTO e **não tem origem** na entidade (ou vice-versa), você enxerga o erro **na compilação**.
    - Evita _bugs_ silenciosos e melhora a **manutenibilidade** do contrato.

3) **Mapeamentos complexos e aninhados**
    - Suporte para **tipos compostos** (Price, Seller, Category...) e **coleções**.
    - Converte elementos (ex.: `String` → `URI`) com **métodos auxiliares**.
    - Permite _expressions_ para calcular campos (ex.: `totalPages`).

4) **Customização controlada**
    - `@Mapping` para renomear campos (`productId` ↔ `id`).
    - Estratégias de null-handling (`NullValuePropertyMappingStrategy`).
    - Mapeamento incremental com `@MappingTarget` (útil se, no futuro, houver _patch/update_).

5) **Separação de responsabilidades**
    - _Controller_ fala o **idioma do contrato** (DTO).
    - _Service/Domain_ fala o **idioma do negócio** (Entities).
    - O **mapper é a fronteira** que documenta e **automatiza** a tradução entre camadas.

6) **Testabilidade**
    - Fácil de testar: **sem mocks de reflexão** ou configuração pesada.
    - Você testa uma classe Java concreta, gerada automaticamente.

**Boa prática:** manter mappers **pequenos e coesos**, 1-para-1 por agregado, e mover lógicas de transformação mais elaboradas para **helpers** claramente nomeados (_e.g._ conversor `String ↔ URI`).

---

## Arquitetura escolhida — e por quê

A estrutura segue um **clean layering** (inspirada em Clean/Hexagonal), mas **pragmática** para um serviço REST simples e _design-first_:

```
com.product
├─ contract/          # código gerado do OpenAPI (DTOs, controllers/stubs)
├─ api/               # implementação dos stubs (Delegate) + advice de erros
├─ app/               # serviços (casos de uso), orquestra regra + mappers
├─ domain/            # entidades de negócio + portas (repos)
│  └─ repository/...  # interfaces e implementação in-memory (JSON)
├─ infra/             # cross-cutting (ex.: exceções padronizadas)
└─ ProductApplication # bootstrap Spring Boot
```

### Decisões e benefícios

- **Design First como contrato único**  
  O `openapi.yaml` define **endpoints, schemas e erros**. Todo o resto se alinha ao contrato, facilitando integração com **front/mobile/parceiros** e _mock servers_ para QA.

- **Separação “Contract vs Implementation”**
    - **contract/** contém apenas elementos **gerados** (DTOs e, se habilitado, os controllers _stubs_).
    - **api/** implementa o **Delegate** (negócio) sem poluir classes geradas. Atualizar o contrato não quebra a implementação.

- **Domínio independente de tecnologia**  
  `domain/` não depende de Spring MVC ou Jackson. Permite **migrar o backend** (ex.: de JSON in-memory para Postgres, S3, etc.) sem reescrever regras de negócio.

- **Infra como cross-cutting**  
  Tratamento de erro padronizado fica numa camada **única** (`infra/exception`), garantindo **consistência** nas respostas (code/message/timestamp) e facilitando **observabilidade**.

- **App como orquestrador**  
  `app/` chama repositórios do domínio, aplica paginação e converte para DTO com MapStruct. Mantém a lógica coesa e **testável** (sem HTTP).

- **Evolução suave**
    - Hoje: **somente leitura** em JSON embarcado.
    - Amanhã: acrescentar segunda implementação de repositório (JPA, Mongo, Redis) **sem trocar** controller/DTO/contrato.
    - Escalonamento: inserir **cache** ou filas mantendo _contract_ e mappers.

### Anti‑padrões evitados

- **Editar código gerado**: ao invés disso, toda customização fica em `api/` e `app/`.
- **Misturar DTO com Entity**: o mapper preserva **fronteira** clara.
- **Regras no controller**: controllers finos, regras na **service**.

---

## Boas práticas com MapStruct (checklist)

- ✅ `componentModel = "spring"` para injeção automática.
- ✅ Um mapper **por agregado** (ex.: `ProductMapper`, `ProductPageMapper`).
- ✅ Converters auxiliares para tipos especiais (`String ↔ URI`).
- ✅ Testes de mapeamento simples — asseguram que nenhum campo “sumiu” na evolução.
- ✅ Evite “mega-mapper”; extraia **helpers** para reduzir acoplamento.

---

## Boas práticas arquiteturais (checklist)

- ✅ Contrato versionado (tag `version` no OpenAPI; pasta `v1`, se necessário).
- ✅ Erro padronizado (code/message/timestamp) — uma surface única para todas as falhas.
- ✅ Controllers finos, sem regra; services centrando a orquestra.
- ✅ Domínio sem dependências de framework HTTP/JSON.
- ✅ Camadas desacopladas — troque infra (ex.: storage) sem tocar contrato.

---

## Observações (escopo de exemplo)

Esta é uma API **exemplificativa**. Para simplificar, **não** foram implementados:

- **Autenticação e autorização** (ex.: OAuth2/JWT). Todos os endpoints estão abertos no ambiente local.
- **Resiliência** (retries com backoff, circuit breaker, timeouts afinados). Como o repositório é em memória/JSON, não há dependências externas.
- **Criptografia**:
    - **Em trânsito**: a aplicação roda em **HTTP** no ambiente local; use um **reverse proxy** (Nginx/Ingress) com **TLS** em produção.
    - **Em repouso**: os dados ficam embarcados como JSON de demonstração; para produção, considerar KMS/volumes criptografados.

> OBSERVAÇÃO: Por se tratar de uma API de exemplo não implementei coisas importantes como **autenticação**, **resiliência**, **observabilidade** e **criptografia**.
