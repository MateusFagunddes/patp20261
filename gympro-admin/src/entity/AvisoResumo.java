package entity;

public class AvisoResumo {

    private final int id;
    private final String titulo;
    private final String descricao;
    private final String dataPublicacao;
    private final String publico;
    private final String autor;

    public AvisoResumo(int id, String titulo, String descricao, String dataPublicacao, String publico, String autor) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataPublicacao = dataPublicacao;
        this.publico = publico;
        this.autor = autor;
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDataPublicacao() {
        return dataPublicacao;
    }

    public String getPublico() {
        return publico;
    }

    public String getAutor() {
        return autor;
    }
}
