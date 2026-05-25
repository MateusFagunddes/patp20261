package entity;

import java.util.Date;

public class Usuario {

    private int id;
    private String nome;
    private String email;
    private String senha;
    private String status; // enum no banco → String no Java
    private Date createdAt;
    private Date updatedAt;

public Usuario() {}

public Usuario(int id, String nome, String email, String senha, String status, Date createdAt, Date updatedAt) {
    this.id = id;
    this.nome = nome;
    this.email = email;
    this.senha = senha;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
}

public int getId() {
    return id;
}

public void setId(int id) {
    this.id = id;
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
    this.email = email;
}

public String getSenha() {
    return senha;
}

public void setSenha(String senha) {
    this.senha = senha;
}

public String getStatus() {
    return status;
}

public void setStatus(String status) {
    this.status = status;
}

public Date getCreatedAt() {
    return createdAt;
}

public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
}

public Date getUpdatedAt() {
    return updatedAt;
}

public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
}


}
