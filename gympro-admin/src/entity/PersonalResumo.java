package entity;

public class PersonalResumo {

    private final int personalId;
    private final int usuarioId;
    private String nome;
    private final String email;
    private String cref;
    private String especialidade;
    private String status;

    public PersonalResumo(int personalId, int usuarioId, String nome, String email, String cref, String especialidade, String status) {
        this.personalId = personalId;
        this.usuarioId = usuarioId;
        this.nome = nome;
        this.email = email;
        this.cref = cref;
        this.especialidade = especialidade;
        this.status = status;
    }

    public int getPersonalId() { return personalId; }
    public int getUsuarioId() { return usuarioId; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getCref() { return cref; }
    public String getEspecialidade() { return especialidade; }
    public String getStatus() { return status; }
    public void setNome(String nome) { this.nome = nome; }
    public void setCref(String cref) { this.cref = cref; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }
    public void setStatus(String status) { this.status = status; }
}
