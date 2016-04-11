/*
 * #%L
 * Medication Administration Record
 * %%
 * Copyright (C) 2014 - 2016 Healthcare Services Platform Consortium
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.hspconsortium.cwf.ui.mar.render;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Column;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Vbox;

import org.hspconsortium.cwf.fhir.common.FhirUtil;
import org.hspconsortium.cwf.ui.mar.MedicationActionUtil;
import org.hspconsortium.cwf.ui.mar.controller.MainController;
import org.hspconsortium.cwf.ui.mar.model.MarModel;
import org.socraticgrid.fhir.generated.IQICoreMedicationAdministration;
import org.socraticgrid.fhir.generated.IQICoreMedicationOrder;

import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder.DosageInstruction;

/**
 * Medication Administration Record Grid Renderer. Renderer identifies grid cells with the
 * placeholder text, generally an "x" and replace the placeholder with an icon specified by the
 * image path 'imagePath'.
 * 
 * @author cnanjo
 */
public class MarRenderer implements RowRenderer<List<Object>> {
    
    
    /**
     * Formats dates to the form 12:05 PM
     */
    public static SimpleDateFormat hourMinuteFormatter = new SimpleDateFormat("hh:mm a");
    
    /**
     * The placeholder to replace with the appropriate image
     */
    private String imagePlaceholder;
    
    /**
     * The image to fill in the cell in place of the placeholder marker
     */
    private String imagePath;
    
    private MainController marController;
    
    public class SignMedAdminListener implements EventListener<MouseEvent> {
        
        
        private IQICoreMedicationOrder prescription;
        
        private final MainController marController;
        
        public SignMedAdminListener(MainController marController, IQICoreMedicationOrder prescription) {
            this.prescription = prescription;
            this.marController = marController;
        }
        
        @Override
        public void onEvent(MouseEvent event) {
            MedicationActionUtil.show(false, prescription);
            marController.initializeMar();
        }
        
        public IQICoreMedicationOrder getPrescription() {
            return prescription;
        }
        
        public void setPrescription(IQICoreMedicationOrder prescription) {
            this.prescription = prescription;
        }
    }
    
    public MarRenderer() {
    }
    
    /**
     * Constructor
     * 
     * @param imagePlaceholder The placeholder for the image
     * @param imagePath The path of the image
     */
    public MarRenderer(MainController marController, String imagePlaceholder, String imagePath) {
        this();
        this.imagePath = imagePath;
        this.imagePlaceholder = imagePlaceholder;
        this.marController = marController;
    }
    
    /**
     * Renders a MAR in the grid
     */
    @Override
    public void render(Row row, List<Object> data, int index) {
        for (Object s : data) {
            if (s instanceof IQICoreMedicationOrder) {
                //    			Button sign = new Button("Administer");
                //    			sign.addEventListener("onClick", new SignMedAdminListener((IQICoreMedicationOrder)s));
                //    			row.appendChild(sign);
                Cell cell = new Cell();
                cell.addEventListener("onClick", new SignMedAdminListener(marController, (IQICoreMedicationOrder) s));
                row.appendChild(cell);
            } else if (s != null && !s.equals(imagePlaceholder) && s instanceof String) {
                String entry = (String) s;
                String[] entries = entry.split("\\|");
                Cell cell = new Cell();
                Vbox vbox = new Vbox();
                cell.appendChild(vbox);
                for (String item : entries) {
                    Label label = new Label(item);
                    //    			label.setPre(true);
                    //    			label.setMultiline(true);
                    vbox.appendChild(label);
                }
                row.appendChild(cell);
            } else {
                Image image = new Image();
                image.setSrc(imagePath);
                
                row.appendChild(image);
            }
        }
    }
    
    /**
     * Returns the path to the image
     * 
     * @return
     */
    public String getImagePath() {
        return imagePath;
    }
    
    /**
     * Sets the path to the image icon
     * 
     * @param imagePath
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    /**
     * Returns the placeholder for the image
     * 
     * @return
     */
    public String getImagePlaceholder() {
        return this.imagePlaceholder;
    }
    
    /**
     * Sets the image placeholder text
     * 
     * @param imagePlaceholder
     */
    public void setImagePlaceholder(String imagePlaceholder) {
        this.imagePlaceholder = imagePlaceholder;
    }
    
    public static String getUnitLabel(String unit) {
        if (unit == null || !unit.equals("{tbl}")) {
            return unit;
        } else {
            return "tablet";
        }
    }
    
    /**
     * Convenience method to initialize a ZK grid based on the information contained in this model.
     * 
     * @param grid
     * @param marModel
     */
    public static void populateGrid(Grid grid, MarModel marModel, MarRenderer marRenderer) {
        if (marRenderer != null) {
            grid.setRowRenderer(marRenderer);
        }
        grid.getColumns().getChildren().clear();
        marModel.getHeaders();
        for (String header : marModel.getHeaders()) {
            grid.getColumns().appendChild(new Column(header));
        }
        grid.getColumns().appendChild(new Column("Current Time"));
        grid.setModel(marModel.getRows());
        
        //Set width of medication column
        ((Column) grid.getColumns().getChildren().get(0)).setWidth("200px");
    }
    
    /**
     * Method takes a list of valid medication orders and convert that list into a list of order
     * sentences for the given orders.
     * 
     * @param orders
     * @return
     */
    public static List<String> getOrderSentences(List<IQICoreMedicationOrder> orders) {
        List<String> sentences = new ArrayList<String>();
        for (IQICoreMedicationOrder order : orders) {
            sentences.add(generateMedicationOrderSentence(order));
        }
        return sentences;
    }
    
    /**
     * Method takes a medication order and converts it into an order sentence of the form:
     * dispensable route frequency TODO Adjust template as necessary
     * 
     * @param order
     * @return
     */
    public static String generateMedicationOrderSentence(IQICoreMedicationOrder order) {
        StringBuilder sentence = new StringBuilder();
        String dispense = order.getMedicationCodeableConcept().getCodingFirstRep().getDisplay();
        sentence.append(dispense).append("\n");
        if (order.getDosageInstruction() != null && order.getDosageInstruction().size() > 0) {
            DosageInstruction dosage = order.getDosageInstruction().get(0);
            if (dosage.getDose() != null && dosage.getDose() instanceof QuantityDt) {
                QuantityDt doseAmnt = (QuantityDt) dosage.getDose();
                sentence.append(doseAmnt.getValue()).append(" ").append(getUnitLabel(doseAmnt.getUnit())).append(" ");
            }
            if (dosage.getRoute().getCodingFirstRep() != null) {
                sentence.append(dosage.getRoute().getCodingFirstRep().getDisplay()).append(" ");
            }
            if (dosage.getTiming() != null) {
                sentence.append(FhirUtil.getFrequencyFromRepeat(order.getDosageInstruction().get(0).getTiming().getRepeat())
                        .getDisplay());
            }
        }
        return sentence.toString();
    }
    
    public static void recordAdministrationNotes(List<Object> row, int index, IQICoreMedicationAdministration medAdmin,
                                                 IQICoreMedicationOrder order, String username) {
        String item = (String) row.get(index);
        if (item == null || item.trim().length() == 0) {
            item = "";
        } else {
            item += "|";
        }
        QuantityDt adminQty = medAdmin.getDosage().getQuantity();
        QuantityDt orderQty = (QuantityDt) order.getDosageInstruction().get(0).getDose();
        if (!FhirUtil.equalQuantities(adminQty, orderQty)) {
            item += "Dose adj. " + adminQty.getValue() + " " + getUnitLabel(adminQty.getUnit()) + " at ";
        }
        item += hourMinuteFormatter.format(medAdmin.getEffectiveTimeDateTime());
        item += " by " + username;
        row.set(index, item);
    }
}
