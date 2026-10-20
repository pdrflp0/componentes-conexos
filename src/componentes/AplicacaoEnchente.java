package componentes;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Aplicação: isolamento de municípios por uma cheia.
 *
 * <p>Modelagem: cada município é um vértice e cada estrada de mão dupla é uma aresta.
 * Uma cheia interdita estradas (remove arestas). Os componentes conexos do grafo
 * resultante são exatamente os grupos de municípios que ainda se alcançam por terra.
 * Um componente sem nenhuma base de apoio precisa de socorro por via aérea ou fluvial.</p>
 *
 * <p>Etapas: (1) rede normal e estradas críticas; (2) cheia: componentes e grupos sem
 * apoio; (3) consultas de conectividade; (4) plano de reparo simulado com União-Busca;
 * (5) ordem de reparo sugerida, que reconecta primeiro quem está sem apoio.</p>
 */
public final class AplicacaoEnchente {

    private final Grafo rede;
    private final Set<Integer> bases = new HashSet<>();
    private final List<int[]> alagadas = new ArrayList<>();
    private final List<int[]> consultas = new ArrayList<>();
    private final PrintStream out;

    public AplicacaoEnchente(LeitorGrafo.Dados dados, PrintStream out) {
        this.rede = dados.grafo();
        this.out = out;
        for (String b : dados.extra("base")) bases.add(rede.indiceDe(b));
        for (String a : dados.extra("alagada")) alagadas.add(par(a));
        for (String c : dados.extra("consulta")) consultas.add(par(c));
    }

    public static AplicacaoEnchente deArquivo(Path arquivo, PrintStream out) throws IOException {
        return new AplicacaoEnchente(LeitorGrafo.ler(arquivo), out);
    }

    public void executar() {
        AlgoritmoComponentes dfs = new BuscaEmProfundidade();

        // ------------------------------------------------------------------ 1
        titulo("1. REDE EM CONDIÇÕES NORMAIS");
        Componentes normal = dfs.calcular(rede);
        out.printf("  %d municípios, %d estradas, %d componente(s) conexo(s)%n",
                rede.numVertices(), rede.numArestas(), normal.quantidade());
        out.println("  Bases de apoio: " + nomes(new ArrayList<>(ordenar(bases))));

        out.println("\n  Estradas críticas (se apenas ela for interditada, a rede se divide):");
        int criticas = 0;
        for (int[] e : rede.arestas()) {
            Grafo teste = rede.copia();
            teste.removerAresta(e[0], e[1]);
            Componentes c = dfs.calcular(teste);
            if (c.quantidade() > normal.quantidade()) {
                criticas++;
                List<Integer> menor = menorLado(c, e);
                out.printf("    - %-40s isola: %s%n", rotulo(e), nomes(menor));
            }
        }
        if (criticas == 0) out.println("    (nenhuma)");
        out.printf("  %d de %d estradas são críticas.%n", criticas, rede.numArestas());

        // ------------------------------------------------------------------ 2
        titulo("2. APÓS A CHEIA");
        Grafo cheia = rede.copia();
        out.println("  Estradas interditadas:");
        for (int[] e : alagadas) {
            cheia.removerAresta(e[0], e[1]);
            out.println("    x " + rotulo(e));
        }
        Componentes comp = dfs.calcular(cheia);
        verificarConcordancia(cheia, comp);
        out.printf("%n  Resultado: %d componentes conexos (antes: %d)%n%n", comp.quantidade(), normal.quantidade());

        int semBase = 0;
        List<List<Integer>> grupos = comp.listar();
        for (int c = 0; c < grupos.size(); c++) {
            List<Integer> g = grupos.get(c);
            List<Integer> basesDoGrupo = new ArrayList<>();
            for (int v : g) if (bases.contains(v)) basesDoGrupo.add(v);
            String situacao;
            if (basesDoGrupo.isEmpty()) {
                semBase++;
                situacao = "SEM BASE DE APOIO -> socorro aéreo/fluvial";
            } else {
                situacao = "atendido por " + nomes(basesDoGrupo);
            }
            out.printf("  Componente %d (%d município%s): %s%n", c + 1, g.size(), g.size() > 1 ? "s" : "", nomes(g));
            out.printf("     situação: %s%n", situacao);
        }
        out.printf("%n  %d componente(s) sem base de apoio.%n", semBase);

        // ------------------------------------------------------------------ 3
        titulo("3. CONSULTAS DE CONECTIVIDADE (O(1) após rotular)");
        for (int[] q : consultas) {
            boolean antes = normal.conectados(q[0], q[1]);
            boolean depois = comp.conectados(q[0], q[1]);
            out.printf("  %-40s antes: %-3s | depois: %s%n", rotulo(q), simNao(antes), simNao(depois));
        }

        // ------------------------------------------------------------------ 4
        titulo("4. PLANO DE REPARO DA DEFESA CIVIL (simulado com União-Busca)");
        simularReparo(cheia, alagadas, "Ordem planejada");

        // ------------------------------------------------------------------ 5
        titulo("5. ORDEM DE REPARO SUGERIDA");
        out.println("  Critério guloso: primeiro as estradas que ligam um grupo SEM base a um grupo COM base;");
        out.println("  depois, as que unem dois grupos ainda separados; estradas redundantes ficam por último.\n");
        simularReparo(cheia, ordemSugerida(cheia), "Ordem sugerida");
    }

    /** Reabre as estradas na ordem dada, mantendo os componentes com União-Busca. */
    private void simularReparo(Grafo cheia, List<int[]> ordem, String nomeOrdem) {
        UniaoBusca uf = estruturaInicial(cheia);
        boolean[] temBase = basesPorRaiz(uf);
        out.printf("  %-4s %-34s %-12s %-12s %s%n", "Pas.", "Estrada reaberta", "Efeito", "Componentes", "Sem base");
        out.printf("  %-4s %-34s %-12s %-12d %d%n", "0", "(situação após a cheia)", "-", uf.numComponentes(),
                contarSemBase(uf, temBase));
        int passoTodosAtendidos = contarSemBase(uf, temBase) == 0 ? 0 : -1, passoReconectado = -1, uteis = 0;
        for (int i = 0; i < ordem.size(); i++) {
            int[] e = ordem.get(i);
            int ra = uf.buscar(e[0]), rb = uf.buscar(e[1]);
            boolean basesUniao = temBase[ra] || temBase[rb];
            boolean util = uf.unir(e[0], e[1]);
            if (util) {
                uteis++;
                temBase[uf.buscar(e[0])] = basesUniao;
            }
            int sem = contarSemBase(uf, temBase);
            if (sem == 0 && passoTodosAtendidos < 0) passoTodosAtendidos = i + 1;
            if (uf.numComponentes() == 1 && passoReconectado < 0) passoReconectado = i + 1;
            out.printf("  %-4d %-34s %-12s %-12d %d%n", i + 1, rotulo(e), util ? "reconecta" : "redundante",
                    uf.numComponentes(), sem);
        }
        out.printf("%n  %s: todos os grupos com acesso a uma base no passo %d; rede reunificada no passo %d.%n",
                nomeOrdem, passoTodosAtendidos, passoReconectado);
        out.printf("  Estradas que efetivamente reconectaram grupos: %d de %d.%n", uteis, ordem.size());
    }

    /** Ordem gulosa: prioriza ligar componentes sem base a componentes com base. */
    private List<int[]> ordemSugerida(Grafo cheia) {
        UniaoBusca uf = estruturaInicial(cheia);
        boolean[] temBase = basesPorRaiz(uf);
        List<int[]> restantes = new ArrayList<>(alagadas);
        List<int[]> ordem = new ArrayList<>();
        while (true) {
            int escolha = -1;
            for (int i = 0; i < restantes.size() && escolha < 0; i++) {           // 1ª prioridade
                int ra = uf.buscar(restantes.get(i)[0]), rb = uf.buscar(restantes.get(i)[1]);
                if (ra != rb && temBase[ra] != temBase[rb]) escolha = i;
            }
            for (int i = 0; i < restantes.size() && escolha < 0; i++) {           // 2ª prioridade
                if (!uf.conectados(restantes.get(i)[0], restantes.get(i)[1])) escolha = i;
            }
            if (escolha < 0) break;                                                // só redundantes
            int[] e = restantes.remove(escolha);
            boolean b = temBase[uf.buscar(e[0])] || temBase[uf.buscar(e[1])];
            uf.unir(e[0], e[1]);
            temBase[uf.buscar(e[0])] = b;
            ordem.add(e);
        }
        ordem.addAll(restantes);                                                   // redundantes no fim
        return ordem;
    }

    private UniaoBusca estruturaInicial(Grafo g) {
        UniaoBusca uf = new UniaoBusca(g.numVertices());
        for (int[] e : g.arestas()) uf.unir(e[0], e[1]);
        return uf;
    }

    private boolean[] basesPorRaiz(UniaoBusca uf) {
        boolean[] temBase = new boolean[rede.numVertices()];
        for (int b : bases) temBase[uf.buscar(b)] = true;
        return temBase;
    }

    private int contarSemBase(UniaoBusca uf, boolean[] temBase) {
        int sem = 0;
        for (int v = 0; v < rede.numVertices(); v++)
            if (uf.buscar(v) == v && !temBase[v]) sem++;
        return sem;
    }

    /** Confere que DFS, BFS e União-Busca chegam ao mesmo resultado. */
    private void verificarConcordancia(Grafo g, Componentes referencia) {
        AlgoritmoComponentes[] outros = {new BuscaEmLargura(), new UniaoBusca()};
        StringBuilder sb = new StringBuilder("  Conferência: DFS");
        for (AlgoritmoComponentes a : outros) {
            boolean igual = java.util.Arrays.equals(a.calcular(g).rotulos(), referencia.rotulos());
            if (!igual) throw new IllegalStateException(a.nome() + " divergiu da DFS");
            sb.append(" = ").append(a.nome());
        }
        out.println(sb.append(" (mesmos componentes)"));
    }

    private List<Integer> menorLado(Componentes c, int[] e) {
        List<List<Integer>> grupos = c.listar();
        List<Integer> a = grupos.get(c.componenteDe(e[0])), b = grupos.get(c.componenteDe(e[1]));
        return a.size() <= b.size() ? a : b;
    }

    private int[] par(String texto) {
        String[] p = LeitorGrafo.separarAresta(texto);
        if (p == null) throw new IllegalArgumentException("par inválido: " + texto);
        return new int[] {rede.indiceDe(p[0]), rede.indiceDe(p[1])};
    }

    private String rotulo(int[] e) { return rede.nome(e[0]) + " -- " + rede.nome(e[1]); }

    private String nomes(List<Integer> vs) {
        List<String> n = new ArrayList<>();
        for (int v : vs) n.add(rede.nome(v));
        return String.join(", ", n);
    }

    private static List<Integer> ordenar(Set<Integer> s) {
        List<Integer> l = new ArrayList<>(s);
        l.sort(null);
        return l;
    }

    private static String simNao(boolean b) { return b ? "sim" : "não"; }

    private void titulo(String t) {
        out.println();
        out.println("=".repeat(78));
        out.println(" " + t);
        out.println("=".repeat(78));
    }
}
