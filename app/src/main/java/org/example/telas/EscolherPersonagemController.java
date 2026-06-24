package org.example.telas;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
//import javafx.scene.image.ImageView;
//import javafx.scene.image.Image;
import javafx.geometry.Pos;

public class EscolherPersonagemController {

    @FXML private HBox containerPerfis;
    @FXML private VBox overlayCriacao;
    @FXML private TextField campoNome; 
    @FXML private VBox btnAdicionar;

    private int totalPersonagens = 0;

    @FXML
    private void abrirCriacao() {
        overlayCriacao.setVisible(true);
    }

    @FXML
    private void cancelarCriacao() {
        overlayCriacao.setVisible(false);
        campoNome.clear();
    }

    @FXML
    private void confirmarCriacao() {
        String nome = campoNome.getText().trim();
        
        if (!nome.isEmpty() && totalPersonagens < 5) {
            adicionarPerfilNaTela(nome);
            totalPersonagens++;
            
            // Esconde o botão de "+" se atingir o limite de 5
            if (totalPersonagens == 5) {
                containerPerfis.getChildren().remove(btnAdicionar);
            }
        }
        overlayCriacao.setVisible(false);
        campoNome.clear();
    }

    private void adicionarPerfilNaTela(String nome) {
        // 1. Cria a VBox do personagem
        VBox perfil = new VBox();
        perfil.getStyleClass().add("container-personagem");
        perfil.setSpacing(15);
        perfil.setAlignment(Pos.CENTER);

        // 2. Cria o quadrado do Avatar e TRAVA o tamanho em 150x150 na marra
        StackPane avatarQuadrado = new StackPane();
        avatarQuadrado.getStyleClass().add("avatar-numero");
        avatarQuadrado.setMinWidth(150);
        avatarQuadrado.setMinHeight(150);
        avatarQuadrado.setMaxWidth(150);
        avatarQuadrado.setMaxHeight(150);
        avatarQuadrado.setPrefSize(150, 150);

        // 3. Cria o número gigante (Branco)
        Label lblNumero = new Label(String.valueOf(totalPersonagens + 1));
        lblNumero.getStyleClass().add("numero-perfil");
        
        // Coloca o número dentro do quadrado
        avatarQuadrado.getChildren().add(lblNumero);

        // 4. Cria o texto com o nome (Roxo)
        Label lblNome = new Label(nome);
        lblNome.getStyleClass().add("texto-perfil");

        // Une todo mundo na VBox
        perfil.getChildren().addAll(avatarQuadrado, lblNome);

        // 5. Injeta na tela antes do botão de "+"
        int posicaoBotaoMais = containerPerfis.getChildren().indexOf(btnAdicionar);
        if (posicaoBotaoMais != -1) {
            containerPerfis.getChildren().add(posicaoBotaoMais, perfil);
        } else {
            containerPerfis.getChildren().add(perfil);
        }
    }
}