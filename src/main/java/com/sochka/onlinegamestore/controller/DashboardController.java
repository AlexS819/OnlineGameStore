package com.sochka.onlinegamestore.controller;

import com.sochka.onlinegamestore.dto.ActivationKeyDTO;
import com.sochka.onlinegamestore.dto.GameDTO;
import com.sochka.onlinegamestore.dto.OrderDTO;
import com.sochka.onlinegamestore.dto.PublisherDTO;
import com.sochka.onlinegamestore.dto.GenreDTO;
import com.sochka.onlinegamestore.viewmodel.DashboardViewModel;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Main application dashboard controller responsible for routing visual flows,
 * configuring secure domain visibility bounds, and dispatching catalog interactions.
 */
@Component
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardViewModel viewModel;
    private final ApplicationContext springContext;
    private final com.sochka.onlinegamestore.ui.UserSession userSession;
    private final com.sochka.onlinegamestore.ui.SceneSwitcher sceneSwitcher;

    @FXML
    private TableView<GameDTO> gamesTable;
    @FXML
    private TableColumn<GameDTO, String> titleColumn;
    @FXML
    private TableColumn<GameDTO, String> publisherColumn;
    @FXML
    private TableColumn<GameDTO, java.util.Set<String>> genreColumn;
    @FXML
    private TableColumn<GameDTO, BigDecimal> priceColumn;
    @FXML
    private TableColumn<GameDTO, Integer> stockColumn;

    @FXML
    private TextField searchField;
    @FXML
    private javafx.scene.control.MenuButton publisherFilterMenu;
    @FXML
    private javafx.scene.control.MenuButton genreFilterMenu;
    @FXML
    private Button clearFiltersBtn;
    @FXML
    private Button addProductBtn;

    // Inventory handles
    @FXML
    private VBox catalogView;
    @FXML
    private VBox inventoryView;
    @FXML
    private TableView<ActivationKeyDTO> keysTable;
    @FXML
    private TableColumn<ActivationKeyDTO, UUID> keyIdColumn;
    @FXML
    private TableColumn<ActivationKeyDTO, String> keyValueColumn;
    @FXML
    private TableColumn<ActivationKeyDTO, String> keyStatusColumn;
    @FXML
    private TableColumn<ActivationKeyDTO, String> keyGameColumn;
    @FXML
    private TableColumn<ActivationKeyDTO, LocalDateTime> keySoldAtColumn;
    @FXML
    private TableColumn<ActivationKeyDTO, String> keyBuyerColumn;
    @FXML
    private Button addKeyBtn;

    // Orders / Library handles
    @FXML
    private VBox ordersView;
    @FXML
    private TableView<OrderDTO> ordersTable;
    @FXML
    private TableColumn<OrderDTO, LocalDateTime> orderDateColumn;
    @FXML
    private TableColumn<OrderDTO, String> orderEmailColumn;
    @FXML
    private TableColumn<OrderDTO, String> orderGameColumn;
    @FXML
    private TableColumn<OrderDTO, String> orderKeyColumn;
    @FXML
    private TableColumn<OrderDTO, BigDecimal> orderPriceColumn;
    @FXML
    private Button exportOrdersBtn;

    @FXML
    private Button gameCatalogBtn;
    @FXML
    private Button keyInventoryBtn;
    @FXML
    private Button customersBtn;
    @FXML
    private Button publishersBtn;
    @FXML
    private Button genresBtn;
    @FXML
    private Button deleteProfileBtn;
    @FXML
    private Button signOutBtn;

    // Publishers View
    @FXML
    private VBox publishersView;
    @FXML
    private TableView<PublisherDTO> publishersTable;
    @FXML
    private TableColumn<PublisherDTO, String> publisherNameCol;
    @FXML
    private TableColumn<PublisherDTO, String> publisherWebsiteCol;
    @FXML
    private TableColumn<PublisherDTO, String> publisherEmailCol;
    @FXML
    private Button addPublisherBtn;

    // Genres View
    @FXML
    private VBox genresView;
    @FXML
    private TableView<GenreDTO> genresTable;
    @FXML
    private TableColumn<GenreDTO, String> genreNameCol;
    @FXML
    private Button addGenreBtn;

    @FXML
    public void initialize() {
        // 1. Initialize data presentation mapping
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisherName"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genreNames"));
        genreColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(java.util.Set<String> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isEmpty()) {
                    setText("");
                } else {
                    setText(String.join(", ", item));
                }
            }
        });
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Custom visual indicator for stock availability
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("availableKeysCount"));
        stockColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(""); // Ensure visual state wipe for reuse cycles
                } else {
                    if (item > 0) {
                        setText("In Stock (" + item + ")");
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        setText("OUT OF STOCK");
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // 2. Synchronize data streams
        gamesTable.setItems(viewModel.getGameList());

        // 2.5 Configure Key Inventory columns
        keyIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        keyValueColumn.setCellValueFactory(new PropertyValueFactory<>("keyValue"));
        keyGameColumn.setCellValueFactory(new PropertyValueFactory<>("gameTitle"));
        keyBuyerColumn.setCellValueFactory(new PropertyValueFactory<>("buyerEmail"));

        // 2.5.1 Color-coded status for Key Inventory
        keyStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        keyStatusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(""); // Reset style totally during recycle
                } else {
                    setText(item);
                    if ("Available".equalsIgnoreCase(item)) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else if ("Sold".equalsIgnoreCase(item)) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: gray;");
                    }
                }
            }
        });

        // 2.5.2 Date formatted SoldAt column
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        keySoldAtColumn.setCellValueFactory(new PropertyValueFactory<>("soldAt"));
        keySoldAtColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(timeFormatter));
                }
            }
        });

        keysTable.setItems(viewModel.getKeyList());

        // 2.7 Configure Orders/Library columns
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("purchaseDate"));
        orderDateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(timeFormatter));
                }
            }
        });

        orderEmailColumn.setCellValueFactory(new PropertyValueFactory<>("userEmail"));
        orderGameColumn.setCellValueFactory(new PropertyValueFactory<>("gameTitle"));
        orderKeyColumn.setCellValueFactory(new PropertyValueFactory<>("activationKey"));
        orderPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        ordersTable.setItems(viewModel.getOrderList());

        // 2.8 Configure Publishers & Genres columns
        publisherNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        publisherWebsiteCol.setCellValueFactory(new PropertyValueFactory<>("website"));
        publisherEmailCol.setCellValueFactory(new PropertyValueFactory<>("supportEmail"));
        publishersTable.setItems(viewModel.getPublisherList());

        genreNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        genresTable.setItems(viewModel.getGenreList());

        // Hide Customer Email column if current user is not admin
        if (!userSession.isAdmin()) {
            orderEmailColumn.setVisible(false);
        }

        // 3. Configure real-time search bindings
        searchField.textProperty().bindBidirectional(viewModel.searchQueryProperty());
        searchField.textProperty().addListener((obs, oldVal, newVal) -> viewModel.performSearch());

        // Setup List Listeners to auto-populate menus whenever source lists update from
        // database
        viewModel.getPublisherList().addListener(
                (javafx.collections.ListChangeListener.Change<? extends PublisherDTO> c) -> populatePublisherMenu());
        viewModel.getGenreList().addListener(
                (javafx.collections.ListChangeListener.Change<? extends GenreDTO> c) -> populateGenreMenu());

        // Initial population
        populatePublisherMenu();
        populateGenreMenu();

        clearFiltersBtn.setOnAction(e -> {
            searchField.clear();
            viewModel.getSelectedPublisherIds().clear();
            viewModel.getSelectedGenreIds().clear();

            // Deselect visuals manually
            publisherFilterMenu.getItems().stream()
                    .filter(it -> it instanceof CheckMenuItem)
                    .forEach(it -> ((CheckMenuItem) it).setSelected(false));

            genreFilterMenu.getItems().stream()
                    .filter(it -> it instanceof CheckMenuItem)
                    .forEach(it -> ((CheckMenuItem) it).setSelected(false));

            viewModel.performSearch();
        });

        // Setup high-speed responsive Tooltip
        Tooltip resetTooltip = new Tooltip("Reset All Filters & Search");
        resetTooltip.setShowDelay(Duration.millis(100)); // Near instantaneous
        clearFiltersBtn.setTooltip(resetTooltip);

        // 4. Wire visual event triggers
        addProductBtn.setOnAction(e -> handleOpenForm(null));
        addKeyBtn.setOnAction(e -> handleKeyOpenForm(null));
        exportOrdersBtn.setOnAction(e -> handleExportAllOrders());
        deleteProfileBtn.setOnAction(e -> handleDeleteProfile());
        signOutBtn.setOnAction(e -> handleSignOut());

        addGenreBtn.setOnAction(e -> handleGenreDialog(null));
        addPublisherBtn.setOnAction(e -> handlePublisherDialog(null));

        gameCatalogBtn.setOnAction(e -> showCatalogView());
        keyInventoryBtn.setOnAction(e -> showInventoryView());
        customersBtn.setOnAction(e -> showOrdersView());
        publishersBtn.setOnAction(e -> showPublishersView());
        genresBtn.setOnAction(e -> showGenresView());

        // 5. Apply rigorous Role-Based privilege isolation
        enforcePermissions();

        // 6. Setup right-click Context Menu for full CRUD actions
        setupRowFactory();
        setupKeyRowFactory();
        setupOrderRowFactory();
        setupPublisherRowFactory();
        setupGenreRowFactory();

        // 7. Run initial catalog population
        viewModel.loadAllGames();
        viewModel.loadAllKeys();
        viewModel.loadPublishers();
        viewModel.loadGenres();
        viewModel.loadOrders(userSession.getCurrentUser().getId(), userSession.isAdmin());
    }

    private void hideAllViews() {
        catalogView.setVisible(false);
        catalogView.setManaged(false);
        inventoryView.setVisible(false);
        inventoryView.setManaged(false);
        ordersView.setVisible(false);
        ordersView.setManaged(false);
        publishersView.setVisible(false);
        publishersView.setManaged(false);
        genresView.setVisible(false);
        genresView.setManaged(false);
    }

    private void showCatalogView() {
        hideAllViews();
        catalogView.setVisible(true);
        catalogView.setManaged(true);
    }

    private void showInventoryView() {
        hideAllViews();
        inventoryView.setVisible(true);
        inventoryView.setManaged(true);
    }

    private void showOrdersView() {
        hideAllViews();
        ordersView.setVisible(true);
        ordersView.setManaged(true);
        viewModel.loadOrders(userSession.getCurrentUser().getId(), userSession.isAdmin());
    }

    private void showPublishersView() {
        hideAllViews();
        publishersView.setVisible(true);
        publishersView.setManaged(true);
    }

    private void showGenresView() {
        hideAllViews();
        genresView.setVisible(true);
        genresView.setManaged(true);
    }

    /**
     * Sets visual control availability based on authenticated user role.
     */
    private void enforcePermissions() {
        boolean isPrivileged = userSession.isAdmin();

        // Hide management controls for regular users
        addProductBtn.setVisible(isPrivileged);
        addProductBtn.setManaged(isPrivileged);

        // Advanced admin panels restricted
        keyInventoryBtn.setVisible(isPrivileged);
        keyInventoryBtn.setManaged(isPrivileged);
        publishersBtn.setVisible(isPrivileged);
        publishersBtn.setManaged(isPrivileged);
        genresBtn.setVisible(isPrivileged);
        genresBtn.setManaged(isPrivileged);

        // Profile deletion restricted to standard users
        deleteProfileBtn.setVisible(!isPrivileged);
        deleteProfileBtn.setManaged(!isPrivileged);
    }

    private void handleSignOut() {
        userSession.clear();
        Stage stage = (Stage) signOutBtn.getScene().getWindow();
        sceneSwitcher.switchScene(stage, "/views/login.fxml", "Game Store - Authorization", 800,
                600);
    }

    private void handleDeleteProfile() {
        // 1. Create visual authentication buffer barrier
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Confirm Identity");
        dialog.setHeaderText(
                "Action requires identity verification.\nPlease supply current password to authenticate deletion.");

        ButtonType executeBtn = new ButtonType("💣 Permanently Delete Account", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(executeBtn, ButtonType.CANCEL);

        PasswordField field = new PasswordField();
        field.setPromptText("Security credential...");
        VBox content = new VBox(new Label("Verify Password:"), field);
        content.setSpacing(10.0);
        dialog.getDialogPane().setContent(content);

        // Link result directly to typed token
        dialog.setResultConverter(b -> b == executeBtn ? field.getText() : null);

        java.util.Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            String passwordStr = result.get();
            try {
                viewModel.deleteCurrentUser(userSession.getCurrentUser().getId(), passwordStr);

                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Account Action");
                info.setHeaderText("Account successfully deleted.");
                info.setContentText("Your profile has been permanently removed.");
                info.showAndWait();

                handleSignOut(); 
            } catch (Exception e) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Action Error");
                error.setHeaderText("Request rejected.");
                error.setContentText("Action aborted: validation failed (" + e.getMessage() + ")");
                error.showAndWait();
            }
        }
    }

    private void setupRowFactory() {
        gamesTable.setRowFactory(tv -> {
            TableRow<GameDTO> row = new TableRow<>();

            // Double click to buy
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY
                        && event.getClickCount() == 2) {
                    handleBuyGame(row.getItem());
                }
            });

            ContextMenu menu = new ContextMenu();
            MenuItem buyItem = new MenuItem("🛒 Buy Game");
            buyItem.setOnAction(e -> {
                GameDTO selectedItem = row.getItem();
                if (selectedItem != null) {
                    handleBuyGame(selectedItem);
                }
            });
            menu.getItems().add(buyItem);

            // Authorization Safeguard: Only construct modifying context menu if user is
            // privileged
            if (userSession.isAdmin()) {
                menu.getItems().add(new SeparatorMenuItem());

                MenuItem editItem = new MenuItem("🖊 Edit Product Details");
                MenuItem deleteItem = new MenuItem("🗑 Delete This Title");

                editItem.setOnAction(e -> {
                    GameDTO selectedItem = row.getItem();
                    if (selectedItem != null) {
                        handleOpenForm(selectedItem);
                    }
                });

                deleteItem.setOnAction(e -> {
                    GameDTO selectedItem = row.getItem();
                    if (selectedItem != null) {
                        handleDeletion(selectedItem);
                    }
                });

                menu.getItems().addAll(editItem, deleteItem);
            }

            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(menu));

            return row;
        });
    }

    private void handleBuyGame(GameDTO game) {
        if (game.getAvailableKeysCount() <= 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Out of Stock");
            alert.setHeaderText("Purchase Impossible");
            alert.setContentText(
                    "Sorry! There are currently no keys available for '" + game.getTitle() + "'.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Purchase");
        alert.setHeaderText("Acquiring '" + game.getTitle() + "'");
        alert.setContentText(
                "Are you sure you want to purchase this game for $" + game.getPrice() + "?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                OrderDTO purchaseOrder = viewModel.buyGame(userSession.getCurrentUser().getId(), game.getId());
                String key = purchaseOrder.getActivationKey();

                viewModel.loadOrders(userSession.getCurrentUser().getId(), userSession.isAdmin()); // update library

                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Purchase Finalized!");
                success.setHeaderText("Operation Successful!");
                success.setContentText("Code provisioned securely. Click 'Copy Key' or download official receipt.");

                TextField keyDisplay = new TextField(key);
                keyDisplay.setEditable(false);
                keyDisplay.setStyle("-fx-font-family: monospace; -fx-font-weight: bold;");
                success.getDialogPane().setExpandableContent(keyDisplay);
                success.getDialogPane().setExpanded(true);

                ButtonType copyBtn = new ButtonType("📋 Copy Key");
                ButtonType exportBtn = new ButtonType("📄 Export Receipt");
                success.getButtonTypes().setAll(copyBtn, exportBtn, ButtonType.OK);

                java.util.Optional<ButtonType> result = success.showAndWait();

                if (result.isPresent()) {
                    if (result.get() == copyBtn) {
                        Clipboard clipboard = Clipboard.getSystemClipboard();
                        ClipboardContent content = new ClipboardContent();
                        content.putString(key);
                        clipboard.setContent(content);
                    } else if (result.get() == exportBtn) {
                        handleExportSingleReceipt(purchaseOrder);
                    }
                }
            } catch (Exception e) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Transaction Failed");
                error.setHeaderText("Order could not be processed.");
                error.setContentText(e.getMessage());
                error.showAndWait();
            }
        }
    }

    private void setupKeyRowFactory() {
        keysTable.setRowFactory(tv -> {
            TableRow<ActivationKeyDTO> row = new TableRow<>();

            if (userSession.isAdmin()) {
                ContextMenu menu = new ContextMenu();

                MenuItem editItem = new MenuItem("🖊 Edit Key");
                MenuItem deleteItem = new MenuItem("🗑 Delete This Key");

                editItem.setOnAction(e -> {
                    ActivationKeyDTO selectedItem = row.getItem();
                    if (selectedItem != null) {
                        handleKeyOpenForm(selectedItem);
                    }
                });

                deleteItem.setOnAction(e -> {
                    ActivationKeyDTO selectedItem = row.getItem();
                    if (selectedItem != null) {
                        handleKeyDeletion(selectedItem);
                    }
                });

                menu.getItems().addAll(editItem, deleteItem);

                row.contextMenuProperty().bind(
                        javafx.beans.binding.Bindings.when(row.emptyProperty())
                                .then((ContextMenu) null)
                                .otherwise(menu));
            }

            return row;
        });
    }

    private void setupOrderRowFactory() {
        ordersTable.setRowFactory(tv -> {
            TableRow<OrderDTO> row = new TableRow<>();

            ContextMenu menu = new ContextMenu();
            MenuItem copyKeyItem = new MenuItem("📋 Copy Activation Key");

            copyKeyItem.setOnAction(e -> {
                OrderDTO selectedItem = row.getItem();
                if (selectedItem != null && selectedItem.getActivationKey() != null) {
                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    ClipboardContent content = new ClipboardContent();
                    content.putString(selectedItem.getActivationKey());
                    clipboard.setContent(content);
                }
            });

            menu.getItems().add(copyKeyItem);

            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(menu));

            return row;
        });
    }

    private void handleDeletion(GameDTO game) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Catalog Deletion");
        alert.setHeaderText("Permanently discard product?");
        alert.setContentText("Action will remove '" + game.getTitle() + "' permanently.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            viewModel.deleteSelectedGame(game);
        }
    }

    private void handleKeyDeletion(ActivationKeyDTO key) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Key Deletion");
        alert.setHeaderText("Permanently discard activation key?");
        alert.setContentText("Action will remove '" + key.getKeyValue() + "' permanently.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            viewModel.deleteSelectedKey(key);
        }
    }

    private void handleOpenForm(GameDTO existingGame) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/game_form.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            GameFormController controller = loader.getController();

            // If editing, hydrate fields before display
            if (existingGame != null) {
                controller.loadForEdit(existingGame);
            }

            Stage dialogStage = new Stage();
            dialogStage.setTitle(existingGame == null ? "Add New Game" : "Edit Game Specification");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(gamesTable.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            // Post-close reaction trigger
            if (controller.isSaveConfirmed()) {
                viewModel.loadAllGames();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleKeyOpenForm(ActivationKeyDTO existingKey) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/key_form.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            KeyFormController controller = loader.getController();

            if (existingKey != null) {
                controller.loadForEdit(existingKey);
            }

            Stage dialogStage = new Stage();
            dialogStage.setTitle(existingKey == null ? "Stock New Key" : "Edit Key Details");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(keysTable.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            if (controller.isSaveConfirmed()) {
                viewModel.loadAllKeys();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupPublisherRowFactory() {
        publishersTable.setRowFactory(tv -> {
            TableRow<PublisherDTO> row = new TableRow<>();
            if (userSession.isAdmin()) {
                ContextMenu menu = new ContextMenu();
                MenuItem editItem = new MenuItem("🖊 Edit Publisher");
                MenuItem deleteItem = new MenuItem("🗑 Delete Publisher");
                editItem.setOnAction(e -> handlePublisherDialog(row.getItem()));
                deleteItem.setOnAction(e -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                            "Delete publisher '" + row.getItem().getName() + "'?", ButtonType.YES, ButtonType.CANCEL);
                    if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.YES) {
                        viewModel.deleteSelectedPublisher(row.getItem());
                    }
                });
                menu.getItems().addAll(editItem, deleteItem);
                row.contextMenuProperty().bind(javafx.beans.binding.Bindings.when(row.emptyProperty())
                        .then((ContextMenu) null).otherwise(menu));
            }
            return row;
        });
    }

    private void setupGenreRowFactory() {
        genresTable.setRowFactory(tv -> {
            TableRow<GenreDTO> row = new TableRow<>();
            if (userSession.isAdmin()) {
                ContextMenu menu = new ContextMenu();
                MenuItem editItem = new MenuItem("🖊 Edit Genre");
                MenuItem deleteItem = new MenuItem("🗑 Delete Genre");
                editItem.setOnAction(e -> handleGenreDialog(row.getItem()));
                deleteItem.setOnAction(e -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                            "Delete genre '" + row.getItem().getName() + "'?", ButtonType.YES, ButtonType.CANCEL);
                    if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.YES) {
                        viewModel.deleteSelectedGenre(row.getItem());
                    }
                });
                menu.getItems().addAll(editItem, deleteItem);
                row.contextMenuProperty().bind(javafx.beans.binding.Bindings.when(row.emptyProperty())
                        .then((ContextMenu) null).otherwise(menu));
            }
            return row;
        });
    }

    private void handleGenreDialog(GenreDTO genre) {
        TextInputDialog dialog = new TextInputDialog(genre != null ? genre.getName() : "");
        dialog.setTitle(genre != null ? "Edit Genre" : "New Genre");
        dialog.setHeaderText("Enter genre name:");
        java.util.Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                if (genre != null) {
                    viewModel.updateGenre(genre.getId(), name.trim());
                } else {
                    viewModel.addGenre(name.trim());
                }
            }
        });
    }

    private void handlePublisherDialog(PublisherDTO publisher) {
        Dialog<PublisherDTO> dialog = new Dialog<>();
        dialog.setTitle(publisher != null ? "Edit Publisher" : "New Publisher");

        ButtonType saveBtnType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtnType, ButtonType.CANCEL);

        TextField nameField = new TextField(publisher != null ? publisher.getName() : "");
        nameField.setPromptText("Company Name");
        TextField webField = new TextField(
                publisher != null && publisher.getWebsite() != null ? publisher.getWebsite() : "");
        webField.setPromptText("Website URL");
        TextField emailField = new TextField(
                publisher != null && publisher.getSupportEmail() != null ? publisher.getSupportEmail() : "");
        emailField.setPromptText("Support Email");

        VBox content = new VBox(10, new Label("Name:"), nameField, new Label("Website:"), webField,
                new Label("Support Email:"), emailField);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtnType) {
                return PublisherDTO.builder()
                        .name(nameField.getText())
                        .website(webField.getText())
                        .supportEmail(emailField.getText())
                        .build();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(dto -> {
            if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
                if (publisher != null) {
                    viewModel.updatePublisher(publisher.getId(), dto.getName(), dto.getWebsite(),
                            dto.getSupportEmail());
                } else {
                    viewModel.addPublisher(dto.getName(), dto.getWebsite(), dto.getSupportEmail());
                }
            }
        });
    }

    private void populatePublisherMenu() {
        publisherFilterMenu.getItems().clear();
        for (PublisherDTO pub : viewModel.getPublisherList()) {
            CheckMenuItem item = new CheckMenuItem(pub.getName());
            item.selectedProperty().addListener((obs, o, isNowSelected) -> {
                if (isNowSelected) {
                    viewModel.getSelectedPublisherIds().add(pub.getId());
                } else {
                    viewModel.getSelectedPublisherIds().remove(pub.getId());
                }
                viewModel.performSearch();
            });
            publisherFilterMenu.getItems().add(item);
        }
    }

    private void populateGenreMenu() {
        genreFilterMenu.getItems().clear();
        for (GenreDTO gen : viewModel.getGenreList()) {
            CheckMenuItem item = new CheckMenuItem(gen.getName());
            item.selectedProperty().addListener((obs, o, isNowSelected) -> {
                if (isNowSelected) {
                    viewModel.getSelectedGenreIds().add(gen.getId());
                } else {
                    viewModel.getSelectedGenreIds().remove(gen.getId());
                }
                viewModel.performSearch();
            });
            genreFilterMenu.getItems().add(item);
        }
    }

    private void handleExportSingleReceipt(OrderDTO order) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Store Official Receipt");
        fc.setInitialFileName("Receipt_" + order.getGameTitle().replaceAll("[^a-zA-Z0-9]", "_") + ".xlsx");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Workspace (*.xlsx)", "*.xlsx"));
        
        Stage current = (Stage) exportOrdersBtn.getScene().getWindow();
        File file = fc.showSaveDialog(current);
        
        if (file != null) {
            // Decoupled operational execution utilizing concurrency model as defined by directives
            javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<>() {
                @Override protected Void call() throws Exception {
                    viewModel.exportReceipt(order, file);
                    return null;
                }
            };
            task.setOnSucceeded(e -> {
                Alert succ = new Alert(Alert.AlertType.INFORMATION, "Receipt serialization finalized successfully.", ButtonType.OK);
                succ.show();
            });
            task.setOnFailed(e -> {
                Alert err = new Alert(Alert.AlertType.ERROR, "Critical Failure exporting document: " + task.getException().getMessage(), ButtonType.OK);
                err.show();
            });
            new Thread(task).start();
        }
    }

    private void handleExportAllOrders() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Export Total Transaction Ledger");
        fc.setInitialFileName("GameStore_Operational_Ledger.xlsx");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Workspace (*.xlsx)", "*.xlsx"));
        
        Stage current = (Stage) exportOrdersBtn.getScene().getWindow();
        File file = fc.showSaveDialog(current);
        
        if (file != null) {
            javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<>() {
                @Override protected Void call() throws Exception {
                    viewModel.exportActiveOrderLog(file);
                    return null;
                }
            };
            task.setOnSucceeded(e -> {
                Alert succ = new Alert(Alert.AlertType.INFORMATION, "Grid data aggregation and writing complete.", ButtonType.OK);
                succ.show();
            });
            task.setOnFailed(e -> {
                Alert err = new Alert(Alert.AlertType.ERROR, "Aggregate write failure: " + task.getException().getMessage(), ButtonType.OK);
                err.show();
            });
            new Thread(task).start();
        }
    }
}
