package componentes;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * Ponto de entrada.
 *
 * <pre>
 * java -cp out componentes.Main                         aplicação (rede do Litoral Leste)
 * java -cp out componentes.Main aplicacao [arquivo]     aplicação com outro arquivo de rede
 * java -cp out componentes.Main exemplo                 exemplo didático passo a passo
 * java -cp out componentes.Main componentes ARQ [alg]   componentes de qualquer grafo (dfs|bfs|uf)
 * java -cp out componentes.Main desempenho              medição de tempo
 * </pre>
 */
public final class Main {

    static final Path REDE_PADRAO = Path.of("dados", "rede_litoral_leste.txt");

    private Main() {}

    /** As três estratégias implementadas. */
    public static AlgoritmoComponentes[] algoritmos() {
        return new AlgoritmoComponentes[] {new BuscaEmProfundidade(), new BuscaEmLargura(), new UniaoBusca()};
    }

    public static void main(String[] args) throws IOException {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        String comando = args.length > 0 ? args[0].toLowerCase() : "aplicacao";
        switch (comando) {
            case "aplicacao" -> AplicacaoEnchente
                    .deArquivo(args.length > 1 ? Path.of(args[1]) : REDE_PADRAO, out).executar();
            case "exemplo" -> ExemploDidatico.executar(out);
            case "desempenho" -> Desempenho.executar(out);
            case "componentes" -> {
                if (args.length < 2) { ajuda(out); System.exit(2); }
                componentes(Path.of(args[1]), args.length > 2 ? args[2] : "dfs", out);
            }
            default -> { ajuda(out); System.exit(comando.equals("ajuda") ? 0 : 2); }
        }
    }

    private static void componentes(Path arquivo, String alg, PrintStream out) throws IOException {
        Grafo g = LeitorGrafo.ler(arquivo).grafo();
        AlgoritmoComponentes a = switch (alg.toLowerCase()) {
            case "bfs" -> new BuscaEmLargura();
            case "uf", "uniao" -> new UniaoBusca();
            default -> new BuscaEmProfundidade();
        };
        Componentes c = a.calcular(g);
        out.printf("%s: %d vértices, %d arestas, %d componente(s)%n", a.nome(), g.numVertices(), g.numArestas(),
                c.quantidade());
        var grupos = c.listar();
        for (int i = 0; i < grupos.size(); i++) {
            StringBuilder sb = new StringBuilder();
            for (int v : grupos.get(i)) sb.append(sb.length() == 0 ? "" : ", ").append(g.nome(v));
            out.printf("  C%d (%d): %s%n", i + 1, grupos.get(i).size(), sb);
        }
    }

    private static void ajuda(PrintStream out) {
        out.println("""
                Uso: java -cp out componentes.Main [comando]
                  aplicacao [arquivo]       isolamento de municípios por uma cheia (padrão)
                  exemplo                   exemplo didático passo a passo (DFS e União-Busca)
                  componentes ARQ [alg]     componentes de um grafo em arquivo (alg = dfs | bfs | uf)
                  desempenho                tempo dos três algoritmos em grafos aleatórios
                """);
    }
}
