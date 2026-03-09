package drinkshop.ui;

import drinkshop.builder.DrinkShopServiceBuilder;
import drinkshop.domain.*;
import drinkshop.repository.Repository;
import drinkshop.repository.file.*;
import drinkshop.service.DrinkShopService;
import drinkshop.service.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DrinkShopApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        DrinkShopServiceBuilder builder = new DrinkShopServiceBuilder();
        DrinkShopService service = builder.build();

        // ---------- Incarcare FXML ----------

        FXMLLoader loader = new FXMLLoader(getClass().getResource("drinkshop.fxml"));
        Scene scene = new Scene(loader.load());

        // ---------- Setare Service in Controller ----------
        DrinkShopController controller = loader.getController();
        controller.setService(service);

        // ---------- Afisare Fereastra ----------
        stage.setTitle("Coffee Shop Management");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}