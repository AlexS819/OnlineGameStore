package com.sochka.onlinegamestore.controller;

import com.sochka.onlinegamestore.dto.ActivationKeyDTO;
import com.sochka.onlinegamestore.dto.GameDTO;
import com.sochka.onlinegamestore.dto.GenreDTO;
import com.sochka.onlinegamestore.dto.OrderDTO;
import com.sochka.onlinegamestore.dto.PublisherDTO;
import com.sochka.onlinegamestore.viewmodel.DashboardViewModel;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Main application dashboard controller responsible for routing visual flows,
 * configuring secure
 * domain visibility bounds, and dispatching catalog interactions.
 */
@Component
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardViewModel viewModel;
    private final ApplicationContext springContext;
    private final com.sochka.onlinegamestore.ui.UserSession userSession;
    private final com.sochka.onlinegamestore.ui.SceneSwitcher sceneSwitcher;
    private final com.sochka.onlinegamestore.service.UserService userService;
    private final com.sochka.onlinegamestore.infrastructure.LiqPayService liqPayService;

    @FXML
    private FlowPane gamesFlowPane;
    @FXML
    private StackPane carouselContainer;

    private Timeline carouselTimeline;
    private int currentCarouselIndex = 0;

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

    // Profile & Settings handles
    @FXML
    private Button profileBtn;
    @FXML
    private VBox profileView;
    @FXML
    private Label profileNameLabel;
    @FXML
    private Label profileEmailLabel;
    @FXML
    private Label profileRoleLabel;
    @FXML
    private Label profileBalanceLabel;
    @FXML
    private CheckBox twoFactorCheckbox;
    @FXML
    private Button topUpBalanceBtn;
    @FXML
    private Separator profileBalanceSeparator;
    @FXML
    private Label profileBalanceHeader;
    @FXML
    private Label securitySettingsHeader;
    @FXML
    private Label twoFactorDescLabel;

    @FXML
    public void initialize() {
        // 1. Initialize data presentation mapping
        viewModel.getGameList()
                .addListener((javafx.collections.ListChangeListener.Change<? extends GameDTO> c) -> {
                    updateGameGrid();
                    updateCarousel();
                });

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
        profileBtn.setOnAction(e -> showProfileView());
        topUpBalanceBtn.setOnAction(e -> handleTopUpBalance());

        twoFactorCheckbox.setOnAction(e -> {
            try {
                boolean selected = twoFactorCheckbox.isSelected();
                userService.toggleTwoFactor(userSession.getCurrentUser().getId(), selected);
                userSession.getCurrentUser().setTwoFactorEnabled(selected);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Two-Factor Authentication");
                alert.setHeaderText(null);
                alert.setContentText(
                        "2FA has been successfully " + (selected ? "enabled" : "disabled") + ".");
                alert.showAndWait();
            } catch (Exception ex) {
                twoFactorCheckbox.setSelected(!twoFactorCheckbox.isSelected());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Action Failed");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        });

        // 5. Apply rigorous Role-Based privilege isolation
        enforcePermissions();

        // 6. Setup right-click Context Menu for full CRUD actions
        setupKeyRowFactory();
        setupOrderRowFactory();
        setupPublisherRowFactory();
        setupGenreRowFactory();

        // 7. Run initial catalog population
        viewModel.loadAllGames();
        updateGameGrid();
        updateCarousel();
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
        profileView.setVisible(false);
        profileView.setManaged(false);
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

    private void showProfileView() {
        hideAllViews();

        // Refresh account properties from active session state
        var user = userSession.getCurrentUser();
        profileNameLabel.setText(user.getName());
        profileEmailLabel.setText(user.getEmail());
        profileRoleLabel.setText(user.getRole());

        if (userSession.isAdmin()) {
            profileBalanceLabel.setText("∞ (Unlimited)");
        } else if (user.getBalance() != null) {
            profileBalanceLabel.setText(String.format("$%.2f", user.getBalance()));
        } else {
            profileBalanceLabel.setText("$0.00");
        }

        twoFactorCheckbox.setSelected(user.isTwoFactorEnabled());

        profileView.setVisible(true);
        profileView.setManaged(true);
    }

    private void handleTopUpBalance() {
        // 1. Prompt for top-up amount
        TextInputDialog amountDialog = new TextInputDialog("10.00");
        amountDialog.setTitle("Wallet Top Up");
        amountDialog.setHeaderText("Add Funds to Store Balance");
        amountDialog.setContentText("Enter top-up amount in USD (minimum $1.00):");

        java.util.Optional<String> amountResult = amountDialog.showAndWait();
        if (amountResult.isPresent()) {
            String inputStr = amountResult.get().trim();
            BigDecimal amount;
            try {
                amount = new BigDecimal(inputStr);
                if (amount.compareTo(BigDecimal.ONE) < 0) {
                    showError("Invalid Amount", "The minimum top-up amount is $1.00.");
                    return;
                }
            } catch (NumberFormatException e) {
                showError("Invalid Amount", "Please enter a valid numeric value.");
                return;
            }

            // 2. Generate unique order ID
            String orderId = "TOPUP-" + UUID.randomUUID().toString().substring(0, 8) + "-"
                    + System.currentTimeMillis();

            try {
                // 3. Generate HTML content
                String htmlContent = liqPayService.generateCheckoutHtml(
                        orderId,
                        amount,
                        userSession.getCurrentUser().getEmail());

                // 4. Write HTML to temporary file inside the workspace
                File tempDir = new File("target/liqpay");
                if (!tempDir.exists()) {
                    tempDir.mkdirs();
                }
                File tempFile = new File(tempDir, "checkout-" + orderId + ".html");
                try (java.io.FileWriter writer = new java.io.FileWriter(tempFile)) {
                    writer.write(htmlContent);
                }

                // 5. Open local HTML file in default system browser
                if (java.awt.Desktop.isDesktopSupported() && java.awt.Desktop.getDesktop()
                        .isSupported(java.awt.Desktop.Action.BROWSE)) {
                    java.awt.Desktop.getDesktop().browse(tempFile.toURI());
                } else {
                    // Fallback
                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler "
                            + tempFile.getAbsolutePath());
                }

                // 6. Show "Awaiting Payment" verification dialog in JavaFX
                Alert awaitingAlert = new Alert(Alert.AlertType.CONFIRMATION);
                awaitingAlert.setTitle("LiqPay Checkout");
                awaitingAlert.setHeaderText("Awaiting Sandbox Payment Completion");
                awaitingAlert.setContentText(
                        "We have opened the LiqPay Sandbox page in your web browser.\n\n" +
                                "1. Complete the test transaction in your browser (use any test card details).\n"
                                +
                                "2. Once done, return here and click 'Confirm Payment' to verify and credit your wallet.");

                ButtonType confirmBtnType = new ButtonType("Confirm Payment",
                        ButtonBar.ButtonData.OK_DONE);
                ButtonType cancelBtnType = new ButtonType("Cancel Top Up",
                        ButtonBar.ButtonData.CANCEL_CLOSE);
                awaitingAlert.getButtonTypes().setAll(confirmBtnType, cancelBtnType);

                java.util.Optional<ButtonType> alertResult = awaitingAlert.showAndWait();
                if (alertResult.isPresent() && alertResult.get() == confirmBtnType) {
                    // Call LiqPay Status API
                    boolean verified = liqPayService.verifyPaymentStatus(orderId);
                    if (verified) {
                        // Credit wallet balance
                        userService.topUpBalance(userSession.getCurrentUser().getId(), amount);
                        // Update local session
                        userSession.getCurrentUser()
                                .setBalance(userSession.getCurrentUser().getBalance().add(amount));
                        // Refresh Profile view
                        showProfileView();

                        Alert success = new Alert(Alert.AlertType.INFORMATION);
                        success.setTitle("Top Up Successful!");
                        success.setHeaderText("Wallet Credited!");
                        success.setContentText("Transaction verified successfully. $" + amount
                                + " has been added to your balance.\n" +
                                "A receipt has been dispatched to your email address!");
                        success.showAndWait();
                    } else {
                        showError("Verification Failed",
                                "LiqPay reported that the transaction is not completed or has failed.\n\n"
                                        +
                                        "Please make sure you finished the payment in the browser before confirming.");
                    }
                }

                // Clean up temporary HTML file safely after transaction
                tempFile.deleteOnExit();

            } catch (Exception ex) {
                showError("Top Up Error",
                        "Failed to initiate payment sequence: " + ex.getMessage());
            }
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
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

        if (isPrivileged) {
            twoFactorCheckbox.setVisible(false);
            twoFactorCheckbox.setManaged(false);
            twoFactorDescLabel.setVisible(false);
            twoFactorDescLabel.setManaged(false);
            securitySettingsHeader.setVisible(false);
            securitySettingsHeader.setManaged(false);

            topUpBalanceBtn.setVisible(false);
            topUpBalanceBtn.setManaged(false);
            profileBalanceSeparator.setVisible(false);
            profileBalanceSeparator.setManaged(false);
            profileBalanceHeader.setVisible(false);
            profileBalanceHeader.setManaged(false);
        }
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

        ButtonType executeBtn = new ButtonType("Permanently Delete Account",
                ButtonBar.ButtonData.OK_DONE);
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

    private void updateGameGrid() {
        gamesFlowPane.getChildren().clear();
        for (GameDTO game : viewModel.getGameList()) {
            gamesFlowPane.getChildren().add(createGameCard(game));
        }
    }

    private void handleShowGameDetails(GameDTO game) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/game_details.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            GameDetailsController controller = loader.getController();
            controller.setGame(game, () -> handleBuyGame(game));

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Game Details - " + game.getTitle());
            dialogStage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/icon.png")));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(gamesFlowPane.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Node createGameCard(GameDTO game) {
        VBox card = new VBox(8);
        card.getStyleClass().add("game-card");
        card.setPrefWidth(170);
        card.setPrefHeight(320);
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle("-fx-cursor: hand;");

        ImageView coverView = new ImageView();
        coverView.setFitWidth(150);
        coverView.setFitHeight(225);
        coverView.setPreserveRatio(false);
        coverView.setStyle("-fx-background-radius: 6px;");

        String imgUrl = game.getImageUrl();
        if (imgUrl != null && !imgUrl.trim().isEmpty()) {
            try {
                coverView.setImage(com.sochka.onlinegamestore.utils.ImageCache.getCachedImage(imgUrl));
            } catch (Exception ex) {
                coverView.setImage(com.sochka.onlinegamestore.utils.ImageCache.getCachedImage("https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=400&q=80"));
            }
        } else {
            coverView.setImage(com.sochka.onlinegamestore.utils.ImageCache.getCachedImage("https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=400&q=80"));
        }

        VBox textContainer = new VBox(4);
        textContainer.setPadding(new Insets(2, 8, 8, 8));
        textContainer.setAlignment(Pos.TOP_LEFT);

        Label title = new Label(game.getTitle());
        title.getStyleClass().add("game-card-title");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label price = new Label("$" + game.getPrice());
        price.getStyleClass().add("game-card-price");

        Label stock = new Label();
        stock.getStyleClass().add("game-card-stock");
        if (game.getAvailableKeysCount() > 0) {
            stock.setText("In Stock (" + game.getAvailableKeysCount() + ")");
            stock.getStyleClass().add("game-card-stock-in");
        } else {
            stock.setText("OUT OF STOCK");
            stock.getStyleClass().add("game-card-stock-out");
        }

        textContainer.getChildren().addAll(title, price, stock);
        card.getChildren().addAll(coverView, textContainer);

        // Single click to view details
        card.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                handleShowGameDetails(game);
            }
        });

        card.setOnMouseEntered(event -> {
            javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(150),
                    card);
            st.setToX(1.03);
            st.setToY(1.03);
            st.play();
        });
        card.setOnMouseExited(event -> {
            javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(150),
                    card);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });

        // Context Menu
        ContextMenu menu = new ContextMenu();
        MenuItem detailsItem = new MenuItem("View Details");
        detailsItem.setOnAction(e -> handleShowGameDetails(game));
        MenuItem buyItem = new MenuItem("Buy Game");
        buyItem.setOnAction(e -> handleBuyGame(game));
        menu.getItems().addAll(detailsItem, buyItem);

        if (userSession.isAdmin()) {
            menu.getItems().add(new SeparatorMenuItem());
            MenuItem editItem = new MenuItem(
                    "Edit Product Details");
            MenuItem deleteItem = new MenuItem("Delete This Title");
            editItem.setOnAction(e -> handleOpenForm(game));
            deleteItem.setOnAction(e -> handleDeletion(game));
            menu.getItems().addAll(editItem, deleteItem);
        }

        card.setOnContextMenuRequested(event -> {
            menu.show(card, event.getScreenX(), event.getScreenY());
        });

        return card;
    }

    private void updateCarousel() {
        carouselContainer.getChildren().clear();
        if (carouselTimeline != null) {
            carouselTimeline.stop();
        }

        java.util.List<GameDTO> featuredGames = new java.util.ArrayList<>();
        for (int i = 0; i < viewModel.getGameList().size(); i++) {
            featuredGames.add(viewModel.getGameList().get(i));
        }

        if (featuredGames.isEmpty()) {
            carouselContainer.setVisible(false);
            carouselContainer.setManaged(false);
            return;
        }

        carouselContainer.setVisible(true);
        carouselContainer.setManaged(true);

        currentCarouselIndex = 0;
        showCarouselSlide(featuredGames);

        carouselTimeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            currentCarouselIndex = (currentCarouselIndex + 1) % featuredGames.size();
            showCarouselSlide(featuredGames);
        }));
        carouselTimeline.setCycleCount(Timeline.INDEFINITE);
        carouselTimeline.play();
    }

    private void showCarouselSlide(java.util.List<GameDTO> featuredGames) {
        carouselContainer.getChildren().clear();
        if (featuredGames.isEmpty()) {
            return;
        }

        GameDTO game = featuredGames.get(currentCarouselIndex);

        BorderPane slide = new BorderPane();
        slide.getStyleClass().add("carousel-banner");
        slide.setStyle(
                "-fx-padding: 30; -fx-background-color: linear-gradient(to right, #1877F2, #00C6FF); -fx-background-radius: 12px;");

        VBox info = new VBox(15);
        info.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label(game.getTitle());
        title.getStyleClass().add("carousel-title");

        Label publisher = new Label("By " + game.getPublisherName());
        publisher.setStyle("-fx-text-fill: rgba(255,255,255,0.8); -fx-font-size: 16px;");

        Label price = new Label("$" + game.getPrice());
        price.getStyleClass().add("carousel-price");

        Button detailsBtn = new Button("View Details");
        detailsBtn.getStyleClass().add("carousel-btn");
        detailsBtn.setOnAction(e -> handleShowGameDetails(game));

        info.getChildren().addAll(title, publisher, price, detailsBtn);

        HBox controls = new HBox(10);
        controls.setAlignment(Pos.BOTTOM_RIGHT);
        Button prevBtn = new Button("<");
        prevBtn.getStyleClass().add("carousel-btn");
        prevBtn.setOnAction(e -> {
            currentCarouselIndex = (currentCarouselIndex - 1 + featuredGames.size()) % featuredGames.size();
            showCarouselSlide(featuredGames);
            if (carouselTimeline != null) {
                carouselTimeline.playFromStart();
            }
        });

        Button nextBtn = new Button(">");
        nextBtn.getStyleClass().add("carousel-btn");
        nextBtn.setOnAction(e -> {
            currentCarouselIndex = (currentCarouselIndex + 1) % featuredGames.size();
            showCarouselSlide(featuredGames);
            if (carouselTimeline != null) {
                carouselTimeline.playFromStart();
            }
        });

        controls.getChildren().addAll(prevBtn, nextBtn);

        slide.setLeft(info);
        slide.setRight(controls);
        BorderPane.setAlignment(controls, Pos.BOTTOM_RIGHT);

        carouselContainer.getChildren().add(slide);
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
                OrderDTO purchaseOrder = viewModel.buyGame(userSession.getCurrentUser().getId(),
                        game.getId());

                // Update local session balance dynamically
                if (!userSession.isAdmin() && userSession.getCurrentUser().getBalance() != null) {
                    userSession.getCurrentUser().setBalance(
                            userSession.getCurrentUser().getBalance().subtract(game.getPrice()));
                }

                String key = purchaseOrder.getActivationKey();

                viewModel.loadOrders(userSession.getCurrentUser().getId(),
                        userSession.isAdmin()); // update library
                viewModel.loadAllGames();
                viewModel.loadAllKeys();

                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Purchase Finalized!");
                success.setHeaderText("Operation Successful!");
                success.setContentText(
                        "Code provisioned securely. Click 'Copy Key' or download official receipt.");

                TextField keyDisplay = new TextField(key);
                keyDisplay.setEditable(false);
                keyDisplay.setStyle("-fx-font-family: monospace; -fx-font-weight: bold;");
                success.getDialogPane().setExpandableContent(keyDisplay);
                success.getDialogPane().setExpanded(true);

                ButtonType copyBtn = new ButtonType("Copy Key");
                ButtonType exportBtn = new ButtonType("Export Receipt");
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

                MenuItem editItem = new MenuItem("Edit Key");
                MenuItem deleteItem = new MenuItem("Delete This Key");

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
            MenuItem copyKeyItem = new MenuItem("Copy Activation Key");

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
            viewModel.loadAllGames();
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
            dialogStage.initOwner(gamesFlowPane.getScene().getWindow());
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
                viewModel.loadAllGames();
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
                MenuItem editItem = new MenuItem("Edit Publisher");
                MenuItem deleteItem = new MenuItem("Delete Publisher");
                editItem.setOnAction(e -> handlePublisherDialog(row.getItem()));
                deleteItem.setOnAction(e -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                            "Delete publisher '" + row.getItem().getName() + "'?", ButtonType.YES,
                            ButtonType.CANCEL);
                    if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.YES) {
                        viewModel.deleteSelectedPublisher(row.getItem());
                    }
                });
                menu.getItems().addAll(editItem, deleteItem);
                row.contextMenuProperty()
                        .bind(javafx.beans.binding.Bindings.when(row.emptyProperty())
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
                MenuItem editItem = new MenuItem("Edit Genre");
                MenuItem deleteItem = new MenuItem("Delete Genre");
                editItem.setOnAction(e -> handleGenreDialog(row.getItem()));
                deleteItem.setOnAction(e -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                            "Delete genre '" + row.getItem().getName() + "'?", ButtonType.YES,
                            ButtonType.CANCEL);
                    if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.YES) {
                        viewModel.deleteSelectedGenre(row.getItem());
                    }
                });
                menu.getItems().addAll(editItem, deleteItem);
                row.contextMenuProperty()
                        .bind(javafx.beans.binding.Bindings.when(row.emptyProperty())
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
                publisher != null && publisher.getSupportEmail() != null ? publisher.getSupportEmail()
                        : "");
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
        fc.setInitialFileName(
                "Receipt_" + order.getGameTitle().replaceAll("[^a-zA-Z0-9]", "_") + ".xlsx");
        fc.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Excel Workspace (*.xlsx)", "*.xlsx"));

        Stage current = (Stage) exportOrdersBtn.getScene().getWindow();
        File file = fc.showSaveDialog(current);

        if (file != null) {
            // Decoupled operational execution utilizing concurrency model as defined by
            // directives
            javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<>() {
                @Override
                protected Void call() throws Exception {
                    viewModel.exportReceipt(order, file);
                    return null;
                }
            };
            task.setOnSucceeded(e -> {
                Alert succ = new Alert(Alert.AlertType.INFORMATION,
                        "Receipt serialization finalized successfully.", ButtonType.OK);
                succ.show();
            });
            task.setOnFailed(e -> {
                Alert err = new Alert(Alert.AlertType.ERROR,
                        "Critical Failure exporting document: " + task.getException().getMessage(),
                        ButtonType.OK);
                err.show();
            });
            new Thread(task).start();
        }
    }

    private void handleExportAllOrders() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Export Total Transaction Ledger");
        fc.setInitialFileName("GameStore_Operational_Ledger.xlsx");
        fc.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Excel Workspace (*.xlsx)", "*.xlsx"));

        Stage current = (Stage) exportOrdersBtn.getScene().getWindow();
        File file = fc.showSaveDialog(current);

        if (file != null) {
            javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<>() {
                @Override
                protected Void call() throws Exception {
                    viewModel.exportActiveOrderLog(file);
                    return null;
                }
            };
            task.setOnSucceeded(e -> {
                Alert succ = new Alert(Alert.AlertType.INFORMATION,
                        "Grid data aggregation and writing complete.", ButtonType.OK);
                succ.show();
            });
            task.setOnFailed(e -> {
                Alert err = new Alert(Alert.AlertType.ERROR,
                        "Aggregate write failure: " + task.getException().getMessage(),
                        ButtonType.OK);
                err.show();
            });
            new Thread(task).start();
        }
    }
}
