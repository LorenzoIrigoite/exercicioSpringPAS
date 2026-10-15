# Contexto vivo — Sistema de Gestão de Planos de Voo

Este arquivo é a fonte de contexto do projeto. Registra linguagem do domínio, regras de negócio, invariantes, decisões arquiteturais e fatos relevantes para implementação. Atualizar quando uma regra for esclarecida, uma decisão for tomada ou a documentação entregue mudar.

## Fonte do domínio

Requisitos funcionais vêm de `PAS_TF_2026_2_PlanosDeVoo (1).md`, documento fornecido para o trabalho final. Quando este arquivo e o documento divergirem, registrar a decisão neste contexto antes de codificar.

## Linguagem do domínio

### Rede aérea

**Aeroporto**: local identificado por código único, com nome e quantidade de vagas de estacionamento.
_Avoid_: base, terminal, estação.

**Aerovia**: ligação aérea orientada entre aeroporto de origem e aeroporto de destino, com identificador único e distância em quilômetros. Aerovias em sentidos opostos são registros distintos.
_Avoid_: rota, caminho, trecho. Use `trecho` para uma ocorrência de uso de uma aerovia dentro de um plano.

**Malha aérea**: conjunto de aeroportos e aerovias cadastrados.

**Altitude**: nível de voo em pés. Altitudes válidas para ocupação vão de 25.000 a 35.000 pés, em incrementos de 1.000 pés.

**Slot**: intervalo de uma hora alinhado à hora cheia. Qualquer uso parcial ocupa o slot inteiro.

### Pessoas e recursos

**Piloto**: pessoa com identificador único e nome. O escopo não considera licenças, habilitações ou jornada.

**Aeronave**: recurso identificado por prefixo único, com velocidade de cruzeiro e autonomia nominal, pertencente a uma categoria operacional.

**Aeronave particular**: aeronave cuja informação específica é empresa responsável pela manutenção.

**Aeronave comercial de passageiros**: aeronave cuja informação específica é companhia aérea e capacidade máxima de passageiros.

**Aeronave comercial de carga**: aeronave cuja informação específica é companhia aérea e capacidade máxima de carga em toneladas.

**Vaga**: posição de estacionamento de um aeroporto. Qualquer vaga pode receber qualquer aeronave deste trabalho.

### Plano e percurso

**Plano de voo**: agregado que associa piloto, aeronave, origem, destino, partida prevista e percurso. Possui identificador único e situação de ciclo de vida.

**Situação do plano**: `EM_EDICAO`, `APROVADO`, `CANCELADO` ou `ABANDONADO`.

**Trecho**: item ordenado do percurso, contendo aerovia, altitude, horário de entrada e operação ao chegar ao destino da aerovia.

**Sobrevoo**: passagem pelo aeroporto sem ocupar vaga e sem reabastecer. A distância desde o último abastecimento continua acumulada.

**Pouso**: chegada ao aeroporto com vaga pretendida e tempo de permanência. Considera reabastecimento antes do próximo trecho.

**Data de referência**: data usada na apresentação para demonstrar disponibilidade, conflitos, validação, aprovação e reservas. Deve ser identificada nos dados iniciais e na documentação de execução.

### Reserva

**Reserva de aerovia**: ocupação de uma combinação de aerovia, altitude e slots decorrente de plano aprovado.

**Reserva de vaga**: ocupação de uma combinação de aeroporto, vaga e slots durante permanência de pouso.

**Disponibilidade**: consulta momentânea de recursos livres. Consulta não cria reserva.

## Regras de negócio

### Malha aérea

- Código de aeroporto é único.
- Identificador de aerovia é único.
- Aerovia é orientada.
- Aerovia cadastrada não pode ser alterada nem excluída.
- Aerovia em desuso permanece cadastrada.
- O sistema não encontra rota. O usuário informa a sequência de aerovias.
- Novos aeroportos e aerovias devem ser cadastráveis sem alterar a lógica de validação.

### Aeronaves

- Prefixo de aeronave é único.
- Velocidade constante determina duração: `distância / velocidade de cruzeiro`.
- Distância sem reabastecimento não pode exceder `85% da autonomia nominal`.
- Sobrevoo acumula distância.
- Pouso reinicia o controle de autonomia após reabastecimento.
- Particular só pode usar 25.000, 26.000 ou 27.000 pés.
- Comercial de passageiros só pode usar altitude igual ou superior a 28.000 pés, dentro da faixa permitida.
- Comercial de carga pode usar qualquer altitude prevista, mas todo o intervalo de cada aerovia precisa ocorrer entre 00:00 inclusive e 06:00 exclusive.
- Regras e categorias futuras devem poder ser incorporadas sem mudanças extensas no fluxo principal.

### Percurso e horários

- Plano precisa ter um ou mais trechos.
- Origem da primeira aerovia deve ser a origem do plano.
- Destino da última aerovia deve ser o destino do plano.
- Destino de cada aerovia deve ser a origem da próxima.
- Entrada do primeiro trecho deve ser igual à partida do plano.
- Chegada do trecho é calculada pelo sistema: entrada + distância / velocidade.
- Após sobrevoo, entrada do próximo trecho é a chegada calculada.
- Após pouso intermediário, entrada do próximo trecho é chegada + permanência.
- Destino final exige pouso.
- Partida e chegada do voo devem ocorrer na mesma data.
- Permanência no destino final pode avançar para o dia seguinte; reservas de vaga devem incluir todos os slots.
- Horários fornecidos pelo cliente não podem substituir horários calculados pelo sistema.
- Sistema não sugere rota alternativa nem corrige percurso inválido.

### Recursos e slots

- Ocupação de aerovia usa slots horários alinhados à hora cheia.
- Entrada às 15:52 e saída às 17:07 ocupa slots 15:00–16:00, 16:00–17:00 e 17:00–18:00.
- Uma mesma altitude de uma mesma aerovia não pode ter sobreposição de slots entre aeronaves.
- Vaga fica ocupada durante toda a permanência.
- Chegada às 15:50 com permanência de 20 minutos ocupa slots 15:00–16:00 e 16:00–17:00.
- Vaga precisa estar livre em todos os slots exigidos.
- Plano em edição não reserva recurso.
- Validar não reserva recurso.
- Aprovar reserva todos os recursos de forma integral: sucesso reserva tudo; falha não deixa reserva parcial.
- Cancelar plano aprovado futuro libera todas as reservas.
- Plano abandonado não possui reservas a liberar.

### Ciclo de vida

- Todo plano nasce `EM_EDICAO`.
- Somente plano em edição pode ser modificado, validado ou aprovado.
- Plano em edição pode ser abandonado; depois fica imutável.
- Plano aprovado preserva seus dados e reservas.
- Plano aprovado só pode ser cancelado se a partida prevista ainda não ocorreu.
- Cancelamento muda situação para `CANCELADO` e libera reservas.
- Plano aprovado cuja partida já ocorreu é considerado executado e não pode ser cancelado.
- Planos aprovados, cancelados e abandonados permanecem consultáveis.
- Validar e aprovar são operações distintas.
- Aprovação deve validar novamente regras e disponibilidade no momento da operação.

### Cadastro e integridade dos dados

- Aeroportos, pilotos e aeronaves precisam ser consultáveis e cadastráveis pela API.
- Aerovias precisam ser consultáveis e cadastráveis pela API.
- Identificadores de aeroporto, aerovia, piloto, aeronave e plano devem ser únicos dentro de seus respectivos tipos.
- Aerovia só pode ser criada apontando para aeroportos existentes.
- Distância de aerovia deve ser expressa em quilômetros e ser compatível com cálculo de duração.
- Quantidade de vagas de aeroporto deve representar capacidade disponível para estacionamento.
- Dados de categoria precisam conter os atributos específicos exigidos pela categoria.
- Referências usadas por plano aprovado ou reserva devem permanecer recuperáveis após reinicialização.
- Aerovia não pode ser alterada ou excluída depois do cadastro; não há desativação gerenciada no escopo.

### Operações de plano

- Criar plano exige piloto, aeronave, origem, destino e data/hora prevista de partida.
- Plano sem percurso não pode ser validado nem aprovado.
- Edição pode alterar percurso, altitudes, pousos, sobrevoos e permanências enquanto a situação for `EM_EDICAO`.
- Edição não cria nem libera reservas.
- Validação é consulta de regras: não muda situação, não persiste reservas e pode ser repetida depois de alterações.
- Aprovação deve reavaliar regras e disponibilidade na mesma operação que persiste reservas.
- Falha em qualquer validação impede mudança para `APROVADO`.
- Falha de qualquer reserva impede todas as reservas da tentativa.
- Abandono exige situação `EM_EDICAO` e torna o plano imutável.
- Cancelamento exige situação `APROVADO` e partida futura no instante da operação.
- Plano aprovado com partida já ocorrida permanece consultável como `APROVADO`; não recebe situação adicional de executado.
- Plano aprovado, cancelado ou abandonado preserva os dados existentes ao atingir a situação.
- Consultas devem permitir buscar plano por identificador e filtrar por qualquer situação existente.

### Dados iniciais

- Primeira inicialização precisa carregar aeroportos, aerovias, pilotos e aeronaves de todas as categorias.
- Primeira inicialização precisa carregar planos aprovados e reservas correspondentes.
- Na data de referência, pelo menos cinco das oito aerovias iniciais devem possuir ocupação.
- Ocupações devem resultar de planos aprovados, não de registros artificiais de reserva.
- Dados precisam conter disponibilidade e conflitos; não podem bloquear todos os recursos na data inteira.
- Carga inicial precisa ser reproduzível a partir do repositório, sem cadastro manual prévio.
- Alterações da API precisam sobreviver à reinicialização.
- Data de referência deve ser explícita e usada nos exemplos de validação, disponibilidade, conflito e reserva.

### Requisitos de demonstração

A API precisa permitir demonstrar, usando requisições HTTP e os dados iniciais:

- criação e edição progressiva de plano;
- percurso inconsistente;
- horário incompatível com duração calculada;
- restrição de altitude;
- janela horária de aeronave de carga;
- autonomia insuficiente;
- conflito de aerovia/altitude;
- indisponibilidade de vaga;
- validação sem reserva;
- aprovação integral e falha sem reserva parcial;
- disponibilidade atual no momento da aprovação;
- cancelamento futuro com liberação de reservas;
- bloqueio de cancelamento após partida;
- abandono;
- consulta de planos em todos os estados;
- bloqueio de alteração após aprovação, cancelamento ou abandono.

## Dados iniciais da malha

| Aeroporto | Nome resumido | Vagas |
|---|---|---:|
| `POA` | Aeroporto Internacional Salgado Filho — Porto Alegre | 41 |
| `FLN` | Aeroporto Internacional de Florianópolis/Hercílio Luz | 16 |
| `CWB` | Aeroporto Internacional Afonso Pena — Curitiba | 26 |
| `GRU` | Aeroporto Internacional de São Paulo/Guarulhos | 115 |

| Aerovia | Origem | Destino | Distância km |
|---|---|---|---:|
| `R1` | `POA` | `FLN` | 363 |
| `R2` | `POA` | `GRU` | 865 |
| `R3` | `GRU` | `POA` | 865 |
| `R4` | `FLN` | `CWB` | 245 |
| `R5` | `CWB` | `FLN` | 245 |
| `R6` | `CWB` | `GRU` | 360 |
| `R7` | `GRU` | `CWB` | 360 |
| `R8` | `FLN` | `POA` | 363 |

## Decisões arquiteturais vigentes

### Backend

- Escopo é exclusivamente backend.
- Tecnologia obrigatória: Java 17 e Spring Boot 3.3.4, conforme projeto atual.
- Interface externa: API REST documentada por OpenAPI ou equivalente.
- Persistência obrigatória em banco; memória ou CSV não atendem ao trabalho.
- PostgreSQL/Supabase é a direção de persistência adotada para desenvolvimento e entrega. Credenciais entram somente por variáveis de ambiente e não pertencem ao repositório.
- H2 pode ser usado em testes automatizados quando reduzir custo de execução, mas não substitui persistência de entrega.

### Organização interna

Adotamos **arquitetura hexagonal**. O domínio e os casos de uso não dependem de Spring MVC, JPA, PostgreSQL ou detalhes de transporte; adaptadores conectam entradas externas e saídas persistentes às portas da aplicação.

- `domain`: entidades, objetos de valor, estados, invariantes e serviços de domínio;
- `application`: casos de uso e portas de entrada; coordena transações e regras sem conhecer HTTP ou JPA;
- `adapters/in`: controllers REST e conversores de requests para comandos de aplicação;
- `adapters/out`: persistência JPA, carga inicial e conversores entre modelo persistente e domínio;
- `configuration`: composição de dependências e configuração Spring.

Dependências apontam para dentro: adaptadores dependem de portas; aplicação depende do domínio; domínio não depende de framework. Evitar interfaces sem necessidade real de isolamento ou substituição.

### Consistência transacional

A aprovação é operação transacional: valida plano, verifica novamente disponibilidade, grava todas as reservas e muda o estado apenas se tudo funcionar. Qualquer falha mantém plano em edição e não deixa reservas parciais.

O cancelamento futuro é operação transacional: muda estado e libera todas as reservas associadas como uma unidade.

### API

A API deve expor operações para:

- consultar e cadastrar aeroportos;
- consultar e cadastrar aerovias;
- consultar e cadastrar pilotos;
- consultar e cadastrar aeronaves;
- criar, editar e consultar planos;
- definir percurso;
- consultar disponibilidade de altitudes por aerovia e vagas por aeroporto para período informado;
- validar e aprovar;
- abandonar;
- cancelar planos aprovados futuros.

A documentação da API deve informar operações, parâmetros, requisições, respostas e erros por OpenAPI ou mecanismo equivalente. Clientes HTTP e a interface da documentação podem ser usados na apresentação; frontend próprio não faz parte do escopo.

Respostas de erro devem permitir identificar regra violada e recurso envolvido. Não esconder falha de domínio como erro genérico.

### Entrega e arquitetura documentada

O repositório deve conter código Java/Spring Boot, configuração do banco, carga inicial reproduzível, modelo de domínio UML, diagramas C4 de contexto, contêineres e componentes, decisões arquiteturais justificadas e `README.md` de preparação, banco, carga, compilação, execução, API e data de referência.

C4 nível 4 não é exigido; o modelo UML cobre o detalhamento do domínio. Diagramas devem permanecer coerentes com a implementação efetivamente entregue.

A solução deve ser reproduzível sem configuração ou dados exclusivos da equipe. O banco deve recuperar rede aérea, pilotos, aeronaves, planos e reservas depois de reinicialização.

## Pendências arquiteturais para decisão

Estas escolhas não estão definidas pelo documento e serão decididas antes da implementação. Cada decisão deve ser registrada aqui com alternativa escolhida e justificativa:

1. Estilo arquitetural interno: camadas modularizadas, hexagonal ou outra — **decidido: arquitetura hexagonal**, para isolar domínio e casos de uso de HTTP, JPA e PostgreSQL.
2. Identidade dos agregados: UUID, identificador numérico ou código natural por entidade — **decidido: UUID técnico interno + identificadores de negócio**, como `POA`, `R1` e prefixo, com unicidade própria.

3. Modelagem de categorias: herança JPA, composição por categoria ou regras por estratégia — **decidido: estratégias de operação**, para adicionar categorias e regras sem alterar o fluxo principal.
4. Representação de operação de chegada: tipo simples de sobrevoo/pouso ou modelo polimórfico — **decidido: tipo discriminado simples**, com `SOBREVOO` ou `POUSO`; somente pouso possui vaga e permanência.
5. Precisão temporal: minuto, segundo ou outra unidade para duração fracionária — **decidido: minuto**, por ser a unidade usada na elaboração e apresentação do plano.
6. Arredondamento de duração fracionária — **decidido: minuto mais próximo**, com regra matemática usual (`0,5` arredonda para cima).
7. Tolerância de comparação de horários calculados e informados — **decidido: comparação exata em minutos**, porque entrada, duração arredondada e horários derivados usam a mesma unidade.
8. Estratégia de slots: materializar slots ou calcular intervalos sob demanda — **decidido: calcular sob demanda**, armazenando início e fim da ocupação; a regra de slots é aplicada nas consultas e reservas.
9. Modelo de reserva: uma entidade genérica ou reservas específicas de aerovia e vaga — **decidido: reservas específicas**, separando `ReservaAerovia` e `ReservaVaga` para manter invariantes e consultas explícitas.
10. Concorrência de aprovação: lock pessimista, lock otimista ou transação com restrição no banco — **decidido: lock pessimista em transação**, bloqueando os recursos consultados antes de verificar e gravar reservas; escolha direta e suficiente para evitar aprovações concorrentes conflitantes.
11. Carga inicial: migration SQL, `CommandLineRunner`, Flyway/Liquibase ou outro mecanismo — **decidido: Flyway**, para versionar schema e carga inicial de forma reproduzível e executar apenas migrations ainda não aplicadas.
12. Contrato de erros HTTP e códigos para regras de domínio — **decidido: `ProblemDetail` seguindo RFC 7807**, com status HTTP, título, detalhe, URI do tipo de erro e identificador da regra violada quando aplicável.
13. Documentação OpenAPI: Springdoc ou mecanismo equivalente já disponível — **decidido: Springdoc OpenAPI**, integrado ao Spring Boot e exposto pela API para uso na apresentação.
14. Estratégia de testes de integração: PostgreSQL real/Testcontainers ou H2 compatível — **decidido: PostgreSQL via Testcontainers** nos testes que verificam persistência e transações; H2 não será usado para validar comportamento específico do banco de entrega.
15. Política de data/hora: UTC, timezone fixa ou timezone configurável — **decidido: `America/Sao_Paulo` como timezone padrão e configurável por propriedade**. O enunciado não define fuso; define apenas horários locais, slots alinhados à hora cheia, janela de carga `00:00–06:00`, data de referência e partida/chegada na mesma data. A escolha acompanha Porto Alegre e o contexto brasileiro sem alterar regras do documento.
16. Formato da API para edição progressiva: plano completo ou endpoints de comandos parciais — **decidido: comandos parciais**, com operações explícitas para alterar dados básicos e substituir o percurso enquanto o plano estiver `EM_EDICAO`; isso reflete construção progressiva e rejeita alterações após transição de estado.

## Estado atual do repositório

- Branch de trabalho: `feat/planos-de-voo`.
- Projeto ainda contém scaffold anterior de biblioteca, que será substituído progressivamente pelo domínio de planos de voo.
- `exercicio/pom.xml` já possui Spring Web, Spring Data JPA, Validation, PostgreSQL runtime, H2 para testes e Spring Boot Test.
- `exercicio/src/main/resources/application.properties` aceita configuração de datasource por `SPRING_DATASOURCE_*` e mantém fallback H2.
- `exercicio/.env.example` documenta variáveis sem segredos.
- Maven Wrapper não existe no repositório; validação depende de Maven instalado no ambiente executor.

## Critérios de verificação obrigatórios

Toda implementação relevante precisa deixar verificável, por teste automatizado ou chamada HTTP:

1. percurso contínuo e percurso inconsistente;
2. horários calculados e horários incompatíveis;
3. restrição de altitude por categoria;
4. janela horária de carga;
5. autonomia insuficiente e reinício após pouso;
6. início e fim no mesmo dia;
7. conflito de altitude/aerovia por slots;
8. conflito de vaga por slots;
9. validação sem reserva;
10. aprovação integral sem reservas parciais;
11. aprovação considerando disponibilidade atual;
12. cancelamento liberando reservas;
13. bloqueio de cancelamento após partida;
14. abandono de plano em edição;
15. imutabilidade de planos aprovados, cancelados e abandonados;
16. persistência após reinicialização;
17. consulta de estados e disponibilidade pela API.

## Atualização deste contexto

Adicionar uma entrada quando:

- requisito for interpretado de forma não óbvia;
- termo do domínio ganhar definição mais precisa;
- alternativa arquitetural for avaliada e decisão tomada;
- regra for alterada pelo professor;
- código contradizer este arquivo.

Remover ou marcar como supersedida qualquer decisão que deixar de valer. Não usar este arquivo para copiar classes, endpoints ou configurações que podem ser descobertos diretamente no código; registrar apenas decisões, regras e motivos que evitam interpretação errada.
