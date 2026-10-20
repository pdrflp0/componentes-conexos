# Componentes Conexos — Seminário AV2 de Grafos

Equipe: **Pedro Felipe, Leonardo Costa e Pedro Melo**

Implementação em **Java** de três algoritmos para encontrar os componentes conexos de um
grafo não direcionado e sua aplicação a um problema real: **quais municípios ficam isolados
quando uma cheia interdita estradas**, e em que ordem reabrir as estradas.

| Algoritmo | Classe | Tempo |
|---|---|---|
| Busca em profundidade (DFS, iterativa) | `BuscaEmProfundidade` | O(V + E) |
| Busca em largura (BFS) | `BuscaEmLargura` | O(V + E) |
| União-Busca (união por tamanho + compressão de caminho) | `UniaoBusca` | O((V + E) · α(V)) |

## Dependências

* **JDK 17 ou superior** (testado com OpenJDK 21, compilando com `--release 17`).
* Nenhuma biblioteca externa, nem Maven ou Gradle.

Para conferir a versão instalada: `java -version` e `javac -version`.

## Como compilar

Na pasta do projeto:

```bash
javac -encoding UTF-8 -d out src/componentes/*.java testes/componentes/*.java
```

No Windows (PowerShell ou CMD), o comando é o mesmo. Se os acentos aparecerem trocados no
terminal, execute `chcp 65001` antes.

## Como executar

Todos os comandos devem ser executados **na pasta do projeto**, porque a aplicação lê
`dados/rede_litoral_leste.txt`.

```bash
java -cp out componentes.Main                    # aplicação: cheia no Litoral Leste / Baixo Jaguaribe
java -cp out componentes.Main exemplo            # exemplo didático passo a passo (DFS e União-Busca)
java -cp out componentes.Main desempenho         # tempos em grafos aleatórios (~40 s)
java -cp out componentes.Main componentes dados/rede_litoral_leste.txt bfs   # qualquer grafo em arquivo
java -cp out componentes.TestesComponentes       # testes automatizados
```

### O que a aplicação mostra

1. A rede em condições normais e as **estradas críticas** (cuja interdição isolada divide a rede).
2. A rede **após a cheia**: os componentes conexos, ou seja, os grupos de municípios que ainda se
   alcançam por terra, e quais deles ficaram **sem base de apoio**.
3. Consultas de conectividade ("ainda dá para ir de Fortaleza a Aracati?").
4. A simulação do **plano de reparo** com União-Busca, estrada por estrada, mostrando quais
   reaberturas reconectam grupos e quais são redundantes.
5. Uma **ordem de reparo sugerida**, que leva apoio primeiro aos municípios isolados.

## Formato dos arquivos de grafo

Uma declaração por linha; `#` inicia comentário.

```
vertice: Fortaleza                  # vértice (mesmo que isolado)
aresta:  Fortaleza -- Aquiraz       # aresta não direcionada
base:    Fortaleza                  # (aplicação) município com base de apoio
alagada: Beberibe -- Aracati        # (aplicação) estrada interditada, em ordem de reparo
consulta: Fortaleza -- Aracati      # (aplicação) par a consultar
```

> A rede em `dados/` é um **modelo ilustrativo**: os municípios são reais, mas as ligações
> representam as estradas de forma aproximada e não reproduzem a malha viária oficial.

## Estrutura

```
src/componentes/
  Grafo.java                 grafo não direcionado com listas de adjacência
  AlgoritmoComponentes.java  interface comum aos algoritmos
  BuscaEmProfundidade.java   DFS iterativa (e a versão recursiva, para comparação)
  BuscaEmLargura.java        BFS
  UniaoBusca.java            União-Busca (estrutura incremental)
  Componentes.java           resultado: rótulo por vértice, tamanhos, consultas O(1)
  LeitorGrafo.java           leitura do formato de arquivo acima
  AplicacaoEnchente.java     aplicação (cheia e plano de reparo)
  ExemploDidatico.java       exemplo do relatório, passo a passo
  Desempenho.java            medição de tempo
  Main.java                  ponto de entrada
testes/componentes/
  TestesComponentes.java     4.073 verificações (inclui comparação com fecho transitivo)
dados/
  rede_litoral_leste.txt     rede da aplicação
saidas/                      saídas reais usadas no relatório (aplicação, exemplo, testes, desempenho)
```
