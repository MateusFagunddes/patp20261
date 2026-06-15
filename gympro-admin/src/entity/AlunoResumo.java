package entity;

public class AlunoResumo {

    private final int alunoId;
    private final int usuarioId;
    private String nome;
    private final String email;
    private String telefone;
    private String objetivo;
    private String status;

    public AlunoResumo(int alunoId, int usuarioId, String nome, String email, String telefone, String objetivo, String status) {
        this.alunoId = alunoId;
        this.usuarioId = usuarioId;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.objetivo = objetivo;
        this.status = status;
    }

    public int getAlunoId() {
        return alunoId;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
