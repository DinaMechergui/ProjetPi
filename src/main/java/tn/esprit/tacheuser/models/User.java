package tn.esprit.tacheuser.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class User {
    private int id;
    private String nom;
    private String prenom;
    private String mail;
    private String tel;
    private String gender;
    private String password;
    private String age;
    private String confirmpassword;
    private String status = "inactive";
    private String role = "USER";

    public User() {
    }
    public User(int id, String nom, String prenom, String mail, String tel, String gender, String age, String password) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
        this.tel = tel;
        this.gender = gender;
        this.age = age;
        this.password = password;

    }


    public User(int id, String nom, String prenom, String mail, String tel, String gender, String status) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
        this.tel = tel;
        this.gender = gender;
        this.status = status;
    }

    public User(int id, String nom, String prenom, String mail, String tel, String gender, String password, String age, String confirmpassword) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
        this.tel = tel;
        this.gender = gender;
        this.password = password;
        this.age = age;
        this.confirmpassword = confirmpassword;
    }

    public User(int i, String nom, String prenom, String mail, String tel, String gender, String age, String password, String confirmPassword, String status, String role) {
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
        this.tel = tel;
        this.gender = gender;
        this.password = password;
        this.age = age;
    }

    public User(int i, String nom, String prenom, String mail, String tel, String gender, String age, String password, String status, String role) {
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
        this.tel = tel;
        this.gender = gender;
        this.password = password;
        this.age = age;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return this.prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getMail() {
        return this.mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getTel() {
        return this.tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAge() {
        return this.age;
    }

    public void setAge(String age) {
        this.age = age;
    }
    public String getConfirmpassword() {
        return this.confirmpassword;
    }

    public void setConfirmpassword(String confirmpassword) {
        this.confirmpassword = confirmpassword;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Nom: " + nom + ", Prénom: " + prenom + ", Email: " + mail +
                ", Téléphone: " + tel + ", Genre: " + gender + ", Âge: " + age + ", Statut: " + status;
    }

}