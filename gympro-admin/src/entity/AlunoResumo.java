package entity;

public class AlunoResumo {

    private final String nome;
    private final String email;
    private final String telefone;
    private final String objetivo;
    private final String status;

    public AlunoResumo(String nome, String email, String telefone, String objetivo, String status) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.objetivo = objetivo;
        this.status = status;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public String getStatus() {
        return status;
    }
}
