package com.softserve.edu.documents.chain;

import com.softserve.edu.documents.action.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents standard chains of actions to generate files.
 */
public enum OperationChain {
    /**
     * Chain of actions to generate a docx file.
     */
    DOCX_CHAIN {
        /**
         * Initialize this constant's operations.
         * Replacement of the default constructor.
         */
        @Override
        public void init() {
            operations = new ArrayList<>();

            operations.add(LoadTemplate.INSTANCE);
            operations.add(Normalize.INSTANCE);
            operations.add(InsertText.INSTANCE);
            operations.add(FormatText.INSTANCE);
            operations.add(Cleanse.INSTANCE);
        }
    },
    /**
     * Chain of actions to generate a pdf file.
     */
    PDF_CHAIN {
        /**
         * Initialize this constant's operations.
         * Replacement of the default constructor.
         */
        @Override
        public void init() {
            operations = new ArrayList<>(DOCX_CHAIN.getOperations());
            operations.add(DocxToPdf.INSTANCE);
        }
    },
    INFO_DOCX_CHAIN {
        /**
         * Initialize this constant's operations.
         * Replacement of the default constructor.
         */
        @Override
        public void init() {
            operations = new ArrayList<>();

            operations.add(LoadTemplate.INSTANCE);
            operations.add(NormalizeInfo.INSTANCE);
            operations.add(InsertText.INSTANCE);
            operations.add(FormatText.INSTANCE);
            operations.add(Cleanse.INSTANCE);
        }
    },
    /**
     * Chain of actions to generate a pdf file.
     */
    INFO_PDF_CHAIN {
        /**
         * Initialize this constant's operations.
         * Replacement of the default constructor.
         */
        @Override
        public void init() {
            operations = new ArrayList<>(INFO_DOCX_CHAIN.getOperations());
            operations.add(DocxToPdf.INSTANCE);
        }
    };

    protected List<Operation> operations = new ArrayList<>();

    /**
     * Default constructor.
     */
    OperationChain() {
        init();
    }

    /**
     * Initialize a constant. Replacement of default constructor.
     */
    public abstract void init();

    public List<Operation> getOperations() {
        return operations;
    }

    ;
}
