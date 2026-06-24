package org.example.telas;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class TelaInicialController {

    @FXML
    private void handleIniciar(ActionEvent event) {
        System.out.println(">>> O BOTAO FOI CLICADO COM SUCESSO! <<<");
        
        try {
            // Tentativa 1: Procura o arquivo na mesma pasta atual
            URL fxmlLocation = getClass().getResource("escolher_personagem.fxml");
            
            // Tentativa 2: Se não achar, procura a partir da raiz (bin/)
            if (fxmlLocation == null) {
                fxmlLocation = getClass().getResource("/escolher_personagem.fxml");
            }
            
            // Tentativa 3: Se ainda não achar, força o caminho pela pasta 'telas'
            if (fxmlLocation == null) {
                fxmlLocation = getClass().getResource("/telas/escolher_personagem.fxml");
            }

            // Se mesmo assim der null, avisa exatamente o que aconteceu
            if (fxmlLocation == null) {
                throw new IOException("O Java não conseguiu encontrar o arquivo 'escolher_personagem.fxml' em nenhum dos caminhos.");
            }

            // Carrega a tela usando o caminho que deu certo
            Parent proximaTela = FXMLLoader.load(fxmlLocation);
            Scene novaCena = new Scene(proximaTela);
            
            // Pega a janela atual e aplica a nova cena
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(novaCena);
            window.show();
            
        } catch (IOException e) {
            System.out.println("❌ ERRO AO MUDAR DE TELA:");
            e.printStackTrace();
        }
    }
}