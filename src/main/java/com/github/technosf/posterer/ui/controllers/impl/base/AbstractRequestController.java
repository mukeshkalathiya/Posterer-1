/*
 * Copyright 2014 technosf [https://github.com/technosf]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.technosf.posterer.ui.controllers.impl.base;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.technosf.posterer.models.Request;
import com.github.technosf.posterer.models.ResponseModel;
import com.github.technosf.posterer.models.StatusModel;
import com.github.technosf.posterer.models.impl.ProxyBean;
import com.github.technosf.posterer.models.impl.RequestBean;
import com.github.technosf.posterer.ui.components.FileChooserComboBox;
import com.github.technosf.posterer.ui.controllers.Controller;
import com.github.technosf.posterer.ui.controllers.impl.ResponseController;
import com.github.technosf.posterer.ui.controllers.impl.StatusController;
import com.github.technosf.posterer.utils.PrettyPrinters;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Abstract Controller for the Request window that holds the FXML objects and
 * display relationships
 * <p>
 * Listeners are provided for timeout and proxy changes to modify the underlying
 * request model.
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
// @SuppressWarnings("null")
public abstract class AbstractRequestController
        extends AbstractController
        implements Controller
{
    /*
     * ------------ FXML Components -----------------
     */

    @FXML
    protected ComboBox<URI> endpoint;

    @FXML
    protected ComboBox<ProxyBean> proxyCombo;

    @FXML
    protected ComboBox<String> endpointFilter, useAlias;

    @FXML
    protected FileChooserComboBox certificateFileChooser;

    @FXML
    protected TextField timeoutText, proxyHost, proxyPort, proxyUser,
            proxyPassword, homedir;

    @FXML
    protected Label proxyComboLabel, proxyHostLabel, proxyPortLabel,
            proxyUserLabel, proxyPasswordLabel;

    @FXML
    protected PasswordField password, certificatePassword;

    @FXML
    protected Slider timeoutSlider;

    @FXML
    protected TextArea statusWindow, payload;

    @FXML
    protected ProgressIndicator progress;

    @FXML
    protected Button fire1, fire2, fire3, fire4, fire5, save,
            validateCertificate, saveProxy;

    @FXML
    protected ToggleButton proxyToggle1, proxyToggle2, proxyToggle3,
            proxyToggle4, proxyToggle5;

    @FXML
    protected ChoiceBox<String> method, mime, security;

    @FXML
    protected RadioButton encode;

    @FXML
    protected TabPane tabs;

    @FXML
    protected Tab destination, configuration, store;

    @FXML
    protected StackPane stack;

    @FXML
    protected TableView<Request> propertiesTable;

    @FXML
    protected TableColumn<Request, String> endpointColumn, payloadColumn,
            methodColumn, securityColumn, contentTypeColumn;

    @FXML
    protected TableColumn<Request, Boolean> base64Column;

    /* Context Menus */
    protected RadioButton payloadWrap = new RadioButton("Wrap");
    protected CustomMenuItem payloadWrapMI = new CustomMenuItem(payloadWrap);
    protected MenuItem payloadFormat = new MenuItem("Format");
    protected ContextMenu payloadCM =
            new ContextMenu(payloadWrapMI, payloadFormat);

    /* ---- State vars ----- */

    /**
     * The request data to reflect in this window
     */
    protected final RequestBean requestBean = new RequestBean();

    protected final boolean preferencesAvailable = true;

    protected ProxyBean proxyBean = new ProxyBean();
    protected StatusController statusController;
    protected StatusModel status;

    /* ---- Display Constants ----- */

    /**
     * The FXML definition of the View
     */
    public static final String FXML = "/fxml/Request.fxml";

    private static final Logger LOG =
            LoggerFactory.getLogger(AbstractRequestController.class);

    private static final String INFO_PROPERTIES =
            "Error :: Cannot store endpoints and requests: %1$s";

    private static final String INFO_FIRED =
            "Fired request #%1$d:   Method [%2$s]   Endpoint [%3$s] ";

    private static final String LEGEND_PROXY_ON = "Proxy On";
    private static final String LEGEND_PROXY_OFF = "Proxy Off";
    private static final Paint CONST_PAINT_BLACK = Paint.valueOf("#292929");
    private static final Paint CONST_PAINT_GREY = Paint.valueOf("#808080");

    /*
     * ------------ FXML Bindings -----------------
     */

    /*
     * The time out
     */
    private IntegerProperty timeoutProperty = new SimpleIntegerProperty();

    /**
     * Use a proxy?
     */
    protected BooleanProperty useProxyProperty =
            new SimpleBooleanProperty(false);

    /*
     * Toggle proxy button text
     */
    private StringProperty useProxyTextProperty = new ReadOnlyStringWrapper(
            useProxyProperty.get() ? LEGEND_PROXY_ON : LEGEND_PROXY_OFF);

    protected ObservableList<Request> propertiesList =
            FXCollections.observableArrayList();

    private FilteredList<Request> filteredPropertiesList =
            new FilteredList<>(propertiesList, p -> true);
    private SortedList<Request> sortedPropertiesList =
            new SortedList<>(filteredPropertiesList);


    /*
     * ---------- Code ------------------
     */

    /**
     * Instantiate with the given title
     * 
     * @param title
     */
    protected AbstractRequestController(String title)
    {
        super(title);
    }

    /*
     * ------------ Initiators -----------------
     */


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.ui.controllers.Controller#initialize()
     */
    @Override
    public void initialize()
    {

        LOG.debug("Initialization starts");

        statusController =
                StatusController.loadController(statusWindow.textProperty());
        statusController.setStyle(getStyle());
        status = statusController.getStatusModel();

        /*
         * Bulk initializations
         */

        initializeListeners();

        initializeCertificateFileChooser();

        initializeProperties();

        initializeBindings();

        /*
         * Preferences
         */
        try
        {
            // homedir.textProperty().set(propertiesModel.getPropertiesDir());
            homedir.textProperty().set(propertiesDirectory());
        }
        catch (IOException e)
        {
            store.setDisable(true);
            status.write(INFO_PROPERTIES, e.getMessage());
        }

        payloadFormat.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent e)
            {
                payload.setText(PrettyPrinters.xml(payload.getText(), true));
            }
        });

        LOG.debug("Initialization complete");
    }


    /**
     * Initialize listeners
     */
    private void initializeListeners()
    {
        // TODO Refactor status window
        /*
         * Status windows
         */
        statusWindow.textProperty().addListener(new ChangeListener<Object>()
        {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue,
                    Object newValue)
            {
                statusWindow.setScrollTop(Double.MAX_VALUE); // this will scroll
                                                             // to the bottom
                                                             // use Double.MIN_VALUE to scroll to the top
            }
        });

        endpoint.getEditor().focusedProperty()
                .addListener(new ChangeListener<Object>()
                {
                    @Override
                    public void changed(ObservableValue<?> arg0, Object arg1,
                            Object arg2)
                    {
                        endpointValidate(endpoint.getEditor().getText());
                    }
                });

        proxyHost.focusedProperty().addListener(new ChangeListener<Object>()
        {
            @Override
            public void changed(ObservableValue<?> arg0, Object arg1,
                    Object arg2)
            {
                proxyUpdate();
            }
        });

        proxyPort.focusedProperty().addListener(new ChangeListener<Object>()
        {
            @Override
            public void changed(ObservableValue<?> arg0, Object arg1,
                    Object arg2)
            {
                proxyUpdate();
            }
        });

        proxyUser.focusedProperty().addListener(new ChangeListener<Object>()
        {
            @Override
            public void changed(ObservableValue<?> arg0, Object arg1,
                    Object arg2)
            {
                proxyUpdate();
            }
        });

        proxyPassword.focusedProperty().addListener(new ChangeListener<Object>()
        {
            @Override
            public void changed(ObservableValue<?> arg0, Object arg1,
                    Object arg2)
            {
                proxyUpdate();
            }
        });

        /*
         * Listener to manage the proxy
         */
        proxyCombo.valueProperty()
                .addListener((observable, oldValue, newValue) -> {
                    proxyHost.setText(newValue.getProxyHost());
                    proxyPort.setText(newValue.getProxyPort());
                    proxyUser.setText(newValue.getProxyUser());
                    proxyPassword.setText(newValue.getProxyPassword());
                });

        /*
         * Listener to manage the endpoint filter
         */
        endpointFilter.valueProperty()
                .addListener((observable, oldValue, newValue) -> {
                    filteredPropertiesList.setPredicate(

                            request -> {
                        /*
                         * If filter text is empty, display all requests.
                         */
                        if (newValue == null || newValue.isEmpty())
                        {
                            return true;
                        }

                        /*
                         * Compare first name and last name of every person with filter
                         * text
                         */
                        if (request.getEndpoint().toLowerCase().trim()
                                .contains(newValue.toLowerCase().trim()))
                        {
                            return true; // Filter matches first name.
                        }

                        return false; // Does not match.
                    });

                });

    }


    /**
     * Initialize the bindings
     */
    private void initializeBindings()
    {
        LOG.debug("Initializing Bindings");

        timeoutText.textProperty().bind(timeoutProperty.asString("%d"));
        timeoutProperty.bind(timeoutSlider.valueProperty());

        /*
         * Bidirectionally Bind the proxy buttons to a single property
         * so that when one button is clicked they all are
         */
        useProxyProperty.bindBidirectional(proxyToggle1.selectedProperty());
        useProxyProperty.bindBidirectional(proxyToggle2.selectedProperty());
        useProxyProperty.bindBidirectional(proxyToggle3.selectedProperty());
        useProxyProperty.bindBidirectional(proxyToggle4.selectedProperty());
        useProxyProperty.bindBidirectional(proxyToggle5.selectedProperty());

        /*
         * Bind proxy button text to single property
         */
        proxyToggle1.textProperty().bind(useProxyTextProperty);
        proxyToggle2.textProperty().bind(useProxyTextProperty);
        proxyToggle3.textProperty().bind(useProxyTextProperty);
        proxyToggle4.textProperty().bind(useProxyTextProperty);
        proxyToggle5.textProperty().bind(useProxyTextProperty);

        // Link proxy fields together from host
        proxyHostLabel.textFillProperty()
                .bind(proxyComboLabel.textFillProperty());
        proxyPortLabel.textFillProperty()
                .bind(proxyComboLabel.textFillProperty());
        proxyUserLabel.textFillProperty()
                .bind(proxyComboLabel.textFillProperty());
        proxyPasswordLabel.textFillProperty()
                .bind(proxyComboLabel.textFillProperty());

        proxyCombo.disableProperty().bind(useProxyProperty.not());
        proxyHost.disableProperty().bind(useProxyProperty.not());
        proxyPort.disableProperty().bind(useProxyProperty.not());
        proxyUser.disableProperty().bind(useProxyProperty.not());
        proxyPassword.disableProperty().bind(useProxyProperty.not());
        saveProxy.disableProperty().bind(useProxyProperty.not());

        // proxyHost.textProperty().set(requestModel.getProxyHost());
        // proxyPort.textProperty().set(requestModel.getProxyPort());
        // proxyUser.textProperty().set(requestModel.getProxyUser());
        // proxyPassword.textProperty().set(requestModel.getProxyPassword());

        payload.wrapTextProperty().bind(payloadWrap.selectedProperty().not());
    }


    /**
     * Initialize the certificateFileChooser
     */
    private void initializeCertificateFileChooser()
    {
        LOG.debug("Initializing Certificate File Chooser");

        certificateFileChooser.setRoot(getRoot());
        certificateFileChooser.getChosenFileProperty()
                .addListener(new ChangeListener<File>()
                {
                    @Override
                    public void changed(ObservableValue<? extends File> arg0,
                            File oldValue, File newValue)
                    {
                        setCertificateFile(newValue);
                    }
                });
    }


    /**
     * Initialize the properties sub system
     */
    private void initializeProperties()
    {
        LOG.debug("Initializing Properties");

        propertiesTable.setRowFactory(
                new Callback<TableView<Request>, TableRow<Request>>()
                {
                    @Override
                    public TableRow<Request> call(TableView<Request> tableView)
                    {
                        final TableRow<Request> row = new TableRow<>();
                        final ContextMenu rowMenu = new ContextMenu();

                        ContextMenu tableMenu = tableView.getContextMenu();
                        if (tableMenu != null)
                        {
                            rowMenu.getItems().addAll(tableMenu.getItems());
                            rowMenu.getItems().add(new SeparatorMenuItem());
                        }
                        MenuItem removeItem = new MenuItem("Delete");
                        removeItem.setOnAction(new EventHandler<ActionEvent>()
                        {
                            @Override
                            public void handle(ActionEvent e)
                            {
                                removeFromProperties(row.getItem());
                                propertiesList.remove(row.getItem());
                            }
                        });
                        rowMenu.getItems().addAll(removeItem);
                        row.contextMenuProperty()
                                .bind(Bindings
                                        .when(Bindings
                                                .isNotNull(row.itemProperty()))
                                        .then(rowMenu)
                                        .otherwise((ContextMenu) null));
                        return row;
                    }
                });

        propertiesTable.addEventFilter(MouseEvent.MOUSE_CLICKED,

                event -> {
                    if (event.getClickCount() > 1
                            && event.getButton().equals(MouseButton.PRIMARY))
                    {
                        requestLoad(propertiesTable.getSelectionModel()
                                .getSelectedItem());
                    }
                });

        sortedPropertiesList.comparatorProperty()
                .bind(propertiesTable.comparatorProperty());
        propertiesTable.setItems(sortedPropertiesList);

        endpointColumn.setCellValueFactory(
                new PropertyValueFactory<Request, String>("endpoint"));
        payloadColumn.setCellValueFactory(
                new PropertyValueFactory<Request, String>("payload"));
        methodColumn.setCellValueFactory(
                new PropertyValueFactory<Request, String>("method"));
        securityColumn.setCellValueFactory(
                new PropertyValueFactory<Request, String>("security"));
        contentTypeColumn.setCellValueFactory(
                new PropertyValueFactory<Request, String>("contentType"));
        base64Column.setCellValueFactory(
                new PropertyValueFactory<Request, Boolean>("base64"));

        processProperties();

    }


    /**
     * {@inheritDoc}
     * <p>
     * Could've have put this functionality into {@code close()} too.
     *
     * @see com.github.technosf.posterer.ui.controllers.Controller#onStageClose()
     */
    @Override
    public void onStageClose(Stage stage)
    {
        LOG.debug("Closing StatusController");
        statusController.onStageClose(stage);
    }


    /*
     * ---------- Events -------------------
     */

    /**
     * Fire event - User hits the {@code Fire} button
     * <p>
     * Create a response task and fires it off in the back ground.
     * 
     * @throws IOException
     */
    public final void fire()
    {
        LOG.debug("Fire  --  Starts");

        // if (!endpointValid(endpoint.getValue()))
        // return;

        progress.setVisible(true); // Show we're busy

        try
        {
            requestUpdate();
            proxyUpdate();

            // URI uri = new URI(StringUtils.trim(endpoint.getValue()));

            if (!endpoint.getItems().contains(endpoint.getValue()))
            /*
             * Add endpoint if not already added
             * 
             */
            {
                endpoint.getItems().add(endpoint.getValue());
            }

            /* Fire off the request */
            ResponseModel response = fireRequest(requestBean.copy());

            /* Feedback to Request status panel */
            status.append(INFO_FIRED, response.getReferenceId(),
                    response.getRequest().getMethod(),
                    response.getRequest().getUri());

            /*
             * Open the Response window managing this request instance
             */
            ResponseController.loadStage(response).show();

        }
        finally
        /*
         * Clear the progress ticker
         */
        {
            progress.setVisible(false); // No longer busy
        }

        LOG.debug("Fire  --  ends");
    }


    /**
     * Enable the proxy fields
     * 
     * @param enable
     *            true to enable
     */
    protected final void proxyEnableFields(boolean enable)
    {
        if (enable)
        {
            useProxyTextProperty.setValue(LEGEND_PROXY_ON);
            proxyComboLabel.setTextFill(CONST_PAINT_BLACK);
            saveProxy.setTextFill(CONST_PAINT_BLACK);
        }
        else
        {
            useProxyTextProperty.setValue(LEGEND_PROXY_OFF);
            proxyComboLabel.setTextFill(CONST_PAINT_GREY);
            saveProxy.setTextFill(CONST_PAINT_GREY);
        }
    }


    /**
     * User asks to save the current request configuration via (@code save}
     * button.
     */
    protected abstract void proxySave();


    /**
     * User asks to save the current request configuration via (@code save}
     * button.
     */
    protected abstract void requestSave();


    /* ------------------------------------------ */

    /**
     * Proxy Toggle event - Use toggles the {@code Proxy} button.
     * <p>
     * The {@code useProxy} property has already received any change prior to
     * toggleProxy being fired from the FXML. Could have used a {@code Listener}
     * in the {@code initialize} method where the bindings are done, but using
     * the {@code onAction} methods is easier.
     */
    protected abstract void proxyToggle();


    /**
     * Update the request bean from the values bound to the window
     */
    protected abstract void requestUpdate();


    /**
     * Update the request bean from the values bound to the window
     */
    protected abstract void proxyUpdate();


    /**
     * Validate the security Certificate selected
     * <p>
     * Loads the certificate and give it to the cert viewer
     */
    protected abstract void certificateValidate();


    /* ---------------  Functions ----------------- */

    /**
     * Fires a request off and returns the Response
     * 
     * @param request
     *            the request
     * @return the response
     */
    protected abstract ResponseModel fireRequest(Request request);


    /**
     * returns true if a proxy should be used
     * 
     * @return true if proxy should be used
     */
    protected abstract boolean getUseProxy();


    /**
     * Assures the existence of the certificate file selection and configures
     * the UI.
     * 
     * @param file
     *            the new certificate file
     */
    protected abstract void setCertificateFile(File file);


    /**
     * Removes the given request from the properties
     * 
     * @param request
     *            the request to remove
     */
    protected abstract void removeFromProperties(Request request);


    /**
     * Transfers stored properties to the UI properties tab
     */
    protected abstract void processProperties();


    /**
     * Loads a {@code Request} into the {@code RequestController} bound vars.
     * 
     * @param requestdata
     *            the {@code Request} to pull into the ui
     */
    protected abstract void requestLoad(Request requestdata);


    /**
     * Validate and place the endpoint where needed, set tls etc
     * 
     * @param endpoint
     */
    protected abstract void endpointValidate(String endpoint);


    /**
     * Returns the directory where the properties file reside
     * 
     * @return the directory
     * @throws IOException
     */
    protected abstract String propertiesDirectory() throws IOException;
}