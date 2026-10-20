package componentes;

/**
 * Estrutura União-Busca (Union-Find / Disjoint Set Union) e o cálculo de componentes
 * conexos a partir dela.
 *
 * <p>Cada componente é uma árvore cujo representante é a raiz. Duas otimizações deixam
 * as operações em tempo amortizado O(α(n)), praticamente constante:
 * <ul>
 *   <li><b>união por tamanho</b>: a árvore menor é pendurada na maior;</li>
 *   <li><b>compressão de caminho</b> (variante "halving"): durante a busca, cada nó passa
 *       a apontar para o avô, encurtando o caminho até a raiz.</li>
 * </ul>
 *
 * <p>Diferentemente da DFS/BFS, a União-Busca é <b>incremental</b>: aceita novas arestas
 * a qualquer momento sem recalcular tudo — é o que a aplicação usa para simular a
 * reabertura de estradas uma a uma.</p>
 */
public final class UniaoBusca implements AlgoritmoComponentes {

    private int[] pai;
    private int[] tam;
    private int componentes;

    /** Construtor usado apenas como estratégia ({@link #calcular(Grafo)}). */
    public UniaoBusca() { this(0); }

    /** Cria n conjuntos unitários {0}, {1}, ..., {n-1}. */
    public UniaoBusca(int n) {
        pai = new int[n];
        tam = new int[n];
        for (int i = 0; i < n; i++) {
            pai[i] = i;
            tam[i] = 1;
        }
        componentes = n;
    }

    /** Representante (raiz) do conjunto que contém x. */
    public int buscar(int x) {
        while (pai[x] != x) {
            pai[x] = pai[pai[x]];   // compressão de caminho (halving)
            x = pai[x];
        }
        return x;
    }

    /**
     * Une os conjuntos de a e b. Devolve true se eles eram distintos (a união reduziu o
     * número de componentes) e false se já estavam no mesmo conjunto.
     */
    public boolean unir(int a, int b) {
        int ra = buscar(a), rb = buscar(b);
        if (ra == rb) return false;
        if (tam[ra] < tam[rb]) { int t = ra; ra = rb; rb = t; }
        pai[rb] = ra;               // a árvore menor fica embaixo da maior
        tam[ra] += tam[rb];
        componentes--;
        return true;
    }

    public boolean conectados(int a, int b) { return buscar(a) == buscar(b); }

    public int numComponentes() { return componentes; }

    public int tamanhoDoConjunto(int x) { return tam[buscar(x)]; }

    @Override
    public String nome() { return "União-Busca"; }

    @Override
    public Componentes calcular(Grafo g) {
        UniaoBusca uf = new UniaoBusca(g.numVertices());
        for (int[] e : g.arestas()) uf.unir(e[0], e[1]);
        int[] rep = new int[g.numVertices()];
        for (int v = 0; v < rep.length; v++) rep[v] = uf.buscar(v);
        return Componentes.normalizar(rep);
    }
}
