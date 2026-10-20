package componentes;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;

/**
 * Medição empírica do tempo dos três algoritmos em grafos aleatórios e demonstração do
 * limite da DFS recursiva. Os tempos dependem da máquina; o que interessa é a tendência
 * (crescimento linear em V + E).
 */
public final class Desempenho {

    private Desempenho() {}

    /** Grafo aleatório com n vértices e m arestas distintas (semente fixa = reprodutível). */
    public static Grafo aleatorio(int n, int m, long semente) {
        Grafo g = Grafo.comVertices(n);
        Random r = new Random(semente);
        while (g.numArestas() < m) {
            int u = r.nextInt(n), v = r.nextInt(n);
            if (u != v) g.adicionarAresta(u, v);
        }
        return g;
    }

    /** Caminho 0-1-2-...-(n-1): o pior caso de profundidade para a DFS recursiva. */
    public static Grafo caminho(int n) {
        Grafo g = Grafo.comVertices(n);
        for (int i = 0; i + 1 < n; i++) g.adicionarAresta(i, i + 1);
        return g;
    }

    public static void executar(PrintStream out) {
        AlgoritmoComponentes[] algs = Main.algoritmos();
        out.println("Tempo mediano de 7 execuções (ms), grafos aleatórios com semente fixa");
        out.printf("%10s %10s %10s %10s %10s %12s%n", "V", "E", "k", "DFS", "BFS", "União-Busca");
        int[] tamanhos = {10_000, 100_000, 1_000_000};
        double[] densidades = {0.5, 1.0, 2.0};               // E = densidade * V
        for (int n : tamanhos) {
            for (double d : densidades) {
                Grafo g = aleatorio(n, (int) (d * n), 42L + n);
                int k = algs[0].calcular(g).quantidade();
                double[] ms = new double[algs.length];
                for (int a = 0; a < algs.length; a++) ms[a] = medir(algs[a], g);
                out.printf("%10d %10d %10d %10.2f %10.2f %12.2f%n", n, g.numArestas(), k, ms[0], ms[1], ms[2]);
            }
        }

        out.println("\nDFS recursiva x iterativa num caminho (profundidade = V):");
        for (int n : new int[] {1_000, 10_000, 100_000, 1_000_000}) {
            Grafo g = caminho(n);
            String rec;
            try {
                rec = "ok (k = " + new BuscaEmProfundidade(true).calcular(g).quantidade() + ")";
            } catch (StackOverflowError e) {
                rec = "StackOverflowError";
            }
            String it = "ok (k = " + new BuscaEmProfundidade(false).calcular(g).quantidade() + ")";
            out.printf("  V = %9d   recursiva: %-20s iterativa: %s%n", n, rec, it);
        }
    }

    private static double medir(AlgoritmoComponentes a, Grafo g) {
        for (int i = 0; i < 3; i++) a.calcular(g);           // aquecimento do JIT
        double[] t = new double[7];
        for (int i = 0; i < t.length; i++) {
            long ini = System.nanoTime();
            a.calcular(g);
            t[i] = (System.nanoTime() - ini) / 1e6;
        }
        Arrays.sort(t);
        return t[t.length / 2];
    }
}
