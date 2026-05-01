# PathFinder — Documentação do Projeto

## 1. Identificação do projeto

**Nome:** PathFinder  
**Tema:** análise logística de transporte entre capitais brasileiras  
**Disciplina:** Inteligência Artificial  
**Tópicos aplicados:** busca em grafos, A*, Kruskal e Algoritmo Genético  
**Arquitetura:** backend em Spring Boot, frontend em React/Vite e banco PostgreSQL

O PathFinder foi desenvolvido para simular decisões logísticas envolvendo transporte de cargas entre capitais brasileiras. O sistema representa o território como um grafo, calcula rotas de menor custo e compara cenários com rodovias e ferrovias.

O projeto não foi pensado apenas como um site visual. A interface web funciona como meio de demonstração e validação dos algoritmos exigidos, permitindo que os avaliadores testem entradas, executem os métodos implementados e observem os resultados gerados pelo backend.

## 2. Objetivo

O objetivo do projeto é resolver o problema proposto no trabalho por meio de uma aplicação completa, composta por API backend e interface web.

A aplicação permite:

- Representar as capitais brasileiras como vértices de um grafo.
- Representar ligações entre capitais como arestas ponderadas por distância.
- Calcular rotas de menor custo usando A*.
- Gerar uma malha ferroviária mínima usando Kruskal.
- Otimizar a escolha de ferrovias usando Algoritmo Genético.
- Apresentar custos totais, trechos percorridos, modais utilizados e transbordos.
- Demonstrar os resultados por meio de uma interface web acessível aos avaliadores.

## 3. Modelagem do problema

O problema foi modelado como um grafo ponderado.

Cada capital é representada como um vértice. Cada ligação direta entre capitais de estados vizinhos é representada como uma aresta. O peso de cada aresta corresponde à distância entre as capitais.

A modelagem considera:

- Rodovias entre capitais de estados que possuem fronteira em comum.
- Exceção para Brasília, que possui conexão com Goiânia e Belo Horizonte.
- Custo rodoviário de R$ 5,00 por km.
- Custo ferroviário de R$ 1,20 por km.
- Custo de construção ferroviária de R$ 2.000.000,00 por km.
- Custo de transbordo de R$ 1.000,00 quando há troca de modal.

## 4. Atendimento aos itens do trabalho

### Item A — elaboração do grafo

O grafo é formado pelas 27 unidades federativas, usando as capitais como nós. As conexões diretas seguem as adjacências territoriais entre os estados, com distância associada a cada aresta.

Esses dados são utilizados pelos algoritmos de busca, geração de malha ferroviária e otimização.

### Item B — A* com rodovias

Foi implementado um endpoint para encontrar a rota de menor custo usando apenas rodovias.

O custo é calculado com base em:

```txt
R$ 5,00 por km rodado
```

O resultado apresenta origem, destino, distância total, custo total e lista de trechos percorridos.

### Item C — Kruskal

Foi implementado o algoritmo de Kruskal para gerar uma malha ferroviária mínima capaz de conectar todas as capitais.

O custo de construção é calculado com base em:

```txt
R$ 2.000.000,00 por km construído
```

O resultado inclui a distância total da malha, o custo total de implantação e a lista de ferrovias selecionadas.

### Item D — A* com malha do Kruskal

Foi implementada uma versão do A* que considera a malha ferroviária gerada por Kruskal.

Nesse modo, cada trecho pode ser percorrido por rodovia ou ferrovia, dependendo da disponibilidade da ferrovia na malha. Quando há troca de modal, o custo de transbordo é adicionado.

### Item E — Algoritmo Genético

Foi implementado um Algoritmo Genético para selecionar trechos ferroviários respeitando o orçamento disponível.

A meta do AG é reduzir o custo total de transporte nas rotas mais comuns, considerando a quantidade diária de cargas em cada rota.

Cada cromossomo representa uma possível seleção de ferrovias. A função de fitness avalia o custo da malha e o custo logístico resultante.

### Item F — A* com malha do Algoritmo Genético

Foi implementada uma versão do A* que usa a malha ferroviária gerada pelo Algoritmo Genético.

Esse endpoint permite avaliar o impacto prático da malha otimizada nas rotas selecionadas pelo usuário.

## 5. Arquitetura da solução

A solução está dividida em três partes principais.

### 5.1 Backend

O backend foi implementado em Java com Spring Boot.

Responsabilidades:

- Expor os algoritmos por meio de endpoints REST.
- Carregar os dados do banco PostgreSQL.
- Executar A*, Kruskal e Algoritmo Genético.
- Calcular custos de transporte, construção e transbordo.
- Retornar respostas em JSON para o frontend.

Tecnologias usadas:

- Java 17.
- Spring Boot.
- Spring Web.
- Spring Data JPA.
- Flyway.
- PostgreSQL.
- Maven.

### 5.2 Frontend

O frontend foi implementado em React com Vite e TypeScript.

Responsabilidades:

- Apresentar o mapa e as conexões entre estados.
- Permitir seleção de origem e destino.
- Permitir escolha do modo de cálculo.
- Chamar os endpoints do backend.
- Exibir custos, rotas, trechos, modais e malhas ferroviárias.

A interface existe para facilitar a demonstração do projeto. Os algoritmos são executados no backend.

### 5.3 Banco de dados

O banco utilizado é PostgreSQL.

Ele armazena dados como:

- Capitais.
- Ligações entre capitais.
- Distâncias entre capitais.
- Rotas comuns de transporte.
- Informações necessárias para os algoritmos.

As migrations são controladas com Flyway.

## 6. Endpoints da API

A API utiliza o prefixo:

```txt
/api
```

### 6.1 A* rodoviário

```http
POST /api/astar/road
```

Exemplo de requisição:

```json
{
  "origin": "SC",
  "destination": "RR"
}
```

### 6.2 Kruskal

```http
GET /api/kruskal
```

Retorna a malha ferroviária mínima, custo total de construção e orçamento disponível para o Algoritmo Genético.

### 6.3 Algoritmo Genético

```http
POST /api/genetic/run
```

Exemplo de requisição:

```json
{
  "budgetLimit": 16731600000.0,
  "popSize": 200,
  "generations": 100,
  "mutationRate": 0.02,
  "tournamentSize": 3
}
```

### 6.4 A* com Kruskal

```http
POST /api/astar/kruskal
```

Exemplo de requisição:

```json
{
  "origin": "SC",
  "destination": "RR",
  "railwayNetwork": ["SC-PR", "PR-SP"]
}
```

### 6.5 A* com Algoritmo Genético

```http
POST /api/astar/genetic
```

Exemplo de requisição:

```json
{
  "origin": "SC",
  "destination": "RR",
  "railwayEdges": ["SC-PR", "PR-SP"]
}
```

## 7. Algoritmos implementados

## 7.1 A*

O A* foi usado para encontrar a rota de menor custo entre duas capitais.

Ele considera o custo acumulado da rota e uma heurística baseada na distância estimada até o destino. O algoritmo foi adaptado para trabalhar com modais diferentes, permitindo diferenciar rodovia e ferrovia.

O resultado inclui:

- Origem e destino.
- Distância total.
- Custo total.
- Número de transbordos.
- Custo de transbordo.
- Lista de segmentos percorridos.
- Modal utilizado em cada segmento.

## 7.2 Kruskal

Kruskal foi usado para gerar uma árvore geradora mínima sobre o grafo das capitais.

A finalidade é encontrar uma malha ferroviária que conecte todas as capitais com o menor custo possível de construção.

A saída do algoritmo é uma lista de trechos ferroviários selecionados e o custo total da obra.

## 7.3 Algoritmo Genético

O Algoritmo Genético foi usado para selecionar uma combinação de ferrovias dentro do orçamento disponível.

A estrutura geral do AG é:

- Geração de população inicial.
- Avaliação por função de fitness.
- Seleção por torneio.
- Crossover entre cromossomos.
- Mutação.
- Elitismo.
- Retorno da melhor solução encontrada.

Os parâmetros usados na execução principal são:

```json
{
  "popSize": 200,
  "generations": 100,
  "mutationRate": 0.02,
  "tournamentSize": 3
}
```

## 8. Otimizações no Algoritmo Genético

Durante os testes, o Algoritmo Genético inicialmente apresentava tempo de execução alto e podia cair em timeout. O problema principal estava na função de fitness, que era chamada milhares de vezes.

Com `popSize = 200` e `generations = 100`, a execução exige aproximadamente 20.000 avaliações de cromossomos. Por isso, qualquer custo desnecessário dentro do fitness se multiplica rapidamente. Pequena tragédia matemática, porque aparentemente loops ainda fazem loop.

As otimizações aplicadas foram:

### 8.1 Avaliação mais direta

A função de fitness passou a calcular apenas o necessário para comparar cromossomos.

O AG não precisa montar uma rota detalhada em cada avaliação. Ele precisa comparar custos. A rota detalhada é calculada depois pelos endpoints de A*, quando o usuário escolhe origem e destino.

### 8.2 Penalização antecipada

Cromossomos que ultrapassam o orçamento recebem penalidade imediatamente.

Isso evita gastar tempo calculando rotas para soluções que já são inválidas.

### 8.3 Validação correta do orçamento

A validação foi ajustada para aceitar soluções cujo custo seja igual ao orçamento:

```java
constructionCost <= budgetLimit
```

### 8.4 Correção da mutação

A mutação foi ajustada para desfazer alterações apenas quando elas estouram o orçamento.

Mutações válidas são mantidas, permitindo que a população evolua corretamente.

### 8.5 Redução de logs

Logs dentro do loop do AG foram reduzidos/removidos para evitar custo desnecessário durante a execução em produção.

### 8.6 Cache de avaliações

Foi usado cache para evitar recalcular rotas ou configurações já avaliadas.

Isso melhora o desempenho porque o AG pode gerar cromossomos repetidos ou semelhantes ao longo das gerações.

## 9. Interface de demonstração

A interface web foi construída para demonstrar os algoritmos de forma visual e interativa.

Ela permite:

- Visualizar a malha de estados.
- Alternar entre malha completa, Kruskal e Algoritmo Genético.
- Executar o Algoritmo Genético.
- Selecionar origem e destino.
- Escolher o tipo de rota.
- Visualizar a rota calculada.
- Conferir custo total, distância, transbordos e segmentos.

A interface serve como camada de apresentação dos resultados gerados pela API. A lógica central permanece no backend.

## 10. Execução local sem Docker

### 10.1 Backend

Pré-requisitos:

- Java 17.
- Maven.
- PostgreSQL.

Configurar as variáveis de ambiente do banco:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/pathfinder
SPRING_DATASOURCE_USERNAME=pathfinder
SPRING_DATASOURCE_PASSWORD=pathfinder
SPRING_FLYWAY_ENABLED=true
```

Executar o backend:

```bash
mvn clean package -DskipTests
mvn spring-boot:run
```

A API ficará disponível em:

```txt
http://localhost:8080/api
```

### 10.2 Frontend

Entrar na pasta do frontend:

```bash
cd frontend
```

Instalar dependências:

```bash
npm install
```

Criar o arquivo `.env`:

```env
VITE_API_URL=http://localhost:8080/api
```

Rodar o frontend:

```bash
npm run dev
```

A interface ficará disponível em:

```txt
http://localhost:5173
```

## 11. Execução local com Docker

Esta seção descreve uma forma padronizada de executar o projeto localmente usando Docker. Ela é útil para avaliação, testes e demonstração em uma máquina limpa.

## 11.1 Pré-requisitos

Instalar:

- Docker.
- Docker Compose.

Verificar instalação:

```bash
docker --version
docker compose version
```

## 11.2 Arquivo `docker-compose.yml`

Na raiz do projeto, criar um arquivo chamado `docker-compose.yml`:

```yaml
services:
  db:
    image: postgres:16
    container_name: pathfinder-db
    environment:
      POSTGRES_DB: pathfinder
      POSTGRES_USER: pathfinder
      POSTGRES_PASSWORD: pathfinder
    ports:
      - "5432:5432"
    volumes:
      - pathfinder-db-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U pathfinder -d pathfinder"]
      interval: 5s
      timeout: 5s
      retries: 10

  backend:
    image: maven:3.9-eclipse-temurin-17
    container_name: pathfinder-backend
    working_dir: /app
    volumes:
      - .:/app
      - pathfinder-maven-cache:/root/.m2
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/pathfinder
      SPRING_DATASOURCE_USERNAME: pathfinder
      SPRING_DATASOURCE_PASSWORD: pathfinder
      SPRING_FLYWAY_ENABLED: "true"
      SERVER_PORT: 8080
    command: mvn spring-boot:run
    ports:
      - "8080:8080"
    depends_on:
      db:
        condition: service_healthy

  frontend:
    image: node:22
    container_name: pathfinder-frontend
    working_dir: /app
    volumes:
      - ./frontend:/app
      - pathfinder-node-modules:/app/node_modules
    environment:
      VITE_API_URL: http://localhost:8080/api
    command: sh -c "npm install && npm run dev -- --host 0.0.0.0"
    ports:
      - "5173:5173"
    depends_on:
      - backend

volumes:
  pathfinder-db-data:
  pathfinder-maven-cache:
  pathfinder-node-modules:
```

## 11.3 Subir o projeto

Na raiz do repositório, executar:

```bash
docker compose up --build
```

O primeiro start pode demorar porque Maven e npm precisam baixar dependências. Ou seja, é o Docker fazendo café antes de trabalhar, comportamento muito humano para uma ferramenta que se vende como solução.

Quando os serviços subirem:

```txt
Frontend: http://localhost:5173
Backend:  http://localhost:8080/api
Banco:    localhost:5432
```

## 11.4 Testar a API

Com os containers rodando, testar o Kruskal:

```bash
curl http://localhost:8080/api/kruskal
```

Testar A* rodoviário:

```bash
curl -X POST http://localhost:8080/api/astar/road \
  -H "Content-Type: application/json" \
  -d '{"origin":"SC","destination":"RR"}'
```

Testar Algoritmo Genético:

```bash
curl -X POST http://localhost:8080/api/genetic/run \
  -H "Content-Type: application/json" \
  -d '{
    "budgetLimit": 16731600000.0,
    "popSize": 200,
    "generations": 100,
    "mutationRate": 0.02,
    "tournamentSize": 3
  }'
```

## 11.5 Parar o projeto

Para parar os containers:

```bash
docker compose down
```

Para apagar também os volumes locais:

```bash
docker compose down -v
```

Use `-v` apenas quando quiser apagar os dados do banco local.

## 11.6 Problemas comuns no Docker

Se o backend subir antes do banco estar pronto, execute novamente:

```bash
docker compose restart backend
```

Se as dependências do frontend ficarem inconsistentes:

```bash
docker compose down -v
docker compose up --build
```

Se a porta 5432 já estiver ocupada por outro PostgreSQL local, altere o mapeamento no compose:

```yaml
ports:
  - "15432:5432"
```

Nesse caso, dentro do Docker o backend continua usando `db:5432`; a mudança afeta apenas o acesso pelo host.

## 12. Deploy em produção

O deploy final foi organizado da seguinte forma:

- Backend em Railway.
- Banco PostgreSQL em Railway.
- Frontend em Vercel.

No Railway, o backend precisa das variáveis de banco:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://HOST:PORT/DATABASE
SPRING_DATASOURCE_USERNAME=USER
SPRING_DATASOURCE_PASSWORD=PASSWORD
SERVER_PORT=${PORT}
SPRING_FLYWAY_ENABLED=true
```

Na Vercel, o frontend precisa da variável:

```env
VITE_API_URL=https://URL_DO_BACKEND_RAILWAY.up.railway.app/api
```

Após alterar variáveis na Vercel, é necessário realizar novo deploy.

## 13. Estrutura principal do projeto

```txt
PathFinder/
├── pom.xml
├── docker-compose.yml
├── src/
│   ├── main/
│   │   ├── java/com/ai/PathFinder/
│   │   │   ├── controllers/
│   │   │   ├── services/
│   │   │   ├── strategy/
│   │   │   │   ├── genetic/
│   │   │   │   ├── kruskal/
│   │   │   │   ├── search/
│   │   │   │   └── graph/
│   │   │   ├── dtos/
│   │   │   ├── entities/
│   │   │   └── repositories/
│   │   └── resources/
│   └── test/
└── frontend/
    ├── src/
    │   ├── components/
    │   ├── hooks/
    │   ├── services/
    │   ├── data/
    │   ├── types/
    │   └── utils/
    └── package.json
```

## 14. Checklist para avaliação

Antes da apresentação, verificar:

- Backend online.
- Banco conectado.
- Frontend online.
- `VITE_API_URL` apontando para o backend com `/api`.
- CORS liberando Vercel e localhost.
- `GET /api/kruskal` funcionando.
- `POST /api/astar/road` funcionando.
- `POST /api/genetic/run` funcionando sem timeout.
- `POST /api/astar/kruskal` funcionando.
- `POST /api/astar/genetic` funcionando.
- Interface exibindo mapa e malhas.
- Resultados exibindo custos, distâncias, modais e transbordos.

## 15. Considerações finais

O PathFinder atende aos principais requisitos do trabalho ao disponibilizar os algoritmos em formato de API e apresentar os resultados por meio de uma interface web.

A solução implementa busca com A*, geração de malha mínima com Kruskal e otimização por Algoritmo Genético. Além disso, o sistema foi publicado em ambiente externo, com frontend e backend separados, permitindo demonstração real do funcionamento.

O principal desafio técnico foi tornar o Algoritmo Genético viável em produção mantendo os parâmetros definidos. Isso foi resolvido com otimizações na função de fitness, validação antecipada de cromossomos inválidos, correção da mutação, redução de logs e reaproveitamento de avaliações por cache.
