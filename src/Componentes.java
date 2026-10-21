public final class Componentes {
    private final int[] rotulo;
    private final int quantidade;

    public Componentes(int[] rotulo, int quantidade) {
        this.rotulo = rotulo.clone();
        this.quantidade = quantidade;
    }

    public int quantidade() { return quantidade; }
    public int rotuloDe(int vertice) { return rotulo[vertice]; }
    public boolean conectados(int a, int b) { return rotulo[a] == rotulo[b]; }
}
