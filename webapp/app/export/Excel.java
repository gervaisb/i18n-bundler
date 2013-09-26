package export;

import java.io.IOException;
import java.io.OutputStream;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import models.Bundle;
import models.Translation;

public class Excel implements Format {

    private static final int FIRST_ROW  = 0;    
    private static final int KEY_COLUMN = 0;    
    private static final int NL_COLUMN  = 1;    
    private static final int FR_COLUMN  = 2;    
    
    private final OutputStream out;
    
    private WritableWorkbook workbook;
    private WritableSheet sheet;
    private int row = FIRST_ROW;
    
    public Excel(OutputStream out) {
	this.out = out;
    }
    
    @Override
    public void open(Bundle bundle) throws IOException {
	workbook = Workbook.createWorkbook(out);
	sheet = workbook.createSheet(bundle.name, 0);
	row = FIRST_ROW;
	try {
	    sheet.addCell(new Label(KEY_COLUMN, row, "Key"));
	    sheet.addCell(new Label(NL_COLUMN,  row, "Nl"));
	    sheet.addCell(new Label(FR_COLUMN,  row, "Fr"));
	    row += 1;
	} catch (WriteException e) {
	    throw new IOException("Cannot prepare translations sheet", e);
	}        
    }
    
    @Override
    public void append(Translation translation) throws IOException {
 	try {
	    sheet.addCell(new Label(KEY_COLUMN, row, translation.key));
	    sheet.addCell(new Label(NL_COLUMN,  row, translation.hasDutch()?translation.toDutch():""));
	    sheet.addCell(new Label(FR_COLUMN,  row, translation.hasFrench()?translation.toFrench():""));
	    row += 1;
	} catch (WriteException e) {
	    throw new IOException("Cannot add translation ["+translation.key+"] at row "+row+".", e);
	}
    }
    
    @Override
    public void close() throws IOException {
	try {
	    workbook.write();
	    workbook.close();
	    
	    out.flush();
	} catch (WriteException e) {
	    throw new IOException("Cannot write workbook.", e);
	}
    }
    
    @Override
    public String toString() {
	return new StringBuilder(super.toString())
		.append(".xls").toString();
    }
}
