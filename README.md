# Componentes Conexos

Implementação em Java para calcular os componentes conexos de uma rede de municípios antes e depois da interdição de estradas.

## Dependências

- JDK 17 ou superior;
- nenhuma biblioteca externa;

Verifique a instalação com:

```bash
java -version
javac -version
```

## Como executar

Execute os comandos na raiz do projeto.

### Compilar

```bash
javac -encoding UTF-8 -d out src/*.java
```

### Executar

```bash
java -cp out Main
```

## O que o programa faz

O programa:

1. cria a rede original com 18 municípios e 21 estradas;
2. calcula os componentes conexos antes da enchente;
3. cria uma segunda versão da rede;
4. remove as 8 estradas interditadas;
5. calcula novamente os componentes conexos;
6. exibe os grupos de municípios antes e depois.

A busca utilizada é a DFS iterativa, implementada com uma pilha.

## Resultado esperado

```text
ANTES DA ENCHENTE
Municípios: 18
Componentes conexos: 1

DEPOIS DA ENCHENTE
Municípios: 18
Componentes conexos: 5
```

O programa também imprime os municípios pertencentes a cada grupo.