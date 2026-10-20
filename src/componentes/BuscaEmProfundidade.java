package componentes;

import java.util.Arrays;
import java.util.List;

/**
 * Componentes conexos por busca em profundidade (DFS).
 *
 * <p>Para cada vértice ainda não visitado, inicia-se uma DFS que rotula com o mesmo número
 * todos os vértices alcançáveis a partir dele. Custo: O(V + E).</p>
 *
 * <p>A versão principal é <b>iterativa</b> (pilha explícita). A versão recursiva também
 * está disponível, por ser a forma clássica do livro, mas em grafos com caminhos muito
 * longos ela esgota a pilha de chamadas da JVM ({@link StackOverflowError}).</p>
 */
public final class BuscaEmProfundidade implements AlgoritmoComponentes {

    private final boolean recursiva;

    public BuscaEmProfundidade() { this(false); }

    public BuscaEmProfundidade(boolean recursiva) { this.recursiva = recursiva; }

    @Override
    public String nome() { return recursiva ? "DFS recursiva" : "DFS"; }

    @Override
    public Componentes calcular(Grafo g) {
        int n = g.numVertices();
        int[] rotulo = new int[n];
        Arrays.fill(rotulo, -1);                 // -1 = ainda não visitado
        int k = 0;
        int[] pilha = new int[n];                // cada vértice entra na pilha no máximo uma vez
        for (int s = 0; s < n; s++) {
            if (rotulo[s] != -1) continue;       // s já pertence a um componente
            if (recursiva) visitar(g, s, k, rotulo);
            else explorar(g, s, k, rotulo, pilha);
            k++;                                 // terminou um componente
        }
        return new Componentes(rotulo, k);
    }

    /** DFS iterativa a partir de s: rotula com k tudo o que for alcançável. */
    private static void explorar(Grafo g, int s, int k, int[] rotulo, int[] pilha) {
        int topo = 0;
        rotulo[s] = k;                           // marca ao empilhar: nunca empilha duas vezes
        pilha[topo++] = s;
        while (topo > 0) {
            int u = pilha[--topo];
            for (int w : g.vizinhos(u)) {
                if (rotulo[w] == -1) {
                    rotulo[w] = k;
                    pilha[topo++] = w;
                }
            }
        }
    }

    /** DFS recursiva clássica (cuidado: profundidade da recursão = tamanho do caminho). */
    private static void visitar(Grafo g, int u, int k, int[] rotulo) {
        rotulo[u] = k;
        List<Integer> viz = g.vizinhos(u);
        for (int w : viz)
            if (rotulo[w] == -1) visitar(g, w, k, rotulo);
    }
}
