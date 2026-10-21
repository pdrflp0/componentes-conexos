public final class Main {
    public static void main(String[] args) {
        Grafo antes = redeOriginal();
        Grafo depois = redeOriginal();
        String[][] alagadas = {
                {"Beberibe", "Aracati"}, {"Fortim", "Aracati"},
                {"Palhano", "Aracati"}, {"Russas", "Limoeiro do Norte"},
                {"Morada Nova", "Limoeiro do Norte"}, {"Itaiçaba", "Aracati"},
                {"Jaguaruana", "Itaiçaba"}, {"Russas", "Jaguaruana"}
        };
        for (String[] estrada : alagadas) depois.removerAresta(estrada[0], estrada[1]);

        BuscaEmProfundidade dfs = new BuscaEmProfundidade();
        imprimir("ANTES DA ENCHENTE", antes, dfs.calcular(antes));
        imprimir("DEPOIS DA ENCHENTE", depois, dfs.calcular(depois));
    }

    private static Grafo redeOriginal() {
        Grafo g = new Grafo(
                "Fortaleza", "Aquiraz", "Pindoretama", "Cascavel", "Beberibe",
                "Fortim", "Aracati", "Icapuí", "Pacajus", "Chorozinho",
                "Russas", "Morada Nova", "Limoeiro do Norte", "Quixeré",
                "Tabuleiro do Norte", "Palhano", "Jaguaruana", "Itaiçaba");
        String[][] estradas = {
                {"Fortaleza", "Aquiraz"}, {"Aquiraz", "Pindoretama"},
                {"Pindoretama", "Cascavel"}, {"Cascavel", "Beberibe"},
                {"Beberibe", "Fortim"}, {"Beberibe", "Aracati"},
                {"Fortim", "Aracati"}, {"Aracati", "Icapuí"},
                {"Fortaleza", "Pacajus"}, {"Pacajus", "Chorozinho"},
                {"Chorozinho", "Russas"}, {"Russas", "Morada Nova"},
                {"Russas", "Limoeiro do Norte"}, {"Morada Nova", "Limoeiro do Norte"},
                {"Limoeiro do Norte", "Quixeré"}, {"Limoeiro do Norte", "Tabuleiro do Norte"},
                {"Russas", "Palhano"}, {"Palhano", "Aracati"},
                {"Russas", "Jaguaruana"}, {"Jaguaruana", "Itaiçaba"},
                {"Itaiçaba", "Aracati"}
        };
        for (String[] estrada : estradas) g.adicionarAresta(estrada[0], estrada[1]);
        return g;
    }

    private static void imprimir(String titulo, Grafo g, Componentes c) {
        System.out.println("\n" + titulo);
        System.out.println("Municípios: " + g.tamanho());
        System.out.println("Componentes conexos: " + c.quantidade());
        for (int grupo = 0; grupo < c.quantidade(); grupo++) {
            System.out.print("Grupo " + (grupo + 1) + ": ");
            boolean primeiro = true;
            for (int v = 0; v < g.tamanho(); v++) {
                if (c.rotuloDe(v) == grupo) {
                    if (!primeiro) System.out.print(", ");
                    System.out.print(g.nome(v));
                    primeiro = false;
                }
            }
            System.out.println();
        }
    }
}
