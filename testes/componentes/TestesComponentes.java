package componentes;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Testes automatizados (sem dependências externas).
 * Executar: java -cp out componentes.TestesComponentes
 */
public final class TestesComponentes {

    private static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    private static int verificacoes = 0, falhas = 0;

    public static void main(String[] args) throws Exception {
        exemploDidatico();
        casosLimite();
        arestasParalelasELacos();
        remocaoDeArestas();
        aleatoriosContraForcaBruta();
        uniaoBuscaIncremental();
        dfsRecursivaEstouraPilha();
        leituraDoArquivoDaAplicacao();
        OUT.printf("%n%d verificações, %d falha(s)%n", verificacoes, falhas);
        OUT.println(falhas == 0 ? "TODOS OS TESTES PASSARAM" : "HÁ TESTES FALHANDO");
        System.exit(falhas == 0 ? 0 : 1);
    }

    static void exemploDidatico() {
        secao("Exemplo didático: 10 vértices, 4 componentes");
        Grafo g = ExemploDidatico.grafo();
        for (AlgoritmoComponentes a : Main.algoritmos()) {
            Componentes c = a.calcular(g);
            verificar(c.quantidade() == 4, a.nome() + ": k = 4");
            verificar(c.listar().toString().equals("[[0, 1, 2, 3], [4, 5, 6], [7, 8], [9]]"),
                    a.nome() + ": grupos {A,B,C,D} {E,F,G} {H,I} {J}");
            verificar(c.conectados(g.indiceDe("A"), g.indiceDe("D")), a.nome() + ": A e D conectados");
            verificar(!c.conectados(g.indiceDe("D"), g.indiceDe("E")), a.nome() + ": D e E separados");
        }
    }

    static void casosLimite() {
        secao("Casos-limite: vazio, isolados, caminho, completo");
        for (AlgoritmoComponentes a : Main.algoritmos()) {
            verificar(a.calcular(new Grafo()).quantidade() == 0, a.nome() + ": grafo vazio tem 0 componentes");
            verificar(a.calcular(Grafo.comVertices(7)).quantidade() == 7, a.nome() + ": 7 isolados = 7 componentes");
            verificar(a.calcular(Desempenho.caminho(50)).quantidade() == 1, a.nome() + ": caminho é conexo");
            Grafo k6 = Grafo.comVertices(6);
            for (int u = 0; u < 6; u++) for (int v = u + 1; v < 6; v++) k6.adicionarAresta(u, v);
            verificar(k6.numArestas() == 15 && a.calcular(k6).quantidade() == 1, a.nome() + ": K6 é conexo");
        }
    }

    static void arestasParalelasELacos() {
        secao("Arestas paralelas e laços");
        Grafo g = Grafo.comVertices(3);
        verificar(g.adicionarAresta(0, 1), "primeira aresta 0-1 é inserida");
        verificar(!g.adicionarAresta(1, 0), "aresta paralela 1-0 é ignorada");
        verificar(g.adicionarAresta(2, 2), "laço 2-2 é aceito");
        verificar(g.numArestas() == 2 && g.vizinhos(2).size() == 1, "laço guardado uma única vez");
        for (AlgoritmoComponentes a : Main.algoritmos())
            verificar(a.calcular(g).quantidade() == 2, a.nome() + ": laço não conecta 2 a ninguém");
    }

    static void remocaoDeArestas() {
        secao("Remoção de arestas divide componentes");
        Grafo g = Desempenho.caminho(6);
        verificar(g.removerAresta(2, 3), "remove 2-3");
        verificar(!g.removerAresta(2, 3), "não remove duas vezes");
        for (AlgoritmoComponentes a : Main.algoritmos())
            verificar(a.calcular(g).listar().toString().equals("[[0, 1, 2], [3, 4, 5]]"), a.nome() + ": {0,1,2} {3,4,5}");
    }

    /** Compara os três algoritmos com o fecho transitivo (Floyd–Warshall booleano). */
    static void aleatoriosContraForcaBruta() {
        secao("500 grafos aleatórios: DFS = BFS = União-Busca = fecho transitivo");
        Random r = new Random(2026);
        for (int caso = 0; caso < 500; caso++) {
            int n = 1 + r.nextInt(40), m = r.nextInt(n * 2 + 1);
            Grafo g = Grafo.comVertices(n);
            for (int i = 0; i < m; i++) g.adicionarAresta(r.nextInt(n), r.nextInt(n));
            boolean[][] alc = new boolean[n][n];
            for (int v = 0; v < n; v++) alc[v][v] = true;
            for (int[] e : g.arestas()) alc[e[0]][e[1]] = alc[e[1]][e[0]] = true;
            for (int k = 0; k < n; k++)
                for (int i = 0; i < n; i++)
                    if (alc[i][k]) for (int j = 0; j < n; j++) if (alc[k][j]) alc[i][j] = true;
            int[] ref = null;
            for (AlgoritmoComponentes a : Main.algoritmos()) {
                Componentes c = a.calcular(g);
                boolean ok = true;
                for (int u = 0; u < n && ok; u++)
                    for (int v = 0; v < n && ok; v++) ok = c.conectados(u, v) == alc[u][v];
                verificar(ok, a.nome() + ": caso " + caso + " confere com o fecho transitivo");
                int soma = 0;
                for (int i = 0; i < c.quantidade(); i++) soma += c.tamanho(i);
                verificar(soma == n, a.nome() + ": soma dos tamanhos = V");
                if (ref == null) ref = c.rotulos();
                else verificar(Arrays.equals(ref, c.rotulos()), a.nome() + ": mesmos rótulos que a DFS");
            }
        }
    }

    static void uniaoBuscaIncremental() {
        secao("União-Busca incremental acompanha a DFS aresta a aresta");
        Random r = new Random(7);
        int n = 300;
        Grafo g = Grafo.comVertices(n);
        UniaoBusca uf = new UniaoBusca(n);
        for (int i = 0; i < 400; i++) {
            int u = r.nextInt(n), v = r.nextInt(n);
            g.adicionarAresta(u, v);
            uf.unir(u, v);
            if (i % 20 == 0)
                verificar(uf.numComponentes() == new BuscaEmProfundidade().calcular(g).quantidade(),
                        "após " + (i + 1) + " arestas");
        }
    }

    static void dfsRecursivaEstouraPilha() {
        secao("DFS recursiva estoura a pilha; a iterativa não");
        Grafo g = Desempenho.caminho(1_000_000);
        boolean estourou = false;
        try {
            new BuscaEmProfundidade(true).calcular(g);
        } catch (StackOverflowError e) {
            estourou = true;
        }
        verificar(estourou, "recursiva lança StackOverflowError em caminho de 10^6 vértices");
        verificar(new BuscaEmProfundidade().calcular(g).quantidade() == 1, "iterativa resolve o mesmo caminho");
    }

    static void leituraDoArquivoDaAplicacao() throws Exception {
        secao("Leitura do arquivo da aplicação");
        LeitorGrafo.Dados d = LeitorGrafo.ler(Path.of("dados", "rede_litoral_leste.txt"));
        verificar(d.grafo().numVertices() == 18, "18 municípios");
        verificar(d.grafo().numArestas() == 21, "21 estradas");
        verificar(d.extra("base").size() == 4 && d.extra("alagada").size() == 8, "4 bases e 8 estradas alagadas");
        Grafo cheia = d.grafo().copia();
        for (String a : d.extra("alagada")) {
            String[] p = LeitorGrafo.separarAresta(a);
            verificar(cheia.removerAresta(p[0], p[1]), "estrada alagada existe na rede: " + a);
        }
        Componentes c = new BuscaEmProfundidade().calcular(cheia);
        verificar(c.quantidade() == 5, "após a cheia: 5 componentes");
        verificar(d.grafo().numArestas() == 21, "a cópia não altera a rede original");
        List<String> esperados = List.of("Itaiçaba", "Jaguaruana");
        for (String m : esperados)
            verificar(c.tamanho(c.componenteDe(cheia.indiceDe(m))) == 1, m + " fica totalmente isolado");
    }

    // ---------------------------------------------------------------- utilitários
    private static void secao(String nome) { OUT.println("- " + nome); }

    private static void verificar(boolean cond, String msg) {
        verificacoes++;
        if (!cond) {
            falhas++;
            OUT.println("  [FALHOU] " + msg);
        }
    }
}
