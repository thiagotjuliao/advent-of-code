# Advent of Code — Scala 3

Soluções do [Advent of Code](https://adventofcode.com) de **2015 a 2025**, em Scala 3.9.0.

Um único módulo sbt, um objeto por dia, descoberta por reflexão (sem registro manual)
e download dos inputs sob demanda.

## Começando

```bash
cp .env.example .env     # e cole o cookie de sessão do adventofcode.com
sbt test                 # compila tudo e roda os testes de exemplo
sbt "run list"           # mostra o que já está implementado
```

## Rodando

| comando | o que faz |
| --- | --- |
| `sbt "run 2015 1"` | roda um dia |
| `sbt "run 2024"` | roda todos os dias implementados de um ano |
| `sbt "run all"` | roda tudo |
| `sbt run` | em dezembro, roda o puzzle de hoje |
| `sbt "run list"` | lista o que está implementado |
| `sbt "testOnly aoc.y2015.Day01Suite"` | roda o teste de um dia |

Cada parte sai com o tempo de execução ao lado — útil para saber quando a
solução da parte 2 precisa parar de ser força bruta.

## Começando um dia novo

```bash
./scripts/new-day.sh 2024 7
```

Isso cria `src/main/scala/aoc/y2024/Day07.scala`, o teste correspondente em
`src/test/scala/aoc/y2024/Day07Suite.scala` e baixa `inputs/2024/day07.txt`.

O arquivo nasce como stub (`object Day07 extends Solution(2024, 7)`); é só
sobrescrever as partes:

```scala
object Day07 extends Solution(2024, 7):
  override def part1(in: Input): Any = in.lines.size
  override def part2(in: Input): Any = in.ints.sum
```

O runner enxerga o novo dia sozinho — não existe lista de registro para atualizar.
Quem não sobrescreve nada continua marcado como não implementado e é ignorado
por `run <ano>` e `run all`.

Quando sair o AoC 2026: `./scripts/scaffold-year.sh 2026` e acrescente o ano em
`Solutions.years`.

## Estrutura

```
build.sbt                     sbt 2.0.8, Scala 3.9.0, munit
project/plugins.sbt           sbt-scalafmt
.scalafmt.conf                contrato de formatação (o mesmo dos outros repos Scala)
.vscode/                      settings + extensões recomendadas (Metals, Docs View)
scripts/
  new-day.sh                  cria solução + teste + input de um dia
  fetch-input.sh              baixa só o input
  scaffold-year.sh            cria os 25 stubs de um ano novo
inputs/YYYY/dayDD.txt         inputs (git-ignorados de propósito)
src/main/scala/aoc/
  Solution.scala              classe base (year, day, part1, part2)
  Input.scala                 texto do puzzle já parseado de N formas
  Solutions.scala             descoberta por reflexão
  AocInput.scala              leitura em disco + download com cache
  Runner.scala                CLI
  util/                       Point, Grid, Search, Numbers
  yYYYY/DayDD.scala           uma solução por arquivo
src/test/scala/aoc/           munit; exemplos do enunciado como teste
```

## O que vem pronto

**`Input`** — `text`, `lines`, `blocks` (separados por linha em branco), `ints`,
`longs`, `intLines`, `words`, `grid`, `splitOn(sep)`, `captures(regex)`.

**`util.Point`** — aritmética 2D, `neighbors4/8`, `manhattan`, `rotateLeft/Right`,
`lineTo`. Convenção: `x` para a direita, `y` para **baixo**.

**`util.Grid[A]`** — grade imutável indexada por `Point`: `get`, `updated`,
`points`, `entries`, `find`, `where`, `neighbors4/8`, `map`, `transpose`, `render`.

**`util.Search`** — `bfs` (distâncias), `bfsPath` (caminho), `dijkstra` e
`dijkstraTo`, todos sobre uma função de sucessores genérica.

**`util.Numbers`** — `gcd`, `lcm`, `crt` (teorema chinês do resto), `findCycle`.

## Formatação e editor

O `.scalafmt.conf` é o mesmo contrato usado nos outros repos Scala: sintaxe de
indentação do Scala 3, `maxColumn = 100`, sem alinhamento vertical (alinhar
produz diff em linha que ninguém tocou) e `end` marker a partir de 25 linhas.

```bash
sbt scalafmtAll        # formata main + test
sbt scalafmtCheckAll   # só verifica — é o gate antes de commitar
```

No VS Code, `.vscode/settings.json` liga o format-on-save via Metals (escopado
em `[scala]`, para não reflowar o Markdown), importa o build sem prompt
(`metals.autoImportBuilds: "all"`), põe a régua em 100 e deixa o hover sticky.
`.vscode/extensions.json` recomenda Metals, Docs View e Better Comments.

## Testes

Os exemplos do enunciado viram teste com [munit](https://scalameta.org/munit/):

```scala
class Day01Suite extends FunSuite:
  test("parte 1: exemplos do enunciado") {
    assertEquals(Day01.part1(Input("(())")), 0)
  }
```

Os testes gerados pelo `new-day.sh` já nascem com `.ignore` — tire o `.ignore`
depois de colar o exemplo do dia.

## Sobre inputs e enunciados

`inputs/` está no `.gitignore` porque o AoC pede que inputs e textos dos puzzles
não sejam redistribuídos — só as soluções. O download usa seu cookie de sessão
(em `.env`, também git-ignorado), guarda o arquivo em disco e nunca rebaixa o
mesmo dia, seguindo as [regras de automação](https://www.reddit.com/r/adventofcode/wiki/faqs/automation)
do evento (inclusive o `User-Agent` com contato).
