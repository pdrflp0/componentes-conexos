package componentes;

import java.io.PrintStream;
import java.util.Arrays;

/**
 * Exemplo pequeno usado no relatório para ilustrar o conceito: 10 vértices (A..J),
 * 7 arestas e 4 componentes conexos. Mostra, passo a passo, a execução da DFS e da
 * União-Busca.
 */
public final class ExemploDidatico {

    private ExemploDidatico() {}

    /** O grafo do exemplo: {A,B,C,D}, {E,F,G}, {H,I} e {J} isolado. */
    public static Grafo grafo() {
        Grafo g = new Grafo();
        for (char c = 'A'; c <= 'J'; c++) g.adicionarVertice(String.valueOf(c));
        String[][] arestas = {{"A", "B"}, {"A", "C"}, {"B", "D"}, {"C", "D"}, {"E", "F"}, {"F", "G"}, {"H", "I"}};
        for (String[] e : arestas) g.adicionarAresta(e[0], e[1]);
        return g;
    }

    public static void executar(PrintStream out) {
        Grafo g = grafo();
        out.println("Grafo de exemplo: " + g.numVertices() + " vértices, " + g.numArestas() + " arestas");
        for (int[] e : g.arestas()) out.print("  " + g.nome(e[0]) + "-" + g.nome(e[1]));
        out.println("\n");

        // ---- DFS passo a passo (mesma lógica de BuscaEmProfundidade.explorar)
        out.println("DFS iterativa, passo a passo:");
        int n = g.numVertices();
        int[] rotulo = new int[n];
        Arrays.fill(rotulo, -1);
        int[] pilha = new int[n];
        int k = 0;
        for (int s = 0; s < n; s++) {
            if (rotulo[s] != -1) continue;
            out.printf("  componente %d: começa em %s%n", k + 1, g.nome(s));
            int topo = 0;
            rotulo[s] = k;
            pilha[topo++] = s;
            while (topo > 0) {
                int u = pilha[--topo];
                StringBuilder novos = new StringBuilder();
                for (int w : g.vizinhos(u)) {
                    if (rotulo[w] == -1) {
                        rotulo[w] = k;
                        pilha[topo++] = w;
                        novos.append(novos.length() == 0 ? "" : ", ").append(g.nome(w));
                    }
                }
                out.printf("    desempilha %s -> descobre: %-6s pilha = %s%n", g.nome(u),
                        novos.length() == 0 ? "-" : novos, pilhaTexto(g, pilha, topo));
            }
            k++;
        }

        // ---- União-Busca passo a passo
        out.println("\nUnião-Busca, aresta por aresta (começa com 10 conjuntos unitários):");
        UniaoBusca uf = new UniaoBusca(n);
        for (int[] e : g.arestas()) {
            boolean uniu = uf.unir(e[0], e[1]);
            out.printf("  unir(%s, %s): %-28s componentes = %d%n", g.nome(e[0]), g.nome(e[1]),
                    uniu ? "conjuntos distintos -> une" : "já no mesmo conjunto -> nada", uf.numComponentes());
        }

        // ---- Resultado dos três algoritmos
        out.println("\nResultado:");
        for (AlgoritmoComponentes a : Main.algoritmos()) {
            Componentes c = a.calcular(g);
            StringBuilder sb = new StringBuilder();
            for (var grupo : c.listar()) {
                sb.append(" {");
                for (int i = 0; i < grupo.size(); i++) sb.append(i == 0 ? "" : ",").append(g.nome(grupo.get(i)));
                sb.append("}");
            }
            out.printf("  %-12s k = %d:%s%n", a.nome(), c.quantidade(), sb);
        }
    }

    private static String pilhaTexto(Grafo g, int[] pilha, int topo) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < topo; i++) sb.append(i == 0 ? "" : " ").append(g.nome(pilha[i]));
        return sb.append("]").toString();
    }
}
