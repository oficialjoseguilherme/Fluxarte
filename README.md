# Fluxarte

Plataforma web de streaming de produções artísticas e culturais.

## Testes da camada de models e repositories

Os testes desta camada verificam se as entidades do sistema estão corretamente
mapeadas pelo JPA e se os repositories conseguem persistir, consultar e remover
os dados esperados.

Embora o foco seja a camada de models, estes são testes de integração: eles
inicializam o contexto do Spring com `@SpringBootTest` e executam operações reais
em um banco H2 em memória por meio dos repositories do Spring Data JPA.

### Ambiente de teste

Durante os testes é ativado o perfil `test`, configurado em
`src/test/resources/application-test.properties`. Esse perfil:

- usa um banco H2 em memória, separado do PostgreSQL de desenvolvimento;
- recria as tabelas automaticamente para a execução da suíte;
- utiliza o modo de compatibilidade com PostgreSQL;
- não modifica dados do ambiente de desenvolvimento.

Os testes de repository herdam uma configuração comum que utiliza:

- `@SpringBootTest` para carregar a aplicação;
- `@ActiveProfiles("test")` para selecionar o banco de testes;
- `@Transactional` para desfazer as alterações ao final de cada teste;
- `EntityManager` para executar `flush` e `clear` quando é necessário garantir
  que o objeto foi realmente gravado e recuperado do banco, e não apenas lido
  do cache do JPA.

As entidades válidas usadas como cenário são criadas pela classe
`ModelFixtures`, evitando repetição e mantendo os dados de teste consistentes.

### Organização dos casos

Os casos seguem o padrão apresentado em aula:

1. **Cenário:** criação das entidades e relacionamentos necessários.
2. **Ação:** chamada do repository ou de uma regra do model.
3. **Verificação:** comparação do resultado com o comportamento esperado.

Para testar persistência, normalmente é usado `saveAndFlush()`. Em seguida, o
contexto JPA é limpo e o registro é consultado novamente. Constraints são
verificadas com `assertThrows`, forçando a gravação no banco para confirmar que
valores inválidos ou duplicados são rejeitados.

### O que é testado

| Área | Verificações principais |
| --- | --- |
| Usuários | Persistência, valores padrão, consulta por e-mail, favoritos, gêneros preferidos, remoção e e-mail único |
| Gêneros | Persistência do enum, consulta por nome, remoção e nome único |
| Obras audiovisuais | Campos, enums, timestamps, gêneros, buscas de catálogo, janela de exibição, cascata e remoção de órfãos |
| Mídias | Vínculo obrigatório com obra, enums de mídia e consultas por obra e tipo |
| Eventos | Persistência, busca por nome e consulta de eventos em andamento, inclusive com datas abertas |
| Obras em eventos | Relacionamentos, destaque, consultas e unicidade do par evento/obra |
| Progresso | Posição assistida, busca por usuário/mídia, ordenação e unicidade do progresso |
| Histórico | Campos da visualização e ordenação da atividade mais recente para a mais antiga |
| Recomendações | Algoritmo, ordenação por score e exclusão das recomendações de um usuário |
| Refresh tokens | Valor padrão, tokens revogados, consultas de tokens ativos e token único |
| Atividades | Tipo, metadados, timestamp automático, filtros e ordenação por usuário |

Também existem testes unitários, sem acesso ao banco, para:

- determinar se uma obra está disponível em uma data e hora;
- tratar corretamente os limites da janela de exibição;
- calcular o percentual assistido de uma mídia;
- tratar mídia ausente e duração nula ou igual a zero;
- validar as regras de `ClassificacaoIndicativa`;
- converter os valores de `Resolucao` e rejeitar valores inválidos.

### Como executar

No Windows:

```powershell
cmd.exe /d /c mvnw.cmd test
```

No Linux ou macOS:

```bash
./mvnw test
```

Uma execução bem-sucedida deve terminar com `BUILD SUCCESS`. Atualmente, a
suíte contém 29 testes e não precisa de uma instância local do PostgreSQL.

Os relatórios gerados pelo Maven ficam disponíveis em
`target/surefire-reports`.
